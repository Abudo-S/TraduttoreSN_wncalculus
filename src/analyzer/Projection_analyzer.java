/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer;

import componenti.Variable_index_table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
     * @param tuple_vars_names all variables in tuple (Not repeated in the same linear combination)
     * @param d transition's domain that we use while analyzing projection index using Variable index table
     * @return the analysed projection
     * @throws NullPointerException if projection's text isn't matched by the matcher
     * @throws RuntimeException if the projection is a predecessor/successor and projection's colour class isn't circular/ordered
     */
    public Projection analyze_projection_element(String proj, String transition_name, ArrayList<String> tuple_vars_names, Domain d) throws NullPointerException, RuntimeException{
        Pattern p = Pattern.compile(str_rx_element);
        Matcher m = p.matcher(proj.replaceAll("\\s+", ""));
        Projection pro;

        if(m.find()){
            String var_pro = m.group(1);
            String circ_op = m.group(6);
            //check filter's syntax if exists
            p = Pattern.compile("[@]([_a-zA-Z0-9]*)([\\[](\\d+)[\\]])?");
            m = p.matcher(var_pro);
            boolean isFilter = false;
            //filter's syntax
            if(m.find()){ 
                isFilter = true;
                var_pro = m.group(1);
                String f_var_index = m.group(3);
                
                if(var_pro == null || var_pro.isEmpty()){ //@[i]               
                    var_pro = tuple_vars_names.get(Integer.parseInt(f_var_index));
                    
                }else{ //@C || @C[i]     
                    final String var_pro2 = var_pro;
                    
                    if(f_var_index == null || f_var_index.isEmpty()){ //@C
                        
                        var_pro = tuple_vars_names.stream().filter(
                                var_name -> sn.find_variable(var_name).get_colourClass().name().equals(var_pro2)
                        ).findFirst().orElse("");

                    }else{ //@C[i]
                        tuple_vars_names = tuple_vars_names.stream().filter(
                                var_name -> sn.find_variable(var_name).get_colourClass().name().equals(var_pro2)
                        ).collect(Collectors.toCollection(ArrayList::new));
                        
                        var_pro = tuple_vars_names.get(Integer.parseInt(f_var_index));
                    }
                }    
            }
            
            Variable v = sn.find_variable(var_pro);
            //System.out.println(proj + "," + var_pro);
            //if v == null, choose a variable
            String variable_name = v.get_name();
            int index = vit.get_variable_index(transition_name, variable_name, d, isFilter);
            
            if(circ_op.equals("")){
                //index = this.generate_projection_index(variable_name, 0);
                pro = v.get_available_projection(index, 0);

            }else{
                
                if(circ_op.contains("++")){
                    //index = this.generate_projection_index(variable_name, 1);  
                    
                    if(!v.get_colourClass().isOrdered()){
                        throw new RuntimeException("Trying to assign successor to variable: " + v.get_name() + ", while its colorclass isn't circular/ordered"); 
                    }
                    
                    pro = v.get_available_projection(index, 1);

                }else if(circ_op.contains("--")){
                    //index = this.generate_projection_index(variable_name, -1);
                    
                    if(!v.get_colourClass().isOrdered()){
                        throw new RuntimeException("Trying to assign predecessor to variable: " + v.get_name() + ", while its colorclass isn't circular/ordered"); 
                    }
                                        
                    pro = v.get_available_projection(index, 1);

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
