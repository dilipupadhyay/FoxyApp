/*
 * Page.java
 *
 * Created on June 15, 2006, 8:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package com.foxy.page;

import com.foxy.bean.FoxySessionData;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIData;
import javax.faces.context.ExternalContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import org.apache.myfaces.custom.datascroller.ScrollerActionEvent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SQLQuery;
import com.foxy.db.HibernateUtil;
import java.util.Iterator;
import java.util.Date;



/**
 *
 * @author eric
 */
public class Page {
    public static final String INVALID_SESSION = "invalid_session";
    public static final String ADD = "ADD";
    public static final String DEL = "DELETE";
    public static final String UPD = "UPDATE";
    public static final String ENQ = "ENQUIRY";
    public static final String SCH = "SEARCH";
    public static final String LST = "LIST";
    public static final String ASS = "ASSIGN";
    
    protected DataModel foxyListModel;
    protected UIData foxyTable;
    
    protected FoxySessionData foxySessionData = null;
    protected String formName = null;
    protected FacesContext ctx = null;
    protected ExternalContext ectx = null;
    protected String action = null;
    private Double forexRate = null;
    private Double rawForexRate = null;
    private Integer callCounter = 0;    
    protected String searchKey = null;
    protected String searchType = null;
    protected List tableList = new ArrayList();
    
