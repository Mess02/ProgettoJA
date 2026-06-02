/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

/**
 *
 * @author Mess
 */
public class User {
    private String username;
    
    public User(String username){
        this.username = username;
    }
    
    @Override
    public String toString(){
        return username;
    }
}
