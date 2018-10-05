/*
 * FoxyCustomerDivisionPage.java
 *
 * Created on June 20, 2006, 6:18 PM
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
import com.foxy.db.CustDivision;
import com.foxy.db.CustBrand;
import com.foxy.db.HibernateUtil;
import com.foxy.util.FoxyPagedDataModel;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
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
public class FoxyCustomerDivisionPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    
    private DataModel listModel;
    private CustDivision custDivBean = null;
    private CustDivision dbEditCustDivBean = null;
    private List resultList = null;  //List of brand for current custcode
    private List brandList = null;
    
    
    /** Creates a new instance of FoxyCustomerAddDivisionPage */
    public FoxyCustomerDivisionPage() {
        super(new String("CustomerDivisionForm"));
        
        try {
            this.isAuthorize(MENU_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if ( custDivBean == null){
            custDivBean = new CustDivision();
        }
    }
    
    public CustDivision getCustDivBean(){
        //System.err.println("===>get bean now");
        return custDivBean;
    }
    
    public void setCustDivBean(CustDivision c){
        //System.err.println("===>set bean now");
        this.custDivBean = c;
    }
    
    
    public CustDivision getDbEditCustDivBean() {
        System.err.println("calling update");
        //Should only retrieve if object not yet retrieved !! else will be to heavy and unneccessary query !
        if ( this.dbEditCustDivBean == null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(CustDivision.class);
                System.err.println("id = " + foxySessionData.getPageParameterLong());
                crit.add(Expression.eq("id", foxySessionData.getPageParameterLong()));
                List result = crit.list();
                System.err.println("Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.dbEditCustDivBean = (CustDivision)result.get(0);
                } else {
                    System.err.println("No user with userid = " + foxySessionData.getPageParameterLong());
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
        return this.dbEditCustDivBean;
    }
    
    
    
    public List getBrandItemsList(){
        return getBrandItemsListGeneric(this.custDivBean.getCustCode());
    }
    
    
    public List getBrandItemsListEdit(){
        return getBrandItemsListGeneric(this.dbEditCustDivBean.getCustCode());
    }
    
    public List getBrandItemsListGeneric(String currentcustcode){
        
        //System.err.println("===>get bean list now");
        brandList = null;
        brandList = new ArrayList();
        brandList.add(new SelectItem(new String(""), new String(""))); //Always add a null items, event no records
        
        
        if ( currentcustcode != null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(CustBrand.class);
                //System.err.println("custCode = " + currentcustcode);
                crit.add(Expression.eq("custCode", currentcustcode));
                resultList = crit.list();
                //System.err.println("Result size = " + resultList.size());
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
            
            
            for (int i = 0; i < this.resultList.size(); i ++) {
                CustBrand cbrand = (CustBrand) this.resultList.get(i);
                brandList.add(new SelectItem(cbrand.getBrandCode(), cbrand.getBrandCode() + " - " + cbrand.getBrandDesc()));
                //System.out.println(i + " - " + cbrand.getBrandCode());
            }
        }
        
        return brandList;
        //return list1;
    }
    
    
    
    
    public String saveAdd() {
        //System.out.println("Save Add");
        
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            //System.err.println("Brandcode = " + custDivBean.getBrandCode());
            custDivBean.setStatus("A");
            session.save(custDivBean);
            tx.commit();
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
    
    
    public String saveEdit() {
        System.out.println("Save Edit");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            session.update(dbEditCustDivBean);
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
    
    
    
    public String editCustomerDivision(){
        //System.out.println("Call edit cust division");
        return ("editcustomerdivision");
    }
    
    
    public String search() {
        this.foxySessionData.setAction(LST);
        foxyTable.setFirst(0);
        return (null);
    }
    
    
    public DataModel getCustDivListModel() {
        int firstrow = foxyTable.getFirst();
        int pagesize = foxyTable.getRows();
        Number numofRec = null;
        
        try {
            if (this.searchKey != null) {
                String likestr = "%" +  this.searchKey.replace('*', '%') + "%";
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria criteria = session.createCriteria(CustDivision.class);
                
                //Get Total row count
                criteria.setProjection(Projections.rowCount());
                criteria.add(Expression.eq("custCode", this.getSearchType()));
                criteria.add(Expression.like("divCode", likestr));
                numofRec = ((Number) criteria.uniqueResult());
                tx.commit();
                
                //System.err.println("Total Number of records: [" + numofRec.intValue() + "]");
                numofRec = numofRec == null ? 0 : numofRec.intValue();
                
                
                //Retrieve subset of record base on myTable current view page
                tx = session.beginTransaction();
                criteria = session.createCriteria(CustDivision.class);
                criteria.add(Expression.eq("custCode", this.getSearchType()));
                criteria.add(Expression.like("divCode", likestr));
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
        }catch (Exception e){
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
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            String  qstr  = new String("DELETE CustDivision t ");
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
        }
        
        return null;
    }
}
