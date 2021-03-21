/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

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
    
    public void test_add_ColorClass(String class_name, int start, int end, boolean circular){
        System.out.println(class_name + ":" + start + "," + end + "," + circular);
    }
    
    public void test_add_ColorClass(String class_name, ArrayList<String> token_names, boolean circular){
        System.out.println(class_name + "," + circular + ",...");
        token_names.stream().forEach(e -> System.out.print(e + "-"));
        System.out.println();
    }
    
    public void test_add_ColorClass(String class_name, HashMap<String, ArrayList<String>> subclasses, boolean circular){
        System.out.println(class_name + ",...");
        subclasses.keySet().forEach(str -> {
            subclasses.get(str).stream().forEach(e -> System.out.print(e + "-"));
        });
        System.out.println();
    }
    
    public void test_add_Variable(String variable_name, String variable_type){
        System.out.println(variable_name + "," + variable_type);
    }
    
    public void test_add_Domain(String domain_name, ArrayList<String> colorclasses){
        System.out.println(domain_name + "...");
        colorclasses.stream().forEach(e -> System.out.print(e + "-"));
        System.out.println();
    }
    
    public void test_add_Place(String place_name, String place_type){
        System.out.println(place_name + "," + place_type);
    }
    
    public void test_add_Marking_colorclass(String place_name, HashMap<String, Integer> tokens){
        System.out.println(place_name + "...");
        tokens.keySet().stream().forEach(e -> System.out.print(tokens.get(e) + "*" + e + "-"));
        System.out.println();
    }
    
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
    
    public void test_add_Transition(String Transition_name, LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>  guard, boolean invert_guard){
        System.out.println(Transition_name + "," + invert_guard + ",...");
        
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
    
    public void test_add_Arc(String Arc_name, String arc_type, String from, String to, ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> guards,
    ArrayList<Boolean> invert_guards, ArrayList<String[]> tuples_elements, ArrayList<Integer> tuples_mult){
        System.out.println(Arc_name + "," + arc_type + "," + from + "," + to + ",...");
        
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
    
     public static XML_DataTester get_instance(){
        
        if(instance == null){
            instance = new XML_DataTester();
        }
        
        return instance;
    }
}
