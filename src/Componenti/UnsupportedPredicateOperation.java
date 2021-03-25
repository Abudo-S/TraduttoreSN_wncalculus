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
//used if predicate operation isn't supported, ex: > | < | >= | <= that doesn't exist in library wncalculus and requires implementing tool functions
public class UnsupportedPredicateOperation extends RuntimeException{
    
    public UnsupportedPredicateOperation(String message){
        super(message);
    }
}
