/*
 * Category.java
 *
 * Created on August 6, 2006, 11:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.db;

import java.io.Serializable;
import java.util.Date;
import javax.faces.context.FacesContext;
import com.foxy.util.ListData;

/**
 *
 * @author hcting
 */
public class QuotaCats implements Auditable,Serializable {
    
    private Long qtaCatId = null;
    private Long qtaId = null;
    private String country = null;
    private String quota = null;
    private Long catId = null;
    private Double multiplier = null;
    private Integer parity = null;
    private String status = null;
    private String delUsrId  = null;
    private Date   delTime   = null;
    private String updUsrId = null;
    private Date   updTime = null;
    private String insUsrId  = null;
    private Date   insTime   = null;
    
    
    
    /** Creates a new instance of Category */
    public QuotaCats() {
    }
    
    public Long getQtaCatId() {
        return qtaCatId;
    }
    
    public void setQtaCatId(Long qtaCatId) {
        this.qtaCatId = qtaCatId;
    }
    
    public Long getQtaId() {
        return qtaId;
    }
    
    public void setQtaId(Long qtaId) {
        this.qtaId = qtaId;
    }
    
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getQuota() {
        return quota;
    }
    
    public void setQuota(String quota) {
        this.quota = quota;
    }
    
    public Long getCatId() {
        return catId;
    }
    
    public void setCatId(Long catId) {
        this.catId = catId;
    }
    
    public Double getMultiplier() {
        return multiplier;
    }
    
    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }
    
    public Integer getParity() {
        return parity;
    }
    
    public void setParity(Integer parity) {
        this.parity = parity;
    }
    
    
    public String getListDisplay(){
        FacesContext ctx = FacesContext.getCurrentInstance();
        ListData ld = (ListData)ctx.getApplication().getVariableResolver().resolveVariable(ctx, "listData");
        Category ct = (Category)ld.getCategory(this.catId, 1);
        return ("[" + ct.getCategory() + "] = ["  + this.multiplier + "] pcs");
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getDelTime() {
        return delTime;
    }
    
    public void setDelTime(Date delTime) {
        this.delTime = delTime;
    }
    
    public String getDelUsrId() {
        return delUsrId;
    }
    
    public void setDelUsrId(String delUsrId) {
        this.delUsrId = delUsrId;
    }
    
    public Date getInsTime() {
        return insTime;
    }
    
    public void setInsTime(Date insTime) {
        this.insTime = insTime;
    }
    
    public Date getUpdTime() {
        return updTime;
    }
    
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
    
    public String getInsUsrId() {
        return insUsrId;
    }
    
    public void setInsUsrId(String insUsrId) {
        this.insUsrId = insUsrId;
    }
    
    public String getUpdUsrId() {
        return updUsrId;
    }
    
    public void setUpdUsrId(String updUsrId) {
        this.updUsrId = updUsrId;
    }
}
