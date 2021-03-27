/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package albero_sintattico;

import java.util.LinkedHashMap;

/**
 *
 * @author dell
 */
public class Syntactic_guard { //could be element of Syntactic_tuple or Syntactic_transition
    
    private final boolean invert_guard;
    private final LinkedHashMap<Syntactic_predicate, String> separated_predicates;
    
    /**
     * 
     * @param invert_guard a flag that signs if this guard inverted with (!-NOT)
     * @param separated_predicates map of syntactic predicates that each syntactic predicate is associated with a separator sign (and/or) with next syntactic predicate if exists
     */
    public Syntactic_guard(boolean invert_guard, LinkedHashMap<Syntactic_predicate, String> separated_predicates){
        this.invert_guard = invert_guard;
        this.separated_predicates = separated_predicates;
    }
    
    /**
     * 
     * @return if guard is inverted
     */
    public boolean get_invert_guard(){
        return this.invert_guard;
    }
    
    /**
     * 
     * @return map of syntactic predicates that each syntactic predicate is associated with a separator sign (and/or) with next syntactic predicate if exists
     */
    public LinkedHashMap<Syntactic_predicate, String> get_separated_predicates(){
        return this.separated_predicates;
    }
}
