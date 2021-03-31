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
    
    /**
     * 
     * @param cc parent colour-class that we want to add its subclasses 
     * @param subccs ArrayList of of subclasses (case of partitioned colour class) or parent colour-class name (case of non partitioned colour-class)
     */
    public void add_colorclass_subclasses(String cc, ArrayList<String> subccs){
        this.cc_subccs.put(cc, subccs);
    }
    
    /**
     * 
     * @param cc sub-colour-class / colour-class that we want to add its flag
     * @param explicit true means that its values/tokens exist (explicit token-expressing), false means that it will be estimated by Token_estimator (implicit token-expressing)
     */
    public void set_explicit_cc_flag(String cc, boolean explicit){
        this.subccs_values_exist.put(cc, explicit);
    }
    
    /**
     * 
     * @param cc sub-colour-class / colour-class that we want to add its tokens values
     * @param values ArrayList of tokens values
     */
    public void add_cc_tokens_values(String cc, ArrayList<String> values){
        this.subccs_values.put(cc, values);
    }
    
    /**
     * 
     * @param cc sub-colour-class / colour-class that we want know its tokens values
     * @return ArrayList of explicit tokens if exist (case of tags "finiteenumeration" & "useroperator"), null otherwise (case of tag "finiteintrange")
     */
    public ArrayList<String> get_subcc_values(String cc){
        
        if(!subccs_values_exist.containsKey(cc) || !this.is_cc_explicit(cc)){ //assume that subclass isn't explicitly expressed if its keys doesn't exist in "subccs_values_exist"
            return null;
        }else{ //explicitly expressed subclass
            return this.subccs_values.get(cc);
        }

    }
    
    /**
     * 
     * @param cc the name of colour class/sub-colour-class that we want to know if its tokens are explicitly expressed
     * @return true if cc is explicitly expressed, false otherwise
     */
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
