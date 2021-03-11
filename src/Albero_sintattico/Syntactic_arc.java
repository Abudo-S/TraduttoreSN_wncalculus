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
    
    public Syntactic_arc(String name){
        this.name = name;
        this.inhibitor = inhibitor;
    }
    
    public void add_multiplied_tuple(Syntactic_tuple st, int multiplicity){
        this.multiplied_tuples.put(st, multiplicity);
    }
    
    public HashMap<Syntactic_tuple, Integer> get_all_tuples(){
        return this.multiplied_tuples;
    }
    
    public int get_tuple_multiplicity(Syntactic_tuple st){
        return this.multiplied_tuples.get(st);
    }
    
    public void set_type(boolean inhibitor){
        this.inhibitor = inhibitor;
    }
    
    public boolean get_type(){
        return this.inhibitor;
    }
    
    public String get_name(){
        return this.name;
    }
}
