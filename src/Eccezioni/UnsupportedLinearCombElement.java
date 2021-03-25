/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Eccezioni;

/**
 *
 * @author dell
 */
//used if the element of LinearComb isn't allowed, ex: adding a variable inside place's marking, or adding a token inside arc expression
public class UnsupportedLinearCombElement extends RuntimeException{
    
    public UnsupportedLinearCombElement(String message){
        super(message);
    }
}
