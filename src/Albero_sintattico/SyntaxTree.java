/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Albero_sintattico;

/**
 *
 * @author dell
 */
public class SyntaxTree {
    
    private final Syntactic_place root;
    //single instance
    private static SyntaxTree instance = null;
    
    private SyntaxTree(final Syntactic_place root){
        this.root = root;
    }
    
    public Syntactic_place get_root(){
        return this.root;
    }
    
    public static SyntaxTree get_instance(final Syntactic_place root){

        if(instance == null){
            instance = new SyntaxTree(root);
        }
        
        return instance;
    }
}
