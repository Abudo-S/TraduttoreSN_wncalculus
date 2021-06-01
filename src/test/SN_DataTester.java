/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import struttura_sn.ArcAnnotation;
import struttura_sn.Marking;
import struttura_sn.Node;
import struttura_sn.SN;
import struttura_sn.Token;
import wncalculus.classfunction.All;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.classfunction.Projection;
import wncalculus.classfunction.Subcl;
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
    
    /**
     * print SN ArrayLists data
     */
    public void SN_all_data(){
        SN sn = SN.get_instance();
       
        try{
            System.out.println("ColorClasses:");
            sn.get_C().stream().forEach(
                    x -> {
                        System.out.print(x.name() + " ");
                        boolean circ = x.isOrdered();
                        
                        if(circ){
                            System.out.print("{circular} ");
                        }
                        
                        
                        Interval[] intervals = x.getConstraints();
                        
                        if(intervals.length > 1){
                            System.out.print("= ");
                            
                           for(var i = 0; i < intervals.length; i++){
                               System.out.print(intervals[i].name() + intervals[i].toString());
                               
                               if(i != intervals.length-1){
                                   System.out.print(" + ");
                               }
                           }
                        }else{
                            System.out.print("= " + x.name() + intervals[0].toString());
                        }
                        
                        System.out.println();
                    }
            ); 
            System.out.println();
            
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
            System.out.println();
            
            System.out.println("Variables:");
            sn.get_V().stream().forEach(x -> System.out.println(x.get_name() + ": " + x.get_colourClass().name()));
            System.out.println();
            
            System.out.println("Places:");
            sn.get_P().stream().forEach(
                    x -> {
                        //Semantic_DataTester.get_instance().test_domain(x.get_name(), x.get_node_domain());
                        System.out.println(x.get_name() + ": " + x.get_type());
                    }
            );
            System.out.println();
            
            System.out.println("Transitions:");
            sn.get_T().stream().forEach(
                    x -> {
                        //Semantic_DataTester.get_instance().test_domain(x.get_name(), x.get_node_domain());
                        System.out.print(x.get_name());   
                        Guard g = x.get_guard();
                        
                        if(g != null){
                            System.out.print(": " + g.toString());
                        }else{
                            System.out.print(": True");
                        }
                        
                        System.out.println();
                    }
            );
            System.out.println();
            
            System.out.println("initial Marking:");    
            Marking m0 = sn.get_initial_marking();
            
            m0.get_all_marked_Places().stream().forEach(
                    x -> {
                        System.out.print(x.get_name() + ": ");       
                        HashMap<ArrayList<LinearComb>, Integer> x_mark = m0.get_marking_of_place(x);
                        
                        x_mark.keySet().stream().forEach( //for each marking tuple
                                comb_list -> {
                                        System.out.print(x_mark.get(comb_list) + " *<");
                                        
                                        comb_list.stream().forEach(
                                                comb_element -> this.print_linear_comb(comb_element)
                                        );
                                        System.out.print(">   ");
                                }
                        );
                        System.out.println();
                    }
            );
            System.out.println();
            
        }catch(Exception e){
            System.out.println(e + " in SN_DataTester");
        }
    }
    
    /**
     * print SN nodes connections between places/transitions
     */
    public void print_nodes_connections(){
        SN sn = SN.get_instance();
        System.out.println();
        System.out.println("(SN)Nodes connections via arcs \"node_name ------ arc_name: arc expression ----> node_name\": ");
        System.out.println();
        
        sn.get_P().stream().forEach(place -> {
                        System.out.println(place.get_name() + ", Next nodes: ");
                        HashMap<Node, ArcAnnotation> next = place.get_next_nodes();
                        HashMap<Node, ArcAnnotation> inib = place.get_inib_nodes();
                        
                        this.print_arcs(next, false);
                        
                        System.out.println(place.get_name() + ", Next inhibitors' nodes:");
                        this.print_arcs(inib, false);
                        System.out.println("End of inhibitors.");
                        System.out.println();
                }
        );
        
        sn.get_T().stream().forEach(
                transition -> {
                    System.out.println(transition.get_name() + ", Next nodes: ");
                    HashMap<Node, ArcAnnotation> next = transition.get_next_nodes();
                    this.print_arcs(next, false);
                }
        );
    }
    
    /**
     * prints all associated arcs to a certain transition
     */
    public void print_transitions_connections(){
        SN sn = SN.get_instance();
        Semantic_DataTester sm_dt = Semantic_DataTester.get_instance();
        System.out.println();
        System.out.println("(SN)Nodes connections via arcs \"node_name ------ arc_name: arc expression ----> node_name\": ");
        System.out.println();
        
        sn.get_T().stream().forEach(
                transition -> {
                    String transition_name = transition.get_name();
                    //print domain of transition
                    sm_dt.test_domain(transition_name, transition.get_node_domain());
                    //print transition connected nodes/arcs
                    System.out.println("STR of connected nodes to " + transition_name);
                    System.out.println(transition_name + ", Next nodes: ");
                    HashMap<Node, ArcAnnotation> next = transition.get_next_nodes();
                    this.print_arcs(next, false);
                    System.out.println(transition_name + ", Previous nodes: ");
                    HashMap<Node, ArcAnnotation> previous = transition.get_previous_nodes();
                    this.print_arcs(previous, false);
                    System.out.println(transition_name + ", Inhibitor nodes: ");
                    HashMap<Node, ArcAnnotation> inib = transition.get_inib_nodes();
                    this.print_arcs(inib, true);
                    System.out.println("END of connected nodes to " + transition_name);
                    System.out.println();
                }
        );
    }
    
    /**
     * extract associated arcs to a certain node
     * @param connected_nodes Map of connected nodes and their associated arcs to a certain node
     * @param invert_arc inverts arc head in case of printing an inibitor arc and starting from a transition
     */
    private void print_arcs(HashMap<Node, ArcAnnotation> connected_nodes, boolean invert_arc){
        
        connected_nodes.keySet().stream().forEach(
            next_node -> {
                ArcAnnotation arc = connected_nodes.get(next_node);
                
                if(invert_arc){
                    System.out.print("<-------- " + arc.get_name() + ": ");
                }else{
                    System.out.print("-------- " + arc.get_name() + ": ");
                }
                Map<WNtuple, Integer> arc_tuples_map = (Map<WNtuple, Integer>) arc.get_tuple_bag().asMap();

                arc_tuples_map.keySet().stream().forEach(
                        tuple -> {
                            System.out.print(arc_tuples_map.get(tuple) + "*{");
                            Guard g = tuple.guard();

                            if(g != null){
                                System.out.print("[" + g.toString() + "] ");
                            }
                            
                            System.out.print(" <");
                            tuple.getComponents().stream().forEach(
                                    comb_element -> this.print_linear_comb(comb_element)
                            ); 
                            System.out.print("> ");
                            
                            Guard f = tuple.filter();

                            if(f != null){
                                System.out.print("[" + f.toString() + "] ");
                            }

                            System.out.print("} ");
                        }
                );
                
                if(invert_arc){
                    System.out.print("------- " + next_node.get_name());
                }else{
                    System.out.print("-------> " + next_node.get_name());
                }
                System.out.println();
            }
        );
    }
    
    /**
     * 
     * @param comb the linear combination which we want to print its data
     */
    private void print_linear_comb(LinearComb comb){
        Map<? extends ElementaryFunction, Integer> comb_map = comb.asMap();
        System.out.print("(");
        
        int i = 0, size = comb_map.keySet().size();
        for(ElementaryFunction comb_map_element : comb_map.keySet()){
                System.out.print(" " + comb_map.get(comb_map_element) + "* ");

//                if(comb_map_element instanceof Token){
//                    Token t = (Token) comb_map_element;
//                    System.out.print(t.get_Token_value());
//
//                }else if(comb_map_element instanceof Projection){
//                    Projection p = (Projection) comb_map_element;
//                    System.out.print(p.toString());
//                }else if(comb_map_element instanceof All){ //All
//                    All all = (All) comb_map_element;
//                    System.out.print(all.toString());
//                }else{ //constant
//                    Subcl con = (Subcl) comb_map_element;
//                    System.out.print(con.toString());
//                }
                System.out.print(comb_map_element.toString());
                
                if(i < size-1){
                    System.out.print(" + ");
                }
                i++;
        }
        
        System.out.print(") ");
    }
    
    /**
     * 
     * @return single static instance
     */
    public static SN_DataTester get_instance(){
        
        if(instance == null){
            instance = new SN_DataTester();
        }
        
        return instance;
    }
    
}
