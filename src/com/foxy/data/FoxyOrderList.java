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
public class FoxyOrderList implements Serializable {

    private String orderId = null;
    private String countryCode = null;
    private String countryName = null;
    private String companyNameShort = null;
    private String custCode = null;
    private String custName = null;
    private String custBrand = null;
    private String custBrandName = null;
    private String custDivision = null;
    private String custDivisionName = null;
    private String styleCode = null;
    private String season = null;
    private String seasonD = null;
    private String merchandiser = null;
    private String merchandiserName = null;
    private Date orderDate = null;
    private Double unitPrice = null;
    private String type = null;
    private String description = null;
    //private String fabric = null;
    private String remark = null;
    private Double qtyDzn = null;
    private Double qtyPcs = null;
    private Double horizontal = null;
    private Double vertical = null;
    private String imgFile = null;
    private String priceTerm = null;
    private String fabricMill = null;
    private String fabricYyDz = null;
    private String fabricPrice = null;
    private String cmt = null;
    private String wash = null;
    private String swash = null;
    private String gcost = null;
    private String quotaUom = null;
    private String uom = null;
    private String status = null;

    /**
     * Creates a new instance of Order
     */
    public FoxyOrderList() {
    }

    //PROPERTY: orderId
    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String newValue) {
        this.orderId = newValue;
    }
    //PROPERTY: countryCode

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String newValue) {
        this.countryCode = newValue;
    }
    //PROPERTY: countryName

    public String getCountryName() {
        return this.countryName;
    }

    public void setCountryName(String newValue) {
        this.countryName = newValue;
    }

    //PROPERTY: Company Name
    public String getCompanyNameShort() {
        return companyNameShort;
    }

    public void setCompanyNameShort(String companyNameShort) {
        this.companyNameShort = companyNameShort;
    }

    //PROPERTY: custCode
    public String getCustCode() {
        return this.custCode;
    }

    public void setCustCode(String newValue) {
        this.custCode = newValue;
    }
    //PROPERTY: custName

    public String getCustName() {
        return this.custName;
    }

    public void setCustName(String newValue) {
        this.custName = newValue;
    }
    //PROPERTY: custBrand

    public String getCustBrand() {
        return this.custBrand;
    }

    public void setCustBrand(String newValue) {
        this.custBrand = newValue;
    }
    //PROPERTY: custBrandName

    public String getCustBrandName() {
        return this.custBrandName;
    }

    public void setCustBrandName(String newValue) {
        this.custBrandName = newValue;
    }
    //PROPERTY: custDivision

    public String getCustDivision() {
        return this.custDivision;
    }

    public void setCustDivision(String newValue) {
        this.custDivision = newValue;
    }
    //PROPERTY: custDivisionName

    public String getCustDivisionName() {
        return this.custDivisionName;
    }

    public void setCustDivisionName(String newValue) {
        this.custDivisionName = newValue;
    }
    //PROPERTY: styleCode

    public String getStyleCode() {
        return this.styleCode;
    }

    public void setStyleCode(String newValue) {
        this.styleCode = newValue;
    }
    //PROPERTY: season

    public String getSeason() {
        return this.season;
    }

    public void setSeason(String newValue) {
        this.season = newValue;
    }
    //PROPERTY: seasonD

    public String getSeasonD() {
        return this.seasonD;
    }

    public void setSeasonD(String newValue) {
        this.seasonD = newValue;
    }
    //PROPERTY: merchandiser

    public String getMerchandiser() {
        return this.merchandiser;
    }

    public void setMerchandiser(String newValue) {
        this.merchandiser = newValue;
    }
    //PROPERTY: merchandiserName

    public String getMerchandiserName() {
        return this.merchandiserName;
    }

    public void setMerchandiserName(String newValue) {
        this.merchandiserName = newValue;
    }
    //PROPERTY: orderDate

    public Date getOrderDate() {
        return this.orderDate;
    }

    public void setOrderDate(Date newValue) {
        this.orderDate = newValue;
    }
    //PROPERTY: unitPrice

    public Double getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(Double newValue) {
        this.unitPrice = newValue;
    }
    //PROPERTY: type

    public String getType() {
        return this.type;
    }

    public void setType(String newValue) {
        this.type = newValue;
    }
    //PROPERTY: description

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String newValue) {
        this.description = newValue;
    }
    //PROPERTY: fabric
    //public String getFabric(){
    //    return this.fabric;
    //}
    //public void setFabric(String newValue) {
    //    this.fabric = newValue;
    //}
    //PROPERTY: remark

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String newValue) {
        this.remark = newValue;
    }
    //PROPERTY: qtyDzn

    public Double getQtyDzn() {
        return this.qtyDzn;
    }

    public void setQtyDzn(Double newValue) {
        this.qtyDzn = newValue;
    }
    //PROPERTY: qtyPcs

    public Double getQtyPcs() {
        return this.qtyPcs;
    }

    public void setQtyPcs(Double newValue) {
        this.qtyPcs = newValue;
    }
    //PROPERTY: horizontal

    public Double getHorizontal() {
        return this.qtyPcs;
    }

    public void setHorizontal(Double newValue) {
        this.qtyPcs = newValue;
    }
    //PROPERTY: vartical

    public Double getVertical() {
        return this.vertical;
    }

    public void setVertical(Double newValue) {
        this.vertical = newValue;
    }

    //PROPERTY: priceTerm
    public String getPriceTerm() {
        return this.priceTerm;
    }

    public void setPriceTerm(String newValue) {
        this.priceTerm = newValue;
    }
    //PROPERTY: fabricMill

    public String getFabricMill() {
        return this.fabricMill;
    }

    public void setFabricMill(String newValue) {
        this.fabricMill = newValue;
    }
    //PROPERTY: fabricYyDz

    public String getFabricYyDz() {
        return this.fabricYyDz;
    }

    public void setFabricYyDz(String newValue) {
        this.fabricYyDz = newValue;
    }
    //PROPERTY: fabricPrice

    public String getFabricPrice() {
        return this.fabricPrice;
    }

    public void setFabricPrice(String newValue) {
        this.fabricPrice = newValue;
    }
    //PROPERTY: cmt

    public String getCmt() {
        return this.cmt;
    }

    public void setCmt(String newValue) {
        this.cmt = newValue;
    }
    //PROPERTY: wash

    public String getWash() {
        return this.wash;
    }

    public void setWash(String newValue) {
        this.wash = newValue;
    }
    //PROPERTY: swash

    public String getSwash() {
        return this.swash;
    }

    public void setSwash(String newValue) {
        this.swash = newValue;
    }
    //PROPERTY: gcost

    public String getGcost() {
        return this.gcost;
    }

    public void setGcost(String newValue) {
        this.gcost = newValue;
    }
    //PROPERTY: quotaUom

    public String getQuotaUom() {
        return this.quotaUom;
    }

    public void setQuotaUom(String newValue) {
        this.quotaUom = newValue;
    }
    //PROPERTY: uom

    public String getUom() {
        return this.uom;
    }

    public void setUom(String newValue) {
        this.uom = newValue;
    }
    //PROPERTY: status

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String newValue) {
        this.status = newValue;
    }

    public String getImgFileLink() {
        if (imgFile == null) {
            return "Not Uploaded";
        }
        return imgFile;
    }

    public String getImgFile() {
        return imgFile;
    }

    public void setImgFile(String imgFile) {
        this.imgFile = imgFile;
    }
}
