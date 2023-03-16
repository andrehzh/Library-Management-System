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
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 *
 * @author andre
 */
@Stateless
public class StaffSessionBean implements StaffSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public StaffSessionBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

//    @Override
//    public List<Staff> searchStaff(String name) throws StaffNotFoundException {
//        Query q;
//        if (name != null) {
//            q = em.createQuery("SELECT s FROM Staff s WHERE" + "LOWER(s.name) LIKE :name");
//            q.setParameter("name", "%" + name.toLowerCase() + "%");
//        }
//        else {
//            q = em.createQuery("SELECT s FROM Staff s");
//        }
//        
//        return q.getResultList();
//    }
    
    @Override
    public Long createNewStaff(Staff staff) throws InputDataValidationException {
        Set<ConstraintViolation<Staff>> constraintViolations = validator.validate(staff);
        if (constraintViolations.isEmpty()) {
            em.persist(staff);
            em.flush();
            return staff.getStaffId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorMsg(constraintViolations));
        }
    }
    
        private String prepareInputDataValidationErrorMsg(Set<ConstraintViolation<Staff>> violations) {
        String msg = "Input data validation error :";

        for (ConstraintViolation violation : violations) {
            msg += "\n" + violation.getPropertyPath() + " - " + violation.getMessage();
        }

        return msg;
    }
    
    @Override
    public List<Staff> retrieveAllStaff() {
        String queryString = "SELECT s FROM Staff s";
        Query query = em.createQuery(queryString);
        return query.getResultList();
    }
    
    @Override
    public Staff retrieveStaffById(Long staffId) throws StaffNotFoundException {
        if (em.find(Staff.class, staffId) == null) {
            throw new StaffNotFoundException();
        } else {
            Staff staff = em.find(Staff.class, staffId);
            return staff;
        }
    }
    
    @Override
    public Staff retrieveStaffByUsernameAndPassword(String username, String password) throws InvalidLoginException {
        
        Query query = em.createQuery("SELECT s FROM Staff s "
                + "WHERE s.userName = :username "
                + "AND s.password = :password");
        query.setParameter("username", username);
        query.setParameter("password", password);
        
        if (query.getResultList().isEmpty()) {
            throw new InvalidLoginException();
        } else {
            return (Staff)query.getSingleResult();
        }
    }
    
    @Override
    public void updateStaff(Long staffId, String newFirstName, String newLastName, String newUserName, String newPassword) throws StaffNotFoundException {
        Staff staff = this.retrieveStaffById(staffId);
        staff.setFirstName(newFirstName);
        staff.setLastName(newLastName);
        staff.setUserName(newUserName);
        staff.setPassword(newPassword);    
    }
    
    @Override
    public void deleteStaff(Long staffId) throws StaffNotFoundException {
        Staff staff = this.retrieveStaffById(staffId);
        em.remove(staff);
    }
}
