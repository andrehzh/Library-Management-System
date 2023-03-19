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
public class MemberNotFoundException extends Exception {


    public MemberNotFoundException() {
    }

    public MemberNotFoundException(String msg) {
        super(msg);
    }
}