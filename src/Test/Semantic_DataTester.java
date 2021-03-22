/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import Albero_sintattico.Syntactic_predicate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import wncalculus.expr.Domain;
import wncalculus.guard.Guard;

/**
 *
 * @author dell
 */
//singleton
public class Semantic_DataTester { //for guards and tuples 
    //single instance
    private static Semantic_DataTester instance = null;
    
    private Semantic_DataTester(){
        
    }
    
    public void test_semantic_guard(LinkedHashMap<Syntactic_predicate, String> separated_predicates, Guard g){
        
        if(!separated_predicates.isEmpty()){
            
            separated_predicates.keySet().stream().forEach(
                    separated_p -> {
                        System.out.print((separated_p.get_invert_guard() == true)? "predicate !% " : "predicate % ");
                        ArrayList<String> p_elements = separated_p.get_predicate_elements();

                        p_elements.stream().forEach(
                                element -> System.out.print(element + " ")
                        );

                        String seperator = separated_predicates.get(separated_p);
                        System.out.print("% ");

                        if(seperator != null && !seperator.isEmpty()){
                            System.out.print(" " + seperator + " ");
                        }   
                    }
            );
        
        System.out.print(" >>>equivalent guard>>> " + g.toString());
        System.out.println();
        }
    }
    
    public void test_semantic_predicate(Syntactic_predicate synt_pr, Guard g){
        System.out.print((synt_pr.get_invert_guard() == true)? "predicate !% " : "predicate % ");
        ArrayList<String> p_elements = synt_pr.get_predicate_elements();

        p_elements.stream().forEach(
                element -> System.out.print(element + " ")
        );

        System.out.print("% ");
        System.out.print(" >>>equivalent predicate>>> " + g.toString() + "Class type: " + g.getClass().getName());
        System.out.println();
    }
    
    public void test_semantic_arc(){
        //to be completed
    }
    
    public void test_semantic_arc_tuple(){
        //to be completed
    }
    
    public void test_domain(String element_name, Domain d){ //for WNtuple/transition objects
        //to be completed
    }
    
    public static Semantic_DataTester get_instance(){
        
        if(instance == null){
            instance = new Semantic_DataTester();
        }
        
        return instance;
    }
}
