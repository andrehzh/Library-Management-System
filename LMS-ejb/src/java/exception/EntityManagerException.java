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
public class EntityManagerException extends Exception {

    public EntityManagerException() {
    }

    public EntityManagerException(String msg) {
        super(msg);
    }
}