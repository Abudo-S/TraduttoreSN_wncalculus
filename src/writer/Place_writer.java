/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package writer;

import eccezioni.UnsupportedELementdataException;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author dell
 */
//singleton
public class Place_writer extends ElementWriter{
    //single instance
    private static Place_writer instance = null;
    
    private Place_writer(Document doc){
        super(doc);
    }

    /**
     * 
     * @param element_info ArrayList of element's data that will be added to pnml document
     * @throws UnsupportedELementdataException if one of element_info internal data can't be transformed in pnml format
     */
    @Override
    public void write_info(ArrayList<String> element_info) throws UnsupportedELementdataException{
        Element place = doc.createElement("place");
        doc.appendChild(place);
                
        element_info.stream().forEach(
                single_datum -> {
                    
                    switch(single_datum){
                        case "id": //name
                            Element name = doc.createElement(single_datum);
                            name.appendChild(doc.createTextNode(this.seperate_usable_value(single_datum)));
                            place.setAttribute("id", single_datum);
                            break;
                        case "type":
                            Element type = doc.createElement(single_datum);
                            type.appendChild(doc.createTextNode(this.seperate_usable_value(single_datum)));
                            break;
                        case "hlinitialMarking": //might use Marking_writer
                            Element hlinitialMarking = doc.createElement(single_datum);
                            hlinitialMarking.appendChild(doc.createTextNode(this.seperate_usable_value(single_datum)));
                            break;                            
                        default:
                            throw new UnsupportedELementdataException("Can't transform one of element data in pnml: " + single_datum);
                    }
                }
        );
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Place_writer get_instance(Document doc){

        if(instance == null){
            instance = new Place_writer(doc);
        }
        
        return instance;
    }
}
