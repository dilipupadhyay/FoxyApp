/*
 * FoxyUserResetPasswordPage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import com.foxy.db.HibernateUtil;
import com.foxy.db.User;
import com.foxy.util.MD5;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.Query;



/**
 *
 * @author eric
 */
public class FoxyUserResetPasswordPage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private String oldPassword = null;
    private String newPassword = null;
    private String rePassword = null;
    
    
    /** Creates a new instance of FoxyUserPage */
    public FoxyUserResetPasswordPage() {
        super(new String("UserResetPasswordForm"));
        this.isAuthorize(MENU_CODE);
        
    }
    
    public String getOldPassword() {
        return oldPassword;
    }
    
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    
    
    public String getNewPassword() {
        return this.newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    
    //PROPERTY: rePassword
    public String getRePassword(){
        return this.rePassword;
    }
    
    
    public void setRePassword(String newValue) {
        this.rePassword = newValue;
    }
    
    
    
    
    
    public String savePassword() {
        System.err.println("SAVE password");
        User userbean = null;
        MD5 md5 = new MD5();
        
        String inputOldPassMd5 = null;
        
        //First get user password and check if it a valid user issue the change password
        try {
            inputOldPassMd5 = md5.messageDigest(this.getOldPassword());
            Session session = (Session) HibernateUtil.currentSession();
            Transaction tx= session.beginTransaction();
            Criteria crit = session.createCriteria(User.class);
            System.err.println("userid = " + this.getUserId());
            crit.add(Expression.eq("userId", this.getUserId()));
            List result = crit.list();
            System.err.println("Result size = " + result.size());
            if ( result.size() > 0 ) {
                userbean  = (User)result.get(0);
            }
            tx.commit();
        } catch (HibernateException e) {
            //do something here with the exception
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
            return null;
        } catch (Exception e){
            e.printStackTrace();
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
            ctx.addMessage(null, fmsg);
        }finally {
            HibernateUtil.closeSession();
        }
        
        //Just incase can not get user bean
        if ( userbean == null ){
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to get user info",
                    "User id [" +  this.getUserId() + "] not found");
            ctx.addMessage(null, fmsg);
            return null;
        }
        
        
        //Check user credential
        if (!inputOldPassMd5.equals(userbean.getPassword())){ //Not equal meaning not a credetial user try to hack, reject!!
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to validate user info",
                    "User Authentication Failed");
            ctx.addMessage(null, fmsg);
            return null;
        }
        
        
        if (this.getNewPassword().equals(this.getRePassword()) && getUserId() != null) {
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                //session.update("userId", this.dbUser.getUserId());
                this.setNewPassword(md5.messageDigest(this.getNewPassword()));
                
                String  qstr  = new String("UPDATE User u SET  ");
                qstr = qstr.concat("u.password = :ppassword, ");
                qstr = qstr.concat("u.updUsrId = :pupdUsrId, ");
                qstr = qstr.concat("u.updTime = now() ");
                qstr = qstr.concat("WHERE u.userId = :puserId ");
                
                Query q = session.createQuery(qstr);
                q.setString("ppassword", this.getNewPassword());
                q.setString("pupdUsrId", this.getUserId());
                q.setString("puserId", this.getUserId()); //From page bean
                q.executeUpdate();
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
                return null;
            } finally {
                HibernateUtil.closeSession();
            }
            //this.action = new String("LIST");
        } else {
            FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password mismatch", "Please re-enter password correctly");
            ctx.addMessage(null, fmsg);
            return null;
        }
        
        return ("success");
    }
    
}
