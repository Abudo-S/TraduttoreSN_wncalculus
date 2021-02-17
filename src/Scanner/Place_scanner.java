/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author dell
 */
public class Place_scanner extends ElementScanner{
    
    public Place_scanner(final Document doc){
        super(doc);
    }
    
    @Override
    public void scan_info(Element Place_element){
        
        if(Place_element.getElementsByTagName("type").getLength()>0){
            this.scan_place_type(Place_element);
        }else{
            throw new NullPointerException("Can't find place type: " + this.get_element_name(Place_element));
        }
    }
    
    private void scan_place_type(Element Place_element){ //place tag
        Node pt = Place_element.getElementsByTagName("type").item(0);
        
        if(pt.getNodeType() == Node.ELEMENT_NODE){
            Element pt_tag = (Element) pt;
            String place_type = pt_tag.getElementsByTagName("text").item(0).getTextContent();
            //call DataParser function to create Place of extracted data
            dp.add_Place(Place_element.getAttribute("id"), place_type);
            
            if(Place_element.getElementsByTagName("hlinscription").getLength()>0){ //check for initial marking
                Node hlinscription = Place_element.getElementsByTagName("hlinscription").item(0);
                
                if(hlinscription.getNodeType() == Node.ELEMENT_NODE){
                    Marking_scanner m = new Marking_scanner(doc);
                    Element Marking_element = (Element) hlinscription;
                    m.scan_info(Marking_element);
                }
                
            }
        }
    }
    
}
