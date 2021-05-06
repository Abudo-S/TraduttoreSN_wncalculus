/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pnpro_writer;

import eccezioni.UnsupportedElementdataException;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import writer.ElementWriter;

/**
 *
 * @author dell
 */
public class Variable_writer extends ElementWriter{
    //single instance
    private static Variable_writer instance = null;
    
    private Variable_writer(Document doc){
        super(doc);
    }
    
    /**
     * 
     * @param element_info ArrayList of element's data that will be added to pnml document
     * @param parent the element to which data will be added
     * @throws UnsupportedElementdataException if one of element_info internal data can't be transformed in pnml format
     */
    @Override
    public void write_info(ArrayList<String> element_info, Element parent) throws UnsupportedElementdataException{
        //to be completed
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Variable_writer get_instance(Document doc){

        if(instance == null){
            instance = new Variable_writer(doc);
        }
        
        return instance;
    }
}