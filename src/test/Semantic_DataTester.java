/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import albero_sintattico.Syntactic_predicate;
import componenti.Variable_index_table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.expr.Domain;
import wncalculus.expr.Sort;
import wncalculus.guard.Guard;
import wncalculus.wnbag.LinearComb;
import wncalculus.wnbag.WNtuple;

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
    
    /**
     * 
     * @param separated_predicates LinkedHashMap of ordered syntactic predicates with their separators(with next predicate if exists) following the ordering of predicate insertion 
     * @param g the analysed equivalent guard to separated_predicates
     */
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
    
    /**
     * 
     * @param synt_pr syntactic predicate that contains predicate's data
     * @param g the analysed equivalent guard to synt_pr
     */
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
    
    /**
     * 
     * @param tuple_bag_map Map of tuples (that appear on the same arc) with their multiplicity 
     * @param arc_name the name of arc
     * @param transition_name the name of transition (that contains guard or with which this arc expression is connected that contains that guard)
     */
    public void test_semantic_arc(Map<WNtuple, Integer> tuple_bag_map, String arc_name ,String transition_name){
        System.out.println("Arc name(" + arc_name + "): ");
        System.out.println("STR arc expressions--->");
        tuple_bag_map.keySet().stream().forEach(
            tuple -> {
                this.test_domain(arc_name + "-tuple", tuple.getDomain());
                this.test_semantic_arc_tuple(new ArrayList<LinearComb>(tuple.getComponents()), tuple.guard(), transition_name);
            }
        );
        System.out.println("END arc expressions<---");
        System.out.println();
    }
    
    /**
     * 
     * @param tuple_combs ArrayList of linear combinations that we want to belongs to the same tuple
     * @param g tuple guard
     * @param transition_name the name of transition (that contains guard or with which this arc expression is connected that contains that guard)
     */
    public void test_semantic_arc_tuple(ArrayList<LinearComb> tuple_combs, Guard g, String transition_name){
        System.out.println("Tuple elements(belong to transition "+ transition_name + "):");
        
        if(g != null){
            System.out.print("[guard: " + g.toString() + "] ");
        }
                
        tuple_combs.stream().forEach(
                comb -> {
                    System.out.print("<");
                    this.test_semantic_linearcomb_elements((Map<ElementaryFunction, Integer>) comb.asMap());
                    System.out.print("> + ");
                }
        );
        
        System.out.println();
    }
    
    /**
     * 
     * @param comb_elements Map of linear combination elements
     */
    private void test_semantic_linearcomb_elements(Map<ElementaryFunction, Integer> comb_elements){
        
        comb_elements.keySet().stream().forEach(
                comb_element -> System.out.print(" " + comb_elements.get(comb_element) + "* " + comb_element.getClass().getName())
        );
    }
    
    /**
     * 
     * @param element_name the name of element that contains d
     * @param d the domain of tuple/transition
     */
    public void test_domain(String element_name, Domain d){ //for arc-WNtuple/transition/place objects
        System.out.print("Domain of " + element_name + ": ");
        Map<Sort,Integer> domain_elements = (Map<Sort,Integer>) d.asMap();
        
        domain_elements.keySet().stream().forEach(
                domain_element -> System.out.print("(" + domain_element.name() + " ^" + domain_elements.get(domain_element) + ") * ")
        );
        System.out.println();
    }
    
    /**
     * prints all class variables indices corresponding to transitions
     */
    public void print_all_proj_indices(){
        Variable_index_table vit = Variable_index_table.get_instance();
        
        HashMap<String, HashMap<String, HashMap<String, Integer>>> all = vit.get_all_reserved_indices();
        
        all.keySet().stream().forEach(
                transition_name -> {
                    HashMap<String, HashMap<String, Integer>> cc_vars_indices = all.get(transition_name);
                    System.out.println();
                    System.out.println(transition_name + ":--->> ");
                    
                    cc_vars_indices.keySet().forEach(
                            cc -> {
                                HashMap<String, Integer> vars_indices = cc_vars_indices.get(cc);
                                
                                vars_indices.keySet().stream().forEach(
                                        var ->{
                                            System.out.println(var + " :" +vars_indices.get(var));
                                        }
                                );
                            }
                    );
                }
        );
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Semantic_DataTester get_instance(){
        
        if(instance == null){
            instance = new Semantic_DataTester();
        }
        
        return instance;
    }
}
