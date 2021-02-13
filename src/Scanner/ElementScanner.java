/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import operazioni_xml.DataParser;
import java.util.ArrayList;
import org.w3c.dom.*;


/**
 *
 * @author dell
 */

//parent
public abstract class ElementScanner {
    
    protected static Document doc;
    protected ArrayList<Element> element_tags;
    protected static DataParser dp = new DataParser();
    
    public ElementScanner(final Document doc){
        ElementScanner.doc = doc;  
        this.element_tags = new ArrayList<>();
    }
    
    public void Scan_element(String object_tag)throws NullPointerException{
        try {
            NodeList object_list = doc.getElementsByTagName(object_tag);
            
             for(int i=0; i<object_list.getLength();i++){
                Node node_of_element  = object_list.item(i);
                if(node_of_element.getNodeType() == Node.ELEMENT_NODE){
                    this.element_tags.add((Element) node_of_element);
                }
             }
             if(this.element_tags.size() == 0){
                throw new NullPointerException("Can't found declaration of that type!");
             } 
        }catch(Exception e){
            System.out.println(e + " in ElementScanner");
        }
    }
    
    public String[] get_element_names(){
         int size = this.element_tags.size(); 
         String[] names = new String[size]; 
 
         for(var i = 0; i < size; i++){
            names[i] = element_tags.get(i).getAttribute("id"); // id should have object's name
         }
         
         return names;
    }
    
    public ArrayList<Element> get_element_tags(){
        return this.element_tags;
    }
    
    public String get_element_name(Element e){
        return e.getAttribute("id"); // id should have object's name
    }
    
    public void scan_info(Element element_tag) throws NullPointerException{}
    
}
