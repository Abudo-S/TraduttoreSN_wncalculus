/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.*;
import wncalculus.wnbag.LinearComb;

/**
 *
 * @author dell
 */
//singleton
public class Marking {
    
    // place -> token(s) of multiplicity n, a domained marking will have ArrayList of LinearComb with n multiplicity 
    //the ArrayList will have only 1 element in the marking of place of colorclass type 
    private static HashMap<Place, HashMap<ArrayList<LinearComb>, Integer>> marking; 
    //single instance
    private static Marking instance = null;
    
    private Marking(){
        this.marking = new HashMap<>();
    }
    
    public void mark_colored_place(Place p, HashMap<LinearComb, Integer> multiplied_token){ // mark a coloured place
        HashMap<ArrayList<LinearComb>, Integer> mark = new HashMap<>();
        
        multiplied_token.keySet().stream().forEach(
                comb_element -> mark.put(new ArrayList<LinearComb>(List.of(comb_element)), multiplied_token.get(comb_element))
        );
        
        marking.put(p, mark);
    }
    
    public void mark_domained_place(Place p, HashMap<ArrayList<LinearComb>, Integer> multiplied_token){ // mark a domained place
        marking.put(p, multiplied_token);
    }
    
    public Set<Place> get_all_marked_Places(){
        return marking.keySet();
    }
    
    public HashMap<ArrayList<LinearComb>, Integer> get_marking_of_place(Place p){
        return marking.get(p);
    }
    
    public void update_initial_marking(Marking m0){
        instance = m0;
    }
    
    public static Marking get_instance(){
        
        if(instance == null){
            instance = new Marking();
        }
        
        return instance;
    }
    
}
