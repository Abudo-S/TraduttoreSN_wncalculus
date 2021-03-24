/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni_xml;

import java.util.*;
import Albero_sintattico.*;
import Analyzer.*;
import Test.Semantic_DataTester;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    //cache
    private ArrayList<String> domain_analyzed_variables; //will be treated as cache for each transition that will be (reasigned/reset) as new ArrayList for each transition
    //single instance
    private static SemanticAnalyzer instance = null;
    
    private SemanticAnalyzer(){
        sn = SN.get_instance();
        ga = Guard_analyzer.get_instance();
        ta = Tuple_analyzer.get_instance();
        this.domain_analyzed_variables = new ArrayList<>();
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
                    
                    //Semantic_DataTester.get_instance().test_domain(t.get_name(), d);
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
                //find projections colorclasses
                domain_elements = this.analyze_tuple_colorclasses(domain_elements, synt_tuple);
                domain_elements = this.analyze_guard_colorclasses(domain_elements, synt_tuple.get_syntactic_guard());
            }
        }
        //reset list of projection indices "domain_analyzed_variables" for next transition 
        this.domain_analyzed_variables = new ArrayList<>();
                    
        return new Domain((HashMap<? extends Sort, Integer>) domain_elements);
    }
    
    private Map<ColorClass,Integer> analyze_guard_colorclasses(Map<ColorClass,Integer> domain_elements, Syntactic_guard guard){ 
        //update domain_elements with new data
        if(guard != null){
            LinkedHashMap<Syntactic_predicate,String> separated_predicates= guard.get_separated_predicates();

            for(Syntactic_predicate synt_predicate : separated_predicates.keySet()){
                ArrayList<String> predicate_elements = synt_predicate.get_predicate_elements();
                //projections may appear in the first and the third elements, Note: it won't appear in the second element because it's a predicate-operation 
                String var_name = predicate_elements.get(0);
                
                if(!this.domain_analyzed_variables.contains(var_name)){
                    domain_elements = this.domain_elements_updater(this.analyze_projection_colorclass(var_name), domain_elements);
                    this.domain_analyzed_variables.add(var_name);
                }

                if(predicate_elements.size() > 2){ //3 elements predicate
                    var_name = predicate_elements.get(2);
                    
                    if(!this.domain_analyzed_variables.contains(var_name)){
                        domain_elements = this.domain_elements_updater(this.analyze_projection_colorclass(var_name), domain_elements);
                        this.domain_analyzed_variables.add(var_name);
                    }
                }
            }
        }
        return domain_elements;
    }
    
    private Map<ColorClass,Integer> analyze_tuple_colorclasses(Map<ColorClass,Integer> domain_elements, Syntactic_tuple tuple){
        //update domain_elements with new data
        String[] tuple_elements = tuple.get_tuple_elements();
        
        for(String tuple_element : tuple_elements){
            String[] combs_of_elements = tuple_element.split(Tuple_analyzer.get_str_rx_comb_operation());
            
            for(String comb : combs_of_elements){
                
                if(sn.find_variable(comb) != null){
                    
                    if(!this.domain_analyzed_variables.contains(comb)){
                        domain_elements = this.domain_elements_updater(this.analyze_projection_colorclass(comb), domain_elements);
                        this.domain_analyzed_variables.add(comb);
                    }
                }
            }
        }
        
        return domain_elements;
    }
    
    private Map<ColorClass,Integer> domain_elements_updater(ColorClass cc, Map<ColorClass,Integer> domain_elements){
        
        if(cc != null){
            boolean found = false;

            for(ColorClass cc_element : domain_elements.keySet()){

                if(cc_element.name().equals(cc.name())){ //update map-element
                    domain_elements.put(cc_element, domain_elements.get(cc_element) + 1);
                    found = true;
                }
            }

            if(!found){ //add new map-element 
                domain_elements.put(cc, 1);
            }
        }
        
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
            ArcAnnotation arc = this.create_analyzed_arc(synt_arc,t.get_name(), p.get_name(), t.get_node_domain());
            
            if(synt_arc.get_type()){ //inhibitor
                p.add_inib(arc, t);
                t.add_inib(arc, p);
            }else{
                t = (Transition) p.add_next_Node(arc, t);
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
            ArcAnnotation arc = this.create_analyzed_arc(synt_arc, t.get_name(), p.get_name(), p.get_node_domain());
            
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
    
    private ArcAnnotation create_analyzed_arc(Syntactic_arc synt_arc, String transition_name, String place_name, Domain d){
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
        //Semantic_DataTester.get_instance().test_semantic_arc(tuple_bag_map, synt_arc.get_name(), transition_name);
        
        return new ArcAnnotation(synt_arc.get_name(), new TupleBag(tuple_bag_map));
    }
    
    private ColorClass analyze_projection_colorclass(String element){
        ColorClass cc = null;
        Pattern p = Pattern.compile(ElementAnalyzer.get_str_rx_element());
        Matcher m = p.matcher(element);
        
        if(m.find()){
            Variable v = sn.find_variable(m.group(1));
            
            if(v != null){
                cc = v.get_colourClass();
            }
            
        }else{
            throw new NullPointerException("Can't analyze the colorclass of element: " + element);
        } 
        
        return cc;
    }
    
    public static SemanticAnalyzer get_instance(){

        if(instance == null){
            instance = new SemanticAnalyzer();
        }

        return instance;
    }
}
