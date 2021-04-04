/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componenti;

import eccezioni.BreakconditionException;
import eccezioni.UnsupportedElementNameException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import struttura_sn.Marking;
import struttura_sn.Place;
import struttura_sn.SN;
import struttura_sn.Token;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.color.ColorClass;
import wncalculus.expr.Domain;
import wncalculus.expr.Interval;
import wncalculus.wnbag.LinearComb;

/**
 *
 * @author dell
 */
//singleton
public class Token_estimator { //used to estimate tokens of tag "finiteintrange"
    
    private final ColorClass_tokens_table cc_tt;
    //private final HashMap<Place, HashMap<ArrayList<LinearComb>,Integer>> marking;
    private final Marking_tokens_table mtt;
    private final SN sn;
    //single instance
    private static Token_estimator instance = null; 
    
    private Token_estimator(){
        this.cc_tt = ColorClass_tokens_table.get_instance();
        //this.marking = Marking.get_instance().get_marking();
        this.mtt = Marking_tokens_table.get_instance();
        this.sn = SN.get_instance();
    }
    
    /**
     * 
     * @param cc_name the name of colour class/sub-colour class that we want to search/estimate its color tokens
     * @return ArrayList of tokens found/created
     */
    public ArrayList<Token> get_estimated_cc_tokens(String cc_name){ //colorclass/sub-colorclass name
        ColorClass cc = this.sn.find_colorClass(cc_name);
        ArrayList<Token> tokens = new ArrayList<>();
        
        if(cc == null){ //subcc
            HashMap<Interval, ColorClass> associated_interval = this.search_subclass(cc_name);
            Interval inter = associated_interval.keySet().iterator().next();
            tokens = this.get_subcc_tokens(inter.name(), inter, associated_interval.get(inter));
        }else{ //cc
            Interval[] intervals = cc.getConstraints();
            
            if(intervals.length == 1){ //unpartitioned cc
                tokens = this.get_subcc_tokens(cc.name(), intervals[0], cc);
                //System.out.println(tokens.size() + cc.name());
            }else{ //partitioned cc
                
                for(Interval inter : intervals){
                  tokens.addAll(this.get_subcc_tokens(inter.name(), inter, cc));
                }
            }
        }
        
        return tokens;
    }
    
    /**
     * 
     * @param subcc_name the name of sub-colour class that we want to found its object
     * @return HashMap of one element (found sub-colour class associated with its parent colour class)
     * @throws NullPointerException if the given name doesn't match any sub-colour class name
     */
    private HashMap<Interval, ColorClass> search_subclass(String subcc_name) throws NullPointerException{ //hashmap of one element
        ArrayList<ColorClass> colorclasses = this.sn.get_C();

        for(ColorClass colorclass : colorclasses){
            Interval[] subclasses = colorclass.getConstraints();

            for(Interval subclass : subclasses){
                
                if(subclass.name().equals(subcc_name)){
                    HashMap<Interval, ColorClass> associated_interval = new HashMap<>(); 
                    associated_interval.put(subclass, colorclass);
                    return associated_interval;
                }
            }
        }
        
        throw new NullPointerException("Can't find suclass: " + subcc_name);
    }
    
    /**
     * 
     * @param cc_subcc_name the name of sub-colour class/Non partitioned colour class that we want to know its tokens
     * @param inter Interval object of sub-colour class/Non partitioned colour class that will be used in tokens estimation if needed
     * @param cc colour class that we be used to extract its created initial-marking tokens or for creating new tokens found
     * @return ArrayList of tokens found/created
     */
    private ArrayList<Token> get_subcc_tokens(String cc_subcc_name ,Interval inter, ColorClass cc){ //sub-colorclass
        ArrayList<Token> tokens = new ArrayList<>();
        ArrayList<String> tokens_names = this.cc_tt.get_cc_subcc_values(cc_subcc_name);
        //System.out.println(cc.name() + ","+cc_subcc_name + Arrays.toString(tokens_names.toArray()));
        
        if(tokens_names == null || tokens_names.isEmpty()){ //tokens of subclass "inter" are implicitly expressed and should be estimated
            //estimate subclass tokens
            tokens = this.estimate_tokens(inter, cc);  
        }else{ //tokens of subclass "inter" are explicitly expressed
            
            for(String token_name : tokens_names){
                Token t = this.find_initial_marking_token(token_name, cc, tokens_names);

                if(t == null){
                    t = new Token(token_name, cc);
                }
                tokens.add(t);
            }
        }
        
        return tokens;
    }
    
    /**
     * 
     * @param inter Interval object of sub-colour class that will be used in tokens estimation if needed
     * @param cc colour class that we be used to extract its created initial-marking tokens or for creating new tokens found
     * @return ArrayList of found/estimated tokens
     * @throws RuntimeException if sub-colour class interval is unbounded which can't be estimated 
     */
    private ArrayList<Token> estimate_tokens(Interval inter, ColorClass cc) throws RuntimeException{ //tag "finiteintrange" where inter's lb != ub
        ArrayList<Token> tokens = this.find_created_subcc_tokens(inter, cc);
        ArrayList<String> tokens_names = (ArrayList<String>) tokens.stream().map(t -> t.get_Token_value()).collect(Collectors.toList());
        //update tokens with remaining tokens following inter's size
//        if(inter.lb() == inter.ub()){
//            throw new RuntimeException("Calling Token_estimator/estimate_tokens() for tags that are different from \"finiteintrange\": " + inter.name() + ", " + cc.name());
//        }
        int size = inter.size();
        
        if(size == -1){
            throw new RuntimeException("Calling Token_estimator/estimate_tokens() for an unbounded interval" + inter.name() + ", " + cc.name());
        }
        
        String prefix = cc.name() + "_ct";
        if(!tokens.isEmpty()){
            prefix = this.find_token_prefix(tokens.get(0).get_Token_value(), inter);
        }
        //System.out.println(inter.name() + Arrays.toString(tokens_names.toArray()));
        if(tokens.size() != size){
            
            for(var i = 0; i < size; i++){
                String t_name = prefix;

                for(var j = inter.lb(); j <= inter.ub(); j++){
                    t_name += j;

                    if(!tokens_names.contains(t_name)){ //stop loop if token name is acceptable
                        tokens.add(new Token(t_name, cc));
                        tokens_names.add(t_name); //reserve that name
                        break;
                    }

                    t_name = prefix;
                }

                if(tokens.size() == size){
                   break;
                }
            }
        }
        
        return tokens;
    }
    
