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
public class Marking_writer {
    //single instance
    private static Marking_writer instance = null;
    
    private Marking_writer(){

    }
        
    /**
     * 
     * @return single static instance
     */
    public static Marking_writer get_instance(){

        if(instance == null){
            instance = new Marking_writer();
        }
        
        return instance;
    }
}
