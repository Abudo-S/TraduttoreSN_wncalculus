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
public class ColourClass_writer extends ElementWriter{
    //single instance
    private static ColourClass_writer instance = null;
    
    private ColourClass_writer(Document doc){
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
        Element cc;
        
        if(element_info.get(0).equals("namedsort")){
            cc = doc.createElement("namedsort");
        }else{
            cc = doc.createElement("partition");
        }
        
        for(var i = 1; i < element_info.size(); i++){
            String single_datum = element_info.get(i);
            
            if(single_datum.contains("id@=")){ //name
                single_datum = this.seperate_usable_value(single_datum);
                cc.setAttribute("id", single_datum);
                cc.setAttribute("name", single_datum);
            }else if(single_datum.contains("ordered@=")){
                cc.setAttribute("ordered", this.seperate_usable_value(single_datum));
            }else if(single_datum.contains("finiteintrange@=")){
                Element finiteintrange = doc.createElement("finiteintrange");
                String[] str_end = this.separate_usable_x_y(this.seperate_usable_value(single_datum));
                finiteintrange.setAttribute("start", str_end[0]);
                finiteintrange.setAttribute("end", str_end[1]);
                cc.appendChild(finiteintrange);
            }else if(single_datum.contains("finiteenumeration@=")){
                Element finiteenumeration = doc.createElement("finiteenumeration");
                String[] ccs = this.separate_usable_ccs(this.seperate_usable_value(single_datum));
                
                Arrays.stream(ccs).forEach(
                        feconstant -> {
                            Element fecon = doc.createElement("feconstant");
                            String datum = this.seperate_usable_value(feconstant);
                            fecon.setAttribute("id", datum);
                            fecon.setAttribute("name", datum);
                            finiteenumeration.appendChild(fecon);
                        }
                );
                
                cc.appendChild(finiteenumeration);
            }else if(single_datum.contains("partitionelement@=")){
                Element partitionelement = doc.createElement("partitionelement");
                String tag = this.seperate_usable_value(single_datum);
                
                if(tag.contains("finiteintrange@=")){
                    Element finiteintrange = doc.createElement("finiteintrange");
                    String[] str_end = this.separate_usable_x_y(this.seperate_usable_value(tag));
                    finiteintrange.setAttribute("start", str_end[0]);
                    finiteintrange.setAttribute("end", str_end[1]);
                    partitionelement.appendChild(finiteintrange);
                }else{ //useroperator (similar to finiteenumeration)
                    String[] ccs = this.separate_usable_ccs(this.seperate_usable_value(tag));
                
                    Arrays.stream(ccs).forEach(
                            useroperator -> {
                                Element usop = doc.createElement("feconstant");
                                usop.setAttribute("declaration", this.seperate_usable_value(useroperator));
                                partitionelement.appendChild(usop);
                            }
                    );
                }
                
            }else{
                throw new UnsupportedElementdataException("Can't transform one of element data in pnml: " + single_datum);
            }
        }
    }
    
    /**
     * 
     * @return single static instance
     */
    public static ColourClass_writer get_instance(Document doc){

        if(instance == null){
            instance = new ColourClass_writer(doc);
        }
        
        return instance;
    }
}
