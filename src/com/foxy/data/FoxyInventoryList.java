/*
 * FoxyOrderList.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.foxy.data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author eric
 */
public class FoxyInventoryList  implements Serializable {
    private Long  id = null;
    private Integer refIdInv = null;
    private Integer refIdInvM = null;
    private Integer refIdS = null;
    
    private String supplier = null;
    private String invoiceNumber = null;
    private Date invoiceDate = null;
    private String code = null;
    private Double value = null;
    private Double quantity = null;
    private String unit = null;
    private String type = null;
    private String invtoryCode = null;
    private Date insDate = null;
    
    /** Creates a new instance of Inventory List */
    public FoxyInventoryList() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getRefIdInv() {
        return refIdInv;
    }
    
    public void setRefIdInv(Integer refIdInv) {
        this.refIdInv = refIdInv;
    }
    
    public Integer getRefIdInvM() {
        return refIdInvM;
    }
    
    public void setRefIdInvM(Integer refIdInvM) {
        this.refIdInvM = refIdInvM;
    }
    
    public Integer getRefIdS() {
        return refIdS;
    }
    
    public void setRefIdS(Integer refIdS) {
        this.refIdS = refIdS;
    }
    
    public String getSupplier() {
        return supplier;
    }
    
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    
    public Date getInvoiceDate() {
        return invoiceDate;
    }
    
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getInvtoryCode() {
        return invtoryCode;
    }
    
    public void setInvtoryCode(String invtoryCode) {
        this.invtoryCode = invtoryCode;
    }
    
    public Double getValue() {
        return value;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    public Date getInsDate() {
        return insDate;
    }
    
    public void setInsDate(Date insDate) {
        this.insDate = insDate;
    }
    
}
