/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni_xml;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;
import struttura_sn.SN;

/**
 *
 * @author dell
 */

//singleton class
//will be a part of factory pattern
public class XMLScanner {
//    //Guard: \s*(!)?[(]*\s*[(]*predicate[)]*\s*[)]*(([&]{2}|[|]{2})[(]*\s*[(]*predicate[)]*\s*[)]*)*[)]*\s*
//    //predicate: (\s*[(]*\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*(<=|>=|<|>|=|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*[)]*\s*)
//    //Guard uses predicate:
//    /* 
//    (\s*[(]*\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*(<=|>=|<|>|=|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*
//    ([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*[)]*\s*)(\s*(&amp;&amp;|[|]{2})(\s*[(]*\s*([_a-zA-Z]+[_a-zA-Z0-9]*
//    (\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*(<=|>=|<|>|=|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\s*[)]*\s*))*
//    */ 
//    //tuple: //not used till now
//    /*
//     [&lt;]\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\s*
//    ([,]\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\s*)*[&gt;]
//    */
    
    private static XMLScanner instance = null;
    private String file_address; 
    private DataParser dp;
    private Document doc;
    
    private XMLScanner() throws NullPointerException{ // xml file of pnml format
        this.dp = DataParser.get_instance();
        
        try{

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
        //namesort
        
        //namesort -> productsort
        //partition
        //variabledecl
        //place
        //transition
        //arc
    }
    
    public void set_file_address(final String address){
        this.file_address = address;
    }
    
    public static XMLScanner get_instance(){
        
        if(instance == null){
            instance = new XMLScanner();
        }
        
        return instance;
    }
    
}
