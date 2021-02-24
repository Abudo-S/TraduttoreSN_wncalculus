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
public class Guard_scanner extends ElementScanner{
    private static Guard_scanner instance = null; 
            
    private static final String str_rx_guard = "(\\s*[(]*\\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*"
                                             + "(<=|>=|<|>|=|!\\s*=|\\s+in\\s+|\\s*!\\s*in\\s+)\\s*([_a-zA-Z]+[_a-zA-Z0-9]*"
                                             + "(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*[)]*\\s*)(\\s*(&amp;&amp;|[|]{2})(\\s*[(]*\\s*"
                                             + "([_a-zA-Z]+[_a-zA-Z0-9]*(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*(<=|>=|<|>|=|!\\s*=|\\s+in\\s+|\\s*!\\s*in\\s+)\\s*"
                                             + "([_a-zA-Z]+[_a-zA-Z0-9]*(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*[)]*\\s*))*";
    
    private static final String str_rx_predicate = "(\\s*[(]*\\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*"
                                                 + "(<=|>=|<|>|=|!\\s*=|\\s+in\\s+|\\s*!\\s*in\\s+)\\s*([_a-zA-Z]+[_a-zA-Z0-9]*"
                                                 + "(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*[)]*\\s*)";

    private static final String str_rx_separator = "(&amp;&amp;|[|]{2})";
    
    private Guard_scanner(final Document doc){
        super(doc);
    }
    
    public LinkedHashMap<HashMap<ArrayList<String>, Boolean> ,String> scan_guard(Element Guard_element){
        String guard = this.get_guard_txt(Guard_element);           
        guard = "[" + guard + "]";
        //remove invert-guard
        guard = guard.replaceFirst("\\s*[[]\\s*[!]\\s*[(]\\s*", "").replaceFirst("\\s*[)]\\s*[]]\\s*", guard);
        
        return this.scan_guard(guard);
    }
    
    public boolean scan_invert_guard(Element Guard_element){
        String guard = this.get_guard_txt(Guard_element);  
        guard = "[" + guard + "]";
        Pattern p = Pattern.compile("\\s*[[]\\s*[!]\\s*[(]\\s*");
        Matcher m = p.matcher(guard);
        
        if(m.find()){ //invert guard

            return true;
        }
        
        return false;
    }
    
    public LinkedHashMap<HashMap<ArrayList<String>, Boolean> ,String> scan_guard(String Guard){  
        if(Guard.isEmpty()){
            throw new NullPointerException("Can't add an empty guard");
        }
        //predicates with their separators
        return this.get_guard_map(Guard);
    }
    
    //
    private LinkedHashMap<HashMap<ArrayList<String>, Boolean> ,String> get_guard_map(String Guard) throws RuntimeException{ //map of (not-)inverted predicates with their separators
        LinkedHashMap<HashMap<ArrayList<String>, Boolean> ,String> guard = new LinkedHashMap<>();
        try{            
            // (inverted)predicates with their separators
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

                 if(m.find()){
                    predicates.add(pd_sc.scan_predicate(m.group(0)));
                 }
            }

            while(m1.find()){
                if(m1.find()){
                    separator = m1.group(0);
                    
                    if(separator.equals("||")){
                        separators.add("or");
                    }else{
                        separators.add("and");
                    } 
                }
            }

            for(int i = 0; i< predicates.size(); i++){
                if(i != predicates.size()-1){
                   guard.put(predicates.get(i), separators.get(i));
                }else{
                    guard.put(predicates.get(i), "");
                }
            }
        }catch(Exception e){
            System.out.println(e + " in Guard_scanner/get_guard_map()");
        }    
        
        return guard;
    }
    
    private String get_guard_txt(Element Guard_element){
        Node guard_txt_n = Guard_element.getElementsByTagName("text").item(0);
        
        if(guard_txt_n.getNodeType() == Node.ELEMENT_NODE){
           Element guard_txt = (Element) guard_txt_n;
           return guard_txt.getTextContent();
        }
        
        return "";
    }
    
    @Override
    public void scan_info(Element Guard_element){
        LinkedHashMap<HashMap<ArrayList<String>, Boolean> ,String>  guardOfpredicates = this.scan_guard(Guard_element);
        
        for(Map.Entry ent : guardOfpredicates.entrySet()){
          System.out.println("guard of predicates" + ent.getKey().toString() + "invert" + ent.getValue());
        }
        
    }
    
    public static Guard_scanner get_instance(Document doc){
        
        if(instance == null){
            instance = new Guard_scanner(doc);
        }
        
        return instance;
    }
    
}
