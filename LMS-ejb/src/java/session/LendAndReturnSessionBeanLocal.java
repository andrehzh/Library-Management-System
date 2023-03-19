/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.LendAndReturn;
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
import javax.ejb.Local;

/**
 *
 * @author andre
 */
@Local
public interface LendAndReturnSessionBeanLocal {

    public Long createLendingRecord(String memberIdentityNo, String isbn, Date currentDate) throws MemberNotFoundException, BookNotFoundException, BookNotAvailableException, InputDataValidationException, ParseException;

    public List<LendAndReturn> retrieveAllLendingRecords();

    public List<LendAndReturn> retrieveAllLendingRecordsNotReturned();

    public List<LendAndReturn> retrieveAllLendingRecordsByBookId(Long bookId);

    public List<LendAndReturn> retrieveAllLendingRecordsByBookIsbn(String bookIsbn);

    public List<LendAndReturn> retrieveAllLendingRecordsByBookTitle(String bookTitle);

    public List<LendAndReturn> retrieveAllLendingRecordsByMemberIdNo(String memberIdNo);

    public List<LendAndReturn> retrieveAllLendingRecordsByMemberFirstName(String memberFirstName);

    public List<LendAndReturn> retrieveAllLendingRecordsByMemberLastName(String memberLastName);

    public LendAndReturn retrieveLendingRecordById(Long recordId) throws LendingNotFoundException;

    public LendAndReturn retrieveLendingRecordByIdNoAndTitle(String idNo, String title) throws LendingNotFoundException, BookNotFoundException, MemberNotFoundException;

    public BigDecimal calculateFineAmount(Date currentDate, Date lendDate);

    public BigDecimal retrieveFineAmountForRecord(long lendId, Date returnDate) throws LendingNotFoundException, BookNotFoundException, MemberNotFoundException, BookAlreadyReturnedException;

    public void returnBookNotLate(Long recordId, Date returnDate) throws LendingNotFoundException, BookNotFoundException, MemberNotFoundException, BookAlreadyReturnedException;

    public void returnBookLate(Long recordId, Date returnDate, BigDecimal finePayment) throws LendingNotFoundException, BookNotFoundException, MemberNotFoundException, FineNotPaidException, BookAlreadyReturnedException;
    
}
