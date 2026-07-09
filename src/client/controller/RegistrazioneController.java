/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import client.Controller;
import client.connection.Client;
import common.CredentialsMessage;
import common.TYPE;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author sara
 */
public class RegistrazioneController implements Initializable, Controller {
    private Client client;
    
    @FXML private AnchorPane RegistrazionePane;
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
    
    public void setClient(Client client){
        this.client=client;
        client.setController((Controller) this);
    }
    
    @FXML
    private void handleRegistramiButton(ActionEvent event) throws IOException{
        String username =usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if(username.isEmpty() || password.isEmpty()){
            messaggioLabel.setText("inserisci username e password");
            return;
        }
        
        messaggioLabel.setText("Registrazione in corso . . .");
        registramiButton.setDisable(true);
        
       // client.sendMessage(new CredentialsMessage(username, password, TYPE.REGISTRATION));
        
    }

    public void vaiAlMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/fxml/Menu.fxml"));
        Parent root =loader.load();
        
        MenuController mc = loader.getController();
        mc.setClient(client);
        
        Stage stage= (Stage) RegistrazionePane.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void mostraTesto(String testo) {
        messaggioLabel.setText(testo);
        registramiButton.setDisable(false);
    }
}
