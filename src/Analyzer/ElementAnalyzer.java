/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analyzer;

import struttura_sn.SN;
//import wncalculus.util.ComplexKey;

/**
 *
 * @author dell
 */
public abstract class ElementAnalyzer {
    
    protected static final String str_rx_element = "([_a-zA-Z]+[_a-zA-Z0-9]*)(([+]{2}|[-]{2})?)";
    protected SN sn;
    
//    public int generate_index(String name1, String name2, int flag){
//        ComplexKey ck = new ComplexKey(name1, name2,flag);
//        return ck.hashCode();
//    }   
    
    public String get_str_rx_element(){
        return str_rx_element;
    }
}
