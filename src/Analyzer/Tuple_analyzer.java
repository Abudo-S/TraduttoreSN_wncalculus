/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    
    private static final String str_rx_comb_operation = "[+-]";
    private Projection_analyzer pa;
    private Constant_analyzer ca;
    //single instance
    private static Tuple_analyzer instance = null;
    
    private Tuple_analyzer(){
        pa = Projection_analyzer.get_instance();
        ca = Constant_analyzer.get_instance();
    }
    
    //WNtuple object is consisted of linearcomb which is consisted of projections and subcl(constent)
    //tuples_elements list contains linearcombs
    public WNtuple analyze_arc_tuple(Guard g, String[] tuple_elements , Domain d){
        ArrayList<LinearComb> tuple_combs = new ArrayList<>();
        
        //fill tuple_combs
        Arrays.stream(tuple_elements).forEach(
                tuple_e -> tuple_combs.add(this.analyze_tuple_elements(tuple_e))
        );
        
        return new WNtuple(null, tuple_combs, g, d, true);
    }
    
    private LinearComb analyze_tuple_elements(String tuple_element){
        //uses Projection_analyzer
        //uses Constant_analyzer
        //could create All ClassFunction
        //creates union/diff
        return null;
    }   
    
    public static Tuple_analyzer get_instance(){

        if(instance == null){
            instance = new Tuple_analyzer();
        }
        
        return instance;
    }
}
