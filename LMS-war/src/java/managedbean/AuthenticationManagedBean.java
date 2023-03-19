/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Staff;
import exception.InvalidLoginException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import session.StaffSessionBeanLocal;

/**
 *
 * @author andre
 */
@Named(value = "authenticationManagedBean")
@SessionScoped
public class AuthenticationManagedBean implements Serializable {

    @EJB
    private StaffSessionBeanLocal staffSessionBeanLocal;
    
    private String userName;
    private String password;
    private long staffId;
    

    public AuthenticationManagedBean() {
    }
    
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            Staff staff = staffSessionBeanLocal.retrieveStaffByUsernameAndPassword(userName, password);
            staffId = staff.getStaffId();
            userName = staff.getUserName();
            password = staff.getPassword();
            return "secret/homePage.xhtml?faces-redirect=true";

        } catch (InvalidLoginException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Login credentials are incorrect", ""));
            userName = null; 
            password = null; 
            staffId = -1;
            return "index.xhtml";
        }
    }
    
    public String logout() {
        userName = null; 
        password = null; 
        staffId = -1;
        return "/index.xhtml?faces-redirect=true"; 
    }

    public StaffSessionBeanLocal getStaffSessionBeanLocal() {
        return staffSessionBeanLocal;
    }

    public void setStaffSessionBeanLocal(StaffSessionBeanLocal staffSessionBeanLocal) {
        this.staffSessionBeanLocal = staffSessionBeanLocal;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getStaffId() {
        return staffId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }
}

