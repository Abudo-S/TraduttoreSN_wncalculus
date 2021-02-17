/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.regex.*;

/**
 *
 * @author dell
 */
public class Marking_scanner extends ElementScanner{
    //marking tuple of tokens: (\d*)([_a-zA-Z0-9]+)([,]([_a-zA-Z0-9]+))*
    
    public Marking_scanner(final Document doc){
        super(doc);
    }
    
    @Override
    public void scan_info(Element Marking_element){
        
      if(Marking_element.getElementsByTagName("text").getLength()>0){
            this.scan_marking_text(Marking_element);
      }else{
            throw new NullPointerException("Can't find marking text: " + this.get_element_name(Marking_element));
      }
    }
    
    private void scan_marking_text(Element Marking_element) throws NullPointerException{
        int multiplicity;
        Map tokens;
        String marking = Marking_element.getElementsByTagName("text").item(0).getTextContent();
        
        //remove unnecessary data
        String unnecessaryD_regex = "&lt;|&gt;|\\s";
        marking = marking.replaceAll(unnecessaryD_regex, "");
        
        //scan tuple of tokens
        String tupleT_regex = "(\\d*)([_a-zA-Z0-9]+)([,]([_a-zA-Z0-9]+))*";
        Pattern p = Pattern.compile(tupleT_regex);
        String[] mult_tuples_token = marking.split("+");
        
        //check token type (color class || domain)
        if(mult_tuples_token[0].contains(",")){ //domain
          tokens = new HashMap<Integer, String[]>();
          
        }else{ //color class
          tokens = new HashMap<Integer, String>();
        }
        
        String mult;
        String[] colors_token;
        Matcher m;
        for(var i = 0; i< mult_tuples_token.length; i++){
            multiplicity = 1;
            m = p.matcher(mult_tuples_token[i]);
            
            if(m.find()){
                mult = m.group(0);
                
                if(!mult.isEmpty()){
                    multiplicity = Integer.parseInt(mult);
                } 
                colors_token = mult_tuples_token[i].split(",");
                
                if(colors_token.length == 1){ //color class
                    tokens.put(multiplicity, colors_token[0]);
                    
                }else{ //domain
                    tokens.put(i, colors_token);
                }                                               
            }else{
                throw new NullPointerException("Initial marking exception : " + marking);
            }
        }
        // //call DataParser function to create Marking of extracted data
        dp.add_Marking(marking, tokens);
    }
}
