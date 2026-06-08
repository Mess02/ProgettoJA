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
public class ChallengeMessage implements Serializable{
    private String parolaCifrata;
    private String testo;
    private int timer;
    
    public ChallengeMessage(String parolaCifrata, String testo, int timer){
        this.parolaCifrata=parolaCifrata;
        this.testo=testo;
        this.timer=timer;
    }
    
    public String getTesto(){
        return testo;
    }
    
    public String getParolaCifrata(){
        return parolaCifrata;
    }
    
    public int getTimer(){
        return timer;
    }
    
    @Override
    public String toString(){
        return testo;
    }
}
