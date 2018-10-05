/*
 * FoxyCustomerPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import javax.faces.application.FacesMessage;
import com.foxy.db.Customer;
import com.foxy.db.HibernateUtil;
import com.foxy.util.FoxyPagedDataModel;
import com.foxy.util.ListData;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.DataModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.hibernate.Query;


/**
 *
 * @author eric
 */
public class FoxyCustomerPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private DataModel custListModel;
    private Customer cust = null;
    private Customer dbEditCustBean = null;
    
    
    /** Creates a new instance of FoxyCustomerPage */
    public FoxyCustomerPage() {
        super(new String("CustomerForm"));
        
        try {
            this.isAuthorize(MENU_CODE);
            //System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    
    
    public String saveAdd() {
        System.out.println("Save Add");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            cust.setStatus("A");
            session.save(cust);
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
        
        ListData ld = (ListData)this.getBean("listData");
        ld.resetCustomer(); //To force release data list for combo box listing
        return ("success");
    }
    
    public String editCustomer(){
        System.out.println("Call edit cust");
        return ("editcustomer");
    }
    
    
    //PROPERTY: dbEditCustBean
    
    public void setDbEditCustBean(Customer cb) {
        this.dbEditCustBean  = cb;
    }
    
    
    public Customer getDbEditCustBean() {
        System.err.println("calling update");
        //Should only retrieve if object not yet retrieved !! else will be to heavy and unneccessary query !
        if ( this.dbEditCustBean == null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(Customer.class);
                System.err.println("custCode = " + foxySessionData.getPageParameter());
                crit.add(Expression.eq("custCode", foxySessionData.getPageParameter()));
                List result = crit.list();
                System.err.println("Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.dbEditCustBean = (Customer)result.get(0);
                } else {
                    System.err.println("No user with userid = " + foxySessionData.getPageParameter());
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
        return this.dbEditCustBean;
    }
    
    
    public String saveEdit() {
        System.out.println("Save Edit");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            session.update(dbEditCustBean);
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
        
        ListData ld = (ListData)this.getBean("listData");
        ld.resetCustomer(); //To force release data list for combo box listing
        return ("success");
    }
    
    
    
    public Customer getCustBean(){
        if ( cust == null ){
            cust = new  Customer();
        }
        return cust;
    }
    
    public void setCustBean(Customer c){
        this.cust = c;
    }
    
    
    public String search() {
        this.foxySessionData.setAction(LST);
        foxyTable.setFirst(0);
        return (null);
    }
    
    
    
    public DataModel getCustomerListModel() {
        int firstrow = foxyTable.getFirst();
        int pagesize = foxyTable.getRows();
        Number numofRec = null;
        
        try {
            if (this.searchKey != null) {
                String likestr = "%" +  this.searchKey.replace('*', '%') + "%";
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria criteria = session.createCriteria(Customer.class);
                
                //Get Total row count
                criteria.setProjection(Projections.rowCount());
                criteria.add(Expression.like("custCode", likestr));
                numofRec = ((Number) criteria.uniqueResult());
                tx.commit();
                
                //System.err.println("Total Number of records: [" + numofRec.intValue() + "]");
                numofRec = numofRec == null ? 0 : numofRec.intValue();
                
                
                //Retrieve subset of record base on myTable current view page
                tx = session.beginTransaction();
                criteria = session.createCriteria(Customer.class);
                criteria.add(Expression.like("custCode", likestr));
                criteria.setFirstResult(firstrow);
                criteria.setMaxResults(pagesize);
                List result = criteria.list();
                tx.commit();
                session.clear();
                if (custListModel != null) {
                    custListModel = null;
                }
                //System.err.println("Query Results size = [" + result.size() + "] recs");
                custListModel = (DataModel)new FoxyPagedDataModel(result, numofRec.intValue(), pagesize);
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
        }finally {
            HibernateUtil.closeSession();
        }
        
        return custListModel;
    }
    
    
    public String delete(){
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            String  qstr  = new String("DELETE Customer t ");
            qstr = qstr.concat("WHERE t.custCode = :pcustCode ");
            Query q = session.createQuery(qstr);
            q.setString("pcustCode", foxySessionData.getPageParameter());
            q.executeUpdate();
            
            //Housekeeping Cust Brand table
            qstr = "DELETE CustBrand WHERE custCode = :pcustCode";
            Query q2 = session.createQuery(qstr);
            q2.setString("pcustCode", foxySessionData.getPageParameter());
            q2.executeUpdate();
            //Housekeeping Cust Division table
            qstr = "DELETE CustDivision WHERE custCode = :pcustCode";
            Query q3 = session.createQuery(qstr);
            q3.setString("pcustCode", foxySessionData.getPageParameter());
            q3.executeUpdate();
            
            
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            return (null);
        } finally {
            HibernateUtil.closeSession();
            ListData ld = (ListData)this.getBean("listData");
            ld.resetCustomer(); //To force release data list for combo box listing
        }
        
        return null;
    }
    
    
    
}
