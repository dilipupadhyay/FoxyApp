/*
 * FoxyOrderInstructionPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.foxy.page;

import com.foxy.bean.FoxySessionData;
import com.foxy.data.FoxyOrderDetaiData;
import com.foxy.db.Category;
import com.foxy.db.HibernateUtil;
import com.foxy.db.OrderConfirm;
import com.foxy.db.OrderSummary;
import com.foxy.db.QuotaCats;
import com.foxy.db.QuotaMast;
import com.foxy.util.ListData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author eric
 */
public class FoxyOrderInstructionPage extends Page {

    private static String MENU_CODE = new String("FOXY");
    private String refId = null;
    private String orderId = null;
    private String month = null;
    private String location = null;
    private String factory = null;
    private String factoryD = null;
    private String subLocation = null;
    private String country = null;
    private String orderMethod = null;
    private Long catId = null;
    private String categoryD = null;
    private String importTax = "NON";
    private String quota = "NON"; //default set to NON
    private Double multiplier = null;
    private Date delivery = Calendar.getInstance(Locale.US).getTime();
    private Date vesselDate = Calendar.getInstance(Locale.US).getTime();
    private String make = null;
    private Double quantity = new Double(0.0);
    private Double qtyDzn = new Double(0.0);
    private Double qtyPcs = new Double(0.0);
    private String unit = null;
    private String unitc = null;
    private String remark = null;
    private String destination = null;
    private String ship = "SEA"; //Default set to SEA
    private String poNumber = null;
    private Date poDate = Calendar.getInstance(Locale.US).getTime();
    private Date fabricDeliveryDate = null;
    private String status = null;
    private DataModel orderDetailModel;

    /**
     * Creates a new instance of FoxyOrderDetailPage
     */
    public FoxyOrderInstructionPage() {
        super(new String("OrderInstructionForm"));
        //getOrder(this.orderId);
        this.isAuthorize(MENU_CODE);

        /* Get session data */
        if (this.foxySessionData == null) {
            this.foxySessionData = (FoxySessionData) getBean("foxySessionData");
        }

        /* Set default action */
        if (this.foxySessionData.getAction() == null) {
            this.foxySessionData.setAction("ADD");
        }

        if (this.foxySessionData.getAction() != null) {
            if (this.foxySessionData.getOrderId() != null) {
                this.foxySessionData.setPageParameter(this.foxySessionData.getOrderId());
            }

            if (this.foxySessionData.getPageParameter() != null) {
                this.setOrderId(this.foxySessionData.getPageParameter());
            }
        }
    }

    public Double getQuotaMultiplier(String country, String quota, Long catId) {
        Double ret = new Double(0.0f);
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx = session.beginTransaction();
            Query q1 = session.createQuery("from QuotaCats c WHERE c.country = :pcountry AND c.quota = :pquota AND c.catId = :pcatId)");
            q1.setString("pcountry", country);
            q1.setString("pquota", quota);
            q1.setLong("pcatId", catId);
            List resultlist = q1.list();

            if (resultlist.size() > 0) {
                QuotaCats qc = (QuotaCats) resultlist.get(0);
                ret = qc.getMultiplier();
            }

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession();
        }

