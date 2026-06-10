/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import client.Controller;
import common.ChallengeMessage;
import common.ResultMessage;
import client.connection.Client;
import common.AnswerMessage;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class GameController implements Initializable, Controller {
    
    private Client client;
    private int secondiRimasti;
    private Thread timerThread;
    private Timer timer;

    
    @FXML private TextField rispostaTextField;
    @FXML private Label inserisciLabel;
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
    
    public void setClient(Client client) {
        this.client = client;
        client.setController(this);
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
            if(parola.equalsIgnoreCase(cm.getParolaCifrata())){
                t.setFill(Color.RED);
            }
            testoTextFlow.getChildren().add(t);
        }
        
        avviaTimer(cm.getTimer());
    }
    
    private void avviaTimer(int secondi) {
        secondiRimasti = secondi;
    
        timer = new Timer();
    
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (secondiRimasti > 0) {
                    final int secondiAttuali = secondiRimasti;
                    Platform.runLater(() -> {
                        timerLabel.setText("Timer: " + secondiAttuali);
                        if (secondiAttuali <= 10){
                           timerLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                        }
                    });
            
                    secondiRimasti--;
                    
                } else {
                    
                    Platform.runLater(() -> {
                        timerLabel.setText("Timer: 0");
                        timerLabel.setStyle("-fx-text-fill: red;");
                        rispostaTextField.setDisable(true);
                        inviaButton.setDisable(true);
                        statoLabel.setText("Stato: tempo scaduto!");
                    });
                    
                    timer.cancel(); 
                }
            }
        }, 0, 1000); 
    }

    public void mostraRisultato(ResultMessage rm){
        if (timer != null){
            timer.cancel();
        }
        
        rispostaTextField.setDisable(true);
        inviaButton.setDisable(true);
        
        statoLabel.setText("Stato: partita finita!");
        parolaCorrettaLabel.setText("La parola corretta era: "+rm.getParolaCorretta());
    }
    
    @FXML 
    private void inviaRisposta() throws IOException{
        String risposta = rispostaTextField.getText().trim();
        if (!risposta.isEmpty()) {
            try {
                client.sendMessage(new AnswerMessage(risposta));
                rispostaTextField.clear();
                rispostaTextField.setDisable(true);
                inviaButton.setDisable(true);
                statoLabel.setText("Stato: risposta inviata, aspetta...");
            } catch (IOException ex) {
                Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                statoLabel.setText("Errore nell'invio della risposta.");
            }
        }
    }
    
    @FXML 
    private void scriviRisposta() throws IOException{
        inviaRisposta();
    }
}
