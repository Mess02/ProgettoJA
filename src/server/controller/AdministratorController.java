/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package server.controller;

import common.Player;
import java.awt.Desktop;
import java.io.BufferedReader;
import server.connection.Server;
import server.database.ServerDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import server.User;

/**
 * FXML Controller class
 *
 * @author Mess
 */
public class AdministratorController implements Initializable {
    private FileChooser fileChooser;
    private Server server;
    
    @FXML private Button uploadFileButton;
    @FXML private ListView<String> fileList;
    @FXML private Button analysisButton;
    @FXML private Button uploadAnalysisButton;
    @FXML private Label statoLabel;
    @FXML private Button avviaServerButton;
    @FXML private ListView<Player> clientList;
    @FXML private Button avviaPartitaButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ServerDAO s = new ServerDAO();
        ObservableList<Player> list = FXCollections.observableArrayList(s.getAllPlayer());
        clientList.setItems(list);
        for(Player p : list)
            System.out.println(s.getPlayerMatch(p.getUsername()));
        
        avviaPartitaButton.setDisable(true);
        analysisButton.setDisable(true);
        
        fileChooser = new FileChooser();
        fileChooser.setTitle("Scegli un file!");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File TXT" , "*.txt"));
        
        fileList.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                String filename = fileList.getSelectionModel().getSelectedItem();
                if(filename != null){
                    File file = new File("file/" + filename);
                    try{
                        Desktop.getDesktop().open(file);
                    }catch(IOException ex){
                        System.err.println(ex);
                    }
                }
            }
                    
        });
        
        clientList.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                String username = clientList.getSelectionModel().getSelectedItem().getUsername();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/server/fxml/PlayerView.fxml"));
                    Parent root = loader.load();
                    PlayerViewController c = loader.getController();
                    c.init(username);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException ex) {
                    Logger.getLogger(AdministratorController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }});
        
        aggiornaListaFile();
    }    

    @FXML
    private void uploadFile(ActionEvent event) throws IOException{
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
                
        if(file == null) return;
        
        Path source = file.toPath();
        Path destination = Paths.get("file/" + file.getName());
        
        Files.copy(source , destination , StandardCopyOption.REPLACE_EXISTING);
        
        statoLabel.setText("FileCaricato: " + file.getName());
        aggiornaListaFile();
        
        
        analyze(file);

    }

    @FXML
    private void analiyeText(ActionEvent event) {
        String fileSelezionato = fileList.getSelectionModel().getSelectedItem();
        
        if(fileSelezionato == null){
            statoLabel.setText("Seleziona un file da analizzare");
            return;
        }
        
        statoLabel.setText("Analisi di "+ fileSelezionato + "in corso . . .");
    }

    @FXML
    private void uploadAnalysis(ActionEvent event) {
        statoLabel.setText("analisi caricata");
        avviaPartitaButton.setDisable(false);
    }
    
    private void aggiornaListaFile(){
        File cartella = new File("file/");
        if(cartella.exists()){
            ObservableList<String> files = FXCollections.observableArrayList();
            for(File f: cartella.listFiles()){
                if(f.getName().endsWith(".txt")){
                    files.add(f.getName());
                }
            }
            fileList.setItems(files);
        }
    }
    
    @FXML
    private void avviaServer(ActionEvent event){
        try{
            server = new Server();
            statoLabel.setText("Server avviato! in attesa dei client . . .");
            avviaServerButton.setDisable(true);
        }catch(Exception e){
            statoLabel.setText("Errrore nell'avvio del server");
            System.out.println("Errore: "+ e.getMessage());
        }
    }
    
    @FXML
    private void avviaPartita(ActionEvent event){
        String fileSelezionato = fileList.getSelectionModel().getSelectedItem();
        
        if(fileSelezionato == null){
            statoLabel.setText("Seleziona un testo prima di avviare la partita");
            return;
        }
        
        statoLabel.setText("Partita avviata");
    }

    @FXML
    private void disconnect(ActionEvent event) throws IOException {
        server.disconnect();
    }
    
    private void analyze(File filename){
        
        Path stopwords = Paths.get("file/stopwords-it.txt");
        List<String> stop = new ArrayList<>();
        
        List<String> text = new ArrayList<>();
        try(BufferedReader r = Files.newBufferedReader(stopwords)){
            String line;
            while((line = r.readLine()) != null)
                stop.add(line);
        } catch (IOException ex) {
            Logger.getLogger(AdministratorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try(BufferedReader r = Files.newBufferedReader(filename.toPath())){
            String line;
            while((line = r.readLine()) != null){
                Arrays.stream(line.split(" "))
                        .map(String :: trim)
                        .map(String :: toLowerCase)
                        .filter(s -> !s.isEmpty())
                        .map(s -> s.replaceAll("[à]" , "a"))
                        .map(s -> s.replaceAll("[èé]" , "e"))
                        .map(s -> s.replaceAll("[ì]" , "i"))
                        .map(s -> s.replaceAll("[ò]" , "o"))
                        .map(s -> s.replaceAll("[ù]" , "u"))
                        .map(s -> s.replaceAll("[^a-z]" , ""))
                        .forEach(text :: add);
            }
        } catch (IOException ex) {
            Logger.getLogger(AdministratorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        text = text.stream().filter(p -> !stop.contains(p)).collect(Collectors.toList());
        
    }
}

