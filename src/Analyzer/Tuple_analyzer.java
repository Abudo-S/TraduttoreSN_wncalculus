/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analyzer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.expr.Domain;
import wncalculus.guard.Guard;
import wncalculus.wnbag.LinearComb;
import wncalculus.wnbag.WNtuple;

/**
 *
 * @author dell
 */
//singleton
public class Tuple_analyzer {
    
    private static final String str_rx_circ_projection = "\\s*(\\d*)\\s*" + ElementAnalyzer.get_str_rx_element(); //str_rx_element may match "All"
    private static final String str_rx_comb_element = "\\s*(\\d*)\\s*([_a-zA-Z]+[_a-zA-Z0-9]*)";
    private static final String str_rx_comb_operation = "[+-]";
    private final Projection_analyzer pa;
    private final Constant_analyzer ca;
    private static Place_syntax_table pst;
    //single instance
    private static Tuple_analyzer instance = null;
    
    private Tuple_analyzer(){
        pa = Projection_analyzer.get_instance();
        ca = Constant_analyzer.get_instance();
        pst = Place_syntax_table.get_instance();
    }
    
    //WNtuple object is consisted of linearcomb which is consisted of projections and subcl(constent)
    //tuples_elements list contains linearcombs
    public WNtuple analyze_arc_tuple(Guard g, String[] tuple_elements, String transition_name, String place_name, Domain d){
        ArrayList<LinearComb> tuple_combs = new ArrayList<>();
        
        //fill tuple_combs
        Arrays.stream(tuple_elements).forEach(
                tuple_e -> tuple_combs.add(this.analyze_tuple_element(tuple_e, transition_name, place_name))
        );
        
        return new WNtuple(null, tuple_combs, g, d, true);
    }
    
    //ElementaryFunction is (All, Projection, Subcl)
    private LinearComb analyze_tuple_element(String tuple_element, String transition_name, String place_name){
        Map<ElementaryFunction, Integer> element_t = new HashMap<>();
        Pattern p = Pattern.compile(str_rx_circ_projection);
        Matcher m = p.matcher(tuple_element);
        int mult = 1;
        
        if(m.find()){
            
            if(!m.group(1).isEmpty()){
                mult = Integer.parseInt(m.group(1));
            }
            //add ordered projection with its multiplicity
            element_t.put(pa.analyze_projection_element(tuple_element, transition_name), mult);
        }else{
            element_t = this.more_than_one_element(tuple_element, transition_name, place_name);
        }
        
        return new LinearComb(element_t);
    }
    
    private Map<ElementaryFunction, Integer> more_than_one_element(String tuple_element, String transition_name, String place_name){
        Pattern p = Pattern.compile(str_rx_comb_element);
        Matcher m = p.matcher(tuple_element);
        //to be completed
        //uses pa
        //uses ca
        //could create All ClassFunction
        return null;
    } 
    
    public static Tuple_analyzer get_instance(){

        if(instance == null){
            instance = new Tuple_analyzer();
        }
        
        return instance;
    }
}
