/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dell
 */
//singleton
public class Tuple_scanner { //sub-element of Arc_scanner
    
    private static Tuple_scanner instance = null;
    
    private static final String str_rx_tuple = "\\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\\s*[+-]\\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\\s*"
                                             + "([,]\\s*([_a-zA-Z]+[_a-zA-Z0-9]*([+]{2}|[-]{2})?(\\s*[+-]\\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\\s*)*"; //[<][>] were removed by Arc_scanner
    
    private Tuple_scanner(){
        
    }
    
    /**
     * 
     * @param tuple String of tuple data
     * @return a separated array of elements of tuple, using "," as separator
     * @throws RuntimeException 
     */
    public String[] scan_tuple(String tuple) throws RuntimeException{
        tuple = tuple.replaceAll("\\s*<\\s*", "").replaceAll("\\s*>\\s*", "").replaceAll("\\s+", "");
        
        Pattern p = Pattern.compile(str_rx_tuple);     
        Matcher m = p.matcher(tuple);
        
        if(! m.find()){
            throw new RuntimeException("Can't match tuple: " + tuple);
        }

        return tuple.split(",");
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Tuple_scanner get_instance(){
        
        if(instance == null){
            instance = new Tuple_scanner();
        }
        
        return instance;
    }
}
