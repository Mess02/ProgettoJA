/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import client.Controller;
import common.ChallengeMessage;
import client.connection.Client;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author sara
 */
public class MenuController implements Initializable , Controller{
    private Client client;
    
    @FXML private AnchorPane menuPane;
    @FXML private Button nuovaPartitaButton;
    @FXML private TableView risultatiTableView;
    @FXML private TableColumn dataTableColumn;
    @FXML private TableColumn difficoltaTableColumn;
    @FXML private TableColumn punteggioTableColumn;
    @FXML private ComboBox difficoltaCombo;
    @FXML private Label statoLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        difficoltaCombo.getItems().addAll("Facile", "Medio", "Difficile");
        difficoltaCombo.getSelectionModel().selectFirst();
    
    }    
    
    public void setClient(Client client){
        this.client = client;
    }
    
    @FXML
    private void nuovaPartita(){
        nuovaPartitaButton.setDisable(true);
        statoLabel.setText("In attesa dell'altro giocatore . . . ");
    }
  
    public void vaiAlGioco(ChallengeMessage cm) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/common/fxml/Game.fxml"));
        Parent root= loader.load();
        
        GameController gc = loader.getController();
        
        gc.setChallenge(cm);
        
        Stage stage= (Stage) menuPane.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show(); 
    }
    
}
