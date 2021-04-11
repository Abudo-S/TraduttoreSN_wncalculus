/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

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
    
    private static final String str_multOftuple = "\\s*(\\d+)\\s*[\\[]\\s*";
    
    private static final String str_multOftuple2 = "\\s*(\\d+)\\s*<\\s*";
    
    private static final String str_invertOfguard = "\\s*[\\[]\\s*[!]\\s*[(]\\s*";
    
    private static Arc_scanner instance = null;
    
    /**
     * 
     * @param doc the document from which we scan arcs
     */
    private Arc_scanner(final Document doc){
        super(doc);
    }
    
    /**
     * 
     * @param Arc_element arc-element's tag from which we retrieve arc data
     * @throws NullPointerException if tag "hlinscription" doesn't exist
     */
    @Override
    public void scan_info(Element Arc_element) throws NullPointerException{
             
      if(Arc_element.getElementsByTagName("hlinscription").getLength()>0){
            this.scan_arc(Arc_element);
            
      }else{
          throw new NullPointerException("Can't find arc expression of at least 1 variable: " + this.get_element_name(Arc_element));
      }
 
    }
    
    /**
     * 
     * @param Arc_element arc-element's tag from which we retrieve arc expression 
     */
    private void scan_arc(Element Arc_element){
        //arc expressions
        ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> guards = new ArrayList<>();
        ArrayList<Boolean> invert_guards = new ArrayList<>();
        ArrayList<String[]> tuples_elements = new ArrayList<>();
        ArrayList<Integer> tuples_mult = new ArrayList<>();
        ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> filters = new ArrayList<>();
        ArrayList<Boolean> invert_filters = new ArrayList<>();
        
        //arc attributes
        String arc_name = Arc_element.getAttribute("id");
        String from = Arc_element.getAttribute("source");
        String to = Arc_element.getAttribute("target");
        String arc_type = "tarc";
              
        if(Arc_element.getElementsByTagName("type").getLength()>0){
            arc_type = "inhibitor";
        }
        
        Node ex_node = Arc_element.getElementsByTagName("hlinscription").item(0);
        String ex_txt;
        String[] arc_expressions;
        
        if(ex_node.getNodeType() == Node.ELEMENT_NODE){
            Element ex = (Element) ex_node;
            ex_txt = ex.getElementsByTagName("text").item(0).getTextContent();
            //System.out.println(ex_txt);
            
            //separate arc expressions
            ex_txt = ex_txt.replaceAll("\\s*>\\s*[+]\\s*", "@");
            arc_expressions = ex_txt.split("@");
            //System.out.println(Arrays.toString(arc_expressions));
            
            //extract arc_expression data
            Pattern p;
            Matcher m;
            int tuple_mult;
            
            //fill guards, invert_guards, tuples_elements
            for(String arc_expression : arc_expressions){
                tuple_mult = 1;
                //separate multiplied arc-tuple/function from its associated guard if exists 
                arc_expression = arc_expression.replaceFirst("\\s*[\\]]\\s*<\\s*", "@");
                String[] arc_expression_data = arc_expression.split("@"); //may have 2 elements [0]: multiplicity with guard if exists, [1]: tuple internal elements
                //System.out.println(Arrays.toString(arc_expression_data));
                
                //temporally added
                    filters.add(new LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>());
                    invert_filters.add(Boolean.FALSE);
                //
                
                if(arc_expression_data.length == 1){ //case of tuple only
                    p = Pattern.compile(str_multOftuple2);
                    m = p.matcher(arc_expression_data[0]);

                    if(m.find()){ //multiplicity of function/tuple
                        tuple_mult = Integer.parseInt(m.group(1).replaceAll("\\s*", ""));
                        arc_expression_data[0] = arc_expression_data[0].replaceFirst(str_multOftuple2, "<");
                    } 
                    //extract tuple elements
                    Tuple_scanner t_sc = Tuple_scanner.get_instance();
                    String[] tuple_elements = t_sc.scan_tuple(arc_expression_data[0]);
                    tuples_elements.add(tuple_elements);
                    tuples_mult.add(tuple_mult);
                    guards.add(new LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>());
                    invert_guards.add(Boolean.FALSE);
                    
                }else{ //case of guarded tuple
                    p = Pattern.compile(str_multOftuple);
                    m = p.matcher(arc_expression_data[0]);

                    if(m.find()){ //multiplicity of function/tuple
                        tuple_mult = Integer.parseInt(m.group(1).replaceAll("\\s*", ""));
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
                    arc_expression_data[0] = arc_expression_data[0].replaceFirst("[\\[]", "");
                    Guard_scanner g_sc = Guard_scanner.get_instance();
                    guards.add(g_sc.scan_guard(arc_expression_data[0])); 

                    //extract tuple elements
                    Tuple_scanner t_sc = Tuple_scanner.get_instance();
                    String[] tuple_elements = t_sc.scan_tuple(arc_expression_data[1]);
                    tuples_elements.add(tuple_elements);
                }
            }
        }
        
        //call DataParser function to create Marking of extracted data
        dp.add_Arc(arc_name, arc_type, from, to, guards, invert_guards, tuples_elements, tuples_mult, filters, invert_filters);
    }
    
    /**
     * 
     * @param doc the document from which we scan arcs
     * @return single static instance
     */
    public static Arc_scanner get_instance(Document doc){
        
        if(instance == null){
            instance = new Arc_scanner(doc);
        }
        
        return instance;
    }
}
