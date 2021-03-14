/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analyzer;

import java.util.Arrays;
import struttura_sn.SN;
import wncalculus.classfunction.Subcl;
import wncalculus.color.ColorClass;
import wncalculus.expr.Interval;

/**
 *
 * @author dell
 */
//singleton
public class Constant_analyzer extends ElementAnalyzer{
    //single instance
    private static Constant_analyzer instance = null;
    
    private Constant_analyzer(){
        sn = SN.get_instance();
    }
    
    //constant, es: subclass name
    public Subcl analyze_constant_element(String const_name){
        Subcl con = null;
        
        for(ColorClass cc : sn.get_C()){
            
            if(cc.name().equals(const_name)){ //search in colorclasses' names
                con = Subcl.factory(this.generate_subcl_index(const_name), cc);
                 break;
            }else{ //search in subclasses of colorclass
                Interval interval = Arrays.stream(cc.getConstraints()).filter(
                                                 sub_interval -> sub_interval.name().equals(const_name)
                                                 ).findFirst().orElse(null);
                if(interval != null){
                    con = Subcl.factory(this.generate_subcl_index(const_name), cc); //should we pass the sub-interval in which we have found the constant?
                    break;        
                }
            }
        }
                
        return con;
    }
    
    //subcl/constant index is 0 ?
    private int generate_subcl_index(String const_name){
        return this.generate_index(const_name, "", 0);
    }
    
    public static Constant_analyzer get_instance(){

        if(instance == null){
            instance = new Constant_analyzer();
        }
        
        return instance;
    }
}
