
package exception;

/**
 *
 * @author ongyongen
 */
public class BookAlreadyReturnedException extends Exception {

    public BookAlreadyReturnedException() {
    }

    public BookAlreadyReturnedException(String msg) {
        super(msg);
    }
}