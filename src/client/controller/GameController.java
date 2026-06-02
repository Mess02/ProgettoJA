/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import common.ChallengeMessage;
import common.ResultMessage;
import client.connection.Client;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 *
 * @author Sara
 */
public class GameController implements Initializable {
    
    private Client client;
    private int secondiRimasti;
    private Thread timerThread;
    
    @FXML private TextField rispostaTextField;
    @FXML private Label iniserisciLabel;
    @FXML private Button inviaButton;
    @FXML private TextFlow testoTextFlow;
    @FXML private Label statoLabel;
    @FXML private Label timerLabel;
    @FXML private Label parolaCorrettaLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rispostaTextField.setDisable(true);
        inviaButton.setDisable(true);
        statoLabel.setText("Stato: in attesa dell'altro giocatore . . .");
        parolaCorrettaLabel.setText("");
    }    
    
    public void setChallenge(ChallengeMessage cm){
        Platform.runLater(() -> mostraChallenge(cm));
    }
    
    private void mostraChallenge(ChallengeMessage cm){
        statoLabel.setText("Stato: gioco iniziato");
        
        rispostaTextField.setDisable(false);
        inviaButton.setDisable(false);
        
        testoTextFlow.getChildren().clear();
        
        String[] parole = cm.getTesto().split(" ");
        for(String parola : parole){
            Text t = new Text(parola + " ");
            if(parola.equals(cm.getParolaCifrata())){
                t.setFill(Color.RED);
            }
            testoTextFlow.getChildren().add(t);
        }
        
        avviaTimer(cm.getTimer());
    }
    
    private void avviaTimer(int secondi){
        secondiRimasti = secondi;
        
        timerThread = new Thread(() -> {
            while (secondiRimasti > 0) {
                final int secondiAttuali = secondiRimasti;
                Platform.runLater(() -> {
                    timerLabel.setText("Timer: " + secondiAttuali);
                });
                
                secondiRimasti--;
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            Platform.runLater(() -> {
                timerLabel.setText("Timer: 0");
                timerLabel.setStyle("-fx-text-fill: red;");
                rispostaTextField.setDisable(true);
                inviaButton.setDisable(true);
                statoLabel.setText("Stato: tempo scaduto!");
            });
        });
        
        timerThread.setDaemon(true);
        timerThread.start();
    }
    
    private void mostraRisultato(ResultMessage rm){
        if(timerThread != null){
            timerThread.interrupt();
        }
        
        rispostaTextField.setDisable(true);
        inviaButton.setDisable(true);
        
        statoLabel.setText("Stato: partita finita!");
        parolaCorrettaLabel.setText("La parola corretta era: "+rm.getParolaCorretta());
    }
    
    @FXML 
    private void inviaRisposta() throws IOException{
        String risposta=rispostaTextField.getText().trim();
          
        
    }
    
    @FXML 
    private void scriviRisposta() throws IOException{
        inviaRisposta();
    }
}
