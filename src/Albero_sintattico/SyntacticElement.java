/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Albero_sintattico;

import java.util.ArrayList;

/**
 *
 * @author dell
 */
public abstract class SyntacticElement {
    
    protected final String name;
    protected ArrayList<SyntacticElement> next;
    
    public SyntacticElement(String name){
        this.name = name;
    }
    
    public String get_name(){
        return this.name;
    }
    
    public void add_next(SyntacticElement se){
        this.next.add(se);
    }
    
    public ArrayList<SyntacticElement> get_all_next(){
        return this.next;
    }
    
    public SyntacticElement get_next(String element_name){
        return this.next.stream().filter(
                e -> e.equals(element_name)
        ).findFirst().orElse(null);
    }
}
