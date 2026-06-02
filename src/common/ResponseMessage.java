/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

import java.io.Serializable;

/**
 *
 * @author Mess
 */
public class ResponseMessage implements Serializable{
    private boolean success;
    
    public ResponseMessage(boolean success){
        this.success = success;
    }
    
    public boolean isSuccess(){
        return success;
    }
    
    @Override 
    public String toString(){
        return success ? "verificato" : "espulso";
    }
}
