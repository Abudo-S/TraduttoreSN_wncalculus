/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Componenti;

/**
 *
 * @author dell
 */
public class UnsupportedFileException extends RuntimeException{
    
    public UnsupportedFileException(String message){
        super(message);
    }
}
