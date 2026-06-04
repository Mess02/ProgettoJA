/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package common;

/**
 *
 * @author Mess
 */
public enum DIFFICULTY {
    EASY (8 , 5 , 5) , MEDIUM (5 , 7 , 10) , HARD (2 , 10 , 13);
    
    private final int frequency;
    private final int length;
    private final int shift;
    
    DIFFICULTY(int frequency , int length , int shift){
        this.frequency = frequency;
        this.length = length;
        this.shift = shift;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getLength() {
        return length;
    }

    public int getShift() {
        return shift;
    }
}
