/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer;

import componenti.Place_syntax_table;
import componenti.Marking_tokens_table;
import eccezioni.UnsupportedLinearCombElement;
import test.Semantic_DataTester;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struttura_sn.SN;
import struttura_sn.Token;
import wncalculus.classfunction.All;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.classfunction.Projection;
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
    
    //private static final String str_rx_circ_projection= "\\s*(\\d*)\\s*([_a-zA-Z]+[_a-zA-Z0-9]*)(([\\+]{2}|[-]{2}))"; //str_rx_element may match "All"
    //group1 = (n), group2 = (variable_name), group6 = (+|-)    
    private static final String str_rx_comb_element = "\\s*(\\d*)\\s*(" + ElementAnalyzer.get_str_rx_element() + ")" + Tuple_analyzer.str_rx_comb_operation; 
    private static final String str_rx_comb_operation = "\\s*([\\+-])?\\s*";
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
    
    /**
     * 
     * @param g the guard of tuple
     * @param tuple_elements all tuple elements
     * @param transition_name the name of transition with which an arc expression is connected and contains that tuple
     * @param place_name the place should is connected with tuple and tuple should follow its colour class ordering to create ClassFunction (All) if it's needed
     * @param d the domain of transition with which an arc expression is connected and contains that tuple
     * @return the analysed tuple 
     */
    //WNtuple object is consisted of linearcomb which is consisted of projections and subcl(constent)
    //tuples_elements list contains linearcombs
    public WNtuple analyze_arc_tuple(Guard g, String[] tuple_elements, String transition_name, String place_name, Domain d){
        ArrayList<LinearComb> tuple_combs = new ArrayList<>();
        
        //fill tuple_combs
        for(var i = 0; i < tuple_elements.length; i++){
            tuple_combs.add(this.analyze_tuple_element(tuple_elements[i], transition_name, place_name, i));
        }
        
//        System.out.println(sn.find_colorClass("Risorsa").getConstraints()[0].lb());      
//        tuple_combs.stream().forEach(
//                comb -> {
//                    System.out.println("comb start:");
//                    comb.asMap().keySet().stream().forEach(
//                         comb_element -> System.out.println(comb_element.indexSet().iterator().next())
//                    );
//                    System.out.println("comb end:");
//                }
//        );
//        System.out.println(place_name + "," + tuple_combs.size() + "," + transition_name);
        //Semantic_DataTester.get_instance().test_semantic_arc_tuple(tuple_combs, g, transition_name);
        return new WNtuple(null, tuple_combs, g, d, false);
    }
    
    /**
     * 
     * @param tuple_element tuple element from which we'll retrieve its linear combination
     * @param place_name the place should is connected with tuple and tuple should follow its colour class ordering to create ClassFunction (All) if it's needed
     * @param element_index tuple-element's index that is used in case of creating ClassFunction (All) to determine which class will be used
     * @return linear combination of found ElementaryFunctions and their multiplicity
     * Note: ElementaryFunction is (All, Projection, Subcl) here
     */
    private LinearComb analyze_tuple_element(String tuple_element, String transition_name, String place_name, int element_index){ //used for arc expression tuple 
        Map<ElementaryFunction, Integer> element_t = new HashMap<>();
        Pattern p = Pattern.compile(str_rx_comb_element);
        Matcher m = p.matcher(tuple_element);        

        int mult, sign = 1;
        String op;        
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
                element_t = this.update_or_add(element_t, pa.analyze_projection_element(element, transition_name), mult);
            }else{
                //add constant with its multiplicity
                Subcl con = ca.analyze_constant_element(element);
                
                if(con != null){
                    element_t = this.update_or_add(element_t, con, mult);
                }else{
                    throw new UnsupportedLinearCombElement("Can't add this element in a linear combination of arc tuple: " + element);     
                }  
            }
            
            op = m.group(6);
            if(op != null){ //for element's sign in next matching
                
                if(op.equals("-")){
                    sign = -1;
                }else{
                    sign = 1;
                }
            }
        }

        return new LinearComb(element_t);
    }
    
    /**
     * 
     * @param tuple_element tuple element from which we'll retrieve its linear combination
     * @param place_name the place should is connected with tuple and tuple should follow its colour class ordering to create ClassFunction (All) if it's needed
     * @param element_index tuple-element's index that is used in case of creating ClassFunction (All) to determine which class will be used
     * @return linear combination of found ElementaryFunctions and their multiplicity
     * Note: ElementaryFunction is (All, Token, Subcl) here
     */
    public LinearComb analyze_marking_tuple_element(String tuple_element, String place_name, int element_index){ //used for marking tuple
        Map<ElementaryFunction, Integer> element_m = new HashMap<>();
        Marking_tokens_table cmtt = Marking_tokens_table.get_instance();
        Pattern p = Pattern.compile(str_rx_comb_element);
        Matcher m = p.matcher(tuple_element);        

        int mult, sign = 1;
        String op;        
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
                        element_m = this.update_or_add(element_m, con, mult);
                    }else if(sn.find_variable(element) == null){ //if element isn't a constant then it's a color token (check if it isn't a variable)
                        Token t = cmtt.get_created_similar_token(element);
                        
                        if(t == null){
                            t = new Token(element, sn.find_colorClass(pst.get_place_values(place_name).get(element_index)));
                            cmtt.add_created_token(t);
                        }
                        
                        element_m = this.update_or_add(element_m, t, mult);
                    }else{
                        throw new UnsupportedLinearCombElement("Can't add this element in a linear combination of marking tuple: " + element);    
                    }
                }catch(Exception e){
                    System.out.println(e + " in Tuple_analyzer/analyze_marking_tuple_element");
                }
            }
            
            op = m.group(6);

            if(op != null){ //for element's sign in next matching
                
                if(op.equals("-")){
                    sign = -1;
                }else{
                    sign = 1;
                }
            }
        }
        
        return new LinearComb(element_m);
    }
    
    /**
     * 
     * @param place_name the place should is connected with tuple and tuple should follow its colour class ordering to create ClassFunction (All)
     * @param cc_index the index of colour class that we want to create ClassFunction (All) form it
     * @return ClassFunction (All)
     */
    //cc_index: is the index of colorclass in place type value(s) -> colorclass/domain
    private All create_All_from_index(String place_name, int cc_index){
        return All.getInstance(sn.find_colorClass(pst.get_place_values(place_name).get(cc_index)));
    }
    
    /**
     * 
     * @param element_m Map of ElementaryFunctions(Projection/Constant"Subcl"/Token) that will be updated
     * @param ef ElementaryFunction(Projection/Constant"Subcl"/Token)
     * @param mult ElementaryFunction's multiplicity
     * @return the updated Map of ElementaryFunctions(Projection/Constant"Subcl"/Token) and their multiplicity
     */
    public Map<ElementaryFunction, Integer> update_or_add(Map<ElementaryFunction, Integer> element_m, ElementaryFunction ef, int mult){
        boolean found = false;
        
        if(ef instanceof Projection){ 
            Projection p1 = (Projection) ef;
            
            for(ElementaryFunction map_element : element_m.keySet()){
                
                if(map_element instanceof Projection){
                    Projection p2 = (Projection) map_element;

                    if(p1.getIndex() == p2.getIndex()){
                        element_m.put(ef, element_m.get(ef) + mult);
                        found = true;
                    }
                }
            }
            
        }else if(ef instanceof Token){
            Token t1 = (Token) ef;
            
            for(ElementaryFunction map_element : element_m.keySet()){   
                
                if(map_element instanceof Token){
                Token t2 = (Token) map_element;
                
                    if(t1.get_Token_value().equals(t2.get_Token_value())){
                        element_m.put(ef, element_m.get(ef) + mult);
                        found = true;
                    }
                }
            }
            
        }else{ //constant
            Subcl c1 = (Subcl) ef;
            
            for(ElementaryFunction map_element : element_m.keySet()){   
                
                if(map_element instanceof Subcl){
                    Subcl c2 = (Subcl) map_element;

                    if(c1.index() == c2.index()){
                        element_m.put(ef, element_m.get(ef) + mult);
                        found = true;
                    }
                }
            }
        }
        
        if(!found){
            element_m.put(ef, mult);
        }
        
        return element_m;
    }
    
    /**
     * 
     * @return regex of combination between 2 elements
     */
    public static String get_str_rx_comb_operation(){
        return str_rx_comb_operation;
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Tuple_analyzer get_instance(){

        if(instance == null){
            instance = new Tuple_analyzer();
        }
        
        return instance;
    }
}
