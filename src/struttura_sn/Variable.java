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
    
    /**
     * 
     * @param variable_name the name of variable
     * @param colour_type the colour-class type of variable
     */
    public Variable(String variable_name, ColorClass colour_type){
        this.variable_name = variable_name;
        this.colour_type = colour_type;
        this.available_projections_t = new HashMap<>();
    }
    
    /**
     * 
     * @return the name of variable
     */
    public String get_name(){
        return this.variable_name;
    }
    
    /**
     * 
     * @return the colour-class type of variable
     */
    public ColorClass get_colourClass(){
        return this.colour_type;
    }
    
    /**
     * 
     * @param p an available created projection that refers to this variable
     * @param transition_name the name of transition associated with projection (p)
     */
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
    
    /**
     * 
     * @param index the index that we want to check if exists in available projections
     * @param transition_name the name of transition associated with projection's index
     * @return true if index is found, false otherwise
     */
    public boolean check_if_index_exists(int index, String transition_name){    
        ArrayList<Projection> projs;
        //System.out.println(available_projections_t.size());
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
    
    /**
     * 
     * @param index the index that we want to check if exists in available projections
     * @param transition_name the name of transition associated with projection's index
     * @param cc the colour class associated with index and successor_flag, from them we create a new projection if isn't available in available_projections_t
     * @param successor_flag 1 in case of ++, -1 in case of --, 0 otherwise
     * @return 
     */
    public Projection get_available_projection(int index, String transition_name, ColorClass cc, int successor_flag){
        Projection[] p_wrapper = new Projection[1];
        
        this.available_projections_t.keySet().stream().filter(
                t ->  t.get_name().equals(transition_name)
        ).forEach(
                t -> {
                        ArrayList<Projection> available_p = this.available_projections_t.get(t);
                        
                        p_wrapper[0] = available_p.stream().filter(
                                projection -> projection.getIndex().equals(index) && projection.getSucc() == successor_flag
                        ).findFirst().orElse(null);
                     }
        );
        
        if(p_wrapper[0] == null){
           p_wrapper[0] = Projection.builder(index, -1, cc); 
        }
        
        return p_wrapper[0];
    }
    
}
