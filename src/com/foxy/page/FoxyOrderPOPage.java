/*
 * FoxyOrderPOPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.foxy.page;

import com.foxy.data.FoxyOrderDetaiData;
import com.foxy.db.Category;
import com.foxy.db.HibernateUtil;
import com.foxy.db.OrderConfirm;
import com.foxy.db.OrderSummary;
import com.foxy.util.ListData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author eric
 */
public class FoxyOrderPOPage extends Page {

    private static String MENU_CODE = new String("FOXY");
    private String refIdC = null;
    private String refIdS = null;
    private String orderId = null;
    private String month = null;
    private String location = null;
    private String subLocation = null;
    private String lotId = null;
    private String country = null;
    private String category = null;
    private String categoryD = null;
    private Date vesselDate = Calendar.getInstance(Locale.US).getTime();
    private Date closeDate = Calendar.getInstance(Locale.US).getTime();
    private String make = null;
    private String makeD = null;
    private Double quantity = new Double(0.0);
    private Double qtyDzn = new Double(0.0);
    private Double qtyPcs = new Double(0.0);
    private String unitc = null;
    private String remark = null;
    private String ship = null;
    private String emb = null;
    private String lmerchandiser = null;
    private String mrForCust = null;
    private String destination = null;
    private String poNumber = null;
    private Date poDate = Calendar.getInstance(Locale.US).getTime();
    private Date fabricDate = Calendar.getInstance(Locale.US).getTime();
    private DataModel orderDetailModel;
    private DataModel orderListModel;
    private int rowPerPage = 0;

    /**
     * Creates a new instance of FoxyOrderDetailPage
     */
    public FoxyOrderPOPage() {
        super(new String("OrderPOForm"));
        //getOrder(this.orderId);
        this.isAuthorize(MENU_CODE);

        if (this.action != null) {
            /* Action submited from jsp, perform action initialization */

            if (this.foxySessionData.getOrderId() != null) {
                this.foxySessionData.setPageParameter(this.foxySessionData.getOrderId());
            }

            if (this.foxySessionData.getPageParameter() != null) {
                this.setOrderId(this.foxySessionData.getPageParameter());
            }
        }
    }

