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
public class Place_writer extends ElementWriter{
    //single instance
    private static Place_writer instance = null;
    
    private Place_writer(Document doc){
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
        Element place = doc.createElement("place");
                
        element_info.stream().forEach(
                single_datum -> {
                    
                    if(single_datum.contains("id@=")){ //name
                        String datum = this.seperate_usable_value(single_datum);
                        Element name = doc.createElement("id");
                        name.appendChild(doc.createTextNode(datum));
                        place.setAttribute("id", datum);
                        place.appendChild(name);
                    }else if(single_datum.contains("type@=")){
                        Element type = doc.createElement("type");
                        type.appendChild(doc.createTextNode(this.seperate_usable_value(single_datum)));
                        place.appendChild(type);
                    }else if(single_datum.contains("hlinitialMarking")){ //might use Marking_writer
                        Element hlinitialMarking = doc.createElement("hlinitialMarking");
                        hlinitialMarking.appendChild(doc.createTextNode(this.seperate_usable_value(single_datum)));
                    }else if(single_datum.contains("graphics")){
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
     * @return single static instance
     */
    public static Place_writer get_instance(Document doc){

        if(instance == null){
            instance = new Place_writer(doc);
        }
        
        return instance;
    }
}
