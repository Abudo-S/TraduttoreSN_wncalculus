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
public class Syntactic_tuple { //element of Syntactic_arc
    
    private Syntactic_guard sg;
    private String[] tuple_elements;
    
    public Syntactic_tuple(String[] tuple_elements){
        this.tuple_elements = tuple_elements;
    }
    
    public void set_syntactic_guard(Syntactic_guard sg){
        this.sg = sg;
    }
    
    public Syntactic_guard get_syntactic_guard(){
        return this.sg;
    }
    
    public String[] get_tuple_elements(){
        return this.tuple_elements;
    }
}
