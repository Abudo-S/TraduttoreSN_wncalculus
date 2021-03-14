/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analyzer;

import Albero_sintattico.Syntactic_guard;
import Albero_sintattico.Syntactic_predicate;
import java.util.*;
import wncalculus.classfunction.Projection;
import wncalculus.classfunction.Subcl;
import wncalculus.expr.Domain;
import wncalculus.guard.*;

/**
 *
 * @author dell
 */
//singleton
public class Guard_analyzer{
    
    private Projection_analyzer pa;
    private Constant_analyzer ca;
    //single instance
    private static Guard_analyzer instance = null;
    
    private Guard_analyzer(){
        this.pa = Projection_analyzer.get_instance();
        this.ca = Constant_analyzer.get_instance();
    }
    
    public Guard analyze_guard_of_predicates(Syntactic_guard guard, String name,  Domain d){
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
                    g = this.analyze_predicate(predicate, name, d);

                    if(it.hasNext()){
                        next_p = this.analyze_predicate(it.next(), name, d);
                    }else{
                        res = g;
                        break;
                    }                    
                }else{
                    g = next_p;

                    if(it.hasNext()){
                        next_p = this.analyze_predicate(it.next(), name, d);
                    }else{
                        res = this.analyze_and_or_guard(res, g, separated_predicates.get(predicate));
                        break;
                    }
                }
                res = this.analyze_and_or_guard(res, next_p, separated_predicates.get(predicate));
            }            
            //check if inverted
            if(guard.get_invert_guard()){
                res = Neg.factory(res);
            }
        }catch(Exception e){
            System.out.println(e + " in SemanticAnalyzer/analyze_guard_of_predicates()");
        }
        
        return res;    
    }
    
    private Guard analyze_predicate(Syntactic_predicate synt_pr, String transition_name, Domain d) throws RuntimeException{
        ArrayList<String> predicate_txt = synt_pr.get_predicate_elements();
        Guard g = null;
        
        try{
            String p_txt = predicate_txt.get(0);
            
            if(predicate_txt.size() == 1) { 

                if(p_txt.contains("True")){
                    this.analyze_true_false_guard(true, d);
                }else if(p_txt.contains("False")){
                    this.analyze_true_false_guard(false, d);
                }else{
                    throw new RuntimeException("can't analyze predicates 1 element!");
                }
             //equality || membership
            }else{ //3 elements predicate -> projection, operation, projection/constant 
                //1st element
                Projection p1 = pa.analyze_projection_element(p_txt, transition_name);
                //2nd element
                String operation = predicate_txt.get(1);
                //3rd element
                String op3 = predicate_txt.get(2);
                
                switch (operation){
                    case "=":
                        g = this.analyze_equality_guard(p1, p1, true, d);
                        break;
                    case "!=":
                        g = this.analyze_equality_guard(p1, pa.analyze_projection_element(op3, transition_name), false, d);
                        break;
                    case "in":
                        g = this.analyze_membership_guard(p1, ca.analyze_constant_element(op3), true, d);
                        break;
                    case "!in":
                        g = this.analyze_membership_guard(p1, ca.analyze_constant_element(op3), false, d);
                        break;
                    default:
                        throw new RuntimeException("can't analyze predicate of 3 elements!");
                }
            }
            //check if inverted
            if(synt_pr.get_invert_guard()){ 
                g = Neg.factory(g);
            }                
        }catch(Exception e){
            System.out.println(e + " in SemanticAnalyzer/analyze_predicate()");
        }
        return g;
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
    
    //operation: true = in, false = !in
    private Guard analyze_equality_guard(Projection p1, Projection p2, boolean operation, Domain d){
        return Equality.builder(p1, p2, operation, d);
    }
    
    //operation: true = belongs to, false = doesn't belongs to
    private Guard analyze_membership_guard(Projection g1, Subcl constant, boolean operation, Domain d){
        return Membership.build(g1, constant, operation, d);
    }
    
    public static Guard_analyzer get_instance(){

        if(instance == null){
            instance = new Guard_analyzer();
        }
        
        return instance;
    }
}
