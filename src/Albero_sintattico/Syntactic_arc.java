/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Albero_sintattico;

import java.util.HashMap;
/**
 *
 * @author dell
 */
public class Syntactic_arc{
    
    private final String name;
    private boolean inhibitor;
    private HashMap<Syntactic_tuple, Integer> multiplied_tuples;
    
    /**
     * 
     * @param name arc name
     */
    public Syntactic_arc(String name){
        this.name = name;
        this.multiplied_tuples = new HashMap<>();
    }
    
    /**
     * 
     * @param st a syntactic tuple that exists in the arc expression related to this object 
     * @param multiplicity syntactic tuple multiplicity
     */
    public void add_multiplied_tuple(Syntactic_tuple st, int multiplicity){
        this.multiplied_tuples.put(st, multiplicity);
    }
    
    /**
     * 
     * @return HashMap of syntactic tuples associated with their multiplicity
     */
    public HashMap<Syntactic_tuple, Integer> get_all_tuples(){
        return this.multiplied_tuples;
    }
    
    /**
     * 
     * @param st syntactic tuple that we need to know its multiplicity
     * @return the multiplicity of st
     */
    public int get_tuple_multiplicity(Syntactic_tuple st){
        return this.multiplied_tuples.get(st);
    }
    
    /**
     * 
     * @param inhibitor is inhibitor flag
     * true means that this object is an inhibitor arc
     */
    public void set_type(boolean inhibitor){
        this.inhibitor = inhibitor;
    }
    
    /**
     * 
     * @return true if inhibitor, false otherwise
     */
    public boolean get_type(){
        return this.inhibitor;
    }
    
    /**
     * 
     * @return arc name
     */
    public String get_name(){
        return this.name;
    }
}
