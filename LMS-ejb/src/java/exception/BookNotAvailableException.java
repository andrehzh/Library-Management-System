
package exception;

/**
 *
 * @author ongyongen
 */
public class BookNotAvailableException extends Exception {

    public BookNotAvailableException() {
    }

    public BookNotAvailableException(String msg) {
        super(msg);
    }
}