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
public abstract class SyntacticNode {
    
    protected final String name;
    protected HashMap<SyntacticNode, Syntactic_arc> next;
    
    /**
     * 
     * @param name node name
     */
    public SyntacticNode(String name){
        this.name = name;
        this.next = new HashMap<>();
    }
    
    /**
     * 
     * @return node name
     */
    public String get_name(){
        return this.name;
    }
    
    /**
     * 
     * @param sn next syntactic node of this object
     * @param sa the syntactic this is used to reach sn
     */
    public void add_next(SyntacticNode sn, Syntactic_arc sa){
        this.next.put(sn,sa);
    }
    
    /**
     * 
     * @return HashMap of all next syntactic nodes associated with their syntactic arcs
     */
    public HashMap<SyntacticNode, Syntactic_arc> get_all_next(){
        return this.next;
    }
    
    /**
     * 
     * @param element_name string of syntactic-node's name that might be a next of this object
     * @return syntactic node if found, null otherwise
     */
    public SyntacticNode get_next_node(String element_name){
        return this.next.keySet().stream().filter(
                e -> e.equals(element_name)
        ).findFirst().orElse(null);
    }
    
    /**
     * 
     * @param sn syntactic node that exists in HashMap "next"
     * @return the syntactic arc that is connected to sn
     */
    public Syntactic_arc get_arc(SyntacticNode sn){
        return this.next.get(sn);
    }
}
