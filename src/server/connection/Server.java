/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.connection;

import common.AnswerMessage;
import common.Challenge;
import common.ChallengeMessage;
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
import common.DIFFICULTY;
import common.Player;
import common.RequestGameMessage;
import common.ResponseMessage;
import common.ResultMessage;
import common.TYPE;
import common.WaitingMessage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Classe Server gestisce la connesione dei Client, la ricezione e l'invio dei messaggi Server - Client e Client - Server
 * @author Giuseppe Messalino
 */
public class Server extends Thread{
    private List<ConnectedPlayer> players;
    private Map<DIFFICULTY, ConnectedPlayer> inAttesa = new HashMap<>();
    private ServerSocket socket;
    private final ServerDAO serverDAO;
    
    private String parolaCorretta;
    private Map<String, Integer> frequenza;
    private String testo;
    private String primaRisposta;
    
    public Server() throws IOException {
        setSocket();
        
        players = new ArrayList<>();
        serverDAO = new ServerDAO();
        primaRisposta=null;
        
        this.start();
    }
    
    public void disconnect() throws IOException{
        socket.close();
    }
    
    public void broadcast(Serializable msg  /*,lista di due giocatori */) throws IOException{
        for (ConnectedPlayer cp : players) {
            ObjectOutputStream oos = new ObjectOutputStream(cp.getSocket().getOutputStream());
            oos.writeObject(msg);
            oos.flush();
        }
    }
    
    public void setPartita(Map<String, Integer> frequenza, String testo) {
        this.frequenza = frequenza;
        this.testo = testo;
    }
    
    public void sendMessage(Serializable msg , ObjectOutputStream oos) throws IOException{
        oos.writeObject(msg);
        oos.flush();
    }

    public void handleMessage(Serializable msg , Socket s , ObjectOutputStream oos) throws IOException , MessageException{
        if(msg instanceof CredentialsMessage){
            CredentialsMessage cm = (CredentialsMessage) msg;
            if(cm.getType() == TYPE.LOGIN){
                boolean success;
                
                if(players.contains(new ConnectedPlayer(new Player(cm.getUsername()) , null))) success = false;
                else success = serverDAO.verifyUser(cm);
                
                sendMessage(new ResponseMessage(success) , oos);
                
                if(success){
                    players.add(new ConnectedPlayer(new Player(cm.getUsername()), s));
                    System.out.println(players);
                    
                    if (players.size() == 1)
                        sendMessage(new WaitingMessage("Aspetta l'altro giocatore . . ."), oos);
                    
                    if (players.size() == 2)
                        System.out.println("Entrambi i player sono connessi!");
                }
                
            }else if(cm.getType() == TYPE.REGISTRATION){
                boolean success = serverDAO.insertUser(cm);
                sendMessage(new ResponseMessage(success) , oos);
                
                if(success) players.add(new ConnectedPlayer(new Player(cm.getUsername()) , s));   
                System.out.println(players);
            }    
        }else if (msg instanceof AnswerMessage) {
            AnswerMessage am = (AnswerMessage) msg;
            
            String playerName = "";
            for (ConnectedPlayer cp : players) {
                if (cp.getSocket().equals(s)) {
                    playerName = cp.getPlayer().getUsername();
                    break;
                }
            }
            
            if (primaRisposta == null) {
                primaRisposta = am.getRisposta();
                
                if (primaRisposta.equalsIgnoreCase(parolaCorretta)) {
                    sendMessage(new ResultMessage(parolaCorretta, "Hai vinto!"), oos);
                    broadcast(new ResultMessage(parolaCorretta, playerName + " ha vinto!"));
                } else {
                    sendMessage(new ResultMessage(parolaCorretta, "Risposta sbagliata!"), oos);
                }
            } else {
                sendMessage(new ResultMessage(parolaCorretta, "Troppo tardi!"), oos);
            }
            
        } else if (msg instanceof RequestGameMessage) {
            RequestGameMessage rg = (RequestGameMessage) msg;
            DIFFICULTY difficolta = rg.getDifficolta();
            
            ConnectedPlayer player = null;
            for (ConnectedPlayer cp : players) {
                if (cp.getSocket().equals(s)) {
                    player = cp;
                    break;
                }
            }
            
            if (inAttesa.containsKey(difficolta)) {
                ConnectedPlayer playerAvversario = inAttesa.get(difficolta);
                inAttesa.remove(difficolta);
                
                Challenge challenge = new Challenge();
                ChallengeMessage cm = challenge.prepara(frequenza, testo, difficolta);
                this.parolaCorretta = challenge.getParolaCorretta();
                this.primaRisposta = null;
                
                sendMessage(cm, oos);
                ObjectOutputStream oosAvversario = new ObjectOutputStream(
                    playerAvversario.getSocket().getOutputStream()
                );
                sendMessage(cm, oosAvversario);
                
            } else {
                inAttesa.put(difficolta, player);
                sendMessage(new WaitingMessage("Aspetta un avversario con la stessa difficoltà . . ."), oos);
            }
            
        } else {
            throw new MessageException("Non è stato possibile leggere correttamente il messaggio");
        }
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
    
    public List<ConnectedPlayer> getPlayers() {
        return players;
    }
}