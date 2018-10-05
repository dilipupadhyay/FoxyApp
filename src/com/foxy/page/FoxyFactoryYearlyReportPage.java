/*
 * FoxyCustomerSalesReportPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.foxy.page;

import java.text.SimpleDateFormat;
import javax.faces.application.FacesMessage;
import com.foxy.db.HibernateUtil;
import com.foxy.util.ListData;
import com.foxy.util.FoxyTimeZone;
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
import java.util.Calendar;

/**
 *
 * @author hcting
 */
public class FoxyFactoryYearlyReportPage extends Page implements Serializable {

    private static String MENU_CODE = new String("FOXY");
    private DataModel custSalesListModel;
    private String country = null;
    private String mainfactory = null;
    private Integer year = null;
    private FoxyTimeZone myTimeZone = new FoxyTimeZone();
    private String title = new String("");
    private SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat fmt3 = new SimpleDateFormat("yyyy");    

    public class ReportDataBean {

        private String refNo = null;
        private String month = null;
        private String location = null;
        private String customer = null;
        private String brand = null;
        private String division = null;
        private String style = null;
        private String category = null;
        private Double[] qtyd = new Double[12];  //One month one slot
        private Integer[] dayinmonth = new Integer[12]; //One month one slot

        public String getRefNo() {
            return refNo;
        }

