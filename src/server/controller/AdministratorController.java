/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package server.controller;

import client.connection.Client;
import common.ConnectedPlayer;
import common.Player;
import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import server.connection.Server;
import server.database.ServerDAO;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
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

/**
 * FXML Controller class
 *
 * @author Mess
 */
public class AdministratorController implements Initializable {
    private FileChooser fileChooser;
    private Server server;
    private LinkedHashMap<String, Integer> frequenza;
    private String testo;
    
    @FXML private Button uploadFileButton;
    @FXML private ListView<String> fileList;
    @FXML private Label statoLabel;
    @FXML private ListView<Player> clientList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ServerDAO s = new ServerDAO();
        ObservableList<Player> list = FXCollections.observableArrayList(s.getAllPlayer());
        clientList.setItems(list);
        for(Player p : list) {
            System.out.println(s.getPlayerMatch(p.getUsername()));
        }
        
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
            if (event.getClickCount() == 2) {
                Player p = clientList.getSelectionModel().getSelectedItem();
                if (p != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/server/fxml/PlayerView.fxml"));
                        Parent root = loader.load();
                        PlayerViewController c = loader.getController();
                        c.init(p.getUsername());
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException ex) {
                        Logger.getLogger(AdministratorController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        aggiornaListaFile();
    }    
    
    public void setServer(Server server){
        this.server=server;
        server.setOnPlayersChanged(() -> aggiornaListaConnessi());
        analizzaIFile();
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
        salvaAnalisi("file/" + file.getName().replace(".txt", ".bin"));
        server.aggiungiAnalisi(testo, frequenza);
        
        statoLabel.setText("File caricato: " + file.getName() + " — totale: " + server.getNumeroAnalisi() + " file");
    }
    
    private void analizzaIFile() {
        File cartella = new File("file/");
        if (!cartella.exists()) return;
        
        File[] files = cartella.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            statoLabel.setText("Nessun file TXT trovato in file/");
            return;
        }
        
        for (File f : files) {
            String nomeBin = f.getName().replace(".txt", ".bin");
            File fileBin = new File("file/" + nomeBin);
            
            if (fileBin.exists()) {
                try {
                    frequenza = (LinkedHashMap<String, Integer>) loadMap("file/" + nomeBin);
                    
                    StringBuilder sb = new StringBuilder();
                    try (BufferedReader r = Files.newBufferedReader(f.toPath())) {
                        String line;
                        while ((line = r.readLine()) != null)
                            sb.append(line).append(" ");
                    }
                    testo = sb.toString();
                    System.out.println("Analisi caricata: " + nomeBin);
                    
                } catch (Exception ex) {
                    Logger.getLogger(AdministratorController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                analyze(f);
                salvaAnalisi("file/" + nomeBin);
                System.out.println("Analisi calcolata e salvata: " + nomeBin);
            }
            
            server.aggiungiAnalisi(testo, frequenza);
        }
    }
    
    private void aggiornaListaFile(){
        File cartella = new File("file/");
        if(cartella.exists()){
            ObservableList<String> files = FXCollections.observableArrayList();
            for(File f: cartella.listFiles()){
                if (f.isFile() && f.getName().endsWith(".txt")) {
                    files.add(f.getName());
                }
            }
            fileList.setItems(files);
        }
    }

    @FXML
    private void disconnect(ActionEvent event) throws IOException {
        server.disconnect();
        statoLabel.setText("Server disconnesso.");
    }
    
    private void analyze(File filename){
        
        Path stopwords = Paths.get("file/StopWords/stopwords-it.txt");
        List<String> stop = new ArrayList<>();
        List<String> text = new ArrayList<>();
        
        try(BufferedReader r = Files.newBufferedReader(stopwords)){
            String line;
            while((line = r.readLine()) != null)
                stop.add(line);
        } catch (IOException ex) {
            Logger.getLogger(AdministratorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        StringBuilder sb= new StringBuilder();
        try(BufferedReader r = Files.newBufferedReader(filename.toPath())){
            String line;
            while((line = r.readLine()) != null){
                sb.append(line).append(" ");
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
        
        testo=sb.toString();
        
        text = text.stream().filter(p -> !stop.contains(p)).collect(Collectors.toList());
        
        Map<String, Integer> mappa = new LinkedHashMap<>();               
        
        frequenza = new LinkedHashMap<>();
        for (String s : text) {
            if (frequenza.containsKey(s))
                frequenza.put(s, frequenza.get(s) + 1);
            else
                frequenza.put(s, 1);
        }
        
    }
    
    private void salvaAnalisi(String path) {
        try (OutputStream out = Files.newOutputStream(Paths.get(path),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             BufferedOutputStream bos = new BufferedOutputStream(out);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(frequenza);
        } catch (IOException ex) {
            Logger.getLogger(AdministratorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private LinkedHashMap<String, Integer> loadMap(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(Paths.get(filename))))) {
            return (LinkedHashMap<String, Integer>) ois.readObject();
        }
    }
    
    private void aggiornaListaConnessi() {
        if (server != null) {
            Platform.runLater(() -> {
                ServerDAO s = new ServerDAO();
                ObservableList<Player> tutti = FXCollections.observableArrayList(s.getAllPlayer());
                clientList.setItems(tutti);
                statoLabel.setText("Server avviato — client connessi: " + server.getPlayers().size());
            });
        }
    }
    
    
    
    public void setClient(Client client){
        
    }
}
