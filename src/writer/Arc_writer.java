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
import static writer.ElementWriter.doc;

/**
 *
 * @author dell
 */
//singleton
public class Arc_writer extends ElementWriter{
    //single instance
    private static Arc_writer instance = null;
    
    private Arc_writer(Document doc, Document doc_pnpro){
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
        Element arc = doc.createElement("arc");
                
        element_info.stream().forEach(
                single_datum -> {
                    
                    if(single_datum.contains("id@=")){
                        arc.setAttribute("id", this.seperate_usable_value(single_datum));
                    }else if(single_datum.contains("source@=")){
                        arc.setAttribute("source", this.seperate_usable_value(single_datum));
                    }else if(single_datum.contains("target@=")){
                        arc.setAttribute("target", this.seperate_usable_value(single_datum));
                    }else if(single_datum.contains("type@=")){ //inhibitor/transiting_arc
                        String arc_type = this.seperate_usable_value(single_datum);

                        if(arc_type.equals("inhibitor")){
                            Element type = doc.createElement("type");
                            type.setAttribute("type", "inhibitor");
                            arc.appendChild(type);
                        }
                    }else if(single_datum.contains("hlinscription@=")){
                        Element hlinscription = doc.createElement("hlinscription");
                        Element text = doc.createElement("text");
                        text.appendChild(doc.createTextNode(this.seperate_usable_value(single_datum)));
                        hlinscription.appendChild(text);
                        arc.appendChild(hlinscription);
                    }else{
                        throw new UnsupportedElementdataException("Can't transform one of element data in pnml: " + single_datum);
                    }
                }
        );
        parent.appendChild(arc);
    }
    
    /**
     * 
     * @param element_info ArrayList of element's data that will be added to pnpro document
     * @param parent the element to which data will be added
     * @throws UnsupportedElementdataException if one of element_info internal data can't be transformed in pnpro format
     */
    @Override
    public void write_info_pnpro(ArrayList<String> element_info, Element parent) throws UnsupportedElementdataException{
        Element arc = doc_pnpro.createElement("arc");

        element_info.stream().forEach(
            single_datum -> {

                if(single_datum.contains("head@=")){
                    arc.setAttribute("head", this.seperate_usable_value(single_datum));
                }else if(single_datum.contains("tail@=")){
                    arc.setAttribute("tail", this.seperate_usable_value(single_datum));
                }else if(single_datum.contains("kind@=")){ //INPUT | OUTPUT | INHIBITOR
                    arc.setAttribute("kind", this.seperate_usable_value(single_datum));
                }else if(single_datum.contains("mult@=")){
                    arc.setAttribute("mult", this.seperate_usable_value(single_datum));
                }else{
                    throw new UnsupportedElementdataException("Can't transform one of element data in pnml: " + single_datum);
                }
            }
        );
        parent.appendChild(arc);
    }
    
    /**
     * 
     * @param doc pnml document
     * @param doc_pnpro pnpro document
     * @return single static instance
     */
    public static Arc_writer get_instance(Document doc, Document doc_pnpro){

        if(instance == null){
            instance = new Arc_writer(doc, doc_pnpro);
        }
        
        return instance;
    }
}
