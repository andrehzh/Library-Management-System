/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.LendAndReturn;
import exception.BookAlreadyReturnedException;
import exception.BookNotAvailableException;
import exception.BookNotFoundException;
import exception.FineNotPaidException;
import exception.InputDataValidationException;
import exception.LendingNotFoundException;
import exception.MemberNotFoundException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import session.BookSessionBeanLocal;
import session.LendAndReturnSessionBeanLocal;
import session.MemberSessionBeanLocal;

/**
 *
 * @author andre
 */
@Named(value = "lendBookManagedBean")
@ViewScoped
public class lendBookManagedBean implements Serializable {

    @EJB
    private BookSessionBeanLocal bookSessionBeanLocal;

    @EJB
    private MemberSessionBeanLocal memberSessionBeanLocal;

    @EJB
    private LendAndReturnSessionBeanLocal lendAndReturnSessionBeanLocal;

    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lendId;
    private Date lendDate;
    private BigDecimal fineAmount;
    private String memberIdNo;
    private String bookIsbn;
    private String bookTitle;
    
    private List<LendAndReturn> records;
    private LendAndReturn selectedRecord;
    
    private String searchType = "BOOK_TITLE"; 
    private String searchString; 
   
    private BigDecimal fineAmountPaid;
    private Date returnDate;    

    public lendBookManagedBean() {
        this.fineAmountPaid = new BigDecimal(0.0);
    }
    
    @PostConstruct
    public void init(){
        if (searchString == null || searchString.equals("")) {
            records = lendAndReturnSessionBeanLocal.retrieveAllLendingRecords();
        } else {
            switch (searchType) {
                case "BOOK_TITLE":
                    records = lendAndReturnSessionBeanLocal.retrieveAllLendingRecordsByBookTitle(searchString);
                    break;
                case "BOOK_ISBN":
                    records = lendAndReturnSessionBeanLocal.retrieveAllLendingRecordsByBookIsbn(searchString);
                    break;
                case "MEMBER_IDNOS":
                    records = lendAndReturnSessionBeanLocal.retrieveAllLendingRecordsByMemberIdNo(searchString);
                    break;
                case "MEMBER_FIRSTNAME":
                    records = lendAndReturnSessionBeanLocal.retrieveAllLendingRecordsByMemberFirstName(searchString);
                    break;
                case "MEMBER_LASTNAME":
                    records = lendAndReturnSessionBeanLocal.retrieveAllLendingRecordsByMemberLastName(searchString);
                    break;
                default:
                    records = lendAndReturnSessionBeanLocal.retrieveAllLendingRecords();
                    break;
            }
        }
    }

public void createLendingRecord(ActionEvent event) {
    FacesContext context = FacesContext.getCurrentInstance();

    try {  
        LocalDate lendLocalDate = lendDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date lendRecordDate = Date.from(lendLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        lendAndReturnSessionBeanLocal.createLendingRecord(memberIdNo, bookIsbn, lendRecordDate);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Successfully created record", ""));
    } catch (BookNotFoundException ex) {
        FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Book is not found", "");
        context.addMessage(null, errorMessage);
    } catch (MemberNotFoundException ex) {
        FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Member is not found", "");
        context.addMessage(null, errorMessage);
    } catch (BookNotAvailableException ex) {
        FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Book has already been lent out to another member", "");
        context.addMessage(null, errorMessage);
    } catch (InputDataValidationException ex) {
        FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        context.addMessage(null, errorMessage);
    } catch (ParseException ex) {
        FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date is malformatted", "");
        context.addMessage(null, errorMessage);
        System.out.println(ex);
    }
}

    
    public void updateReturnBook(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            LocalDate returnLocalDate = returnDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date recordReturnDate = Date.from(returnLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            fineAmount = lendAndReturnSessionBeanLocal.retrieveFineAmountForRecord(lendId, recordReturnDate);

            if (fineAmount.compareTo(BigDecimal.valueOf(0)) == -1 || 
                  (!fineAmount.equals(0) && fineAmount.compareTo(fineAmountPaid) <= 0)) {
               if (fineAmount.equals(0)) {
                  lendAndReturnSessionBeanLocal.returnBookNotLate(lendId, recordReturnDate);            
                } else {
                    lendAndReturnSessionBeanLocal.returnBookLate(lendId, returnDate, fineAmountPaid);
             }
               context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Successfully returned book", ""));
           } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Please request library member to pay a fine of $" + fineAmount, ""));
          }
        } catch (BookAlreadyReturnedException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"The book associated with this lending record has already been returned", ""));
        } catch (LendingNotFoundException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"This lending record is not found", ""));
        } catch (BookNotFoundException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"The book associated with this lending record is not found", ""));
        } catch(MemberNotFoundException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"The member associated with this lending record is not found", ""));
        } catch (FineNotPaidException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Please request library member to pay a fine of $" + fineAmount, ""));
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Please fill in the lending record ID" + fineAmount, ""));
        }
    }

       public void loadSelectedRecord() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
  
            this.selectedRecord = lendAndReturnSessionBeanLocal.retrieveLendingRecordById(lendId);    
            bookTitle = this.selectedRecord.getBook().getTitle();
            bookIsbn = this.selectedRecord.getBook().getIsbn();
            memberIdNo = this.selectedRecord.getMember().getIdentityNo();
            lendDate = this.selectedRecord.getLendDate();
            returnDate = this.selectedRecord.getReturnDate();
            fineAmount = this.selectedRecord.getFineAmount();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public LendAndReturnSessionBeanLocal getLendAndReturnSessionBeanLocal() {
        return lendAndReturnSessionBeanLocal;
    }

    public void setLendAndReturnSessionBeanLocal(LendAndReturnSessionBeanLocal lendAndReturnSessionBeanLocal) {
        this.lendAndReturnSessionBeanLocal = lendAndReturnSessionBeanLocal;
    }

    public Long getLendId() {
        return lendId;
    }

    public void setLendId(Long lendId) {
        this.lendId = lendId;
    }

    public Date getLendDate() {
        return lendDate;
    }

    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public BookSessionBeanLocal getBookSessionBeanLocal() {
        return bookSessionBeanLocal;
    }

    public void setBookSessionBeanLocal(BookSessionBeanLocal bookSessionBeanLocal) {
        this.bookSessionBeanLocal = bookSessionBeanLocal;
    }

    public MemberSessionBeanLocal getMemberSessionBeanLocal() {
        return memberSessionBeanLocal;
    }

    public void setMemberSessionBeanLocal(MemberSessionBeanLocal memberSessionBeanLocal) {
        this.memberSessionBeanLocal = memberSessionBeanLocal;
    }

    public String getMemberIdNo() {
        return memberIdNo;
    }

    public void setMemberIdNo(String memberIdNo) {
        this.memberIdNo = memberIdNo;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
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

    public List<LendAndReturn> getRecords() {
        return records;
    }

    public void setRecords(List<LendAndReturn> records) {
        this.records = records;
    }

    public LendAndReturn getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(LendAndReturn selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public BigDecimal getFineAmountPaid() {
        return fineAmountPaid;
    }

    public void setFineAmountPaid(BigDecimal fineAmountPaid) {
        this.fineAmountPaid = fineAmountPaid;
    }

}
