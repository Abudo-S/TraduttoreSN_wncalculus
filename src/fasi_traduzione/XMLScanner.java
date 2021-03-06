/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import java.io.IOException;
import scanner.Place_scanner;
import scanner.Arc_scanner;
import scanner.Domain_scanner;
import scanner.ColorClass_scanner;
import scanner.Transition_scanner;
import scanner.Variable_scanner;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author dell
 */

//singleton
//part of factory pattern with (scanner)
public class XMLScanner {
//    //Guard: \s*(!)?[(]*\s*[(]*predicate[)]*\s*[)]*(([&]{2}|[|]{2})[(]*\s*[(]*predicate[)]*\s*[)]*)*[)]*\s*
//    //predicate: (\s*[(]*\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)
//    \s*(<=|>=|<|>|==|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*[)]*\s*)
//    //Guard uses predicate:
//    /* 
//    ((\s*[(]*\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*
//    (<=|>=|<|>|==|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*[)]*\s*)|\s*[(]*\s*
//    (True|False)[)]*\s*)(\s*([&]{2}|[|]{2})((\s*[(]*\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*
//    (<=|>=|<|>|=|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*[)]*\s*)|\s*[(]*\s*(True|False)[)]*\s*))*
//    */ 
//    //tuple:
//    /* // [<][>] will be removed by Arc_scanner
//     \s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\s*[+|-]\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\s*
//    ([,]\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\s*[+|-]\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\s*)*
//    */
//    marking tuple of tokens: (\d*)([_a-zA-Z0-9]+)([,]([_a-zA-Z0-9]+))*
    
    private String file_address; 
    private Document doc;
    //single instance
    private static XMLScanner instance = null;
    
    /**
     * 
     * @param file_address the address of file that will be scanned
     * @throws NullPointerException if the file_address passed is null
     */
    private XMLScanner(String file_address) throws NullPointerException, ParserConfigurationException, SAXException, IOException{ // xml file of pnml format
        
        try{
            this.file_address = file_address;
            
            if(this.file_address == null){
                throw new NullPointerException("pnml file address isn't acceptable!");
            }            
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.doc = builder.parse(this.file_address);
            
        }catch(Exception e){
            System.out.println(e + " in XMLScanner");
        }
    }    
    
    /**
     * scan all files data in order
     * @throws java.lang.Exception
     */
    public void scan_file_data() throws Exception{
        
        if(doc == null){
            throw new NullPointerException("Can't find folder with this name: " + this.file_address);
        }
        
        this.scan_color_classes();
        this.scan_domains();
        this.scan_variables();
        this.scan_places();
        this.scan_transitions();
        this.scan_arcs();
        
        //pass net declarations without modifications
        Node decl = this.doc.getElementsByTagName("declaration").item(0);
        
        if(decl.getNodeType() == Node.ELEMENT_NODE){
            Element net_decl = (Element) decl;
            XMLWriter.get_instance(this.file_address).set_net_declaration(net_decl);
        }
        
    }
    
    /**
     * scan all tags "namedsort" & "partition"
     * Note: the excluding of tag "namedsort" of domain will be done inside ColorClass_scanner
     */
    private void scan_color_classes(){ //namedsort && partition
        ColorClass_scanner cc_scanner = ColorClass_scanner.get_instance(doc);
        cc_scanner.Scan_element("namedsort");
        //cc_scanner.remove_from_tags_list(); //remove domains that have the same tag "namesort"
        cc_scanner.Scan_element("partition"); //add partitioned color classes
        ArrayList<Element> elements = cc_scanner.get_element_tags();
        
        elements.stream().forEach(e -> {
            cc_scanner.scan_info(e);
        });
    }
    
    /**
     * scan all tags "namedsort"
     * Note: the excluding of tag "namedsort" of colour class will be done inside Domain_scanner
     */
    private void scan_domains(){ //namedsort -> productsort
        Domain_scanner d_scanner = Domain_scanner.get_instance(doc);
        d_scanner.Scan_element("namedsort");
        //d_scanner.remove_from_tags_list(); //remove color classes that have the same tag "namesort"
        ArrayList<Element> elements = d_scanner.get_element_tags();
        
        elements.stream().forEach(e -> {
            d_scanner.scan_info(e);
        });
    }
    
    /**
     * scan all tags "variabledecl"
     */
    private void scan_variables(){ //variabledecl
        Variable_scanner v_scanner = Variable_scanner.get_instance(doc);
        v_scanner.Scan_element("variabledecl");
        ArrayList<Element> elements = v_scanner.get_element_tags();
        
        elements.stream().forEach(e -> {
            v_scanner.scan_info(e);
        });
    }
    
    /**
     * scan all tags "place
     */
    private void scan_places(){ //place
        Place_scanner p_scanner = Place_scanner.get_instance(doc);
        p_scanner.Scan_element("place");
        ArrayList<Element> elements = p_scanner.get_element_tags();
        
        elements.stream().forEach(e -> {
            p_scanner.scan_info(e);
        });
    }
    
    /**
     * scan all tags "transition"
     */
    private void scan_transitions(){ //transition
        Transition_scanner t_scanner = Transition_scanner.get_instance(doc);
        t_scanner.Scan_element("transition");
        ArrayList<Element> elements = t_scanner.get_element_tags();
        
        elements.stream().forEach(e -> {
            t_scanner.scan_info(e);
        });
    }
    
    /**
     * scan all tags "arc"
     */
    private void scan_arcs(){ //arc
        Arc_scanner a_scanner = Arc_scanner.get_instance(doc);
        a_scanner.Scan_element("arc");
        ArrayList<Element> elements = a_scanner.get_element_tags();
        
        elements.stream().forEach(e -> {
            a_scanner.scan_info(e);
        });
    }
    
    /**
     * 
     * @return the address entered of file
     */
    public String get_file_address(){
        return this.file_address;
    }
    
    /**
     * 
     * @param address the address of file that will be scanned
     * @throws NullPointerException if the file_address passed is null
     * @return single static instance
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public static XMLScanner get_instance(final String address) throws NullPointerException, ParserConfigurationException, SAXException, IOException{
        
        if(instance == null){
            instance = new XMLScanner(address);
        }
        
        return instance;
    }
    
}
