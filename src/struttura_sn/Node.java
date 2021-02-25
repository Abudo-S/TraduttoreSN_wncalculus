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
    protected HashMap<Arc,Node> Next;
    protected HashMap<Arc,Node> previous;
    protected String name;
    
    /*public void add_next_Node(Arc arc, Node n){}
    public void add_previous_Node(Arc arc, Node n){}*/
    
    public String get_name(){
        return this.name;
    }
    
    public HashMap<Arc, Node> get_next_nodes(){
        return this.Next;
    }
    
    public HashMap<Arc, Node> get_previous_nodes(){
        return this.previous;
    }
}
