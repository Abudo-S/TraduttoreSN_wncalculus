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
//used if the file name entered doesn't have pnml extension 
public class UnsupportedFileException extends RuntimeException{
    
    public UnsupportedFileException(String message){
        super(message);
    }
}