        return ret;
    }

    public List getApplicableQuotaList() {
        List qlist = null;
        try {
            List resultlist = null;
            if (this.catId != null) {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx = session.beginTransaction();
                Query q1 = session.createQuery("from QuotaMast where qtaId IN (SELECT DISTINCT c.qtaId  FROM QuotaCats c  WHERE c.country = :pcountry AND c.catId = :pcatId)");
                q1.setString("pcountry", this.getFactory());
                q1.setLong("pcatId", this.getCatId());
                resultlist = q1.list();
                tx.commit();
            } else {
                resultlist = new ArrayList();
            }

            //Use LinkedHashMap to supress duplicate entry, bcoz list can have duplicate if join table used!!!! and hibernate do return duplicate
            Map resultMap = new LinkedHashMap();
            QuotaMast qtam = null;
            for (int i = 0; i < resultlist.size(); i++) {
                qtam = (QuotaMast) resultlist.get(i);
                resultMap.put(qtam.getQuota(), qtam);//Since already filter by country and category, quota have to be unique here
            }

            qlist = new ArrayList();
            qlist.add(new SelectItem(new String(""), new String("Please Select One")));
            qlist.add(new SelectItem(new String("NON"), new String("NON - Quota Not Applicable")));

            QuotaMast qm = null;
            Set st = resultMap.entrySet();
            Iterator it = st.iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                qm = (QuotaMast) me.getValue();
                qlist.add(new SelectItem(qm.getQuota(), qm.getListDisplayString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            //ctx.addMessage(null, fmsg);
            return null;
        } finally {
            HibernateUtil.closeSession();
        }
        return qlist;
    }

    private void getOrderSum() {
        ListData ld = (ListData) getBean("listData");
        getApplicableQuotaList();
        try {
            Session session = (Session) HibernateUtil.currentSession();
            //List ordSum = session.createQuery("from OrderSummary where orderid = '" +
            //        this.orderId + "' and month = '" + this.month +
            //        "' and location = '" + this.location + "' order by month").list();
            List ordSum = session.createQuery("from OrderSummary where srefid = "
                    + this.refId + " order by month").list();

            for (int i = 0; i < ordSum.size(); i++) {
                OrderSummary os = (OrderSummary) ordSum.get(i);
                Category cat = ld.getCategory(os.getCatId(), 0);
                if (cat != null) {
                    this.setCategoryD(cat.getCategory() + " - " + cat.getDesc());
                }

                this.setCatId(os.getCatId());
                this.setOrderMethod(os.getOrderMethod());
                this.setImportTax(os.getImportTax());
                this.setQuota(os.getQuota());
                this.setMultiplier(os.getMultiplier());
                this.setRefId(String.valueOf(os.getId()));
                this.setRemark(os.getRemark());
                this.setQtyDzn(os.getQtyDzn());
                this.setQtyPcs(os.getQtyPcs());
                if (os.getUnitc().equals("DZN")) {
                    this.setQuantity(os.getQtyDzn());
                } else if (os.getUnitc().equals("PCS")) {
                    this.setQuantity(os.getQtyPcs());
                }
                this.setUnit(os.getUnit());
                this.setUnitc(os.getUnitc());
                this.setDelivery(os.getDelivery());
                this.setFabricDeliveryDate(os.getFabricDeliveryDate());
                this.setShip(os.getShip());
                this.setFactory(os.getMainFactory());
                this.setFactoryD(ld.getFactoryNameShort(os.getMainFactory()));
                this.setDestination(os.getDestination());
                //cache in session bean
                this.setSessionObject1((Object) os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String save() {
        OrderSummary orders = new OrderSummary();
        if (this.location == null || this.location.length() < 1) {
            this.location = new String(" ");
        }

        try {
            if (this.getRefId() != null) {
                orders.setId(Integer.parseInt(this.getRefId()));
            }

            if (this.quota.equals("NON")) {
                orders.setMultiplier(new Double(0.0f));
            } else {
                orders.setMultiplier(getQuotaMultiplier(this.getFactory(), this.getQuota(), this.getCatId()));
            }

            /* Get Location, destination, unit, based on category*/
            ListData ld = (ListData) getBean("listData");
            Category cat = ld.getCategory(this.getCatId(), 0);
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx = session.beginTransaction();
            orders.setOrderId(this.getOrderId());
            orders.setMonth(this.getMonth());
            orders.setLocation(this.getLocation());
            orders.setMainFactory(this.getFactory());
            orders.setDestination(cat.getCountry());
            orders.setCatId(this.getCatId());
            orders.setOrderMethod(this.getOrderMethod());
            orders.setImportTax(this.getImportTax());
            orders.setQuota(this.getQuota());
            orders.setDelivery(this.getDelivery());
            orders.setFabricDeliveryDate(this.getFabricDeliveryDate());
            orders.setUnit(cat.getUnit());
            orders.setShip(this.getShip());
            orders.setUnitc(this.getUnitc());
            orders.setRemark(this.getRemark());
            orders.setStatus(new String("A"));

            if (this.getUnitc().equals("DZN")) {
                orders.setQtyDzn(this.getQuantity());
                orders.setQtyPcs(this.getQuantity() * 12);
                this.setQtyDzn(orders.getQtyDzn());
                this.setQtyPcs(orders.getQtyPcs());
            } else if (this.getUnitc().equals("PCS")) {
                orders.setQtyDzn(this.getQuantity() / 12);
                orders.setQtyPcs(this.getQuantity());
                this.setQtyDzn(orders.getQtyDzn());
                this.setQtyPcs(orders.getQtyPcs());
            }

            this.setCategoryD(cat.getCategory() + " - " + cat.getDesc());
            this.setFactoryD(ld.getFactoryNameShort(this.getFactory()));
            //retrieve from cache
            OrderSummary tmpos = (OrderSummary) this.getSessionObject1(OrderSummary.class);
            if (tmpos != null) {
                orders.setInsTime(tmpos.getInsTime());
                orders.setInsUsrId(tmpos.getInsUsrId());
            }
            session.saveOrUpdate(orders);
            tx.commit();
            HibernateUtil.closeSession();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            return null;
        } finally {
            HibernateUtil.closeSession();
        }


        this.foxySessionData.setAction(ENQ);
        return ("success");
    }

    public String enquire() {
        if (this.ectx.getRequestParameterMap().containsKey("month")) {
            this.month = this.ectx.getRequestParameterMap().get("month").toString();
        }
        if (this.ectx.getRequestParameterMap().containsKey("location")) {
            this.location = this.ectx.getRequestParameterMap().get("location").toString();
        }
        if (this.ectx.getRequestParameterMap().containsKey("refid")) {
            this.refId = this.ectx.getRequestParameterMap().get("refid").toString();
        }

        this.getOrderSum();
        this.foxySessionData.setAction(ENQ);
        return ("success");
    }

    public String update() {
        this.foxySessionData.setAction(ENQ);
        return ("success");
    }

    public String edit() {
        this.foxySessionData.setAction(UPD);
        this.getOrderSum();
        //return ("success");
        return null;
    }

    public String add() {
        this.setMonth(null);
        this.setLocation(null);
        this.setSessionObject1((Object) null);//to make sure no previous object is cap
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

            //Check if Fabric and accessories referred to this srefid or not
            List resultOS = session.createQuery("from InvMovement where ssRefId = "
                    + this.getRefId()).list();
            if (resultOS.size() > 0) {
                FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to delete", " record id ["
                        + this.getRefId() + "] is still referred by F & A invoice, Please check with account department");
                ctx.addMessage(null, fmsg);
                tx.commit();
                session.clear();
                HibernateUtil.closeSession();
                return (null);
            }

            //Check if Sales invoice referred to this srefid or not
            resultOS = session.createQuery("from SalesInvoiceDetail where srefid = "
                    + this.getRefId()).list();
            if (resultOS.size() > 0) {
                FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to delete", " record id ["
                        + this.getRefId() + "] is still referred by Sales invoice, Please check with account department");
                ctx.addMessage(null, fmsg);
                tx.commit();
                session.clear();
                HibernateUtil.closeSession();
                return (null);
            }

            resultOS = session.createQuery("from OrderSummary where srefid = "
                    + this.getRefId()).list();
            if (resultOS.size() == 1) {
                OrderSummary os = (OrderSummary) resultOS.get(0);

                int resultos = session.createQuery("delete from OrderSummary where srefid = "
                        + this.getRefId()).executeUpdate();

                /*os.setStatus("D");
                 session.saveOrUpdate(os);*/

                List resultOC = session.createQuery("from OrderConfirm where srefid = "
                        + this.getRefId()).list();
                if (resultOC.size() > 0) {
                    for (int j = 0; j < resultOC.size(); j++) {
                        OrderConfirm oc = (OrderConfirm) resultOC.get(j);

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
                         } else {
                         continue;
                         }*/
                    }
                    int resultoc = session.createQuery("delete from OrderConfirm where srefid = "
                            + this.getRefId()).executeUpdate();
                }
            }
            tx.commit();
            session.clear();
            HibernateUtil.closeSession();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ("success");
    }

    public String search() {
        this.action = new String("LIST");
        return ("success");
    }

    public DataModel getOrderDetail() {
        Double totalDzn = new Double(0.0);
        Double totalPcs = new Double(0.0);
        ListData ld = (ListData) getBean("listData");
        //Perfomance improvement
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
                    odDs.setFabricDate(os.getFabricDeliveryDate());

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

                        odDc.setPoNumber(oc.getPoNumber());
                        odDc.setPoDate(oc.getPoDate());
                        odDc.setLocation(oc.getLocation());
                        odDc.setSubLocation(oc.getSubLocation());
                        odDc.setQuantityDzn(oc.getQtyDzn());
                        odDc.setQuantityPcs(oc.getQtyPcs());
                        odDc.setUnit(os.getUnit());
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

    public String order() {
        this.foxySessionData.setAction(ENQ);
        this.foxySessionData.setPageParameter(String.valueOf(this.getOrderId()));
        return ("UPD_ORDER");
    }

    public String shortCut() {
        return ("go_newOrderInstruction");
    }

    //PROPERTY: refId
    public String getRefId() {
        return this.refId;
    }

    public void setRefId(String newValue) {
        this.refId = newValue;
    }

    //PROPERTY: orderId
    public String getOrderId() {
        return this.orderId;
    }

    public String getOrderIdD() {
        return this.orderId;
    }

    public void setOrderId(String newValue) {
        this.orderId = newValue;
    }
    //PROPERTY: lotID

    public String getLotId() {
        return (this.month + this.location);
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

    //PROPERTY: factory
    public String getFactory() {
        return this.factory;
    }

    public void setFactory(String newValue) {
        this.factory = newValue;
    }

    //PROPERTY: factoryD
    public String getFactoryD() {
        return this.factoryD;
    }

    public void setFactoryD(String newValue) {
        this.factoryD = newValue;
    }

    //PROPERTY: catid
    public Long getCatId() {
        return this.catId;
    }

    public void setCatId(Long newValue) {
        this.catId = newValue;
    }

    //PROPERTY: categoryD
    public String getCategoryD() {
        return this.categoryD;
    }

    public void setCategoryD(String newValue) {
        this.categoryD = newValue;
    }

    //PROPERTY: orderMethod
    public String getOrderMethod() {
        return this.orderMethod;
    }

    public void setOrderMethod(String newValue) {
        this.orderMethod = newValue;
    }

    //PROPERTY: importTax
    public String getImportTax() {
        return this.importTax;
    }

    public void setImportTax(String newValue) {
        this.importTax = newValue;
    }

    //PROPERTY: quota
    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    //PROPERTY: multiplier
    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
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

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String newValue) {
        this.unit = newValue;
    }
    //PROPERTY: unitc

    public String getUnitc() {
        return this.unitc;
    }

    public void setUnitc(String newValue) {
        this.unitc = newValue;
    }
    //PROPERTY: delivery

    public Date getDelivery() {
        return this.delivery;
    }

    public void setDelivery(Date newValue) {
        this.delivery = newValue;
    }

    public Date getFabricDeliveryDate() {
        return fabricDeliveryDate;
    }

    public void setFabricDeliveryDate(Date fabricdeliverdate) {
        this.fabricDeliveryDate = fabricdeliverdate;
    }

    //PROPERTY: ship
    public String getShip() {
        return this.ship;
    }

    public void setShip(String newValue) {
        this.ship = newValue;
    }
    //PROPERTY: destination

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String newValue) {
        this.destination = newValue;
    }
    //PROPERTY: remark

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String newValue) {
        this.remark = newValue;
    }
    //PROPERTY: status

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String newValue) {
        this.status = newValue;
    }
}
