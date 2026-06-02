/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.connection;

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
import common.ResponseMessage;
import common.TYPE;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 *
 * @author Mess
 */
public class Server extends Thread{
    private Map<ObjectInputStream , ObjectOutputStream> clients;
    private ServerSocket socket;
    private final ServerDAO serverDAO;
    
    
    public Server() throws IOException {
        setSocket();
        
        clients = new HashMap<>();
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

    public void handleMessage(Serializable msg , ObjectOutputStream oos) throws IOException , MessageException{
        if(msg instanceof CredentialsMessage){
            CredentialsMessage cm = (CredentialsMessage) msg;
            if(cm.getTipo() == TYPE.LOGIN){
                boolean success = serverDAO.verifyUser(cm);
                sendMessage(new ResponseMessage(success) , oos);
            }else if(cm.getTipo() == TYPE.REGISTRATION){
                boolean success = serverDAO.addUser(cm);
                sendMessage(new ResponseMessage(success) , oos);
            }    
        }/* qui vanno aggiunti gli if else () per ogni  messaggio , il throw deve essere l'ultimo else */ 
            else throw new MessageException("Non è stato possibile leggere correttamente il messaggio");
    }
    
    public void run(){
        while(!Thread.currentThread().isInterrupted()){
            try{
                Socket s = socket.accept(); 
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                
                clients.put(ois , oos);
                
                new Thread(() -> {
                    try{
                        while(true){
                            Serializable msg = (Serializable) ois.readObject();
                            if(msg != null)
                                handleMessage(msg , oos);
                        }
                    }catch(Exception ex){
                        System.out.println("Client disconnesso");
                        clients.remove(ois);
                    }
                }).start();
                
            } catch (IOException ex) {
                if(socket.isClosed()){
                    System.out.println("Server chiuso correttamente");
                    for(ObjectInputStream ois : clients.keySet())
                        clients.remove(ois);
                }
                else Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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