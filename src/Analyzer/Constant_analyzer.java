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
                //constant index is 0 if the constant has the colorclass value itself
                con = Subcl.factory(0, cc);
                break;
            }else{ //search in subclasses of colorclass
//                Interval interval = Arrays.stream(cc.getConstraints()).filter(
//                                                 sub_interval -> sub_interval.name().equals(const_name)
//                                                 ).findFirst().orElse(null);
//                if(interval != null){
//                    con = Subcl.factory(this.generate_subcl_index(const_name), cc); //should we pass the sub-interval in which we have found the constant? No
//                    break;        
//                }
                Interval[] intervals = cc.getConstraints();
                
                for(var i = 0; i < intervals.length; i++){
                    
                    if(intervals[i].name().equals(const_name)){
                        con = Subcl.factory(i+1, cc); // 0 is reserved to the colorclass itself
                    }
                }
            }
        }
                
        return con;
    }
    
//    //subcl/constant index is always 0? it's 0 if the constant has the colorclass value itself
//    private int generate_subcl_index(String const_name){
//        return this.generate_index(const_name, "", 0);
//    }
    
    public static Constant_analyzer get_instance(){

        if(instance == null){
            instance = new Constant_analyzer();
        }
        
        return instance;
    }
}
