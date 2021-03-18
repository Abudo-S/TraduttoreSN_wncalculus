/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analyzer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struttura_sn.SN;
import struttura_sn.Token;
import wncalculus.classfunction.All;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.classfunction.Subcl;
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
    
    private static final String str_rx_circ_projection= "\\s*(\\d*)\\s*[_a-zA-Z]+[_a-zA-Z0-9]*)(([+]{2}|[-]{2}))"; //str_rx_element may match "All"
    private static final String str_rx_comb_element = "\\s*(\\d*)\\s*([_a-zA-Z]+[_a-zA-Z0-9]*)";
    private static final String str_rx_comb_operation = "([\\+-])";
    private final Projection_analyzer pa;
    private final Constant_analyzer ca;
    private static Place_syntax_table pst;
    private static SN sn;
    //single instance
    private static Tuple_analyzer instance = null;
    
    private Tuple_analyzer(){
        pa = Projection_analyzer.get_instance();
        ca = Constant_analyzer.get_instance();
        pst = Place_syntax_table.get_instance();
        sn = SN.get_instance();
    }
    
    //WNtuple object is consisted of linearcomb which is consisted of projections and subcl(constent)
    //tuples_elements list contains linearcombs
    public WNtuple analyze_arc_tuple(Guard g, String[] tuple_elements, String transition_name, String place_name, Domain d){
        ArrayList<LinearComb> tuple_combs = new ArrayList<>();
        
        //fill tuple_combs
        for(var i = 0; i < tuple_elements.length; i++){
            tuple_combs.add(this.analyze_tuple_element(tuple_elements[i], transition_name, place_name, i));
        }
        
        return new WNtuple(null, tuple_combs, g, d, true);
    }
    
    //ElementaryFunction is (All, Projection, Subcl)
    private LinearComb analyze_tuple_element(String tuple_element, String transition_name, String place_name, int element_index){ //used for arc expression tuple 
        Map<ElementaryFunction, Integer> element_t = new HashMap<>();
        Pattern p = Pattern.compile(str_rx_circ_projection);
        Matcher m = p.matcher(tuple_element);
        int mult = 1;
        
        if(m.find()){
            String element = m.group(2);
            
            if(!m.group(1).isEmpty()){
                mult = Integer.parseInt(m.group(1));
            }

            //add constant with its multiplicity
            element_t.put(ca.analyze_constant_element(element), mult);       
        }else{
            element_t = this.one_or_more_element(tuple_element, transition_name, place_name, element_index);
        }
        
        return new LinearComb(element_t);
    }
    
    private Map<ElementaryFunction, Integer> one_or_more_element(String tuple_element, String transition_name, String place_name, int element_index){
        Map<ElementaryFunction, Integer> element_t = new HashMap<>();
        Pattern p = Pattern.compile(str_rx_comb_element), p1 = Pattern.compile(str_rx_comb_operation);
        Matcher m = p.matcher(tuple_element), m1 = p1.matcher(tuple_element);        

        int mult, sign = 1;
                
        while(m.find()){
            mult = 1;
            String element = m.group(2);
            
            if(!m.group(1).isEmpty()){
                mult = Integer.parseInt(m.group(1));
            } 
            mult = sign * mult;
            
            if(element.equals("All")){
                //add class function of type "All"
                element_t.put(create_All_from_index(place_name, element_index), mult);
            }else if(sn.find_variable(element) != null){ 
                //add ordered projection with its multiplicity
                element_t.put(pa.analyze_projection_element(tuple_element, transition_name), mult);
            }else{
                //add constant with its multiplicity
                element_t.put(ca.analyze_constant_element(element), mult);
            }
            
            if(m1.find()){ //for element's sign in next matching
                
                if(m1.group(1).equals("-")){
                    sign = -1;
                }else{
                    sign = 1;
                }
            }
        }
        
        return element_t;
    }
    
    public LinearComb analyze_marking_tuple_element(String tuple_element, String place_name, int element_index){ //used for marking tuple
        Map<ElementaryFunction, Integer> element_m = new HashMap<>();
        
        Pattern p = Pattern.compile(str_rx_comb_element), p1 = Pattern.compile(str_rx_comb_operation);
        Matcher m = p.matcher(tuple_element), m1 = p1.matcher(tuple_element);        

        int mult, sign = 1;
                
        while(m.find()){
            mult = 1;
            String element = m.group(2);
            
            if(!m.group(1).isEmpty()){
                mult = Integer.parseInt(m.group(1));
            } 
            mult = sign * mult;
            
            if(element.equals("All")){
                //add class function of type "All"
                element_m.put(create_All_from_index(place_name, element_index), mult);
            }else{    
                try{
                    //assume that element is a constant
                    Subcl con = ca.analyze_constant_element(element);

                    if(con != null){ //add constant with its multiplicity
                        element_m.put(con, mult);
                    }else{ //if element isn't a constant then it's a color token
                        element_m.put(new Token(element, sn.find_colorClass(pst.get_place_values(place_name).get(element_index))), mult);
                    }
                }catch(Exception e){
                    System.out.println(e + " in Tuple_analyzer/analyze_marking_tuple_element");
                }
            }
            
            if(m1.find()){ //for element's sign in next matching
                
                if(m1.group(1).equals("-")){
                    sign = -1;
                }else{
                    sign = 1;
                }
            }
        }
        
        return new LinearComb(element_m);
    }
    
    //cc_index: is the index of colorclass in place type value(s) -> colorclass/domain
    private All create_All_from_index(String place_name, int cc_index){
        return All.getInstance(sn.find_colorClass(pst.get_place_values(place_name).get(cc_index)));
    }
    
    public static Tuple_analyzer get_instance(){

        if(instance == null){
            instance = new Tuple_analyzer();
        }
        
        return instance;
    }
}