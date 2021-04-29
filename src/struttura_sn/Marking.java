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
        marking = new HashMap<>();
    }
    
    /**
     * Note: case of place of colour-class type
     * @param p the place that we want to mark 
     * @param multiplied_token HashMap of linear combinations associated with their multiplicities
     */
    public void mark_colored_place(Place p, HashMap<LinearComb, Integer> multiplied_token){ // mark a coloured place
        HashMap<ArrayList<LinearComb>, Integer> mark = new HashMap<>();
        
        multiplied_token.keySet().stream().forEach(
                comb_element -> mark.put(new ArrayList<LinearComb>(List.of(comb_element)), multiplied_token.get(comb_element))
        );
        
        marking.put(p, mark);
    }
    
    /**
     * Note: case of place of domaintype
     * @param p the place that we want to mark 
     * @param multiplied_token HashMap of ArrayList of linear combinations associated with their multiplicities
     */
    public void mark_domained_place(Place p, HashMap<ArrayList<LinearComb>, Integer> multiplied_token){ // mark a domained place
        marking.put(p, multiplied_token);
    }
    
    /**
     * 
     * @return 
     */
    public Set<Place> get_all_marked_Places(){
        return marking.keySet();
    }
    
    /**
     * 
     * @return 
     */
    public HashMap<Place, HashMap<ArrayList<LinearComb>, Integer>> get_marking(){
        return marking;
    }
    
    /**
     * 
     * @param p the place that we want to get its marking
     * @return HashMap of place marking (colour-class/domain) type
     * Note: the ArrayList of linear combination of colour-class typed place has only 1 element 
     */
    public HashMap<ArrayList<LinearComb>, Integer> get_marking_of_place(Place p){
        return marking.get(p);
    }
    
        /**
     * 
     * @param p_name the name of place that we want to get its marking
     * @return HashMap of place marking (colour-class/domain) type
     * Note: the ArrayList of linear combination of colour-class typed place has only 1 element 
     */
    public HashMap<ArrayList<LinearComb>, Integer> get_marking_of_place(String p_name){
        HashMap<ArrayList<LinearComb>, Integer> mk = new HashMap<>();
        
        Place p = marking.keySet().stream().filter(
                place -> place.get_name().equals(p_name)
        ).findFirst().orElse(null);
        
        if(p != null){
            mk = marking.get(p);
        }
        
        return mk;
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Marking get_instance(){
        
        if(instance == null){
            instance = new Marking();
        }
        
        return instance;
    }
    
}
