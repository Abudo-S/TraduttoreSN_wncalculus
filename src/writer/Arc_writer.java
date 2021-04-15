/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package writer;

import eccezioni.UnsupportedELementdataException;
import java.util.ArrayList;
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
     * @param element_info ArrayList of element's data that will be added to pnml document
     * @throws UnsupportedELementdataException if one of element_info internal data can't be transformed in pnml format
     */
    @Override
    public void write_info(ArrayList<String> element_info) throws UnsupportedELementdataException{
        
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
