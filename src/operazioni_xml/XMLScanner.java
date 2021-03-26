/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni_xml;

import Scanner.*;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 *
 * @author dell
 */

//singleton
//will be a part of factory pattern
public class XMLScanner {
//    //Guard: \s*(!)?[(]*\s*[(]*predicate[)]*\s*[)]*(([&]{2}|[|]{2})[(]*\s*[(]*predicate[)]*\s*[)]*)*[)]*\s*
//    //predicate: (\s*[(]*\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)
//    \s*(<=|>=|<|>|=|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*[)]*\s*)
//    //Guard uses predicate:
//    /* 
//    ((\s*[(]*\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*
//    (<=|>=|<|>|=|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*[)]*\s*)|\s*[(]*\s*
//    (True|False)[)]*\s*)(\s*([&]{2}|[|]{2})((\s*[(]*\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*
//    (<=|>=|<|>|=|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*[)]*\s*)|\s*[(]*\s*(True|False)[)]*\s*))*
//    */ 
//    //tuple:
//    /* // [<][>] will be removed by Arc_scanner
//     \s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\s*[+|-]\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\s*
//    ([,]\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\s*[+|-]\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\s*)*
//    */
//    marking tuple of tokens: (\d*)([_a-zA-Z0-9]+)([,]([_a-zA-Z0-9]+))*
    
    private static XMLScanner instance = null;
    private String file_address; 
    private Document doc;
    
    private XMLScanner(String file_address) throws NullPointerException{ // xml file of pnml format
        
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
    
    public void scan_file_data(){
        this.scan_color_classes();
        this.scan_domains();
        this.scan_variables();
        this.scan_places();
        this.scan_transitions();
        this.scan_arcs();
    }
    
    private void scan_color_classes(){ //namedsort && partition
        ColorClass_scanner cc_scanner = ColorClass_scanner.get_instance(doc);
        cc_scanner.Scan_element("namedsort");
        //cc_scanner.remove_from_tags_list(); //remove domains that have the same tag "namesort"
        cc_scanner.Scan_element("partition"); //add partitioned color classes
        ArrayList<Element> elements = cc_scanner.get_element_tags();
        
        for(Element e : elements){
            cc_scanner.scan_info(e);
        }
    }
    
    private void scan_domains(){ //namedsort -> productsort
        Domain_scanner d_scanner = Domain_scanner.get_instance(doc);
        d_scanner.Scan_element("namedsort");
        //d_scanner.remove_from_tags_list(); //remove color classes that have the same tag "namesort"
        ArrayList<Element> elements = d_scanner.get_element_tags();
        
        for(Element e : elements){
            d_scanner.scan_info(e);
        }
    }
    
    private void scan_variables(){ //variabledecl
        Variable_scanner v_scanner = Variable_scanner.get_instance(doc);
        v_scanner.Scan_element("variabledecl");
        ArrayList<Element> elements = v_scanner.get_element_tags();
        
        for(Element e : elements){
            v_scanner.scan_info(e);
        }
    }
    
    private void scan_places(){ //place
        Place_scanner p_scanner = Place_scanner.get_instance(doc);
        p_scanner.Scan_element("place");
        ArrayList<Element> elements = p_scanner.get_element_tags();
        
        for(Element e : elements){
            p_scanner.scan_info(e);
        }
    }
    
    private void scan_transitions(){ //transition
        Transition_scanner t_scanner = Transition_scanner.get_instance(doc);
        t_scanner.Scan_element("transition");
        ArrayList<Element> elements = t_scanner.get_element_tags();
        
        for(Element e : elements){
            t_scanner.scan_info(e);
        }
    }
    
    private void scan_arcs(){ //arc
        Arc_scanner a_scanner = Arc_scanner.get_instance(doc);
        a_scanner.Scan_element("arc");
        ArrayList<Element> elements = a_scanner.get_element_tags();
        
        for(Element e : elements){
            a_scanner.scan_info(e);
        }
    }
    
    public void set_file_address(final String address){
        this.file_address = address;
    }
    
    public static XMLScanner get_instance(final String address){
        
        if(instance == null){
            instance = new XMLScanner(address);
        }
        
        return instance;
    }
    
}
