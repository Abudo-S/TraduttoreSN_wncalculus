/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni_xml;

import java.util.*;
import Albero_sintattico.*;
import Analyzer.*;
import struttura_sn.*;
import wncalculus.expr.Domain;
import wncalculus.guard.*;
import wncalculus.wnbag.WNtuple;
import wncalculus.wnbag.TupleBag;
import wncalculus.color.ColorClass;
import wncalculus.expr.Sort;
        
/**
 *
 * @author dell
 */
//part of 2 factory patterns (Albero_sintattico & Analyzer) 
//singleton
public class SemanticAnalyzer {
    
    private static SN sn;
    private static SyntaxTree snt;
    private final Guard_analyzer ga;
    private final Tuple_analyzer ta;
    //single instance
    private static SemanticAnalyzer instance = null;
    
    private SemanticAnalyzer(){
        sn = SN.get_instance();
        ga = Guard_analyzer.get_instance();
        ta = Tuple_analyzer.get_instance();
    }
    
    public void set_syntax_tree(final SyntaxTree synt_tree){
        snt = synt_tree;
    }
    
    public SyntaxTree get_syntax_tree(){
        return snt;
    }     
    
    public void analyze_syntax_tree() throws NullPointerException{
        
        if(snt == null){
            throw new NullPointerException("Can't analyze the syntax tree!");
        }
        
        ArrayList<Syntactic_transition> all_transitions = snt.get_synt_transition();
        ArrayList<Syntactic_place> all_places = snt.get_synt_places();
        ArrayList<Syntactic_arc> around_transition = new ArrayList<>();
        //Domain[] wrapper = new Domain[1]; //to wrap transition's domain because lambda expression doesn't allow modifing non final variable
        
        all_transitions.stream().forEach(
                synt_transition -> {
                    HashMap<SyntacticNode, Syntactic_arc> next = synt_transition.get_all_next();
                    
                    //add all next arcs of transition
                    next.keySet().stream().forEach(
                            syntactic_place -> around_transition.add(next.get(syntactic_place))
                    );
                    
                    //add arcs that deliver to the same transition
                    all_places.stream().forEach(
                            synt_place -> {
                                HashMap<SyntacticNode, Syntactic_arc> next_of_synt_place = synt_place.get_all_next();
                                
                                next_of_synt_place.keySet().stream().filter(
                                        syntactic_tansition -> syntactic_tansition.get_name().equals(synt_transition.get_name())
                                ).forEach(
                                        syntactic_tansition -> around_transition.add(next_of_synt_place.get(syntactic_tansition))
                                );                                 
                            }                            
                    );
                    
                    //add domained_transition
                    Domain d = this.analyze_transition_domain(around_transition, synt_transition.get_syntactic_guard());
                    Transition t = this.create_analyzed_transition(
                            synt_transition.get_name(), ga.analyze_guard_of_predicates(synt_transition.get_syntactic_guard(), synt_transition.get_name(), d), d
                    );
                    sn.add_transition(t);
                    //connect transition by arcs
                    sn.update_transition(this.create_connected_transition(synt_transition));
                    //reset list "around_transition" for the next transition
                    around_transition.removeAll(around_transition);
                }
        );
        
        all_places.stream().forEach(
                synt_place -> sn.update_place(this.create_connected_place(synt_place))
        );
        
    }
    
    private Transition create_analyzed_transition(String name, Guard g, Domain d){
        Transition t = new Transition(name, g);
        t.set_node_domain(d);
        return t;
    }
    
    private Domain analyze_transition_domain(ArrayList<Syntactic_arc> around_transition, Syntactic_guard transition_guard){
        //analyze transition guard
        Map<ColorClass,Integer> domain_elements = this.analyze_guard_colorclasses(new HashMap<ColorClass,Integer>(), transition_guard);
        
        //analyze all arcs that exist around transition 
        for(Syntactic_arc synt_arc : around_transition){
            HashMap<Syntactic_tuple,Integer> arc_tuples = synt_arc.get_all_tuples();
            
            for(Syntactic_tuple synt_tuple : arc_tuples.keySet()){
                domain_elements = this.analyze_tuple_colorclasses(domain_elements, synt_tuple);
                domain_elements = this.analyze_guard_colorclasses(domain_elements, synt_tuple.get_syntactic_guard());
            }
        }
 
        return new Domain((HashMap<? extends Sort, Integer>) domain_elements);
    }
    
