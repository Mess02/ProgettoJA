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
public class CredentialsMessage implements Serializable{
    private String username;
    private String password;
    private TYPE type;
    
    public CredentialsMessage(String username , String password, TYPE type){
        this.username = username;
        this.password = password;
        this.type = type;
    }
    
    public String getUsername(){
        return username;
    }
    
    public String getPassword(){
        return password;
    }

    public TYPE getType() {
        return type;
    }
    
}
