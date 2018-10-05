/*
 * FoxyProdScheduleListingPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;


import java.util.LinkedHashSet;
import java.util.Set;
import javax.faces.application.FacesMessage;
import com.foxy.db.HibernateUtil;
import com.foxy.db.ProdSchedule;
import com.foxy.db.ProdScheduleJoinLots;
import java.io.Serializable;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.Query;


/**
 *
 * @author hcting
 */
public class FoxyProdScheduleListingPage extends Page implements Serializable{
    
    private static String MENU_CODE = new String("FOXY");
    
    private final Integer OUTERCOLCOUNT = 6;
    private ProdSchedule prodSchBean = null;
    
    //User for listing in tables
    private DataModel  outerRowBeanModel = null;
    private DataModel  prodschJoinLotsBeanModel = null;
    
    private List<OuterTblRowBean>  outerRowBeanList = null;
    private List<ProdScheduleJoinLots> prodschJoinLotsBeanList = null;
    
    
    
    /**
     *
     * @author hcting
     */
    //Inner class start
    public class OuterTblRowBean {
        private Long  lineNo0 = null;
        private Long  lineNo1 = null;
        private Long  lineNo2 = null;
        private Long  lineNo3 = null;
        private Long  lineNo4 = null;
        private Long  lineNo5 = null;
        private DataModel col0 = null;
        private DataModel col1 = null;
        private DataModel col2 = null;
        private DataModel col3 = null;
        private DataModel col4 = null;
        private DataModel col5 = null;
        
        public OuterTblRowBean() {
        }
        
        public Long getLineNo0() {
            return lineNo0;
        }
        
        public void setLineNo0(Long lineNo0) {
            this.lineNo0 = lineNo0;
        }
        
        public Long getLineNo1() {
            return lineNo1;
        }
        
        public void setLineNo1(Long lineNo1) {
            this.lineNo1 = lineNo1;
        }
        
        public Long getLineNo2() {
            return lineNo2;
        }
        
        public void setLineNo2(Long lineNo2) {
            this.lineNo2 = lineNo2;
        }
        
        public Long getLineNo3() {
            return lineNo3;
        }
        
        public void setLineNo3(Long lineNo3) {
            this.lineNo3 = lineNo3;
        }
        
        public Long getLineNo4() {
            return lineNo4;
        }
        
        public void setLineNo4(Long lineNo4) {
            this.lineNo4 = lineNo4;
        }
        
        public Long getLineNo5() {
            return lineNo5;
        }
        
        public void setLineNo5(Long lineNo5) {
            this.lineNo5 = lineNo5;
        }
        
        
        public DataModel getCol0(){
            return this.col0;
        }
        public void setCol0(DataModel  col){
            this.col0 = col;
        }
        
        
        public DataModel getCol1(){
            return this.col1;
        }
        
        public void setCol1(DataModel  col){
            this.col1 = col;
        }
        
        
        public DataModel getCol2(){
            return this.col2;
        }
        public void setCol2(DataModel  col){
            this.col2 = col;
        }
        
        
        public DataModel getCol3(){
            return this.col3;
        }
        public void setCol3(DataModel  col){
            this.col3 = col;
        }
        
        
        public DataModel getCol4(){
            return this.col4;
        }
        public void setCol4(DataModel  col){
            this.col4 = col;
        }
        
        
        public DataModel getCol5(){
            return this.col5;
        }
        public void setCol5(DataModel  col){
            this.col5 = col;
        }
    };
//Inner class End
    
    
    
    
    
