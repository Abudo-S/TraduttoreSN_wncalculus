/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni_xml;

import Test.XML_DataTester;
import java.util.*;
import struttura_sn.*;
import wncalculus.expr.Interval;
import wncalculus.color.ColorClass;
import wncalculus.expr.Domain;
import wncalculus.wnbag.TupleBag;
import wncalculus.wnbag.WNtuple;
import wncalculus.guard.Guard;

/**
 *
 * @author dell
 */
//singleton
public class DataParser { // will use SemanticAnalyzer
    
    private static SN sn;
    private static SemanticAnalyzer sa;
    //single instance
    private static DataParser instance = null;
    
    private DataParser(){
       sn = SN.get_instance();
       sa = SemanticAnalyzer.get_instance();
    }
    
    public void add_ColorClass(String class_name, int start, int end, boolean circular){ //color class with lb & ub
        //XML_DataTester.get_instance().test_add_ColorClass(class_name, start, end, circular);
        sn.add_colorClass(new ColorClass(class_name, new Interval(start, end), circular));
    }
      
    public void add_ColorClass(String class_name, ArrayList<String> token_names, boolean circular){ //finite enumeration color class
        //XML_DataTester.get_instance().test_add_ColorClass(class_name, token_names, circular);
        sn.add_colorClass(new ColorClass(class_name, new Interval(token_names.size(), token_names.size()), circular)); //takes an interval of Arraylist size exactly 
    }
    
    //HashMap<String, ArrayList<String>> : HashMap<subclass_name, ArrayList of available tokens>
    public void add_ColorClass(String class_name, HashMap<String, ArrayList<String>> subclasses, boolean circular){ //partitioned color class
        //XML_DataTester.get_instance().test_add_ColorClass(class_name, subclasses, circular);
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
            
            intervals[i].set_name(subclass_name);
            i++;
        }
        
