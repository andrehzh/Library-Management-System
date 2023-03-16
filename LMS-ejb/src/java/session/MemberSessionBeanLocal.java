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
import javax.ejb.Local;

/**
 *
 * @author andre
 */
@Local
public interface MemberSessionBeanLocal {

    public Long createNewMember(Member member) throws InputDataValidationException;

    public List<Member> retrieveAllMembers();

    public List<Member> retrieveAllMembersByFirstName(String firstName);

    public List<Member> retrieveAllMembersByLastName(String lastName);

    public List<Member> retrieveAllMembersByIdentityNo(String identityNo);

    public Member retrieveMemberByIdentityNo(String identityNo) throws MemberNotFoundException;

    public Member retrieveMemberByMemberId(Long memberId) throws MemberNotFoundException;

    public void updateMember(Member updatedMember) throws MemberNotFoundException;
    
}
