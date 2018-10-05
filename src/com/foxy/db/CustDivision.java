/*
 * Customer.java
 *
 * Created on June 21, 2006, 3:59 PM
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
public class CustDivision implements Auditable,Serializable {
    private Long id = null;
    private String custCode = null;
    private String brandCode = null;
    private String divCode = null;
    private String divDesc = null;
    private String remark = null;
    private String status = null;
    private String delUsrId   = null;
    private Date   delTime   = null;
    private String updUsrId = null;
    private Date   updTime = null;
    private String insUsrId  = null;
    private Date   insTime   = null;
    
    
    /** Creates a new instance of Customer */
    public CustDivision() {
    }
    
    //PROPERTY: id
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    //PROPERTY: custCode
    public String getCustCode(){
        return this.custCode;
    }
    public void setCustCode(String newValue) {
        this.custCode = newValue;
    }
    
    
    //PROPERTY: brandCode
    public String getBrandCode(){
        return this.brandCode;
    }
    
    public void setBrandCode(String newValue) {
        this.brandCode = newValue;
    }
    
    //PROPERTY: divCode
    public String getDivCode(){
        return this.divCode;
    }
    
    public void setDivCode(String newValue) {
        this.divCode = newValue;
    }
    
    //PROPERTY: divDesc
    public String getDivDesc(){
        return this.divDesc;
    }
    
    public void setDivDesc(String newValue) {
        this.divDesc = newValue;
    }
    
    
    //PROPERTY: remark
    public String getRemark(){
        return this.remark;
    }
    public void setRemark(String newValue) {
        this.remark = newValue;
    }
    
    //PROPERTY: status
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String newValue) {
        this.status = newValue;
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
