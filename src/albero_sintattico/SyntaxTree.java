/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package albero_sintattico;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author dell
 */
//part of factory pattern
//singleton
public class SyntaxTree {
    
    //private SyntacticNode root; //undeterminated
    private static ArrayList<Syntactic_place> all_pl;
    private static ArrayList<Syntactic_transition> all_st;
    //single instance
    private static SyntaxTree instance = null;
    
    private SyntaxTree(){
        all_pl = new ArrayList<>();
        all_st = new ArrayList<>();
    }
    
    /**
     * 
     * @param synt_p syntactic place that will be a node of this tree
     */
    public void add_synt_place(Syntactic_place synt_p){
        all_pl.add(synt_p);
    }
    
    /**
     * 
     * @param synt_t syntactic transition that will be a node of this tree
     */
    public void add_synt_transition(Syntactic_transition synt_t){
        all_st.add(synt_t);
    }
    
    /**
     * 
     * @return ArrayList of all syntactic places that exist in this tree
     */
    public ArrayList<Syntactic_place> get_synt_places(){
        return all_pl;
    }
    
    /**
     * 
     * @return ArrayList of all syntactic transitions that exist in this tree
     */
    public ArrayList<Syntactic_transition> get_synt_transitions(){
        return all_st;
    }
    
    /**
     * 
     * @param name syntactic-place name that we want to retrieve its syntactic-place's object
     * @return syntactic-place object, null otherwise
     */
    public Syntactic_place find_synt_place(String name){
        return all_pl.stream().filter(
                    sp -> name.equals(sp.get_name())
               ).findFirst().orElse(null);
    }
    
    /**
     * 
     * @param name syntactic-transition name that we want to retrieve its syntactic-place's object
     * @return syntactic-transition object, null otherwise
     */
    public Syntactic_transition find_synt_transition(String name){
        return all_st.stream().filter(
                    st -> name.equals(st.get_name())
               ).findFirst().orElse(null);
    }
    
    /**
     * 
     * @param synt_p a syntactic-place object that will replace another syntactic-place object that has the same name
     * @param synt_t a syntactic-transition object that will replace another syntactic-transition object that has the same name
     */
    public void update_synt_p_t(Syntactic_place synt_p, Syntactic_transition synt_t){
        try{
            
            for(var i = 0; i < all_pl.size(); i++){
                
                if(all_pl.get(i).get_name().equals(synt_p.get_name())){
                    all_pl.set(i, synt_p);
                    break;
                }
            }

            for(var i = 0; i < all_st.size(); i++){
                
                if(all_st.get(i).get_name().equals(synt_t.get_name())){
                    all_st.set(i, synt_t);
                    break;
                }
            }
                        
        }catch(Exception e){
            System.err.println(e + " in SyntaxTree/update_synt_p_t");
        }
    }
    
    /**
     * 
     * @param arc_name the name of arc that we want to find its syntactic arc object
     * @return the Syntactic_arc found, null otherwise
     */
    public Syntactic_arc find_syntactic_arc_ByName(String arc_name){
        
        for(var i = 0; i < all_pl.size(); i++){
            HashMap<SyntacticNode, Syntactic_arc> associated_next = all_pl.get(i).get_all_next();
            
            for(SyntacticNode node : associated_next.keySet()){
                Syntactic_arc synt_arc = associated_next.get(node);
                
                if(synt_arc.get_name().equals(arc_name)){
                    return synt_arc;
                }
            }
        }
        
        for(var i = 0; i < all_st.size(); i++){
            HashMap<SyntacticNode, Syntactic_arc> associated_next = all_st.get(i).get_all_next();
            
            for(SyntacticNode node : associated_next.keySet()){
                Syntactic_arc synt_arc = associated_next.get(node);
                
                if(synt_arc.get_name().equals(arc_name)){
                    return synt_arc;
                }
            }
        }
        
        return null;
    } 
    
//    public void set_root(Syntactic_place root){
//        this.root = root;
//    }
//    
//    public SyntacticNode get_root(){
//        return this.root;
//    }
    
//    public static void update_instance(SyntaxTree ins){
//        instance = ins;
//    }
    
    /**
     * 
     * @return single static instance of SyntaxTree
     */
    public static SyntaxTree get_instance(){

        if(instance == null){
            instance = new SyntaxTree();
        }
        
        return instance;
    }
}
