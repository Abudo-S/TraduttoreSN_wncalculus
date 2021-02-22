/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni_xml;

import java.util.*;
import struttura_sn.*;

/**
 *
 * @author dell
 */
//singleton class
public class DataParser {
    
    private static SN sn = new SN();
    
    public DataParser(){
       
    }
    
    public void add_ColorClass(String class_name, int start, int end, boolean circular){
        //to be completed
    }
      
    public void add_ColorClass(String class_name, ArrayList token_names, boolean circular){
        //to be completed  
    }
    
    public void add_ColorClass(String class_name, HashMap<String, ArrayList<String>> subclasses){
        //to be completed  
    }
    
    public void add_Variable(String variable_name, String variable_type){ //type = color class
        //to be completed
    }    
    
    public void add_Domain(String domain_name, ArrayList<String> colorclasses){
        //to be completed
    }
    
    public void add_Place(String place_name, String place_type){ //type = color class or domain
        //to be completed
    }
    
    public void add_Marking(String place_name, Map tokens){ //for place of color class/domain type
        //uses add_Marking_colorclass()
        //uses add_Marking_domain()
    }
    
    //tokens parameter will have 1d colors with their multiplicity
    private void add_Marking_colorclass(String place_name, HashMap<Integer, String> tokens){ //for place of color class type
        //to be completed
    }
    
    //tokens parameter will have (n)d colors with their multiplicity
    private void add_Marking_domain(String place_name, HashMap<Integer, String[]> tokens){ //for place of domain type of n dimension
        //to be completed
    }
    
    //format: HashMap<HashMap<ArrayList, Boolean>, String> = HashMap<HashMap<(inverted)predicate with variables/operators, invert_predicate>, separator with next predicate if exists>
    //predicates describe guard and each predicate might be inverted
    //Note: last element in seperators will be null 
    public void add_Transition(String Transition_name, LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>  guard, boolean invert_guard){
        //to be completed
    }
    
    //an Arc can have array of guards related with tuples
    public void add_Arc(String Arc_name, String arc_type, String from, String to, ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> guards,
    ArrayList<Boolean> invert_guards, ArrayList<String[]> tuples_elements, ArrayList<Integer> tuples_mult){ //type = "tarc/inhibitor"
        //to be completed
    }
    
    
    public SN get_sn(){
        return sn;
    }
}
