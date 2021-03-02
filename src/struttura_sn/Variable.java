/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import wncalculus.classfunction.Projection;
import wncalculus.color.ColorClass;

/**
 *
 * @author dell
 */
public class Variable { //a projection is a variable in arc expression
    
    private final String variable_name;
    private final ColorClass colour_type;
    private Projection current_projection; //current assignment of projection, will be null while variable declaration
    
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
    
    public void set_current_projection(Projection p){
        this.current_projection = p;
    }
    
    public Projection get_current_projection(){
        return this.current_projection;
    }
    
}