        public void setRefNo(String refNo) {
            this.refNo = refNo;
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

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getDivision() {
            return division;
        }

        public void setDivision(String division) {
            this.division = division;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        //Get Quantity for each month
        public Double getQtyd_M01() {
            return qtyd[0];
        }

        public Double getQtyd_M02() {
            return qtyd[1];
        }

        public Double getQtyd_M03() {
            return qtyd[2];
        }

        public Double getQtyd_M04() {
            return qtyd[3];
        }

        public Double getQtyd_M05() {
            return qtyd[4];
        }

        public Double getQtyd_M06() {
            return qtyd[5];
        }

        public Double getQtyd_M07() {
            return qtyd[6];
        }

        public Double getQtyd_M08() {
            return qtyd[7];
        }

        public Double getQtyd_M09() {
            return qtyd[8];
        }

        public Double getQtyd_M10() {
            return qtyd[9];
        }

        public Double getQtyd_M11() {
            return qtyd[10];
        }

        public Double getQtyd_M12() {
            return qtyd[11];
        }

        //Get day in the monnth based on delivery date
        public String getDayinmonth_M01() {
            return String.format("%02d", dayinmonth[0]);
        }

        public String getDayinmonth_M02() {
            return String.format("%02d", dayinmonth[1]);
        }

        public String getDayinmonth_M03() {
            return String.format("%02d", dayinmonth[2]);
        }

        public String getDayinmonth_M04() {
            return String.format("%02d", dayinmonth[3]);
        }

        public String getDayinmonth_M05() {
            return String.format("%02d", dayinmonth[4]);
        }

        public String getDayinmonth_M06() {
            return String.format("%02d", dayinmonth[5]);
        }

        public String getDayinmonth_M07() {
            return String.format("%02d", dayinmonth[6]);
        }

        public String getDayinmonth_M08() {
            return String.format("%02d", dayinmonth[7]);
        }

        public String getDayinmonth_M09() {
            return String.format("%02d", dayinmonth[8]);
        }

        public String getDayinmonth_M10() {
            return String.format("%02d", dayinmonth[9]);
        }

        public String getDayinmonth_M11() {
            return String.format("%02d", dayinmonth[10]);
        }

        public String getDayinmonth_M12() {
            return String.format("%02d", dayinmonth[11]);
        }

        public void setSlotValue(Date deliveryDate, Double quantityInDzn) {
            Calendar cal = Calendar.getInstance(myTimeZone.getMyTimeZone());
            cal.setTime(deliveryDate);
            //cal.set(deliveryDate.getY)
            int slot = cal.get(cal.MONTH);
            //System.err.println("Year [" + cal.get(cal.YEAR) + "] Month [" + cal.get(cal.MONTH) + "] Day [" + cal.get(cal.DAY_OF_MONTH));
            //System.err.println("Setting value for slot[" +  slot + "] Date [" + deliveryDate + "] Qty [" +  quantityInDzn + "]" );
            this.qtyd[slot] = quantityInDzn;  //PUT QUANTITY TO CURREN MONTH SLOT
            this.dayinmonth[slot] = cal.get(cal.DAY_OF_MONTH); //PUT DAY IN THE MONTH INTO CURRENT MONTH SLOT
        }

        public void AccumulateQtyd(Date deliveryDate, Double quantityInDzn) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(deliveryDate);
                int slot = cal.get(cal.MONTH);
                if (this.qtyd[slot] != null) {
                    this.qtyd[slot] += quantityInDzn;  //PUT QUANTITY TO CURREN MONTH SLOT
                } else {
                    this.qtyd[slot] = quantityInDzn;  //PUT QUANTITY TO CURREN MONTH SLOT
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }; //Inner class end

    public FoxyFactoryYearlyReportPage() {
        super(new String("CustomerOrderReportForm"));

        try {
            this.isAuthorize(MENU_CODE);
            //System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    //PROPERTY: country
    public String getCountryName() {
        ListData ld = (ListData) getBean("listData");
        return (ld.getCountryDesc(this.getCountry(), 0));
        //return this.country;
    }

    public String getTitle() {
        ListData ld = (ListData) getBean("listData");
        if (this.year != null) {
            title = " Year [" + fmt3.format(this.getFromDate()) + "] ";
        }

        if (this.country != null) {
            title = title + "Country [" + this.getCountryName() + "] ";
        }

        if (this.mainfactory != null) {
            if (this.mainfactory.equals("ALLSELECTED")) {
                title = title + " Factory[ All Factories ] ";
            } else {
                title = title + " Factory[" + ld.getFactoryNameShort(this.getMainfactory()) + "] ";
            }
        }

        title = title + "  As At [" + fmt2.format(new Date()) + "]";


        return title;
    }

    public List getFactoryByCountryList() {
        ListData ld = (ListData) getBean("listData");
        return (ld.getFactoryListAllByCountry(this.country));
    }

    public String search() {
        this.foxySessionData.setAction(LST);

        if (this.year != null) {
            Calendar cal = Calendar.getInstance();
            // This is the right way to set the month
            cal.set(Calendar.YEAR, this.year);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, 1);

            //this.fromDate = cal.getTime();
            this.setFromDate(cal.getTime());

            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            //this.toDate = cal.getTime();
            this.setToDate(cal.getTime());
        }
        return (null);
    }

    ////////////////////////////////////////////////////////
    public DataModel getCustSalesListModel() {
        List<ReportDataBean> custOderList = null;
        if (this.country != null) {
            try {
                //System.err.println("Search for records " +  this.country + "  " + this.fromDate);
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx = session.beginTransaction();

                String qstr = "Select os.orderid as orderid,  os.month as month, os.location as location, ord.custCode as custcode, ";
                qstr += " ord.styleCode as stylecode, ca.category as category, os.qtydzn as qtydzn, ";
                qstr += " os.delivery as delivery, ord.custbrand as custbrand, ord.custdivision as custdivision ";
                qstr += " from  ordsummary as  os ";
                qstr += " LEFT JOIN category as ca ON ca.catid = os.catid ";
                qstr += " LEFT JOIN orders as  ord  ON os.orderid = ord.orderid ";
                qstr += " LEFT JOIN factorymast fm ON fm.factorycode = os.mainfactory ";
                qstr += " WHERE os.status != 'D' ";

                if (this.country != null) {
                    qstr = qstr + " AND  fm.countrycode = :porigin ";
                }

                if (this.mainfactory != null && this.mainfactory.equals("ALLSELECTED") == false) {
                    qstr = qstr + " AND  fm.factorycode = :pfactory ";
                }

                /*
                 if ( this.custCode != null  && this.custCode.length() > 0 ) {
                 qstr = qstr + " AND ord.custCode = :pcustCode ";
                 }
                 
                 if ( this.custDivision != null  && this.custDivision.length() > 0) {
                 qstr = qstr + " AND ord.custDivision = :pcustDivision ";
                 }
                 */

                if (this.getFromDate() != null) {
                    qstr = qstr + " AND os.delivery >= :pdeliveryDateFrom ";
                }

                if (this.getToDate() != null) {
                    qstr = qstr + " AND os.delivery <= :pdeliveryDateTo ";
                }

                qstr = qstr + " Order by os.orderid,  os.month, os.location ";


                //System.err.println("Search qstr  = " + qstr);


                SQLQuery q = session.createSQLQuery(qstr);
                /*
                 q.setString("porigin", this.country);
                 q.setString("pstatus", "A");
                 q.setDate("pdeliveryStart", this.getFromDate());
                 q.setDate("pdeliveryEnd",this.getToDate());
                 */

                //Set all parameter value if applicable
                if (this.country != null) {
                    q.setString("porigin", this.country);
                }

                if (this.mainfactory != null && this.mainfactory.equals("ALLSELECTED") == false) {
                    q.setString("pfactory", this.mainfactory);
                }

                /*
                 if ( this.custCode != null && this.custCode.length() > 0) {
                 q.setString("pcustCode", this.custCode);
                 }
                 
                 if ( this.custDivision != null && this.custDivision.length() > 0) {
                 q.setString("pcustDivision", this.custDivision);
                 }
                 */

                if (this.getFromDate() != null) {
                    q.setDate("pdeliveryDateFrom", this.getFromDate());
                }

                if (this.getToDate() != null) {
                    q.setDate("pdeliveryDateTo", this.getToDate());
                }

                //Specifically set column datatype
                q.addScalar("orderid", Hibernate.STRING);
                q.addScalar("month", Hibernate.STRING);
                q.addScalar("location", Hibernate.STRING);
                q.addScalar("custcode", Hibernate.STRING);
                q.addScalar("stylecode", Hibernate.STRING);
                q.addScalar("category", Hibernate.STRING);
                q.addScalar("qtydzn", Hibernate.DOUBLE);
                q.addScalar("delivery", Hibernate.DATE);
                q.addScalar("custbrand", Hibernate.STRING);
                q.addScalar("custdivision", Hibernate.STRING);


                //custOderList = q.list();
                Iterator it = q.list().iterator();
                if (custOderList == null) {
                    custOderList = new ArrayList();
                }

                //System.err.println("Search for records 2222");

                ReportDataBean grandTotal = new ReportDataBean();
                //grandTotal.setQtyd(new  Double(0));
                Date tmp_deliveryDate = null;
                Double tmp_qtyD = null;

                while (it.hasNext()) {
                    Object[] tmpRow = (Object[]) it.next();
                    int i = 0;
                    //System.err.println("Search for records 333");
                    ReportDataBean obj = new ReportDataBean();
                    try {
                        obj.setRefNo((String) tmpRow[i++]);
                        obj.setMonth((String) tmpRow[i++]);
                        obj.setLocation((String) tmpRow[i++]);
                        obj.setCustomer((String) tmpRow[i++]);
                        obj.setStyle((String) tmpRow[i++]);
                        obj.setCategory((String) tmpRow[i++]);
                        tmp_qtyD = (Double) tmpRow[i++];
                        tmp_deliveryDate = (Date) tmpRow[i++];
                        obj.setSlotValue(tmp_deliveryDate, tmp_qtyD);
                        obj.setBrand((String) tmpRow[i++]);
                        obj.setDivision((String) tmpRow[i++]);
                        grandTotal.AccumulateQtyd(tmp_deliveryDate, tmp_qtyD);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //System.err.println("Search for records33344444");

                    custOderList.add(obj);

                    //System.err.println("Search for records 555555");
                }

                if (custOderList.size() > 0) { //First item can not be total
                    //Add in subtotal for last group
                    //Add in grand total
                    grandTotal.setRefNo(new String("Grand Total"));
                    grandTotal.setCustomer(null);
                    custOderList.add(grandTotal);
                }


                //System.err.println("Search for records 111, size = " + custOderList.size());
                custSalesListModel = null;
                custSalesListModel = new ListDataModel();
                if (custOderList.size() == 0) {
                    ReportDataBean os = new ReportDataBean();
                    custOderList.add(os);
                }
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

            custSalesListModel.setWrappedData(custOderList);

        } else {
            System.err.println("No records ....!!! Search key is null");
        }

        return custSalesListModel;
    }
}
