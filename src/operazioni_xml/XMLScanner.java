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
//    //predicate: [(]*([_a-zA-Z]+[_a-zA-Z0-9]*)\s*(<=|>=|<|>|=|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*([_a-zA-Z]+[_a-zA-Z0-9]*)[)]*
//    //Guard uses predicate:
//    /* 
//    \s*(!)?[(]*\s*[(]*([(]*([_a-zA-Z]+[_a-zA-Z0-9]*)\s*(<=|>=|<|>|=|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*
//    ([_a-zA-Z]+[_a-zA-Z0-9]*)[)]*)[)]*\s*[)]*(([&]{2}|[|]{2})[(]*\s*[(]*()[(]*([_a-zA-Z]+[_a-zA-Z0-9]*)\s*
//    (<=|>=|<|>|=|!\s*=|\s+in\s+|\s*!\s*in\s+)\s*([_a-zA-Z]+[_a-zA-Z0-9]*)[)]*[)]*\s*[)]*)*[)]*\s* 
//    */ 
//    //tuple:
//    /*
//     [&lt;]\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\s*
//    ([,]\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\s*[+]\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\s*)*[&gt;]
//    */
//    
//    private static XMLScanner instance = null;
//    private String file_address; 
//    private DataParser dp;
//    
//    private XMLScanner() throws NullPointerException{ // xml file of pnml format
//        this.dp = new DataParser(new SN());
//        try{
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document doc = builder.parse("CPN.pnml");
//            if(this.file_address == null){
//                throw new NullPointerException("pnml file address isn't acceptable!");
//            }    
//        }catch(Exception e){
//            System.out.println(e + " in XMLScanner");
//        }
//    }
//    
//    public static XMLScanner get_instance(){
//        if(instance == null){
//            instance = new XMLScanner();
//        }
//        return instance;
//    }
//    
//    public void set_file_address(final String address){
//        this.file_address = address;
//    }
}
