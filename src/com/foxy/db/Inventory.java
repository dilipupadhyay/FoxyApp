/*
 * Inventory.java
 *
 * Created on June 21, 2006, 3:29 PM
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
public class Inventory implements Auditable,Serializable {
    private Long id = null;
    private String supplier = null;
    private String invoice = null;
    private Date invoiceDate = null;
    private String invType = null;
    private String invCode = null;
    private Double quantity = null;
    private String unit = null;
    private Double value = null;
    private String currency = null;
    private Double baseValue = null;
    private Double unitCost = null;
    private String itemDesc = null;
    private String remarks = null;
    private Double forexrate = null;
    
    
    private String status = "A";
    private String delUsrId   = null;
    private Date   delTime   = null;
    private String updUsrId = null;
    private Date   updTime = null;
    private String insUsrId  = null;
    private Date   insTime   = null;
    
    /** Creates a new instance of Order */
    public Inventory() {
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
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSupplier() {
        return supplier;
    }
    
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    
    public String getInvoice() {
        return invoice;
    }
    
    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }
    
    public Date getInvoiceDate() {
        return invoiceDate;
    }
    
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
    
    public String getInvType() {
        return invType;
    }
    
    public void setInvType(String invType) {
        this.invType = invType;
    }
    
    public String getInvCode() {
        return invCode;
    }
    
    public void setInvCode(String invCode) {
        this.invCode = invCode;
    }
    
    public Double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public Double getValue() {
        return value;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    public Double getUnitCost() {
        return unitCost;
    }
    
    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Double getBaseValue() {
        return baseValue;
    }
    
    public void setBaseValue(Double baseValue) {
        this.baseValue = baseValue;
    }
    
    public String getItemDesc() {
        return itemDesc;
    }
    
    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Double getForexrate() {
        return forexrate;
    }
    
    public void setForexrate(Double forexrate) {
        this.forexrate = forexrate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
