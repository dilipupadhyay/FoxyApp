/*
 * FoxyInvMovementPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import com.foxy.db.OrderSummary;
import javax.faces.application.FacesMessage;
import com.foxy.db.Inventory;
import com.foxy.db.InvMovement;
import com.foxy.db.HibernateUtil;
import com.foxy.util.FoxyPagedDataModel;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import com.foxy.data.InvMovementTable01;
import java.text.SimpleDateFormat;



/**
 *
 * @author hcting
 */
public class FoxyInvMovementPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private DataModel ListModel;
    private Inventory inventoryBean = null;
    private InvMovement invMovementBean = null;
    private String currentMode = "VIEW";
    private List  osList = null;
    private String orderId = null;
    private String sumRefId = null;
    private Double inputForexRate = null;
    private Double sgdForexRate = null;
    private String forexRateStr = null;
    
    
    
    /** Creates a new instance of Page */
    public FoxyInvMovementPage() {
        super(new String("InventoryMovementForm"));
        //System.out.println("Calling constructor now!!!!!!!!!!!!!!");
        try {
            //this.getInvMovementBean();
            this.isAuthorize(MENU_CODE);
            //System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public String gotolist(){
        return ("gotolist");
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public boolean isDozenApplicable(){
        if ( "LBS".equals(this.getInventoryBean().getUnit())){
            return false;
        }else{
            return true;
        }
    }
    
    public String getSumRefId() {
        InvMovement tmpObj = this.getInvMovementBean();
        if (  tmpObj != null ){
            Integer tmpInt = tmpObj.getSsRefId();
            if ( tmpInt != null){
                this.sumRefId = tmpInt.toString();
                //System.err.println("Current ssrefid2222222222222 = [" + this.sumRefId + "]");
            }
        }
        
        return sumRefId;
    }
    
    public void setSumRefId(String sumRefId) {
        this.sumRefId = sumRefId;
    }
    
    
    public String getForexRateStr() {
        if (this.inputForexRate == null ) {
            this.inputForexRate = super.getRawForexRate(this.getInventoryBean().getCurrency(), this.getInventoryBean().getInvoiceDate());
        }
        
        if (this.sgdForexRate == null ) {
            this.sgdForexRate = super.getRawForexRate("SGD", this.inventoryBean.getInvoiceDate());
        }
        
        this.forexRateStr = "Currency [ USD_TO_" + this.inventoryBean.getCurrency() +  " = " + this.inputForexRate + "] ";
        this.forexRateStr += "[USD_TO_SGD = " + this.sgdForexRate + "]";
        
        return forexRateStr;
    }
    
    
    //read only (no setter)
    public Inventory getInventoryBean() {
        //System.err.println("Getting Inventory4444 Movement with invRefId = " + foxySessionData.getPageParameterLong2());
        
        if ( this.inventoryBean == null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(Inventory.class);
                //System.err.println("Id = " + foxySessionData.getPageParameterLong());
                crit.add(Expression.eq("id", foxySessionData.getPageParameterLong()));
                List result = crit.list();
                //System.err.println("Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.inventoryBean = (Inventory)result.get(0);
                } else {
                    System.err.println("InvMov No Inventory with invRefId = " + foxySessionData.getPageParameterLong());
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
        return inventoryBean;
    }
    
    
    public InvMovement getInvMovementBean() {
        //System.err.println("Getting Inventory 222Movement with invRefId = " + foxySessionData.getPageParameterLong2());
        if ( this.invMovementBean == null){ //get from session bean if possible
            this.invMovementBean = (InvMovement)super.getSessionObject1(InvMovement.class);
        }
        
        if ( this.invMovementBean == null){
            this.invMovementBean = new InvMovement();
            //System.err.println("Getting  NEW EMPTY Inventory 33222Movement with invRefId = " + foxySessionData.getPageParameterLong2());
        }
        return this.invMovementBean;
    }
    
    public void setInvMovementBean(InvMovement invMovementBean) {
        this.invMovementBean = invMovementBean;
    }
    
    //get total value assigned so far for current inventory
    public Double getAssignedInputVal(){
        Double amt = new Double(0.0);
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            
            String qstr =  "select sum(quantity*unitcost) as asgnval from  invmovement ";
            qstr += " where invrefid = :pinvrefid AND invmrefid != :pinvmrefid ";
            
            SQLQuery q = session.createSQLQuery(qstr);
            
            q.setLong("pinvrefid",  this.inventoryBean.getId());
            q.setLong("pinvmrefid",  this.invMovementBean.getId());
            
            q.addScalar("asgnval", Hibernate.DOUBLE);
            
            List result = q.list();
            if ( result.size() > 0 ) {
                amt = (Double)result.get(0);
            } else {
                //System.err.println("InvMovement assigned yet for invrefid = " + this.inventoryBean.getId());
            }
            
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        return  amt;
    }
    
    
    public String saveAdd() {
        //System.out.println("Save Add ssRefId11   = [" + this.invMovementBean.getSsRefId() +"]" );
        //System.out.println("Save Add ssRefId2222 = [" + this.getSumRefId() +"]" );
        //System.out.println("Save Add ssRefI333 = [" + this.invMovementBean.getQuantity() +"]" );
        try {
            Double tmpd1 = null;
            tmpd1 = this.getAssignedInputVal();
            if ( tmpd1 != null){
                tmpd1 = this.inventoryBean.getValue() - tmpd1 - (this.invMovementBean.getQuantity() * this.invMovementBean.getUnitCost());
            }else{
                tmpd1 = this.inventoryBean.getValue() - (this.invMovementBean.getQuantity() * this.invMovementBean.getUnitCost());
            }
            
            tmpd1 = super.roundDouble(tmpd1,2);
            
            //System.err.println("Assigned Balance = [" + tmpd1 + "]");
            if ( tmpd1.doubleValue() <= -2.00 ){
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Inventory Amt [" + this.inventoryBean.getValue() +
                        "] Not sufficient, Current Balance [" + tmpd1 + "]!!!" );
                ctx.addMessage(null, fmsg);
                return null;
            }
            
            this.invMovementBean.setSsRefId(Integer.parseInt(this.getSumRefId()));
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            this.invMovementBean.setInvRefId(this.inventoryBean.getId());
            this.invMovementBean.setTtDate(new  Date());
            session.save(this.invMovementBean);
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
        //clear all recordssFs
        this.invMovementBean = null;
        super.setSessionObject1(null);
        this.orderId = null;
        this.sumRefId = null;
        this.osList = null;
        
        return null;
    }
    
    
    public String saveUpd() {
        try {
            Double tmpd1 = null;
            tmpd1 = this.getAssignedInputVal();
            if ( tmpd1 != null){
                tmpd1 = this.inventoryBean.getValue() - tmpd1 - (this.invMovementBean.getQuantity() * this.invMovementBean.getUnitCost());
            }else{
                tmpd1 = this.inventoryBean.getValue() - (this.invMovementBean.getQuantity() * this.invMovementBean.getUnitCost());
            }
            
            tmpd1 = super.roundDouble(tmpd1,2);
            
            System.err.println("Assigned Balance = [" + tmpd1 + "]");
            if ( tmpd1.doubleValue() <= -2.00 ){
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Inventory Amt [" + this.inventoryBean.getValue() +
                        "] Not sufficient, Current Balance [" + tmpd1 + "]!!!" );
                ctx.addMessage(null, fmsg);
                return null;
            }
            
            
            
            this.invMovementBean.setSsRefId(Integer.parseInt(this.sumRefId));
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            session.update(this.invMovementBean);
            
            tx.commit();
            HibernateUtil.closeSession();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            e.printStackTrace();
        }finally {
            HibernateUtil.closeSession();
        }
        //clear all recordssFs
        this.invMovementBean = null;
        super.setSessionObject1(null);
        this.orderId = null;
        this.sumRefId = null;
        this.osList = null;
        
        return null;
    }
    
    
    public String updateAssignment(){
        this.currentMode = "UPDASSIGNMENT";
        
        //System.err.println("Getting Inventory 888Movement with invRefId = " + foxySessionData.getPageParameterLong2());
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            Criteria crit = session.createCriteria(InvMovement.class);
            //System.err.println("Id = " + foxySessionData.getPageParameterLong());
            crit.add(Expression.eq("id", foxySessionData.getPageParameterLong2()));
            List result = crit.list();
            //System.err.println("Result size = " + result.size());
            if ( result.size() > 0 ) {
                this.invMovementBean = (InvMovement)result.get(0);
            } else {
                System.err.println("No Inventory Movement with invRefId = " + foxySessionData.getPageParameterLong2());
            }
            crit = session.createCriteria(OrderSummary.class);
            crit.add(Expression.eq("id", this.invMovementBean.getSsRefId()));
            if ( crit.list().size() > 0 ){
                OrderSummary obj = (OrderSummary)crit.list().get(0);
                this.orderId = obj.getOrderId();
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
        super.setSessionObject1(this.invMovementBean);
        return null;
    }
    
    
    public String AddAssignment(){
        //System.err.println("Assigning!!!!!!!!!!! Inventory 888Movement with invRefId = " + foxySessionData.getPageParameterLong2());
        super.setSessionObject1(null); //force to recreate an empty object
        this.invMovementBean = null; //reset to null so getInvMovementBean will recreate a new bean
        this.currentMode = "ADDASSIGNMENT";
        return null;
    }
    
    public boolean isUpdAssg(){
        if ( "UPDASSIGNMENT".equals(this.currentMode)){
            return true;
        }else{
            return false;
        }
    }
    
    
    public boolean isAddAssg(){
        if ( "ADDASSIGNMENT".equals(this.currentMode)){
            return true;
        }else{
            return false;
        }
    }
    
    
    public DataModel getInventoryDetail() {
        Long invMovId = foxySessionData.getPageParameterLong();
        Number numofRec = null;
        int firstrow = this.foxyTable.getFirst();
        int pagesize = this.foxyTable.getRows();
        
        //System.err.println("Query list for [" + foxySessionData.getPageParameterLong() + "]");
        
        if ( invMovId == null){
            System.err.println("Cur invMovId is null !!!!!");
            return null;
        }
        
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            
            String qstr =  "SELECT a.invmrefid as invmrefid, os.orderid as refno, CONCAT(os.month, os.location) as lot, ";
            qstr += " os.delivery as tdate, a.quantity as qty, a.unitcost as unitcost, a.usedqty as uqty, ";
            qstr += " a.itemdesc as itemdesc, ";
            qstr += " a.unitcost*a.quantity as inputval, ";
            qstr += " inv.forexrate as forexrate, par.shortdesc as origin ";
            qstr += " FROM  invmovement as a ";
            qstr += " LEFT JOIN inventory as inv ON inv.invrefid = a.invrefid ";
            qstr += " LEFT JOIN ordsummary as os ON os.srefid = a.srefid ";
            qstr += " LEFT JOIN factorymast fm ON fm.factorycode = os.mainfactory ";
            qstr += " LEFT JOIN parameter as par ON par.category = 'CNTY' AND par.code = fm.countrycode ";
            qstr += " WHERE a.invrefid  = :pinvrefid ";
            qstr += " ORDER BY refno, lot ";
            
            SQLQuery q = session.createSQLQuery(qstr);
            
            q.setLong("pinvrefid",  invMovId);
            
            q.addScalar("invmrefid", Hibernate.LONG);
            q.addScalar("refno", Hibernate.STRING);
            q.addScalar("lot", Hibernate.STRING);
            q.addScalar("tdate", Hibernate.DATE);
            q.addScalar("qty", Hibernate.DOUBLE);
            q.addScalar("unitcost", Hibernate.DOUBLE);
            q.addScalar("uqty", Hibernate.DOUBLE);
            q.addScalar("itemdesc", Hibernate.STRING);
            q.addScalar("inputval", Hibernate.DOUBLE);
            q.addScalar("forexrate", Hibernate.DOUBLE);
            q.addScalar("origin", Hibernate.STRING);
            
            
            
            //custOderList = q.list();
            Iterator it = q.list().iterator();
            numofRec = q.list().size();
            InvMovementTable01 total = new InvMovementTable01();
            Double dtmp = null;
            Double dtmp2 = null;
            
            while (it.hasNext()){
                Object[] tmpRow = (Object[])it.next();
                int i = 0;
                InvMovementTable01 obj = new InvMovementTable01();
                
                try {
                    obj.setInvmrefid((Long)tmpRow[i++]);
                    obj.setRefNo((String)tmpRow[i++]);
                    obj.setLotid((String)tmpRow[i++]);
                    obj.setTtDate((Date)tmpRow[i++]);
                    dtmp = (Double)tmpRow[i++];
                    obj.setQty(dtmp);
                    total.AccQty(dtmp);
                    
                    obj.setUnitcost((Double)tmpRow[i++]);
                    obj.setUsedQty((Double)tmpRow[i++]);
                    obj.setDesc((String)tmpRow[i++]);
                    dtmp  = super.roundDouble((Double)tmpRow[i++],2);//input value
                    dtmp2 = (Double)tmpRow[i++];//forexrate
                    obj.setInputValue(dtmp);
                    obj.setSgdValue(super.roundDouble(dtmp*dtmp2,2));
                    obj.setOrigin((String)tmpRow[i++]);
                    
                    total.AccInputValue(dtmp);
                    total.AccSgdValue(obj.getSgdValue());
                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    this.tableList.add(obj);
                }
            }
            
            tx.commit();
            
            if ( this.tableList.size() <= 0){
                InvMovementTable01 tmpobj = new InvMovementTable01();
                this.tableList.add(tmpobj);
            }else{
                this.tableList.add(total);
            }
            
            if ( ListModel != null ){
                ListModel = null;
            }
            numofRec = numofRec == null ? 0 : numofRec.intValue();
            ListModel = (DataModel)new FoxyPagedDataModel(this.tableList, numofRec.intValue(), pagesize);
            //ListModel.setWrappedData(this.tableList);
            //ListModel = (DataModel)new FoxyPagedDataModel(this.tableList);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        return ListModel;
    }
    
    
    public List getOsList() {
        if ( this.osList == null){
            this.osList = new ArrayList();
            if ( this.orderId != null ) { //Items type, use it to get all relavent supplier list
                List resultList = null;
                try {
                    
                    SQLQuery q = null;
                    Session session = (Session) HibernateUtil.currentSession();
                    Transaction tx= session.beginTransaction();
                    String qstr = "SELECT os.srefid as id, os.month as month, os.location as location, ";
                    qstr += " cat.category as category, par2.shortdesc as origin, ";
                    qstr += " par1.shortdesc as destination, os.delivery as delivery ";
                    qstr += " FROM ordsummary as os ";
                    qstr += " LEFT JOIN factorymast fm ON fm.factorycode = os.mainfactory ";
                    qstr += " LEFT JOIN category   as cat on cat.catid = os.catid ";
                    qstr += " LEFT JOIN parameter  as par1 on par1.code  = os.destination AND par1.category = 'DEST' ";
                    qstr += " LEFT JOIN parameter  as par2 on par2.code  = fm.countrycode AND par2.category = 'CNTY' ";
                    qstr += " WHERE orderid = :porderid ";
                    q = session.createSQLQuery(qstr);
                    //set SQL parameter
                    q.setString("porderid", this.orderId);
                    
                    //Define attribute datatype binding
                    q.addScalar("id", Hibernate.STRING);
                    q.addScalar("month", Hibernate.STRING);
                    q.addScalar("location", Hibernate.STRING);
                    q.addScalar("category", Hibernate.STRING);
                    q.addScalar("origin", Hibernate.STRING);
                    q.addScalar("destination", Hibernate.STRING);
                    q.addScalar("delivery", Hibernate.DATE);
                    Iterator it = q.list().iterator();
                    if ( it.hasNext() ){
                        this.osList.add(new SelectItem(new String(""), new String("Lot_____Cat_______Origin____Dest______Del(DDMMYY)"))); //Always add a null items, event no records
                    }else {
                        this.osList.add(new SelectItem(new String(""), new String("Empty"))); //Always add a null items, event no records
                    }
                    
                    String itemKey = null;
                    String itemDesc = "";
                    String tmpstr = null;
                    Date tmpdate = null;
                    int tmpcount = 0;
                    SimpleDateFormat fmd1 = new SimpleDateFormat("ddMMyy");
                    
                    while (it.hasNext()){
                        int idx = 0;
                        itemDesc = "";
                        Object[] tmpRow = (Object[])it.next();
                        itemKey  =  (String)tmpRow[idx++]; //id
                        
                        tmpstr   =  (String)tmpRow[idx++]; //month
                        if ( tmpstr != null )  itemDesc +=  tmpstr;
                        
                        tmpstr   =  (String)tmpRow[idx++]; //location
                        if ( tmpstr != null )  itemDesc +=  tmpstr;
                        
                        tmpcount = 8 - itemDesc.length();
                        for ( int j = 0; j < tmpcount; j++ ){
                            itemDesc += "_";
                        }
                        
                        tmpstr = (String)tmpRow[idx++]; //category
                        if (tmpstr == null) tmpstr = "UNKNOWN";
                        tmpcount = 10 - tmpstr.length();
                        for ( int j = 0; j < tmpcount; j++ ){
                            tmpstr += "_";
                        }
                        itemDesc += " " + tmpstr;
                        
                        itemDesc += " " + (String)tmpRow[idx++]; //origin
                        itemDesc += "-->";
                        itemDesc += (String)tmpRow[idx++]; //destination
                        
                        tmpdate = (Date)tmpRow[idx++];//Delivery date
                        if ( tmpdate == null){
                            itemDesc += " _??????";
                        }else{
                            itemDesc += " _" + fmd1.format(tmpdate);
                        }
                        this.osList.add(new SelectItem(itemKey, itemDesc)); //add in current items
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
                
            }else {
                this.osList.add(new SelectItem(new String(""), new String("Empty"))); //Always add a null items, event no records
            }
        }
        return osList;
    }
    
    
    public String delete(){
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            String  qstr  = new String("DELETE InvMovement t ");
            qstr = qstr.concat("WHERE t.id  = :pinvMovId ");
            Query q = session.createQuery(qstr);
            q.setLong("pinvMovId", foxySessionData.getPageParameterLong2());
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
