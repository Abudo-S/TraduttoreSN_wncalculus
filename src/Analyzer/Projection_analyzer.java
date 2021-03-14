/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struttura_sn.SN;
import struttura_sn.Variable;
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
        Projection pro;
        
        if(m.find()){
            Variable v = sn.find_variable(m.group(1));
            String circ_op = m.group(2);

            if(circ_op.equals("")){
                pro  = Projection.builder(this.generate_projection_index(transition_name, v.get_name(), 0), 0, v.get_colourClass());
            }else{

                if(circ_op.contains("++")){
                    pro = Projection.builder(this.generate_projection_index(transition_name, v.get_name(), 1), 1, v.get_colourClass());

                }else if(circ_op.contains("--")){
                    pro = Projection.builder(this.generate_projection_index(transition_name, v.get_name(), -1), -1, v.get_colourClass());

                }else{
                    throw new NullPointerException("can't find successor in " + proj);
                }       
            }
            
            v.add_available_projection(pro);
            sn.update_variable_via_projection(v);
                            
        }else{
            throw new NullPointerException("can't find first projection in " + proj);
        }
        
        
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
