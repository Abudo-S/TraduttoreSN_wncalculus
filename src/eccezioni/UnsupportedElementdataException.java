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
public class UnsupportedElementdataException extends RuntimeException{
    
    public UnsupportedElementdataException(String message){
        super(message);
    }
}
