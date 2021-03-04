/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.Map;
import wncalculus.wnbag.BagfunctionTuple;
import wncalculus.wnbag.TupleBag;

/**
 *
 * @author dell
 */
public class Arc {
    
    protected String name;
//    protected ArrayList<Guard> guard_classORdomain; //[guard]<tuple_element> || [guard]<tuple_element,tuple_element,...> 
//    //tuple_element: sum of variable/constant, at least should have 1 variable or 1 constant 
//    //<variable + const> case of colorClass|| <variable + const, ...> case of domain
    protected TupleBag tb;  //will have a list of WNtuple that contains guard, tuple multiplicity : use TupleBag(Map<? extends BagfunctionTuple, Integer> m)
    
    //protected ArrayList<Boolean> invert_guarded_tuples; //default = false if the tuple isn't guarded
    //protected int level;
            
    public Arc(String name, Map<? extends BagfunctionTuple, Integer> multiplied_tuples){
        this.name = name;
        this.tb = new TupleBag(multiplied_tuples);
        //this.level = lvl;
    }    
    
    public Arc(String name){
        this.name = name;
        //this.level = lvl;
    }    
    
    public void set_TupleBag(TupleBag tb){
        this.tb = tb;
    }
    
    public TupleBag get_tuple_bag(){
        return this.tb;
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
