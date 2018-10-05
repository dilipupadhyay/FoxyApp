/*
 * FoxyShippingReportsPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.foxy.page;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.faces.application.FacesMessage;
import com.foxy.db.HibernateUtil;
import com.foxy.util.ListData;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author hcting
 */
public class FoxyShippingReportsPage extends Page implements Serializable {

    private static String MENU_CODE = new String("FOXY");
    private DataModel dataListModel;
    private String country = null;
    private String mainfactory = null;
    private Integer year = null;
    private Integer month = null;
    private String shipMode = null;
    private boolean sordByInvoice = false;
    private SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat fmt3 = new SimpleDateFormat("MMMM yyyy");

    public class ReportDataBean {

        private String orderId = null;
        private String month = null;
        private String location = null;
        private String subLocation = null;
        private String stylecode = null;
        private String custCode = null;
        private String custBrand = null;
        private String custDivision = null;
        private String poNumber = null;
        private Date etd = null;
        private String category = null;
        private String destination = null;
        private Double qtyPcs = null;
        private String lcNumber = null;
        private String invoice = null;
        private Double cmtUnitPrc = null;
        private Double fobUnitPrc = null;
        private String shipMode = null;
        private String visaNo = null;
        private String coCert = null;
        private String expRegNo = null;
        private Double costFreight = null;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getSubLocation() {
            return subLocation;
        }

        public void setSubLocation(String subLocation) {
            this.subLocation = subLocation;
        }

        public String getStylecode() {
            return stylecode;
        }

        public void setStylecode(String stylecode) {
            this.stylecode = stylecode;
        }

        public String getCustCode() {
            return custCode;
        }

        public void setCustCode(String custCode) {
            this.custCode = custCode;
        }

        public String getCustBrand() {
            return custBrand;
        }

        public void setCustBrand(String custBrand) {
            this.custBrand = custBrand;
        }

        public String getCustDivision() {
            return custDivision;
        }

        public void setCustDivision(String custDivision) {
            this.custDivision = custDivision;
        }

        public String getPoNumber() {
            return poNumber;
        }

        public void setPoNumber(String poNumber) {
            this.poNumber = poNumber;
        }

        public Date getEtd() {
            return etd;
        }

        public void setEtd(Date etd) {
            this.etd = etd;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public Double getQtyPcs() {
            return qtyPcs;
        }

        public void setQtyPcs(Double qtyPcs) {
            this.qtyPcs = qtyPcs;
        }

        public void accumulateQtyPcs(Double qtyPcs) {
            if (qtyPcs != null) {
                if (this.qtyPcs == null) {
                    this.qtyPcs = qtyPcs;
                } else {
                    this.qtyPcs += qtyPcs;
                }
            }
        }

        public String getLcNumber() {
            return lcNumber;
        }

        public void setLcNumber(String lcNumber) {
            this.lcNumber = lcNumber;
        }

        public String getInvoice() {
            return invoice;
        }

        public void setInvoice(String invoice) {
            this.invoice = invoice;
        }

        public Double getCmtUnitPrc() {
            return cmtUnitPrc;
        }

        public void setCmtUnitPrc(Double cmtUnitPrc) {
            this.cmtUnitPrc = cmtUnitPrc;
        }

        public Double getFobUnitPrc() {
            return fobUnitPrc;
        }

        public void setFobUnitPrc(Double fobUnitPrc) {
            this.fobUnitPrc = fobUnitPrc;
        }

        public String getShipMode() {
            return shipMode;
        }

        public void setShipMode(String shipMode) {
            this.shipMode = shipMode;
        }

        public String getVisaNo() {
            return visaNo;
        }

        public void setVisaNo(String visaNo) {
            this.visaNo = visaNo;
        }

        public String getCoCert() {
            return coCert;
        }

        public void setCoCert(String coCert) {
            this.coCert = coCert;
        }

        public String getExpRegNo() {
            return expRegNo;
        }

        public void setExpRegNo(String expRegNo) {
            this.expRegNo = expRegNo;
        }

        public Double getCostFreight() {
            return costFreight;
        }

        public void setCostFreight(Double costFreight) {
            this.costFreight = costFreight;
        }

        public void accumulateCostFreight(Double costFreight) {
            if (costFreight != null) {
                if (this.costFreight == null) {
                    this.costFreight = costFreight;
                } else {
                    this.costFreight += costFreight;
                }
            }
        }
    }; //Inner class end

