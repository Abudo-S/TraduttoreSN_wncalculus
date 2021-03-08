/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni_xml;

import java.util.*;
import struttura_sn.Place;
import struttura_sn.Transition;
import wncalculus.classfunction.Projection;
import wncalculus.classfunction.Subcl;
import wncalculus.expr.Domain;
import wncalculus.guard.*;
import wncalculus.wnbag.WNtuple;
import wncalculus.wnbag.LinearComb;

/**
 *
 * @author dell
 */
//singleton
public class SemanticAnalyzer { //check/analyze the semantic of arc expressions & guards/tuples
    //single instance
    private static SemanticAnalyzer instance = null;
    
    private SemanticAnalyzer(){
        
    }
    
    //In wncalculus a guard of predicates has 2 type of guards: guard with or between predicates, guard with and between predicates
    //format: LinkedHashMap<HashMap<ArrayList, Boolean>, String> = LinkedHashMap<HashMap<predicate with projections/constants, invert_predicate>, separator with next predicate if exists>
    public Guard analyze_guard_of_predicates(LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String> guard, boolean invert_guard){
        
        if(guard == null || guard.size() == 0){
            //create True guard
            return null; 
        }
        
        Guard next_p = null, g, res = null; //for not analyzing predicates that were pre-analyzed after and/or operation
        
        try{
            Iterator<HashMap<ArrayList<String>, Boolean>> it = guard.keySet().iterator(); //iterate predicates after and/or operation
            it.next(); //ignore first predicate
            
            for(HashMap<ArrayList<String>, Boolean> predicate : guard.keySet()){

                if(next_p == null){ //first cycle
                    g = this.analyze_predicate(predicate);
                    
                    if(it.hasNext()){
                        next_p = this.analyze_predicate(it.next());
                    }else{
                        res = g;
                        break;
                    }                    
                }else{
                    g = next_p;
                    
                    if(it.hasNext()){
                        next_p = this.analyze_predicate(it.next());
                    }else{
                        res = this.analyze_and_or_guard(res, g, guard.get(predicate));
                        break;
                    }
                }
                res = this.analyze_and_or_guard(g, next_p, guard.get(predicate));
            }            
            //uses analyze_predicates()
            if(!invert_guard){
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
    //predicate map has only one element which is associated with a separator
    private Guard analyze_predicate(HashMap<ArrayList<String>, Boolean> predicate){
        ArrayList<String> predicate_txt = predicate.keySet().iterator().next();
        Guard g = null;
        
        if(predicate.get(predicate_txt) == true){ //invert predicate
            g = Neg.factory(g);
        }
        return g;
    }
    
    //WNtuple object is consisted of linearcomb which is consisted of projections and subcl(constent)
    //tuples_elements list contains linearcombs
    public WNtuple analyze_arc_tuple(Guard g, String[] tuple_elements){
        //uses analyze_tuple_elements()
        return null;
    }
    
    private LinearComb analyze_tuple_elements(String[] tuple_elements){
        //uses analyze_projection_element()
        //uses analyze_constant_element()
        return null;
    }
    
    private Projection analyze_projection_element(){
        return null;
    }
    
    //constant, es: subclass name
    private Subcl analyze_constant_element(){
        return null;
    }
    
    public Domain analyze_place_domain(Place p){ //possible colorclasses in a place
        //to be completed
        return null;
    }
    
    public Domain analyze_transition_domain(Transition t){ //transition's domain will have all colorclasses of variables that exist on connected arcs to it even if variables exist on guards
        //to be completed
        return null;
    }
        
    public static SemanticAnalyzer get_instance(){
        
        if(instance == null){
            instance = new SemanticAnalyzer();
        }
        
        return instance;
    }
}
