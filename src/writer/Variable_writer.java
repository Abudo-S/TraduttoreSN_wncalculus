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
public class Variable_writer extends ElementWriter{
    //single instance
    private static Variable_writer instance = null;
    
    private Variable_writer(Document doc, Document doc_pnpro){
        super(doc, doc_pnpro);
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
                    
                    if(single_datum.contains("id@=")){ //name
                        String datum = this.seperate_usable_value(single_datum);
                        variable.setAttribute("id", datum);
                        variable.setAttribute("name", datum);
                    }else if(single_datum.contains("usersort@=")){
                        Element usersort = doc.createElement("position");
                        usersort.setAttribute("declaration", this.seperate_usable_value(single_datum));
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
     * @param element_info ArrayList of element's data that will be added to pnpro document
     * @param parent the element to which data will be added
     * @throws UnsupportedElementdataException if one of element_info internal data can't be transformed in pnpro format
     */
    @Override
    public void write_info_pnpro(ArrayList<String> element_info, Element parent) throws UnsupportedElementdataException{
        Element variable = doc_pnpro.createElement("color-var");
                
        element_info.stream().forEach(
                single_datum -> {
                    
                    if(single_datum.contains("name@=")){ //name
                        variable.setAttribute("name", this.seperate_usable_value(single_datum));
                    }else if(single_datum.contains("domain@=")){
                        variable.setAttribute("domain", this.seperate_usable_value(single_datum));
                    }else if(single_datum.contains("graphics@=")){
                        String[] xy = this.separate_usable_x_y(this.seperate_usable_value(single_datum));
                        variable.setAttribute("x", xy[0]);
                        variable.setAttribute("y", xy[1]);
                    }else{
                        throw new UnsupportedElementdataException("Can't transform one of element data in pnml: " + single_datum);
                    }
                }
        );
        parent.appendChild(variable);
    }
    
    /**
     * 
     * @param doc pnml document
     * @param doc_pnpro pnpro document
     * @return single static instance
     */
    public static Variable_writer get_instance(Document doc, Document doc_pnpro){

        if(instance == null){
            instance = new Variable_writer(doc, doc_pnpro);
        }
        
        return instance;
    }
}
