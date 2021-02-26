/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.HashMap;
import wncalculus.color.ColorClass;
import wncalculus.expr.Domain;

/**
 *
 * @author dell
 */
public class Place extends Node{
    
    private ColorClass colour_type = null;
    private Domain domain_type = null;
    
    public Place(String name, ColorClass type){
        this.name = name;
        this.Next = new HashMap<>();
        this.previous = new HashMap<>();
        this.colour_type = type;
    }
    
    public Place(String name, Domain type){
        this.name = name;
        this.Next = new HashMap();
        this.previous = new HashMap();
        this.domain_type = type;
    }
    
    //next/previous node of a place is a transition
    //next arc of a place might be an inhibitor or a transitioning arc
    public Transition add_next_Node(Arc arc, Transition t){
        this.Next.put(arc, t);
        t.add_previous_Node(arc, this);
        return t;
    }
    
    //previous arc of a place is a transitioning arc
    public void add_previous_Node(TArc arc, Transition t){
        this.previous.put(arc, t);
    }
    
    public Node get_next_by_Arc(Arc arc){
        return this.Next.get(arc);
    }
    
    public Node get_previous_by_Arc(TArc arc){
        return this.previous.get(arc);
    }
    
    public String get_type(){
        return (this.colour_type == null) ? this.domain_type.name() : this.colour_type.name();
    }
}
