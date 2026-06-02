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
        try(Connection c = DriverManager.getConnection(url , username , this.password);
                PreparedStatement s = c.prepareStatement("SELECT password FROM User WHERE username = ?")){
            
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
    public List<User> getAllPlayer(){
        List<User> list = new ArrayList<>();
        
        try(Connection c = DriverManager.getConnection(url , username , password);
                PreparedStatement s = c.prepareStatement("SELECT * FROM User Where role = 'player'")){
            
            
            ResultSet rs = s.executeQuery();
            while(rs.next())
                list.add(new User(rs.getString("username")));
        } catch (SQLException ex) {
            Logger.getLogger(ServerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    @Override 
    public boolean addUser(CredentialsMessage credentials){
        try(Connection c = DriverManager.getConnection(url , username , password);
                Statement s = c.createStatement()){
            String addUser = String.format("INSERT INTO User VALUES ('%s' , '%s' , '%s')" , credentials.getUsername() , credentials.getPassword() , "player");
            
            return s.execute(addUser);
        } catch (SQLException ex) {
            Logger.getLogger(ServerDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
