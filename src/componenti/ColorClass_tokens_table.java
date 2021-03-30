/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componenti;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author dell
 */
//singleton
public class ColorClass_tokens_table { //contains each colorclass available token that were read from file, that will reduce colorclass tokens stimation dedicated to tag "finiteintrange"
    //each colorclass name will have its corrisponding ArrayList of subclasses names.
    //Note: if colorclass contains only 1 unnamed subclass then the subclass will have parent(key) colorclass's name
    private HashMap<String, ArrayList<String>> cc_subccs; //List is faster than Set in accessing/storing time
    //each subclass will have its corrisponding flag, true means that its values/tokens exist (explicit token-expressing), false means that it will be estimated by Token_estimator (implicit token-expressing)
    private HashMap<String, Boolean> subccs_values_exist;
    //each colorclass with its available values/tokens
    private HashMap<String, ArrayList<String>> subccs_values;
    //single instance
    private static ColorClass_tokens_table instance = null; 
    
    private ColorClass_tokens_table(){
        this.cc_subccs = new HashMap<>();
        this.subccs_values = new HashMap<>();
        this.subccs_values_exist = new HashMap<>();
    }
    
    public void add_colorclass_values(String cc, ArrayList<String> subccs){
        this.cc_subccs.put(cc, subccs);
    }
    
    public void set_explicit_cc_flag(String cc, boolean explicit){
        this.subccs_values_exist.put(cc, explicit);
    }
    
    public void add_cc_tokens_values(String cc, ArrayList<String> values){
        this.subccs_values.put(cc, values);
    }
    
    public ArrayList<String> get_subcc_values(String subcc){
        
        if(!subccs_values_exist.containsKey(subcc) || !this.is_cc_explicit(subcc)){ //assume that subclass isn't explicitly expressed if its keys doesn't exist in "subccs_values_exist"
            return null;
        }else{ //explicitly expressed subclass
            return this.subccs_values.get(subcc);
        }

    }
    
    private boolean is_cc_explicit(String cc){
        return this.subccs_values_exist.get(cc);
    }
    
    /**
     * 
     * @return single static instance
     */
    public static ColorClass_tokens_table get_instance(){

        if(instance == null){
            instance = new ColorClass_tokens_table();
        }
        
        return instance;
    }
    
}
