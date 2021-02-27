/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.ArrayList;
import java.util.HashMap;
import wncalculus.guard.Guard;
import wncalculus.wnbag.WNtuple;

/**
 *
 * @author dell
 */
public abstract class Arc {
    
    protected String name;
    protected ArrayList<Guard> guard_classORdomain; //[guard]<tuple_element> || [guard]<tuple_element,tuple_element,...> 
    //tuple_element: sum of variable/constant, at least should have 1 variable or 1 constant 
    //<variable + const> case of colorClass|| <variable + const, ...> case of domain
    protected HashMap<WNtuple, Integer> multiplied_tuple; 
    //protected int level;
    
    public void add_guard_colourClassORdomain(Guard g){
        this.guard_classORdomain.add(g);
    }
    
    public void add_mult_tupleOfcolourClass(WNtuple t, int mult){ //ex: 2<x>
        this.multiplied_tuple.put(t, mult);
    }
    
    public ArrayList<Guard> get_guards(){
        return this.guard_classORdomain;
    }
    
    public HashMap<WNtuple, Integer> arc_multiplicity(){
        return this.multiplied_tuple;
    }
    
    public String get_name(){
        return this.name;
    }
    
    public String get_type(){ // TArc or Inhibitor
        return this.getClass().getName();
    }

//could be helpful then   
//    public int get_level(){
//        return this.level;
//    }
    
}
