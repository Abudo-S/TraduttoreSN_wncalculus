/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author dell
 */
//singleton
public class XML_DataTester {
    //single instance
    private static XML_DataTester instance = null;
    
    private XML_DataTester(){
        
    }
    
    /**
     * 
     * Note: case of tag "finitintrange"
     * @param class_name the name of colour class
     * @param start the start of colour-class's interval
     * @param end the start of colour-class's interval
     * @param circular true if colour class is circular, false otherwise
     */
    public void test_add_ColorClass(String class_name, int start, int end, boolean circular){
        System.out.println(class_name + ":" + start + "," + end + "," + circular);
    }
    
    /**
     * Note: case of tag "finiteenumeration"
     * @param class_name the name of colour class
     * @param token_names ArrayList of explicit tokens names
     * @param circular circular true if colour class is circular, false otherwise
     */
    public void test_add_ColorClass(String class_name, ArrayList<String> token_names, boolean circular){
        System.out.println(class_name + "," + circular + ",...");
        token_names.stream().forEach(e -> System.out.print(e + "-"));
        System.out.println();
    }
    
    /**
     * 
     * @param class_name the name of colour class
     * @param subclasses HashMap of subclasses names associated with their explicit elements (case of tag "finiteenumeration") or their ranges (case of "finitintrange")
     * @param circular circular true if colour class is circular, false otherwise
     */
    public void test_add_ColorClass(String class_name, HashMap<String, ArrayList<String>> subclasses, boolean circular){
        System.out.println(class_name + ",...");
        subclasses.keySet().forEach(str -> {
            subclasses.get(str).stream().forEach(e -> System.out.print(e + "-"));
        });
        System.out.println();
    }
    
    /**
     * 
     * @param variable_name the name of variable
     * @param variable_type the colour-class type of variable
     */
    public void test_add_Variable(String variable_name, String variable_type){
        System.out.println(variable_name + "," + variable_type);
    }
    
    /**
     * 
     * @param domain_name the name of domain
     * @param colorclasses ArrayList of domain ordered domain colour classes
     */
    public void test_add_Domain(String domain_name, ArrayList<String> colorclasses){
        System.out.println(domain_name + "...");
        colorclasses.stream().forEach(e -> System.out.print(e + "-"));
        System.out.println();
    }
    
    /**
     * 
     * @param place_name the name of place
     * @param place_type (the colour-class/domain) type of place
     */
    public void test_add_Place(String place_name, String place_type){
        System.out.println(place_name + "," + place_type);
    }
    
    /**
     * Note: case of place of colour-class type
     * @param place_name the name of place
     * @param tokens HashMap of explicit tuples of tokens added as a marking of place
     * Note: HashMap "tokens" may contain classFunction "All" which is an implicit marking
     */
    public void test_add_Marking_colorclass(String place_name, HashMap<String, Integer> tokens){
        System.out.println(place_name + "...");
        tokens.keySet().stream().forEach(e -> System.out.print(tokens.get(e) + "*" + e + "-"));
        System.out.println();
    }
    
    /**
     * Note: case of place of domain type 
     * @param place_name the name of place
     * @param tokens HashMap of explicit tuples of tokens added as a marking of place
     * Note: HashMap "tokens" may contain classFunction "All" which is an implicit marking
     */
    public void test_add_Marking_domain(String place_name, HashMap<String[], Integer> tokens){
        System.out.println(place_name + "...");
        tokens.keySet().stream().forEach(
                   t_arr -> {
                       System.out.print(tokens.get(t_arr) + "*(");
                       Arrays.stream(t_arr).forEach(t -> System.out.print(t + "-"));
                       System.out.print(")");
                   });
        System.out.println();
    }
    
    /**
     * 
     * @param transition_name the name of transition that contains guard
     * @param guard the guard of transition
     * @param invert_guard true if predicate is inverted, false otherwise
     */
    public void test_add_Transition(String transition_name, LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>  guard, boolean invert_guard){
        System.out.println(transition_name + "," + invert_guard + ",...");
        
        if(guard != null){
            guard.keySet().stream().forEach(
                    e -> e.keySet().stream().forEach(
                            p -> { 
                                    p.stream().forEach(op -> System.out.print(op + " "));
                                    System.out.println("," + e.get(p) + "-");
                            }
                        )        
                    );
            System.out.println();
        }
    }
    
    /**
     * 
     * @param arc_name the name of arc
     * @param arc_type transiting/inhibitor arc
     * @param from the name of starting node of arc
     * @param to the name of ending node of arc
     * @param guards ordered ArrayList of all guards existing on arc
     * @param invert_guards ordered ArrayList of all inverters that belong to guards
     * @param tuples_elements ordered ArrayList of all tuples with which ArrayList "guards" is associated 
     * @param tuples_mult ordered ArrayList of all multiplicities associated with tuples 
     */
    public void test_add_Arc(String arc_name, String arc_type, String from, String to, ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> guards,
    ArrayList<Boolean> invert_guards, ArrayList<String[]> tuples_elements, ArrayList<Integer> tuples_mult){
        System.out.println(arc_name + "," + arc_type + "," + from + "," + to + ",...");
        
        if(guards != null){
            guards.stream().forEach(
                    guard -> {
                        guard.keySet().stream().forEach(
                        e -> e.keySet().stream().forEach(
                                p -> { 
                                        p.stream().forEach(op -> System.out.print(op + " "));
                                        System.out.println(e.get(p) + "-");
                                }
                            )        
                        );
                        System.out.println();
                    }
                );
            System.out.println("cont. arc");
        }
        invert_guards.stream().forEach(e -> System.out.print(e + "-"));
        System.out.println();
        tuples_elements.stream().forEach(e -> Arrays.stream(e).forEach(e1 -> System.out.print(e1 + "-")));
        System.out.println();
        tuples_mult.stream().forEach(e -> System.out.print(e + "-"));
        System.out.println();
    }
    
    /**
     * 
     * @return single static instance
     */
     public static XML_DataTester get_instance(){
        
        if(instance == null){
            instance = new XML_DataTester();
        }
        
        return instance;
    }
}
