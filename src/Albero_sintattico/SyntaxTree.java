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
    
    
    //single instance
    private static SyntaxTree instance = null;
    
    private SyntaxTree(){
        
    }
    
    public static SyntaxTree get_instance(){

    if(instance == null){
        instance = new SyntaxTree();
    }
        
        return instance;
    }
}
