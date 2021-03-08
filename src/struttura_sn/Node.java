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
    
    protected HashMap<Node, Arc> Next;
    protected HashMap<Node, Arc> previous;
    protected HashMap<Node, Arc> Inib; //will be a next arc of place and previous arc of transition 
    protected String name;
    protected Domain d;//node domain contains all possibile colorclasses of a certain node, Note: transition's domain will have all colorclasses of variables that exist on connected arcs to it 
    
    public Node add_next_Node(Arc arc, Node n){
        return null;
    }
    
    public void add_previous_Node(Arc arc, Node n){}
    
    public void add_inib(Arc arc, Node n){
        this.Inib.put(n, arc);
    }
    
    public String get_name(){
        return this.name;
    }
    
    public HashMap<Node, Arc> get_next_nodes(){
        return this.Next;
    }
    
    public HashMap<Node, Arc> get_previous_nodes(){
        return this.previous;
    }
    
    public void set_node_domain(Domain d){
        this.d = d;
    }
    
    public Domain get_node_domain(){
        return this.d;
    }
}
