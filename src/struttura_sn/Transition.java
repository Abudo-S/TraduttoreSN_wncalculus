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
        this.PostSet = new HashMap<>();
        this.PreSet = new HashMap<>();
        this.InibSet = new HashMap<>();
        this.guard = g;
    }
    
//    public Transition(String name){
//        this.name = name;
//        this.Next = new HashMap<>();
//        this.previous = new HashMap<>();
//    }
//    
//    public void set_guard(Guard g){
//        this.guard = g ;
//    }
    
    //next/previous node of a transition is a place
    //next arc of a transition is a transitioning arc
    @Override
    public Node add_next_Node(ArcAnnotation arc, Node p){
        this.PostSet.put(p, arc);
        p.add_previous_Node(arc, this);
        return p;
    }
    
    //previous arc of a transition might be an inhibitor or a transitioning arc
    @Override
    public void add_previous_Node(ArcAnnotation arc, Node p){
        this.PreSet.put(p, arc);
    }
    
    public ArcAnnotation get_next_by_node(Node node){
        return this.PostSet.get(node);
    }
    
    public ArcAnnotation get_previous_by_node(Node node){
        return this.PreSet.get(node);
    }
    
    public Guard get_guard(){
        return this.guard;
    }
    
    /*public boolean is_enabled(){
        return false;
    }*/
}
