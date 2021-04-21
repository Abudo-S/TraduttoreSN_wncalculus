/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package writer;

import eccezioni.UnsupportedElementdataException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
     * @param element_info ArrayList of element's data that will be added to pnml document
     * @param parent the element to which data will be added
     * @throws UnsupportedElementdataException if one of element_info internal data can't be transformed in pnml format
     */
    public void write_info(ArrayList<String> element_info, Element parent) throws UnsupportedElementdataException{}
    
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
    
    /**
     * 
     * @param single_datum element of ArrayList from which we will extract it value
     * @return the value extracted if found, Undefined value otherwise
     */
    protected String seperate_usable_value(String single_datum){
        String value = "Undefined value";
        Pattern p = Pattern.compile(".+@=(.+)");
        Matcher m = p.matcher(single_datum);
        
        if(m.find()){
            value = m.group(1);
        }
        
        return value;
    }
    
    protected String[] separate_usable_x_y(String single_datum){
        String[] xy = new String[]{"0","0"};
        Pattern p = Pattern.compile("x=(\\d+)y=(\\d+)");
        Matcher m = p.matcher(single_datum);
        
        if(m.find()){
            xy[0] = m.group(1);
            xy[1] = m.group(2);
        }
        
        return xy;
    }
}
