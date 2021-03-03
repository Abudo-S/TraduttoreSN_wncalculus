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
import wncalculus.guard.Guard;

/**
 *
 * @author dell
 */
//singleton
public class DataParser { // will use SemanticAnalyzer
    
    private static SN sn;
    //single instance
    private static DataParser instance = null;
    
    private DataParser(){
       sn = SN.get_instance();
    }
    
    public void add_ColorClass(String class_name, int start, int end, boolean circular){ //color class with lb & ub
//        System.out.println(class_name + "," + start + "," + end + "," + circular);
        sn.add_colorClass(new ColorClass(class_name, new Interval(start, end), circular));
    }
      
    public void add_ColorClass(String class_name, ArrayList<String> token_names, boolean circular){ //finite enumeration color class
//        System.out.println(class_name + "," + circular + ",...");
//        token_names.stream().forEach(e -> System.out.print(e + "-"));
//        System.out.println();
        
        sn.add_colorClass(new ColorClass(class_name, new Interval(token_names.size(), token_names.size()), circular)); //takes an interval of Arraylist size exactly 
    }
    
    //HashMap<String, ArrayList<String>> : HashMap<subclass_name, ArrayList of available tokens>
    public void add_ColorClass(String class_name, HashMap<String, ArrayList<String>> subclasses, boolean circular){ //partitioned color class
//        System.out.println(class_name + ",...");
//        subclasses.keySet().forEach(str -> {
//            subclasses.get(str).stream().forEach(e -> System.out.print(e + "-"));
//        });
//        System.out.println();
        Interval[] intervals = new Interval[subclasses.size()];
        
        int i = 0;
        for(String subclass_name : subclasses.keySet()){
            ArrayList<String> subclass_tokens = subclasses.get(subclass_name);
                    
            if(subclass_tokens.get(0).contains("lb=")){ //range
                String e_lb = subclass_tokens.get(0);
                String e_ub = subclass_tokens.get(1);
                intervals[i] = new Interval(
                               Integer.parseInt(e_lb.substring(e_lb.indexOf("=")+1)),
                               Integer.parseInt(e_ub.substring(e_ub.indexOf("=")+1))
                );
                
            }else{ //finite enumeration(useroperator tag)
                intervals[i] = new Interval(subclass_tokens.size(), subclass_tokens.size());
            }
        
            i++;
        }
        
        sn.add_colorClass(new ColorClass(class_name, intervals, circular));
    }
    
    //add Projection
    public void add_Variable(String variable_name, String variable_type){ //type = color class
//        System.out.println(variable_name + "," + variable_type);
        
        sn.add_variable(new Variable(variable_name, sn.find_colorClass(variable_type)));
    }    
    
    public void add_Domain(String domain_name, ArrayList<String> colorclasses){
//        System.out.println(domain_name + "...");
//        colorclasses.stream().forEach(e -> System.out.print(e + "-"));
//        System.out.println();
        HashMap<ColorClass, Integer> product_sort = new HashMap<>();
        
        colorclasses.stream().forEach( 
                e -> product_sort.put(sn.find_colorClass(e), 1) //GreatSpn tool doesn't allow the (1<) * color class muliplicity
        );
        
        Domain d = new Domain(product_sort);
        d.set_name(domain_name);
        sn.add_domain(d);
    }
    
    public void add_Place(String place_name, String place_type){ //type = color class or domain
//        System.out.println(place_name + "," + place_type);
        ColorClass cc = sn.find_colorClass(place_type);
        
        if(cc != null){ //place of color class type
            sn.add_place(new Place(place_name, cc));
            
        }else{ //place of domain type
            sn.add_place(new Place(place_name, sn.find_domain(place_type)));
        }
    }
    
    //uses add_Marking_colorclass()
    //uses add_Marking_domain()
    public void add_Marking(String place_name, Map tokens){ //for place of color class/domain type
//        System.out.println(place_name + "...");
//        tokens.keySet().stream().forEach(e -> System.out.print(e + "-"));
//        System.out.println();

        try{ //assume that the marking belongs to a place of color class type
            this.add_Marking_colorclass(place_name, new HashMap<>(tokens));
            
        }catch(Exception e){ // if it's not a place of color class type then it's of domain type 
            this.add_Marking_domain(place_name, new LinkedHashMap<>(tokens));
        }
    }
    
    //tokens parameter will have 1d colors with their multiplicity
    private void add_Marking_colorclass(String place_name, HashMap<String, Integer> tokens){ //for place of color class type
        Marking m0 = Marking.get_instance();
        HashMap<Token, Integer> multiplied_token = new HashMap<>();
        
        //fill multiplied_token using tokens
        Place p = sn.find_place(place_name);
        ColorClass cc = sn.find_colorClass(p.get_type());
        
        tokens.keySet().stream().forEach(
                t_name -> multiplied_token.put(new Token(t_name, cc), tokens.get(t_name))
        );
        
        m0.mark_colored_place(sn.find_place(place_name), multiplied_token);
    }
    
    //tokens parameter will have (n)d colors with their multiplicity
    private void add_Marking_domain(String place_name, LinkedHashMap<String[], Integer> tokens){ //for place of domain type of n dimension
        Marking m0 = Marking.get_instance();
        HashMap<Token[], Integer> multiplied_token = new HashMap<>();
        
        //fill multiplied_token using tokens
        Place p = sn.find_place(place_name);
        Domain d = sn.find_domain(p.get_type());
        LinkedHashMap<ColorClass, Integer> product = (LinkedHashMap<ColorClass, Integer>) d.asMap();
        
        Token[] tokens_tuple;
        int i;
        for(String[] tokens_mark : tokens.keySet()){
            tokens_tuple = new Token[tokens_mark.length];
                    
            for(ColorClass cc : product.keySet()){
                i = 0;
                tokens_tuple[i] = new Token(tokens_mark[i], cc);
                i++;
            }
            multiplied_token.put(tokens_tuple, tokens.get(tokens_mark));
        }
                
        m0.mark_domained_place(sn.find_place(place_name), multiplied_token);
    }
    
    //format: HashMap<HashMap<ArrayList, Boolean>, String> = HashMap<HashMap<(inverted)predicate with variables/operators, invert_predicate>, separator with next predicate if exists>
    //predicates describe guard and each predicate might be inverted
    //Note: last element in separators will be null 
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
    
    private Guard get_guard_from_predicates(){ //In wncalculus a guard of predicates has 2 type of guards: guard with or between predicates, guard with and between predicates
        //And.factory(Guard ... guards);
        //Or.factory(true, Guard ... guards);
        return null;
    }
    
    public static DataParser get_instance(){
        
        if(instance == null){
            instance = new DataParser();
        }
        
        return instance;
    }
}