    /**
     * 
     * @param inter Interval object of sub-colour class/Non partitioned colour class that we want to find its created initial-marking tokens
     * @param cc colour class that we be used to extract its created initial-marking tokens
     * @return ArrayList of created tokens
     */
    private ArrayList<Token> find_created_subcc_tokens(Interval inter, ColorClass cc){
        ArrayList<Token> tokens = new ArrayList<>();
        ArrayList<Token> tokens_to_test = this.mtt.get_all_cc_tokens(cc);  
        Pattern p = Pattern.compile("(.*)(\\d+)");
        
        tokens_to_test.stream().forEach(
                token -> {
                   Matcher m = p.matcher(token.get_Token_value());
                   
                   int num;
                   if(m.find()){
                       num = Integer.parseInt(m.group(2));
                       
                       if(num >= inter.lb() && num <= inter.ub()){ //check if token belongs to subclass inter
                           tokens.add(token);
                       }
                   }
                }
        );
 
        return tokens;
    }
    
    /**
     * 
     * @param token_name the name of color token that we want to seach for it in initial marking table "mtt"
     * @param cc colour class that we be used to create new tokens found
     * @param available_tokens ArrayList of all color tokens available in cc
     * @return the token found, null otherwise
     */
    private Token find_initial_marking_token(String token_name, ColorClass cc, ArrayList<String> available_tokens){ //find token if exists in initial marking
        Token[] t_wrapper = new Token[1];        
        
        try{
            //System.out.println(cc.name() + Arrays.toString(available_tokens.toArray()));
            available_tokens.stream().filter(
                    available_token -> available_token.equals(token_name)
            ).forEach(
                    available_token -> {
                        t_wrapper[0] = mtt.get_created_similar_token(available_token);

                        if(t_wrapper[0] != null){
                            t_wrapper[0] = new Token(available_token, cc);
                            throw new BreakconditionException();
                        }  
                    }
            );
            //another solution based on place syntax table but it won't need Marking tokens table & ColorClass tokens table 
//            this.marking.keySet().stream().filter(
//                    place -> {
//                        
//                        if(place.get_type().equals(cc.name())){
//                            return true;
//                        }else{
//                            Domain d = this.sn.find_domain(place.get_type());
//                            
//                            if(d != null && d.asMap().containsKey(cc)){
//                                return true;
//                            }
//                        }
//                       
//                        return false;
//                    }
//            ).forEach(
//                    place -> {
//                        HashMap<ArrayList<LinearComb>,Integer> multiplied_tokens_tuples = this.marking.get(place);
//
//                        multiplied_tokens_tuples.keySet().stream().forEach(
//                                tokens_tuple -> {
//
//                                    tokens_tuple.stream().forEach(
//                                            comb -> {
//                                                Map<ElementaryFunction, Integer> comb_elements = (Map<ElementaryFunction, Integer>) comb.asMap();
//
//                                                comb_elements.keySet().forEach(
//                                                        comb_element -> {
//
//                                                            if(comb_element instanceof Token){
//                                                                Token t = (Token) comb_element; 
//
//                                                                if(t.get_Token_value().equals(token_name)){
//                                                                     t_wrapper[0] = t;
//                                                                     throw new BreakconditionException();
//                                                                }
//                                                            }
//                                                        }
//                                                );
//                                            }
//                                    );
//                                }
//                        );
//                    }
//            );
        }catch(BreakconditionException bce){}
        
        return t_wrapper[0];
    }
    
    /**
     * 
     * @param example token's value that we will use as a model to find its prefix for similar tokens creation
     * @param inter sub-colour class that will be used to validate "example" before returning the prefix usable in token estimation
     * @return the prefix found
     * @throws NullPointerException if prefix isn't extracted
     * @throws UnsupportedElementNameException  if example's data violate subclass's conditions or the prefix terminate with a number that could be matched as interval number
     */
    private String find_token_prefix(String example, Interval inter) throws NullPointerException, UnsupportedElementNameException{
        Pattern p = Pattern.compile("(.*)(\\d+)");
        Matcher m = p.matcher(example);
        
        if(m.find()){
            String prefix = m.group(1);
            int num = Integer.parseInt(m.group(2));
                    
            if(num < inter.lb() || num > inter.ub() || prefix.substring(prefix.length()-1).matches("\\d")){ //check if last characher in token's prefix is a number
                throw new UnsupportedElementNameException("Token's name checked is unsupported : " + example
                                                         + ", try another type of naming to soddisfy this format\"(token prefix that doesn't end with a number)" + inter.toString() + "\"");
            }
            
            return prefix;
        }
        
        throw new NullPointerException("Can't match token's prefix: " + example);
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Token_estimator get_instance(){

        if(instance == null){
            instance = new Token_estimator();
        }
        
        return instance;
    }
}
