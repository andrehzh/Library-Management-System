
package exception;

/**
 *
 * @author ongyongen
 */
public class BookNotFoundException extends Exception {

    public BookNotFoundException() {
    }

    public BookNotFoundException(String msg) {
        super(msg);
    }
}