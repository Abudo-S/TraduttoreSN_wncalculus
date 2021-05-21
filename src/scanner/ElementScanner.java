/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

import fasi_traduzione.DataParser;
import java.util.ArrayList;
import org.w3c.dom.*;


/**
 *
 * @author dell
 */

//part of facade pattern with xml scanner and traduttore sn 
//parent
public abstract class ElementScanner{
    
    protected static Document doc;
    protected ArrayList<Element> element_tags;
    protected static DataParser dp;
    
    /**
     * 
     * @param doc the document from which we scan elements
     */
    public ElementScanner(final Document doc){
        ElementScanner.doc = doc;  
        this.element_tags = new ArrayList<>();
        dp = DataParser.get_instance();
    }
    
    /**
     * 
     * @param object_tag String of element's parent-tag from which we will scan its attributes and sub-tags data
     */
    public void Scan_element(String object_tag){
        try {
            NodeList object_list = doc.getElementsByTagName(object_tag);
            
            for(var i=0; i<object_list.getLength();i++){
                Node node_of_element  = object_list.item(i);
                
                if(node_of_element.getNodeType() == Node.ELEMENT_NODE){
                    this.element_tags.add((Element) node_of_element);
                }
            }
            
        }catch(Exception e){
            System.out.println(e + " in ElementScanner");
        }
    }
    
    /**
     * 
     * @return Array of strings of parent-elements names
     */
    public String[] get_element_names(){
        int size = this.element_tags.size(); 
        String[] names = new String[size]; 
 
        for(var i = 0; i < size; i++){
            names[i] = element_tags.get(i).getAttribute("id"); // id should have object's name
        }
         
        return names;
    }
    
    /**
     * 
     * @return ArrayList of elements found that have the same type
     */
    public ArrayList<Element> get_element_tags(){
        return this.element_tags;
    }
    
    /**
     * 
     * @param e
     * @return 
     */
    public String get_element_name(Element e){
        return e.getAttribute("id"); // id should have object's name
    }
    
    /**
     * 
     * @return the document we're elaborating
     */
    public Document get_document(){
        return doc;
    }
    
    public void scan_info(Element element_tag) throws NullPointerException{}
    
    public void remove_from_tags_list(){}
    
}
