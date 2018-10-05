/*
 * Parameter.java
 *
 * Created on June 30, 2006, 6:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.db;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author eric
 */
public class Parameter implements Auditable,Serializable {
    private Long id = null;
    private String category = null;
    private String code = null;
    private Integer sequence = null;
    private String shortDesc = null;
    private String description = null;
    private String status = null;
    private String delUsrId   = null;
    private Date   delTime   = null;
    private String updUsrId = null;
    private Date   updTime = null;
    private String insUsrId  = null;
    private Date   insTime   = null;
    
    /** Creates a new instance of Farameter */
    public Parameter() {
    }
    
    //PROPERTY:  id
    public Long getId(){
        return this.id;
    }
    public void setId(Long newValue) {
        this.id = newValue;
    }
    //PROPERTY: category
    public String getCategory(){
        return this.category;
    }
    public void setCategory(String newValue) {
        this.category = newValue;
    }
    //PROPERTY: code
    public String getCode(){
        return this.code;
    }
    public void setCode(String newValue) {
        this.code = newValue;
    }
    //PROPERTY: sequence
    public Integer getSequence(){
        return this.sequence;
    }
    public void setSequence(Integer newValue) {
        this.sequence = newValue;
    }
    //PROPERTY: shortDesc
    public String getShortDesc(){
        return this.shortDesc;
    }
    public void setShortDesc(String newValue) {
        this.shortDesc = newValue;
    }
    //PROPERTY: description
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
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
