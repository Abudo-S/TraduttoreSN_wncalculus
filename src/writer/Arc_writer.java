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
public class Arc_writer extends ElementWriter{
    //single instance
    private static Arc_writer instance = null;
    
    private Arc_writer(Document doc){
        super(doc);
    }
        
    /**
     * 
     * @return single static instance
     */
    public static Arc_writer get_instance(Document doc){

        if(instance == null){
            instance = new Arc_writer(doc);
        }
        
        return instance;
    }
}
