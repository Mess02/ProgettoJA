/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import common.ChallengeMessage;
import common.DIFFICULTY;
import common.ResultMessage;
import static common.DIFFICULTY.EASY;
import static common.DIFFICULTY.HARD;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Random;
import server.database.ServerDAO;

/**
 *
 * @author Utente
 */
public class Challenge {
    //voglio far in modo che questa classe prenda i due player e faccia partire il gico
    private String cipherWord;
    private String chosenWord;
    private int shift;
    private final int TIMER = 60;
    
    private long startingTime;
    private int matchId;
    private ServerDAO serverDAO;
    
    private String firstResponse;
    private boolean endedChallenge;
    private ObjectOutputStream oosPlayer1;
    private ObjectOutputStream oosPlayer2;
    private String namePlayer1;
    private String namePlayer2;
    
    private String encryption(String word, int shift){
        word=word.toUpperCase();
        String cipherWord = "";
        int i, j;
            
        for(i=0; i<word.length(); i++){
            if(word.charAt(i)>='A' && word.charAt(i)<='Z'){
                j=(int)word.charAt(i)+shift;
                if(j>90){
                    j-=26;
                }
                cipherWord+=(char)j;
            }
        }
            
        return cipherWord; 
    }
    
    public String chooseWord(Map<String, Integer> frequency, DIFFICULTY difficulty){
        String cipherWord = null;
        int frequencyTarget;
        
        if (null == difficulty) {
            int index = new Random().nextInt(frequency.size());
            int i = 0;
            for (String word : frequency.keySet()) {
                if (i == index) {
                    cipherWord = word;
                    break;
                }
                i++;
            }
        } else switch (difficulty) {
            case EASY:
                frequencyTarget = Integer.MIN_VALUE;
                for (Map.Entry<String, Integer> pair : frequency.entrySet()) {
                    if (pair.getValue() > frequencyTarget && pair.getKey().length() > 3) {
                        frequencyTarget = pair.getValue();
                        cipherWord = pair.getKey();
                    }
                }   break;
            case HARD:
                frequencyTarget = Integer.MAX_VALUE;
                for (Map.Entry<String, Integer> pair : frequency.entrySet()) {
                    if (pair.getValue() < frequencyTarget && pair.getKey().length() > 3) {
                        frequencyTarget = pair.getValue();
                        cipherWord = pair.getKey();
                    }
                }   break;
            default:
                int index = new Random().nextInt(frequency.size());
                int i = 0;
                for (String word : frequency.keySet()) {
                    if (i == index) {
                        cipherWord = word;
                        break;
                    }
                    i++;
                }   break;
        }
        
        return cipherWord;
    }
    
    public ChallengeMessage prepareChallenge(Map<String, Integer> frequency, String text, DIFFICULTY difficulty){
        chosenWord = chooseWord(frequency, difficulty);
        
        if(chosenWord == null){
            System.out.println("nessuna parola trovata");
            return null;
        }
        
        shift = difficulty.getShift();
        cipherWord = encryption(chosenWord, shift);
        String modifiedText = text.replaceAll("(?i)" + chosenWord, cipherWord);
        
        System.out.println("Parola scelta: " + chosenWord);
        System.out.println("Parola cifrata: " + cipherWord);
        System.out.println("Shift: " + shift);
        
        return new ChallengeMessage(cipherWord, modifiedText, TIMER);
    }
    
    public void verifyResponse(String response, String playerName, ObjectOutputStream oos) throws IOException{
        System.out.println("verifyResponse chiamato da: " + playerName + " risposta: " + response);
        System.out.println("oosPlayer1: " + oosPlayer1 + " oosPlayer2: " + oosPlayer2);
        System.out.println("oos corrente: " + oos);
        System.out.println("oos == oosPlayer1: " + (oos == oosPlayer1));
        System.out.println("oos == oosPlayer2: " + (oos == oosPlayer2));
        
        if(endedChallenge){
            return;
        }
        
        ObjectOutputStream oosOpponent = (oos == oosPlayer1) ? oosPlayer2 : oosPlayer1;
        String nameOpponent = playerName.equals(namePlayer1) ? namePlayer2 : namePlayer1;
        
        if(!response.isEmpty() && response.equalsIgnoreCase(chosenWord)){
            endedChallenge=true;
            firstResponse=null;
            
            float timerResponse = calculateTime();
            serverDAO.saveChallenge(matchId, playerName, 1.0f, timerResponse);   
            serverDAO.saveChallenge(matchId, nameOpponent, 0.0f, TIMER);
            
            sendMessage(new ResultMessage(chosenWord, "hai vinto!"), oos);
            sendMessage(new ResultMessage(chosenWord, playerName + " ha vinto! Hai perso"), oosOpponent);
        }else{
            float timerResponse=response.isEmpty() ? TIMER : calculateTime();
            
            String messageSender = response.isEmpty() ? "tempo Scaduto! Hai perso!" : "Risposta sbagliata! Aspetta l'altro. . .";
            
            if(firstResponse == null){
                firstResponse = playerName;
                sendMessage(new ResultMessage(chosenWord, messageSender), oos);
                serverDAO.saveChallenge(matchId, playerName, 0.0f, timerResponse);
            }else{
                endedChallenge = true;
                firstResponse = null;
                
                String messageOpponent = response.isEmpty() ? "Tempo scaduto! Hai perso." : "L'avversario ha sbagliato! Pareggio.";
                serverDAO.saveChallenge(matchId, playerName, 0.0f, timerResponse);
                sendMessage(new ResultMessage(chosenWord, messageSender), oos);
                sendMessage(new ResultMessage(chosenWord, messageOpponent), oosOpponent);
            }
        }
    }
    
    private void sendMessage(ResultMessage msg, ObjectOutputStream oos) throws IOException {
        oos.writeObject(msg);
        oos.flush();
    }
    
    public void startingChallenge(ObjectOutputStream oos1, ObjectOutputStream oos2, String name1, String name2, ServerDAO dao){
        this.oosPlayer1=oos1;
        this.namePlayer1=name1;
        this.oosPlayer2=oos2;
        this.namePlayer2=name2;
        
        this.firstResponse = null;
        this.endedChallenge = false;
        this.startingTime=System.currentTimeMillis();
        this.serverDAO=dao;
        this.matchId=serverDAO.createMatch();
        
        System.out.println("Partita creata nel DB con ID: " + matchId);
    }
    
    private float calculateTime() {
        return (System.currentTimeMillis() - startingTime) / 1000f;
    }
    
    public void disconnect(String disconnectedPlayerName, ObjectOutputStream oosOpponent) throws IOException{
        if(endedChallenge){
            return;
        }
        
        endedChallenge = true;
        serverDAO.saveChallenge(matchId, disconnectedPlayerName, 0.0f, TIMER);
        String nameOpponent = disconnectedPlayerName.equals(namePlayer1) ? namePlayer2 : namePlayer1;
        serverDAO.saveChallenge(matchId, nameOpponent, 1.0f, calculateTime());
        
        sendMessage(new ResultMessage(chosenWord, disconnectedPlayerName +" si è discnnesso! Hai vinto"), oosOpponent);
    }
    
    public String getCorrectWord() {
        return chosenWord;
    }

    public boolean isEndedChallenge() {
        return endedChallenge;
    }
}
