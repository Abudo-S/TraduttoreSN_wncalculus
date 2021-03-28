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
    
    /**
     * 
     * @param arc the inhibitor arc that is connected with node n
     * @param n the node reached by "arc" 
     */
    public void add_inib(ArcAnnotation arc, Node n){
        this.InibSet.put(n, arc);
    }
    
    /**
     * 
     * @return the name of node
     */
    public String get_name(){
        return this.name;
    }
    
    /**
     * 
     * @return HashMap of all nodes that are in the next level of this node and associated with a transiting arc
     */
    public HashMap<Node, ArcAnnotation> get_next_nodes(){
        return this.PostSet;
    }
    
    /**
     * 
     * @return HashMap of all nodes that are in the previous level of this node and associated with a transiting arc
     */
    public HashMap<Node, ArcAnnotation> get_previous_nodes(){
        return this.PreSet;
    }
    
    /**
     * Note: and inhibitor arc is a previous arc of transition node & a next arc of place node
     * @return HashMap of all nodes that are in the next/previous level of this node and associated with a inhibitor arc
     */
    public HashMap<Node, ArcAnnotation> get_inib_nodes(){
        return this.InibSet;
    }
    
    /**
     * 
     * @param d the domain that will be associated with this node
     */
    public void set_node_domain(Domain d){
        this.d = d;
    }
    
    /**
     * 
     * @return the domain associated with this node
     */
    public Domain get_node_domain(){
        return this.d;
    }
}
