/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.connection;

import client.Controller;
import common.ResponseMessage;
import client.controller.AuthenticationController;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author Mess
 */
public class Client extends Thread{
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    
    private Controller controller;
    
    public Client() throws IOException{
        setClient();
        
        this.oos = new ObjectOutputStream(this.socket.getOutputStream());
        this.ois = new ObjectInputStream(this.socket.getInputStream());   
        
        this.start();
    }
    
    public void disconnect() throws IOException{
        this.socket.close();
    }
    
    public void handleMessage(Serializable msg) throws IOException{
        if(msg instanceof ResponseMessage){
            ResponseMessage rm = (ResponseMessage) msg;
            if(rm.isSuccess()){
                Platform.runLater(()->{try {
                    ((AuthenticationController)controller).vaiAlMenu();
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        }
    }
    
    public void sendMessage(Serializable msg) throws IOException{
        this.oos.writeObject(msg);
        oos.flush();
    }
    
    public void run(){
        while(true){
            try {
                Serializable msg = (Serializable) ois.readObject();
                handleMessage(msg);
            } catch (IOException | ClassNotFoundException ex) {
                if(socket.isClosed())
                    System.out.println("Il server ha chiuso la connessione");
                else try {
                    socket.close();
                    System.out.println("Il server ha chiuso la connessione");
                } catch (IOException ex1) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
                }
                break;
            }
        }
    }
    
    private void setClient(){
        Path path = Paths.get("client.properties");
        try(InputStream input = Files.newInputStream(path)){
            Properties properties = new Properties();
            properties.load(input);
            
            String ip = properties.getProperty("server.ip");
            int port = Integer.parseInt(properties.getProperty("server.port"));
            
            this.socket = new Socket(ip , port);
            System.out.println("client avviato sulla porta " + port + " all'indirizzo " + ip);
        } catch (IOException ex){
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public void setController(Controller controller){
        this.controller = controller;
    }
}
