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
public class SyntaxTree {
    
    //private SyntacticNode root; //undeterminated
    private ArrayList<Syntactic_place> all_pl;
    private ArrayList<Syntactic_transition> all_st;
    //single instance
    private static SyntaxTree instance = null;
    
    private SyntaxTree(){

    }
    
    public void add_synt_place(Syntactic_place synt_p){
        this.all_pl.add(synt_p);
    }
    
    public void add_synt_transition(Syntactic_transition synt_t){
        this.all_st.add(synt_t);
    }
    
    public ArrayList<Syntactic_place> get_synt_places(){
        return this.all_pl;
    }
    
    public ArrayList<Syntactic_transition> get_synt_transition(){
        return this.all_st;
    }
    
    public Syntactic_place find_synt_place(String name){
        return all_pl.stream().filter(
                    sp -> sp.get_name().equals(name)
               ).findFirst().orElse(null);
    }
    
    public Syntactic_transition find_synt_transition(String name){
        return all_st.stream().filter(
                    st -> st.get_name().equals(name)
               ).findFirst().orElse(null);
    }
    
    public void update_synt_p_t(Syntactic_place synt_p, Syntactic_transition synt_t){
        
        try{
            all_pl = (ArrayList<Syntactic_place>) all_pl.stream()
                .filter(synt_place -> synt_place.get_name().equals(synt_p.get_name()))
                .map(synt_place -> synt_p)
                .collect(Collectors.toList());

            all_st = (ArrayList<Syntactic_transition>) all_st.stream()
                    .filter(synt_transition -> synt_transition.get_name().equals(synt_t.get_name()))
                    .map(transition -> synt_t)
                    .collect(Collectors.toList());
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
