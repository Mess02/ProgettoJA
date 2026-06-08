/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

/**
 *
 * @author Mess
 */
public class Player {
    private String username;
    
    public Player(String username){
        this.username = username;
    }
    
    public String getUsername(){
        return username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    
    @Override
    public int hashCode(){
        return username.hashCode();
    }
    
    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof Player))
            return false;
        Player p = (Player) obj;
        return this.username.equals(p.getUsername());
    }
    
    @Override
    public String toString(){
        return username;
    }
    
}