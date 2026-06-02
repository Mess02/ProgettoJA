/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import client.connection.Client;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author sara
 */
public class RegistrazioneController implements Initializable {
    private Client client;
    private String tipo = "registrazione";
    
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Button registramiButton;
    @FXML private Label messaggioLabel;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        registramiButton.setDisable(true);
        
        usernameField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validaRegistrazioneForm();
        });
        
        passwordField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            validaRegistrazioneForm();
        });
        
        
        

    }    
    
    private void validaRegistrazioneForm(){
        boolean ok = !usernameField.getText().trim().isEmpty() &&
                !passwordField.getText().trim().isEmpty();
        registramiButton.setDisable(!ok);
    }
    
    @FXML
    public void tornaAlLogin(ActionEvent event) throws IOException{
        vaiAlLogin();
    }
    
    @FXML
    private void handleRegistramiButton(ActionEvent event) throws IOException{
        String username =usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
       
        
        messaggioLabel.setText("Registrazione in corso . . .");
        registramiButton.setDisable(true);
    }

    private void vaiAlLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/authentication.fxml"));
        Parent root =loader.load();
        
        Stage stage= (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
