/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Albero_sintattico;

import java.util.ArrayList;

/**
 *
 * @author dell
 */
public class Syntactic_predicate { //element of Syntactic_guard
   
   private final boolean invert_guard; 
   private final ArrayList<String> predicate_elements; //1 or 3 elements
   
   /**
    * Note: if predicate_elements contains 1 element that means it's (True/False) predicate
    * Note: if predicate_elements contains 3 elements that means it's (Equality/Membership) predicate
    * @param invert_guard true if predicate is inverted, false otherwise
    * @param predicate_elements ArrayList of predicate elements that should contain 1 or 3 elements 
    */
   public Syntactic_predicate(boolean invert_guard, ArrayList<String> predicate_elements){
       this.invert_guard = invert_guard;
       this.predicate_elements = predicate_elements;
   }
   
   /**
    * 
    * @return if guard is inverted
    */
   public boolean get_invert_guard(){
       return this.invert_guard;
   }
   
   /**
    * 
    * @return ArrayList of predicate elements that should contain 1 or 3 elements 
    */
   public ArrayList<String> get_predicate_elements(){
       return this.predicate_elements;
   }
}
