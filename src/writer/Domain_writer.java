/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package writer;

import eccezioni.UnsupportedElementdataException;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author dell
 */
//singleton
public class Domain_writer extends ElementWriter{
    //single instance
    private static Domain_writer instance = null;
    
    private Domain_writer(Document doc){
        super(doc);
    }

    /**
     * 
     * @param element_info ArrayList of element's data that will be added to pnml document
     * @param parent the element to which data will be added
     * @throws UnsupportedElementdataException if one of element_info internal data can't be transformed in pnml format
     */
    @Override
    public void write_info(ArrayList<String> element_info, Element parent) throws UnsupportedElementdataException{}
    
    /**
     * 
     * @return single static instance
     */
    public static Domain_writer get_instance(Document doc){

        if(instance == null){
            instance = new Domain_writer(doc);
        }
        
        return instance;
    }
}
