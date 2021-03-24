/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import struttura_sn.Arc;
import struttura_sn.Marking;
import struttura_sn.Node;
import struttura_sn.SN;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.expr.Interval;
import wncalculus.expr.Sort;
import wncalculus.guard.Guard;
import wncalculus.wnbag.LinearComb;
import wncalculus.wnbag.WNtuple;

/**
 *
 * @author dell
 */
//singleton
public class SN_DataTester {
    //single instance
    private static SN_DataTester instance = null;
    
    private SN_DataTester(){
        
    }
    
    public void SN_all_data(){
        SN sn = SN.get_instance();
       
        try{
            System.out.println("ColorClasses:");
            sn.get_C().stream().forEach(
                    x -> {
                        System.out.print(x.name());
                        
                        Interval[] intervals = x.getConstraints();
                        
                        if(intervals.length > 1){
                            System.out.print("= ");
                            
                           for(var i = 0; i < intervals.length; i++){
                               System.out.print(intervals[i].name());
                               
                               if(i != intervals.length-1){
                                   System.out.print(" + ");
                               }
                           }
                        }
                        
                        System.out.println();
                    }
            ); 
            
            System.out.println("Domains:");
            sn.get_DC().stream().forEach(
                    x -> {
                        System.out.print(x.name() + ": ");
                        Map<? extends Sort,Integer> ccs = x.asMap();
                        
                        ccs.keySet().stream().forEach(
                                cc -> System.out.print(ccs.get(cc) + "*" + cc.name() + " ")
                        );
                        
                        System.out.println();
                    });
            
            System.out.println("Variables:");
            sn.get_V().stream().forEach(x -> System.out.println(x.get_name() + ": " + x.get_colourClass().name()));
            
            System.out.println("Places:");
            sn.get_P().stream().forEach(x -> System.out.println(x.get_name()));
            
            System.out.println("Transitions:");
            sn.get_T().stream().forEach(
                    x -> {
                        System.out.print(x.get_name());   
                        Guard g = x.get_guard();
                        
                        if(g != null){
                            System.out.print(": " + g.toString());
                        }
                        
                        System.out.println();
                    }
            );
            
            System.out.println("initial Marking:");    
            Marking m0 = sn.get_initial_marking();
            
            m0.get_all_marked_Places().stream().forEach(
                    x -> {
                        System.out.print(x.get_name() + ": ");       
                        HashMap<ArrayList<LinearComb>, Integer> x_mark = m0.get_marking_of_place(x);
                        
                        x_mark.keySet().stream().forEach(
                                comb_list -> {
                                        System.out.print(x_mark.get(comb_list) + " *[");
                                        
                                        comb_list.stream().forEach(
                                                comb_element -> this.print_linear_comb(comb_element)
                                        );
                                }
                        );
                        
                        System.out.println("]");
                    }
            );
            
        }catch(Exception e){
            System.out.println(e + " in SN_DataTester");
        }
    }
    
    public void print_nodes_connections(){
        SN sn = SN.get_instance();
        System.out.println();
        System.out.println("(SN)Nodes connections via arcs \"node_name ------ arc_name: arc expression ----> node_name\": ");
        System.out.println();
        
        sn.get_P().stream().forEach(
                place -> {
                        System.out.println(place.get_name() + ", Next nodes: ");
                        HashMap<Node, Arc> next = place.get_next_nodes();
                        HashMap<Node, Arc> inib = place.get_inib_nodes();
                        
                        next.keySet().stream().forEach(
                                next_node -> {
                                    Arc arc = next.get(next_node);
                                    System.out.print("-------- " + arc.get_name() + ": ");
                                    Map<WNtuple, Integer> arc_tuples_map = (Map<WNtuple, Integer>) arc.get_tuple_bag().asMap();
                                     
                                    arc_tuples_map.keySet().stream().forEach(
                                            tuple -> {
                                                System.out.print(arc_tuples_map.get(tuple) + " *[");
                                                Guard g = tuple.guard();
                                                
                                                if(g != null){
                                                    System.out.print("guard #" + g.toString() + "# ");
                                                }
                                                
                                                tuple.getComponents().stream().forEach(
                                                        comb_element -> this.print_linear_comb(comb_element)
                                                );
                                                System.out.print(" / ");
                                            }
                                    );
                                    
                                    System.out.print("] -------> " + next_node.get_name());
                                    System.out.println();
                                }
                        );
                        
                        System.out.println(place.get_name() + ", Next inhibitors' nodes:");
                        inib.keySet().stream().forEach(
                                next_node -> {
                                    Arc arc = inib.get(next_node);
                                    System.out.print("-------- " + arc.get_name() + ": ");
                                    Map<WNtuple, Integer> arc_tuples_map = (Map<WNtuple, Integer>) arc.get_tuple_bag().asMap();
                                     
                                    arc_tuples_map.keySet().stream().forEach(
                                            tuple -> {
                                                System.out.print(arc_tuples_map.get(tuple) + " *[");
                                                Guard g = tuple.guard();
                                                
                                                if(g != null){
                                                    System.out.print("guard #" + g.toString() + "# ");
                                                }
                                                
                                                tuple.getComponents().stream().forEach(
                                                        comb_element -> this.print_linear_comb(comb_element)
                                                );
                                                System.out.print(" / ");
                                            }
                                    );
                                    
                                    System.out.print("] -------> " + next_node.get_name());
                                    System.out.println();
                                }
                        );
                        System.out.println("End of inhibitors:");
                        System.out.println();
                }
        );
        
        sn.get_T().stream().forEach(
                transition -> {
                    System.out.println(transition.get_name() + ", Next nodes: ");
                        HashMap<Node, Arc> next = transition.get_next_nodes();
                        
                        next.keySet().stream().forEach(
                                next_node -> {
                                    Arc arc = next.get(next_node);
                                    System.out.print("-------- " + arc.get_name() + ": ");
                                    Map<WNtuple, Integer> arc_tuples_map = (Map<WNtuple, Integer>) arc.get_tuple_bag().asMap();
                                     
                                    arc_tuples_map.keySet().stream().forEach(
                                            tuple -> {
                                                System.out.print(arc_tuples_map.get(tuple) + " *[");
                                                Guard g = tuple.guard();
                                                
                                                if(g != null){
                                                    System.out.print("guard #" + g.toString() + "# ");
                                                }
                                                
                                                tuple.getComponents().stream().forEach(
                                                        comb_element -> this.print_linear_comb(comb_element)
                                                );
                                                System.out.print(" / ");
                                            }
                                    );
                                    
                                    System.out.print("] -------> " + next_node.get_name());
                                    System.out.println();
                                }
                        );
                }
        );
    }
    
    private void print_linear_comb(LinearComb comb){
        Map<? extends ElementaryFunction, Integer> comb_map = comb.asMap();
        System.out.print("(");
        
        comb_map.keySet().stream().forEach(
                comb_map_element -> {
                    System.out.print(comb_map.get(comb_map_element) + " * ");
                    System.out.print(comb_map_element.getClass().getName());
                }
        );
        
        System.out.print(")  ");
    }
    
    public static SN_DataTester get_instance(){
        
        if(instance == null){
            instance = new SN_DataTester();
        }
        
        return instance;
    }
    
}
