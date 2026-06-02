/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package common.exceptions;

/**
 *
 * @author Mess
 */
public class MessageException extends Exception {

    /**
     * Creates a new instance of <code>MessageException</code> without detail
     * message.
     */
    public MessageException() {
    }

    /**
     * Constructs an instance of <code>MessageException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public MessageException(String msg) {
        super(msg);
    }
}
