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
import common.TYPE;
import server.User;

/**
 *
 * @author Mess
 */
public class ServerDAO implements DAO{
    private String url = "jdbc:sqlite:data/SystemManagementDB.db";
    private String username = "";
    private String password = ""; 
    
    @Override 
    public boolean verifyUser(CredentialsMessage credentials){
        String getPassword = null;
        TYPE type = credentials.getType();
        try(Connection c = DriverManager.getConnection(url , username , this.password)){
            PreparedStatement s;
            
            if(type.equals(TYPE.ADLOGIN)) s = c.prepareStatement("SELECT password FROM users WHERE username = ? and type = 'administrator'");
            else s = c.prepareStatement("SELECT password FROM users WHERE username = ? and type = 'user'");
            
            s.setString(1, credentials.getUsername());
            
            ResultSet rs = s.executeQuery();
            if(rs.next())
                getPassword = rs.getString("password");
            
        } catch (SQLException ex) {
            Logger.getLogger(ServerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(getPassword == null)
            return false;
        return getPassword.equals(credentials.getPassword());
    }
    
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

    @Override 
    public boolean addUser(CredentialsMessage credentials){
        try(Connection c = DriverManager.getConnection(url , username , password);
                Statement s = c.createStatement()){
            String addUser = String.format("INSERT INTO users VALUES ('%s' , '%s' , '%s')" , credentials.getUsername() , credentials.getPassword() , "player");
            
            return s.execute(addUser);
        } catch (SQLException ex) {
            Logger.getLogger(ServerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    // f insert into tabella texts che ha titlee come id principale, lunghezza del test e path e analisis e poi  devo chiamare questa f nel metodo upload file
    
    public void insert(String title, int length, String path, String analysis) throws SQLException{
        try(Connection c = DriverManager.getConnection(url, username, password);
        PreparedStatement cmd = c.prepareStatement(
            "INSERT INTO TEXTS(TITLE, LENGTH, PATH, ANALYSIS) VALUES (?,?,?,?)");){
            cmd.setString(1, title);
            cmd.setInt(2, length);
            cmd.setString(3, path);
            cmd.setString(4, analysis);
            cmd.executeUpdate();
        }
    }

    @Override
    public int getPlayerWin(String username) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getPlayerMatch(String username) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double getPlayerResponseTime(String username) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void getPlayersMatchHistory(String username) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
