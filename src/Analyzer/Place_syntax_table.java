/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Analyzer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author dell
 */
//singleton
public class Place_syntax_table { //used for "All" class functuon to follow ColorClass ordering
    
    private HashMap<String, String> syntax; //place_name and its type, will be added while adding a new place
    private HashMap<String, ArrayList<String>> type_values; //colorclass/domain type with its ordered value(s) of colorclasses' names, will be added while adding new colorclass/domain
    //single instance
    private static Place_syntax_table instance = null;
    
    private Place_syntax_table(){
        
    }
    
    public void add_place_type(String place_name, String type){
        this.syntax.put(place_name, type);
    }
    
    public void add_type_value(String type, ArrayList<String> value){
        this.type_values.put(type, value);
    }
    
    public ArrayList<String> get_place_value(String place_name){
        return this.type_values.get(this.syntax.get(place_name));
    }
    
    public static Place_syntax_table get_instance(){

        if(instance == null){
            instance = new Place_syntax_table();
        }
        
        return instance;
    }
}
