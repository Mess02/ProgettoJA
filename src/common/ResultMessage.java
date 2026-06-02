/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.Serializable;

/**
 *
 * @author Sara
 */
public class ResultMessage implements Serializable{
    private String parolaCorretta;
    private String esito;

    public ResultMessage(String parolaCorretta, String esito) {
        this.parolaCorretta = parolaCorretta;
        this.esito = esito;
    }

    public String getEsito() {
        return esito;
    }
    
    public String getParolaCorretta(){
        return parolaCorretta;
    }

    @Override
    public String toString() {
        return esito;
    }
    
    
}
