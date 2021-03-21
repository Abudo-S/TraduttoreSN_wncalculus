/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import Albero_sintattico.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author dell
 */
//singleton
public class SyntaxTree_DataTester {
    //single instance
    private static SyntaxTree_DataTester instance = null;
    
    private SyntaxTree_DataTester(){
        
    }
    
    public void SyntaxTree_all_data(){
        SyntaxTree synt_tree = SyntaxTree.get_instance();
        
        synt_tree.get_synt_places().stream().forEach(
                synt_p -> {
                    System.out.println(synt_p.get_name() + ", Next node: ");
                    HashMap<SyntacticNode, Syntactic_arc> next = synt_p.get_all_next();
                    
                    next.keySet().stream().forEach(
                            synt_next -> {
                                System.out.print("-------- " + next.get(synt_next).get_name() + ": ");
                                Syntactic_arc synt_arc = next.get(synt_next);
                                System.out.print("inib?" + synt_arc.get_type() + ", ");
                                HashMap<Syntactic_tuple, Integer> tuples_map = synt_arc.get_all_tuples();
                                
                                tuples_map.keySet().stream().forEach(
                                        synt_tuple -> {
                                            System.out.print(tuples_map.get(synt_tuple) + "*[");
                                            
                                            Syntactic_guard synt_guard = synt_tuple.get_syntactic_guard();
                                            System.out.print((synt_guard.get_invert_guard() == true)? "guard !#" : "guard #");
                                            
                                            LinkedHashMap<Syntactic_predicate, String> separated_predicates = synt_guard.get_separated_predicates();
                                            
                                            separated_predicates.keySet().stream().forEach(
                                                    separated_p -> {
                                                            System.out.print((separated_p.get_invert_guard() == true)? "predicate !%" : "predicate %");
                                                            ArrayList<String> p_elements = separated_p.get_predicate_elements();
                                                            
                                                            p_elements.stream().forEach(
                                                                    element -> System.out.print(element + " ")
                                                            );
                                                            
                                                            String seperator = separated_predicates.get(separated_p);
                                                            if(seperator != null && !seperator.isEmpty()){
                                                                System.out.print(" " + seperator + " ");
                                                            }
                                                            System.out.print("% ");
                                                        }
                                            );
                                                    
                                            System.out.print("# (");
                                            Arrays.stream(synt_tuple.get_tuple_elements()).forEach(
                                                    tp_element -> System.out.print(tp_element + ",")
                                            );
                                        }
                                );
                                System.out.print(")] -------> " + synt_next.get_name());
                                System.out.println();
                            }
                    );
                    System.out.println();
                }
        );
        
        synt_tree.get_synt_transition().stream().forEach(
                synt_t -> {
                    System.out.println(synt_t.get_name() + ", Next node: ");
                    HashMap<SyntacticNode, Syntactic_arc> next = synt_t.get_all_next();
                    
                    next.keySet().stream().forEach(
                            synt_next -> {
                                System.out.print("-------- " + next.get(synt_next).get_name() + ": ");
                                Syntactic_arc synt_arc = next.get(synt_next);
                                System.out.print("inib?" + synt_arc.get_type() + ", ");
                                HashMap<Syntactic_tuple, Integer> tuples_map = synt_arc.get_all_tuples();
                                
                                tuples_map.keySet().stream().forEach(
                                        synt_tuple -> {
                                            System.out.print(tuples_map.get(synt_tuple) + "*[");
                                            
                                            Syntactic_guard synt_guard = synt_tuple.get_syntactic_guard();
                                            System.out.print((synt_guard.get_invert_guard() == true)? "guard !#" : "guard #");
                                            
                                            LinkedHashMap<Syntactic_predicate, String> separated_predicates = synt_guard.get_separated_predicates();
                                            
                                            separated_predicates.keySet().stream().forEach(
                                                    separated_p -> {
                                                            System.out.print((separated_p.get_invert_guard() == true)? "predicate !%" : "predicate %");
                                                            ArrayList<String> p_elements = separated_p.get_predicate_elements();
                                                            
                                                            p_elements.stream().forEach(
                                                                    element -> System.out.print(element + " ")
                                                            );
                                                            
                                                            String seperator = separated_predicates.get(separated_p);
                                                            if(seperator != null && !seperator.isEmpty()){
                                                                System.out.print(" " + seperator + " ");
                                                            }
                                                            System.out.print("% ");
                                                        }
                                            );
                                                    
                                            System.out.print("# (");
                                            Arrays.stream(synt_tuple.get_tuple_elements()).forEach(
                                                    tp_element -> System.out.print(tp_element + ",")
                                            );
                                            
                                        }                                       
                                );
                                System.out.print(")] -------> " + synt_next.get_name());
                                System.out.println();
                            }
                    );
                    System.out.println();
                }
        );
    }
    
    public static SyntaxTree_DataTester get_instance(){
        
        if(instance == null){
            instance = new SyntaxTree_DataTester();
        }
        
        return instance;
    }
}
