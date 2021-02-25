/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import org.w3c.dom.Document;

/**
 *
 * @author dell
 */
//singleton
public class Tuple_scanner {
    private static Tuple_scanner instance = null;
    
    private Tuple_scanner(){
        
    }
    
    //returns a separated array of elements of tuple, using "," as separator
    public String[] scan_tuple(String tuple){
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
