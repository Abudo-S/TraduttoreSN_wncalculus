/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struttura_sn.ArcAnnotation;
import struttura_sn.Node;
import struttura_sn.Place;
import struttura_sn.SN;
import struttura_sn.Transition;
import struttura_sn.Variable;
import wncalculus.classfunction.Projection;
import wncalculus.classfunction.Subcl;
import wncalculus.expr.Domain;
import wncalculus.guard.*;
import wncalculus.wnbag.WNtuple;
import wncalculus.wnbag.LinearComb;
import wncalculus.color.ColorClass;
import wncalculus.expr.Interval;
import wncalculus.util.ComplexKey;

/**
 *
 * @author dell
 */
//replaced by SemanticAnalyzer (Note: SemanticAnalyzer is built from SemanticAnalyzerV2)
//singleton
public class SemanticAnalyzerV2 { //check/analyze the semantic of arc expressions & guards/tuples
    
    private static SN sn;
    //single instance
    private static SemanticAnalyzerV2 instance = null;
    
    private SemanticAnalyzerV2(){
        sn = SN.get_instance();
    }
    
    //In wncalculus a guard of predicates has 2 type of guards: guard with or between predicates, guard with and between predicates
    //format: LinkedHashMap<HashMap<ArrayList, Boolean>, String> = LinkedHashMap<HashMap<predicate with projections/constants, invert_predicate>, separator with next predicate if exists>
    public Guard analyze_guard_of_predicates(LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String> guard, boolean invert_guard, String transition_name){
        
        if(guard == null || guard.isEmpty()){
            //create True guard
            return null; 
        }
        
        Guard next_p = null, g, res = null; //for not analyzing predicates that were pre-analyzed after and/or operation
        
        try{
            Iterator<HashMap<ArrayList<String>, Boolean>> it = guard.keySet().iterator(); //iterate predicates after and/or operation
            it.next(); //ignore first predicate
            
            for(HashMap<ArrayList<String>, Boolean> predicate : guard.keySet()){

                if(next_p == null){ //first cycle
                    g = this.analyze_predicate(predicate, transition_name);
                    
                    if(it.hasNext()){
                        next_p = this.analyze_predicate(it.next(), transition_name);
                    }else{
                        res = g;
                        break;
                    }                    
                }else{
                    g = next_p;
                    
                    if(it.hasNext()){
                        next_p = this.analyze_predicate(it.next(), transition_name);
                    }else{
                        res = this.analyze_and_or_guard(res, g, guard.get(predicate));
                        break;
                    }
                }
                res = this.analyze_and_or_guard(res, next_p, guard.get(predicate));
            }            
            //check if inverted
            if(invert_guard){
                res = Neg.factory(res);
            }
        }catch(Exception e){
            System.out.println(e + " in SemanticAnalyzer/analyze_guard_of_predicates()");
        }
        return res;
    }
    
    private Guard analyze_and_or_guard(Guard g1, Guard g2, String operation){
        
        if(operation.equals("and")){
            return And.factory(g1, g2); 
        }
        return Or.factory(false, g1, g2); 
    }
    
    //In wncalculus predicate is also a guard
    //predicate map has only one element which is associated with a separator if exists
    private Guard analyze_predicate(HashMap<ArrayList<String>, Boolean> predicate, String transition_name){
        ArrayList<String> predicate_txt = predicate.keySet().iterator().next();
        Guard g = null;
        
        try{
            String p_txt = predicate_txt.get(0);
            
            if(predicate_txt.size() == 1) {

                if(p_txt.contains("True")){
                    //create True guard
                }else if(p_txt.contains("False")){
                    //create False guard
                }
             //equality || membership
            }else{ //3 elements predicate -> projection, operation, projection/constant 
                //1st element
                Projection p1 = this.analyze_projection_element(p_txt, transition_name);
                //2nd element
                String operation = predicate_txt.get(1);
                //3rd element
                String op3 = predicate_txt.get(2);
                
                if(operation.equals("=")){
                    g = this.analyze_equality_guard(p1, p1, true);
                    
                }else if(operation.equals("!=")){
                    g = this.analyze_equality_guard(p1, this.analyze_projection_element(op3, transition_name), false);
                    
                }else if(operation.equals("in")){
                    g = this.analyze_membership_guard(p1, this.analyze_constant_element(op3), true);
                }else{// !in
                    g = this.analyze_membership_guard(p1, this.analyze_constant_element(op3), false);
                }
            }

            if(predicate.get(predicate_txt) == true){ //invert predicate
                g = Neg.factory(g);
            }
        }catch(Exception e){
            System.out.println(e + " in SemanticAnalyzer/analyze_predicate()");
        }
        return g;
    }
    
