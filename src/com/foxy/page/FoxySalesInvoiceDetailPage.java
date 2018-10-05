/*
 * FoxySalesInvoiceDetailPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import com.foxy.db.OrderSummary;
import javax.faces.application.FacesMessage;
import com.foxy.db.SalesInvoice;
import com.foxy.db.SalesInvoiceDetail;
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
import com.foxy.data.SalesInvoiceDetailTable01;
import java.text.SimpleDateFormat;


/**
 *
 * @author hcting
 */
public class FoxySalesInvoiceDetailPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private DataModel ListModel;
    private SalesInvoice salesInvoiceBean = null;
    private SalesInvoiceDetail salesInvoiceDetailBean = null;
    private String currentMode = "VIEW";
    private List  osList = null;
    private String orderId = null;
    private String sumRefId = null;
    private Double inputForexRate = null;
    private Double sgdForexRate = null;
    private String forexRateStr = null;
    
    
    /** Creates a new instance of Page */
    public FoxySalesInvoiceDetailPage() {
        super(new String("SalesInvoiceDetailForm"));
        //System.out.println("Calling constructor now!!!!!!!!!!!!!!");
        try {
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
    
    public String getSumRefId() {
        SalesInvoiceDetail tmpObj = this.getSalesInvoiceDetailBean();
        if (  tmpObj != null ){
            Integer tmpInt = tmpObj.getSrefid();
            if ( tmpInt != null){
                this.sumRefId = tmpInt.toString();
                //System.err.println("Current ssrefid2222222222222 = [" + this.sumRefId + "]");
            }
        }
        
        return sumRefId;
    }
    
    public void setSumRefId(String sumRefId) {
        //System.err.println("Current ssrefid33333333 = [" + this.sumRefId + "]");
        this.sumRefId = sumRefId;
    }
    
    
    public String getForexRateStr() {
        if (this.inputForexRate == null ) {
            this.inputForexRate = super.getRawForexRate(this.getSalesInvoiceBean().getCurrency(), this.getSalesInvoiceBean().getInvdate());
        }
        
        if (this.sgdForexRate == null ) {
            this.sgdForexRate = super.getRawForexRate("SGD", this.salesInvoiceBean.getInvdate());
        }
        
        
        this.forexRateStr = "Currency [ USD_TO_" + this.salesInvoiceBean.getCurrency() +  " = " + this.inputForexRate + "] ";
        this.forexRateStr += "[USD_TO_SGD = " + this.sgdForexRate + "]";
        
        return forexRateStr;
    }
    
    
    
    //read only (no setter)
    public SalesInvoice getSalesInvoiceBean() {
        //System.err.println("Getting Inventory4444 Movement with invRefId = " + foxySessionData.getPageParameterLong2());
        
        if ( this.salesInvoiceBean == null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                Criteria crit = session.createCriteria(SalesInvoice.class);
                //System.err.println("Id = " + foxySessionData.getPageParameterLong());
                crit.add(Expression.eq("id", foxySessionData.getPageParameterLong()));
                List result = crit.list();
                //System.err.println("Result size = " + result.size());
                if ( result.size() > 0 ) {
                    this.salesInvoiceBean = (SalesInvoice)result.get(0);
                } else {
                    System.err.println("Sales Inv No Inventory with invRefId = " + foxySessionData.getPageParameterLong());
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
        return salesInvoiceBean;
    }
    
    
    public SalesInvoiceDetail getSalesInvoiceDetailBean() {
        //System.err.println("Getting Inventory 222Movement with invRefId = " + foxySessionData.getPageParameterLong2());
        if ( this.salesInvoiceDetailBean == null){ //get from session bean if possible
            this.salesInvoiceDetailBean = (SalesInvoiceDetail)super.getSessionObject1(SalesInvoiceDetail.class);
        }
        
        if ( this.salesInvoiceDetailBean == null){
            this.salesInvoiceDetailBean = new SalesInvoiceDetail();
            if ( getSalesInvoiceBean() != null ){
                this.salesInvoiceDetailBean.setFobval(this.salesInvoiceBean.getFobval());
                this.salesInvoiceDetailBean.setCmtval(this.salesInvoiceBean.getCmtval());
                this.salesInvoiceDetailBean.setRevenue(this.salesInvoiceBean.getRevenue());
            }
            //System.err.println("Getting  NEW EMPTY Inventory 33222Movement with invRefId = " + foxySessionData.getPageParameterLong2());
        }
        return this.salesInvoiceDetailBean;
    }
    
    public void setSalesInvoiceDetailBean(SalesInvoiceDetail salesInvoiceDetailBean) {
        this.salesInvoiceDetailBean = salesInvoiceDetailBean;
    }
    
    
    public String saveAdd() {
        try {
            //System.err.println("Current SRefid = [" + this.getSumRefId() + "]");
            this.salesInvoiceDetailBean.setSrefid(Integer.parseInt(this.getSumRefId()));
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            this.salesInvoiceDetailBean.setSaleinvid(this.salesInvoiceBean.getSaleinvid());
            
            //this.salesInvoiceDetailBean.setCmtuprc(new Double(1.11));
            //this.salesInvoiceDetailBean.setCmtval(new Double(2.22));
            
            //this.salesInvoiceDetailBean.setFobval(new Double(3.33));
            //this.salesInvoiceDetailBean.setQtypcs(new Double(1000));
            //this.salesInvoiceDetailBean.setRevenue(new Double(2000));
            //this.salesInvoiceDetailBean.setPaydate(new Date());
            //this.salesInvoiceDetailBean.setPonumber("POPOPOPO");
            
            
            
            
            session.save(this.salesInvoiceDetailBean);
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
        //clear all recordssFs
        this.salesInvoiceDetailBean = null;
        super.setSessionObject1(null);
        this.orderId = null;
        this.sumRefId = null;
        this.osList = null;
        
        return null;
    }
    
    
    public String saveUpd() {
        //System.err.println("!!!!!!!!!!!!!!!!!!!Save Upd");
        //System.err.println("Current Mode = " + this.currentMode);
        try {
            //System.err.println("!!!!!!!!!!! -Current SumRefId = " + this.sumRefId);
            this.salesInvoiceDetailBean.setSrefid(Integer.parseInt(this.sumRefId));
            Session session = (Session) HibernateUtil.currentSession();
            //System.err.println("Save upde11 = [" +  this.salesInvoiceDetailBean.getInvRefId() + "]");
            //System.err.println("Save upde22 = [" +  this.salesInvoiceDetailBean.getId() + "]");
            //System.err.println("Save upde33 = [" +  this.salesInvoiceDetailBean.getSsRefId() + "]");
            //System.err.println("Session invRefId = " + foxySessionData.getPageParameterLong2());
            Transaction tx= session.beginTransaction();
            session.update(this.salesInvoiceDetailBean);
            
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
        this.salesInvoiceDetailBean = null;
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
            Criteria crit = session.createCriteria(SalesInvoiceDetail.class);
            //System.err.println("Id = " + foxySessionData.getPageParameterLong());
            crit.add(Expression.eq("id", foxySessionData.getPageParameterLong2()));
            List result = crit.list();
            //System.err.println("Result size = " + result.size());
            if ( result.size() > 0 ) {
                this.salesInvoiceDetailBean = (SalesInvoiceDetail)result.get(0);
            } else {
                System.err.println("No Inventory Movement with invRefId = " + foxySessionData.getPageParameterLong2());
            }
            crit = session.createCriteria(OrderSummary.class);
            crit.add(Expression.eq("id", this.salesInvoiceDetailBean.getSrefid()));
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
        } catch (Exception e){
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
        }finally {
            HibernateUtil.closeSession();
        }
        super.setSessionObject1(this.salesInvoiceDetailBean);
        return null;
    }
    
    
    public String AddAssignment(){
        //System.err.println("Assigning!!!!!!!!!!! Inventory 888Movement with invRefId = " + foxySessionData.getPageParameterLong2());
        super.setSessionObject1(null); //force to recreate an empty object
        this.salesInvoiceDetailBean = null; //reset to null so getSalesInvoiceDetailBean will recreate a new bean
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
    
    
    public DataModel getSalesInvoiceDetail() {
        Long recId = foxySessionData.getPageParameterLong();
        Number numofRec = null;
        int firstrow = this.foxyTable.getFirst();
        int pagesize = this.foxyTable.getRows();
        
        //System.err.println("Query list for [" + foxySessionData.getPageParameterLong() + "]");
        
        if ( recId == null){
            System.err.println("Cur salesInvId is null !!!!!");
            return null;
        }
        
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            
            String qstr =  "SELECT a.saleinvdetailid as saleinvdetailid, os.orderid as refno, CONCAT(os.month, os.location) as lot, ";
            qstr += " salinv.forexrate as forexrate, ";
            qstr += " a.cmtval as cmtval, a.fobval as fobval, a.revenue as revenue, ";
            qstr += " a.cmtuprc as cmtuprc, a.paydate as paydate, a.qtypcs as qtypcs, a.qtyctns as qtyctns, ";
            qstr += " a.ponumber as ponumber, a.etd as etd ";
            qstr += " FROM  salesinvoicedetail as a ";
            qstr += " LEFT JOIN salesinvoice as salinv ON salinv.saleinvid = a.saleinvid ";
            qstr += " LEFT JOIN ordsummary as os ON os.srefid = a.srefid ";
            qstr += " WHERE a.saleinvid  = :psaleinvid ";
            qstr += " ORDER BY refno, lot ";
            
            SQLQuery q = session.createSQLQuery(qstr);
            
            q.setLong("psaleinvid",  recId);
            
            q.addScalar("saleinvdetailid", Hibernate.LONG);
            q.addScalar("refno", Hibernate.STRING);
            q.addScalar("lot", Hibernate.STRING);
            q.addScalar("forexrate", Hibernate.DOUBLE);
            q.addScalar("cmtval", Hibernate.DOUBLE);
            q.addScalar("fobval", Hibernate.DOUBLE);
            q.addScalar("revenue", Hibernate.DOUBLE);
            q.addScalar("cmtuprc", Hibernate.DOUBLE);
            q.addScalar("paydate", Hibernate.DATE);
            q.addScalar("qtypcs", Hibernate.DOUBLE);
            q.addScalar("qtyctns", Hibernate.INTEGER);
            q.addScalar("ponumber", Hibernate.STRING);
            q.addScalar("etd", Hibernate.DATE);
            
            
            
            
            //custOderList = q.list();
            Iterator it = q.list().iterator();
            numofRec = q.list().size();
            SalesInvoiceDetailTable01 total = new SalesInvoiceDetailTable01();
            Double dtmp = null;
            Double dtmp2 = null;
            Integer iTmp = null;
            Double tmpForexRate = null;
            
            while (it.hasNext()){
                Object[] tmpRow = (Object[])it.next();
                int i = 0;
                SalesInvoiceDetailTable01 obj = new SalesInvoiceDetailTable01();
                
                try {
                    obj.setSaleinvdetailid((Long)tmpRow[i++]);
                    obj.setRefno((String)tmpRow[i++]);
                    obj.setLotid((String)tmpRow[i++]);
                    
                    tmpForexRate  = (Double)tmpRow[i++]; //forexrate
                    
                    dtmp = (Double)tmpRow[i++];
                    obj.setCmtval(dtmp);
                    total.AccCmtval(dtmp);
                    dtmp2 = super.roundDouble(dtmp*tmpForexRate, 2); //Value in base value
                    obj.setCmtvalBase(dtmp2);
                    total.AccCmtvalBase(dtmp2);
                    
                    dtmp = (Double)tmpRow[i++];
                    obj.setFobval(dtmp);
                    total.AccFobval(dtmp);
                    dtmp2 = super.roundDouble(dtmp*tmpForexRate, 2); //Value in base value
                    obj.setFobvalBase(dtmp2);
                    total.AccFobvalBase(dtmp2);
                    
                    dtmp = (Double)tmpRow[i++];
                    obj.setRevenue(dtmp);
                    total.AccRevenue(dtmp);
                    dtmp2 = super.roundDouble(dtmp*tmpForexRate, 2); //Value in base value
                    obj.setRevenueBase(dtmp2);
                    total.AccRevenueBase(dtmp2);
                    
                    
                    obj.setCmtuprc((Double)tmpRow[i++]);
                    obj.setPaydate((Date)tmpRow[i++]);
                    dtmp = (Double)tmpRow[i++];
                    obj.setQtypcs(dtmp);
                    total.AccQtyPcs(dtmp);
                    
                    iTmp = (Integer)tmpRow[i++];
                    obj.setQtyctns(iTmp);
                    total.AccQtyctns(iTmp);
                    
                    obj.setPonumber((String)tmpRow[i++]);
                    obj.setEtd((Date)tmpRow[i++]);
                    
                    //obj.setSgdValue(super.roundDouble(dtmp*dtmp2,2));
                } catch (Exception e){
                    e.printStackTrace();
                }finally {
                    this.tableList.add(obj);
                }
            }
            
            tx.commit();
            
            if ( this.tableList.size() <= 0){
                SalesInvoiceDetailTable01 tmpobj = new SalesInvoiceDetailTable01();
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
                    qstr += " cat.category as category, fm.factoryname as factory, ";
                    qstr += " par1.shortdesc as destination, os.delivery as delivery ";
                    qstr += " FROM ordsummary as os ";
                    qstr += " LEFT JOIN factorymast fm ON fm.factorycode = os.mainfactory ";
                    qstr += " LEFT JOIN category   as cat on cat.catid = os.catid ";
                    qstr += " LEFT JOIN parameter  as par1 on par1.code  = os.destination AND par1.category = 'DEST' ";
                    qstr += " WHERE orderid = :porderid ";
                    qstr += " ORDER BY id, month, location ";
                    q = session.createSQLQuery(qstr);
                    //set SQL parameter
                    q.setString("porderid", this.orderId);
                    
                    //Define attribute datatype binding
                    q.addScalar("id", Hibernate.STRING);
                    q.addScalar("month", Hibernate.STRING);
                    q.addScalar("location", Hibernate.STRING);
                    q.addScalar("category", Hibernate.STRING);
                    q.addScalar("factory", Hibernate.STRING);
                    q.addScalar("destination", Hibernate.STRING);
                    q.addScalar("delivery", Hibernate.DATE);
                    
                    Iterator it = q.list().iterator();
                    if ( it.hasNext() ){
                        this.osList.add(new SelectItem(new String(""), new String("Lot_____Cat_______Factory___Dest______Del(DDMMYY)"))); //Always add a null items, event no records
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
                        
                        itemDesc += " " + (String)tmpRow[idx++]; //main factory
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
                } finally {
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
            String  qstr  = new String("DELETE SalesInvoiceDetail t ");
            qstr = qstr.concat("WHERE t.saleinvdetailid = :psaleinvdetailid ");
            Query q = session.createQuery(qstr);
            q.setLong("psaleinvdetailid", foxySessionData.getPageParameterLong2());
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
