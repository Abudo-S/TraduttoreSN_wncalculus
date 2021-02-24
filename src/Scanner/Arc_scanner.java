/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author dell
 */
//singleton
public class Arc_scanner extends ElementScanner{
    
    private static Arc_scanner instance = null;
    
    private Arc_scanner(final Document doc){
        super(doc);
    }
    
    @Override
    public void scan_info(Element Arc_element){
             
      if(Arc_element.getElementsByTagName("hlinscription").getLength()>0){
            this.scan_arc(Arc_element);
            
      }else{
          throw new NullPointerException("Can't find arc expression of at least 1 variable: " + this.get_element_name(Arc_element));
      }
 
    }
    
    private void scan_arc(Element Arc_element){
        //arc expressions
        ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> guards = new ArrayList<>();
        ArrayList<Boolean> invert_guards = new ArrayList<>();
        ArrayList<String[]> tuples_elements = new ArrayList<>();
        ArrayList<Integer> tuples_mult = new ArrayList<>();
        
        //arc attributes
        String arc_name = Arc_element.getAttribute("id");
        String from = Arc_element.getAttribute("source");
        String to = Arc_element.getAttribute("target");
        String arc_type = "tarc";
              
        if(Arc_element.getElementsByTagName("type").getLength()>0){
            arc_type = "inhibitor";
        }
        
        Node ex_node = Arc_element.getElementsByTagName("hlinscription").item(0);
        String ex_txt = "";
        String[] arc_expressions;
        
        if(ex_node.getNodeType() == Node.ELEMENT_NODE){
            Element ex = (Element) ex_node;
            ex_txt = ex.getElementsByTagName("text").item(0).getTextContent();
            
            //separate arc expressions
            ex_txt = ex_txt.replaceAll("\\s*&gt;\\s*[+]\\s*", "@");
            arc_expressions = ex_txt.split("@");
            
            //extract arc_expression data
            String str_multOftuple = "\\s*(\\d+)\\s*[[]\\s*";
            String str_invertOfguard = "\\s*[[]\\s*[!]\\s*[(]\\s*";
            Pattern p;
            Matcher m;
            int tuple_mult;
            
            //fill guards, invert_guards, tuples_elements
            for(String arc_expression : arc_expressions){
                tuple_mult = 1;
                //separate multiplied arc-tuple/function from its associated guard if exists 
                arc_expression = arc_expression.replaceFirst("\\s*[]]\\s*&lt;\\s*", "@");
                String[] arc_expression_data = arc_expression.split("@"); //should have 2 elements [0]: multiplicity with guard if exists, [1]: tuple internal elements
                p = Pattern.compile(str_multOftuple);
                m = p.matcher(arc_expression_data[0]);
                
                if(m.find()){ //multiplicity of function/tuple
                    tuple_mult = Integer.parseInt(m.group(0).replaceAll("\\s*", ""));
                    arc_expression_data[0] = arc_expression_data[0].replaceFirst(str_multOftuple, "[");
                }
                tuples_mult.add(tuple_mult);
                
                p = Pattern.compile(str_invertOfguard);
                m = p.matcher(arc_expression_data[0]);
                
                if(m.find()){ //invert guard
                    invert_guards.add(Boolean.TRUE);
                    arc_expression_data[0] = arc_expression_data[0].replaceFirst(str_invertOfguard, "[");
                }else{
                    invert_guards.add(Boolean.FALSE);
                }
                
                //guard of function/tuple
                arc_expression_data[0] = arc_expression_data[0].replaceFirst("[[]", "");
                Guard_scanner g_sc = Guard_scanner.get_instance(doc);
                guards.add(g_sc.scan_guard(arc_expression_data[0])); 
                
                //extract tuple elements
                arc_expression_data[1] = arc_expression_data[1].replaceFirst("\\s*", "");
                String[] tuple_elements = arc_expression_data[1].split(",");
                tuples_elements.add(tuple_elements);
            }
        }
        
        //call DataParser function to create Marking of extracted data
        dp.add_Arc(arc_name, arc_type, from, to, guards, invert_guards, tuples_elements, tuples_mult);
    }
    
    public static Arc_scanner get_instance(Document doc){
        
        if(instance == null){
            instance = new Arc_scanner(doc);
        }
        
        return instance;
    }
}
