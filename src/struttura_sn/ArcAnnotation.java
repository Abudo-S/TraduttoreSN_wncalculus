/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import wncalculus.wnbag.TupleBag;

/**
 *
 * @author dell
 */
public class ArcAnnotation {
    
    protected String name;
//    protected ArrayList<Guard> guard_classORdomain; //[guard]<tuple_element> || [guard]<tuple_element,tuple_element,...> 
//    //tuple_element: sum of variable/constant, at least should have 1 variable or 1 constant 
//    //<variable + const> case of colorClass|| <variable + const, ...> case of domain
    protected TupleBag tb;  //will have a list of WNtuple that contains guard, tuple multiplicity : use TupleBag(Map<? extends BagfunctionTuple, Integer> m)
    
    //protected ArrayList<Boolean> invert_guarded_tuples; //default = false if the tuple isn't guarded
    //protected int level;
    
    /**
     * 
     * @param name the name of arc
     * @param tb object of tuples that appear on arc (contains arc expression)
     */
    public ArcAnnotation(String name, TupleBag tb){
        this.name = name;
        this.tb = tb;
        //this.level = lvl;
    }    
    
    /**
     * 
     * @param name the name of arc
     */
    public ArcAnnotation(String name){
        this.name = name;
        //this.level = lvl;
    }    
    
    /**
     * 
     * @param tb tb object of tuples that appear on arc (contains arc expression)
     */
    public void set_TupleBag(TupleBag tb){
        this.tb = tb;
    }
    
    /**
     * 
     * @return tb object of tuples that appear on arc (contains arc expression)
     */
    public TupleBag get_tuple_bag(){
        return this.tb;
    }
    
    /**
     * 
     * @return the name assigned to this arc
     */
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
