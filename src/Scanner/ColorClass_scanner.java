/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import static Scanner.ElementScanner.dp;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author dell
 */
public class ColorClass_scanner extends ElementScanner{
    
    public ColorClass_scanner(final Document doc){
        super(doc);
    }
    
    @Override
    public void scan_info(Element color_class){
         
        if(color_class.getElementsByTagName("finiteintrange").getLength()>0){
            this.scan_finiterange(color_class);
        }else if(color_class.getElementsByTagName("finiteenumeration").getLength()>0){
            this.scan_finiteenumeration(color_class);
        }else if(color_class.getElementsByTagName("partitionelement").getLength()>0){
            this.scan_partionedclass(color_class);
        }else{
            throw new NullPointerException("Can't find color class type: " + this.get_element_name(color_class));
        }
    }
    
    private void scan_finiterange(Element color_class){
        int rangStart, rangEnd;
        boolean circular = false;
        Node fing = color_class.getElementsByTagName("finiteintrange").item(0);
        
        if(fing.getNodeType() == Node.ELEMENT_NODE){
            Element finiteintrange = (Element) fing;
            rangStart = Integer.parseInt(finiteintrange.getAttribute("start"));
            rangEnd = Integer.parseInt(finiteintrange.getAttribute("end"));

            if(color_class.getAttribute("ordered").equals("true")){
                circular = true;
            } 
            //call DataParser function to create color class of extracted data
            dp.add_ColorClass(this.get_element_name(color_class), rangStart, rangEnd, circular);
        }
    }
    
    private void scan_finiteenumeration(Element color_class){
        boolean circular = false;
        NodeList finco_list = color_class.getElementsByTagName("feconstant");
        ArrayList<String> token_names = new ArrayList<>();
        
        for(var i = 0; i < finco_list.getLength(); i++){
            Node finco = finco_list.item(i);
            if(finco.getNodeType() == Node.ELEMENT_NODE){
                Element finiteenumeration = (Element) finco;
                token_names.add(finiteenumeration.getAttribute("id"));
            }
        }

        if(color_class.getAttribute("ordered").equals("true")){
          circular = true;
        } 
        //call DataParser function to create color class of extracted data
        dp.add_ColorClass(this.get_element_name(color_class), token_names, circular);
    }
    
    private void scan_partionedclass(Element color_class){ //partition tag
        
    }

}