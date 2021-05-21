/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer;

import struttura_sn.SN;
//import wncalculus.util.ComplexKey;

/**
 *
 * @author dell
 */
//part of facade pattern with semantic analyzer and traduttore sn
public abstract class ElementAnalyzer {
    
    protected static final String str_rx_element = "(([_a-zA-Z]+[_a-zA-Z0-9]*)|[@]([_a-zA-Z0-9]*)([\\[](\\d+)[\\]])?)(([\\+]{2}|[-]{2})?)";
    protected static SN sn;
    
//    public int generate_index(String name1, String name2, int flag){
//        ComplexKey ck = new ComplexKey(name1, name2,flag);
//        return ck.hashCode();
//    }   
    
    /**
     * 
     * @return regex of guard/tuple element
     */
    public static String get_str_rx_element(){
        return str_rx_element;
    }
}
