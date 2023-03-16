/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Book;
import exception.BookNotFoundException;
import exception.InputDataValidationException;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 *
 * @author andre
 */
@Stateless
public class BookSessionBean implements BookSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final javax.validation.Validator validator;

    public BookSessionBean() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }
       
    private String prepareInputDataValidationErrorMsg(Set<ConstraintViolation<Book>> violations) {
        String msg = "Input data validation error :";

        for (ConstraintViolation violation : violations) {
            msg += "\n" + violation.getPropertyPath() + " - " + violation.getMessage();
        }

        return msg;
    }
    
    @Override
    public List<Book> retrieveAllBooks() {
        String queryString = "SELECT b FROM Book b";
        Query query = em.createQuery(queryString);
        return query.getResultList();
    }
    
    @Override
    public List<Book> retrieveAllBooksByTitle(String title) {
        Query query = em.createQuery("SELECT b FROM Book b WHERE b.title LIKE :title");
        query.setParameter("title", title + "%");
        return query.getResultList();
    }
    
    @Override
    public List<Book> retrieveAllBooksByIsbn(String isbn) {
        Query query = em.createQuery("SELECT b FROM Book b WHERE b.isbn LIKE :isbn");
        query.setParameter("isbn", isbn + "%");
        return query.getResultList();
    }
    
    @Override
    public List<Book> retrieveAllBooksByAuthor(String author) {
        Query query = em.createQuery("SELECT b FROM Book b WHERE b.author LIKE :author");
        query.setParameter("author", author + "%");
        return query.getResultList();
    }
    
    @Override
    public Book retrieveBookByTitle(String title) throws BookNotFoundException {        
        Query query = em.createQuery("SELECT b FROM Book b "
                + "WHERE b.title = :title");
              
        query.setParameter("title", title);
        
        if (query.getResultList().isEmpty()) {
            throw new BookNotFoundException();
        } else {
            return (Book)query.getSingleResult();
        }
    }
    
    @Override
    public Book retrieveBookByIsbn(String isbn) throws BookNotFoundException {        
        Query query = em.createQuery("SELECT b FROM Book b "
                + "WHERE b.isbn = :isbn");
              
        query.setParameter("isbn", isbn);
        
        if (query.getResultList().isEmpty()) {
            throw new BookNotFoundException();
        } else {
            return (Book)query.getSingleResult();
        }
    }
    
    @Override
    public Book retrieveBookById(Long bookId) throws BookNotFoundException {
        if (em.find(Book.class, bookId) == null) {
            throw new BookNotFoundException();
        } else {
            Book book = em.find(Book.class, bookId);
            return book;
        }   
    }
    
    @Override
    public Long createNewBook (Book book) throws InputDataValidationException {
        Set<ConstraintViolation<Book>> constraintViolations = validator.validate(book);
        if (constraintViolations.isEmpty()) {
            em.persist(book);
            em.flush();
            return book.getBookId();
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorMsg(constraintViolations));
        }
    }
    
    @Override
    public void updateBook(Book updatedBook) throws BookNotFoundException {
        Long bookId = updatedBook.getBookId();
        Book oldBook = this.retrieveBookById(bookId);
        oldBook.setAuthor(updatedBook.getAuthor());
        oldBook.setTitle(updatedBook.getTitle());
        oldBook.setIsbn(updatedBook.getIsbn());
    }

}
