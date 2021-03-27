/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package albero_sintattico;

/**
 *
 * @author dell
 */
public class Syntactic_tuple { //element of Syntactic_arc
    
    private Syntactic_guard sg;
    private final String[] tuple_elements;
    
    /**
     * 
     * @param tuple_elements Array of all elements of tuple
     */
    public Syntactic_tuple(String[] tuple_elements){
        this.tuple_elements = tuple_elements;
    }
    
    /**
     * 
     * @param sg syntactic guard of this tuple object
     */
    public void set_syntactic_guard(Syntactic_guard sg){
        this.sg = sg;
    }
    
    /**
     * 
     * @return syntactic guard of this tuple object
     */
    public Syntactic_guard get_syntactic_guard(){
        return this.sg;
    }
    
    /**
     * 
     * @return Array of all elements of tuple
     */
    public String[] get_tuple_elements(){
        return this.tuple_elements;
    }

}
