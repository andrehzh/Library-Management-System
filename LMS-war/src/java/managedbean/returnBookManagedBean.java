/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import exception.BookAlreadyReturnedException;
import exception.BookNotFoundException;
import exception.FineNotPaidException;
import exception.LendingNotFoundException;
import exception.MemberNotFoundException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
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
@Named(value = "returnBookManagedBean")
@ViewScoped
public class returnBookManagedBean implements Serializable {
    @EJB
    private BookSessionBeanLocal bookSessionBeanLocal;

    @EJB
    private MemberSessionBeanLocal memberSessionBeanLocal;

    @EJB
    private LendAndReturnSessionBeanLocal lendAndReturnSessionBeanLocal;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;
    private Date returnTime;
    private Date returnDate;
    private BigDecimal fineAmount;
    private String memberIdNo;
    private String bookIsbn;
    private String bookTitle;
    
    private BigDecimal fineAmountPaid;


    public returnBookManagedBean() {
        this.fineAmountPaid = new BigDecimal(0.0);
    }
    
    public void updateReturnBook(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            LocalDateTime returnDateTime = LocalDateTime.of(
                          returnDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 
                          returnTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
            );
            Date recordReturnDateTime = Date.from(returnDateTime.toInstant(ZoneOffset.UTC));

            fineAmount = lendAndReturnSessionBeanLocal.retrieveFineAmountForRecord(recordId, recordReturnDateTime);
            
            if (fineAmount.compareTo(BigDecimal.valueOf(0)) == -1 || 
                    (!fineAmount.equals(0) && fineAmount.compareTo(fineAmountPaid) <= 0)) {
                if (fineAmount.equals(0)) {
                    lendAndReturnSessionBeanLocal.returnBookNotLate(recordId, recordReturnDateTime);            
                } else {
                    lendAndReturnSessionBeanLocal.returnBookLate(recordId, returnDate, fineAmountPaid);
                }
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Successfully returned book", ""));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Please pay your fine of $" + fineAmount, ""));
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
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Please pay your fine of $" + fineAmount, ""));
        }
    }
    
        public void getFineAmount(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            LocalDateTime returnDateTime = LocalDateTime.of(
                          returnDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 
                          returnTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
            );
            Date recordReturnDateTime = Date.from(returnDateTime.toInstant(ZoneOffset.UTC));

            fineAmount = lendAndReturnSessionBeanLocal.retrieveFineAmountForRecord(recordId, recordReturnDateTime);
            
            if (fineAmount.compareTo(BigDecimal.valueOf(0)) == -1 || 
                    (!fineAmount.equals(0) && fineAmount.compareTo(fineAmountPaid) <= 0)) {
                if (fineAmount.equals(0)) {
                    lendAndReturnSessionBeanLocal.returnBookNotLate(recordId, recordReturnDateTime);            
                } else {
                    lendAndReturnSessionBeanLocal.returnBookLate(recordId, returnDate, fineAmountPaid);
                }
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Successfully returned book", ""));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Please pay your fine of $" + fineAmount, ""));
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
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Please pay your fine of $" + fineAmount, ""));
        }
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

    public LendAndReturnSessionBeanLocal getLendAndReturnSessionBeanLocal() {
        return lendAndReturnSessionBeanLocal;
    }

    public void setLendAndReturnSessionBeanLocal(LendAndReturnSessionBeanLocal lendAndReturnSessionBeanLocal) {
        this.lendAndReturnSessionBeanLocal = lendAndReturnSessionBeanLocal;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
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

    public BigDecimal getFineAmountPaid() {
        return fineAmountPaid;
    }

    public void setFineAmountPaid(BigDecimal fineAmountPaid) {
        this.fineAmountPaid = fineAmountPaid;
    }
   
}
