/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import analyzer.Guard_analyzer;
import analyzer.ElementAnalyzer;
import analyzer.Tuple_analyzer;
import albero_sintattico.*;
import componenti.Variable_index_table;
import struttura_sn.*;
import java.util.*;
import test.Semantic_DataTester;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
//part of 2 factory patterns (albero_sintattico & analyzer) 
//singleton
public class SemanticAnalyzer {
    
    private static SN sn;
    private static SyntaxTree snt;
    private static Variable_index_table vit;
    private final Guard_analyzer ga;
    private final Tuple_analyzer ta;
    //cache
    private ArrayList<String> domain_analyzed_variables; //will be treated as cache for each transition that will be (reasigned/reset) as new ArrayList for each transition
    //single instance
    private static SemanticAnalyzer instance = null;
    
    private SemanticAnalyzer(){
        sn = SN.get_instance();
        vit = Variable_index_table.get_instance();
        this.ga = Guard_analyzer.get_instance();
        this.ta = Tuple_analyzer.get_instance();
        this.domain_analyzed_variables = new ArrayList<>();
    }
    
    /**
     * 
     * @param synt_tree 
     */
    public void set_syntax_tree(final SyntaxTree synt_tree){
        snt = synt_tree;
    }
    
    /**
     * 
     * @return 
     */
    public SyntaxTree get_syntax_tree(){
        return snt;
    }     
    
