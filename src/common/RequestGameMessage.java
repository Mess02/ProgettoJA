/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.Serializable;

/**
 *
 * @author Utente
 */
public class RequestGameMessage implements Serializable {
    
    private DIFFICULTY difficolta;
    
    public RequestGameMessage(DIFFICULTY difficolta) {
        this.difficolta = difficolta;
    }
    
    public DIFFICULTY getDifficolta() {
        return difficolta;
    }
}