    public FoxyShippingReportsPage() {
        super(new String("ProductionReportsForm"));

        try {
            this.isAuthorize(MENU_CODE);
            //System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        ListData ld = (ListData) getBean("listData");
        String str = " ";

        if (this.year != null && this.month != null) {
            str += "(" + this.getCountryName() + ") ";
            if (this.mainfactory != null) {
                if (this.mainfactory.equals("ALLSELECTED")) {
                    str = str + " Factory[ All Factories ] ";
                } else {
                    str = str + " Factory[" + ld.getFactoryNameShort(this.getMainfactory()) + "] ";
                }
            }
            str += "Month [" + fmt3.format(this.getFromDate()) + "] ";

        } else {
            str += "(" + this.getCountryName() + ") ";
            if (this.mainfactory != null) {
                if (this.mainfactory.equals("ALLSELECTED")) {
                    str = str + " Factory[ All Factories ] ";
                } else {
                    str = str + " Factory[" + ld.getFactoryNameShort(this.getMainfactory()) + "] ";
                }
            }
            str += "ETD Date From [" + fmt1.format(this.getFromDate()) + "] ";
            str += "To [" + fmt1.format(this.getToDate()) + "] ";
        }

        if (this.shipMode != "") {
            str += " Shipmode [" + this.shipMode + "] ";
        }

        str += "As At [" + fmt2.format(new Date()) + "] ";

        if (this.sordByInvoice) {
            str += " -Sort By Invoice";
        }

        return str;
    }

    public List getFactoryByCountryList() {
        ListData ld = (ListData) getBean("listData");
        return (ld.getFactoryListAllByCountry(this.country));
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryName() {
        ListData ld = (ListData) getBean("listData");
        return (ld.getCountryDesc(this.getCountry(), 0));
        //return this.country;
    }

    public String getMainfactory() {
        return mainfactory;
    }

    public void setMainfactory(String mainfactory) {
        this.mainfactory = mainfactory;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getShipMode() {
        return shipMode;
    }

    public void setShipMode(String shipMode) {
        this.shipMode = shipMode;
    }

    public boolean isSordByInvoice() {
        return sordByInvoice;
    }

    public void setSordByInvoice(boolean sordByInvoice) {
        this.sordByInvoice = sordByInvoice;
    }

    public String search() {
        this.foxySessionData.setAction(LST);

        if (this.year != null && this.month != null) {
            Calendar cal = Calendar.getInstance();
            // This is the right way to set the month
            cal.set(Calendar.YEAR, this.year);
            cal.set(Calendar.MONTH, (this.month - 1));
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));

            //this.fromDate = cal.getTime();
            this.setFromDate(cal.getTime());

            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            //this.toDate = cal.getTime();
            this.setToDate(cal.getTime());
        }

        return (null);
    }

    public DataModel getShippingReportData() {
        List<ReportDataBean> recordList = null;
        if (this.country != null) {
            try {
                //System.err.println("Search for records " +  this.country + "  " + this.quota + " " + this.getFromDate() + " " + this.getToDate());
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx = session.beginTransaction();

                String qstr = "SELECT od.orderid as orderid, os.month as month, os.location as location, ";
                qstr += "  oc.sublocation as sublocation, od.stylecode as stylecode, ";
                qstr += "  od.custcode as custcode, od.custbrand as custbrand, od.custdivision as custdivision, ";
                qstr += "  oc.ponumber as ponumber, sh.etd as etd, cat.category as category, par.shortdesc as destination, ";
                qstr += "  sh.qtypcs as qtypcs, sh.lcnumber as lcnumber, sh.invoice as invoice, ";
                qstr += "  sh.cmtunitprc as cmtunitprc, sh.fobunitprc as fobunitprc, sh.shipmode as shipmode, ";
                qstr += "  sh.visano as visano, sh.cocert as cocert, sh.expregno as expregno, sh.costfreight as costfreight ";
                qstr += "  FROM shipping as sh ";
                qstr += "  LEFT JOIN ordconfirm as oc on oc.crefid  = sh.crefid ";
                qstr += "  LEFT JOIN ordsummary as os on os.srefid = oc.srefid ";
                qstr += "  LEFT JOIN orders as od on od.orderid = oc.orderid ";
                qstr += "  LEFT JOIN category   as cat on cat.catid = os.catid ";
                qstr += "  LEFT JOIN parameter  as par on par.code  = os.destination AND par.category = 'DEST' ";
                qstr += "  LEFT JOIN factorymast fm ON fm.factorycode = os.mainfactory ";
                qstr += "  WHERE od.status != 'D' ";
                qstr += "  AND ( sh.etd >= :pdateStart AND  sh.etd <= :pdateEnd AND fm.countrycode = :porigin ) ";
                if (this.mainfactory != null && this.mainfactory.equals("ALLSELECTED") == false) {
                    qstr = qstr + " AND  fm.factorycode = :pfactory ";
                }
                if (this.shipMode != null && this.shipMode.length() > 0) {
                    qstr += "AND sh.shipmode = :pshipmode ";
                }

                if (this.sordByInvoice) {
                    qstr += " ORDER BY invoice";
                } else {
                    qstr += " ORDER BY orderid, month, location, sublocation";
                }

                //System.err.println("Test 1111111122222");
                SQLQuery q = session.createSQLQuery(qstr);
                //System.err.println("Test 1111111122222333333");
                q.setString("porigin", this.country);
                q.setDate("pdateStart", this.getFromDate());
                q.setDate("pdateEnd", this.getToDate());
                if (this.mainfactory != null && this.mainfactory.equals("ALLSELECTED") == false) {
                    q.setString("pfactory", this.mainfactory);
                }
                if (this.shipMode != null && this.shipMode.length() > 0) {
                    q.setString("pshipmode", this.shipMode);
                }

                //add scalar
                //line 1
                q.addScalar("orderid", Hibernate.STRING);
                q.addScalar("month", Hibernate.STRING);
                q.addScalar("location", Hibernate.STRING);
                //line 2
                q.addScalar("sublocation", Hibernate.STRING);
                q.addScalar("stylecode", Hibernate.STRING);
                //line 3
                q.addScalar("custcode", Hibernate.STRING);
                q.addScalar("custbrand", Hibernate.STRING);
                q.addScalar("custdivision", Hibernate.STRING);
                //line 4
                q.addScalar("ponumber", Hibernate.STRING);
                q.addScalar("etd", Hibernate.DATE);
                q.addScalar("category", Hibernate.STRING);
                q.addScalar("destination", Hibernate.STRING);
                //line 5
                q.addScalar("qtypcs", Hibernate.DOUBLE);
                q.addScalar("lcnumber", Hibernate.STRING);
                q.addScalar("invoice", Hibernate.STRING);
                //line 6
                q.addScalar("cmtunitprc", Hibernate.DOUBLE);
                q.addScalar("fobunitprc", Hibernate.DOUBLE);
                q.addScalar("shipmode", Hibernate.STRING);
                //line 7
                q.addScalar("visano", Hibernate.STRING);
                q.addScalar("cocert", Hibernate.STRING);
                q.addScalar("expregno", Hibernate.STRING);
                q.addScalar("costfreight", Hibernate.DOUBLE);

                //recordList = q.list();
                //System.err.println("Test 111111112222233333344444444");
                Iterator it = q.list().iterator();

                //System.err.println("Total records = " + q.list().size());

                if (recordList == null) {
                    recordList = new ArrayList();
                }

                //System.err.println("Test 11111");
                ReportDataBean grandTotal = new ReportDataBean();
                try {
                    grandTotal.setOrderId(new String("Grand Total:"));
                    grandTotal.setQtyPcs(new Double(0));
                    grandTotal.setCostFreight(new Double(0));
                    Double tmp = null;
                    while (it.hasNext()) {
                        int idx = 0;
                        Object[] tmpRow = (Object[]) it.next();
                        ReportDataBean obj = new ReportDataBean();
                        obj.setOrderId((String) tmpRow[idx++]);
                        obj.setMonth((String) tmpRow[idx++]);
                        obj.setLocation((String) tmpRow[idx++]);
                        obj.setSubLocation((String) tmpRow[idx++]);
                        obj.setStylecode((String) tmpRow[idx++]);
                        obj.setCustCode((String) tmpRow[idx++]);
                        obj.setCustBrand((String) tmpRow[idx++]);
                        obj.setCustDivision((String) tmpRow[idx++]);
                        obj.setPoNumber((String) tmpRow[idx++]);
                        obj.setEtd((Date) tmpRow[idx++]);
                        obj.setCategory((String) tmpRow[idx++]);
                        obj.setDestination((String) tmpRow[idx++]);
                        tmp = (Double) tmpRow[idx++];
                        obj.setQtyPcs(tmp);
                        grandTotal.accumulateQtyPcs(tmp);
                        obj.setLcNumber((String) tmpRow[idx++]);
                        obj.setInvoice((String) tmpRow[idx++]);
                        obj.setCmtUnitPrc((Double) tmpRow[idx++]);
                        obj.setFobUnitPrc((Double) tmpRow[idx++]);
                        obj.setShipMode((String) tmpRow[idx++]);
                        obj.setVisaNo((String) tmpRow[idx++]);
                        obj.setCoCert((String) tmpRow[idx++]);
                        obj.setExpRegNo((String) tmpRow[idx++]);
                        tmp = (Double) tmpRow[idx++];
                        obj.setCostFreight(tmp);
                        grandTotal.accumulateCostFreight(tmp);

                        recordList.add(obj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //System.err.println("Test 11111333");
                recordList.add(grandTotal);
                //System.err.println("Test 1111144444");
                if (dataListModel == null) {
                    //System.err.println("Search for records 333");
                    dataListModel = new ListDataModel();
                }

                //System.err.println("Test 111115555");
                dataListModel.setWrappedData(recordList);
                //System.err.println("Test 11111666");
                tx.commit();
            } catch (HibernateException e) {
                //do something here with the exception
                e.printStackTrace();
                FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            } catch (Exception e) {
                e.printStackTrace();
                FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            } finally {
                HibernateUtil.closeSession();
            }
        } else {
            System.err.println("No records ....!!! Search key is null");
        }
        //Avoid null pointer exception
        if (dataListModel == null) {
            System.err.println("No records ....!!!");
            dataListModel = new ListDataModel();
        }

        return dataListModel;
    }
}
