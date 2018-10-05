/*
 * FoxyCustomerBrandPage.java
 *
 * Created on Aug 24, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

/**
 *
 * @author hcting
 */

import javax.faces.application.FacesMessage;
import com.foxy.db.CustBrand;
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


/**
 *
 * @author hcting
 */
public class FoxyCustomerBrandPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private CustBrand custBrandBean = null;
    private CustBrand dbEditCustBrandBean = null;
    private DataModel listModel;
    
    /** Creates a new instance of FoxyCustomerPage */
    public FoxyCustomerBrandPage() {
        super(new String("CustomerBrandForm"));
        
        try {
            this.isAuthorize(MENU_CODE);
            System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    
    public String saveAdd() {
        System.out.println("Save Add");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            custBrandBean.setStatus("A");
            session.save(custBrandBean);
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
    
    public String editCustomerBrand(){
        System.out.println("Call edit cust brand");
        return ("editcustomerbrand");
    }
    
    
    //PROPERTY: dbEditCustBrandBean
    public void setDbEditCustBrandBean(CustBrand cb) {
        this.dbEditCustBrandBean  = cb;
    }
    
    
    public CustBrand getDbEditCustBrandBean() {
        System.err.println("calling update");
        //Should only retrieve if object not yet retrieved !! else will be to heavy and unneccessary query !
        if ( this.dbEditCustBrandBean == null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(CustBrand.class);
                System.err.println("id = " + foxySessionData.getPageParameterLong());
                crit.add(Expression.eq("id", foxySessionData.getPageParameterLong()));
                List result = crit.list();
                System.err.println("Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.dbEditCustBrandBean = (CustBrand)result.get(0);
                } else {
                    System.err.println("No user with userid = " + foxySessionData.getPageParameterLong());
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
        return this.dbEditCustBrandBean;
    }
    
    
    public String saveEdit() {
        System.out.println("Save Edit");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            session.update(dbEditCustBrandBean);
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
    
    
    
    public CustBrand getCustBrandBean(){
        if ( custBrandBean == null ){
            custBrandBean = new  CustBrand();
        }
        return custBrandBean;
    }
    
    public void setCustBrandBean(CustBrand c){
        this.custBrandBean = c;
    }
    
    
    
    public String search() {
        this.foxySessionData.setAction(LST);
        foxyTable.setFirst(0);
        return (null);
    }
    
    
    
    public DataModel getCustBrandListModel() {
        int firstrow = foxyTable.getFirst();
        int pagesize = foxyTable.getRows();
        Number numofRec = null;
        
        try {
            if (this.searchKey != null) {
                String likestr = "%" +  this.searchKey.replace('*', '%') + "%";
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria criteria = session.createCriteria(CustBrand.class);
                
                //Get Total row count
                criteria.setProjection(Projections.rowCount());
                criteria.add(Expression.eq("custCode", this.getSearchType()));
                criteria.add(Expression.like("brandCode", likestr));
                numofRec = ((Number) criteria.uniqueResult());
                tx.commit();
                
                //System.err.println("Total Number of records: [" + numofRec.intValue() + "]");
                numofRec = numofRec == null ? 0 : numofRec.intValue();
                
                
                //Retrieve subset of record base on myTable current view page
                tx = session.beginTransaction();
                criteria = session.createCriteria(CustBrand.class);
                criteria.add(Expression.eq("custCode", this.getSearchType()));
                criteria.add(Expression.like("brandCode", likestr));
                criteria.setFirstResult(firstrow);
                criteria.setMaxResults(pagesize);
                List result = criteria.list();
                tx.commit();
                session.clear();
                if (listModel != null) {
                    listModel = null;
                }
                //System.err.println("Query Results size = [" + result.size() + "] recs");
                listModel = (DataModel)new FoxyPagedDataModel(result, numofRec.intValue(), pagesize);
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
        
        return listModel;
    }
    
    
    public String delete(){
        try {
            CustBrand cb = getDbEditCustBrandBean();//retrieve based on foxySessionData.getPageParameterLong() should return same bean
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            String  qstr  = "DELETE CustDivision WHERE custCode = :pcustCode  AND brandCode = :pbrandCode";
            Query q = session.createQuery(qstr);
            q.setString("pcustCode", cb.getCustCode());
            q.setString("pbrandCode", cb.getBrandCode());
            q.executeUpdate();
            
            qstr  = "DELETE CustBrand t WHERE t.id = :pid ";
            Query q2 = session.createQuery(qstr);
            q2.setLong("pid", foxySessionData.getPageParameterLong());
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
