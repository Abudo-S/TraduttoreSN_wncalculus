/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import org.w3c.dom.*;

/**
 *
 * @author dell
 */

//parent
public class ElementScanner {
    protected static Document doc;
    protected Element element;
    
    public ElementScanner(final Document doc){
        ElementScanner.doc = doc;     
    }
    
    public void Scan_element(String object_tag){
        try {
            NodeList object_list = doc.getElementsByTagName(object_tag);
            
             for(int i=0; i<object_list.getLength();i++){
                Node node_of_element  = object_list.item(i);
                if(node_of_element.getNodeType() == Node.ELEMENT_NODE){
                    this.element = (Element) node_of_element;
                }
             }   
        }catch(Exception e){
            System.out.println(e + " in ElementScanner");
        }
    }
    
    public String get_element_name(){
        return this.element.getAttribute("id"); // id should have object's name
    }
    
    public Element get_element_tag(){
        return this.element;
    }
    
}
