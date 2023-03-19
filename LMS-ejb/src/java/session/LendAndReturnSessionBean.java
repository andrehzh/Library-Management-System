/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Book;
import entity.LendAndReturn;
import entity.Member;
import exception.BookAlreadyReturnedException;
import exception.BookNotAvailableException;
import exception.BookNotFoundException;
import exception.FineNotPaidException;
import exception.InputDataValidationException;
import exception.LendingNotFoundException;
import exception.MemberNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJB;
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
public class LendAndReturnSessionBean implements LendAndReturnSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private MemberSessionBeanLocal memberSessionBeanLocal;

    @EJB
    private BookSessionBeanLocal bookSessionBeanLocal;

    @PersistenceContext
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public LendAndReturnSessionBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }
    
    private String prepareInputDataValidationErrorMsg(Set<ConstraintViolation<LendAndReturn>> violations) {
        String msg = "Input data validation error :";

        for (ConstraintViolation violation : violations) {
            msg += "\n" + violation.getPropertyPath() + " - " + violation.getMessage();
        }

        return msg;
    }
           
    @Override
    public Long createLendingRecord(String memberIdentityNo, String isbn, Date currentDate) throws MemberNotFoundException, BookNotFoundException, BookNotAvailableException, InputDataValidationException, ParseException {
        Member member = memberSessionBeanLocal.retrieveMemberByIdentityNo(memberIdentityNo);
        Book book = bookSessionBeanLocal.retrieveBookByIsbn(isbn);
                
        Long bookId = book.getBookId();
        List<LendAndReturn> bookRecords = this.retrieveAllLendingRecordsByBookId(bookId);
        boolean isAvailable = true;
        if (!bookRecords.isEmpty()) {
            for (LendAndReturn bookRecord : bookRecords) {
                Date lendDate = bookRecord.getLendDate();
                Date returnDate = bookRecord.getReturnDate();
                if (currentDate.after(lendDate) && returnDate == null || currentDate.equals(lendDate) && returnDate == null) {
                    isAvailable = false;
                    break;
                }
            }
        }
        
        if (isAvailable == true) {
            LendAndReturn record = new LendAndReturn(currentDate);
            record.setMember(member);
            record.setBook(book);
                        
            Set<ConstraintViolation<LendAndReturn>> constraintViolations = validator.validate(record);
            if (constraintViolations.isEmpty()) {
                em.persist(record);
                em.flush();
                return record.getLendId();
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorMsg(constraintViolations));
            }
            
        } else {
            throw new BookNotAvailableException();
        }

    }
    
    @Override
    public List<LendAndReturn> retrieveAllLendingRecords() {
        String queryString = "SELECT record FROM LendAndReturn record";
        Query query = em.createQuery(queryString);
        return query.getResultList();   
    }
    
    @Override 
    public List<LendAndReturn> retrieveAllLendingRecordsNotReturned() {
        Query query = em.createQuery("SELECT record FROM LendAndReturn record WHERE record.returnDate is NULL");
        return query.getResultList();
    }
    
    @Override
    public List<LendAndReturn> retrieveAllLendingRecordsByBookId(Long bookId) {
        Query query = em.createQuery("SELECT record FROM LendAndReturn record "
                + "WHERE record.book.bookId = :bookId");
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }
    
    @Override
    public List<LendAndReturn> retrieveAllLendingRecordsByBookIsbn(String bookIsbn) {
        Query query = em.createQuery("SELECT record FROM LendAndReturn record "
                + "WHERE record.book.isbn LIKE :bookIsbn");
        query.setParameter("bookIsbn", bookIsbn + "%");
        return query.getResultList();
    }
    
    @Override
    public List<LendAndReturn> retrieveAllLendingRecordsByBookTitle(String bookTitle) {
        Query query = em.createQuery("SELECT record FROM LendAndReturn record "
                + "WHERE record.book.title LIKE :bookTitle");
        query.setParameter("bookTitle", bookTitle + "%");
        return query.getResultList();
    }
    
    @Override
    public List<LendAndReturn> retrieveAllLendingRecordsByMemberIdNo(String memberIdNo) {
        Query query = em.createQuery("SELECT record FROM LendAndReturn record "
                + "WHERE record.member.identityNo LIKE :memberIdNo");
        query.setParameter("memberIdNo", memberIdNo + "%");
        return query.getResultList();
    }
    
    @Override
    public List<LendAndReturn> retrieveAllLendingRecordsByMemberFirstName(String memberFirstName) {
        Query query = em.createQuery("SELECT record FROM LendAndReturn record "
                + "WHERE record.member.firstName LIKE :memberFirstName");
        query.setParameter("memberFirstName", memberFirstName + "%");
        return query.getResultList();
    }
    
    @Override
    public List<LendAndReturn> retrieveAllLendingRecordsByMemberLastName(String memberLastName) {
        Query query = em.createQuery("SELECT record FROM LendAndReturn record "
                + "WHERE record.member.lastName LIKE :memberLastName");
        query.setParameter("memberLastName", memberLastName + "%");
        return query.getResultList();
    }
    
    @Override
    public LendAndReturn retrieveLendingRecordById(Long recordId) throws LendingNotFoundException {
        if (em.find(LendAndReturn.class, recordId) == null) {
            throw new LendingNotFoundException();
        } else {
            LendAndReturn record = em.find(LendAndReturn.class, recordId);
            return record;
        }
    }
    
    @Override
    public LendAndReturn retrieveLendingRecordByIdNoAndTitle(String idNo, String title) throws LendingNotFoundException, BookNotFoundException, MemberNotFoundException {
        Long bookId = bookSessionBeanLocal.retrieveBookByTitle(title).getBookId();
        Long memberId = memberSessionBeanLocal.retrieveMemberByIdentityNo(idNo).getMemberId();
           
        Query query = em.createQuery("SELECT record FROM LendAndReturn record "
                + "WHERE record.book.bookId = :bookId "
                + "AND record.member.memberId = :memberId");
 
        query.setParameter("bookId", bookId);
        query.setParameter("memberId", memberId);

        if (query.getResultList().isEmpty()) {
            throw new LendingNotFoundException();
        } else {
            return (LendAndReturn)query.getSingleResult();
        }
    }

    @Override
    public BigDecimal calculateFineAmount(Date currentDate, Date lendDate) {
        long days = TimeUnit.DAYS.convert(Math.abs(currentDate.getTime() - lendDate.getTime()), TimeUnit.MILLISECONDS);
        return BigDecimal.valueOf(Math.max(0, days - 14) * 0.50);
    }
    
    @Override
    public BigDecimal retrieveFineAmountForRecord(long lendId, Date returnDate) throws LendingNotFoundException, BookNotFoundException, MemberNotFoundException, BookAlreadyReturnedException {
        LendAndReturn record = retrieveLendingRecordById(lendId);
        BigDecimal fine = calculateFineAmount(record.getLendDate(), returnDate);

        if (fine.compareTo(BigDecimal.ZERO) > 0) { 
            record.setFineAmount(fine);
            return fine;
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    public void returnBookNotLate(Long recordId, Date returnDate) throws LendingNotFoundException, BookNotFoundException, MemberNotFoundException, BookAlreadyReturnedException {
        LendAndReturn record = this.retrieveLendingRecordById(recordId);
        record.setReturnDate(returnDate);
        if (record.getReturnDate() != null) {
            throw new BookAlreadyReturnedException();
        }
    }
    
    @Override
    public void returnBookLate(Long recordId, Date returnDate, BigDecimal finePayment) throws LendingNotFoundException, BookNotFoundException, MemberNotFoundException, FineNotPaidException, BookAlreadyReturnedException {
        LendAndReturn record = this.retrieveLendingRecordById(recordId);
        Date lendDate = record.getLendDate(); 
        BigDecimal fineAmount = calculateFineAmount(returnDate, lendDate);
        if (record.getReturnDate() != null) {
            throw new BookAlreadyReturnedException();
        } else {
            if (finePayment.compareTo(fineAmount) == -1) {
                throw new FineNotPaidException();
            } else {
                record.setReturnDate(returnDate);
                record.setFineAmount(fineAmount);
            }
        }
    }

}
