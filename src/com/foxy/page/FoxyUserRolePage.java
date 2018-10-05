/*
 * FoxyUserRolePage.java
 *
 * Created on June 20, 2006, 6:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.foxy.page;

import com.foxy.db.HibernateUtil;
import com.foxy.db.UserRole;
import com.foxy.db.MenuTreeList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.faces.application.FacesMessage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import javax.faces.component.UISelectMany;



/**
 *
 * @author hcting
 */
public class FoxyUserRolePage extends Page implements Serializable{
    private static String MENU_CODE = new String("FOXY");
    
    private String rePassword = null;
    private List fullMenuList = null;
    private List saveOldCodeList = null;
    private String[] menuCodeSelected = null;
    private UISelectMany selMany = null;
    private String userId = null;
    
    
    
    
    /** Creates a new instance of FoxyUserPage */
    public FoxyUserRolePage() {
        super(new String("UserRoleEditForm"));
        this.isAuthorize(MENU_CODE);
        
        userId = (String)foxySessionData.getPageParameter();
        
        if ( userId != null) {
            //System.err.println("Retrieving user configured value now ...");
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                String  qstr  = new String("Select ur.role from UserRole as ur ");
                qstr = qstr.concat("WHERE ur.userId = :puserId ");
                qstr = qstr.concat("AND ur.role != :pnotrole1 ");
                qstr = qstr.concat("AND ur.role != :pnotrole2 ");
                Query q = session.createQuery(qstr);
                q.setString("puserId", this.userId);
                q.setString("pnotrole1", "FOXY");
                q.setString("pnotrole2", "manager");
                List result = q.list();
                tx.commit();
                menuCodeSelected = (String[])result.toArray(new String[result.size()]);
                saveOldCodeList = result;
                //this.selMany.setTransient(true);
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
                HibernateUtil.closeSession();
            }
        }
    }
    
    public UISelectMany getSelMany() {
        return selMany;
    }
    
    public void setSelMany(UISelectMany selMany) {
        this.selMany = selMany;
    }
    
    
    public String[] getMenuCodeSelected() {
        return menuCodeSelected;
    }
    
    public void setMenuCodeSelected(String[] menuCodeSelected) {
        this.menuCodeSelected = menuCodeSelected;
    }
    
    
    public String getUserId(){
        return (String)this.userId;
    }
    
    
    
    public String saveUserRole(){
        //Save new user role matrix
        //Following code can also get the list of selected item
        /*
        String [] sel = (String[])selMany.getSelectedValues();
        for ( int i = 0; i < sel.length; i++){
            System.err.println("Menu code selected using UIObj[" + i + "] = " + sel[i]);
        }
         */
        
        for ( int i = 0; i < menuCodeSelected.length; i++){
            if ( saveOldCodeList.contains((String)menuCodeSelected[i])){
                //System.err.println("Object menuCodeSelected  [" + menuCodeSelected[i] + "] found in old list --IGNORE");
                //If found, then remove from oldList, until the end of this loop, all remaining items
                //in old list need to be deleted from database (those are represent user uncheck items
                saveOldCodeList.remove((String)menuCodeSelected[i]);
                //Found in old list meaning no need to update database at all, sckip to next loop
                continue;
            }
            
            //System.err.println("Menu code Newly selected[" + i + "] = " + menuCodeSelected[i]);
            //Not found in old list, meaning new items and add into database
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                //Insert all selected User ROLE
                UserRole ur = new UserRole(this.userId, menuCodeSelected[i]);
                ur.setStatus("A");
                session.saveOrUpdate(ur);
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
                return null;
            } finally {
                HibernateUtil.closeSession();
                //return ("success");
            }
        }//try insert end
        
        
        //Deleting uncheck items
        Iterator it = saveOldCodeList.iterator();
        String str = null;
        while (it.hasNext()){
            str = (String)it.next();
            //System.err.println("User uncheck this item [" + str + "] --Deleting from database");
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                //Deleting all unchecked User ROLE
                String  qstr  = new String("DELETE UserRole ur ");
                qstr = qstr.concat("WHERE ur.userId = :puserId ");
                qstr = qstr.concat("AND   ur.role = :prole");
                Query q = session.createQuery(qstr);
                q.setString("puserId", this.userId);
                q.setString("prole", str);
                q.executeUpdate();
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                FacesMessage fmsg  = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getCause().toString(), e.getMessage());
                ctx.addMessage(null, fmsg);
                return null;
            } finally {
                HibernateUtil.closeSession();
                //return ("success");
            }
        }//try delete end
        
        return ("success");
        //return null;
    }
    
    
    
    private void compileMenuList(MenuTreeList MenuListArr[], String parentStr){
        if ( MenuListArr.length == 0){
            //System.err.print("Tail found with desc = [" + MenuListArr[0].getMenuName() + "]");
            return;
        }
        
        for ( int i = 0; i < MenuListArr.length; i++) {
            MenuTreeList ls = (MenuTreeList)MenuListArr[i];
            if ( ls.getSubMenus().size() == 0 ) {
                fullMenuList.add(new SelectItem(ls.getMenuCode(), "[" + ls.getMenuCode() + "] " + parentStr + "-->" + ls.getMenuName()));
                //System.err.println(parentStr + "====>" + ls.getMenuName() + "] [" + ls.getMenuCode() + "]");
            } else {
                compileMenuList((MenuTreeList[])MenuListArr[i].getSubMenus().toArray(new MenuTreeList[MenuListArr[i].getSubMenus().size()]),
                        parentStr + " --> " + MenuListArr[i].getMenuName());
            }
        }
    }
    
    
    public List getFullMenuList() {
        if ( fullMenuList == null){
            fullMenuList = new ArrayList();
            try {
                Session session = (Session) HibernateUtil.currentSession();
                Transaction tx= session.beginTransaction();
                List result = session.createQuery("from MenuTreeList as ls WHERE ls.menuParent = 0").list();
                tx.commit();
                //Using recursive to compile the full menu list
                compileMenuList((MenuTreeList[])result.toArray(new MenuTreeList[result.size()]), "MENU");
                session.clear();
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
                HibernateUtil.closeSession();
            }
        }
        return this.fullMenuList;
    }
}
