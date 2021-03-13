/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni_xml;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Albero_sintattico.*;
import struttura_sn.*;
import wncalculus.classfunction.Projection;
import wncalculus.classfunction.Subcl;
import wncalculus.color.ColorClass;
import wncalculus.expr.Domain;
import wncalculus.expr.Interval;
import wncalculus.guard.*;
import wncalculus.util.ComplexKey;
import wncalculus.wnbag.LinearComb;
import wncalculus.wnbag.WNtuple;

/**
 *
 * @author dell
 */
//singleton
public class SemanticAnalyzer {
    
    private static SN sn;
    private static SyntaxTree snt;
    //single instance
    private static SemanticAnalyzer instance = null;
    
    private SemanticAnalyzer(){
        sn = SN.get_instance();
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
        Domain[] wrapper = new Domain[1]; //to wrap transition's domain because lambda expression doesn't allow modifing non final variable
        
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
                    wrapper[0] = this.analyze_transition_domain(around_transition, synt_transition.get_syntactic_guard());
                    sn.add_transition(
                            this.create_analyzed_transition(synt_transition.get_name(), this.analyze_guard_of_predicates(synt_transition, wrapper[0]), wrapper[0])
                    );
                    //reset around for the next transition
                    around_transition.removeAll(around_transition);
                }
        );
        
    }
    
    private Transition create_analyzed_transition(String name, Guard g, Domain d){
        Transition t = new Transition(name, g);
        t.set_node_domain(d);
        return t;
    }
    
    private Guard analyze_guard_of_predicates(Syntactic_transition synt_t, Domain d){
        Syntactic_guard guard = synt_t.get_syntactic_guard();
        LinkedHashMap<Syntactic_predicate,String> separated_predicates = guard.get_separated_predicates();
        
        if(separated_predicates.isEmpty()){
            return this.analyze_true_false_guard(true, d);
        }
        Guard next_p = null, g, res = null; //for not analyzing predicates that were pre-analyzed after and/or operation

        try{
            Iterator<Syntactic_predicate> it = separated_predicates.keySet().iterator(); //iterate predicates after and/or operation
            it.next(); //ignore first predicate

            for(Syntactic_predicate predicate : separated_predicates.keySet()){

                if(next_p == null){ //first cycle
                    g = this.analyze_predicate(predicate, d);

                    if(it.hasNext()){
                        next_p = this.analyze_predicate(it.next(), d);
                    }else{
                        res = g;
                        break;
                    }                    
                }else{
                    g = next_p;

                    if(it.hasNext()){
                        next_p = this.analyze_predicate(it.next(), d);
                    }else{
                        res = this.analyze_and_or_guard(res, g, separated_predicates.get(predicate));
                        break;
                    }
                }
                res = this.analyze_and_or_guard(res, next_p, separated_predicates.get(predicate));
            }            
            //uses analyze_predicates()
            if(guard.get_invert_guard()){
                res = Neg.factory(res);
            }
        }catch(Exception e){
            System.out.println(e + " in SemanticAnalyzer/analyze_guard_of_predicates()");
        }
        
        return res;    
    }
    
    private Guard analyze_predicate(Syntactic_predicate synt_pr, Domain d){
        Guard p = null;
        //to be completed
        return null;
    }
    
    private Guard analyze_and_or_guard(Guard g1, Guard g2, String operation){

        if(operation.equals("and")){
            return And.factory(g1, g2); 
        }
        return Or.factory(false, g1, g2); 
    }
    
    private Guard analyze_true_false_guard(boolean TF, Domain d){
        
        if(TF){ //create true guard
            True.getInstance(d);
        }
        return False.getInstance(d);
    }
    
    private Guard analyze_equality_guard(Projection g1, Projection g2, boolean operation, Domain d){
        //to be completed
        return null;
    }
    
    private Guard analyze_membership_guard(Projection g1, Subcl constant, boolean operation, Domain d){
        //to be completed
        return null;
    }
    
    private Projection analyze_projection_element(String proj, String transition_name) throws NullPointerException{
        Pattern p = Pattern.compile("([_a-zA-Z]+[_a-zA-Z0-9]*)(([+]{2}|[-]{2})?)");
        Matcher m = p.matcher(proj.replaceAll("\\s*", ""));
        Projection pro = null;
        //to be completed
        return pro;
    }
    //constant, es: subclass name
    private Subcl analyze_constant_element(String const_name){
        Subcl con = null;
        
        for(ColorClass cc : sn.get_C()){
            
            if(cc.name().equals(const_name)){ //search in colorclasses' names
                con = Subcl.factory(this.generate_subcl_index(const_name), cc);
                 break;
            }else{ //search in subclasses of colorclass
                Interval interval = Arrays.stream(cc.getConstraints()).filter(
                                                 sub_interval -> sub_interval.name().equals(const_name)
                                                 ).findFirst().orElse(null);
                if(interval != null){
                    con = Subcl.factory(this.generate_subcl_index(const_name), cc); //should we pass the sub-interval in which we have found the constant?
                    break;        
                }
            }
        }
                
        return con;
    }
    
    private Domain analyze_transition_domain(ArrayList<Syntactic_arc> around_transition, Syntactic_guard transition_guard){
        Domain d = null;
        //to be completed
        return d;
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
    
    //WNtuple object is consisted of linearcomb which is consisted of projections and subcl(constent)
    //tuples_elements list contains linearcombs
    public WNtuple analyze_arc_tuple(Guard g, String[] tuple_elements , Domain domain){
        //uses analyze_tuple_elements()
        return null;
    }
    
    private LinearComb analyze_tuple_elements(String[] tuple_elements){
        //uses analyze_projection_element()
        //uses analyze_constant_element()
        return null;
    }
    
    private int generate_subcl_index(String const_name){
        return this.generate_projection_index(const_name, "", 0);
    }
    
    //successor_flag = 1 in case of ++, -1 in case of --, 0 otherwise
    private int generate_projection_index(String transition_name, String variable_name, int successor_flag){
        ComplexKey ck = new ComplexKey(transition_name, variable_name, successor_flag);
        return ck.hashCode();
    }
    
    private void connect_nodes_via_arcs(){
        //to be completed
        //update sn instance
    }
    
    public static SemanticAnalyzer get_instance(){

        if(instance == null){
            instance = new SemanticAnalyzer();
        }

        return instance;
    }
}
