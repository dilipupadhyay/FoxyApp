/*
 * FoxySampCheckListAddPage.java
 *
 * Created on July 3, 2006, 8:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.checklist.page;

import javax.faces.application.FacesMessage;
import com.foxy.db.HibernateUtil;
import com.foxy.db.ChkLstFinishRpt;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.foxy.page.Page;


/**
 *
 * @author hcting
 */
public class FoxyFinishRptAddPage extends Page {
    private static String MENU_CODE = new String("FOXY");
    private Integer ccode = null;
    private String refNo = null;
    private String buyer = null;
    private String merchant = null;
    private Integer totQty = null;
    private Integer cutQty = null;
    private Integer sewQty = null;
    private Integer washQty = null;
    private Integer packQty = null;
    private Integer exportQty = null;
    private Integer sampQty = null;
    private Integer logaQty = null;
    private Integer logbQty = null;
    
    
    /** Creates a new instance of FoxySampCheckListAddPage */
    public FoxyFinishRptAddPage() {
        super(new String("FinishRptAddForm"));
        try {
            this.isAuthorize(MENU_CODE);
            //System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
//PROPERTY: ccode
    public Integer getCcode() {
        return (Integer)this.ccode;
    }
    
    
    public void  setCcode(Integer newvalInteger) {
        this.ccode = newvalInteger;
    }
    
    
//PROPERTY: refNo
    public String getRefNo() {
        return (String)this.refNo;
    }
    
    
    public void  setRefNo(String newvalString) {
        this.refNo = newvalString;
    }
    
    
//PROPERTY: buyer
    public String getBuyer() {
        return (String)this.buyer;
    }
    
    
    public void  setBuyer(String newvalString) {
        this.buyer = newvalString;
    }
    
    
//PROPERTY: merchant
    public String getMerchant() {
        return (String)this.merchant;
    }
    
    
    public void  setMerchant(String newvalString) {
        this.merchant = newvalString;
    }
    
    
    
//PROPERTY: totQty
    public Integer getTotQty() {
        return (Integer)this.totQty;
    }
    
    
    public void  setTotQty(Integer newvalInteger) {
        this.totQty = newvalInteger;
    }
    
    
//PROPERTY: cutQty
    public Integer getCutQty() {
        return (Integer)this.cutQty;
    }
    
    
    public void  setCutQty(Integer newvalInteger) {
        this.cutQty = newvalInteger;
    }
    
    
//PROPERTY: sewQty
    public Integer getSewQty() {
        return (Integer)this.sewQty;
    }
    
    
    public void  setSewQty(Integer newvalInteger) {
        this.sewQty = newvalInteger;
    }
    
    
//PROPERTY: washQty
    public Integer getWashQty() {
        return (Integer)this.washQty;
    }
    
    
    public void  setWashQty(Integer newvalInteger) {
        this.washQty = newvalInteger;
    }
    
    
//PROPERTY: packQty
    public Integer getPackQty() {
        return (Integer)this.packQty;
    }
    
    
    public void  setPackQty(Integer newvalInteger) {
        this.packQty = newvalInteger;
    }
    
    
//PROPERTY: exportQty
    public Integer getExportQty() {
        return (Integer)this.exportQty;
    }
    
    
    public void  setExportQty(Integer newvalInteger) {
        this.exportQty = newvalInteger;
    }
    
    
    
//PROPERTY: sampQty
    public Integer getSampQty() {
        return (Integer)this.sampQty;
    }
    
    
    public void  setSampQty(Integer newvalInteger) {
        this.sampQty = newvalInteger;
    }
    
    
//PROPERTY: logaQty
    public Integer getLogaQty() {
        return (Integer)this.logaQty;
    }
    
    
    public void  setLogaQty(Integer newvalInteger) {
        this.logaQty = newvalInteger;
    }
    
    
//PROPERTY: logbQty
    public Integer getLogbQty() {
        return (Integer)this.logbQty;
    }
    
    
    public void  setLogbQty(Integer newvalInteger) {
        this.logbQty = newvalInteger;
    }
    
    
    
    
    
    public String add(){
        
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            
            //Create hibernate object
            ChkLstFinishRpt frpt = new ChkLstFinishRpt();
            frpt.setCcode(this.ccode);
            frpt.setRefNo(this.refNo);
            frpt.setBuyer(this.buyer);
            frpt.setMerchant(this.merchant);
            frpt.setTotQty(this.totQty);
            frpt.setCutQty(this.cutQty);
            frpt.setSewQty(this.sewQty);
            frpt.setWashQty(this.washQty);
            frpt.setPackQty(this.packQty);
            frpt.setExportQty(this.exportQty);
            frpt.setSampQty(this.sampQty);
            frpt.setLogaQty(this.logaQty);
            frpt.setLogbQty(this.logbQty);
            
            session.save(frpt);
            tx.commit();
        } catch (Exception e) {
            System.err.println("Failed coz excveption!!!!");
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            e.printStackTrace();
            return (null);
        } finally {
            HibernateUtil.closeSession();
            //return ("success");
        }
        
        return ("success");
        
    }
    
}
