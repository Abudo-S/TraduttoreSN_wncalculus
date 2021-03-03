/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import wncalculus.color.ColorClass;
import wncalculus.expr.Interval;

/**
 *
 * @author dell
 */
public class ColourClass extends ColorClass{ //a color class with explicit tokens representation that will help to find different modes of token/color assignment in reachability graph
    
   private Token[] available_tokens; // tokens that don't belong to a color class
   private HashMap<String, Token[]> subClasses; //subclass name, available tokens
   
   public ColourClass(String name, Interval interval, Token[] available_tokens, boolean ordered){ //finite enumeration
       super(name, interval, ordered);
       this.available_tokens = new Token[available_tokens.length];
       
       for(var i = 0; i < available_tokens.length; i++){
           this.available_tokens[i] = available_tokens[i];
       }
   }
   
   public ColourClass(String name, Interval interval, boolean ordered){  //range
       super(name, interval, ordered);
       this.available_tokens = new Token[(interval.ub() - interval.lb())+1];
       
       for(var i = interval.lb(); i < interval.ub(); i++){ //random assignment of token's value before knowing the correct assignment while place marking
           this.available_tokens[i] = new Token(this.name().toLowerCase().charAt(0) + String.valueOf(i), this);
       }
   }
   
   public ColourClass(String name, Interval interval){ //range
       this(name, interval, false);
   }
   
   public ColourClass(String name){
       super(name);
   }
   
   //partitioned color class of range or finite enumeration (useroperator tag)
   public ColourClass(String name, Interval[] intervals, HashMap<String, Token[]> subClasses, boolean ordered){ 
       super(name, intervals, ordered);
       this.subClasses = new HashMap<>(subClasses);
   }
   
   public void update_available_tokens_colorclass(Token[] available_tokens){
       this.available_tokens = available_tokens;
   }
   
   public void update_available_tokens_subcolorclass(String subcolorclass_name, Token[] tokens){
       this.subClasses.put(subcolorclass_name, tokens);
   }
   
   public Set<String> get_subclasses_names(){
       return this.subClasses.keySet();
   }
   
   public boolean is_available(Token t){
       return Arrays.binarySearch(available_tokens, t) >= 0;
   }
   
   public boolean is_available_in_subclass(Token t, String subclass_name){
       Token[] av_tokens = this.subClasses.get(subclass_name);
       
       for(var i = 0; i < av_tokens.length; i++){
           
           if(av_tokens[i].get_Token_value().equals(t.get_Token_value())){
               return true;
           }
       }
       
       return false;
   }
   
   public Token[] get_tokens(){
       return this.available_tokens;
   }
  
   public Token find_token(String token_name){

      for(Token t : this.available_tokens){

           if(t.get_Token_value().equals(token_name)){
                return t;
           }
       }

      for(String subclass_name : subClasses.keySet()){
           Token[] av_tokens = this.subClasses.get(subclass_name);
           
           for(var i = 0; i < av_tokens.length; i++){
               
               if(av_tokens[i].get_Token_value().equals(token_name)){
                   return av_tokens[i];
               }
           }
       }

    return null;
   }
   
}

