/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dell
 */
//singleton
public class Tuple_scanner {
    
    private static Tuple_scanner instance = null;
    
    private static final String str_rx_tuple = "\\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\\s*[+-]\\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\\s*"
                                             + "([,]\\s*([_a-zA-Z]+[_a-zA-Z0-9]*(\\s*[+-]\\s*([_a-zA-Z]+[_a-zA-Z0-9]*))*)\\s*)*"; //[<][>] were removed by Arc_scanner
    
    private Tuple_scanner(){
        
    }
    
    //returns a separated array of elements of tuple, using "," as separator
    public String[] scan_tuple(String tuple) throws RuntimeException{
        Pattern p = Pattern.compile(str_rx_tuple);
        Matcher m = p.matcher(tuple);
        
        if(! m.find()){
            throw new RuntimeException("Can't match tuple: " + tuple);
        }
        tuple = tuple.replaceAll("\\s*<\\s*", "").replaceAll("\\s*>\\s*", "").replaceAll("\\s*", "");
        
        return tuple.split(",");
    }
    
    public static Tuple_scanner get_instance(){
        
        if(instance == null){
            instance = new Tuple_scanner();
        }
        
        return instance;
    }
}
