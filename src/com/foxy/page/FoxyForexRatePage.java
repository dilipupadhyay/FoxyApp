/*
 * FoxyForexRatePage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import java.util.Calendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import com.foxy.db.ForexRate;
import com.foxy.db.HibernateUtil;
import com.foxy.util.FoxyPagedDataModel;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.DataModel;
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
public class FoxyForexRatePage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private DataModel ListModel;
    private ForexRate forexRateBean = null;
    private ForexRate dbEditForexRateBean = null;
    private boolean autoPopulateForex = true;
    private Integer populateDays = null;
    
    
    
    /** Creates a new instance of FoxyForexRatePage */
    public FoxyForexRatePage() {
        super(new String("ForexRateForm"));
        
        try {
            this.isAuthorize(MENU_CODE);
            //System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public boolean isAutoPopulateForex() {
        return autoPopulateForex;
    }
    
    public void setAutoPopulateForex(boolean autoPopulateForex) {
        this.autoPopulateForex = autoPopulateForex;
    }
    
    
    private String getFoxyParam(String key){
        ResourceBundle foxyParam = null;
        Locale defaultEng = new Locale("en");
        String tmpstr = null;
        try {
            foxyParam = ResourceBundle.getBundle("Foxy", defaultEng);
            tmpstr = foxyParam.getString(key);
        } catch ( MissingResourceException e ) {
            //e.printStackTrace();
            System.err.println("Parameter key [AutoPopulateForexRate] not found");
        }finally {
        }
        return tmpstr;
    }
    
    public Integer getPopulateDays() {
        if ( populateDays == null ){
            populateDays = new Integer(getFoxyParam("AutoPopulateForexRate"));
        }
        return populateDays;
    }
    
    
    public ForexRate getForexRateBean() {
        if ( forexRateBean == null){
            forexRateBean = new ForexRate();
        }
        return forexRateBean;
    }
    
    public void setForexRateBean(ForexRate forexRateBean) {
        this.forexRateBean = forexRateBean;
    }
    
    
    public String saveAdd() {
        //System.out.println("Save Add");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            forexRateBean.setStatus("A");
            forexRateBean.setParentId(0L);
            session.save(forexRateBean);
            //System.out.println("Object id = " + this.forexRateBean.getForexRateId());
            if ( this.autoPopulateForex){
                //System.out.println("Auto populate for Object id = " + this.forexRateBean.getForexRateId());
                ForexRate tmpForex = null;
                Calendar cal = Calendar.getInstance();
                for ( int i = 0; i < this.getPopulateDays(); i++ ){
                    //System.out.println("Auto create num " + i);
                    tmpForex = new ForexRate();
                    tmpForex.setParentId(this.forexRateBean.getForexRateId());
                    tmpForex.setCurCode(this.forexRateBean.getCurCode());
                    //Increase the date by one
                    cal.setTime(this.forexRateBean.getRateDate());
                    cal.add(Calendar.DAY_OF_MONTH, 1+i); //0+1, 0+2 ...
                    tmpForex.setRateDate(cal.getTime());
                    tmpForex.setPerUsdRate(this.forexRateBean.getPerUsdRate());
                    tmpForex.setStatus(this.forexRateBean.getStatus());
                    session.save(tmpForex);
                }
            }
            tx.commit();
            HibernateUtil.closeSession();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            e.printStackTrace();
            return null;
        }finally {
            HibernateUtil.closeSession();
        }
        
        return ("success");
    }
    
    public String editForexRate(){
        //System.out.println("Call edit ForexRate");
        return ("editforexrate");
    }
    
    
//PROPERTY: dbEditForexRateBean
    public void setDbEditForexRateBean(ForexRate fr) {
        this.dbEditForexRateBean  = fr;
    }
    
    
    public ForexRate getDbEditForexRateBean() {
        //System.err.println("calling update");
        //Should only retrieve if object not yet retrieved !! else will be to heavy and unneccessary query !
        if ( this.dbEditForexRateBean == null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(ForexRate.class);
                //System.err.println("forexRateId = " + foxySessionData.getPageParameterLong());
                crit.add(Expression.eq("forexRateId", foxySessionData.getPageParameterLong()));
                List result = crit.list();
                //System.err.println("Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.dbEditForexRateBean = (ForexRate)result.get(0);
                } else {
                    System.err.println("No sup with forexRateId = " + foxySessionData.getPageParameterLong());
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
            }finally {
                HibernateUtil.closeSession();
            }
        }
        return this.dbEditForexRateBean;
    }
    
    
    public String saveEdit() {
        //System.out.println("Save Edit");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            session.update(dbEditForexRateBean);
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
    
    
    public String search() {
        this.foxySessionData.setAction(LST);
        foxyTable.setFirst(0);
        return (null);
    }
    
    public DataModel getForexRateListModel() {
        int firstrow = foxyTable.getFirst();
        int pagesize = foxyTable.getRows();
        Number numofRec = null;
        
        try {
            if (this.getSearchKey() != null) {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria criteria = session.createCriteria(ForexRate.class);
                
                //Get Total row count
                criteria.setProjection(Projections.rowCount());
                criteria.add(Expression.like("curCode", this.searchKey));
                if ( this.getFromDate() != null){
                    criteria.add(Expression.ge("rateDate", this.getFromDate()));
                }
                
                if ( this.getToDate() != null){
                    criteria.add(Expression.le("rateDate", this.getToDate()));
                }
                
                numofRec = ((Number) criteria.uniqueResult());
                tx.commit();
                
                //System.err.println("Total Number of records: [" + numofRec.intValue() + "]");
                numofRec = numofRec == null ? 0 : numofRec.intValue();
                
                
                //Retrieve subset of record base on myTable current view page
                tx = session.beginTransaction();
                criteria = session.createCriteria(ForexRate.class);
                criteria.add(Expression.like("curCode", this.searchKey));
                if ( this.getFromDate() != null){
                    criteria.add(Expression.ge("rateDate", this.getFromDate()));
                }
                
                if ( this.getToDate() != null){
                    criteria.add(Expression.le("rateDate", this.getToDate()));
                }
                criteria.addOrder(Order.asc("rateDate"));
                criteria.setFirstResult(firstrow);
                criteria.setMaxResults(pagesize);
                List result = criteria.list();
                tx.commit();
                session.clear();
                if (ListModel != null) {
                    ListModel = null;
                }
                //System.err.println("Query Results size = [" + result.size() + "] recs");
                ListModel = (DataModel)new FoxyPagedDataModel(result, numofRec.intValue(), pagesize);
            } else {
                System.err.println("Search key is null !!! [ " + this.searchKey + " ]");
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
        }finally {
            HibernateUtil.closeSession();
        }
        
        return ListModel;
    }
    
    
    public String delete(){
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            String  qstr  = new String("DELETE ForexRate t ");
            qstr = qstr.concat("WHERE t.forexRateId = :pforexrateid ");
            Query q = session.createQuery(qstr);
            q.setLong("pforexrateid", foxySessionData.getPageParameterLong());
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
