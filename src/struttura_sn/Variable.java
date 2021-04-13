/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import eccezioni.BreakconditionException;
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
    private ArrayList<Projection> available_projections; //all projections of variable that are written on/around transitions, will be empty while variable declaration
    //Note: ArrayList<Projection> of a transition may contains x, x++, x--
    
    /**
     * 
     * @param variable_name the name of variable
     * @param colour_type the colour-class type of variable
     */
    public Variable(String variable_name, ColorClass colour_type){
        this.variable_name = variable_name;
        this.colour_type = colour_type;
        this.available_projections = new ArrayList<>();
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
     */
    private void add_available_projection(Projection p){
        this.available_projections.add(p);
    }
    
//    /**
//     * 
//     * @param index the index that we want to check if exists in available projections
//     * @return true if index is found, false otherwise
//     */
//    public boolean check_if_index_exists(int index){    
//        
//        for(Projection proj : this.available_projections){
//            
//            if(proj.getIndex() == index){
//                return true;
//            }
//        }
//        
//        return false;
//    }
    
    
    /**
     * 
     * @param index the index that we want to check if exists in available projections
     * @param successor_flag 1 in case of ++, -1 in case of --, 0 otherwise
     * @return 
     */
    public Projection get_available_projection(int index, int successor_flag){
        Projection[] p_wrapper = new Projection[1];
        
        try{
            this.available_projections.stream().forEach(
                    proj-> {

                        if(proj.getIndex() == index && proj.getSucc() == successor_flag){
                            p_wrapper[0] = proj;
                            throw new BreakconditionException();
                        }
                    }
            );
        }catch(BreakconditionException e){
            return p_wrapper[0];
        }
        
        Projection pro = Projection.builder(index, successor_flag, this.colour_type);
        this.add_available_projection(pro);
        return pro;
    }
    
}
