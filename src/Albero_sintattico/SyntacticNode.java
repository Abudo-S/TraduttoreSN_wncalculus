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
    
    public SyntacticNode(String name){
        this.name = name;
    }
    
    public String get_name(){
        return this.name;
    }
    
    public void add_next(SyntacticNode sn, Syntactic_arc sa){
        this.next.put(sn,sa);
    }
    
    public HashMap<SyntacticNode, Syntactic_arc> get_all_next(){
        return this.next;
    }
    
    public SyntacticNode get_next_node(String element_name){
        return this.next.keySet().stream().filter(
                e -> e.equals(element_name)
        ).findFirst().orElse(null);
    }
    
    public Syntactic_arc get_arc(SyntacticNode sn){
        return this.next.get(sn);
    }
}
