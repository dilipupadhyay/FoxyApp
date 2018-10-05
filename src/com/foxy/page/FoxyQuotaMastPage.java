/*
 * FoxyQuotaMastPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import com.foxy.db.QuotaMastJoinCats;
import javax.faces.application.FacesMessage;
import com.foxy.db.QuotaMast;
import com.foxy.db.QuotaCats;
import com.foxy.db.HibernateUtil;
import com.foxy.util.FoxyPagedDataModel;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.TreeMap;
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
public class FoxyQuotaMastPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private DataModel qtaListModel;
    private QuotaMast qtaBean = null;
    private QuotaMast sessQuotaMastBean = null;
    private QuotaCats qtaCatBean = null;
    private TreeMap<String,QuotaCats> quotaCatsMap = null;
    private List quotaCatsList = null;
    private String addQuotaCatSta = null;
    private String[] selectedCats = null;
    
    
    /**
     * Creates a new instance of FoxyQuotaMastPage
     */
    public FoxyQuotaMastPage() {
        super(new String("QuotaMainForm"));
        
        try {
            this.isAuthorize(MENU_CODE);
            System.out.println(ctx.getApplication().getViewHandler().toString());
            if (this.foxySessionData.getAction().equals(ADD)) {
                //System.err.println("Recreating ProdSchBean in session bean");
                this.qtaBean = null;
                this.qtaBean = new QuotaMast();
                quotaCatsMap = null;
            }else{
                //Get search parameter from session bean object
                sessQuotaMastBean = (QuotaMast)this.getSessionObject1(QuotaMast.class);
                //Create new object and replace session bean's object1 with this new one
                if ( sessQuotaMastBean == null){
                    sessQuotaMastBean = new QuotaMast();
                    this.setSessionObject1((Object)sessQuotaMastBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public QuotaMast getSessQuotaMastBean() {
        return sessQuotaMastBean;
    }
    
    public void setSessQuotaMastBean(QuotaMast sessQuotaMastBean) {
        this.sessQuotaMastBean = sessQuotaMastBean;
    }
    
    public String saveAdd() {
        System.out.println("Save Add");
        
        if ( quotaCatsMap == null || quotaCatsMap.size() == 0) {
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Category list can not empty", "Category required");
            ctx.addMessage(null, fmsg);
            return null;
        }
        
        
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            qtaBean.setStatus("A");
            session.save(qtaBean);
            QuotaCats qtaCatObj = null;
            Set st = quotaCatsMap.entrySet();
            Iterator it = st.iterator();
            while( it.hasNext()){
                Map.Entry me = (Map.Entry)it.next();
                qtaCatObj = (QuotaCats)me.getValue();
                qtaCatObj.setQtaId(this.qtaBean.getQtaId());
                qtaCatObj.setCountry(this.qtaBean.getCountry());
                qtaCatObj.setQuota(this.qtaBean.getQuota());
                session.save(qtaCatObj);
            }
            tx.commit();
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
    
    
    public String saveEdit(){
        //System.out.println("Save Edit");
        if ( quotaCatsMap == null || quotaCatsMap.size() == 0) {
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Category list can not empty", "Category required");
            ctx.addMessage(null, fmsg);
            return null;
        }
        
        try {
            //System.out.println("Save Edit upduserid " + prodSchBean.getUpdUsrId());
            Session session1 = (Session) HibernateUtil.currentSession();
            Transaction tx= session1.beginTransaction();
            //Handling QuotaCats table
            Criteria crit = session1.createCriteria(QuotaCats.class);
            //Retrieve all QuotaCats info
            crit.add(Expression.eq("qtaId", this.qtaBean.getQtaId()));
            crit.addOrder(Order.asc("catId"));
            List dbQtaCatsList = crit.list();
            tx.commit();
            HibernateUtil.closeSession();
            
            
            //open a new session
            Session session = (Session) HibernateUtil.currentSession();
            tx = session.beginTransaction();
            System.err.println("Quota country ====> " + this.qtaBean.getCountry());
            session.update(this.qtaBean);
            
            QuotaCats dbcat = null;
            QuotaCats editcat = null;
            boolean isdeleted = false;
            Set st = quotaCatsMap.entrySet();
            Iterator it = st.iterator();
            while( it.hasNext()){
                isdeleted = true;
                Map.Entry me = (Map.Entry)it.next();
                editcat = (QuotaCats)me.getValue();
                if ( editcat.getQtaCatId() != null) { //existing record, possible deleted
                    for ( int i = 0; i < dbQtaCatsList.size(); i++){
                        dbcat = (QuotaCats)dbQtaCatsList.get(i);
                        //System.err.println("check edit lot id = " + editcat.getCategory() + " dbcat = " + dbcat.getQtaCatId());
                        if (editcat.getQtaCatId().equals(dbcat.getQtaCatId())){
                            System.err.println("Ignore from list cat id = " + editcat.getCatId());
                            dbQtaCatsList.remove(i); //remove from list if editcat have a copy. so that left behind is deleted by user
                            continue;//not delete, force break
                        }
                    }
                    editcat.setQtaId(this.qtaBean.getQtaId());
                    editcat.setCountry(this.qtaBean.getCountry());
                    editcat.setQuota(this.qtaBean.getQuota());
                    session.update(editcat);
                }else {
                    editcat.setQtaId(this.qtaBean.getQtaId());
                    editcat.setCountry(this.qtaBean.getCountry());
                    editcat.setQuota(this.qtaBean.getQuota());
                    session.save(editcat);//LotId is null, new records or can be existing record update
                }
            }
            
            //delete remaining in lotlist
            for ( int i = 0; i < dbQtaCatsList.size(); i++){
                dbcat = (QuotaCats)dbQtaCatsList.get(i);
                System.err.println("delete cat id  = " + dbcat.getCatId());
                session.delete(dbcat);
            }
            
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            return null;
        }finally {
            HibernateUtil.closeSession();
        }
        //System.out.println("Save success for " + prodSchBean.getProdSchId());
        return ("success");
    }
    
    
    public String editQtaMast(){
        System.out.println("Call edit Quota mast");
        return ("editquotamast");
    }
    
    
    //PROPERTY: dbEditCatBean
    public void setDbEditQtaBean(QuotaMast dbEditQtaBean) {
        this.qtaBean  = dbEditQtaBean;
    }
    
    
    public QuotaMast getDbEditQtaBean() {
        System.err.println("calling get bean for update");
        //Should only retrieve if object not yet retrieved !! else will be to heavy and unneccessary query !
        if ( this.qtaBean == null) {
            System.err.println("Retrieve bean from database for edit ...");
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(QuotaMast.class);
                System.err.println("qtaId = " + foxySessionData.getPageParameterLong());
                crit.add(Expression.eq("qtaId", foxySessionData.getPageParameterLong()));
                List result = crit.list();
                System.err.println("Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.qtaBean = (QuotaMast)result.get(0);
                } else {
                    System.err.println("No Quota bean with qtaId = " + foxySessionData.getPageParameterLong());
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
        return this.qtaBean;
    }
    
    
    public QuotaMast getQtaBean(){
        if ( qtaBean == null ){
            qtaBean = new  QuotaMast();
        }
        return qtaBean;
    }
    
    public void setQtaBean(QuotaMast qtaBean){
        this.qtaBean = qtaBean;
    }
    
    public QuotaCats getQtaCatBean() {
        if ( this.qtaCatBean == null){
            qtaCatBean = new QuotaCats();
        }
        return qtaCatBean;
    }
    
    public void setQtaCatBean(QuotaCats qtaCatBean) {
        this.qtaCatBean = qtaCatBean;
    }
    
    
    
    public String[] getSelectedCats() {
        return selectedCats;
    }
    
    public void setSelectedCats(String[] selectedCats) {
        this.selectedCats = selectedCats;
    }
    
    
    //QuotaCats map place holder for new entry record
    public TreeMap<String, QuotaCats> getQuotaCatsMap() {
        if ( this.quotaCatsMap == null){
            this.quotaCatsMap = new TreeMap();
        }
        return quotaCatsMap;
    }
    
    public void setQuotaCatsMap(TreeMap<String, QuotaCats> quotaCatsMap) {
        this.quotaCatsMap = quotaCatsMap;
    }
    
    
    public TreeMap<String, QuotaCats> getEditQuotaCatsMap() {
        if ( this.quotaCatsMap == null ) {
            try {
                this.quotaCatsMap = new  TreeMap();
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(QuotaCats.class);
                //System.err.println("prodSchId = " + this.prodSchBean.getProdSchId());
                //Retrieve all lots info
                crit.add(Expression.eq("qtaId", this.qtaBean.getQtaId()));
                crit.addOrder(Order.asc("catId"));
                List result = crit.list();
                QuotaCats tmpobj = null;
                for ( int i = 0; i < result.size(); i++){
                    tmpobj = (QuotaCats)result.get(i);
                    if(tmpobj != null){
                        quotaCatsMap.put(tmpobj.getCatId().toString(), tmpobj);
                    }
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
        return quotaCatsMap;
    }
    
    
    public void setEditQuotaCatsMap(TreeMap<String, QuotaCats> quotaCatsMap) {
        this.quotaCatsMap = quotaCatsMap;
    }
    
    
    
    public String getAddQuotaCatSta() {
        return addQuotaCatSta;
    }
    
    public void setAddQuotaCatSta(String addQuotaCatSta) {
        this.addQuotaCatSta = addQuotaCatSta;
    }
    
    //To generate ListBox content (display only)
    public List getQuotaCatsList(){
        System.err.println("Get Quota Cat list ...");
        if ( this.quotaCatsList == null) {
            this.quotaCatsList =  new ArrayList();
        } else {
            this.quotaCatsList.clear();
        }
        
        if ( quotaCatsMap != null) {
            QuotaCats qCatsObj = null;
            Set st = quotaCatsMap.entrySet();
            Iterator it = st.iterator();
            try {
                while( it.hasNext()){
                    Map.Entry me = (Map.Entry)it.next();
                    qCatsObj = (QuotaCats)me.getValue();
                    quotaCatsList.add(new SelectItem(me.getKey(), qCatsObj.getListDisplay()));
                }
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }
        return quotaCatsList;
    }
    
    public List getEditQuotaCatsList(){
        this.getEditQuotaCatsMap(); //Force to refresh Map with db records
        return this.getQuotaCatsList();
    }
    
    
    public String onAddCats() {
        if ( this.qtaCatBean.getCatId() == null){
            this.addQuotaCatSta = "Failed to add category into list, Category can not Empty";
        } else if (this.qtaCatBean.getMultiplier() == null ){
            this.addQuotaCatSta = "Failed to add category into list, multiplier required";
        } else {
            this.addQuotaCatSta = null;
            try {
                if ( this.qtaCatBean != null){
                    QuotaCats qc = new  QuotaCats();
                    qc.setCatId(this.qtaCatBean.getCatId());
                    qc.setMultiplier(this.qtaCatBean.getMultiplier());
                    qc.setStatus("A");
                    quotaCatsMap.put(qc.getCatId().toString(), qc);
                    //this.qtaCatBean = null; //force to recreate a new one
                }
            }catch ( Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
    
    
//Delete selected category from list
    public String delQuotaCatsListItem() {
        try {
            for ( int i = 0; i < selectedCats.length; i++) {
                quotaCatsMap.remove(selectedCats[i]);
            }
        }catch ( Exception e){
            e.printStackTrace();
        }finally {
            
        }
        return null;
    }
    
    
    
    public String search() {
        this.foxySessionData.setAction(LST);
        foxyTable.setFirst(0);
        return (null);
    }
    
    
    public DataModel getQtaListModel() {
        try {
            int firstrow = this.foxyTable.getFirst();
            int pagesize = this.foxyTable.getRows();
            
            Number numofRec = null;
            System.err.println("Calling getQuotaListModel [from " +
                    firstrow + ", pgsize " + pagesize + "]");
            
            String qtapattern = this.sessQuotaMastBean.getQuota();
            if (qtapattern != null) {
                String likestr = "%" +  qtapattern.replace('*', '%') + "%";
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria criteria = session.createCriteria(QuotaMastJoinCats.class);
                
                //Get Total row count
                criteria.setProjection(Projections.rowCount());
                criteria.add(Expression.like("quota", likestr));
                criteria.add(Expression.eq("country", this.getSessQuotaMastBean().getCountry()));
                numofRec = ((Number) criteria.uniqueResult());
                tx.commit();
                
                System.err.println("Total Number of records: [" + numofRec.intValue() + "]");
                numofRec = numofRec == null ? 0 : numofRec.intValue();
                
                
                //Retrieve subset of record base on foxyTable current view page
                tx = session.beginTransaction();
                criteria = session.createCriteria(QuotaMastJoinCats.class);
                criteria.add(Expression.like("quota", likestr));
                criteria.add(Expression.eq("country", this.getSessQuotaMastBean().getCountry()));
                criteria.setFirstResult(firstrow);
                criteria.setMaxResults(pagesize);
                List result = criteria.list();
                tx.commit();
                if (qtaListModel != null) {
                    qtaListModel = null;
                }
                System.err.println("Query Results size = [" + result.size() + "] recs [from " +
                        firstrow + ", pgsize " + pagesize + "]");
                qtaListModel = (DataModel)new FoxyPagedDataModel(result, numofRec.intValue(), pagesize);
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
        
        return qtaListModel;
    }
    
    
    public String delete(){
        try {
            System.err.println("Deleting quota id = " + foxySessionData.getPageParameterLong());
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            String  qstr  = new String("DELETE QuotaMast t ");
            qstr = qstr.concat("WHERE t.qtaId = :pqtaId ");
            Query q = session.createQuery(qstr);
            q.setLong("pqtaId", foxySessionData.getPageParameterLong());
            q.executeUpdate();
            
            String  qstr2  = new String("DELETE QuotaCats t ");
            qstr2 = qstr2.concat("WHERE t.qtaId = :pqtaId ");
            Query q2 = session.createQuery(qstr2);
            q2.setLong("pqtaId", foxySessionData.getPageParameterLong());
            q2.executeUpdate();
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
