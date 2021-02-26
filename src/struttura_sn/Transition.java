/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.HashMap;
import wncalculus.guard.Guard;

/**
 *
 * @author dell
 */
public class Transition extends Node{
    
    private final Guard guard;
    
    public Transition(String name, Guard g){
        this.name = name;
        this.Next = new HashMap<>();
        this.previous = new HashMap<>();
        this.guard = g;
    }
    
    //next/previous node of a transition is a place
    //next arc of a transition is a transitioning arc
    public Place add_next_Node(TArc arc, Place p){
        this.Next.put(arc, p);
        p.add_previous_Node(arc, this);
        return p;
    }
    
    //previous arc of a transition might be an inhibitor or a transitioning arc
    public void add_previous_Node(Arc arc, Place p){
        this.previous.put(arc, p);
    }
    
    public Node get_next_by_Arc(TArc arc){
        return this.Next.get(arc);
    }
    
    public Node get_previous_by_Arc(Arc arc){
        return this.previous.get(arc);
    }
    
    public Guard get_guard(){
        return this.guard;
    }
    
    /*public boolean is_enabled(){
        return false;
    }*/
}
