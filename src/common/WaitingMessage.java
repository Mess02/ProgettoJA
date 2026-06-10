/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.Serializable;

/**
 *
 * @author Sara
 */
public class WaitingMessage implements Serializable{
    private String testo;

    public WaitingMessage(String testo) {
        this.testo = testo;
    }

    public String getTesto() {
        return testo;
    }

    @Override
    public String toString() {
        return testo;
    }
    
    
}
