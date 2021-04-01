/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

/**
 *
 * @author dell
 */
//singleton
public class Predicate_scanner{ //sub-element of Guard_scanner
    
    private static Predicate_scanner instance = null;
    
    private static final String str_rx_predicate = "((\\s*[(]*\\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)"
                                                 + "\\s*(<=|>=|<|>|==|!\\s*=|\\s+in\\s+|\\s*!\\s*in\\s+)\\s*([_a-zA-Z]+[_a-zA-Z0-9]*"
                                                 + "(\\s*[+]\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)*)\\s*[)]*\\s*)|\\s*[(]*\\s*(True|False)[)]*\\s*)";
    
    private static final String str_rx_inverter = "\\s*[\\[]\\s*[!]\\s*[(]*\\s*[_a-zA-Z]+[_a-zA-Z0-9]*";
    
    private Predicate_scanner(){
        
    }
    
    /**
     * Note: scans an/a (inverted)predicate as ArrayList of operators and variables/constants
     * @param predicate String of predicate the will be elaborated
     * @return HashMap of predicate elements and their associated flag that will be true if predicate is inverted, false otherwise
     * @throws RuntimeException if predicate isn't matched by the matcher
     */
    public HashMap<ArrayList<String>,Boolean> scan_predicate(String predicate) throws RuntimeException{ //could be inverted
        if(!predicate.matches(str_rx_predicate)){
           throw new RuntimeException("Can't match predicate: " + predicate);   
        }
        
        HashMap<ArrayList<String>,Boolean> predicates_with_invert = new HashMap<>();       
        predicates_with_invert.put(this.get_predicate_operators(predicate), this.get_invert_predicate(predicate));

        return predicates_with_invert;
    }
    
    /**
     * 
     * @param predicate String of predicate the will be elaborated
     * @return ArrayList of predicate elements
     */
    private ArrayList<String> get_predicate_operators(String predicate){
        ArrayList<String> predicate_op = new ArrayList<>();
        Pattern p = Pattern.compile(str_rx_predicate);
        Matcher m = p.matcher(predicate);
        
        if(m.find()){
            
            if(m.group(1).contains("True")){
                predicate_op.add("True");
            }else if(m.group(1).contains("False")){
                predicate_op.add("False");
            }else{
                predicate_op.add(m.group(3)); //op1
                predicate_op.add(m.group(6)); //operation
                predicate_op.add(m.group(7)); //op2
            }
        }
        
        return predicate_op;
    }
    
    /**
     * 
     * @param predicate String of predicate in which we will search for Not-sign(!)
     * @return true if predicate is inverted, false otherwise
     */
    private Boolean get_invert_predicate(String predicate){
        Pattern p = Pattern.compile(str_rx_inverter);
        Matcher m = p.matcher(predicate);
        
        if(m.find()){
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Predicate_scanner get_instance(){
        
        if(instance == null){
            instance = new Predicate_scanner();
        }
        
        return instance;
    }
} 
