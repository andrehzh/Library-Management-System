
package exception;

/**
 *
 * @author ongyongen
 */
public class FineNotPaidException extends Exception {

    public FineNotPaidException() {
    }

    public FineNotPaidException(String msg) {
        super(msg);
    }
}