/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Albero_sintattico;

import struttura_sn.SN;

/**
 *
 * @author dell
 */
public class SyntaxTree {
    
    private SyntacticNode root;
    //single instance
    private static SyntaxTree instance = null;
    
    private SyntaxTree(){

    }
    
    public void set_root(Syntactic_place root){
        this.root = root;
    }
    
    public SyntacticNode get_root(){
        return this.root;
    }
    
    public static void update_instance(SyntaxTree ins){
        instance = ins;
    }
    
    public static SyntaxTree get_instance(){

        if(instance == null){
            instance = new SyntaxTree();
        }
        
        return instance;
    }
}