    private Guard analyze_equality_guard(Projection p1, Projection p2, boolean operation){
        //to be completed
        return null;
    }
    
    private Guard analyze_membership_guard(Projection p1, Subcl constant, boolean operation){
        //to be completed
        return null;
    }
    
    //WNtuple object is consisted of linearcomb which is consisted of projections and subcl(constent)
    //tuples_elements list contains linearcombs
    public WNtuple analyze_arc_tuple(Guard g, String[] tuple_elements /*, Domain domain*/){
        //uses analyze_tuple_elements()
        return null;
    }
    
    private LinearComb analyze_tuple_elements(String[] tuple_elements){
        //uses analyze_projection_element()
        //uses analyze_constant_element()
        return null;
    }
    
    private Projection analyze_projection_element(String proj, String transition_name) throws NullPointerException{
        
        Pattern p = Pattern.compile("([_a-zA-Z]+[_a-zA-Z0-9]*)(([+]{2}|[-]{2})?)");
        Matcher m = p.matcher(proj.replaceAll("\\s*", ""));
        Projection pro = null;
        
        if(m.find()){
            Variable v = sn.find_variable(m.group(1));
            String circ_op = m.group(2);

            if(circ_op.equals("")){
                pro  = Projection.builder(this.generate_projection_index(transition_name, v.get_name(), 0), 0, v.get_colourClass());
            }else{

                if(circ_op.contains("++")){
                    pro = Projection.builder(this.generate_projection_index(transition_name, v.get_name(), 1), 1, v.get_colourClass());

                }else if(circ_op.contains("--")){
                    pro = Projection.builder(this.generate_projection_index(transition_name, v.get_name(), -1), -1, v.get_colourClass());

                }else{
                    throw new NullPointerException("can't find successor in " + proj);
                }       
            }
            
            v.add_available_projection(pro, transition_name);
            sn.update_variable_via_projection(v);
                            
        }else{
            throw new NullPointerException("can't find first projection in " + proj);
        }
        
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
    
    public Domain analyze_transition_domain(Transition t){ //transition's domain will have all colorclasses of variables that exist on connected arcs to it even if variables exist on guards
        HashMap<ColorClass,Integer> domain_elements;
        Domain d = t.get_node_domain();
        
        if(d == null){ 
            domain_elements = new HashMap<>();
            HashMap<Node, ArcAnnotation> t_next = t.get_next_nodes();
            HashMap<Node, ArcAnnotation> t_previous = t.get_previous_nodes();
            HashMap<Node, ArcAnnotation> t_inib = t.get_inib_nodes();
            
            t_next.keySet().stream().forEach(
                    place -> t_next.get(place)
            );

            d = new Domain(domain_elements);
            t.set_node_domain(d);
            //update sn
            sn.update_transition(t);
        }else{ //update transition domain
            
        }
        
        return d;
    }
    
    private ArrayList<ColorClass> search_arc_projection(ArcAnnotation arc){
        //to be completed
        return null;
    }
    
    private ArrayList<ColorClass> seach_tuple_projection(WNtuple t){
        //to be completed
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
        
    public static SemanticAnalyzerV2 get_instance(){
        
        if(instance == null){
            instance = new SemanticAnalyzerV2();
        }
        
        return instance;
    }
}