    private Map<ColorClass,Integer> analyze_guard_colorclasses(Map<ColorClass,Integer> domain_elements, Syntactic_guard guard){
        //update domain_elements with new data
        //to be completed
        return domain_elements;
    }
    
    private Map<ColorClass,Integer> analyze_tuple_colorclasses(Map<ColorClass,Integer> domain_elements, Syntactic_tuple tuple){
        //update domain_elements with new data
        //to be completed
        return domain_elements;
    }
    
    public Domain analyze_place_domain(Place p){ //possible colorclasses in a place
        Domain d = p.get_node_domain();
        
        if(d == null){
            String place_type = p.get_type();
            d = sn.find_domain(place_type); //assume that the place type is domain

            if(d == null){ //if the place type isn't domain then it's colorclass
                d = new Domain(sn.find_colorClass(place_type));
            }
            
            p.set_node_domain(d);
            //update sn
            sn.update_place(p);
        }
        
        return d;
    }    
        
    private Place create_connected_place(Syntactic_place synt_place){
        Place p = sn.find_place(synt_place.get_name());
        HashMap<SyntacticNode, Syntactic_arc> next_of_synt_place = synt_place.get_all_next(); //arcs from place to transitions
        
        for(SyntacticNode synt_t : next_of_synt_place.keySet()){
            
            Syntactic_arc synt_arc = next_of_synt_place.get(synt_t);
            Transition t = sn.find_transition(synt_t.get_name());
            //pass transition domain
            Arc arc = this.create_analyzed_arc(synt_arc,t.get_name(), p.get_name(), t.get_node_domain());
            
            if(synt_arc.get_type()){ //inhibitor
                p.add_inib(arc, t);
                t.add_inib(arc, p);
            }else{
                t = (Transition) p.add_next_Node(arc, p);
            }         
            //update connected transition
            sn.update_transition(t);
        }
        
        return p;
    }
    
    private Transition create_connected_transition(Syntactic_transition synt_transition){
        Transition t = sn.find_transition(synt_transition.get_name());
        HashMap<SyntacticNode, Syntactic_arc> next_of_synt_place = synt_transition.get_all_next(); //arcs from transition to places
        
        for(SyntacticNode synt_p : next_of_synt_place.keySet()){
            
            Syntactic_arc synt_arc = next_of_synt_place.get(synt_p);
            Place p = sn.find_place(synt_p.get_name());
            //pass place domain
            Arc arc = this.create_analyzed_arc(synt_arc, t.get_name(), p.get_name(), p.get_node_domain());
            
            if(synt_arc.get_type()){ //inhibitor
                p.add_inib(arc, t);
                t.add_inib(arc, p);
            }else{
                p = (Place) t.add_next_Node(arc, p);
            }         
            //update connected transition
            sn.update_place(p);
        }
        
        return t;
    }
    
    private Arc create_analyzed_arc(Syntactic_arc synt_arc, String transition_name, String place_name, Domain d){
        HashMap<Syntactic_tuple, Integer> multiplied_tuples = synt_arc.get_all_tuples();
        Map<WNtuple, Integer> tuple_bag_map =  new HashMap<>();
        
        //fill tuple_bag_map
        multiplied_tuples.keySet().stream().forEach(
                synt_tuple -> tuple_bag_map.put(
                        ta.analyze_arc_tuple(
                                ga.analyze_guard_of_predicates(synt_tuple.get_syntactic_guard(), synt_arc.get_name(), d), synt_tuple.get_tuple_elements(),transition_name, place_name, d
                        ), multiplied_tuples.get(synt_tuple)
                )
        );
        
        return new Arc(synt_arc.get_name(), new TupleBag(tuple_bag_map));
    }
    
    public static SemanticAnalyzer get_instance(){

        if(instance == null){
            instance = new SemanticAnalyzer();
        }

        return instance;
    }
}
