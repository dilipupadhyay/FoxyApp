/*
 * ForexRate.java
 *
 * Created on August 6, 2006, 11:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.db;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author hcting
 */
public class ForexRate implements Auditable,Serializable {
    
    private Long forexRateId = null;
    private Long parentId = null;
    private String curCode = null;
    private Date rateDate = null;
    private Double perUsdRate = null;
    private String status = null;
    private String delUsrId  = null;
    private Date   delTime   = null;
    private String updUsrId = null;
    private Date   updTime = null;
    private String insUsrId  = null;
    private Date   insTime   = null;
    
    
    
    /** Creates a new instance of Category */
    public ForexRate() {
    }
    
    public Long getForexRateId() {
        return forexRateId;
    }
    
    public void setForexRateId(Long forexRateId) {
        this.forexRateId = forexRateId;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public String getRecDesc(){
        if ( this.parentId == 0L ){
            return "Manual";
        }else{
            return "Auto";
        }
    }
    
    public String getCurCode() {
        return curCode;
    }
    
    public void setCurCode(String curCode) {
        this.curCode = curCode;
    }
    
    public Date getRateDate() {
        return rateDate;
    }
    
    public void setRateDate(Date rateDate) {
        this.rateDate = rateDate;
    }
    
    public Double getPerUsdRate() {
        return perUsdRate;
    }
    
    public void setPerUsdRate(Double perUsdRate) {
        this.perUsdRate = perUsdRate;
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
    
    public String getDelUsrId() {
        return delUsrId;
    }
    
    public void setDelUsrId(String delUsrId) {
        this.delUsrId = delUsrId;
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
