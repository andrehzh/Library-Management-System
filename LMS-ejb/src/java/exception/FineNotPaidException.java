/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception;

/**
 *
 * @author andre
 */
public class FineNotPaidException extends Exception {

    public FineNotPaidException() {
    }

    public FineNotPaidException(String msg) {
        super(msg);
    }
}