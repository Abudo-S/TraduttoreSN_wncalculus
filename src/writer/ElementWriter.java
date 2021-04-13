/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package writer;

import java.util.ArrayList;
import org.w3c.dom.Document;

/**
 *
 * @author dell
 */
//parent
public abstract class ElementWriter {
    
    protected static Document doc;
    protected ArrayList<ArrayList<String>> element_data;
    
    /**
     * 
     * @param doc the document in which we write elements
     */
    public ElementWriter(final Document doc){
        ElementWriter.doc = doc;  
        this.element_data = new ArrayList<>();
    }
    
    /**
     * 
     * @param e_data all required data to create an element
     */
    public void add_element_data(ArrayList<String> e_data){
        this.element_data.add(e_data);
    }
    
    /**
     * 
     * @return ArrayList of all elements data that will be written
     */
    public ArrayList<ArrayList<String>> get_element_data(){
        return this.element_data;
    }
}
