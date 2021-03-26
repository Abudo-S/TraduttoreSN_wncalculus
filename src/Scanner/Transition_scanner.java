/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author dell
 */
//singleton
public class Transition_scanner extends ElementScanner{
    
    private static Transition_scanner instance = null;
    
    private Transition_scanner(final Document doc){
        super(doc);
    }
    
     /**
     * 
     * @param Transition_element transition-element's tag from which we retrieve transition data
     */
    @Override
    public void scan_info(Element Transition_element){   
        LinkedHashMap<HashMap<ArrayList<String>, Boolean> ,String> guard = null; //guard has predicates with separators and each predicate might be inverted
        boolean invert_guard = false;
        
        if(Transition_element.getElementsByTagName("condition").getLength()>0){ //transition's condition may exist or not
            Node cond_node = Transition_element.getElementsByTagName("condition").item(0);
            
            if(cond_node.getNodeType() == Node.ELEMENT_NODE){
               Element guard_element = (Element) cond_node; 
               
               //acquire guard data
               Guard_scanner g_sc = Guard_scanner.get_instance();
               guard = g_sc.scan_guard(guard_element);
               invert_guard = g_sc.scan_invert_guard(guard_element);
            }   
        }
        //call DataParser function to create Transition of extracted data
        dp.add_Transition(Transition_element.getAttribute("id"), guard, invert_guard);
    }
    
    /**
     * 
     * @param doc the document from which we scan transitions
     * @return single static instance
     */
    public static Transition_scanner get_instance(Document doc){
        
        if(instance == null){
            instance = new Transition_scanner(doc);
        }
        
        return instance;
    }
}
