/*
 * FoxyProdSchedulePage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;


import java.util.TreeMap;
import java.util.Set;
import javax.faces.application.FacesMessage;
import com.foxy.db.HibernateUtil;
import com.foxy.db.ProdSchedule;
import com.foxy.db.ProdScheduleLots;
import com.foxy.db.ProdScheduleJoinLots;
import com.foxy.db.Orders;
import com.foxy.util.FoxyPagedDataModel;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
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
public class FoxyProdSchedulePage extends Page implements Serializable{
    
    private static String MENU_CODE = new String("FOXY");
    
    private final Integer OUTERCOLCOUNT = 6;
    private String status = null;
    private String addLotStat = null;
    private ProdSchedule prodSchBean = null;
    private ProdSchedule sessProdSchBean = null;
    private DataModel prodSchListModel = null;
    private ProdScheduleLots prodLotBean = null;
    private TreeMap<String,ProdScheduleLots> lotsMap = null;
    private List lotsListItems = null;
    private String[] selectedLots = null;
    
    /**
     *
     * @author hcting
     */
    
    
    /** Creates a new instance of FoxyCustomerPage */
    public FoxyProdSchedulePage() {
        super(new String("ProdScheduleForm"));
        
        try {
            this.isAuthorize(MENU_CODE);
            System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (this.foxySessionData.getAction().equals(ADD)) {
            //System.err.println("Recreating ProdSchBean in session bean");
            this.prodSchBean = null;
            this.prodSchBean = new ProdSchedule();
            lotsMap = null;
        }else{
            //Get search parameter from session bean object
            sessProdSchBean = (ProdSchedule)this.getSessionObject1(ProdSchedule.class);
            //Create new object and replace session bean's object1 with this new one
            if ( sessProdSchBean == null){
                sessProdSchBean = new ProdSchedule();
                this.setSessionObject1((Object)sessProdSchBean);
            }
        }
        
    }
    
    
    public ProdSchedule getEditProdSchBean() {
        if ( this.prodSchBean == null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(ProdSchedule.class);
                //System.err.println("ee prodSchId = " + foxySessionData.getPageParameterLong());
                crit.add(Expression.eq("prodSchId", foxySessionData.getPageParameterLong()));
                List result = crit.list();
                //System.err.println("ee Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.prodSchBean = (ProdSchedule)result.get(0);
                } else {
                    System.err.println("No ProdSchedule record with id = " + foxySessionData.getPageParameterLong());
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
        return prodSchBean;
    }
    
    public void setEditProdSchBean(ProdSchedule prodSchBean) {
        this.prodSchBean = prodSchBean;
    }
    
    
    public List getLotsListItems(){
        if ( this.lotsListItems == null) {
            this.lotsListItems =  new ArrayList();
        } else {
            this.lotsListItems.clear();
        }
        
        if ( lotsMap != null) {
            ProdScheduleLots lotobj = null;
            Set st = lotsMap.entrySet();
            Iterator it = st.iterator();
            try {
                while( it.hasNext()){
                    Map.Entry me = (Map.Entry)it.next();
                    lotobj = (ProdScheduleLots)me.getValue();
                    lotsListItems.add(new SelectItem(me.getKey(), lotobj.getListDisplayFormat()));
                }
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }
        return lotsListItems;
    }
    
    
    public List getEditLotsListItems(){
        this.getEditLotsMap(); //Force to refesh the map list
        return getLotsListItems();
    }
    
    
    public TreeMap<String, ProdScheduleLots> getLotsMap() {
        if ( this.lotsMap == null ) {
            this.lotsMap = new  TreeMap();
        }
        return lotsMap;
    }
    
    public void setLotsMap(TreeMap<String, ProdScheduleLots> lotsMap) {
        this.lotsMap = lotsMap;
    }
    
    
    public TreeMap<String, ProdScheduleLots> getEditLotsMap() {
        if ( this.lotsMap == null ) {
            try {
                this.lotsMap = new  TreeMap();
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(ProdScheduleLots.class);
                //System.err.println("prodSchId = " + this.prodSchBean.getProdSchId());
                //Retrieve all lots info
                crit.add(Expression.eq("prodSchId", this.prodSchBean.getProdSchId()));
                crit.addOrder(Order.asc("lot"));
                List retlot = crit.list();
                ProdScheduleLots plot = null;
                for ( int i = 0; i < retlot.size(); i++){
                    plot = (ProdScheduleLots)retlot.get(i);
                    if(plot != null){
                        lotsMap.put(plot.getLot(), plot);
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
        return lotsMap;
    }
    
    
    public void setEditLotsMap(TreeMap<String, ProdScheduleLots> lotsMap) {
        this.lotsMap = lotsMap;
    }
    
    
    
    public String[] getSelectedLots() {
        return selectedLots;
    }
    
    public void setSelectedLots(String[] selectedLots) {
        this.selectedLots = selectedLots;
    }
    
    public String getAddLotStat() {
        return addLotStat;
    }
    
    public void setAddLotStat(String addLotStat) {
        this.addLotStat = addLotStat;
    }
    
    
    public ProdSchedule getProdSchBean(){
        //System.err.println("Get prod sch bean now ");
        if ( this.prodSchBean == null){
            this.prodSchBean = new ProdSchedule();
            //System.err.println("Creating new  prod sch bean now ");
        }
        return this.prodSchBean;
    }
    
    public void setProdSchBean(ProdSchedule prodSchBean) {
        this.prodSchBean = prodSchBean;
    }
    
    public ProdSchedule getSessProdSchBean(){
        return this.sessProdSchBean;
    }
    
    public void setSessProdSchBean(ProdSchedule sessprodSchBean) {
        this.sessProdSchBean = sessprodSchBean;
    }
    
    public ProdScheduleLots getProdLotBean() {
        if ( this.prodLotBean == null){
            prodLotBean = new ProdScheduleLots();
        }
        return prodLotBean;
    }
    
    public void setProdLotBean(ProdScheduleLots prodLotBean) {
        this.prodLotBean = prodLotBean;
    }
    
    public String saveEdit(){
        //System.out.println("Save Edit");
        try {
            //System.out.println("Save Edit upduserid " + prodSchBean.getUpdUsrId());
            Session session1 = (Session) HibernateUtil.currentSession();
            Transaction tx= session1.beginTransaction();
            //Handling prodschedule lots table
            Criteria crit = session1.createCriteria(ProdScheduleLots.class);
            //System.err.println("prodSchId = " + this.prodSchBean.getProdSchId());
            //Retrieve all lots info
            crit.add(Expression.eq("prodSchId", this.prodSchBean.getProdSchId()));
            crit.addOrder(Order.asc("lot"));
            List retlot = crit.list();
            tx.commit();
            HibernateUtil.closeSession();
            
            
            //open a new session
            Session session = (Session) HibernateUtil.currentSession();
            tx = session.beginTransaction();
            session.update(this.prodSchBean);
            ProdScheduleLots dblot = null;
            ProdScheduleLots editlot = null;
            boolean isdeleted = false;
            Set st = lotsMap.entrySet();
            Iterator it = st.iterator();
            while( it.hasNext()){
                isdeleted = true;
                Map.Entry me = (Map.Entry)it.next();
                editlot = (ProdScheduleLots)me.getValue();
                if ( editlot.getLotId() != null) { //existing record, possible deleted
                    for ( int i = 0; i < retlot.size(); i++){
                        dblot = (ProdScheduleLots)retlot.get(i);
                        //System.err.println("check edit lot id = " + editlot.getLotId() + " dblotid = " + dblot.getLotId());
                        if (editlot.getLotId().equals(dblot.getLotId())){
                            //System.err.println("Remove from list lot id = " + editlot.getLotId());
                            retlot.remove(i); //remove from list if editlot have a copy. so that left behind is deleted by user
                            continue;//not delete, force break
                        }
                    }
                    //System.err.println("update lot id = " + editlot.getLotId() + " dblotid = " + dblot.getLotId());
                    session.update(editlot);
                }else {
                    editlot.setProdSchId(this.prodSchBean.getProdSchId());
                    //System.err.println("save lot id = " + editlot.getLotId() + " - " + editlot.getProdSchId());
                    session.save(editlot);//LotId is null, new records or can be existing record update
                }
            }
            
            //delete remaining in lotlist
            for ( int i = 0; i < retlot.size(); i++){
                dblot = (ProdScheduleLots)retlot.get(i);
                //System.err.println("delete lot id  = " + dblot.getLotId());
                session.delete(dblot);
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
    
    
    public String saveAdd() {
        System.out.println("Save Add prod sch bean");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            prodSchBean.setStatus("A");
            session.save(this.prodSchBean);
            
            if ( lotsMap != null) {
                ProdScheduleLots lotobj = null;
                Set st = lotsMap.entrySet();
                Iterator it = st.iterator();
                while( it.hasNext()){
                    Map.Entry me = (Map.Entry)it.next();
                    lotobj = (ProdScheduleLots)me.getValue();
                    lotobj.setProdSchId(this.prodSchBean.getProdSchId());
                    lotobj.setStatus("A");
                    session.save(lotobj);
                }
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
    
    
    public String edit(){
        return ("editprodschedule");
    }
    
    
//Delete selected sub ref number from list
    public String delLotsListItem() {
        try {
            for ( int i = 0; i < selectedLots.length; i++) {
                lotsMap.remove(selectedLots[i]);
            }
        }catch ( Exception e){
            e.printStackTrace();
        }finally {
            
        }
        return null;
    }
    
    
//add sub ref no into list
    public String onAddSubRefNo() {
        if ( this.prodLotBean.getLot() == null || this.prodLotBean.getLot().length() == 0){
            this.addLotStat = "Failed to add lot into list, Lot can not Empty";
        } else if (this.prodLotBean.getVesselDate() == null ){
            this.addLotStat = "Failed to add lot into list, Vessel date  required";
        } else {
            lotsMap.put(new String(this.prodLotBean.getLot()),
                    new  ProdScheduleLots(this.prodLotBean.getLot(), this.prodLotBean.getVesselDate()));
            this.addLotStat = null;
        }
        return null;
    }
    
    
//Validate ref no to ensure it exist in database
    public String onRefNoChange(){
        System.err.println("Ajax call onRefNoChange, refno = " + this.prodSchBean.getRefNo() + "]");
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            Criteria crit = session.createCriteria(Orders.class);
            crit.add(Expression.eq("orderId", this.prodSchBean.getRefNo()));
            List result = crit.list();
            if ( result.size() == 0 ) {
                status = "Record [" + this.prodSchBean.getRefNo() + "] does not exist!!!";
            } else {
                
                System.err.println("Ajax called ...");
                status = null;
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
        return (null); //Must return null, else Ajax would not work
    }
    
    public String getCheckStatus(){
        return this.status;
    }
    public void setCheckStatus(String str){
        this.status = str;
    }
    
    public boolean isStatusOk(){
        if ( status == null) {
            return true;
        } else {
            return false;
        }
    }
    
    public String search() {
        this.foxySessionData.setAction(LST);
        System.err.println("Calling search...");
        if ( this.foxyTable != null){
            foxyTable.setFirst(0);
        }
        return (null);
    }
    
    
    public DataModel getProdScheduleListModel() {
        int firstrow = this.foxyTable.getFirst();
        int pagesize = this.foxyTable.getRows();
        
        
        Number numofRec = null;
        //System.err.println("Calling getProdScheduleListModel [from " + firstrow + ", pgsize " + pagesize + "]");
        
        try {
            if (this.sessProdSchBean.getCcode() != null ) {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                //Get Total row count
                Criteria crit = session.createCriteria(ProdScheduleJoinLots.class);
                crit.setProjection(Projections.rowCount());
                crit.add(Expression.eq("ccode", this.sessProdSchBean.getCcode()));
                if ( this.sessProdSchBean.getSewStart() != null) {
                    crit.add(Expression.ge("sewStart", this.sessProdSchBean.getSewStart()));
                }
                if ( this.sessProdSchBean.getSewEnd() != null) {
                    crit.add(Expression.le("sewEnd", this.sessProdSchBean.getSewEnd()));
                }
                crit.addOrder(Order.asc("lineNo"));
                numofRec = ((Number) crit.uniqueResult());
                tx.commit();
                System.err.println("Total Number of records: [" + numofRec.intValue() + "]");
                numofRec = numofRec == null ? 0 : numofRec.intValue();
                
                
                
                
                tx = session.beginTransaction();
                //Select all data based on use condition
                crit = session.createCriteria(ProdScheduleJoinLots.class);
                crit.add(Expression.eq("ccode", this.sessProdSchBean.getCcode()));
                if ( this.sessProdSchBean.getSewStart() != null) {
                    crit.add(Expression.ge("sewStart", this.sessProdSchBean.getSewStart()));
                }
                if ( this.sessProdSchBean.getSewEnd() != null) {
                    crit.add(Expression.le("sewEnd", this.sessProdSchBean.getSewEnd()));
                }
                crit.addOrder(Order.asc("lineNo"));
                crit.setFirstResult(firstrow);
                crit.setMaxResults(pagesize);
                List ret = crit.list();
                tx.commit();
                
                if (prodSchListModel != null) {
                    prodSchListModel = null;
                }
                
                System.err.println("Query Results size = [" + ret.size() + "] recs [from " +
                        firstrow + ", pgsize " + pagesize + "]");
                prodSchListModel = (DataModel)new FoxyPagedDataModel(ret, numofRec.intValue(), pagesize);
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
        
        return prodSchListModel;
    }
    
    
    public String delete(){
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            
            //Deleting lots tables prodscheduleLots
            String  qstr  = new String("DELETE ProdScheduleLots p ");
            qstr = qstr.concat("WHERE p.prodSchId = :pprodSchId ");
            Query q = session.createQuery(qstr);
            q.setLong("pprodSchId", foxySessionData.getPageParameterLong());
            q.executeUpdate(); //Deleting prodscheduleLots table
            
            //Deleteing master table prodSchedule
            qstr  = new String("DELETE ProdSchedule p ");
            qstr = qstr.concat("WHERE p.prodSchId = :pprodSchId ");
            q = session.createQuery(qstr);
            q.setLong("pprodSchId", foxySessionData.getPageParameterLong());
            q.executeUpdate(); //Deleting prodschedule table
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