        sn.add_colorClass(new ColorClass(class_name, intervals, circular));
    }
    
    public void add_Variable(String variable_name, String variable_type){ //type = color class
        //XML_DataTester.get_instance().test_add_Variable(variable_name, variable_type);
        sn.add_variable(new Variable(variable_name, sn.find_colorClass(variable_type)));
    }    
    
    public void add_Domain(String domain_name, ArrayList<String> colorclasses){
        //XML_DataTester.get_instance().test_add_Domain(domain_name, colorclasses);
        HashMap<ColorClass, Integer> product_sort = new HashMap<>();
        
        colorclasses.stream().forEach( 
                e -> product_sort.put(sn.find_colorClass(e), 1) //GreatSpn tool doesn't allow the (1<) * color class muliplicity
        );
        
        Domain d = new Domain(product_sort);
        d.set_name(domain_name);
        sn.add_domain(d);
    }
    
    public void add_Place(String place_name, String place_type){ //type = color class or domain
        //XML_DataTester.get_instance().test_add_Place(place_name, place_type);
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
        try{ //assume that the marking belongs to a place of color class type
            this.add_Marking_colorclass(place_name, new HashMap<String, Integer>(tokens));
        }catch(Exception e){ // if it's not a place of color class type then it's of domain type 
            this.add_Marking_domain(place_name, new LinkedHashMap<String[], Integer>(tokens));
        }
    }
    
    //tokens parameter will have 1d colors with their multiplicity
    private void add_Marking_colorclass(String place_name, HashMap<String, Integer> tokens){ //for place of color class type
        //XML_DataTester.get_instance().test_add_Marking_colorclass(place_name, tokens);
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
        //XML_DataTester.get_instance().test_add_Marking_domain(place_name, tokens);
        Marking m0 = Marking.get_instance();
        HashMap<Token[], Integer> multiplied_token = new HashMap<>();
        
        //fill multiplied_token using tokens
        Place p = sn.find_place(place_name);
        Domain d = sn.find_domain(p.get_type());
        Map<ColorClass, Integer> product = (Map<ColorClass, Integer>) d.asMap();
        
        Token[] tokens_tuple = new Token[d.asMap().keySet().size()];
//        int i;
//        for(String[] tokens_mark : tokens.keySet()){
//            tokens_tuple;
//            i = 0;
//            
//            for(ColorClass cc : product.keySet()){    
//                tokens_tuple[i] = new Token(tokens_mark[i], cc);
//                i++;
//            }
//            multiplied_token.put(tokens_tuple, tokens.get(tokens_mark));
//        }
//same solution of the following stream method:
        
        ArrayList<Token> tokens_t = new ArrayList<>();
        
        tokens.keySet().stream().forEach(
                token_arr -> { 
                               Iterator<ColorClass> cc_it = product.keySet().iterator();
                               
                               Arrays.stream(token_arr).forEach(
                                       token_str -> tokens_t.add(new Token(token_str, cc_it.next()))
                               );
                               multiplied_token.put(tokens_t.toArray(tokens_tuple), tokens.get(token_arr));
                             }
        );
                
        m0.mark_domained_place(sn.find_place(place_name), multiplied_token);
    }
    
    //format: LinkedHashMap<HashMap<ArrayList, Boolean>, String> = LinkedHashMap<HashMap<predicate with projections/constants, invert_predicate>, separator with next predicate if exists>
    //predicates describe guard and each predicate might be inverted
    //Note: last element in separators will be null 
    public void add_Transition(String Transition_name, LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String> guard, boolean invert_guard){
        //XML_DataTester.get_instance().test_add_Transition(Transition_name, guard, invert_guard);        
        sn.add_transition(new Transition(Transition_name, sa.analyze_guard_of_predicates(guard, invert_guard, Transition_name)));
    }
    
    //an Arc can have array of guards related with tuples
    //TupleBag contains WNtuples, each of them has a multiplicity and a guard
    public void add_Arc(String Arc_name, String arc_type, String from, String to, ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> guards,
    ArrayList<Boolean> invert_guards, ArrayList<String[]> tuples_elements, ArrayList<Integer> tuples_mult){ //type = "tarc/inhibitor"
        
        XML_DataTester.get_instance().test_add_Arc(Arc_name, arc_type, from, to, guards, invert_guards, tuples_elements, tuples_mult);
        
        Place p = sn.find_place(from); //assume that the starting node is a place
        Transition t;
        Arc arc;
        
        if(arc_type.equals("inhibitor")){ //inhibitor arc starts from a place definitely
            t = sn.find_transition(to);
            arc = new Arc(Arc_name, new TupleBag(this.fill_TupleBag_map(guards, invert_guards, tuples_elements, tuples_mult, t.get_name())));
            t.add_inib(arc, t);
            p.add_inib(arc, p);
            
        }else{

            if(p == null){ //t -> p
                t = sn.find_transition(from);
                p = sn.find_place(to);
                arc = new Arc(Arc_name, new TupleBag(this.fill_TupleBag_map(guards, invert_guards, tuples_elements, tuples_mult, t.get_name())));
                p = (Place) t.add_next_Node(arc, p);

            }else{ //p -> t
                t = sn.find_transition(to);
                arc = new Arc(Arc_name, new TupleBag(this.fill_TupleBag_map(guards, invert_guards, tuples_elements, tuples_mult, t.get_name())));
                t = (Transition) p.add_next_Node(arc, t);
            }
        }
        
        if(p.get_node_domain() == null){
           p.set_node_domain(sa.analyze_place_domain(p));
        }
        //analyze transition domain ....
        
        //update node domain when a new arc is connected with it
        t.set_node_domain(sa.analyze_transition_domain(t));
        
        //exchange sn nodes with connected nodes
        sn.update_nodes_via_arc(p, t);
    }
    
    private Map<WNtuple, Integer> fill_TupleBag_map(ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> guards,
    ArrayList<Boolean> invert_guards, ArrayList<String[]> tuples_elements, ArrayList<Integer> tuples_mult, String transition_name){
        //create (multiplied)TupleBag object of WNtuples 
        Map<WNtuple, Integer> multiplied_tuples = new HashMap<>();
        
        for(var i = 0; i < tuples_elements.size(); i++){
            Guard g = sa.analyze_guard_of_predicates(guards.get(i), invert_guards.get(i), transition_name);
            WNtuple tuple = sa.analyze_arc_tuple(g, tuples_elements.get(i));
            multiplied_tuples.put(tuple, tuples_mult.get(i));
        }
        
        return multiplied_tuples;
    }
    
    public static DataParser get_instance(){
        
        if(instance == null){
            instance = new DataParser();
        }
        
        return instance;
    }
}
