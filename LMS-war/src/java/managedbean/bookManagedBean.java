/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Book;
import exception.InputDataValidationException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import session.BookSessionBeanLocal;

/**
 *
 * @author andre
 */
@Named(value = "bookManagedBean")
@ViewScoped
public class bookManagedBean implements Serializable {

    @EJB
    private BookSessionBeanLocal bookSessionBeanLocal;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    private String title;    
    private String isbn;
    private String author;    
    
    private List<Book> books;
    private Book selectedBook;
    
    private String searchType = "TITLE"; 
    private String searchString; 

    public bookManagedBean() {
    }
    
    @PostConstruct
    public void init(){
        if (searchString == null || searchString.equals("")) {
            books = bookSessionBeanLocal.retrieveAllBooks();
        } else {
            switch (searchType) {
                case "TITLE":
                    books = bookSessionBeanLocal.retrieveAllBooksByTitle(searchString);
                    break;
                case "ISBN":
                    books = bookSessionBeanLocal.retrieveAllBooksByIsbn(searchString);
                    break;
                case "AUTHOR":
                    books = bookSessionBeanLocal.retrieveAllBooksByAuthor(searchString);
                    break;
                default:
                    books = bookSessionBeanLocal.retrieveAllBooks();
                    break;
            }
        }
    }
    
    public void createBook(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();   
        Book b = new Book();
        b.setAuthor(author);
        b.setTitle(title);
        b.setIsbn(isbn);
        try {
            bookSessionBeanLocal.createNewBook(b);
            context.addMessage(null, new FacesMessage("Successfully created a new library book", ""));
        } catch (InputDataValidationException ex) {
            Logger.getLogger(memberManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public BookSessionBeanLocal getBookSessionBeanLocal() {
        return bookSessionBeanLocal;
    }

    public void setBookSessionBeanLocal(BookSessionBeanLocal bookSessionBeanLocal) {
        this.bookSessionBeanLocal = bookSessionBeanLocal;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public void setSelectedBook(Book selectedBook) {
        this.selectedBook = selectedBook;
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
