/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package writer;

import eccezioni.UnsupportedElementdataException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void write_info(ArrayList<String> element_info, Element parent) throws UnsupportedElementdataException{
        Element variable = doc.createElement("namedsort");
                
        element_info.stream().forEach(
                single_datum -> {
                    
                    if(single_datum.contains("id")){ //name
                        variable.setAttribute("id", single_datum);
                        variable.setAttribute("name", single_datum);
                    }else if(single_datum.contains("productsort")){
                        Element productsort = doc.createElement("productsort");
                        String[] usersorts = this.separate_usable_ccs(this.seperate_usable_value(single_datum));

                        Arrays.stream(usersorts).forEach(
                                usersort -> {
                                    Element cc = doc.createElement("usersort");
                                    cc.setAttribute("declaration", usersort);
                                    productsort.appendChild(cc);
                                }
                        );
                        variable.appendChild(productsort);

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
    public static Domain_writer get_instance(Document doc){

        if(instance == null){
            instance = new Domain_writer(doc);
        }
        
        return instance;
    }
}
