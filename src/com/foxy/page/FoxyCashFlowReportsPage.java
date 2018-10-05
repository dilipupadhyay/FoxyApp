/*
 * FoxyCashFlowReportsPage.java
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
public class FoxyCashFlowReportsPage extends Page implements Serializable {

    private static String MENU_CODE = new String("FOXY");
    private DataModel dataListModel;
    private String country = null;
    private String mainfactory = null;
    private Integer year = null;
    private Integer month = null;
    private SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat fmt3 = new SimpleDateFormat("MMMM yyyy");
    private Calendar cal = Calendar.getInstance();
    private Integer currentMonth = 0;
    private Integer totalweek = 0;
    private Integer numofdays_week1 = 0;   //exclude sun (working days only
    private Integer numofdays_lastweek = 0;  //exclude sun (working days only

    public class ReportDataBean {

        private String refNo = null;
        private String custCode = null;
        private Date delivery = null;
        private Date payDate = null;
        private Integer payTerm = null;
        private Double qtyPcs = null;
        private Double unitPrice = null;
        private Double totalValue[] = new Double[8];

        public String getRefNo() {
            return refNo;
        }

        public void setRefNo(String refNo) {
            this.refNo = refNo;
        }

        public String getCustCode() {
            return custCode;
        }

        public void setCustCode(String custCode) {
            this.custCode = custCode;
        }

        public Date getDelivery() {
            return delivery;
        }

        public void setDelivery(Date delivery) {
            this.delivery = delivery;
        }

        public Date getPayDate() {
            return payDate;
        }

        public void setPayDate(Date payDate) {
            this.payDate = payDate;
        }

        public Integer getPayTerm() {
            return payTerm;
        }

        public void setPayTerm(Integer payTerm) {
            this.payTerm = payTerm;
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

        public Double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(Double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public void setTotalValue(int index, Double totalValue) {
            this.totalValue[index] = totalValue;
        }

        public void accumulateTotalValue(int index, Double total) {
            if (total != null) {
                if (this.totalValue[index] == null) {
                    this.totalValue[index] = total;
                } else {
                    this.totalValue[index] += total;
                }
            }
        }

        public Double getTotalValue_1() {
            return totalValue[0];
        }

        public Double getTotalValue_2() {
            return totalValue[1];
        }

        public Double getTotalValue_3() {
            return totalValue[2];
        }

        public Double getTotalValue_4() {
            return totalValue[3];
        }

        public Double getTotalValue_5() {
            return totalValue[4];
        }

        public Double getTotalValue_6() {
            return totalValue[5];
        }

        public Double getTotalValue_7() {
            return totalValue[6];
        }

        public Double getTotalValue_8() {
            return totalValue[7];
        }
    }; //Inner class end

    public FoxyCashFlowReportsPage() {
        super(new String("CashFlowReportPage"));

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
            str += "Delivery From [" + fmt1.format(this.getFromDate()) + "] ";
            str += "To [" + fmt1.format(this.getToDate()) + "] ";
        }

        str += "As At [" + fmt2.format(new Date()) + "] ";

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

    public String search() {
        this.foxySessionData.setAction(LST);

        if (this.year != null && this.month != null) {
            // This is the right way to set the month
            cal.set(Calendar.YEAR, this.year);
            cal.set(Calendar.MONTH, (this.month - 1));
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            this.currentMonth = this.month - 1;

            this.setFromDate(cal.getTime());

            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            this.setToDate(cal.getTime());
        }

        return (null);
    }

    private void resetBeanVariable(Date curDate) {
        cal.setTime(curDate);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        this.numofdays_week1 = (Integer) (8 - cal.get(Calendar.DAY_OF_WEEK)); //Sunday not counted
        if (this.numofdays_week1 == 7) { //included sunday, minus one to exclude it
            this.numofdays_week1--;
        }

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        this.numofdays_lastweek = (Integer) (cal.get(Calendar.DAY_OF_WEEK) - 1);
        this.totalweek = cal.getActualMaximum(Calendar.WEEK_OF_MONTH); //Sunday not counted
    }

    //Method to decide how to move between
    private Integer finaliseWeekSlot(Integer curWeek, Integer curMonth) {
        Integer tmp = 0;
        if (this.totalweek <= 4) {
            tmp = curWeek;
        } else if (this.totalweek == 5) {
            if (this.numofdays_week1 <= this.numofdays_lastweek) {
                if (curWeek > 1) { //Move all second week onward one week ealier (-1  week)
                    tmp = curWeek - 1;
                } else {
                    tmp = curWeek; //first week no changes
                }
            } else { //only move forward one week ealier for last week only
                if (curWeek == 5) {
                    tmp = curWeek - 1;
                } else {
                    tmp = curWeek; //first 4 week no changes
                }
            }
        } else if (this.totalweek == 6) {
            //move week 2 to 5 one week ealier
            if (curWeek > 1) {
                tmp = curWeek - 1;
                if (curWeek == 6) {
                    tmp--; //move one more week ealier current date is week 6 (6 - 4 = 4!!)
                }
            } else {
                tmp = curWeek; //first week no changes
            }

        } else {
            System.err.println("Failed to decide week slot for cur week = [" + curWeek
                    + "], Total week = [" + this.totalweek + "] First week working days = [" + this.numofdays_week1
                    + "] last week working days = [" + this.numofdays_lastweek + "]");
            tmp = -1;
            return tmp; //force return
        }

        tmp += (curMonth - this.currentMonth + 1) * 4;
        if (tmp > 8) {//avoid index overflow
            tmp = -1;
        }
        return tmp;
    }

    public DataModel getOrdCashFlowForecastData() {
        List<ReportDataBean> recordList = null;
        if (this.country != null) {
            try {
                //System.err.println("Search for records " +  this.country + "  " + this.quota + " " + this.getFromDate() + " " + this.getToDate());
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx = session.beginTransaction();
                String qstr = "SELECT  CONCAT(a.orderid,  a.month, a.location) AS refno, b.custcode as custcode, ";
                qstr += " b.unitprice as unitprice, a.qtypcs as qtypcs, a.delivery as delivery, ";
                qstr += " ADDDATE(a.delivery, c.payterm) as paydate, c.payterm as payterm ";
                qstr += " FROM ordsummary a ";
                qstr += " LEFT JOIN orders b ON a.orderid = b.orderid ";
                qstr += " LEFT JOIN customer c ON c.custcode = b.custcode ";
                qstr += " LEFT JOIN factorymast fm ON fm.factorycode = a.mainfactory ";
                qstr += " WHERE fm.countrycode = :porigin AND a.delivery >= ADDDATE(:pdateStart, -c.payterm) ";
                qstr += " AND a.delivery <= ADDDATE(:pdateEnd, -c.payterm) ";
                qstr += " AND a.status = 'A' ";

                if (this.mainfactory != null && this.mainfactory.equals("ALLSELECTED") == false) {
                    qstr = qstr + " AND  fm.factorycode = :pfactory ";
                }

                qstr += " Order By paydate, refno ";
                //System.err.println("Test 1111111122222");
                SQLQuery q = session.createSQLQuery(qstr);
                //System.err.println("Test 1111111122222333333");
                q.setString("porigin", this.country);

                if (this.mainfactory != null && this.mainfactory.equals("ALLSELECTED") == false) {
                    q.setString("pfactory", this.mainfactory);
                }

                cal.setTime(this.getFromDate());//initialise calendar
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1); //reduce one month (previous month
                q.setDate("pdateStart", cal.getTime());
                q.setDate("pdateEnd", this.getToDate());

                //add scalar
                //line 1
                q.addScalar("refno", Hibernate.STRING);
                q.addScalar("custcode", Hibernate.STRING);
                q.addScalar("unitprice", Hibernate.DOUBLE);
                q.addScalar("qtypcs", Hibernate.DOUBLE);
                q.addScalar("payterm", Hibernate.INTEGER);
                q.addScalar("delivery", Hibernate.DATE);
                q.addScalar("paydate", Hibernate.DATE);


                //System.err.println("Test 111111112222233333344444444");
                Iterator it = q.list().iterator();

                //System.err.println("Total records = " + q.list().size());
                if (recordList == null) {
                    recordList = new ArrayList();
                }

                //System.err.println("Test 11111");
                ReportDataBean grandTotal = new ReportDataBean();
                ReportDataBean subTotal = new ReportDataBean();
                grandTotal.setRefNo("Grand Total");
                grandTotal.setCustCode(null);
                subTotal.setRefNo("Sub Total");
                subTotal.setCustCode(null);

                cal.setTime(this.getFromDate());
                try {
                    subTotal.setQtyPcs(new Double(0));
                    Double tmp = null;
                    Double tmp2 = null;
                    Double tmp3 = null;
                    Integer curweek = 0;
                    Integer curmonth = 0;
                    Integer curSlot = 0;
                    Integer prevrecmonth = 0;


                    Date tmpDate1 = null;
                    while (it.hasNext()) {
                        int idx = 0;
                        Object[] tmpRow = (Object[]) it.next();
                        ReportDataBean obj = new ReportDataBean();
                        obj.setRefNo((String) tmpRow[idx++]);
                        obj.setCustCode((String) tmpRow[idx++]);
                        tmp = (Double) tmpRow[idx++];
                        obj.setUnitPrice(tmp);

                        tmp2 = (Double) tmpRow[idx++];
                        obj.setQtyPcs(tmp2);
                        subTotal.accumulateQtyPcs(tmp2);

                        obj.setPayTerm((Integer) tmpRow[idx++]);
                        obj.setDelivery((Date) tmpRow[idx++]);

                        tmpDate1 = (Date) tmpRow[idx++];
                        obj.setPayDate(tmpDate1);
                        cal.setTime(tmpDate1);
                        curweek = cal.get(Calendar.WEEK_OF_MONTH);
                        curmonth = cal.get(Calendar.MONTH);
                        if (prevrecmonth == 0 || prevrecmonth != curmonth) {
                            resetBeanVariable(tmpDate1);
                        }
                        curSlot = finaliseWeekSlot(curweek, curmonth);
                        prevrecmonth = curmonth;

                        if (curSlot < 0) {
                            System.err.println("Invalid Week Slot for curDate = [" + tmpDate1
                                    + "] CurWeek = [" + curweek + "] New Week = [" + curSlot + "]");
                        }
                        //System.err.println("curDate = [" + tmpDate1 + "] CurWeek = [" + curweek + "] New Week = [" + curSlot + "]");

                        curSlot--; //minus one to represent array index (start from 0 instead of 1)
                        tmp3 = tmp * tmp2;
                        obj.setTotalValue(curSlot, tmp3);
                        subTotal.accumulateTotalValue(curSlot, tmp3);
                        //subTotal.accumulateQtyPcs(tmp2);
                        recordList.add(obj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //System.err.println("Test 11111333");
                recordList.add(subTotal);
                grandTotal.setTotalValue(0, null);
                grandTotal.setTotalValue(1, null);
                grandTotal.setTotalValue(2, null);
                grandTotal.accumulateTotalValue(3, subTotal.getTotalValue_1());
                grandTotal.accumulateTotalValue(3, subTotal.getTotalValue_2());
                grandTotal.accumulateTotalValue(3, subTotal.getTotalValue_3());
                grandTotal.accumulateTotalValue(3, subTotal.getTotalValue_4());
                grandTotal.setTotalValue(4, null);
                grandTotal.setTotalValue(5, null);
                grandTotal.setTotalValue(6, null);
                grandTotal.accumulateTotalValue(7, subTotal.getTotalValue_5());
                grandTotal.accumulateTotalValue(7, subTotal.getTotalValue_6());
                grandTotal.accumulateTotalValue(7, subTotal.getTotalValue_7());
                grandTotal.accumulateTotalValue(7, subTotal.getTotalValue_8());
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

    public DataModel getSaleCashFlowForecastData() {
        List<ReportDataBean> recordList = null;
        if (this.country != null) {
            try {
                //System.err.println("Search for records " +  this.country + "  " + this.quota + " " + this.getFromDate() + " " + this.getToDate());
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx = session.beginTransaction();

                String qstr = " SELECT  CONCAT(oc.orderid,  oc.month, oc.location, oc.sublocation) AS refno, od.custcode as custcode, ";
                qstr += " od.unitprice as unitprice, sh.qtypcs as qtypcs, sh.etd as delivery, ";
                qstr += " ADDDATE(sh.etd, c.payterm) as paydate, c.payterm as payterm ";
                qstr += " FROM shipping  sh ";
                qstr += " LEFT JOIN ordconfirm oc ON oc.crefid = sh.crefid ";
                qstr += " LEFT JOIN ordsummary os ON os.srefid = oc.srefid ";
                qstr += " LEFT JOIN orders od ON od.orderid = oc.orderid ";
                qstr += " LEFT JOIN customer c ON c.custcode = od.custcode ";
                qstr += " LEFT JOIN factorymast fm ON fm.factorycode = os.mainfactory ";
                qstr += " WHERE  fm.countrycode = :porigin  AND sh.etd >= ADDDATE(:pdateStart, -c.payterm) ";
                qstr += " AND sh.etd <= ADDDATE(:pdateEnd, -c.payterm) ";
                qstr += " AND sh.status = 'A' ";
                qstr += " Order By paydate, refno ";

                //System.err.println("Test 1111111122222");
                SQLQuery q = session.createSQLQuery(qstr);
                //System.err.println("Test 1111111122222333333");
                q.setString("porigin", this.country);

                cal.setTime(this.getFromDate());//initialise calendar
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1); //reduce one month (previous month
                q.setDate("pdateStart", cal.getTime());
                q.setDate("pdateEnd", this.getToDate());

                //add scalar
                //line 1
                q.addScalar("refno", Hibernate.STRING);
                q.addScalar("custcode", Hibernate.STRING);
                q.addScalar("unitprice", Hibernate.DOUBLE);
                q.addScalar("qtypcs", Hibernate.DOUBLE);
                q.addScalar("payterm", Hibernate.INTEGER);
                q.addScalar("delivery", Hibernate.DATE);
                q.addScalar("paydate", Hibernate.DATE);


                //System.err.println("Test 111111112222233333344444444");
                Iterator it = q.list().iterator();

                //System.err.println("Total records = " + q.list().size());
                if (recordList == null) {
                    recordList = new ArrayList();
                }

                //System.err.println("Test 11111");
                ReportDataBean grandTotal = new ReportDataBean();
                ReportDataBean subTotal = new ReportDataBean();
                grandTotal.setRefNo("Grand Total");
                grandTotal.setCustCode(null);
                subTotal.setRefNo("Sub Total");
                subTotal.setCustCode(null);

                cal.setTime(this.getFromDate());
                try {
                    subTotal.setQtyPcs(new Double(0));
                    Double tmp = null;
                    Double tmp2 = null;
                    Double tmp3 = null;
                    Integer curweek = 0;
                    Integer curmonth = 0;
                    Integer curSlot = 0;
                    Integer prevrecmonth = 0;


                    Date tmpDate1 = null;
                    while (it.hasNext()) {
                        int idx = 0;
                        Object[] tmpRow = (Object[]) it.next();
                        ReportDataBean obj = new ReportDataBean();
                        obj.setRefNo((String) tmpRow[idx++]);
                        obj.setCustCode((String) tmpRow[idx++]);
                        tmp = (Double) tmpRow[idx++];
                        obj.setUnitPrice(tmp);

                        tmp2 = (Double) tmpRow[idx++];
                        obj.setQtyPcs(tmp2);
                        subTotal.accumulateQtyPcs(tmp2);

                        obj.setPayTerm((Integer) tmpRow[idx++]);
                        obj.setDelivery((Date) tmpRow[idx++]);

                        tmpDate1 = (Date) tmpRow[idx++];
                        obj.setPayDate(tmpDate1);
                        cal.setTime(tmpDate1);
                        curweek = cal.get(Calendar.WEEK_OF_MONTH);
                        curmonth = cal.get(Calendar.MONTH);
                        if (prevrecmonth == 0 || prevrecmonth != curmonth) {
                            resetBeanVariable(tmpDate1);
                        }
                        curSlot = finaliseWeekSlot(curweek, curmonth);
                        prevrecmonth = curmonth;

                        if (curSlot < 0) {
                            System.err.println("Invalid Week Slot for curDate = [" + tmpDate1
                                    + "] CurWeek = [" + curweek + "] New Week = [" + curSlot + "]");
                        }
                        //System.err.println("curDate = [" + tmpDate1 + "] CurWeek = [" + curweek + "] New Week = [" + curSlot + "]");

                        curSlot--; //minus one to represent array index (start from 0 instead of 1)
                        tmp3 = tmp * tmp2;
                        obj.setTotalValue(curSlot, tmp3);
                        subTotal.accumulateTotalValue(curSlot, tmp3);
                        //subTotal.accumulateQtyPcs(tmp2);
                        recordList.add(obj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //System.err.println("Test 11111333");
                recordList.add(subTotal);
                grandTotal.setTotalValue(0, null);
                grandTotal.setTotalValue(1, null);
                grandTotal.setTotalValue(2, null);
                grandTotal.accumulateTotalValue(3, subTotal.getTotalValue_1());
                grandTotal.accumulateTotalValue(3, subTotal.getTotalValue_2());
                grandTotal.accumulateTotalValue(3, subTotal.getTotalValue_3());
                grandTotal.accumulateTotalValue(3, subTotal.getTotalValue_4());
                grandTotal.setTotalValue(4, null);
                grandTotal.setTotalValue(5, null);
                grandTotal.setTotalValue(6, null);
                grandTotal.accumulateTotalValue(7, subTotal.getTotalValue_5());
                grandTotal.accumulateTotalValue(7, subTotal.getTotalValue_6());
                grandTotal.accumulateTotalValue(7, subTotal.getTotalValue_7());
                grandTotal.accumulateTotalValue(7, subTotal.getTotalValue_8());
                recordList.add(grandTotal);
                //System.err.println("Test 1111144444");
                if (dataListModel == null) {
                    //System.err.println("Search for records 333");
                    dataListModel = new ListDataModel();
                }

                //System.err.println("Test 111115555");
                dataListModel.setWrappedData(recordList);
                //System.err.println("Test 11111666");

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
