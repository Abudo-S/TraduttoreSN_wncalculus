/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componenti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import struttura_sn.Marking;
import struttura_sn.Place;
import struttura_sn.SN;
import struttura_sn.Token;
import wncalculus.color.ColorClass;
import wncalculus.expr.Interval;
import wncalculus.wnbag.LinearComb;

/**
 *
 * @author dell
 */
//singleton
public class Token_estimator { //used to estimate tokens of tag "finiteintrange"
    
    private final ColorClass_tokens_table cc_tt;
    private HashMap<Place, HashMap<ArrayList<LinearComb>,Integer>> marking;
    //single instance
    private static Token_estimator instance = null; 
    
    private Token_estimator(){
        cc_tt = ColorClass_tokens_table.get_instance();
        marking = Marking.get_instance().get_marking();
    }
    

    
    private HashMap<Interval, ColorClass> search_subclass(String subcc_name) throws NullPointerException{ //hashmap of one element
        SN sn = SN.get_instance();
        ArrayList<ColorClass> colorclasses = sn.get_C();

        for(ColorClass colorclass : colorclasses){
            Interval[] subclasses = colorclass.getConstraints();

            for(Interval subclass : subclasses){
                
                if(subclass.name().equals(subcc_name)){
                    HashMap<Interval, ColorClass> associated_interval = new HashMap<>(); 
                    associated_interval.put(subclass, colorclass);
                    return associated_interval;
                }
            }
        }
        
        throw new NullPointerException("Can't find suclass: " + subcc_name);
    }
    
    private ArrayList<Token> get_cc_tokens(String cc_name, Interval inter, ColorClass cc){ //colorclass/sub-colorclass
        ArrayList<Token> tokens = new ArrayList<>();
        ArrayList<String> tokens_names = cc_tt.get_subcc_values(cc_name);
        
        if(tokens_names == null || tokens_names.isEmpty()){ //tokens of subclass "inter" are implicitly expressed and should be estimated
                //estimate subclass tokens
                tokens = this.estimate_tokens(inter);
            }else{ //tokens of subclass "inter" are explicitly expressed
            
                for(String token_name : tokens_names){
                    Token t = this.find_initial_marking_token(token_name);

                    if(t == null){
                        t = new Token(token_name, cc);
                    }
                    tokens.add(t);
                }
            }
        
        return tokens;
    }
    
    private ArrayList<Token> estimate_tokens(Interval inter){
        ArrayList<Token> tokens = new ArrayList<>();
        //to be completed
        return tokens;
    }
    
    private Token find_initial_marking_token(String token_name){ //find token if exists in initial marking
        //to be completed
        return null;
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Token_estimator get_instance(){

        if(instance == null){
            instance = new Token_estimator();
        }
        
        return instance;
    }
}
