/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componenti;

import java.util.HashMap;

/**
 *
 * @author dell
 */
public class subcc_token_index_table {
    //single instance
    private static subcc_token_index_table instance = null; 
    private HashMap<String, Integer[]> subcc_token_index;
    
    private subcc_token_index_table(){
        this.subcc_token_index = new HashMap<>();
    }
    
    /**
     * 
     * @param subcc the name of sub-class that we want to know its limits
     * @param limits array of sub-class's limits (lower bound, upper bound)
     */
    public void add_subcc_limits_indices(String subcc, Integer[] limits){
        this.subcc_token_index.put(subcc, limits);
    }
    
    /**
     * 
     * @param subcc the name of sub-class that we want to know its limits
     * @return true if subclass exists, false otherwise
     */
    public boolean check_if_has_subcc(String subcc){
        return this.subcc_token_index.containsKey(subcc);
    }
    
    /**
     * 
     * @param subcc the name of sub-class that we want to know its limits
     * @return array of sub-class's limits (lower bound, upper bound)
     */
    public Integer[] get_subcc_limits_indices(String subcc){
        return this.subcc_token_index.get(subcc);
    }
    
    /**
     * 
     * @return single static instance
     */
    public static subcc_token_index_table get_instance(){

        if(instance == null){
            instance = new subcc_token_index_table();
        }
        
        return instance;
    }
}
