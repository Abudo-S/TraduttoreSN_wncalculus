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
public abstract class SyntacticElement {
    
    protected final String name;
    
    public SyntacticElement(String name){
        this.name = name;
    }
    
    public String get_name(){
        return this.name;
    }
}
