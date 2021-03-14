/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Albero_sintattico;

import java.util.ArrayList;
import java.util.stream.Collectors;
import struttura_sn.SN;

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
    
    public void add_synt_place(Syntactic_place synt_p){
        all_pl.add(synt_p);
    }
    
    public void add_synt_transition(Syntactic_transition synt_t){
        all_st.add(synt_t);
    }
    
    public ArrayList<Syntactic_place> get_synt_places(){
        return all_pl;
    }
    
    public ArrayList<Syntactic_transition> get_synt_transition(){
        return all_st;
    }
    
    public Syntactic_place find_synt_place(String name){
        return all_pl.stream().filter(
                    sp -> name.equals(sp.get_name())
               ).findFirst().orElse(null);
    }
    
    public Syntactic_transition find_synt_transition(String name){
        return all_st.stream().filter(
                    st -> name.equals(st.get_name())
               ).findFirst().orElse(null);
    }
    
    public void update_synt_p_t(Syntactic_place synt_p, Syntactic_transition synt_t){
        try{
            
            for(var i = 0; i < all_pl.size(); i++){
                
                if(all_pl.get(i).get_name().equals(synt_p)){
                    all_pl.set(i, synt_p);
                    break;
                }
            }

            for(var i = 0; i < all_st.size(); i++){
                
                if(all_st.get(i).get_name().equals(synt_p)){
                    all_st.set(i, synt_t);
                    break;
                }
            }
                        
        }catch(Exception e){
            System.err.println(e + " in SyntaxTree/update_synt_p_t");
        }
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
    
    public static SyntaxTree get_instance(){

        if(instance == null){
            instance = new SyntaxTree();
        }
        
        return instance;
    }
}
