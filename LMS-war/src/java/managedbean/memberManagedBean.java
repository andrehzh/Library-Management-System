/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.LendAndReturn;
import entity.Member;
import exception.InputDataValidationException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import session.MemberSessionBeanLocal;

/**
 *
 * @author andre
 */
@Named(value = "memberManagedBean")
@ViewScoped
public class memberManagedBean implements Serializable {

    @EJB
    private MemberSessionBeanLocal memberSessionBeanLocal;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String firstName;    
    private String lastName;
    private char gender;    
    private Integer age;    
    private String identityNo;
    private String phone;    
    private String address;    
    private List<entity.LendAndReturn> lendAndReturns;
    
    private List<Member> members;
    private Member selectedMember;
    
    private String searchType = "FIRST_NAME"; 
    private String searchString; 

    public memberManagedBean() {
    }

    @PostConstruct
    public void init(){
        if (searchString == null || searchString.equals("")) {
            members = memberSessionBeanLocal.retrieveAllMembers();
        } else {
            switch (searchType) {
                case "FIRST_NAME":
                    members = memberSessionBeanLocal.retrieveAllMembersByFirstName(searchString);
                    break;
                case "LAST_NAME":
                    members = memberSessionBeanLocal.retrieveAllMembersByLastName(searchString);
                    break;
                case "IDENTITY_NO":
                    members = memberSessionBeanLocal.retrieveAllMembersByIdentityNo(searchString);
                    break;
                default:
                    members = memberSessionBeanLocal.retrieveAllMembers();
                    break;
            }
        }
    } 

    public void createMember(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();   
        Member m = new Member();
        m.setAddress(address);
        m.setAge(age);
        m.setFirstName(firstName);
        m.setLastName(lastName);
        m.setGender(gender);
        m.setIdentityNo(identityNo);
        m.setPhone(phone);
        try {
            memberSessionBeanLocal.createNewMember(m);
            context.addMessage(null, new FacesMessage("Successfully registered a new library member", ""));
        } catch (InputDataValidationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public MemberSessionBeanLocal getMemberSessionBeanLocal() {
        return memberSessionBeanLocal;
    }
    
    public void setMemberSessionBeanLocal(MemberSessionBeanLocal memberSessionBeanLocal) {
        this.memberSessionBeanLocal = memberSessionBeanLocal;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<LendAndReturn> getLendAndReturns() {
        return lendAndReturns;
    }

    public void setLendAndReturns(List<LendAndReturn> lendAndReturns) {
        this.lendAndReturns = lendAndReturns;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public Member getSelectedMember() {
        return selectedMember;
    }

    public void setSelectedMember(Member selectedMember) {
        this.selectedMember = selectedMember;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
    
}
