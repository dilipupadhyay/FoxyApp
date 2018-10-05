/*
 * FoxyQuotaEntryPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import javax.faces.application.FacesMessage;
import com.foxy.db.QuotaEntry;
import com.foxy.db.QuotaMast;
import com.foxy.db.HibernateUtil;
import com.foxy.util.FoxyPagedDataModel;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.Query;


/**
 *
 * @author hcting
 */
public class FoxyQuotaEntryPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private DataModel qtaEntryListModel;
    private QuotaEntry qtaEntryBean = null;
    private QuotaEntry sessQuotaEntryBean = null;
    private List qTypeList = null;
    private List quotaForCountry = null;
    
    
    /** Creates a new instance of FoxyQuotaEntryPage */
    public FoxyQuotaEntryPage() {
        super(new String("QuotaEntryForm"));
        
        try {
            this.isAuthorize(MENU_CODE);
            //System.out.println(ctx.getApplication().getViewHandler().toString());
            
            if ( ! this.foxySessionData.getAction().equals(ADD)) {
                //Get search parameter from session bean object
                sessQuotaEntryBean = (QuotaEntry)this.getSessionObject1(QuotaEntry.class);
                //Create new object and replace session bean's object1 with this new one
                if ( sessQuotaEntryBean == null){
                    sessQuotaEntryBean = new QuotaEntry();
                    sessQuotaEntryBean.setExpDate(null);
                    this.setSessionObject1((Object)sessQuotaEntryBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        qTypeList = new ArrayList();
        qTypeList.add(new SelectItem(new String(""), new String("Pls Select One")));
        qTypeList.add(new SelectItem(new String("BI"), new String("BI-Bid/Buy In")));
        qTypeList.add(new SelectItem(new String("TI"), new String("TI-Transfer In")));
        qTypeList.add(new SelectItem(new String("TO"), new String("TO-Transfer Out")));
    }
    
    
    public List getQtaTypeList(){
        return this.qTypeList;
    }
    
    public QuotaEntry getSessQuotaEntryBean() {
        return sessQuotaEntryBean;
    }
    
    public void setSessQuotaEntryBean(QuotaEntry sessQuotaEntryBean) {
        this.sessQuotaEntryBean = sessQuotaEntryBean;
    }
    
    
    //Ajax used to get latest Quota list based on country submitted
    public List getQtaByCountryList(){
        System.out.println("Get quota list for country " + this.qtaEntryBean.getCountry());
        if (quotaForCountry == null )
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria criteria = session.createCriteria(QuotaMast.class);
                criteria.add(Expression.eq("country", this.qtaEntryBean.getCountry()));
                criteria.addOrder(Order.asc("quota"));
                List resultlist = criteria.list();
                tx.commit();
                HibernateUtil.closeSession();
                
                //Use LinkedHashMap to supress duplicate entry, bcoz list can have duplicate if join table used!!!! and hibernate do return duplicate
                Map resultMap = new LinkedHashMap();
                QuotaMast qtam = null;
                for ( int i = 0; i < resultlist.size(); i++ ){
                    qtam = (QuotaMast)resultlist.get(i);
                    resultMap.put(qtam.getQtaId(), qtam);
                }
                //System.err.println("result size = " +  resultMap.size());
                
                quotaForCountry = new ArrayList();
                quotaForCountry.add(new SelectItem(new String(""),
                        new String((resultMap.size() == 0)?"No Quota Applicable":"Please Select One")));
                
                QuotaMast qm = null;
                Set st = resultMap.entrySet();
                Iterator it = st.iterator();
                while( it.hasNext()){
                    Map.Entry me = (Map.Entry)it.next();
                    qm = (QuotaMast)me.getValue();
                    quotaForCountry.add(new SelectItem(qm.getQuota(), qm.getListDisplayString()));
                    //System.err.println("=====>>>" + qm.getQtaId() + " - " + qm.getListDisplayString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
                return null;
            } finally {
                HibernateUtil.closeSession();
            }
            return (quotaForCountry);
    }
    
    
    
    public String saveAdd() {
        System.out.println("Save Add");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            
            //Transfer out meaning sale extract quota to other factory
            if ("TO".equals(this.qtaEntryBean.getType())){//Transfer out should be -ve value!
                if ( this.qtaEntryBean.getQuotaQty() > 0)
                    this.qtaEntryBean.setQuotaQty( this.qtaEntryBean.getQuotaQty() * (-1) );
                if (qtaEntryBean.getCost() > 0)
                    this.qtaEntryBean.setCost(qtaEntryBean.getCost() * -1);//should be minus
            }else{
                if ( this.qtaEntryBean.getQuotaQty() < 0)
                    this.qtaEntryBean.setQuotaQty(this.qtaEntryBean.getQuotaQty() * (-1));
                if (qtaEntryBean.getCost() < 0)
                    this.qtaEntryBean.setCost(qtaEntryBean.getCost() * -1);//should be minus
            }
            
            qtaEntryBean.setStatus("A");
            session.save(qtaEntryBean);
            tx.commit();
            HibernateUtil.closeSession();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            return null;
        } finally {
            HibernateUtil.closeSession();
        }
        return ("success");
    }
    
    
    public String editQtaEntry(){
        System.out.println("Call edit Quota entry");
        return ("editquotaentry");
    }
    
    
    //PROPERTY: dbEditCatBean
    public void setDbEditQtaEntryBean(QuotaEntry qtaEntryBean) {
        this.qtaEntryBean  = qtaEntryBean;
    }
    
    
    public QuotaEntry getDbEditQtaEntryBean() {
        System.err.println("calling get bean for update");
        //Should only retrieve if object not yet retrieved !! else will be to heavy and unneccessary query !
        if ( this.qtaEntryBean == null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(QuotaEntry.class);
                //System.err.println("qtaEntryId = " + foxySessionData.getPageParameterLong());
                crit.add(Expression.eq("qtaEntryId", foxySessionData.getPageParameterLong()));
                List result = crit.list();
                //System.err.println("Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.qtaEntryBean = (QuotaEntry)result.get(0);
                } else {
                    System.err.println("No Quota Entry bean with qtaEntryId = " + foxySessionData.getPageParameterLong());
                }
                tx.commit();
            } catch (HibernateException e) {
                //do something here with the exception
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            }catch (Exception e){
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            } finally {
                HibernateUtil.closeSession();
            }
        }
        return this.qtaEntryBean;
    }
    
    
    public String saveEdit() {
        System.out.println("Save Edit");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            
            //Transfer out meaning sale extract quota to other factory
            if ("TO".equals(this.qtaEntryBean.getType())){//Transfer out should be -ve value!
                if ( this.qtaEntryBean.getQuotaQty() > 0)
                    this.qtaEntryBean.setQuotaQty( this.qtaEntryBean.getQuotaQty() * (-1) );
                if (qtaEntryBean.getCost() > 0)
                    this.qtaEntryBean.setCost(qtaEntryBean.getCost() * -1);//should be minus
            }else{
                if ( this.qtaEntryBean.getQuotaQty() < 0)
                    this.qtaEntryBean.setQuotaQty(this.qtaEntryBean.getQuotaQty() * (-1));
                if (qtaEntryBean.getCost() < 0)
                    this.qtaEntryBean.setCost(qtaEntryBean.getCost() * -1);//should be minus
            }
            
            
            session.update(qtaEntryBean);
            tx.commit();
            HibernateUtil.closeSession();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            return null;
        }finally {
            HibernateUtil.closeSession();
        }
        return ("success");
    }
    
    
    
    public QuotaEntry getQtaEntryBean(){
        if ( qtaEntryBean == null ){
            qtaEntryBean = new  QuotaEntry();
        }
        return qtaEntryBean;
    }
    
    public void setQtaEntryBean(QuotaEntry qtaEntryBean){
        this.qtaEntryBean = qtaEntryBean;
    }
    
    
    public String search() {
        this.foxySessionData.setAction(LST);
        foxyTable.setFirst(0);
        return (null);
    }
    
    public DataModel getQtaEntryListModel() {
        try {
            int firstrow = this.foxyTable.getFirst();
            int pagesize = this.foxyTable.getRows();
            
            Number numofRec = null;
            
            String qtapattern = this.sessQuotaEntryBean.getQuota();
            if (qtapattern != null) {
                String likestr = "%" + qtapattern.replace('*', '%') + "%";
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria criteria = session.createCriteria(QuotaEntry.class);
                
                //Get Total row count
                criteria.setProjection(Projections.rowCount());
                criteria.add(Expression.like("quota", likestr));
                criteria.add(Expression.eq("country", this.getSessQuotaEntryBean().getCountry()));
                criteria.add(Expression.ge("effDate", this.getSessQuotaEntryBean().getEffDate()));
                criteria.add(Expression.le("effDate", this.getSessQuotaEntryBean().getExpDate()));
                numofRec = ((Number) criteria.uniqueResult());
                tx.commit();
                
                //System.err.println("Total Number of records: [" + numofRec.intValue() + "]");
                numofRec = numofRec == null ? 0 : numofRec.intValue();
                
                
                //Retrieve subset of record base on foxyTable current view page
                tx = session.beginTransaction();
                criteria = session.createCriteria(QuotaEntry.class);
                criteria.add(Expression.like("quota", likestr));
                criteria.add(Expression.eq("country", this.getSessQuotaEntryBean().getCountry()));
                criteria.add(Expression.ge("effDate", this.getSessQuotaEntryBean().getEffDate()));
                criteria.add(Expression.le("effDate", this.getSessQuotaEntryBean().getExpDate()));
                criteria.addOrder(Order.asc("effDate"));
                criteria.setFirstResult(firstrow);
                criteria.setMaxResults(pagesize);
                List result = criteria.list();
                tx.commit();
                if (qtaEntryListModel != null) {
                    qtaEntryListModel = null;
                }
                //System.err.println("Query Results size = [" + result.size() + "] recs [from " +
                //firstrow + ", pgsize " + pagesize + "]");
                qtaEntryListModel = (DataModel)new FoxyPagedDataModel(result, numofRec.intValue(), pagesize);
                session.clear();
            } else {
                System.err.println("Search key is null !!!");
            }
        } catch (HibernateException e) {
            //do something here with the exception
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
        }catch (Exception e){
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
        } finally {
            HibernateUtil.closeSession();
        }
        
        return qtaEntryListModel;
    }
    
    
    public String delete(){
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            String  qstr  = new String("DELETE QuotaEntry t ");
            qstr = qstr.concat("WHERE t.qtaEntryId = :pqtaEntryId ");
            Query q = session.createQuery(qstr);
            q.setLong("pqtaEntryId", foxySessionData.getPageParameterLong());
            q.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            return (null);
        } finally {
            HibernateUtil.closeSession();
        }
        return null;
    }
}
