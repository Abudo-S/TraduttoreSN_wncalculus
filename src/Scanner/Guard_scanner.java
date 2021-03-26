/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import java.util.*;
import java.util.regex.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author dell
 */
//singleton
public class Guard_scanner{ //sub-element of Transition_scanner/Arc_scanner
    
    private static Guard_scanner instance = null; 
            
    private static final String str_rx_guard = "((\\s*[(]*\\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)"
                                             + "\\s*(<=|>=|<|>|=|!\\s*=|\\s+in\\s+|\\s*!\\s*in\\s+)\\s*([_a-zA-Z]+[_a-zA-Z0-9]*"
                                             + "(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*[)]*\\s*)|\\s*[(]*\\s*(True|False)[)]*\\s*)"
                                             + "(\\s*([&]{2}|[|]{2})((\\s*[(]*\\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?"
                                             + "(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*(<=|>=|<|>|=|!\\s*=|\\s+in\\s+|\\s*!\\s*in\\s+)\\s*"
                                             + "([_a-zA-Z]+[_a-zA-Z0-9]*(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*[)]*\\s*)|\\s*[(]*\\s*(True|False)[)]*\\s*))*";
    
    private static final String str_rx_predicate = "((\\s*[(]*\\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)"
                                                 + "\\s*(<=|>=|<|>|=|!\\s*=|\\s+in\\s+|\\s*!\\s*in\\s+)\\s*([_a-zA-Z]+[_a-zA-Z0-9]*"
                                                 + "(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*[)]*\\s*)|\\s*[(]*\\s*(True|False)[)]*\\s*)";
    
    private static final String str_rx_inverter = "\\s*[\\[]\\s*[!]\\s*[(]*\\s*[_a-zA-Z]+[_a-zA-Z0-9]*";

    private static final String str_rx_separator = "([&]{2}|[|]{2})";
    
    private Guard_scanner(){
        
    }
    
    /**
     * 
     * @param Guard_element the element from which we'll retrieve guard all data
     * @return 
     */
    public LinkedHashMap<HashMap<ArrayList<String>, Boolean> ,String> scan_guard(Element Guard_element){
        String guard = this.get_guard_txt(Guard_element);           
        guard = "[" + guard + "]";
        //remove invert-guard
        guard = guard.replaceFirst(str_rx_inverter, "").replaceFirst("\\s*[)]\\s*[]]\\s*", guard).replaceFirst("[\\[]", "").replaceFirst("[\\]]", "");
        
        return this.scan_guard(guard);
    }
    
    /**
     * 
     * @param Guard_element the element in which we'll search for Not-sign(!)
     * @return 
     */
    public boolean scan_invert_guard(Element Guard_element){
        String guard = this.get_guard_txt(Guard_element);  
        guard = "[" + guard + "]";
        Pattern p = Pattern.compile(str_rx_inverter);
        Matcher m = p.matcher(guard);
        
        if(m.find()){ //invert guard
            return true;
        }
        
        return false;
    }
    
    /**
     * 
     * @param Guard String of guard the will be elaborated
     * @return LinkedHashMap of ordered predicated with their separators following the ordering of predicate insertion 
     * @throws NullPointerException if Guard is an empty String
     */
    public LinkedHashMap<HashMap<ArrayList<String>, Boolean> ,String> scan_guard(String Guard) throws NullPointerException{  
        if(Guard.isEmpty()){
            throw new NullPointerException("Can't add an empty guard");
        }
        //predicates with their separators
        return this.get_guard_map(Guard);
    }
    
    /**
     * 
     * @param Guard String of guard the will be elaborated
     * @return LinkedHashMap of ordered predicated with their separators following the ordering of predicate insertion
     * @throws RuntimeException if Guard isn't matched by the matcher
     */
    private LinkedHashMap<HashMap<ArrayList<String>, Boolean> ,String> get_guard_map(String Guard) throws RuntimeException{ //map of (not-)inverted predicates with their separators
        LinkedHashMap<HashMap<ArrayList<String>, Boolean> ,String> guard = new LinkedHashMap<>();
        
        try{
            //(inverted)predicates with their separators
            Pattern p = Pattern.compile(str_rx_guard);
            Matcher m = p.matcher(Guard);

            if(!m.find()){
                throw new RuntimeException("Can't match guard: " + Guard);
            }

            p = Pattern.compile(str_rx_predicate);
            m = p.matcher(Guard);
            Pattern p1 = Pattern.compile(str_rx_separator);
            Matcher m1 = p1.matcher(Guard);

            Predicate_scanner pd_sc = Predicate_scanner.get_instance();
            ArrayList<HashMap<ArrayList<String>, Boolean>> predicates= new ArrayList<>();
            ArrayList<String> separators = new ArrayList<>();
            String separator;
            
            while(m.find()){
                 predicates.add(pd_sc.scan_predicate(m.group(1)));   
            }

            while(m1.find()){             
                separator = m1.group(1);

                if(separator.equals("||")){
                    separators.add("or");
                }else{
                    separators.add("and");
                } 
            }
            
            for(int i = 0; i < predicates.size(); i++){
                if(i != predicates.size()-1){
                   guard.put(predicates.get(i), separators.get(i));
                }else{
                    guard.put(predicates.get(i), null);
                }
            }
        }catch(Exception e){
            System.out.println(e + " in Guard_scanner/get_guard_map()");
        }    

        return guard;
    }
    
    /**
     * 
     * @param Guard_element the element from which we will retrieve guard's text
     * @return String of guard's text if found
     */
    private String get_guard_txt(Element Guard_element){
        Node guard_txt_n = Guard_element.getElementsByTagName("text").item(0);
        
        if(guard_txt_n.getNodeType() == Node.ELEMENT_NODE){
           Element guard_txt = (Element) guard_txt_n;
           return guard_txt.getTextContent();
        }
        
        return "";
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Guard_scanner get_instance(){
        
        if(instance == null){
            instance = new Guard_scanner();
        }
        
        return instance;
    }
    
}
