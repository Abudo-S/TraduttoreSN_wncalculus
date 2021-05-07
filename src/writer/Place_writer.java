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
public class Place_writer extends ElementWriter{
    //single instance
    private static Place_writer instance = null;
    
    private Place_writer(Document doc, Document doc_pnpro){
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
        Element place = doc.createElement("place");
                
        element_info.stream().forEach(
                single_datum -> {
                    
                    if(single_datum.contains("id@=")){ //name
                        String datum = this.seperate_usable_value(single_datum);
                        Element name = doc.createElement("name");
                        Element text = doc.createElement("text");
                        text.appendChild(doc.createTextNode(datum));
                        name.appendChild(text);
                        place.setAttribute("id", datum);
                        place.appendChild(name);
                    }else if(single_datum.contains("type@=")){
                        Element type = doc.createElement("type");
                        Element text = doc.createElement("text");
                        text.appendChild(doc.createTextNode(this.seperate_usable_value(single_datum)));
                        type.appendChild(text);
                        place.appendChild(type);
                    }else if(single_datum.contains("hlinitialMarking@=")){ //might use Marking_writer
                        Element hlinitialMarking = doc.createElement("hlinitialMarking");
                        hlinitialMarking.appendChild(doc.createTextNode(this.seperate_usable_value(single_datum)));
                    }else if(single_datum.contains("graphics@=")){
                        String[] xy = this.separate_usable_x_y(this.seperate_usable_value(single_datum));
                        Element graphics = doc.createElement("graphics");                        
                        Element position = doc.createElement("position");
                        position.setAttribute("x", xy[0]);
                        position.setAttribute("y", xy[1]);
                        graphics.appendChild(position);
                        place.appendChild(graphics);
                    }else{
                        throw new UnsupportedElementdataException("Can't transform one of element data in pnml: " + single_datum);
                    }
                }
        );
        parent.appendChild(place);
    }
    
    /**
     * 
     * @param element_info ArrayList of element's data that will be added to pnpro document
     * @param parent the element to which data will be added
     * @throws UnsupportedElementdataException if one of element_info internal data can't be transformed in pnpro format
     */
    @Override
    public void write_info_pnpro(ArrayList<String> element_info, Element parent) throws UnsupportedElementdataException{
        Element place = doc_pnpro.createElement("place");

        element_info.stream().forEach(
            single_datum -> {

                if(single_datum.contains("name@=")){
                    place.setAttribute("name", this.seperate_usable_value(single_datum));
                }else if(single_datum.contains("domain@=")){
                    place.setAttribute("domain", this.seperate_usable_value(single_datum));
                }else if(single_datum.contains("marking@=")){
                    place.setAttribute("marking", this.seperate_usable_value(single_datum));
                }else if(single_datum.contains("graphics@=")){
                    String[] xy = this.separate_usable_x_y(this.seperate_usable_value(single_datum));
                    place.setAttribute("x", xy[0]);
                    place.setAttribute("y", xy[1]);
                }else{
                    throw new UnsupportedElementdataException("Can't transform one of element data in pnml: " + single_datum);
                }
            }
        );
        parent.appendChild(place);
    }
    
    /**
     * 
     * @param doc pnml document
     * @param doc_pnpro doc_pnpro
     * @return single static instance
     */
    public static Place_writer get_instance(Document doc, Document doc_pnpro){

        if(instance == null){
            instance = new Place_writer(doc, doc_pnpro);
        }
        
        return instance;
    }
}
