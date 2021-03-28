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
    
    /**
     * 
     * @param name the name of transition
     * @param g the guard of transition
     */
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
    
    /**
     * 
     * @param arc the arc expression associated with next node (p) of transition
     * @param p next node of transition
     * @return the updated next node (that contains transition as a previous node)
     */
    //next/previous node of a transition is a place
    //next arc of a transition is a transitioning arc
    @Override
    public Node add_next_Node(ArcAnnotation arc, Node p){
        this.PostSet.put(p, arc);
        p.add_previous_Node(arc, this);
        return p;
    }
    
    /**
     * 
     * @param arc the arc expression associated with previous node (p) of transition
     * @param p previous node of transition
     */
    //previous arc of a transition might be an inhibitor or a transitioning arc
    @Override
    public void add_previous_Node(ArcAnnotation arc, Node p){
        this.PreSet.put(p, arc);
    }
    
    /**
     * 
     * @param node the arc expression associated with next node (node)
     * @return the arc expression found, null otherwise
     */
    public ArcAnnotation get_next_by_node(Node node){
        return this.PostSet.get(node);
    }
    
    /**
     * 
     * @param node the arc expression associated with previous node (node)
     * @return the arc expression found, null otherwise
     */
    public ArcAnnotation get_previous_by_node(Node node){
        return this.PreSet.get(node);
    }
    
    /**
     * 
     * @return the guard associated with transition (if exists)
     */
    public Guard get_guard(){
        return this.guard;
    }
    
    /*public boolean is_enabled(){
        return false;
    }*/
}
