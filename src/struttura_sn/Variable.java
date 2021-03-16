/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.ArrayList;
import java.util.HashMap;
import wncalculus.classfunction.Projection;
import wncalculus.color.ColorClass;

/**
 *
 * @author dell
 */
public class Variable { //a projection is a variable in arc expression
    
    private final String variable_name;
    private final ColorClass colour_type;
    //private HashMap<Arc, Projection> available_projections_a; //all projections of variable that are written on arcs, will be empty while variable declaration
    private HashMap<Transition, ArrayList<Projection>> available_projections_t; //all projections of variable that are written on transition, will be empty while variable declaration
    //Note: ArrayList<Projection> of a transition may contains x, x++, x--
    
    public Variable(String variable_name, ColorClass colour_type){
        this.variable_name = variable_name;
        this.colour_type = colour_type;
    }
    
    public String get_name(){
        return this.variable_name;
    }
    
    public ColorClass get_colourClass(){
        return this.colour_type;
    }
    
    public void add_available_projection(Projection p, String transition_name){
        this.available_projections_t.keySet().stream().filter(
                t -> t.get_name().equals(transition_name)
        ).forEach(
                t -> {
                        ArrayList<Projection> available_p = this.available_projections_t.get(t);
                        available_p.add(p);
                        this.available_projections_t.put(t, available_p);
                     }
        );
    }
    
    public boolean check_if_index_exists(int index, String transition_name){    
        ArrayList<Projection> projs;
        
        for(Transition t : this.available_projections_t.keySet()){
            
            if(t.get_name().equals(transition_name)){
                projs = this.available_projections_t.get(t);
                
                for(Projection pro : projs){

                    if(pro.getIndex() == index){
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public Projection get_available_projection(int index, String transition_name){
        Projection[] p_wrapper = new Projection[1];
        
        this.available_projections_t.keySet().stream().filter(
                t ->  t.get_name().equals(transition_name)
        ).forEach(
                t -> {
                        ArrayList<Projection> available_p = this.available_projections_t.get(t);
                        
                        p_wrapper[0] = available_p.stream().filter(
                                projection -> projection.getIndex().equals(index)
                        ).findFirst().orElse(null);
                     }
        );
        
        return p_wrapper[0];
    }
    
}
