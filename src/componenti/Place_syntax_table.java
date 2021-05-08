/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package componenti;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author dell
 */
//singleton
public class Place_syntax_table { //used for analyzing "All" class functuon to follow ColorClass ordering
    
    private final HashMap<String, String> syntax; //place_name and its type, will be added while adding a new place
    private final HashMap<String, ArrayList<String>> type_values; //colorclass/domain type with its ordered value(s) of colorclasses' names, will be added while adding new colorclass/domain
    //single instance
    private static Place_syntax_table instance = null;
    
    private Place_syntax_table(){
        this.syntax = new HashMap<>();
        this.type_values = new HashMap<>();
    }
    
    /**
     * 
     * @param place_name place name in SN
     * @param type place type (colour class/domain)
     */
    public void add_place_type(String place_name, String type){
        this.syntax.put(place_name, type);
    }
    
    /**
     * 
     * @param type a place type (colour class/domain)
     * @param value ArrayList of ordered values of that type
     * Note: if type is a colour class then the corresponding ArrayList will contain only one element which is colour-class's name
     * Note: if type is a domain then the corresponding ArrayList will contain n > 1 elements which are colour-classes names
     */
    public void add_type_value(String type, ArrayList<String> value){
        this.type_values.put(type, value);
    }
    
    /**
     * 
     * @param cd_name colour domain name that we want to know its colour class(es) ordering
     * @return colour domain's colour-class(es) ordering/syntax
     */
    public ArrayList<String> get_cd_values(String cd_name){
        return this.type_values.get(cd_name);
    }
    /**
     * 
     * @param place_name place name that we want to know its values that should follow a certain colour-classes ordering/syntax
     * @return place's colour-classes ordering/syntax
     */
    public ArrayList<String> get_place_values(String place_name){
        return this.type_values.get(this.syntax.get(place_name));
    }
    
    /**
     * 
     * @return single static instance
     */
    public static Place_syntax_table get_instance(){

        if(instance == null){
            instance = new Place_syntax_table();
        }
        
        return instance;
    }
}
