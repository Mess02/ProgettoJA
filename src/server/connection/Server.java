/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.connection;

import common.AnswerMessage;
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
import common.TYPE;
import common.WaitingMessage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import javafx.application.Platform;
import server.Challenge;

/**
 * Classe Server gestisce la connesione dei Client, la ricezione e l'invio dei messaggi Server - Client e Client - Server
 * @author Giuseppe Messalino
 */
public class Server extends Thread{
    private List<ConnectedPlayer> players;
    private Map<DIFFICULTY, ConnectedPlayer> inAttesa = new HashMap<>();
    private ServerSocket socket;
    private final ServerDAO serverDAO;
    
    private Map<String, Map<String, Integer>> analisi = new LinkedHashMap<>();
    private Challenge currentChallenge;
    private ObjectOutputStream oosPlayer1;
    private ObjectOutputStream oosPlayer2;    
    
    private Runnable onPlayersChanged;
    
    public Server() throws IOException {
        setSocket();
        
        players = new ArrayList<>();
        serverDAO = new ServerDAO();
        
        this.start();
    }
    
    public void disconnect() throws IOException{
        socket.close();
    }
    
    public void aggiungiAnalisi(String testo, Map<String, Integer> frequenza) {
        analisi.put(testo, frequenza);
    }
    
    public int getNumeroAnalisi() {
        return analisi.size();
    }
    
    public void setOnPlayersChanged(Runnable callback) {
        this.onPlayersChanged = callback;
    }
    
    private void notificaCambiamento() {
        if (onPlayersChanged != null) {
            Platform.runLater(onPlayersChanged);
        }
    }
    
    public void sendMessage(Serializable msg , ObjectOutputStream oos) throws IOException{
        oos.writeObject(msg);
        oos.flush();
    }
    
    private Map.Entry<String, Map<String, Integer>> scegliAnalisi(DIFFICULTY difficolta) {
        if (analisi.isEmpty()) return null;
        
        Map.Entry<String, Map<String, Integer>> scelta = null;
        double targetLunghezza;
        
        if (difficolta == DIFFICULTY.EASY) {
            targetLunghezza = Double.MAX_VALUE;
            for (Map.Entry<String, Map<String, Integer>> entry : analisi.entrySet()) {
                double media = calcolaLunghezzaMedia(entry.getValue());
                if (media < targetLunghezza) {
                    targetLunghezza = media;
                    scelta = entry;
                }
            }
        } else if (difficolta == DIFFICULTY.HARD) {
            targetLunghezza = Double.MIN_VALUE;
            for (Map.Entry<String, Map<String, Integer>> entry : analisi.entrySet()) {
                double media = calcolaLunghezzaMedia(entry.getValue());
                if (media > targetLunghezza) {
                    targetLunghezza = media;
                    scelta = entry;
                }
            }
        } else {
            int indice = analisi.size() / 2;
            int i = 0;
            for (Map.Entry<String, Map<String, Integer>> entry : analisi.entrySet()) {
                if (i == indice) {
                    scelta = entry;
                    break;
                }
                i++;
            }
        }
        
        return scelta;
    }
    
    private double calcolaLunghezzaMedia(Map<String, Integer> frequenza) {
        return frequenza.keySet().stream()
            .mapToInt(String::length)
            .average()
            .orElse(0);
    }

    public void handleMessage(Serializable msg , Socket s , ObjectOutputStream oos) throws IOException , MessageException{
        if(msg instanceof CredentialsMessage){
            CredentialsMessage cm = (CredentialsMessage) msg;
            boolean success = true;
            ResponseMessage response = null;
            
                if(players.contains(new ConnectedPlayer(new Player(cm.getUsername()) , null))) {
                    success = false;
                } else {
                    response = serverDAO.verifyUser(cm);
                }
                
                sendMessage(response , oos);
                
                if(success){
                    players.add(new ConnectedPlayer(new Player(cm.getUsername()), s, oos));
                    notificaCambiamento();
                    System.out.println(players);
                }
                
            }/*else if(cm.getType() == TYPE.REGISTRATION){
                boolean success = serverDAO.insertUser(cm);
                sendMessage(new ResponseMessage(success) , oos);
                
                if(success){
                    players.add(new ConnectedPlayer(new Player(cm.getUsername()) , s, oos));
                    notificaCambiamento(); 
                }   
                System.out.println(players);
            }    
        }*/else if (msg instanceof AnswerMessage) {
            AnswerMessage am = (AnswerMessage) msg;
            
            if(currentChallenge == null){
                return;
            }
            
            String playerName= "";
            for(ConnectedPlayer cp: players){
                if(cp.getSocket().equals(s)){
                    playerName=cp.getPlayer().getUsername();
                    break;
                }
            }
            
            currentChallenge.verifyResponse(am.getRisposta(), playerName, oos);
            
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
                
                Map.Entry<String, Map<String, Integer>> analisiScelta = scegliAnalisi(difficolta);
                
                if (analisiScelta == null) {
                    sendMessage(new WaitingMessage("Nessun file disponibile!"), oos);
                    return;
                }
                
                currentChallenge = new Challenge();
                ChallengeMessage cm = currentChallenge.prepareChallenge(
                    analisiScelta.getValue(),
                    analisiScelta.getKey(),
                    difficolta
                );
                
                if(cm==null){
                    sendMessage(new WaitingMessage("Errore nella preparazione della sfida"), oos);
                    return;
                }
                
                this.oosPlayer1 = oos;
                this.oosPlayer2 = playerAvversario.getOutput();
                
                currentChallenge.startingChallenge(oosPlayer1, oosPlayer2, player.getPlayer().getUsername(), playerAvversario.getPlayer().getUsername(), serverDAO);
    
                sendMessage(cm, oosPlayer1);
                sendMessage(cm, oosPlayer2);
                
            } else {
                inAttesa.put(difficolta, player);
                sendMessage(new WaitingMessage("Aspetta un avversario con la stessa difficoltà . . ."), oos);
            }
            
        } else {
            throw new MessageException("Non è stato possibile leggere correttamente il messaggio");
        }
    }
    
    @Override
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
                            if(msg != null) {
                                handleMessage(msg , s ,oos);
                            }
                        }
                    }catch(MessageException | IOException | ClassNotFoundException ex){
                        System.out.println("Client disconnesso");
                        /* l'iterator serve per non far lanciare l'exception ConcurrentModificationException */
                        String nomeDisconnesso = "";
                        Iterator<ConnectedPlayer> it = players.iterator();
                        while (it.hasNext()) {
                            ConnectedPlayer conn = it.next();
                            if (conn.getSocket().equals(s)) {
                                nomeDisconnesso = conn.getPlayer().getUsername();
                                it.remove();
                            }
                        }
                        
                        notificaCambiamento();
                        
                        if (currentChallenge != null && !currentChallenge.isEndedChallenge()) {
                            try {
                                ObjectOutputStream oosAvversario = (oos == oosPlayer1) ? oosPlayer2 : oosPlayer1;
                                currentChallenge.disconnect(nomeDisconnesso, oosAvversario);
                            } catch (IOException e) {
                                System.out.println("Errore notifica disconnessione.");
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
                } else {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
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
