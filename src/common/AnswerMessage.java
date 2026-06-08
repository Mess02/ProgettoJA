C/*
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
public class AnswerMessage implements Serializable{
    public String risposta;

    public AnswerMessage(String risposta) {
        this.risposta=risposta;
    }

    public String getRisposta() {
        return risposta;
    }

    @Override
    public String toString() {
        return risposta;
    }
    
}
