/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package writer;

import eccezioni.UnsupportedElementdataException;
import java.util.ArrayList;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author dell
 */
//singleton
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
        Element variable = doc.createElement("variabledecl");
                
        element_info.stream().forEach(
                single_datum -> {
                    
                    if(single_datum.contains("id")){ //name
                        String datum = this.seperate_usable_value(single_datum);
                        variable.setAttribute("id", datum);
                        variable.setAttribute("name", datum);
                    }else if(single_datum.contains("usersort")){
                        Element usersort = doc.createElement("position");
                        usersort.setAttribute("declaration", single_datum);
                        variable.appendChild(usersort);
                    }else{
                        throw new UnsupportedElementdataException("Can't transform one of element data in pnml: " + single_datum);
                    }
                }
        );
        parent.appendChild(variable);
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
