/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struttura_sn.SN;
import wncalculus.classfunction.Projection;

/**
 *
 * @author dell
 */
//singleton
public class Projection_analyzer extends ElementAnalyzer{
    //single instance
    private static Projection_analyzer instance = null;
    
    private Projection_analyzer(){
        sn = SN.get_instance();
    }
    
    public Projection analyze_projection_element(String proj, String transition_name) throws NullPointerException{
        Pattern p = Pattern.compile(str_rx_element);
        Matcher m = p.matcher(proj.replaceAll("\\s+", ""));
        Projection pro = null;
        //to be completed
        //add projection to sn variable
        return pro;
    }
    
    //successor_flag = 1 in case of ++, -1 in case of --, 0 otherwise
    private int generate_projection_index(String transition_name, String variable_name, int successor_flag){
        return this.generate_index(transition_name, variable_name, successor_flag);
    }   
    
    public static Projection_analyzer get_instance(){

        if(instance == null){
            instance = new Projection_analyzer();
        }
        
        return instance;
    }
}
