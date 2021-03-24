/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.HashMap;
import wncalculus.expr.Domain;

/**
 *
 * @author dell
 */
public abstract class Node {
    
    protected HashMap<Node, ArcAnnotation> PostSet;
    protected HashMap<Node, ArcAnnotation> PreSet;
    protected HashMap<Node, ArcAnnotation> InibSet; //will be a next arc of place and previous arc of transition 
    protected String name;
    protected Domain d;//node domain contains all possibile colorclasses of a certain node, Note: transition's domain will have all colorclasses of variables that exist on connected arcs to it 
    
    public Node add_next_Node(ArcAnnotation arc, Node n){
        return null;
    }
    
    public void add_previous_Node(ArcAnnotation arc, Node n){}
    
    public void add_inib(ArcAnnotation arc, Node n){
        this.InibSet.put(n, arc);
    }
    
    public String get_name(){
        return this.name;
    }
    
    public HashMap<Node, ArcAnnotation> get_next_nodes(){
        return this.PostSet;
    }
    
    public HashMap<Node, ArcAnnotation> get_previous_nodes(){
        return this.PreSet;
    }
    
    public HashMap<Node, ArcAnnotation> get_inib_nodes(){
        return this.InibSet;
    }
    
    public void set_node_domain(Domain d){
        this.d = d;
    }
    
    public Domain get_node_domain(){
        return this.d;
    }
}
