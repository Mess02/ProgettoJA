/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package server.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import server.database.ServerDAO;

/**
 * FXML Controller class
 *
 * @author Mess
 */
public class PlayerViewController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private Label matchLabel;
    @FXML private Label winLabel;
    @FXML private Label timeLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }    
    
    public void init(String username){
        ServerDAO s = new ServerDAO();
        usernameLabel.setText(username);
        
        matchLabel.setText("Partite giocate: " + s.getPlayerMatch(username));
        winLabel.setText("Partite vinte: " + s.getPlayerWin(username));
        timeLabel.setText("Tempo medio: " + String.format("%.2f", s.getPlayerResponseTime(username)) + " sec");
    }
    
}
