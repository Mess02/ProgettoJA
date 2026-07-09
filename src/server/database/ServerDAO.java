/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import common.CredentialsMessage;
import common.Player;
import common.ResponseMessage;
import common.TYPE;

/**
 * Classe ServerDAO implementa interfaccia DAO 
 * gestisce la connessione con il database e implmenta le query utilizzate dall'applicazione 
 * @author Giuseppe Messalino , Angela Monti 
 */
public class ServerDAO implements DAO{
    private String url = "jdbc:sqlite:data/SystemManagementDB.db";
    private String username = "";
    private String password = ""; 
    
    /**
     * Il metodo verifica le credenziali dell'utente 
     * @param credentials credenziali (username , password) dell'utente da verificare
     * @return true/false a seconda che le credenziali siano giuste / errate
     * @author Giuseppe Messalino
     */
    @Override 
    public ResponseMessage verifyUser(CredentialsMessage credentials){
        String getPassword = null;
        String getType = null;
        TYPE type= null;
        try(Connection c = DriverManager.getConnection(url , username , this.password)){
            PreparedStatement s = c.prepareStatement("SELECT password , type FROM users WHERE username = ?");
            s.setString(1, credentials.getUsername());
            
            ResultSet rs = s.executeQuery();
            if(rs.next()){
                getPassword = rs.getString("password");
                getType = rs.getString("type");                
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(getType.equals("user"))
            type = TYPE.LOGIN;
        else type = TYPE.ADLOGIN;
        
        if(getPassword == null)
            return null;
        return new ResponseMessage(getPassword.equals(credentials.getPassword()) , type);
    }
    
    /**
     * Il metodo restituisce una Lista di tutti i Player presenti in database
     * @return list - lista contenente tutti i player presenti in database
     * @author Giuseppe Messalino
     */
    @Override 
    public List<Player> getAllPlayer(){
        List<Player> list = new ArrayList<>();
        
        try(Connection c = DriverManager.getConnection(url , username , password);
                PreparedStatement s = c.prepareStatement("SELECT * FROM users Where type = 'user'")){
            
            
            ResultSet rs = s.executeQuery();
            while(rs.next())
                list.add(new Player(rs.getString("username")));
        } catch (SQLException ex) {
            Logger.getLogger(ServerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
    /**
     * Metodo che permette di aggiungere un nuovo utente al database
     * @param credentials credenziali dell'utente da registrare 
     * @return true/false a seconda di come sia andato l'inserimento
     * @author Giuseppe Messallino
     */
    @Override 
    public boolean insertUser(CredentialsMessage credentials){
        try(Connection c = DriverManager.getConnection(url , username , password);
                Statement s = c.createStatement()){
            String addUser = String.format("INSERT INTO users VALUES ('%s' , '%s' , '%s')" , credentials.getUsername() , credentials.getPassword() , "user");
            
            s.executeUpdate(addUser);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ServerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Il metodo aggiunge al database le informazioni relative ad un testo 
     * @param title titolo del testo da aggiungere 
     * @param length lunghezza (numero di parole) del testo
     * @param path path del testo 
     * @param analysis path dell'analisi relativa al testo
     * @author Angela Monti
     */

    @Override
    public void insertText(String title, int length, String path, String analysis){
        try(Connection c = DriverManager.getConnection(url, username, password);
        PreparedStatement cmd = c.prepareStatement(
            "INSERT INTO TEXTS(TITLE, LENGTH, PATH, ANALYSIS) VALUES (?,?,?,?)");){
            cmd.setString(1, title);
            cmd.setInt(2, length);
            cmd.setString(3, path);
            cmd.setString(4, analysis);
            cmd.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ServerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getPlayerWin(String username) {
        return 0;
    }

    @Override
    public int getPlayerMatch(String username) {
        return 0;
    }

    @Override
    public double getPlayerResponseTime(String username) {
        return 0;
    }

    @Override
    public void getPlayersMatchHistory(String username) {
        
    }

    @Override
    public void saveChallenge(int matchId, String playerName, float f, float timerResponse) {
        
    }

    @Override
    public int createMatch() {
        return 0;
    }
}