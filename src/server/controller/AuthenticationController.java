/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package server.controller;

import common.CredentialsMessage;
import common.TYPE;
import server.database.ServerDAO;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import server.connection.Server;

/**
 * FXML Controller class
 *
 * @author Mess
 */
public class AuthenticationController implements Initializable {
    
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messaggioLabel;
    
    @FXML
    private Button signInButton;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO      
        signInButton.disableProperty().bind(usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty()));
        try {
            Server server = new Server();
        } catch (IOException ex) {
            Logger.getLogger(AuthenticationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    

    @FXML
    public void signIn(ActionEvent event) throws IOException {
    /*    String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        ServerDAO serverDao = new ServerDAO();
        
        if(serverDao.verifyUser(new CredentialsMessage(username , password, TYPE.ADLOGIN))){
            System.out.println("\n Amministratore autenticato \n");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/server/fxml/AdministratorView.fxml"));
            Parent root = loader.load();
            
            Server server = new Server(); 
            AdministratorController ac = loader.getController();
            ac.setServer(server); 

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }else{
            messaggioLabel.setText("Credenziali errate");
        }*/
    }
}

