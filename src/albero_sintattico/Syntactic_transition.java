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
public class Syntactic_transition extends SyntacticNode{
    
    private Syntactic_guard sg;
    
    /**
     * 
     * @param name transition name
     */
    public Syntactic_transition(String name){
        super(name);
    }
    
    /**
     * 
     * @param sg syntactic guard of this syntactic-transition object
     */
    public void set_syntactic_guard(Syntactic_guard sg){
        this.sg = sg;
    }
    
    /**
     * 
     * @return syntactic guard of this syntactic-transition object
     */
    public Syntactic_guard get_syntactic_guard(){
        return this.sg;
    }
}
