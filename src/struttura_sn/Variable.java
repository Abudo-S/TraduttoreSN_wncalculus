/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.ArrayList;
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
    private ArrayList<Projection> available_projections_t; //all projections of variable that are written on transitions, will be empty while variable declaration
    
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
    
//    public void add_available_projection(Arc arc, Projection p){
//        this.available_projections_a.put(arc, p);
//    }
    
    public void add_available_projection(Projection p){
        this.available_projections_t.add(p);
    }
    
//    public Projection get_available_projection(Arc arc){
//        return this.available_projections_a.get(arc);
//    }
    
    public Projection get_available_projection(int index){
        Projection p = null;
        
        for(var i = 0; i < this.available_projections_t.size(); i++){
            
            if(this.available_projections_t.get(i).getIndex() == index){
                p = this.available_projections_t.get(i);
                break;
            }
        }
        
        return p;
    }
    
}
