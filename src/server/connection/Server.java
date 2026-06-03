/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.connection;

import common.ConnectedPlayer;
import common.exceptions.MessageException;
import server.database.ServerDAO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import common.CredentialsMessage;
import common.Player;
import common.ResponseMessage;
import common.TYPE;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Mess
 */
public class Server extends Thread{
    private List<ConnectedPlayer> players;
    private ServerSocket socket;
    private final ServerDAO serverDAO;
    
    
    public Server() throws IOException {
        setSocket();
        
        players = new ArrayList<>();
        serverDAO = new ServerDAO();
        
        this.start();
    }
    
    public void disconnect() throws IOException{
        this.socket.close();
    }
    
    public void broadcast(Serializable msg) throws IOException{
        /* qua il messaggio broadcast deve essere mandato in broadcast ai player che fanno parte della stessa partita (in teoria)
        */
    }
    
    public void sendMessage(Serializable msg , ObjectOutputStream oos) throws IOException{
        oos.writeObject(msg);
        oos.flush();
    }

    public void handleMessage(Serializable msg , Socket s , ObjectOutputStream oos) throws IOException , MessageException{
        if(msg instanceof CredentialsMessage){
            CredentialsMessage cm = (CredentialsMessage) msg;
            if(cm.getTipo() == TYPE.LOGIN){
                boolean success = serverDAO.verifyUser(cm);
                sendMessage(new ResponseMessage(success) , oos);
                
                if(success) players.add(new ConnectedPlayer(new Player(cm.getUsername()) , s));
                System.out.println(players);
                
            }else if(cm.getTipo() == TYPE.REGISTRATION){
                boolean success = serverDAO.addUser(cm);
                sendMessage(new ResponseMessage(success) , oos);
                
                if(success) players.add(new ConnectedPlayer(new Player(cm.getUsername()) , s));   
                System.out.println(players);
            }    
        }/* qui vanno aggiunti gli if else () per ogni  messaggio , il throw deve essere l'ultimo else */ 
            else throw new MessageException("Non è stato possibile leggere correttamente il messaggio");
    }
    
    public void run(){
        while(true){
            try{
                Socket s = socket.accept(); 
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                
                new Thread(() -> {
                    try{
                        while(true){
                            Serializable msg = (Serializable) ois.readObject();
                            if(msg != null)
                                handleMessage(msg , s ,oos);
                        }
                    }catch(Exception ex){
                        System.out.println("Client disconnesso");
                        /* l'iterator serve per non far lanciare l'exception ConcurrentModificationException */
                        Iterator<ConnectedPlayer> it = players.iterator();
                        while (it.hasNext()) {
                            ConnectedPlayer conn = it.next();
                            if (conn.getSocket().equals(s)) {
                                it.remove();
                            }
                        }
                        System.out.println(players);
                    }
                }).start();
                
            } catch (IOException ex) {
                if(socket.isClosed()){
                    System.out.println("Server chiuso correttamente");
                    for(ConnectedPlayer cp : players)
                        try {
                            cp.getSocket().close();
                        } catch (IOException ex1) {}
                    players.clear();
                } else Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }
    
    private void setSocket(){
        Path path = Paths.get("server.properties");
        try(InputStream input = Files.newInputStream(path)){
            Properties properties = new Properties();
            properties.load(input);
            int port = Integer.parseInt(properties.getProperty("server.port"));
            
            this.socket = new ServerSocket(port);
            System.out.println("Server avviato sulla porta " + port);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}