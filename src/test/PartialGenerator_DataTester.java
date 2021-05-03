/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author dell
 */
public class PartialGenerator_DataTester {
    //single instance
    private static PartialGenerator_DataTester instance = null;
    
    private PartialGenerator_DataTester(){
        
    }
    /**
     * 
     * @return single static instance
     */
    public static PartialGenerator_DataTester get_instance(){
        
        if(instance == null){
            instance = new PartialGenerator_DataTester();
        }
        
        return instance;
    }
}
