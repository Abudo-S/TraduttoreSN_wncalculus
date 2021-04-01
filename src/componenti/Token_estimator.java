/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componenti;

import eccezioni.BreakconditionException;
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
import wncalculus.expr.Interval;
import wncalculus.wnbag.LinearComb;

/**
 *
 * @author dell
 */
//singleton
public class Token_estimator { //used to estimate tokens of tag "finiteintrange"
    
    private final ColorClass_tokens_table cc_tt;
    private HashMap<Place, HashMap<ArrayList<LinearComb>,Integer>> marking;
    private SN sn;
    //single instance
    private static Token_estimator instance = null; 
    
    private Token_estimator(){
        cc_tt = ColorClass_tokens_table.get_instance();
        marking = Marking.get_instance().get_marking();
        sn = SN.get_instance();
    }
    
    public ArrayList<Token> get_estimated_cc_tokens(String cc_name){ //colorclass/sub-colorclass name
        ColorClass cc = sn.find_colorClass(cc_name);
        ArrayList<Token> tokens;
        
        if(cc == null){
            HashMap<Interval, ColorClass> associated_interval = this.search_subclass(cc_name);
            Interval inter = associated_interval.keySet().iterator().next();
            tokens = this.get_cc_tokens(cc_name, inter, associated_interval.get(inter));
        }else{
            Interval inter = cc.getConstraint(1);
            tokens = this.get_cc_tokens(cc_name, inter, cc);
        }
        
        return tokens;
    }
    
    private HashMap<Interval, ColorClass> search_subclass(String subcc_name) throws NullPointerException{ //hashmap of one element
        ArrayList<ColorClass> colorclasses = sn.get_C();

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
    
    private ArrayList<Token> get_cc_tokens(String cc_name, Interval inter, ColorClass cc){ //colorclass/sub-colorclass
        ArrayList<Token> tokens = new ArrayList<>();
        ArrayList<String> tokens_names = cc_tt.get_subcc_values(cc_name);
        
        if(tokens_names == null || tokens_names.isEmpty()){ //tokens of subclass "inter" are implicitly expressed and should be estimated
            //estimate subclass tokens
            tokens = this.estimate_tokens(inter, cc);
        }else{ //tokens of subclass "inter" are explicitly expressed

            for(String token_name : tokens_names){
                Token t = this.find_initial_marking_token(token_name, cc);

                if(t == null){
                    t = new Token(token_name, cc);
                }
                tokens.add(t);
            }
        }
        
        return tokens;
    }
    
    private ArrayList<Token> estimate_tokens(Interval inter, ColorClass cc) throws RuntimeException{ //tag "finiteintrange" where inter's lb != ub
        ArrayList<Token> tokens = this.find_created_cc_tokens(cc);
        ArrayList<String> tokens_names = (ArrayList<String>) tokens.stream().map(t -> t.get_Token_value()).collect(Collectors.toList());
        //update tokens with remaining tokens following inter's size
        if(inter.lb() == inter.ub()){
            throw new RuntimeException("Calling Token_estimator/estimate_tokens() for tags that are different from \"finiteintrange\": " + inter.name() + ", " + cc.name());
        }
        int size = inter.size();
        
        if(size == -1){
            throw new RuntimeException("Calling Token_estimator/estimate_tokens() for an unbounded interval" + inter.name() + ", " + cc.name());
        }
        
        String prefix = cc.name() + "_ct";
        if(!tokens.isEmpty()){
            prefix = this.find_token_prefix(tokens.get(0).get_Token_value());
        }
        
        for(var i = 0; i < size; i++){
            String t_name = prefix;
            
            for(var j = inter.lb(); j <= inter.ub() - i; j++){
                t_name += j;
                
                if(!tokens_names.contains(t_name)){ //stop loop if token name is acceptable
                    break;
                }
            }
            tokens.add(new Token(t_name, cc));
            
            if(tokens.size() == size){
               break;
            }
        }
        
        return tokens;
    }
    
    private ArrayList<Token> find_created_cc_tokens(ColorClass cc){
        ArrayList<Token> tokens = new ArrayList<>();
        Place_syntax_table pst = Place_syntax_table.get_instance();
        
        for(Place place : this.marking.keySet()){
            ArrayList<String> ccs_names = pst.get_place_values(place.get_name());
            
            if(ccs_names.contains(cc.name())){
                HashMap<ArrayList<LinearComb>,Integer> multiplied_tokens_tuples = this.marking.get(place);
                
                for(var i = 0; i < ccs_names.size(); i++){
                    
                    if(cc.name().equals(ccs_names.get(i))){ //linear-comb index in marking tuple
                        
                        for(ArrayList<LinearComb> comb_list : multiplied_tokens_tuples.keySet()){
                            Map<ElementaryFunction, Integer> comb_elements = (Map<ElementaryFunction, Integer>) comb_list.get(i).asMap();
                            
                            comb_elements.keySet().stream().filter(
                                    comb_element -> comb_element instanceof Token
                            ).forEach(
                                    comb_element -> tokens.add((Token) comb_element)
                            );
                        }
                    }
                }
            }
        }
        
        return tokens;
    }
    
    private Token find_initial_marking_token(String token_name, ColorClass cc){ //find token if exists in initial marking
        Token[] t_wrapper = new Token[1];
        
        try{
            this.marking.keySet().stream().filter(
                    place -> place.get_type().equals(cc.name()) || sn.find_domain(place.get_type()).asMap().containsKey(cc)
            ).forEach(
                    place -> {
                        HashMap<ArrayList<LinearComb>,Integer> multiplied_tokens_tuples = this.marking.get(place);

                        multiplied_tokens_tuples.keySet().stream().forEach(
                                tokens_tuple -> {

                                    tokens_tuple.stream().forEach(
                                            comb -> {
                                                Map<ElementaryFunction, Integer> comb_elements = (Map<ElementaryFunction, Integer>) comb.asMap();

                                                comb_elements.keySet().forEach(
                                                        comb_element -> {

                                                            if(comb_element instanceof Token){
                                                                Token t = (Token) comb_element; 

                                                                if(t.get_Token_value().equals(token_name)){
                                                                     t_wrapper[0] = t;
                                                                     throw new BreakconditionException();
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                    );
                                }
                        );
                    }
            );
        }catch(BreakconditionException bce){}
        
        return t_wrapper[0];
    }
    
    private String find_token_prefix(String example) throws NullPointerException{
        Pattern p = Pattern.compile(example);
        Matcher m = p.matcher("(.*)(\\d+)");
        
        if(m.find()){
            return m.group(0);
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
