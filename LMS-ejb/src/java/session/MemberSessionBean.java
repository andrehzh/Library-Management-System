/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Member;
import exception.InputDataValidationException;
import exception.MemberNotFoundException;
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
public class MemberSessionBean implements MemberSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public MemberSessionBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewMember(Member member) throws InputDataValidationException {
        Set<ConstraintViolation<Member>> constraintViolations = validator.validate(member);
        if (constraintViolations.isEmpty()) {
            em.persist(member);
            em.flush();
            return member.getMemberId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorMsg(constraintViolations));
        }
    }
    
    private String prepareInputDataValidationErrorMsg(Set<ConstraintViolation<Member>> violations) {
        String msg = "Input data validation error :";

        for (ConstraintViolation violation : violations) {
            msg += "\n" + violation.getPropertyPath() + " - " + violation.getMessage();
        }

        return msg;
    }
    
    @Override
    public List<Member> retrieveAllMembers() {
        String queryString = "SELECT m FROM Member m";
        Query query = em.createQuery(queryString);
        return query.getResultList();
    }
    
    @Override 
    public List<Member> retrieveAllMembersByFirstName(String firstName) {
        Query query = em.createQuery("SELECT m FROM Member m WHERE m.firstName LIKE :firstName");
        query.setParameter("firstName", firstName + "%");
        return query.getResultList();
    }
    
    @Override 
    public List<Member> retrieveAllMembersByLastName(String lastName) {
        Query query = em.createQuery("SELECT m FROM Member m WHERE m.lastName LIKE :lastName");
        query.setParameter("lastName", lastName + "%");
        return query.getResultList();
    }
    
    @Override 
    public List<Member> retrieveAllMembersByIdentityNo(String identityNo) {
        Query query = em.createQuery("SELECT m FROM Member m WHERE m.identityNo LIKE :identityNo");
        query.setParameter("identityNo", identityNo + "%");
        return query.getResultList();
    }
    
    @Override
    public Member retrieveMemberByIdentityNo(String identityNo) throws MemberNotFoundException {
           
        Query query = em.createQuery("SELECT m FROM Member m "
                + "WHERE m.identityNo = :identityNo");
              
        query.setParameter("identityNo", identityNo);
        
        if (query.getResultList().isEmpty()) {
            throw new MemberNotFoundException();
        } else {
            return (Member)query.getSingleResult();
        }
    }
    
    @Override
    public Member retrieveMemberByMemberId(Long memberId) throws MemberNotFoundException {
        if (em.find(Member.class, memberId) == null) {
            throw new MemberNotFoundException();
        } else {
            Member member = em.find(Member.class, memberId);
            return member;
        }
    }
    
    @Override
    public void updateMember(Member updatedMember) throws MemberNotFoundException {
        Long memberId = updatedMember.getMemberId();
        Member oldMember = this.retrieveMemberByMemberId(memberId);
        oldMember.setFirstName(updatedMember.getFirstName());
        oldMember.setLastName(updatedMember.getLastName());
        oldMember.setIdentityNo(updatedMember.getIdentityNo());
        oldMember.setAddress(updatedMember.getAddress());
        oldMember.setPhone(updatedMember.getPhone());
        oldMember.setAge(updatedMember.getAge());
        oldMember.setGender(updatedMember.getGender());
    }
    
}
