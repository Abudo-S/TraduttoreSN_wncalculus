/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componenti;

import java.util.ArrayList;
import java.util.HashSet;
import struttura_sn.Token;
import wncalculus.color.ColorClass;

/**
 *
 * @author dell
 */
//singleton
public class Marking_tokens_table {
    private HashSet<Token> created_token;
    //single instance
    private static Marking_tokens_table instance = null;
    
    private Marking_tokens_table(){
        this.created_token = new HashSet<>();
    }
    
    /**
     * 
     * @param t add new created token from initial marking
     */
    public void add_created_token(Token t){
        this.created_token.add(t);
    }
    
    /**
     * 
     * @param token_name token's name which we want to search if there's a pre-created token that has this name
     * @return token if found, null otherwise
     */
    public Token get_created_similar_token(String token_name){
        
        return this.created_token.stream().filter(
                token ->  token.get_Token_value().equals(token_name)
        ).findFirst().orElse(null);
    }
    
    /**
     * 
     * @param cc the colour class which we want to get its created tokens
     * @return ArrayList of created tokens found
     */
    public ArrayList<Token> get_all_cc_tokens(ColorClass cc){
        ArrayList<Token> cc_tokens = new ArrayList<>();
        
        this.created_token.stream().filter(
                t -> (t.get_type().name().equals(cc.name()))
        ).forEach(
                t -> cc_tokens.add(t)
        );
        
        return cc_tokens;
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Marking_tokens_table get_instance(){
        
        if(instance == null){
            instance = new Marking_tokens_table();
        }
        
        return instance;
    }
}
