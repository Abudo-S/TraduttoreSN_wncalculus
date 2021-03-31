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
