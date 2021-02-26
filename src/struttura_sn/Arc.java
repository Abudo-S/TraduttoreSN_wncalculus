/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.ArrayList;
import java.util.HashMap;
import wncalculus.classfunction.Projection;
import wncalculus.guard.Guard;

/**
 *
 * @author dell
 */
public abstract class Arc { //will be updated with the ability of adding constants in tuple's elements
    
    protected String name;
    protected ArrayList<Guard> guard_classORdomain; //[guard]<variable> || [guard]<variable,variable,...> 
    protected HashMap<Projection[], Integer> multiplied_tuple; //<variable> case of colourClass|| <variable,variable,...> case of domain
    //protected int level;
    
    public void add_guard_colourClassORdomain(Guard g){
        this.guard_classORdomain.add(g);
    }
    
    public void add_mult_varOfcolourClass(Projection v, int mult){ //ex: 2<x>
        this.multiplied_tuple.put(new Projection[]{v}, mult);
    }
    
    public void add_mult_varsOfdomain(Projection[] vars, int mult){ //ex: 2<x,y>
        this.multiplied_tuple.put(vars, mult);
    }
    
    public ArrayList<Guard> get_guards(){
        return this.guard_classORdomain;
    }
    
    public HashMap<Projection[], Integer> arc_multiplicity(){
        return this.multiplied_tuple;
    }
    
    public String get_name(){
        return this.name;
    }
    
//    public int get_level(){
//        return this.level;
//    }
    
}
