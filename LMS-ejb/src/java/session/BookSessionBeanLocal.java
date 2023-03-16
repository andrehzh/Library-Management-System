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
import javax.ejb.Local;

/**
 *
 * @author andre
 */
@Local
public interface BookSessionBeanLocal {

    public List<Book> retrieveAllBooks();

    public List<Book> retrieveAllBooksByTitle(String title);

    public List<Book> retrieveAllBooksByIsbn(String isbn);

    public List<Book> retrieveAllBooksByAuthor(String author);

    public Book retrieveBookByTitle(String title) throws BookNotFoundException;

    public Book retrieveBookByIsbn(String isbn) throws BookNotFoundException;

    public Book retrieveBookById(Long bookId) throws BookNotFoundException;

    public Long createNewBook(Book book) throws InputDataValidationException;

    public void updateBook(Book updatedBook) throws BookNotFoundException;
    
}
