/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Componenti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author dell
 */
//singleton
public class Variable_index_table { //used for assigning variables' indices for each colorclass
    
    private HashMap<String, ArrayList<String>> cc_vars_names; //all variables that are created for a colorclass, Note: variable index will be considered as projection index+1
    //single instance
    private static Variable_index_table instance = null;
    
    private Variable_index_table(){
        this.cc_vars_names = new HashMap<>();
    }
    
    public void add_cc_var_name(String cc, String var){
        
        if(this.cc_vars_names.containsKey(cc)){ //if cc exists then update it's ArrayList of variables
            ArrayList<String> vars_names = this.cc_vars_names.get(cc);
            vars_names.add(var);
            this.cc_vars_names.put(cc, vars_names);
        }else{ //if cc doesn't exist then add new row
            this.cc_vars_names.put(cc, new ArrayList<String>(List.of(var)));
        }
    }
    
    public int get_variable_index(String var){
        
        for(String cc : this.cc_vars_names.keySet()){
            ArrayList<String> cc_vars = this.cc_vars_names.get(cc);

            for(var i = 0; i < cc_vars.size(); i++){

                if(cc_vars.get(i).equals(var)){
                    return i+1;
                }
            }
                    
        }
        
        throw new RuntimeException("Can't find the index of var: " + var);
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
    
    public static Variable_index_table get_instance(){

        if(instance == null){
            instance = new Variable_index_table();
        }
        
        return instance;
    }
}
