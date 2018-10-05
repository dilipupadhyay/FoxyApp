/*
 * FoxyParameterPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import com.foxy.db.HibernateUtil;
import com.foxy.db.Parameter;
import com.foxy.util.FoxyPagedDataModel;
import com.foxy.util.ListData;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
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
 * @author eric
 */
public class FoxyParameterPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private Parameter parameterBean = null;
    
    
    /** Creates a new instance of FoxyParameterPage */
    public FoxyParameterPage() {
        super(MENU_CODE);
        this.isAuthorize(MENU_CODE);
    }
    
    
    //parameterBean getter
    public Parameter getParameterBean() {
        if ( parameterBean == null ){
            parameterBean = new  Parameter();
        }
        return parameterBean;
    }
    
    //parameterBean setter
    public void setParameterBean(Parameter parameterBean) {
        this.parameterBean = parameterBean;
    }
    
    
    
    /**
     *  Retrive data from database based on recordid
     */
    public Parameter getDbParameterBean() {
        //Should only retrieve if object not yet retrieved !! else will be to heavy and unneccessary query !
        if ( this.parameterBean == null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(Parameter.class);
                System.err.println("id = " + foxySessionData.getPageParameterLong());
                crit.add(Expression.eq("id", foxySessionData.getPageParameterLong()));
                List result = crit.list();
                System.err.println("Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.parameterBean = (Parameter)result.get(0);
                } else {
                    System.err.println("No parameter record with id = " + foxySessionData.getPageParameterLong());
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
        return this.parameterBean;
    }
    
    //Setter required to allow update from client to be sync
    public void setDbParameterBean(Parameter parameterBean) {
        this.parameterBean = parameterBean;
    }
    
    
    /**
     *  Prepare data for parameter listing
     */
    public DataModel getParameterList() {
        Number numofRec = null;
        int firstrow = foxyTable.getFirst();
        int pagesize = foxyTable.getRows();
        
        try {
            if (this.getSearchKey() != null) {
                String likestr = "%" +  this.searchKey.replace('*', '%') + "%";
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria criteria = session.createCriteria(Parameter.class);
                
                //Get Total row count
                criteria.setProjection(Projections.rowCount());
                criteria.add(Expression.like("description", likestr));
                criteria.add(Expression.eq("category", this.getSearchType()));
                criteria.addOrder(Order.asc("sequence"));
                numofRec = ((Number) criteria.uniqueResult());
                tx.commit();
                
                System.err.println("Total Number of records: [" + numofRec.intValue() + "]");
                System.err.println("Where clause " + likestr + "   " + this.getSearchType());
                numofRec = numofRec == null ? 0 : numofRec.intValue();
                
                
                //Retrieve subset of record base on foxyTable current view page
                tx = session.beginTransaction();
                criteria = session.createCriteria(Parameter.class);
                criteria.add(Expression.like("description", likestr));
                criteria.add(Expression.eq("category", this.getSearchType()));
                criteria.addOrder(Order.asc("sequence"));
                criteria.setFirstResult(firstrow);
                criteria.setMaxResults(pagesize);
                List result = criteria.list();
                tx.commit();
                if (this.foxyListModel != null) {
                    this.foxyListModel = null;
                }
                System.err.println("Query Results size = [" + result.size() + "] recs [from " +
                        firstrow + ", pgsize " + pagesize + "]");
                this.foxyListModel = (DataModel)new FoxyPagedDataModel(result, numofRec.intValue(), pagesize);
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
        return this.foxyListModel;
    }
    
    /**
     *  Save data into database (Both add and edit used same back bean, only differ is one
     *  created will all null value and the other call getDbParameterBean to initialise with db
     */
    public String saveAdd() {
        System.out.println("Save Parameter");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            parameterBean.setStatus("A");
            session.saveOrUpdate(parameterBean);
            tx.commit();
            session.clear();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            return null;
        } finally {
            HibernateUtil.closeSession();
            ListData ld = (ListData)this.getBean("listData");
            ld.resetParamList(parameterBean.getCategory()); //To force release data list for combo box listing
        }
        return ("success");
    }
    
    public String saveEdit(){
        return saveAdd();//use same method is ok
    }
    
    
    /**
     *  Prepare for listing
     */
    public String search() {
        this.foxySessionData.setAction(LST);
        this.foxyTable.setFirst(0);
        return ("success");
    }
    
    
    public String editParameter(){
        return ("editparameter");
    }
    
    
    public String delete(){
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            String  qstr  = new String("DELETE Parameter t ");
            qstr = qstr.concat("WHERE t.id = :pid ");
            Query q = session.createQuery(qstr);
            q.setLong("pid", foxySessionData.getPageParameterLong());
            q.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            return (null);
        } finally {
            HibernateUtil.closeSession();
            ListData ld = (ListData)this.getBean("listData");
            ld.resetParamList(this.getSearchType()); //To force release data list for combo box listing
        }
        
        return null;
    }
    
    
}
