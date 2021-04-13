/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package writer;

import org.w3c.dom.Document;

/**
 *
 * @author dell
 */
//singleton
public class Transition_writer extends ElementWriter{
    //single instance
    private static Transition_writer instance = null;
    
    private Transition_writer(Document doc){
        super(doc);
    }
        
    /**
     * 
     * @return single static instance
     */
    public static Transition_writer get_instance(Document doc){

        if(instance == null){
            instance = new Transition_writer(doc);
        }
        
        return instance;
    }
}