    /** Creates a new instance of FoxyCustomerPage */
    public FoxyProdScheduleListingPage() {
        super(new String("ProdScheduleListingForm"));
        
        try {
            this.isAuthorize(MENU_CODE);
            System.out.println(ctx.getApplication().getViewHandler().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (this.foxySessionData.getAction() == null) {
            this.foxySessionData.setAction("ADD");
        }
        
        try {
            /*
            Object obj = this.foxySessionData.getPageParamObj();
            Class c = ProdSchedule.class;
            if ( c.isInstance(obj)){
                System.err.println("getProdBean from session bean .........");
                this.prodSchBean = (ProdSchedule)obj;
            } else {
                if ( obj != null)
                    System.err.println("session bean obj is type of [" + obj.getClass().getName() + "], Create new ProdSchBean to replace");
                else
                    System.err.println("session bean obj is null, Create new ProdSchBean to replace");
                this.prodSchBean = new  ProdSchedule();
                this.foxySessionData.setPageParamObj((Object)this.prodSchBean);
            }*/
            
        } catch ( Exception e) {
            e.printStackTrace();
        }finally {
            
        }
        
    }
    
    
    public ProdSchedule getProdSchBean(){
        if ( this.prodSchBean == null){
            System.err.println("getProdBean creating new bean.........");
            this.prodSchBean = new  ProdSchedule();
        }
        
        System.err.println("getProdBean.........");
        //this.prodSchBean = (ProdSchedule)this.foxySessionData.getPageParamObj();
        return this.prodSchBean;
    }
    
    public void setProdSchBean(ProdSchedule prodSchBeanNew) {
        System.err.println("setProdBean.........");
        this.prodSchBean = prodSchBeanNew;
        //this.foxySessionData.setPageParamObj((Object)this.prodSchBean);
    }
    
    
    public DataModel getOuterRowBeanModel() {
        //System.err.println("Calling get  Outer Row Bean list ...");
        if ( ! getAction().equals(LST) || this.getProdSchBean().getCcode() == null){
            System.err.println("Not list mode, skip db access ...");
            //return null;
        } else if ( this.outerRowBeanList == null){
            this.outerRowBeanList = new ArrayList();
            //For every 6 cols need to create a additional row bean
            
            System.err.println("Accessing database now ...");
            
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                
                //Get all line no selected --START
                String  qstr  = new String("Select Distinct lineNo from ProdSchedule ");
                qstr = qstr.concat("Where ccode = :pccode ");
                if ( this.prodSchBean.getSewStart() != null) {
                    qstr = qstr.concat("AND sewStart >= :psewStart ");
                }
                
                if ( this.prodSchBean.getSewEnd() != null) {
                    qstr = qstr.concat("AND sewEnd <= :psewEnd ");
                }
                qstr = qstr.concat("Order by lineNo ");
                Query q = session.createQuery(qstr);
                
                q.setString("pccode",this.prodSchBean.getCcode());
                System.err.println("pcode = [" + this.prodSchBean.getCcode() + "]");
                
                if ( this.prodSchBean.getSewStart() != null) {
                    q.setDate("psewStart", this.prodSchBean.getSewStart());
                }
                if ( this.prodSchBean.getSewEnd() != null) {
                    q.setDate("psewEnd", this.prodSchBean.getSewEnd());
                }
                
                Set result = new LinkedHashSet(q.list());
                Iterator it = result.iterator();
                Long lineNo = null;
                Integer linecount = 0;
                Integer colcount = 0;
                OuterTblRowBean orb = null;
                try {
                    while( it.hasNext()){
                        //Proccess curren line no
                        lineNo = (Long)it.next();
                        
                        //Select all data based on use condition
                        Criteria crit = session.createCriteria(ProdScheduleJoinLots.class);
                        crit.add(Expression.eq("ccode", this.prodSchBean.getCcode()));
                        
                        if ( this.prodSchBean.getSewStart() != null) {
                            crit.add(Expression.ge("sewStart", this.prodSchBean.getSewStart()));
                        }
                        
                        if ( this.prodSchBean.getSewEnd() != null) {
                            crit.add(Expression.le("sewEnd", this.prodSchBean.getSewEnd()));
                        }
                        
                        crit.add(Expression.eq("lineNo", lineNo));//Only select specific lineNo only
                        crit.addOrder(Order.asc("sewStart"));
                        
                        List ret = crit.list();
                        
                        //wrap list in datamodel
                        DataModel dm = new ListDataModel();
                        dm.setWrappedData(ret);
                        
                        //Logic to decide which column to put
                        colcount = linecount % OUTERCOLCOUNT; //Decide which column to build
                        switch (colcount) {
                            case 0:
                                //New Row bean when encounter colcount = 0
                                if ( orb != null ){ //add to Outer table row list b4 create a new one
                                    outerRowBeanList.add(orb);
                                }
                                orb = new OuterTblRowBean();
                                orb.setCol0(dm);
                                orb.setLineNo0(lineNo);
                                break;
                            case 1:
                                orb.setCol1(dm);
                                orb.setLineNo1(lineNo);
                                break;
                            case 2:
                                orb.setCol2(dm);
                                orb.setLineNo2(lineNo);
                                break;
                            case 3:
                                orb.setCol3(dm);
                                orb.setLineNo3(lineNo);
                                break;
                            case 4:
                                orb.setCol4(dm);
                                orb.setLineNo4(lineNo);
                                break;
                            case 5:
                                orb.setCol5(dm);
                                orb.setLineNo5(lineNo);
                                break;
                            default:
                                System.err.println("====>Line count failed to count valid col number [" + colcount + "] for line no " + lineNo );
                                break;
                        } //End switch
                        System.err.println("====>Line no selected = [" + lineNo + "]");
                        
                        linecount++; //Keep a total of lineno selected
                    }//end while
                    
                    if ( orb != null ){ //add to outer table row list if not null (can be half filled)
                        outerRowBeanList.add(orb);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    
                }//End try
                //Get all line no selected --END
                tx.commit();
            } catch (HibernateException e) {
                //do something here with the exception
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            } catch (Exception e){
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
            }finally {
                HibernateUtil.closeSession();  //Can't close for lazy join lazy="true" tables Lazy is on by default
            }
        }
        
        if ( outerRowBeanModel == null){
            outerRowBeanModel = new ListDataModel();
        }
        
        if ( outerRowBeanList != null) {
            System.err.println("Not null and calling wrap");
            outerRowBeanModel.setWrappedData(outerRowBeanList);
        }
        
        return outerRowBeanModel;
    }
    
    
    
    public String search() {
        this.foxySessionData.setAction(LST);
        System.err.println("Calling search...");
        
        //foxyTable.setFirst(0);
        return (null);
    }
    
    
    public DataModel getProdschJoinLotsBeanModel() {
        try {
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            Criteria crit = session.createCriteria(ProdScheduleJoinLots.class);
            crit.add(Expression.eq("ccode", this.prodSchBean.getCcode()));
            
            if ( this.prodSchBean.getSewStart() != null) {
                crit.add(Expression.ge("sewStart", this.prodSchBean.getSewStart()));
            }
            
            if ( this.prodSchBean.getSewEnd() != null) {
                crit.add(Expression.le("sewEnd", this.prodSchBean.getSewEnd()));
            }
            
            prodschJoinLotsBeanList = crit.list();
            tx.commit();
        } catch (HibernateException e) {
            //do something here with the exception
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
        } catch (Exception e){
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
        }finally {
            //HibernateUtil.closeSession();  Can't close for lazy join tables
        }
        
        if ( prodschJoinLotsBeanList.size() > 0){
            if ( prodschJoinLotsBeanModel == null){
                prodschJoinLotsBeanModel = new ListDataModel();
            }
            prodschJoinLotsBeanModel.setWrappedData(prodschJoinLotsBeanList);
        }
        return prodschJoinLotsBeanModel;
    }
    
}
