/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import wncalculus.guard.Guard;
import wncalculus.wnbag.GuardedArcFunction;
import wncalculus.wnbag.WNtuple;

/**
 *
 * @author dell
 */
public class Arc {
    
    protected String name;
//    protected ArrayList<Guard> guard_classORdomain; //[guard]<tuple_element> || [guard]<tuple_element,tuple_element,...> 
//    //tuple_element: sum of variable/constant, at least should have 1 variable or 1 constant 
//    //<variable + const> case of colorClass|| <variable + const, ...> case of domain
    protected LinkedHashMap<GuardedArcFunction, Integer> multiplied_arc_tuples;  //uses WNtuple
    protected ArrayList<Boolean> invert_guarded_tuples; //default = false if the tuple isn't guarded
    //protected int level;
            
    public Arc(String name){
        this.name = name;
        //this.level = lvl;
        this.multiplied_arc_tuples = new LinkedHashMap<>();
    }        
    
    public void mult_GuardedArcFunction(Guard g, WNtuple tuple, int mult){
        this.multiplied_arc_tuples.put(new GuardedArcFunction(null, tuple, g), mult);
    }    
    
    public HashMap<WNtuple, Guard> get_guarded_tuples(){
        HashMap<WNtuple, Guard> guarded_t = new HashMap<>();
        
        this.multiplied_arc_tuples.keySet().stream().forEach(
                g_func -> guarded_t.put((WNtuple) g_func.expr(), g_func.guard())
        );
        
        return guarded_t;
    }
    
    public LinkedHashMap<GuardedArcFunction, Integer> get_mult_guarded_tuple(){
        return this.multiplied_arc_tuples;
    }
    
    public ArrayList<Boolean> get_invert_tuples(){
        return this.invert_guarded_tuples;
    }
    
    public String get_name(){
        return this.name;
    }
    
//    public String get_type(){ // TArc or Inhibitor
//        return this.getClass().getName();
//    }

//could be helpful then   
//    public int get_level(){
//        return this.level;
//    }
    
}
