/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author dell
 */
//singleton
public class Marking {
    
    // place -> token(s) of multiplicity n, a domained marking will have array of tokens with n multiplicity 
    private static HashMap<Place, HashMap<Token[], Integer>> marking; 
    //single instance
    private static Marking instance = null;
    
    private Marking(){
        this.marking = new HashMap<>();
    }
    
    public void mark_colored_place(Place p, HashMap<Token, Integer> multiplied_token){ // mark a coloured place
        HashMap<Token[], Integer> mark = new HashMap<>();
        
        multiplied_token.keySet().stream().forEach(
                token -> mark.put(new Token[]{token}, multiplied_token.get(token))
        );
        
        marking.put(p, mark);
    }
    
    public void mark_domained_place(Place p, HashMap<Token[], Integer> multiplied_token){ // mark a domained place
        marking.put(p, multiplied_token);
    }
    
    public Set<Place> get_all_marked_Places(){
        return marking.keySet();
    }
    
    public HashMap<Token[], Integer> get_marking_of_place(Place p){
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
