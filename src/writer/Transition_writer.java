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
import static writer.ElementWriter.doc;

/**
 *
 * @author dell
 */
//singleton
public class Transition_writer extends ElementWriter{
    //single instance
    private static Transition_writer instance = null;
    
    private Transition_writer(Document doc, Document doc_pnpro){
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
        Element transition = doc.createElement("transition");
                
        element_info.stream().forEach(
                single_datum -> {
                    
                    if(single_datum.contains("id@=")){ //name
                        String datum = this.seperate_usable_value(single_datum);
                        Element name = doc.createElement("name");
                        Element text = doc.createElement("text");
                        text.appendChild(doc.createTextNode(datum));
                        name.appendChild(text);
                        transition.setAttribute("id", datum);
                        transition.appendChild(name);
                    }else if(single_datum.contains("condition@=")){
                        Element condition = doc.createElement("condition");
                        Element text = doc.createElement("text");
                        text.appendChild(doc.createTextNode(this.seperate_usable_value(single_datum)));
                        condition.appendChild(text);
                        transition.appendChild(condition);
                    }else if(single_datum.contains("graphics@=")){
                        String[] xy = this.separate_usable_x_y(this.seperate_usable_value(single_datum));
                        Element graphics = doc.createElement("graphics");                        
                        Element position = doc.createElement("position");
                        position.setAttribute("x", xy[0]);
                        position.setAttribute("y", xy[1]);
                        graphics.appendChild(position);
                        transition.appendChild(graphics);
                    }else{
                        throw new UnsupportedElementdataException("Can't transform one of element data in pnml: " + single_datum);
                    }
                }
        );
        parent.appendChild(transition);
    }
    
    /**
     * 
     * @param doc pnml document
     * @param doc_pnpro pnpro document
     * @return single static instance
     */
    public static Transition_writer get_instance(Document doc, Document doc_pnpro){

        if(instance == null){
            instance = new Transition_writer(doc, doc_pnpro);
        }
        
        return instance;
    }
}
