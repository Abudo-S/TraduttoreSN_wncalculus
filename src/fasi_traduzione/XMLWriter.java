/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import writer.*;

/**
 *
 * @author dell
 */
//singleton
//part of factory pattern with (writer)
public class XMLWriter {
    
    private final String file_address; 
    private final Document doc;
    private final Place_writer pw;
    private final Transition_writer tw;
    private final Arc_writer aw;
    //single instance
    private static XMLWriter instance = null;
    
    /**
     * 
     * @param file_address the address of file that will be generated
     */
    private XMLWriter(String file_address) throws ParserConfigurationException, SAXException, IOException{
        Pattern p = Pattern.compile("(.*)[.]pnml");
        Matcher m = p.matcher(file_address);
        
        if(m.find()){
            file_address = m.group(1);
        }
        
        this.file_address = file_address + "_generated.pnml";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.doc = builder.newDocument();
        //initialize elements scanners
        this.pw = Place_writer.get_instance(this.doc);
        this.tw = Transition_writer.get_instance(this.doc);
        this.aw = Arc_writer.get_instance(this.doc);
    }
    
    /**
     * 
     * @return the address generated of file
     */
    public String get_file_address(){
        return this.file_address;
    }
    
    /**
     * write all necessary data in document, then create a file from them
     */
    public void write_all_data() throws TransformerException{
        this.pw.get_element_data().stream().forEach(
                place_data -> this.pw.write_info(place_data)
        );
        
        this.tw.get_element_data().stream().forEach(
                transition_data -> this.tw.write_info(transition_data)
        );
                
        this.aw.get_element_data().stream().forEach(
                arc_data -> this.aw.write_info(arc_data)
        );
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource domsr = new DOMSource(this.doc);
        StreamResult streamResult = new StreamResult(new File(this.file_address));
        transformer.transform(domsr, streamResult);
    }
    
    /**
     * delegator
     * @param element_info ArrayList of all element data that's ready to be added
     */
    public void add_place(ArrayList<String> element_info){
        this.pw.add_element_data(element_info);
    }
    
    /**
     * delegator
     * @param element_info ArrayList of all element data that's ready to be added
     */
    public void add_transition(ArrayList<String> element_info){
        this.tw.add_element_data(element_info);
    }
    
    /**
     * delegator
     * @param element_info ArrayList of all element data that's ready to be added
     */
    public void add_arc(ArrayList<String> element_info){
        this.aw.add_element_data(element_info);
    }
        
    /**
     * 
     * @param address the address of file that will be generated
     * @return single static instance
     */
    public static XMLWriter get_instance(final String address) throws ParserConfigurationException, SAXException, IOException{
        
        if(instance == null){
            instance = new XMLWriter(address);
        }
        
        return instance;
    }
}