    private void getOrderPO() {
        ListData ld = (ListData) getBean("listData");

        try {
            Session session = (Session) HibernateUtil.currentSession();
            List ordSum = session.createQuery("from OrderConfirm where orderid = '"
                    + this.orderId
                    + "' and month = :pmonth"
                    + "  and location = :plocation"
                    + "  and sublocation = :psublocation"
                    + "  order by month").setString("pmonth", this.month).setString("plocation",
                    this.location).setString("psublocation", this.subLocation).list();

            for (int i = 0; i < ordSum.size(); i++) {
                OrderConfirm oc = (OrderConfirm) ordSum.get(i);

                this.setRefIdC(String.valueOf(oc.getId()));
                this.setMake(oc.getMake());
                this.setMakeD(ld.getFactoryNameShort(oc.getMake()));
                this.setPoNumber(oc.getPoNumber());
                this.setPoDate(oc.getPoDate());
                this.setQtyDzn(oc.getQtyDzn());
                this.setQtyPcs(oc.getQtyPcs());
                this.setVesselDate(oc.getVesselDate());
                this.setCloseDate(oc.getCloseDate());
                this.setFabricDate(oc.getFabricDate());
                this.setRemark(oc.getRemark());
                this.setDestination(this.getDestination());
                this.setUnitc(oc.getUnitc());
                this.setShip(oc.getShip());
                this.setEmb(oc.getEmb());
                this.setLmerchandiser(oc.getLmerchandiser());
                this.setMrForCust(oc.getMrForCust());

                if (oc.getUnitc().equals("DZN")) {
                    this.setQuantity(oc.getQtyDzn());
                } else if (oc.getUnitc().equals("PCS")) {
                    this.setQuantity(oc.getQtyPcs());
                }

                //save in cache session bean
                this.setSessionObject1((Object) oc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
    }

    public String save() {
        ListData ld = (ListData) getBean("listData");

        if (this.lotId != null) {
            this.month = this.lotId.substring(0, 1);
            this.location = this.lotId.substring(1);
            this.refIdS = this.getSummaryId(this.orderId, this.month, this.location);
        }

        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx = session.beginTransaction();
            OrderConfirm orderc = new OrderConfirm();

            if (this.getRefIdC() != null && this.getRefIdC().length() > 0) {
                orderc.setId(Integer.parseInt(this.getRefIdC()));
            } else {
                //clear session cache bean (dirty)
                this.setSessionObject1((Object) null);
            }

            orderc.setSsRefId(Integer.parseInt(this.getRefIdS()));
            orderc.setOrderId(this.getOrderId());
            orderc.setMonth(this.getMonth());
            orderc.setLocation(this.getLocation());
            orderc.setSubLocation(this.getSubLocation());
            //THC-Hide this, using duplicate instead orderc.setMake(this.getMake());
            orderc.setMake("1");
            orderc.setPoNumber(this.getPoNumber());
            orderc.setPoDate(this.getPoDate());
            orderc.setQtyDzn(this.getQtyDzn());
            orderc.setQtyPcs(this.getQtyPcs());
            orderc.setVesselDate(this.getVesselDate());
            orderc.setCloseDate(this.getCloseDate());
            orderc.setFabricDate(this.getFabricDate());
            orderc.setShip(this.getShip());
            orderc.setEmb(this.getEmb());
            orderc.setLmerchandiser(this.getLmerchandiser());
            orderc.setMrForCust(this.getMrForCust());
            orderc.setRemark(this.getRemark());
            orderc.setUnitc(this.getUnitc());
            orderc.setStatus(new String("A"));

            if (this.getUnitc().equals("DZN")) {
                orderc.setQtyDzn(this.getQuantity());
                orderc.setQtyPcs(this.getQuantity() * 12);
                this.setQtyDzn(orderc.getQtyDzn());
                this.setQtyPcs(orderc.getQtyPcs());
            } else if (this.getUnitc().equals("PCS")) {
                orderc.setQtyDzn(this.getQuantity() / 12);
                orderc.setQtyPcs(this.getQuantity());
                this.setQtyDzn(orderc.getQtyDzn());
                this.setQtyPcs(orderc.getQtyPcs());
            }

            OrderConfirm tmpoc = (OrderConfirm) this.getSessionObject1(OrderConfirm.class);
            if (tmpoc != null) {
                orderc.setInsTime(tmpoc.getInsTime());
                orderc.setInsUsrId(tmpoc.getInsUsrId());
            }
            session.saveOrUpdate(orderc);
            tx.commit();
            HibernateUtil.closeSession();
            this.setMakeD(ld.getFactoryNameShort(this.getMake()));
        } catch (Exception e) {
            FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Duplicate Key", "!!");
            ctx.addMessage(null, fmsg);
            e.printStackTrace();
            //return ("success");
        } finally {
            HibernateUtil.closeSession();
        }
        this.foxySessionData.setAction(ADD);
        return ("success");
    }

    public String add() {
        if (this.ectx.getRequestParameterMap().containsKey("month")) {
            this.month = this.ectx.getRequestParameterMap().get("month").toString();
        }
        if (this.ectx.getRequestParameterMap().containsKey("location")) {
            this.location = this.ectx.getRequestParameterMap().get("location").toString();
        }
        if (this.ectx.getRequestParameterMap().containsKey("refIdS")) {
            this.refIdS = this.ectx.getRequestParameterMap().get("refIdS").toString();
        }
        if (this.ectx.getRequestParameterMap().containsKey("refIdC")) {
            if (this.ectx.getRequestParameterMap().get("refIdC").toString() != null
                    && this.ectx.getRequestParameterMap().get("refIdC").toString().length() > 0) {
                this.refIdC = this.ectx.getRequestParameterMap().get("refIdC").toString();
            } else {
                this.refIdC = null;
            }
        }
        this.refIdC = null;
        this.foxySessionData.setAction(ADD);
        return ("success");
    }

    /**
     * Delete from database
     */
    public String delete() {
        this.foxySessionData.setAction(ENQ);

        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx = session.beginTransaction();
            List resultOC = session.createQuery("from OrderConfirm where crefid = "
                    + this.getRefIdC()).list();
            if (resultOC.size() == 1) {
                OrderConfirm oc = (OrderConfirm) resultOC.get(0);
                int resultoc = session.createQuery("delete from OrderConfirm where crefid = "
                        + this.getRefIdC()).executeUpdate();

                /*oc.setStatus("D");
                 session.saveOrUpdate(oc);*/

                int resultSP = session.createQuery("delete from Shipping where crefid = "
                        + oc.getId()).executeUpdate();

                /*List resultSP = session.createQuery("from Shipping where crefid = " +
                 oc.getId()).list();
                 if (resultSP.size() == 1) {
                 Shipping sp = (Shipping) resultSP.get(0);
                 sp.setStatus("D");
                 session.saveOrUpdate(sp);
                 }*/
            }
            tx.commit();
            session.clear();
            HibernateUtil.closeSession();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ("success");
    }

    public String enquire() {
        if (this.ectx.getRequestParameterMap().containsKey("month")) {
            this.month = this.ectx.getRequestParameterMap().get("month").toString();
        }
        if (this.ectx.getRequestParameterMap().containsKey("location")) {
            this.location = this.ectx.getRequestParameterMap().get("location").toString();
        }
        if (this.ectx.getRequestParameterMap().containsKey("subLocation")) {
            this.subLocation = this.ectx.getRequestParameterMap().get("subLocation").toString();
        }
        if (this.ectx.getRequestParameterMap().containsKey("refIdS")) {
            this.refIdS = this.ectx.getRequestParameterMap().get("refIdS").toString();
        }
        if (this.ectx.getRequestParameterMap().containsKey("refIdC")) {
            this.refIdC = this.ectx.getRequestParameterMap().get("refIdC").toString();
        }

        this.getOrderPO();
        this.foxySessionData.setAction(ENQ);
        return ("success");
    }

    public String edit() {
        this.foxySessionData.setAction(UPD);
        this.getOrderPO();
        return ("success");
    }

    public DataModel getOrderDetail() {
        Double totalDzn = new Double(0.0);
        Double totalPcs = new Double(0.0);
        ListData ld = (ListData) getBean("listData");
        if (orderDetailModel == null) {
            try {
                orderDetailModel = new ListDataModel();

                this.tableList.clear();
                Session session = (Session) HibernateUtil.currentSession();
                List ordSum = session.createQuery("from OrderSummary where orderid = '"
                        + this.orderId + "' and status != 'D' order by month, location").list();
                if (ordSum.size() <= 0) {
                    FoxyOrderDetaiData odDs = new FoxyOrderDetaiData();
                    this.tableList.add(odDs);
                }
                for (int i = 0; i < ordSum.size(); i++) {
                    FoxyOrderDetaiData odDs = new FoxyOrderDetaiData();
                    OrderSummary os = (OrderSummary) ordSum.get(i);

                    List result = session.createQuery("from Category where catId = " + os.getCatId()).list();
                    if (result.size() > 0) { //Check to ensure no array out of bound exception if cat not found
                        Category cat = (Category) result.get(0);
                        odDs.setCategory(cat.getCategory());
                    }

                    odDs.setRefIdS(os.getId());
                    odDs.setMonth(os.getMonth());
                    odDs.setDestName(ld.getDestinationDesc(os.getDestination(), 1));
                    odDs.setDestShortName(ld.getDestinationShortDesc(os.getDestination(), 1));
                    odDs.setLocation(os.getLocation());
                    odDs.setOrderMethod(os.getOrderMethod());
                    odDs.setDelivery(os.getDelivery());

                    odDs.setMainFactoryShortName(ld.getFactoryNameShort(os.getMainFactory()));
                    odDs.setRemark(os.getRemark());
                    odDs.setQuantityDzn(os.getQtyDzn());
                    odDs.setQuantityPcs(os.getQtyPcs());
                    odDs.setUnit(os.getUnit());
                    this.tableList.add(odDs);

                    List ordCon = session.createQuery("from OrderConfirm where orderid = '"
                            + this.orderId + "' and srefid = " + os.getId() + " and status != 'D' order by month, location, sublocation").list();

                    //List ordCon = session.createQuery("from OrderConfirm where orderid = '" +
                    //        this.orderId + "' and month = '" + os.getMonth() + "' and location = '" + os.getLocation() + "' and status != 'D' order by month, location, sublocation").list();

                    totalDzn = 0.0;
                    totalPcs = 0.0;

                    if (ordCon.size() <= 0) {
                        FoxyOrderDetaiData tot = new FoxyOrderDetaiData();
                        tot.setMonth(os.getMonth());
                        tot.setLocation(os.getLocation());
                        tot.setMainFactoryShortName(ld.getFactoryNameShort(os.getMainFactory()));
                        tot.setCategory(odDs.getCategory());
                        tot.setOrderMethod(os.getOrderMethod());
                        tot.setDelivery(os.getDelivery());
                        tot.setDestName(odDs.getDestName());
                        tot.setDestShortName(odDs.getDestShortName());

                        this.tableList.add(tot);
                    }

                    for (int j = 0; j < ordCon.size(); j++) {
                        FoxyOrderDetaiData odDc = new FoxyOrderDetaiData();
                        OrderConfirm oc = (OrderConfirm) ordCon.get(j);

                        /*result = session.createQuery("from Category where catId = '" + oc.getCategory() +"'").list();
                         cat = (Category) result.get(0);
                         odDc.setCategory(cat.getCategory());*/

                        odDc.setMonth(oc.getMonth());
                        odDc.setMainFactoryShortName(ld.getFactoryNameShort(os.getMainFactory()));
                        odDc.setCategory(odDs.getCategory());
                        odDc.setOrderMethod(os.getOrderMethod());
                        odDc.setDelivery(os.getDelivery());
                        odDc.setDestName(odDs.getDestName());
                        odDc.setDestShortName(odDs.getDestShortName());

                        odDc.setRefIdS(os.getId());
                        odDc.setRefIdC(oc.getId());
                        odDc.setPoNumber(oc.getPoNumber());
                        odDc.setPoDate(oc.getPoDate());
                        odDc.setLocation(oc.getLocation());
                        odDc.setSubLocation(oc.getSubLocation());
                        odDc.setQuantityDzn(oc.getQtyDzn());
                        odDc.setQuantityPcs(oc.getQtyPcs());
                        odDc.setUnit(os.getUnit());
                        odDc.setCloseDate(oc.getCloseDate());
                        odDc.setVesselDate(oc.getVesselDate());
                        odDc.setFabricDate(oc.getFabricDate());
                        this.tableList.add(odDc);
                        totalDzn += oc.getQtyDzn();
                        totalPcs += oc.getQtyPcs();
                        if (j == (ordCon.size() - 1)) {
                            FoxyOrderDetaiData tot = new FoxyOrderDetaiData();
                            tot.setMonth(oc.getMonth());
                            tot.setLocation(os.getLocation());
                            tot.setMainFactoryShortName(ld.getFactoryNameShort(os.getMainFactory()));
                            tot.setCategory(odDs.getCategory());
                            tot.setOrderMethod(os.getOrderMethod());
                            tot.setDelivery(os.getDelivery());
                            tot.setDestName(odDs.getDestName());
                            tot.setDestShortName(odDs.getDestShortName());

                            tot.setQuantityDzn(totalDzn);
                            tot.setQuantityPcs(totalPcs);
                            tot.setUnit(os.getUnit());
                            this.tableList.add(tot);
                        }
                    }
                }
                orderDetailModel.setWrappedData(tableList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                HibernateUtil.closeSession();
            }
        }
        return orderDetailModel;
    }

    public DataModel getOrderDetail1() {
        Double totalDzn = new Double(0.0);
        Double totalPcs = new Double(0.0);
        ListData ld = (ListData) getBean("listData");
        try {
            this.tableList.clear();
            Session session = (Session) HibernateUtil.currentSession();
            List ordSum = session.createQuery("from OrderSummary where orderid = '"
                    + this.orderId + "' and status != 'D' group by month, location order by month, location").list();
            if (ordSum.size() <= 0) {
                FoxyOrderDetaiData odDs = new FoxyOrderDetaiData();
                this.tableList.add(odDs);
            }

            for (int i = 0; i < ordSum.size(); i++) {
                FoxyOrderDetaiData odDs = new FoxyOrderDetaiData();
                OrderSummary os = (OrderSummary) ordSum.get(i);

                List result = session.createQuery("from Category where catId = " + os.getCatId()).list();
                Category cat = null;
                if (result.size() > 0) {
                    cat = (Category) result.get(0);
                    odDs.setCategory(cat.getCategory());
                }

                odDs.setRefIdS(os.getId());
                odDs.setMonth(os.getMonth());
                odDs.setLocation(os.getLocation());
                odDs.setMainFactoryShortName(ld.getFactoryNameShort(os.getMainFactory()));
                odDs.setRemark(os.getRemark());
                odDs.setQuantityDzn(os.getQtyDzn());
                odDs.setQuantityPcs(os.getQtyPcs());
                odDs.setUnit(os.getUnit());
                //this.tableList.add(odDs);

                //List ordCon = session.createQuery("from OrderConfirm where orderid = '" +
                //        this.orderId + "' and srefid = " + os.getId()).list();

                List ordCon = session.createQuery("from OrderConfirm where orderid = '"
                        + this.orderId + "' and month = :pmonth"
                        + " and location = :plocation"
                        + " and status != 'D' order by month, location, sublocation").setString("pmonth",
                        os.getMonth()).setString("plocation", os.getLocation()).list();

                totalDzn = 0.0;
                totalPcs = 0.0;

                if (ordCon.size() <= 0) {
                    FoxyOrderDetaiData tot = new FoxyOrderDetaiData();
                    this.tableList.add(tot);
                }

                for (int j = 0; j < ordCon.size(); j++) {
                    FoxyOrderDetaiData odDc = new FoxyOrderDetaiData();
                    OrderConfirm oc = (OrderConfirm) ordCon.get(j);

                    /*List result = session.createQuery("from Category where catId = '" + oc.getCategory() +"'").list();
                     cat = (Category) result.get(0);
                     odDc.setCategory(cat.getCategory());*/
                    if (cat != null) {
                        odDc.setCategory(cat.getCategory());
                    }
                    odDc.setMainFactoryShortName(ld.getFactoryNameShort(os.getMainFactory()));
                    odDc.setDestName(ld.getDestinationDesc(os.getDestination(), 1));
                    odDc.setDestShortName(ld.getDestinationShortDesc(os.getDestination(), 1));

                    odDc.setRefIdS(os.getId());
                    odDc.setRefIdC(oc.getId());
                    odDc.setMonth(oc.getMonth());
                    odDc.setPoNumber(oc.getPoNumber());
                    odDc.setPoDate(oc.getPoDate());
                    odDc.setLocation(oc.getLocation());
                    odDc.setSubLocation(oc.getSubLocation());
                    //odDc.setCategory(oc.getCategory());
                    odDc.setQuantityDzn(oc.getQtyDzn());
                    odDc.setQuantityPcs(oc.getQtyPcs());
                    odDc.setUnit(os.getUnit());
                    odDc.setVesselDate(oc.getVesselDate());
                    odDc.setCloseDate(oc.getCloseDate());
                    odDc.setFabricDate(oc.getFabricDate());
                    this.tableList.add(odDc);
                    totalDzn += oc.getQtyDzn();
                    totalPcs += oc.getQtyPcs();
                    if (j == (ordCon.size() - 1)) {
                        FoxyOrderDetaiData tot = new FoxyOrderDetaiData();
                        tot.setMonth(oc.getMonth());
                        tot.setLocation(os.getLocation());
                        tot.setQuantityDzn(totalDzn);
                        tot.setQuantityPcs(totalPcs);
                        tot.setUnit(os.getUnit());
                        this.tableList.add(tot);
                    }
                }
            }
            if (orderDetailModel == null) {
                orderDetailModel = new ListDataModel();
            }
            orderDetailModel.setWrappedData(tableList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        return orderDetailModel;
    }

    public String order() {
        this.foxySessionData.setAction(ENQ);
        this.foxySessionData.setPageParameter(String.valueOf(this.getOrderId()));
        return ("UPD_ORDER");
    }

    public String shortCut() {
        //this.foxySessionData.setAction(ENQ);
        //this.foxySessionData.setPageParameter(String.valueOf(this.getOrderId()));
        return ("go_enqOrderPO");
    }

    public String orderInstruction() {
        this.foxySessionData.setAction(ADD);
        this.foxySessionData.setPageParameter(String.valueOf(this.getOrderId()));
        return ("ADD_DETAIL");
    }

    public List getLotIdList() {
        List paramList = new ArrayList();
        List lotList = null;
        String lotCode = null;

        String orderId = foxySessionData.getOrderId();

        try {
            Session session = (Session) HibernateUtil.currentSession();
            lotList = session.createQuery("from OrderSummary where orderid = '"
                    + orderId + "' order by month").list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        try {
            for (int i = 0; i < lotList.size(); i++) {
                if (i == 0) {
                    paramList.add(new SelectItem(new String(""), new String("  ")));
                }
                OrderSummary ords = (OrderSummary) lotList.get(i);
                lotCode = new String(ords.getMonth() + ords.getLocation());
                paramList.add(new SelectItem(lotCode, lotCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paramList;
    }

    public String getSummaryId(String orderId, String month, String location) {
        String refIdKey = null;
        try {
            Session session = (Session) HibernateUtil.currentSession();
            List result = session.createQuery("from OrderSummary where orderid = '"
                    + orderId + "' and month = :pmonth"
                    + " and location = :plocation ").setString("pmonth",
                    month).setString("plocation", location).list();
            for (int i = 0; i < result.size(); i++) {
                OrderSummary ords = (OrderSummary) result.get(i);
                refIdKey = String.valueOf(ords.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }
        return refIdKey;
    }

    /**
     * Action for edit
     */
    public boolean isOperation() {
        if (this.getLotId() != null && this.getLocation() != null) {
            return (true);
        } else {
            return (false);
        }
    }

    //PROPERTY: refIdC
    public String getRefIdC() {
        return this.refIdC;
    }

    public void setRefIdC(String newValue) {
        this.refIdC = newValue;
    }

    //PROPERTY: refIdS
    public String getRefIdS() {
        return this.refIdS;
    }

    public void setRefIdS(String newValue) {
        this.refIdS = newValue;
    }

    //PROPERTY: orderId
    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String newValue) {
        this.orderId = newValue;
    }

    //PROPERTY: lotID
    public String getLotId() {
        if (this.month != null && this.location != null) {
            return (this.month + this.location);
        } else {
            return (new String(""));
        }
    }

    public void setLotId(String newValue) {
        this.lotId = newValue;
    }

    //PROPERTY: month
    public String getMonth() {
        return this.month;
    }

    public void setMonth(String newValue) {
        this.month = newValue;
    }

    //PROPERTY: location
    public String getLocation() {
        return this.location;
    }

    public void setLocation(String newValue) {
        this.location = newValue;
    }

    //PROPERTY: subLocation
    public String getSubLocation() {
        return this.subLocation;
    }

    public void setSubLocation(String newValue) {
        this.subLocation = newValue;
    }

    //PROPERTY: ship
    public String getShip() {
        return this.ship;
    }

    public void setShip(String newValue) {
        this.ship = newValue;
    }

    //PROPERTY: emb
    public String getEmb() {
        return emb;
    }

    public void setEmb(String emb) {
        this.emb = emb;
    }

    //PROPERTY: lmerchandiser == (Local MR for origin country)
    public String getLmerchandiser() {
        return lmerchandiser;
    }

    public void setLmerchandiser(String lmerchandiser) {
        this.lmerchandiser = lmerchandiser;
    }

    //PROPERTY: mrForCust
    public String getMrForCust() {
        return mrForCust;
    }

    public void setMrForCust(String mrForCust) {
        this.mrForCust = mrForCust;
    }

    //PROPERTY: country
    public String getCountry() {
        return this.country;
    }

    public void setCountry(String newValue) {
        this.country = newValue;
    }

    //PROPERTY: category
    public String getCategory() {
        return this.category;
    }

    public void setCategory(String newValue) {
        this.category = newValue;
    }

    //PROPERTY: categoryD
    public String getCategoryD() {
        return this.categoryD;
    }

    public void setCategoryD(String newValue) {
        this.categoryD = newValue;
    }

    //PROPERTY: vesselDate
    public Date getVesselDate() {
        return this.vesselDate;
    }

    public void setVesselDate(Date newValue) {
        this.vesselDate = newValue;
    }

    //PROPERTY: closeDate
    public Date getCloseDate() {
        return this.closeDate;
    }

    public void setCloseDate(Date newValue) {
        this.closeDate = newValue;
    }

    //PROPERTY: make
    public String getMake() {
        return this.make;
    }

    public void setMake(String newValue) {
        this.make = newValue;
    }

    //PROPERTY: makeD
    public String getMakeD() {
        return this.makeD;
    }

    public void setMakeD(String newValue) {
        this.makeD = newValue;
    }

    //PROPERTY: poNumber
    public String getPoNumber() {
        return this.poNumber;
    }

    public void setPoNumber(String newValue) {
        this.poNumber = newValue;
    }

    //PROPERTY: poDate
    public Date getPoDate() {
        return this.poDate;
    }

    public void setPoDate(Date newValue) {
        this.poDate = newValue;
    }

    //PROPERTY: fabricDate
    public Date getFabricDate() {
        return this.fabricDate;
    }

    public void setFabricDate(Date newValue) {
        this.fabricDate = newValue;
    }

    //PROPERTY: quantity
    public Double getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Double newValue) {
        this.quantity = newValue;
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

    //PROPERTY: unit
    public String getUnitc() {
        return this.unitc;
    }

    public void setUnitc(String newValue) {
        this.unitc = newValue;
    }

    //PROPERTY: remark
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String newValue) {
        this.remark = newValue;
    }

    //PROPERTY: destination
    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String newValue) {
        this.destination = newValue;
    }
}
