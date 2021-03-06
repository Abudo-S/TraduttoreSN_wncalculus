/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.regex.*;

/**
 *
 * @author dell
 */
//singleton
public class Marking_scanner extends ElementScanner{
    
    private static final String str_rx_TupleToken = "(\\d*)([_a-zA-Z0-9]+)([,]([_a-zA-Z0-9]+))*";
    
    private static final String str_rx_multiplicity = "(\\d*)";
    //single instance
    private static Marking_scanner instance = null;
    
    private Marking_scanner(final Document doc){
        super(doc);
    }
    
    public void scan_info(Element Marking_element, String place_name){
        
      if(Marking_element.getElementsByTagName("text").getLength()>0){
            this.scan_marking_text(Marking_element, place_name);
      }else{
            throw new NullPointerException("Can't find marking text: " + this.get_element_name(Marking_element));
      }
      
    }
    
    private void scan_marking_text(Element Marking_element, String place_name) throws NullPointerException{
        int multiplicity;
        Map tokens;
        String marking = Marking_element.getElementsByTagName("text").item(0).getTextContent();
        
        //remove unnecessary data
        marking = marking.replaceAll("[<\\s>]", "");
        
        //scan tuple of tokens
        Pattern p = Pattern.compile(str_rx_TupleToken);
        String[] mult_tuples_token = marking.split("\\+");
        
        //check token type (color class || domain)
        if(mult_tuples_token[0].contains(",")){ //domain
          tokens = new LinkedHashMap<String[], Integer>();
          
        }else{ //color class
          tokens = new HashMap<String, Integer>();
        }
        
        String mult;
        String[] colors_token;
        Matcher m;
        for(var i = 0; i< mult_tuples_token.length; i++){
            multiplicity = 1;
            m = p.matcher(mult_tuples_token[i]);
            
            if(m.find()){
                mult = m.group(1);
                
                if(!mult.isEmpty()){
                    multiplicity = Integer.parseInt(mult);
                } 
                colors_token = mult_tuples_token[i].split(",");
                
                if(colors_token.length == 1){ //color class
                    tokens.put(colors_token[0].replaceFirst(str_rx_multiplicity, ""), multiplicity);
                    
                }else{ //domain
                    colors_token[0] = colors_token[0].replaceFirst(str_rx_multiplicity, "");
                    tokens.put(colors_token, multiplicity);
                }                                               
            }else{
                throw new NullPointerException("Initial marking exception : " + marking);
            }
        }
        
        dp.add_Marking(place_name, tokens);
    }
    
    public static Marking_scanner get_instance(Document doc){
        
        if(instance == null){
            instance = new Marking_scanner(doc);
        }
        
        return instance;
    }
}
