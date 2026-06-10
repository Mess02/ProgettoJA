/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import static common.DIFFICULTY.EASY;
import static common.DIFFICULTY.HARD;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Utente
 */
public class Challenge {
    private String parolaCifrata;
    private String parolaScelta;
    private int shift;
    private final int TIMER = 60;
    
    private String cifra(String parola, int shift){
        parola=parola.toUpperCase();
        String parolaCifrata = "";
        int i, j;
            
        for(i=0; i<parola.length(); i++){
            if(parola.charAt(i)>='A' && parola.charAt(i)<='Z'){
                j=(int)parola.charAt(i)+shift;
                if(j>90){
                    j-=26;
                }
                parolaCifrata+=(char)j;
            }
        }
            
        return parolaCifrata; 
    }
    
    public String scegliParola(Map<String, Integer> frequenza, DIFFICULTY difficolta){
        String parolaScelta = null;
        int frequenzaTarget;
        
        if (difficolta == EASY) {
            frequenzaTarget = Integer.MIN_VALUE;
            
            for (Map.Entry<String, Integer> coppia : frequenza.entrySet()) {
                if (coppia.getValue() > frequenzaTarget && coppia.getKey().length() > 3) {
                    frequenzaTarget = coppia.getValue();
                    parolaScelta = coppia.getKey();
                }
            }
            
        } else if (difficolta == HARD) {
            frequenzaTarget = Integer.MAX_VALUE;
            
            for (Map.Entry<String, Integer> coppia : frequenza.entrySet()) {
                if (coppia.getValue() < frequenzaTarget && coppia.getKey().length() > 3) {
                    frequenzaTarget = coppia.getValue();
                    parolaScelta = coppia.getKey();
                }
            }
            
        } else {
            // difficoltà media → parola a caso
            int indice = new Random().nextInt(frequenza.size());
            int i = 0;
            for (String parola : frequenza.keySet()) {
                if (i == indice) {
                    parolaScelta = parola;
                    break;
                }
                i++;
            }
        }
        
        return parolaScelta;
    }
    
    public ChallengeMessage prepara(Map<String, Integer> frequenza, String testo, DIFFICULTY difficolta){
        parolaScelta = scegliParola(frequenza, difficolta);
        
        if(parolaScelta == null){
            System.out.println("nessuna parola trovata");
            return null;
        }
        
        shift = difficolta.getShift();
        
        parolaCifrata = cifra(parolaScelta, shift);
        
        String testoModificato = testo.replace(parolaScelta, parolaCifrata);
        
        System.out.println("Parola scelta: " + parolaScelta);
        System.out.println("Parola cifrata: " + parolaCifrata);
        System.out.println("Shift: " + shift);
        
        return new ChallengeMessage(parolaCifrata, testoModificato, TIMER);
    }
    
    public String getParolaCorretta() {
        return parolaScelta;
    }
}
