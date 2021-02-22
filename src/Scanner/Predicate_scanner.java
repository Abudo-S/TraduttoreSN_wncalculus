/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

/**
 *
 * @author dell
 */
public class Predicate_scanner{
    
    private static final String str_rx_predicate = "(\\s*[(]*\\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*"
                                                 + "(<=|>=|<|>|=|!\\s*=|\\s+in\\s+|\\s*!\\s*in\\s+)\\s*([_a-zA-Z]+[_a-zA-Z0-9]*"
                                                 + "(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*[)]*\\s*)";
    //scans an/a (inverted)predicate as arraylist of operators and variables/constants
    public HashMap<ArrayList<String>,Boolean> scan_predicate(String predicate) throws RuntimeException{ //could be inverted
        if(!predicate.matches(str_rx_predicate)){
           throw new RuntimeException("Can't match predicate: " + predicate);   
        }
        
        HashMap<ArrayList<String>,Boolean> predicates_with_invert = new HashMap<>();       
        predicates_with_invert.put(this.get_predicate_operators(predicate), this.get_invert_predicate(predicate));
        
        return predicates_with_invert;
    }
    
    private ArrayList<String> get_predicate_operators(String predicate){
        ArrayList<String> predicate_op = new ArrayList<>();
        Pattern p = Pattern.compile(str_rx_predicate);
        Matcher m = p.matcher(predicate);
        
        if(m.find()){
            predicate_op.add(m.group(2)); //op1
            predicate_op.add(m.group(4)); //operation
            predicate_op.add(m.group(5)); //op2
        }
        return null;
    }
    
    private Boolean get_invert_predicate(String predicate){
        Pattern p = Pattern.compile("\\s*!\\s*[(]\\s*");
        Matcher m = p.matcher(predicate);
        
        if(m.find()){
            return true;
        }
        return false;
    }
} 
