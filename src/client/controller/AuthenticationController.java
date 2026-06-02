/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package client.controller;

import client.Controller;
import common.TYPE;
import common.CredentialsMessage;
import client.connection.Client;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mess
 */
public class AuthenticationController implements Initializable , Controller {
    private Client client;
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messaggioLabel;
    @FXML private Button loginButton;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            client = new Client();
        } catch (IOException ex) {
            Logger.getLogger(AuthenticationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        client.setController(this);
    }    

    @FXML
    private void signIn(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty() || password.isEmpty()){
            messaggioLabel.setText("inserisci username e password");
        }
        
        client.sendMessage(new CredentialsMessage(username, password, TYPE.LOGIN));
    }

    @FXML
    private void signUp(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/fxml/registrazione.fxml"));
        Parent root= loader.load();
        
        Stage stage= (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void vaiAlMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/fxml/Menu.fxml"));
        Parent root= loader.load();
        
        MenuController mc = loader.getController();
        mc.setClient(client);
        
        Stage stage= (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        
        /* quest'operazione qua va fatta ad ogni stage */ 
        stage.setOnCloseRequest(value -> {
            System.out.println("Sto chiudendo lo stage ...");
            try {
                client.disconnect();
            } catch (IOException ex) {
                System.out.println("Client disconnesso con successo");
            }
        });
    }
}
