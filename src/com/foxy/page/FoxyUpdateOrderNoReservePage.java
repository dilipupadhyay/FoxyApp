package com.foxy.page;

import com.foxy.db.HibernateUtil;
import com.foxy.db.HibernateUtilInternal;
import com.foxy.db.OrderNoReserved;
import com.foxy.util.ListData;
import com.foxy.util.OrderNumber;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Subqueries;

public class FoxyUpdateOrderNoReservePage extends Page implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3271249491428994434L;
	private static String MENU_CODE = "FOXY";
	private OrderNoReserved ordNoReservedBean = null;
	private OrderNoReserved ordNoReservedBeanFromDB = null;
	private String reservedNo = null;
	private String orderIdYear = null;
	private String cnameCode = null;
	private String mainFactoryCode = null;
	private Integer daysToExpired = Integer.valueOf(90);

	public FoxyUpdateOrderNoReservePage() {
		super(MENU_CODE);
		isAuthorize(MENU_CODE);
		init();

	}

	public void init() {
		ordNoReservedBeanFromDB = loadData(foxySessionData.getResvNoId());
		reservedNo = ordNoReservedBeanFromDB.getReservedNo();
		cnameCode = ordNoReservedBeanFromDB.getCnameCode();
		daysToExpired = ordNoReservedBeanFromDB.getDaysToExpiry().intValue();
		mainFactoryCode = String.valueOf(ordNoReservedBeanFromDB.getMainFactory());
		orderIdYear = String.valueOf(ordNoReservedBeanFromDB.getYear());
		orderIdYear = String.valueOf(ordNoReservedBeanFromDB.getYear());

	}

	private OrderNoReserved loadData(Long id) {
		Session session = HibernateUtilInternal.currentSession();
		OrderNoReserved ordReserved = null;
		try {
			Criteria crit = session.createCriteria(OrderNoReserved.class);
			crit.add(Expression.eq("resvNoId", id));

			List resultList = crit.list();

			if (resultList.size() > 0) {
				ordReserved = (OrderNoReserved) resultList.get(0);

			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
			ctx.addMessage(null, fmsg);
			return null;
		} finally {
			HibernateUtil.closeSession();
		}
		return ordReserved;
	}

	public String update() {
		Session session = HibernateUtil.currentSession();
		try {
			Transaction tx = session.beginTransaction();
			ordNoReservedBeanFromDB.setMainFactory(Integer.parseInt(mainFactoryCode));
			ordNoReservedBeanFromDB.setCnameCode(cnameCode);
			ordNoReservedBeanFromDB.setReservedNo(reservedNo);
			session.update(ordNoReservedBeanFromDB);
			tx.commit();
			session.clear();
			return ("success");
		} catch (Exception e) {
			e.printStackTrace();
			FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
			ctx.addMessage(null, fmsg);
			return null;
		} finally {
			HibernateUtil.closeSession();
			foxySessionData.setResvNoId(null);
		}
	}

	public OrderNoReserved getOrdNoReservedBean() {
		return ordNoReservedBean;
	}

	public String getReservedNo() {
		return reservedNo;
	}

	public void setReservedNo(String reservedNo) {
		this.reservedNo = reservedNo;
	}

	public String getCnameCode() {
		return cnameCode;
	}

	public void setCnameCode(String cnameCode) {
		this.cnameCode = cnameCode;
	}

	public void setOrdNoReservedBean(OrderNoReserved ordNoReservedBean) {
		this.ordNoReservedBean = ordNoReservedBean;
	}

	public String getOrderIdYear() {
		return orderIdYear;
	}

	public void setOrderIdYear(String orderIdYear) {
		this.orderIdYear = orderIdYear;
	}

	public String getMainFactoryCode() {
		return mainFactoryCode;
	}

	public void setMainFactoryCode(String mainFactoryCode) {
		this.mainFactoryCode = mainFactoryCode;
	}

	public Integer getDaysToExpired() {
		return daysToExpired;
	}

	public void setDaysToExpired(Integer daysToExpired) {
		this.daysToExpired = daysToExpired;
	}

}
