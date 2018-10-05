/*
 * FoxySupplierPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import javax.faces.application.FacesMessage;
import com.foxy.db.Supplier;
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
import org.hibernate.criterion.Projections;
import org.hibernate.Query;
import com.foxy.page.FoxySuccessPage;


/**
 *
 * @author hcting
 */
public class FoxySupplierPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private DataModel ListModel;
    private Supplier supBean = null;
    private Supplier dbEditSupBean = null;
    
    
    /** Creates a new instance of FoxySupplierPage */
    public FoxySupplierPage() {
        super(new String("SupplierForm"));
        
        try {
            this.isAuthorize(MENU_CODE);
            //System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public Supplier getSupBean() {
        if ( supBean == null){
            supBean = new Supplier();
        }
        return supBean;
    }
    
    public void setSupBean(Supplier supBean) {
        this.supBean = supBean;
    }
    
    
    public String saveAdd() {
        //System.out.println("Save Add");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            supBean.setStatus("A");
            session.save(supBean);
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
        
        FoxySuccessPage sp = (FoxySuccessPage)this.getBean("foxySuccess");
        sp.setMsg("Saving Supplier code [" + supBean.getSupCode() + "]   Supplier Name [" + supBean.getSupName() + "]");
        return ("success");
    }
    
    public String editSupplier(){
        //System.out.println("Call edit Supplier");
        return ("editsupplier");
    }
    
    
    //PROPERTY: dbEditSupBean
    public void setDbEditSupBean(Supplier sup) {
        this.dbEditSupBean  = sup;
    }
    
    
    public Supplier getDbEditSupBean() {
        //System.err.println("calling update");
        //Should only retrieve if object not yet retrieved !! else will be to heavy and unneccessary query !
        if ( this.dbEditSupBean == null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(Supplier.class);
                //System.err.println("supid = " + foxySessionData.getPageParameterLong());
                crit.add(Expression.eq("supId", foxySessionData.getPageParameterLong()));
                List result = crit.list();
                //System.err.println("Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.dbEditSupBean = (Supplier)result.get(0);
                } else {
                    System.err.println("No sup with supid = " + foxySessionData.getPageParameterLong());
                }
                tx.commit();
            } catch (HibernateException e) {
                //do something here with the exception
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            } catch (Exception e){
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            }finally {
                HibernateUtil.closeSession();
            }
        }
        return this.dbEditSupBean;
    }
    
    
    public String saveEdit() {
        //System.out.println("Save Edit");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            session.update(dbEditSupBean);
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
    
    
    
    public DataModel getSupplierListModel() {
        int firstrow = foxyTable.getFirst();
        int pagesize = foxyTable.getRows();
        Number numofRec = null;
        
        try {
            if (this.searchKey != null) {
                String likestr = "%" +  this.searchKey.replace('*', '%') + "%";
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria criteria = session.createCriteria(Supplier.class);
                
                //Get Total row count
                criteria.setProjection(Projections.rowCount());
                criteria.add(Expression.like("supCode", likestr));
                numofRec = ((Number) criteria.uniqueResult());
                tx.commit();
                
                //System.err.println("Total Number of records: [" + numofRec.intValue() + "]");
                numofRec = numofRec == null ? 0 : numofRec.intValue();
                
                
                //Retrieve subset of record base on myTable current view page
                tx = session.beginTransaction();
                criteria = session.createCriteria(Supplier.class);
                criteria.add(Expression.like("supCode", likestr));
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
                System.err.println("Search key is null !!!");
            }
        } catch (HibernateException e) {
            //do something here with the exception
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
        } catch (Exception e){
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
            String  qstr  = new String("DELETE Supplier t ");
            qstr = qstr.concat("WHERE t.supId = :psupid ");
            Query q = session.createQuery(qstr);
            q.setLong("psupid", foxySessionData.getPageParameterLong());
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
