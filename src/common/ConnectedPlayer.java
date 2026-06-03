/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Mess
 */
public class ConnectedPlayer {
    Player player;
    Socket s;
    
    public ConnectedPlayer(Player player , Socket s){
        this.player = player;
        this.s = s;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Socket getSocket() {
        return s;
    }

    public void setSocket(Socket s) {
        this.s = s;
    }
    
    public ObjectInputStream getInput() throws IOException{
        return (ObjectInputStream) s.getInputStream();
    }
    
    public ObjectOutputStream getOutput() throws IOException{
        return (ObjectOutputStream) s.getOutputStream();
    }  
    
    @Override
    public int hashCode(){
        return player.hashCode();
    }
    
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if(obj == null) return false;
        if(!(obj instanceof ConnectedPlayer)) return false;
        ConnectedPlayer cp = (ConnectedPlayer) obj;
        return this.player.equals(cp.getPlayer());
    }
    
    @Override
    public String toString(){
        return player.toString() + " - " + s;
    }
}
