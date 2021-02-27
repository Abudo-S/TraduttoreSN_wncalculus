/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni_xml;

import java.util.*;
import struttura_sn.*;
import wncalculus.expr.Interval;
import wncalculus.color.ColorClass;
import wncalculus.expr.Domain;
import wncalculus.classfunction.Projection;
import wncalculus.expr.Sort;

/**
 *
 * @author dell
 */
//singleton
public class DataParser {
    
    private static DataParser instance = null;
    private static SN sn;
    
    private DataParser(){
       sn = SN.get_instance();
    }
    
    public void add_ColorClass(String class_name, int start, int end, boolean circular){
//        System.out.println(class_name + "," + start + "," + end + "," + circular);
        sn.add_colorClass(new ColorClass(class_name, new Interval(start, end), circular));
    }
      
    public void add_ColorClass(String class_name, ArrayList<String> token_names, boolean circular){
//        System.out.println(class_name + "," + circular + ",...");
//        token_names.stream().forEach(e -> System.out.print(e + "-"));
//        System.out.println();

        //to be completed
    }
    
    public void add_ColorClass(String class_name, HashMap<String, ArrayList<String>> subclasses){
//        System.out.println(class_name + ",...");
//        subclasses.keySet().forEach(str -> {
//            subclasses.get(str).stream().forEach(e -> System.out.print(e + "-"));
//        });
//        System.out.println();
        
        //to be completed
    }
    
    //add Projection
    public void add_Variable(String variable_name, String variable_type){ //type = color class
//        System.out.println(variable_name + "," + variable_type);
        
        //to be completed
    }    
    
    public void add_Domain(String domain_name, ArrayList<String> colorclasses){
//        System.out.println(domain_name + "...");
//        colorclasses.stream().forEach(e -> System.out.print(e + "-"));
//        System.out.println();
        HashMap<ColorClass, Integer> product_sort = new HashMap<>();
        colorclasses.stream().forEach( 
                e -> product_sort.put(sn.find_colorClass(e), 1) //GreatSpn tool doesn't allow the 1+ color class muliplicity
        );
        
        Domain d = new Domain(product_sort);
        d.set_name(domain_name);
        sn.add_domain(d);
    }
    
    public void add_Place(String place_name, String place_type){ //type = color class or domain
//        System.out.println(place_name + "," + place_type);

    }
    
    public void add_Marking(String place_name, Map tokens){ //for place of color class/domain type
        //uses add_Marking_colorclass()
        //uses add_Marking_domain()
//        System.out.println(place_name + "...");
//        tokens.keySet().stream().forEach(e -> System.out.print(e + "-"));
//        System.out.println();
        
        //to be completed
    }
    
    //tokens parameter will have 1d colors with their multiplicity
    private void add_Marking_colorclass(String place_name, HashMap<Integer, String> tokens){ //for place of color class type
        //to be completed
    }
    
    //tokens parameter will have (n)d colors with their multiplicity
    private void add_Marking_domain(String place_name, HashMap<Integer, String[]> tokens){ //for place of domain type of n dimension
        //to be completed
    }
    
    //format: HashMap<HashMap<ArrayList, Boolean>, String> = HashMap<HashMap<(inverted)predicate with variables/operators, invert_predicate>, separator with next predicate if exists>
    //predicates describe guard and each predicate might be inverted
    //Note: last element in seperators will be null 
    public void add_Transition(String Transition_name, LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>  guard, boolean invert_guard){

//        System.out.println(Transition_name + "," + invert_guard + ",...");
//        if(guard != null){
//            guard.keySet().stream().forEach(e -> System.out.print(guard.get(e) + "-"));
//            System.out.println();
//        }
        
        //to be completed
    }
    
    //an Arc can have array of guards related with tuples
    public void add_Arc(String Arc_name, String arc_type, String from, String to, ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> guards,
    ArrayList<Boolean> invert_guards, ArrayList<String[]> tuples_elements, ArrayList<Integer> tuples_mult){ //type = "tarc/inhibitor"
        
//        System.out.println(Arc_name + "," + arc_type + "," + from + "," + to + ",...");
//        if(guards != null){
//            guards.stream().forEach(e -> e.keySet().stream().forEach(e1 -> System.out.print(e.get(e1) + "-")));
//            System.out.println("cont. arc");
//        }
//        invert_guards.stream().forEach(e -> System.out.print(e + "-"));
//        System.out.println();
//        tuples_elements.stream().forEach(e -> Arrays.stream(e).forEach(e1 -> System.out.print(e1 + "-")));
//        System.out.println();
//        tuples_mult.stream().forEach(e -> System.out.print(e + "-"));
//        System.out.println();
        
        //to be completed
    }
    
    public SN get_sn(){
        return sn;
    }
    
    public static DataParser get_instance(){
        
        if(instance == null){
            instance = new DataParser();
        }
        
        return instance;
    }
}
