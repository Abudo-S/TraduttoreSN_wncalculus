/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eccezioni;

/**
 *
 * @author dell
 */
public class UnsupportedElementNameException extends RuntimeException{
    
    public UnsupportedElementNameException(String message){
        super(message);
    }
}
