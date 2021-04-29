/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import albero_sintattico.Syntactic_guard;
import albero_sintattico.Syntactic_tuple;
import albero_sintattico.Syntactic_predicate;
import albero_sintattico.SyntacticNode;
import albero_sintattico.SyntaxTree;
import albero_sintattico.Syntactic_arc;
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
    
    /**
     * print all syntax tree data including nodes connections with arc expressions 
     */
    public void SyntaxTree_all_data(){
        SyntaxTree synt_tree = SyntaxTree.get_instance();
        System.out.println();
        System.out.println("(SyntaxTree)Nodes connections via arcs \"node_name ------ arc_name: is inhibitor ,arc expression ----> node_name\": ");
        System.out.println();
        
        synt_tree.get_synt_places().stream().forEach(
                synt_p -> {
                    System.out.println(synt_p.get_name() + ", Next node: ");
                    HashMap<SyntacticNode, Syntactic_arc> next = synt_p.get_all_next();
                    
                    next.keySet().stream().forEach(
                            synt_next -> {
                                System.out.print("-------- " + next.get(synt_next).get_name() + ": ");
                                Syntactic_arc synt_arc = next.get(synt_next);
                                System.out.print("inib?" + synt_arc.get_type() + ", ");
                                LinkedHashMap<Syntactic_tuple, Integer> tuples_map = synt_arc.get_all_tuples();
                                
                                tuples_map.keySet().stream().forEach(
                                        synt_tuple -> {
                                            System.out.print(tuples_map.get(synt_tuple) + "*[");
                                            
                                            Syntactic_guard synt_guard = synt_tuple.get_syntactic_guard();
                                            System.out.print((synt_guard.get_invert_guard() == true)? "guard !#" : "guard #");
                                            
                                            LinkedHashMap<Syntactic_predicate, String> separated_predicates = synt_guard.get_separated_predicates();
                                            
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
                                                    
                                            System.out.print("# (");
                                            Arrays.stream(synt_tuple.get_tuple_elements()).forEach(
                                                    tp_element -> System.out.print(tp_element + ",")
                                            );
                                            System.out.print(") ");
                                            
                                            Syntactic_guard synt_filter = synt_tuple.get_syntactic_filter();
                                            System.out.print((synt_guard.get_invert_guard() == true)? "filter !#" : "filter #");
                                            
                                            LinkedHashMap<Syntactic_predicate, String> separated_predicates1 = synt_filter.get_separated_predicates();
                                            
                                            separated_predicates1.keySet().stream().forEach(
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
                                            System.out.print("# ");
                                        }
                                );
                                System.out.print("] -------> " + synt_next.get_name());
                                System.out.println();
                            }
                    );
                    System.out.println();
                }
        );
        
        synt_tree.get_synt_transitions().stream().forEach(
                synt_t -> {
                    System.out.println(synt_t.get_name() + ", Next node: ");
                    HashMap<SyntacticNode, Syntactic_arc> next = synt_t.get_all_next();
                    
                    next.keySet().stream().forEach(
                            synt_next -> {
                                System.out.print("-------- " + next.get(synt_next).get_name() + ": ");
                                Syntactic_arc synt_arc = next.get(synt_next);
                                System.out.print("inib?" + synt_arc.get_type() + ", ");
                                LinkedHashMap<Syntactic_tuple, Integer> tuples_map = synt_arc.get_all_tuples();
                                
                                tuples_map.keySet().stream().forEach(
                                        synt_tuple -> {
                                            System.out.print(tuples_map.get(synt_tuple) + "*[");
                                            
                                            Syntactic_guard synt_guard = synt_tuple.get_syntactic_guard();
                                            System.out.print((synt_guard.get_invert_guard() == true)? "guard !#" : "guard #");
                                            
                                            LinkedHashMap<Syntactic_predicate, String> separated_predicates = synt_guard.get_separated_predicates();
                                            
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
                                                    
                                            System.out.print("# (");
                                            Arrays.stream(synt_tuple.get_tuple_elements()).forEach(
                                                    tp_element -> System.out.print(tp_element + ",")
                                            );
                                            System.out.print(")");
                                            
                                            Syntactic_guard synt_filter = synt_tuple.get_syntactic_filter();
                                            System.out.print((synt_guard.get_invert_guard() == true)? "filter !#" : "filter #");
                                            
                                            LinkedHashMap<Syntactic_predicate, String> separated_predicates1 = synt_filter.get_separated_predicates();
                                            
                                            separated_predicates1.keySet().stream().forEach(
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
                                            System.out.print("# ");
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
    
    /**
     * 
     * @return single static instance
     */
    public static SyntaxTree_DataTester get_instance(){
        
        if(instance == null){
            instance = new SyntaxTree_DataTester();
        }
        
        return instance;
    }
}
