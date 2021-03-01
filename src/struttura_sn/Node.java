/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.HashMap;

/**
 *
 * @author dell
 */
public abstract class Node {
    
    protected HashMap<Node, Arc> Next;
    protected HashMap<Node, Arc> previous;
    protected HashMap<Node, Arc> Inib; // will be a next arc of place and previous arc of transition 
    protected String name;
    
    /*public void add_next_Node(Arc arc, Node n){}
    public void add_previous_Node(Arc arc, Node n){}*/
    
    public String get_name(){
        return this.name;
    }
    
    public HashMap<Node, Arc> get_next_nodes(){
        return this.Next;
    }
    
    public HashMap<Node, Arc> get_previous_nodes(){
        return this.previous;
    }
}