    /** Creates a new instance of Page */
    public Page(){
        try {
            this.ctx = FacesContext.getCurrentInstance();
            this.ectx = ctx.getExternalContext();
            //System.out.println(this.ectx.getRequest().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public Page(String form) {
        this.formName = form;
        try {
            this.ctx = FacesContext.getCurrentInstance();
            this.ectx = ctx.getExternalContext();
            this.foxySessionData = (FoxySessionData) getBean("foxySessionData");
            this.action = this.getAction();
            System.out.println("====Action==== : " + this.action);
            System.out.println(this.ectx.getRequest().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Action 3: " +                                                                this.action);
    }
    
    
    //Used to track multiple call
    public String printCallCounter(String str){
        System.out.println("printCallCounter - Bean[" + this.hashCode() + "] Called[" + str + "]  Times[" + ++callCounter + "]");
        return null;
    }
    
    
    /**
     *
     * Get action from jsp page
     *
     */
    
    public String getAction() {
        /* Action from menu hast the first priority */
        String act = (String) this.ectx.getRequestParameterMap().get("jscook_action");
        if(act != null && act.length() > 23) {
            this.foxySessionData.resetAll();
            if (act.substring(23).startsWith("go_new")) {
                if (act.endsWith("go_newOrder")) {
                    this.foxySessionData.setOrderId(null);
                }
                this.foxySessionData.setAction(ADD);
                return ("ADD");
            } else if (act.substring(23).startsWith("go_enq")) {
                this.foxySessionData.setAction(ENQ);
                return ("ENQUIRY");
            } else if (act.substring(23).startsWith("go_upd")) {
                this.foxySessionData.setAction(UPD);
                return ("UPDATE");
            } else if (act.substring(23).startsWith("go_del")) {
                this.foxySessionData.setAction(DEL);
                return ("DELETE");
            } else if (act.substring(23).startsWith("go_sch")) {
                this.foxySessionData.setAction(SCH);
                return ("SEARCH");
            } else if (act.substring(23).startsWith("go_lst")) {
                this.foxySessionData.setAction(LST);
                return ("LIST");
                
/*            } else if (this.ectx.getRequestParameterMap().containsKey("action")) {
                System.out.println(this.ectx.getRequestParameterMap().get("action").toString());
                return(this.ectx.getRequestParameterMap().get("action").toString());
            } else if (this.getAction().length() > 0){
                return(this.getAction());*/
            } else {
                return (this.action);
            }
        } else {
            /* If action if not from menu, check submited action */
            /* Parameter action has the second priority */
            if (this.ectx.getRequestParameterMap().containsKey("action")) {
                if (this.ectx.getRequestParameterMap().get("action").toString() != null &&
                        this.ectx.getRequestParameterMap().get("action").toString().length() > 0) {
                    System.out.println("[" + this.ectx.getRequestParameterMap().get("action").toString() + "]");
                    return(this.ectx.getRequestParameterMap().get("action").toString());
                }
            }
            
            /* Parameter action has the third priority */
            if (this.foxySessionData.getAction() != null) {
                return this.foxySessionData.getAction();
            } else {
                return null;
            }
            
            /* Form action hast the lowest priority */
            /*if(this.ectx.getRequestParameterMap().containsKey("Add"+ this.formName+ ":action")) {
                System.out.println(this.ectx.getRequestParameterMap().get("Add"+ this.formName+ ":action").toString());
                return(this.ectx.getRequestParameterMap().get("Add"+ this.formName+ ":action").toString());
            } else if (this.ectx.getRequestParameterMap().containsKey("Enq"+ this.formName+ ":action")) {
                System.out.println(this.ectx.getRequestParameterMap().get("Enq"+ this.formName+ ":action").toString());
                return(this.ectx.getRequestParameterMap().get("Enq"+ this.formName+ ":action").toString());
            } else if (this.ectx.getRequestParameterMap().containsKey("Search"+ this.formName+ ":action")) {
                System.out.println(this.ectx.getRequestParameterMap().get("Search"+ this.formName+ ":action").toString());
                return(this.ectx.getRequestParameterMap().get("Search"+ this.formName+ ":action").toString());
            } else if (this.ectx.getRequestParameterMap().containsKey("List"+ this.formName+ ":action")) {
                System.out.println(this.ectx.getRequestParameterMap().get("List"+ this.formName+ ":action").toString());
                return(this.ectx.getRequestParameterMap().get("List"+ this.formName+ ":action").toString());
            } else {
                System.out.println("NULL Action --- NULL Action --- NULL Action --- NULL Action ");*/
            /* Can't get action, return null to fall back to page default view */
            //return null;
            //}
        }
    }
    
    
    /**
     * return a rounded double
     *
     */
    public static Double roundDouble(Double d, int places) {
        if ( d == null){
            return null;
        }
        Double dd = Math.pow(10, (double) places);
        return Math.round(d * dd) / dd;
    }
    
    
    /**
     *
     * Returns "true" if the current user is associated with this screen
     *
     */
    public void isAuthorize(String menuCode) {
        if(this.ectx.isUserInRole(menuCode)) {
            //System.out.println("a");
        } else {
            try {
                this.ectx.redirect(ectx.getRequestContextPath() + "/app/noAccess.jsf");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
    /**
     *
     *  Get user id
     *
     */
    
    public String getUserId() {
        return(this.ectx.getRemoteUser());
    }
    
    
    protected void addInternalErrorMessage(FacesContext ctx) {
        FacesMessage errMsg =
                new FacesMessage(
                "Internal Error Occured.",
                "The error has been logged, "
                + "please try your request in a minute.");
        this.ctx.addMessage(null, errMsg);
    }
    
    
    
    /**
     *
     *  Scroller action handler
     *
     */
    
    public void scrollerAction(ActionEvent event) {
        ScrollerActionEvent scrollerEvent = (ScrollerActionEvent) event;
        this.ectx.log("scrollerAction: facet: "
                + scrollerEvent.getScrollerfacet()
                + ", pageindex: "
                + scrollerEvent.getPageIndex());
    }
    
    public String sessionCheck() {
        return (INVALID_SESSION);
    }
    
    
    /**
     *
     *  Action Checking Add
     *
     */
    public boolean isAdd(){
        if (this.foxySessionData.getAction() != null &&
                this.foxySessionData.getAction().equals(ADD)) {
            return (true);
        } else {
            return (false);
        }
    }
    
    /**
     *
     *  Action Checking Delete
     *
     */
    public boolean isDelete(){
        if (this.foxySessionData.getAction() != null &&
                this.foxySessionData.getAction().equals(DEL)) {
            return (true);
        } else {
            return (false);
        }
    }
    
    /**
     *
     *  Action Checking Update
     *
     */
    public boolean isUpdate(){
        if (this.foxySessionData.getAction() != null &&
                this.foxySessionData.getAction().equals(UPD)) {
            return (true);
        } else {
            return (false);
        }
    }
    
    
    /**
     *
     *  Action Checking Update / Add
     *
     */
    public boolean isUpdAdd() {
        if (this.foxySessionData.getAction() != null &&
                (this.foxySessionData.getAction().equals(ADD) ||
                this.foxySessionData.getAction().equals(UPD))) {
            return (true);
        } else {
            return (false);
        }
    }
    
    /**
     *
     *  Action Checking Enquiry
     *
     */
    public boolean isEnquiry(){
        if (this.foxySessionData.getAction() != null &&
                this.foxySessionData.getAction().equals(ENQ)) {
            return (true);
        } else {
            return (false);
        }
    }
    
    /**
     *
     *  Action Checking Assign Inventory
     *
     */
    public boolean isAssign(){
        if (this.foxySessionData.getAction() != null &&
                this.foxySessionData.getAction().equals(ASS)) {
            return (true);
        } else {
            return (false);
        }
    }
    
    /**
     *
     *  Action Checking Search
     *
     */
    public boolean isSearch(){
        if (this.foxySessionData.getAction() != null &&
                this.foxySessionData.getAction().equals(SCH)) {
            return (true);
        } else {
            return (false);
        }
    }
    
    /**
     *
     *  Action Checking Listing
     *
     */
    public boolean isList(){
        if (this.foxySessionData.getAction() != null &&
                this.foxySessionData.getAction().equals(LST)) {
            return (true);
        } else {
            return (false);
        }
    }
    
    //PROPERTY: searchKey
    public String getSearchKey(){
        this.searchKey = this.foxySessionData.getSearchKey();
        return this.searchKey;
    }
    
    public void setSearchKey(String newValue) {
        this.searchKey = newValue;
        foxySessionData.setSearchKey(this.searchKey);
    }
    
    //PROPERTY: searchType
    public String getSearchType(){
        this.searchType = this.foxySessionData.getSearchType();
        return this.searchType;
    }
    
    public void setSearchType(String newValue) {
        this.searchType = newValue;
        foxySessionData.setSearchType(this.searchType);
    }
    
    public Date getFromDate(){
        return this.foxySessionData.getFromDate();
    }
    
    public void setFromDate(Date d) {
        this.foxySessionData.setFromDate(d);
    }
    
    public Date getToDate() {
        return this.foxySessionData.getToDate();
    }
    
    public void setToDate(Date d){
        this.foxySessionData.setToDate(d);
    }
    
    //PROPERTY: searchList
    public List getListData() {
        return tableList;
    }
    
    
    //PROPERTY: foxyTable
    public UIData getFoxyTable(){
        return this.foxyTable;
    }
    
    //PROPERTY: foxyTable
    public void setFoxyTable(UIData newValue) {
        this.foxyTable = newValue;
    }
    
    public void resetForexRate(){
        this.forexRate = null;
    }
    
    public Double getForexRate(){
        return this.forexRate;
    }
    
    
    //read only property to retrieve to  SGD forex exchange for a currency at particular date
    public Double getForexRate(String currencycode, Date d) {
        if ( this.forexRate == null ){
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                String qstr = " SELECT b.perusdrate as usdrate, a.perusdrate as usd2sgd ";
                qstr += " FROM forexrate as a ";
                qstr += " LEFT JOIN forexrate as b ON b.curcode = :pcurcode and b.ratedate = a.ratedate ";
                qstr += " WHERE a.curcode = 'SGD' AND a.ratedate = :pratedate ";
                
                SQLQuery q = session.createSQLQuery(qstr);
                //System.err.println("Test 1111111122222333333");
                q.setString("pcurcode", currencycode);
                q.setDate("pratedate", d);
                
                //add scalar
                q.addScalar("usdrate", Hibernate.DOUBLE);
                q.addScalar("usd2sgd", Hibernate.DOUBLE);
                
                if ( q.list().size() > 0 ){
                    Iterator it = q.list().iterator();
                    Double d1 = null;
                    Double d2 = null;
                    try {
                        while (it.hasNext()){
                            int idx = 0;
                            Object[] tmpRow = (Object[])it.next();
                            d1 = (Double)tmpRow[idx++];
                            d2 = (Double)tmpRow[idx++];
                            if ( "USD".equals(currencycode) ){
                                d1 = new Double(1.0);
                            }
                            
                            if ( d1 != null && d2 != null){
                                this.forexRate = d2/d1; // SGD_RATE/CURCODE
                                //System.err.println("forex rate for [" + this.inventoryBean.getCurrency() + "] AND SGD  On [" + this.inventoryBean.getInvoiceDate() + "] = " + this.forexRate);
                            }else {
                                //System.err.println("NULL forex rate for [" + this.inventoryBean.getCurrency() + "] AND SGD  On [" + this.inventoryBean.getInvoiceDate() + "]");
                                this.forexRate = null;
                            }
                        }
                    } catch ( Exception e){
                        e.printStackTrace();
                        
                    } finally {
                    }
                }else {
                    //not found forex
                    System.err.println("No forex rate for [" + currencycode + "] On [" + d + "]");
                    this.forexRate = null;
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
                e.printStackTrace();
            }finally {
                HibernateUtil.closeSession();
            }
            
        }//if not null do not search db
        
        return this.forexRate;
    }
    
    
    //read only property to retrieve actual forex exchange for a currency at particular date
    public Double getRawForexRate(String currencycode, Date d) {
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            
            String qstr = " SELECT a.perusdrate as usdrate ";
            qstr += " FROM forexrate as a ";
            qstr += " WHERE a.curcode = :pcurcode and a.ratedate = :pratedate ";
            
            SQLQuery q = session.createSQLQuery(qstr);
            q.setString("pcurcode", currencycode);
            q.setDate("pratedate", d);
            
            //add scalar
            q.addScalar("usdrate", Hibernate.DOUBLE);
            
            if ( q.list().size() > 0 ){
                this.rawForexRate = (Double)q.list().get(0);
            }else {
                //not found forex
                if ("USD".equals(currencycode)){
                    this.rawForexRate = new Double(1.0);
                }else{
                    System.err.println("No forex rate for [" + currencycode + "] On [" + d + "]");
                    this.rawForexRate = null;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            e.printStackTrace();
        }finally {
            HibernateUtil.closeSession();
        }
        
        return this.rawForexRate;
    }
    
    
    
    //PROPERTY: sessionBean
    protected Object getBean(String name) {
        return this.ctx.getApplication().getVariableResolver().resolveVariable(this.ctx, name);
    }
    
    //Method to get request parameter based on key passed in
    protected String getReqParam(String key){
        if (this.ectx.getRequestParameterMap().containsKey(key)) {
            return this.ectx.getRequestParameterMap().get(key).toString();
        } else {
            return null;
        }
    }
    
    
    protected Object getSessionObject1(Class obclass){
        Object obj = null;
        try {
            obj =  this.foxySessionData.getSessObject1();
            if ( obj == null) {
                return null;
            } else if ( obj.getClass() != obclass){
                Class objClass = obj.getClass();
                //System.err.println("Existing session obj1 is [" + objClass.getName() + "] Not as requested [" + obclass.getName() + "]");
                return null;
            }else {
                //System.err.println("Get sessProdSchBean in session bean --OK");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj;
    }
    
    
    protected void setSessionObject1(Object obj){
        this.foxySessionData.setSessObject1(obj);
    }
    
}

