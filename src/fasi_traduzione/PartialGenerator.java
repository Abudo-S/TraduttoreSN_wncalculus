/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

/**
 *
 * @author dell
 */
//singleton
//will be a part of factory pattern with (place_partial_unfolding)
public class PartialGenerator {
    //single instance
    private static PartialGenerator instance = null;
    
    private PartialGenerator(){
        
    }
    
    /**
     * 
     * @return single static instance
     */
    public static PartialGenerator get_instance(){
        
        if(instance == null){
            instance = new PartialGenerator();
        }
        
        return instance;
    }
}
