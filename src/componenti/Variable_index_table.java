/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componenti;

import java.util.HashMap;
import struttura_sn.SN;
import struttura_sn.Transition;
import struttura_sn.Variable;
import test.Semantic_DataTester;
import wncalculus.expr.Domain;

/**
 *
 * @author dell
 */
//singleton
public class Variable_index_table { //used for assigning projections' indices for each transition 
    //HashMap<t_name, HashMap<cc_name, HashMap<var_name, int>>> -> HashMap of all reserved indices of a certain colour-class variables for a transition
    private final HashMap<String, HashMap<String, HashMap<String, Integer>>> t_reserved_cc_var_indices; //all reserved indices of a certain transition variables of the same colorclass
    private final HashMap<Domain, HashMap<String, Integer>> filters_vars_indices;
    private static SN sn;
    //single instance
    private static Variable_index_table instance = null;
    
    private Variable_index_table(){
        this.t_reserved_cc_var_indices = new HashMap<>();
        this.filters_vars_indices = new HashMap<>();
        sn = SN.get_instance();
    }
    
    /**
     * 
     * @param tran the name of transition that we want to add its variables that remain around/in it 
     * @param d the domain of transition that we will extract used colour-classes in it
     */
    public void initialize_transition_cc(String tran, Domain d){
        HashMap<String, HashMap<String, Integer>> cc_t_vars = new HashMap<>();
        
        d.asMap().keySet().stream().forEach(
                cc -> cc_t_vars.put(cc.name(), new HashMap<>())
        );
        
        this.t_reserved_cc_var_indices.put(tran, cc_t_vars);
    }                
    
    /**
     * 
     * @return HashMap of all reserved indices of a certain transition variables of the same colour-class
     */
    public HashMap<String, HashMap<String, HashMap<String, Integer>>> get_all_reserved_indices(){
        return this.t_reserved_cc_var_indices;
    }
    
    /**
     * 
     * @param t_name the key transition that we use to get its all colour classes variables-indices map
     * @param cc_name the colour class that we want to get its all variables-indices-map
     * @param index the index for which we search in HashMap t_reserved_cc_var_indices
     * @return variable name if found, null otherwise
     */
    public String get_var_name_at_index(String t_name, String cc_name, int index){
        HashMap<String, Integer> vars_indices= this.t_reserved_cc_var_indices.get(t_name).get(cc_name);
        
        return vars_indices.keySet().stream().filter(
                var_name -> vars_indices.get(var_name) == index
        ).findFirst().orElse(null);
    }
    
    /**
     * 
     * @param tran the transition that we want to search in its variables
     * @param var variable name that we want to get its index to create a projection
     * @param d the domain of transition (used before transition's creation)
     * @param isFilter true if the variable is a filter's variable
     * @return the index available
     * @throws NullPointerException if variable or transition isn't found
     * @throws RuntimeException if this method can't assign an index
     * Note: +1 is added because that library wncalculus doesn't allow creating a projection with index 0 
     * Note: wncalculus doesn't allow creating projection index out of its colour-class's multiplicity in transition domain
     */
    public int get_variable_index(String tran, String var, Domain d, boolean isFilter) throws NullPointerException, RuntimeException{     

        if(isFilter){
            return this.get_filter_var_index(var, d);
        }
 
        Variable v = sn.find_variable(var);   

        if(v != null){
            int max_index;
            Transition t = sn.find_transition(tran);
            
            if(t == null){
                max_index = d.mult(v.get_colourClass());
            }else{
                max_index = t.get_node_domain().mult(v.get_colourClass());
            }

            //Semantic_DataTester.get_instance().test_domain(tran, d);
            for(int i = 1; i <= max_index; i++){
                String cc_name = v.get_colourClass().name();
                HashMap<String, HashMap<String, Integer>> cc_t_vars = this.t_reserved_cc_var_indices.get(tran);
                HashMap<String, Integer> vars_indices = cc_t_vars.get(cc_name);
                
                if(vars_indices.containsKey(var)){ //index is already reserved 
                    return vars_indices.get(var);
                    
                }else{ //new index should be reserved
                    boolean found = true;
                    
                    for(String var_name : vars_indices.keySet()){
                        
                        if(vars_indices.get(var_name) == i){ //index is reserved
                            found = false;
                        }
                    }
                    
                    if(found){
                        //update t_reserved_cc_var_indices
                        vars_indices.put(var, i);
                        cc_t_vars.put(cc_name, vars_indices);
                        this.t_reserved_cc_var_indices.put(tran, cc_t_vars);
                        
                        return i;
                    }
                }
            }
//            String cc_name = v.get_colourClass().name();
//                HashMap<String, HashMap<String, Integer>> cc_t_vars = this.t_reserved_cc_var_indices.get(tran);
//                HashMap<String, Integer> vars_indices = cc_t_vars.get(cc_name);
//                
//                vars_indices.keySet().stream().forEach(
//                        vr -> System.out.println(vr + "," + vars_indices.get(vr) + "," + tran)
//                );
            throw new RuntimeException("Can't assign an index to : " + var + " around/in " + tran);
        }
        
        throw new NullPointerException("Can't find variable: " + var);
    }
    
    /**
     * 
     * @param var variable name that we want to get its index to create a projection
     * @param d filter's domain
     * @return the index generated/found
     * @throws NullPointerException if couldn't find a variable with name as var/ if couldn't assign filter's variable index
     */
    public int get_filter_var_index(String var, Domain d) throws NullPointerException{

        if(this.filters_vars_indices.containsKey(d)){
            HashMap<String, Integer> f_d_vars= this.filters_vars_indices.get(d);
            
            if(f_d_vars.containsKey(var)){
                return f_d_vars.get(var);
            }
            Variable v = sn.find_variable(var);
            
            if(v == null){
                throw new NullPointerException("Can't find filter_variable: " + var);
            }
            
            int mult = d.mult(v.get_colourClass());
            for(var i = 1; i <= mult; i++){
                
                if(!f_d_vars.containsValue(mult)){
                    f_d_vars.put(var, mult);
                    this.filters_vars_indices.put(d, f_d_vars);
                    return i;
                }
            }
            
        }else{
            HashMap<String, Integer> map = new HashMap<>();
            map.put(var, 1);
            this.filters_vars_indices.put(d, map);
            return 1;
        }
        
        throw new NullPointerException("Can't assign filter variable index:" + var);
    }
    
//    public String find_var_of_cc(String cc_name, Domain d){
//        HashMap<String, Integer> vars_indices = this.filters_vars_indices.get(d);
//         
//        for(String var_name : vars_indices.keySet()){
//            Variable v = sn.find_variable(var_name);
//            
//            if(v != null){
//                if(v.get_colourClass().name().equals(cc_name)){
//                    return var_name;
//                }
//            }
//        }
//         
//        return null;
//    }
    
    /**
     * 
     * @param index the index for which we search in reserved indices of transition
     * @param t_name transition's name
     * @param cc_name the name of colour class that we want to get its created variables around transition
     * @return appropriate variable's name if found, null otherwise
     */
//    public String get_t_var_of_filter_v_index(int index, String t_name, String cc_name){
//        HashMap<String, Integer> cc_vars_indices = this.t_reserved_cc_var_indices.get(t_name).get(cc_name);
//        
//        for(String var : cc_vars_indices.keySet()){
//            
//            if(cc_vars_indices.get(var) == index){
//                return var;
//            }
//        }
//        
//        return null;
//    }
            
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
    public static Variable_index_table get_instance(){

        if(instance == null){
            instance = new Variable_index_table();
        }
        
        return instance;
    }
}