    /**
     * this method analyse all elements
     * @throws NullPointerException if syntax tree is null
     */
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
                    Domain d = this.analyze_transition_domain(around_transition, synt_transition.get_syntactic_guard(), synt_transition.get_name());
                    //prepare transition's table of variabile indices
                    vit.initialize_transition_cc(synt_transition.get_name(), d);
                    Transition t = this.create_analyzed_transition(
                            synt_transition.get_name(), ga.analyze_guard_of_predicates(synt_transition.get_syntactic_guard(), synt_transition.get_name(), null, d), d
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
    
    /**
     * 
     * @param name transition's name
     * @param g transition's guard
     * @param d transition's domain
     * @return the created transition
     */
    private Transition create_analyzed_transition(String name, Guard g, Domain d){
        Transition t = new Transition(name, g);
        t.set_node_domain(d);
        return t;
    }
    
    /**
     * 
     * @param around_transition ArrayList of all syntactic arcs around transition that we need to analyse their tuples to find transition domain
     * @param transition_guard the guard of transition that we need to analyse to find transition domain
     * @return transition's domain
     */
    private Domain analyze_transition_domain(ArrayList<Syntactic_arc> around_transition, Syntactic_guard transition_guard, String t_name){
        //analyze transition guard
        Map<ColorClass,Integer> domain_elements = this.analyze_guard_colorclasses(new HashMap<ColorClass,Integer>(), transition_guard);
        
        //analyze all arcs that exist around transition 
        for(Syntactic_arc synt_arc : around_transition){
            LinkedHashMap<Syntactic_tuple,Integer> arc_tuples = synt_arc.get_all_tuples();
            
            for(Syntactic_tuple synt_tuple : arc_tuples.keySet()){
                //find projections colorclasses
                domain_elements = this.analyze_tuple_colorclasses(domain_elements, synt_tuple);
                domain_elements = this.analyze_guard_colorclasses(domain_elements, synt_tuple.get_syntactic_guard());
            }
        }

        //System.out.println(t_name + "," + Arrays.toString(this.domain_analyzed_variables.toArray()));
        //reset list of projection indices "domain_analyzed_variables" for next transition 
        this.domain_analyzed_variables = new ArrayList<>();
                    
        return new Domain((HashMap<? extends Sort, Integer>) domain_elements);
    }
    
    /**
     * 
     * @param domain_elements the Map that we are elaborating
     * @param guard the syntactic guard that we want to analyse its colour classes
     * @return the updated Map "domain_elements"
     */
    private Map<ColorClass,Integer> analyze_guard_colorclasses(Map<ColorClass,Integer> domain_elements, Syntactic_guard guard){ 
        //update domain_elements with new data
        if(guard != null){
            LinkedHashMap<Syntactic_predicate,String> separated_predicates= guard.get_separated_predicates();

            for(Syntactic_predicate synt_predicate : separated_predicates.keySet()){
                ArrayList<String> predicate_elements = synt_predicate.get_predicate_elements();
                //projections may appear in the first and the third elements, Note: it won't appear in the second element because it's a predicate-operation 
                String var_name = predicate_elements.get(0);
                domain_elements = this.domain_elements_updater(this.analyze_projection_colorclass(var_name), domain_elements);

                if(predicate_elements.size() > 2){ //3 elements predicate
                    var_name = predicate_elements.get(2);
                    domain_elements = this.domain_elements_updater(this.analyze_projection_colorclass(var_name), domain_elements);
                }
            }
        }
        return domain_elements;
    }
    
    /**
     * 
     * @param domain_elements the Map that we are elaborating
     * @param tuple the syntactic tuple that we want to analyse its colour classes
     * @return the updated Map "domain_elements"
     */
    private Map<ColorClass,Integer> analyze_tuple_colorclasses(Map<ColorClass,Integer> domain_elements, Syntactic_tuple tuple){
        //update domain_elements with new data
        String[] tuple_elements = tuple.get_tuple_elements();

        for(String tuple_element : tuple_elements){
            String[] combs_of_elements = tuple_element.split("\\s*[\\+-]\\s*");

            for(String comb : combs_of_elements){
                
                if(sn.find_variable(comb) != null){
                    domain_elements = this.domain_elements_updater(this.analyze_projection_colorclass(comb), domain_elements);
                }
            }
        }
        
        return domain_elements;
    }
    
    /**
     * 
     * @param cc the colour class key that we want to update its multiplicity in Map "domain_elements"
     * @param domain_elements the Map that we are elaborating
     * @return the updated Map "domain_elements"
     */
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
    
    /**
     * 
     * @param p the place that we want to get its domain
     * @return the analysed place's domain
     */
    public Domain analyze_place_domain(Place p){ //possible colorclasses in a place
        Domain d = p.get_node_domain();
        
        if(d == null){
            String place_type = p.get_type();
            d = sn.find_domain(place_type); //assume that the place type is domain

            if(d == null){ //if the place type isn't domain then it's colorclass
                d = new Domain(sn.find_colorClass(place_type));
            }
        }
        //Semantic_DataTester.get_instance().test_domain(p.get_name(), d);
        return d;
    }    
    
    public Domain analyze_tuple_codomain(String name, String[] tuple_elements, String place_name){
        HashMap<ColorClass, Integer> domain_elements = new HashMap<>();
        
        for(var i = 0; i < tuple_elements.length; i++){
            HashMap<ColorClass, Integer> t_element_ccs = ta.analyze_t_element_cc(tuple_elements[i], place_name, i);

            t_element_ccs.keySet().stream().forEach(
                    cc -> {

                        if(domain_elements.containsKey(cc)){
                            domain_elements.put(cc, domain_elements.get(cc) + t_element_ccs.get(cc));
                        }else{
                            domain_elements.put(cc, t_element_ccs.get(cc));
                        }
                    }
            );
       }
        
        return new Domain(domain_elements);
    }    
    /**
     * 
     * @param synt_place syntactic place from which a place will be analysed
     * @return the created place
     */
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
    
    /**
     * 
     * @param synt_transition syntactic transition from which a transition will be analysed
     * @return the created transition
     */
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
    
    /**
     * 
     * @param synt_arc syntactic arc from which an arc annotation will be analysed 
     * @param transition_name the name of transition connected to syntactic arc
     * @param place_name the name of place connected to syntactic arc
     * @param d the domain of tuples that appear on syntactic arc
     * @return the created arc annotation
     */
    private ArcAnnotation create_analyzed_arc(Syntactic_arc synt_arc, String transition_name, String place_name, Domain d){
        LinkedHashMap<Syntactic_tuple, Integer> multiplied_tuples = synt_arc.get_all_tuples();
        Map<WNtuple, Integer> tuple_bag_map =  new LinkedHashMap<>();
  
        //fill tuple_bag_map
        multiplied_tuples.keySet().stream().forEach(
                synt_tuple ->  
                    tuple_bag_map.put(
                        ta.analyze_arc_tuple(
                                ga.analyze_guard_of_predicates(synt_tuple.get_syntactic_guard(),  transition_name, null, d),
                                //filter's domain is different
                                ga.analyze_guard_of_predicates(synt_tuple.get_syntactic_filter(), transition_name, ta.get_tuple_vars_names(synt_tuple.get_tuple_elements(), place_name)
                                                              , this.analyze_tuple_codomain(synt_arc.get_name(), synt_tuple.get_tuple_elements(), place_name)),
                                synt_tuple.get_tuple_elements(),transition_name, place_name, d
                        ), multiplied_tuples.get(synt_tuple)
                    )
        );
        //Semantic_DataTester.get_instance().test_semantic_arc(tuple_bag_map, synt_arc.get_name(), transition_name);
        
        return new ArcAnnotation(synt_arc.get_name(), new TupleBag(tuple_bag_map));
    }
    
    /**
     * 
     * @param element the name of variable from which we want to get variable's type (colour class)
     * @param t_name the name of transition that we want to add its variable in Variable index table
     * @return the colour class found of element
     * @throws NullPointerException if element isn't matched by the matcher
     */
    private ColorClass analyze_projection_colorclass(String element) throws NullPointerException{
        ColorClass cc = null;
        Pattern p = Pattern.compile(ElementAnalyzer.get_str_rx_element());
        Matcher m = p.matcher(element);
        
        if(m.find()){
            Variable v = sn.find_variable(m.group(1));

            if(v != null){
                
                if(!this.domain_analyzed_variables.contains(element)){
                    this.domain_analyzed_variables.add(element);
                }else{
                    return cc;
                }
                
                cc = v.get_colourClass();
            }
            
        }else{
            throw new NullPointerException("Can't analyze the colorclass of element: " + element);
        } 
        
        return cc;
    }
    
    /**
     * 
     * @return single static instance
     */
    public static SemanticAnalyzer get_instance(){

        if(instance == null){
            instance = new SemanticAnalyzer();
        }

        return instance;
    }
}
