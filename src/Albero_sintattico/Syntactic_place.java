/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Albero_sintattico;

import struttura_sn.Place;
import struttura_sn.SN;

/**
 *
 * @author dell
 */
public class Syntactic_place extends SyntacticNode{
    
    /**
     * 
     * @param name syntactic-place's name
     */
    public Syntactic_place(String name){
        super(name);
    }
    
    /**
     * 
     * @return place's equivalent object from SN 
     */
    public Place get_place_from_sn(){
        return SN.get_instance().find_place(this.name);
    }
}
