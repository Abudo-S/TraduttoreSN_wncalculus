/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer;

import componenti.Variable_index_table;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import struttura_sn.SN;
import struttura_sn.Variable;
import test.Semantic_DataTester;
import wncalculus.classfunction.Projection;
import wncalculus.expr.Domain;

/**
 *
 * @author dell
 */
//singleton
public class Projection_analyzer extends ElementAnalyzer{
    
    private static Variable_index_table vit;
    //single instance
    private static Projection_analyzer instance = null;
    
    private Projection_analyzer(){
        sn = SN.get_instance();
        vit = Variable_index_table.get_instance();
    }
    
    /**
     * 
     * @param proj projection's text that is found in the entered pnml file
     * @param transition_name the name of transition (that contains projection or with which an arc expression is connected that contains that projection)
     * @return the analysed projection
     * @throws NullPointerException if projection's text isn't matched by the matcher
     * @throws RuntimeException if the projection is a predecessor/successor and projection's colour class isn't circular/ordered
     */
    public Projection analyze_projection_element(String proj, String transition_name, Domain d) throws NullPointerException, RuntimeException{
        Pattern p = Pattern.compile(str_rx_element);
        Matcher m = p.matcher(proj.replaceAll("\\s+", ""));
        Projection pro;

        if(m.find()){
            Variable v = sn.find_variable(m.group(1));
            String variable_name = v.get_name();
            String circ_op = m.group(2);

            if(circ_op.equals("")){
                //index = this.generate_projection_index(variable_name, 0);
                pro = v.get_available_projection(vit.get_variable_index(transition_name, variable_name, d), 0);

            }else{
                
                if(circ_op.contains("++")){
                    //index = this.generate_projection_index(variable_name, 1);  
                    
                    if(!v.get_colourClass().isOrdered()){
                        throw new RuntimeException("Trying to assign successor to variable: " + v.get_name() + ", while its colorclass isn't circular/ordered"); 
                    }
                    
                    pro = v.get_available_projection(vit.get_variable_index(transition_name, variable_name, d), 1);

                }else if(circ_op.contains("--")){
                    //index = this.generate_projection_index(variable_name, -1);
                    
                    if(!v.get_colourClass().isOrdered()){
                        throw new RuntimeException("Trying to assign predecessor to variable: " + v.get_name() + ", while its colorclass isn't circular/ordered"); 
                    }
                                        
                    pro = v.get_available_projection(vit.get_variable_index(transition_name, variable_name, d), 1);

                }else{
                    throw new NullPointerException("Can't find successor/predecessor in " + proj);
                }       
            }
            sn.update_variable_via_projection(v);
            
            //System.out.println(v.get_name() + "," + pro.getIndex());
        }else{
            throw new NullPointerException("can't find first projection in " + proj);
        }
        
        return pro;
    }
    
    //successor_flag = 1 in case of ++, -1 in case of --, 0 otherwise
//    private int generate_projection_index(String variable_name, int successor_flag){
//        //return this.generate_index(transition_name, variable_name, successor_flag);
//        int index = 0;
//        
//        for(var i = 0; i< variable_name.length(); i++){
//            index += variable_name.charAt(i);
//        }
//        
//        return index + successor_flag;
//    }   
    
    /**
     * 
     * @return single static instance
     */
    public static Projection_analyzer get_instance(){

        if(instance == null){
            instance = new Projection_analyzer();
        }
        
        return instance;
    }
}
