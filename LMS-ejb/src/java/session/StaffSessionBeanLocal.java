/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Staff;
import exception.InputDataValidationException;
import exception.InvalidLoginException;
import exception.StaffNotFoundException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author andre
 */
@Local
public interface StaffSessionBeanLocal {

    //public List<Staff> searchStaff(String name) throws StaffNotFoundException;

    public Long createNewStaff(Staff staff) throws InputDataValidationException;

    public List<Staff> retrieveAllStaff();

    public Staff retrieveStaffById(Long staffId) throws StaffNotFoundException;

    public Staff retrieveStaffByUsernameAndPassword(String username, String password) throws InvalidLoginException;

    public void updateStaff(Long staffId, String newFirstName, String newLastName, String newUserName, String newPassword) throws StaffNotFoundException;

    public void deleteStaff(Long staffId) throws StaffNotFoundException;

}
