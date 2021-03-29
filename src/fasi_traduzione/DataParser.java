/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import albero_sintattico.*;
import struttura_sn.Place;
import struttura_sn.Variable;
import struttura_sn.Marking;
import struttura_sn.SN;
import componenti.Place_syntax_table;
import analyzer.Tuple_analyzer;
import componenti.Variable_index_table;
import test.XML_DataTester;
import java.util.*;
import wncalculus.expr.Interval;
import wncalculus.color.ColorClass;
import wncalculus.expr.Domain;
import wncalculus.wnbag.LinearComb;

/**
 *
 * @author dell
 */
//singleton
public class DataParser { // will use SemanticAnalyzer
    
    private static SN sn;
    private static Place_syntax_table pst; 
    private static Variable_index_table vit;
    private static SyntaxTree snt;
    private static Marking m0;
    //single instance
    private static DataParser instance = null;
    
    private DataParser(){
       sn = SN.get_instance();
       pst = Place_syntax_table.get_instance();
       vit = Variable_index_table.get_instance();
       snt = SyntaxTree.get_instance();
       m0 = Marking.get_instance();
    }
    
    /**
     * Note: in case of colour-class's data exists in tag "finiteintrange"
     * @param class_name the name class that will be added
     * @param start the starting number of colour class range
     * @param end the ending number of colour class range
     * @param circular true if colour class is circular/ordered, false otherwise 
     */
    public void add_ColorClass(String class_name, int start, int end, boolean circular){ //color class with lb & ub
        //XML_DataTester.get_instance().test_add_ColorClass(class_name, start, end, circular);
        sn.add_colorClass(new ColorClass(class_name, new Interval(start, end), circular));
    }
    
    /**
     * Note: in case of colour-class's data exists in tag "feconstant"
     * @param class_name the name class that will be added
     * @param token_names explicit available tokens in colour class
     * @param circular true if colour class is circular/ordered, false otherwise 
     */
    public void add_ColorClass(String class_name, ArrayList<String> token_names, boolean circular){ //finite enumeration color class
        //XML_DataTester.get_instance().test_add_ColorClass(class_name, token_names, circular);
        sn.add_colorClass(new ColorClass(class_name, new Interval(token_names.size(), token_names.size()), circular)); //takes an interval of Arraylist size exactly 
    }
    
    /**
     * Note: in case of colour-class's data exists in tag "pratition"
     * @param class_name the name class that will be added
     * @param subclasses explicit/implicit tokens of subclasses that belongs to colour classes
     * @param circular true if colour class is circular/ordered, false otherwise 
     */
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
    
    /**
     * 
     * @param variable_name the name of variable
     * @param variable_type the type of variable (colour class) name
     */
    public void add_Variable(String variable_name, String variable_type){ //type = color class
        //XML_DataTester.get_instance().test_add_Variable(variable_name, variable_type);
        sn.add_variable(new Variable(variable_name, sn.find_colorClass(variable_type)));
        vit.add_cc_var_name(variable_type, variable_name);
    }    
    
    /**
     * 
     * @param domain_name the name of domain 
     * @param colorclasses ordered colour classes multiplication that belongs to domain
     */
    public void add_Domain(String domain_name, ArrayList<String> colorclasses){
        //XML_DataTester.get_instance().test_add_Domain(domain_name, colorclasses);
        HashMap<ColorClass, Integer> product_sort = new HashMap<>();

//GreatSpn tool doesn't allow the (1<n) * color class muliplicity        
        colorclasses.stream().forEach( 
                e -> {
                    ColorClass cc = sn.find_colorClass(e);

                    if(product_sort.containsKey(cc)){
                        int mult = product_sort.get(cc);
                        mult += 1;
                        product_sort.put(cc, mult);
                    }else{
                        product_sort.put(cc, 1);
                    }
                }
        );
        pst.add_type_value(domain_name, colorclasses);
        
        Domain d = new Domain(product_sort);
        d.set_name(domain_name);
        sn.add_domain(d);
    }
    
    /**
     * 
     * @param place_name the name of place
     * @param place_type the type of place (colour class/domain) name
     */
    public void add_Place(String place_name, String place_type){ //type = color class or domain
        //XML_DataTester.get_instance().test_add_Place(place_name, place_type);
        ColorClass cc = sn.find_colorClass(place_type);
        Place p;
        
        if(cc != null){ //place of color class type
            p = new Place(place_name, cc);
            //add to Place_syntax_table
            pst.add_place_type(place_name, cc.name());
            pst.add_type_value(cc.name(), new ArrayList<>(List.of(cc.name())));
        }else{ //place of domain type
            p = new Place(place_name, sn.find_domain(place_type));
            //add to Place_syntax_table
            pst.add_place_type(place_name, place_type);
        }
        snt.add_synt_place(new Syntactic_place(place_name));
        //analyze place domain
        p.set_node_domain(SemanticAnalyzer.get_instance().analyze_place_domain(p));
        sn.add_place(p);
    }
    
    /**
     * 
     * @param place_name the name of place
     * @param tokens Map of tokens that belong to place initial-marking
     */
    //uses add_Marking_colorclass()
    //uses add_Marking_domain()
    //may contain marking of type "All"
    public void add_Marking(String place_name, Map tokens){ //for place of color class/domain type
        Tuple_analyzer ta = Tuple_analyzer.get_instance();
        
        try{ //assume that the marking belongs to a place of color class type
            this.add_Marking_colorclass(place_name, new HashMap<String, Integer>(tokens), ta);
        }catch(Exception e){ // if it's not a place of color class type then it's of domain type 
            this.add_Marking_domain(place_name, new HashMap<String[], Integer>(tokens), ta);
        }
    }
    
    /**
     * 
     * @param place_name the name of place
     * @param tokens HashMap of tokens that belong to place initial-marking (case of colour class)
     * @param ta the tuple analyser used
     * Note: each marking tuple should contain 1 linear combination
     */
    //tokens parameter will have 1d colors with their multiplicity
    private void add_Marking_colorclass(String place_name, HashMap<String, Integer> tokens, Tuple_analyzer ta){ //for place of color class type
        //XML_DataTester.get_instance().test_add_Marking_colorclass(place_name, tokens);
        HashMap<LinearComb, Integer> multiplied_token = new HashMap<>();
        
        tokens.keySet().stream().forEach(
                t_name -> multiplied_token.put(ta.analyze_marking_tuple_element(t_name, place_name, 0), tokens.get(t_name))
        );
        
        m0.mark_colored_place(sn.find_place(place_name), multiplied_token);
    }
    
    /**
     * 
     * @param place_name the name of place
     * @param tokens HashMap of tokens that belong to place initial-marking (case of domain)
     * @param ta the tuple analyser used
     * Note: each marking tuple should contain more than 1 linear combination that follows the ordering of domain elements
     */
    private void add_Marking_domain(String place_name, HashMap<String[], Integer> tokens, Tuple_analyzer ta){ //for place of domain type of n dimension
        //XML_DataTester.get_instance().test_add_Marking_domain(place_name, tokens);
        HashMap<ArrayList<LinearComb>, Integer> multiplied_token = new HashMap<>();
        
        tokens.keySet().stream().forEach(
                marking_tuple_element -> {
                    ArrayList<LinearComb> comb_elements = new ArrayList<>();

                    for(var i = 0; i < marking_tuple_element.length; i++){
                        comb_elements.add(ta.analyze_marking_tuple_element(marking_tuple_element[i], place_name, i));
                    }
                    multiplied_token.put(comb_elements, tokens.get(marking_tuple_element));
                }
        );
        
        m0.mark_domained_place(sn.find_place(place_name), multiplied_token);
    }
    
//    //tokens parameter will have 1d colors with their multiplicity
//    private void add_Marking_colorclass(String place_name, HashMap<String, Integer> tokens){ //for place of color class type
//        //XML_DataTester.get_instance().test_add_Marking_colorclass(place_name, tokens);
//        Marking m0 = Marking.get_instance();
//        HashMap<Token, Integer> multiplied_token = new HashMap<>(); //may contain "All"
//        //fill multiplied_token using tokens
//        Place p = sn.find_place(place_name);
//        ColorClass cc = sn.find_colorClass(p.get_type());
//        
//        tokens.keySet().stream().forEach(
//                t_name -> multiplied_token.put(new Token(t_name, cc), tokens.get(t_name))
//        );
//        
//        m0.mark_colored_place(sn.find_place(place_name), multiplied_token);
//    }
//    
//    //tokens parameter will have (n)d colors with their multiplicity
//    private void add_Marking_domain(String place_name, LinkedHashMap<String[], Integer> tokens){ //for place of domain type of n dimension
//        //XML_DataTester.get_instance().test_add_Marking_domain(place_name, tokens);
//        Marking m0 = Marking.get_instance();
//        HashMap<Token[], Integer> multiplied_token = new HashMap<>(); //may contain array of "All"
//        
//        //fill multiplied_token using tokens
//        Place p = sn.find_place(place_name);
//        Domain d = sn.find_domain(p.get_type());
//        Map<ColorClass, Integer> product = (Map<ColorClass, Integer>) d.asMap();
//        
//        Token[] tokens_tuple = new Token[d.asMap().keySet().size()];
////        int i;
////        for(String[] tokens_mark : tokens.keySet()){
////            tokens_tuple;
////            i = 0;
////            
////            for(ColorClass cc : product.keySet()){    
////                tokens_tuple[i] = new Token(tokens_mark[i], cc);
////                i++;
////            }
////            multiplied_token.put(tokens_tuple, tokens.get(tokens_mark));
////        }
////same solution of the following stream method:
//        
//        ArrayList<Token> tokens_t = new ArrayList<>();
//        
//        tokens.keySet().stream().forEach(
//                token_arr -> { 
//                               Iterator<ColorClass> cc_it = product.keySet().iterator();
//                               
//                               Arrays.stream(token_arr).forEach(
//                                       token_str -> tokens_t.add(new Token(token_str, cc_it.next()))
//                               );
//                               multiplied_token.put(tokens_t.toArray(tokens_tuple), tokens.get(token_arr));
//                             }
//        );
//                
//        m0.mark_domained_place(sn.find_place(place_name), multiplied_token);
//    }
    
    /**
     * 
     * @param Transition_name the name of transition that contains guard
     * @param guard the guard of transition, LinkedHashMap of ordered predicates associated with their separators with next predicate (if exists)
     * @param invert_guard true if predicate is inverted, false otherwise
     */
    //format: LinkedHashMap<HashMap<ArrayList, Boolean>, String> = LinkedHashMap<HashMap<predicate with projections/constants, invert_predicate>, separator with next predicate if exists>
    //predicates describe guard and each predicate might be inverted
    //Note: last element in separators will be null 
    public void add_Transition(String Transition_name, LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String> guard, boolean invert_guard){
        //XML_DataTester.get_instance().test_add_Transition(Transition_name, guard, invert_guard);        
        //sn.add_transition(new Transition(Transition_name, sa.analyze_guard_of_predicates(guard, invert_guard, Transition_name)));
        Syntactic_transition st = new Syntactic_transition(Transition_name);
        
        if(guard != null){
            st.set_syntactic_guard(this.get_synt_guard(guard, invert_guard));
        }
        //add to syntax tree
        snt.add_synt_transition(st);
    }
    
    /**
     * 
     * @param Arc_name the name of arc
     * @param arc_type transiting/inhibitor arc
     * @param from the name of starting node of arc
     * @param to the name of ending node of arc
     * @param guards ordered ArrayList of all guards existing on arc
     * @param invert_guards ordered ArrayList of all inverters that belong to guards
     * @param tuples_elements ordered ArrayList of all tuples with which ArrayList "guards" is associated 
     * @param tuples_mult ordered ArrayList of all multiplicities associated with tuples 
     */
    //an Arc can have array of guards related with tuples
    //TupleBag contains WNtuples, each of them has a multiplicity and a guard
    public void add_Arc(String Arc_name, String arc_type, String from, String to, ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> guards,
    ArrayList<Boolean> invert_guards, ArrayList<String[]> tuples_elements, ArrayList<Integer> tuples_mult){ //type = "tarc/inhibitor"
        
        //XML_DataTester.get_instance().test_add_Arc(Arc_name, arc_type, from, to, guards, invert_guards, tuples_elements, tuples_mult);
        Syntactic_place synt_p = snt.find_synt_place(from);
        Syntactic_transition synt_t;        
        Syntactic_arc synt_arc = new Syntactic_arc(Arc_name);
        
        for(var i = 0; i < tuples_elements.size(); i++){
            Syntactic_tuple syntc_tuple = new Syntactic_tuple(tuples_elements.get(i));
            syntc_tuple.set_syntactic_guard(this.get_synt_guard(guards.get(i), invert_guards.get(i)));
            synt_arc.add_multiplied_tuple(syntc_tuple, tuples_mult.get(i));
        } 
                
        if(arc_type.equals("inhibitor")){
            synt_arc.set_type(true);
            synt_t = snt.find_synt_transition(to);
            synt_p.add_next(synt_t, synt_arc);
        }else{
            synt_arc.set_type(false);
            
            if(synt_p == null){ //t->p
                synt_t = snt.find_synt_transition(from);
                synt_p = snt.find_synt_place(to);
                synt_t.add_next(synt_p, synt_arc);   
                
            }else{ //p->t
                synt_t = snt.find_synt_transition(to);
                synt_p.add_next(synt_t, synt_arc);
            }
        }
        //update syntactic place/transition list
        snt.update_synt_p_t(synt_p, synt_t);
    }
    
    /**
     * 
     * @param guard LinkedHashMap of ordered predicates associated with their separators with next predicate (if exists)
     * @param invert_guard true if syntactic-guard should be inverted, false otherwise
     * @return the syntactic guard generated
     */
    private Syntactic_guard get_synt_guard(LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String> guard, boolean invert_guard){
        Syntactic_guard synt_guard = null;
        
        if(guard != null || !guard.isEmpty()){
            LinkedHashMap<Syntactic_predicate, String> separated_predicates = new LinkedHashMap<>();
            
            guard.keySet().stream().forEach(
                    syntactic_p -> {
                        ArrayList<String> pe = syntactic_p.keySet().stream().findFirst().orElse(null);
                        Syntactic_predicate sp = new Syntactic_predicate(syntactic_p.get(pe), pe);
                        separated_predicates.put(sp, guard.get(syntactic_p));
                    }    
            );
            
           synt_guard = new Syntactic_guard(invert_guard, separated_predicates);
        }
        return synt_guard;
    }
     
//    private Map<WNtuple, Integer> fill_TupleBag_map(ArrayList<LinkedHashMap<HashMap<ArrayList<String>, Boolean>, String>> guards,
//    ArrayList<Boolean> invert_guards, ArrayList<String[]> tuples_elements, ArrayList<Integer> tuples_mult, String transition_name){
//        //create (multiplied)TupleBag object of WNtuples 
//        Map<WNtuple, Integer> multiplied_tuples = new HashMap<>();
//        
//        for(var i = 0; i < tuples_elements.size(); i++){
//            Guard g = sa.analyze_guard_of_predicates(guards.get(i), invert_guards.get(i), transition_name);
//            WNtuple tuple = sa.analyze_arc_tuple(g, tuples_elements.get(i));
//            multiplied_tuples.put(tuple, tuples_mult.get(i));
//        }
//        
//        return multiplied_tuples;
//    }
    
    /**
     * 
     * @return filled syntax tree
     */
    //will be called after finishing all file data scanning    
    public static SyntaxTree get_syntax_tree(){
        return snt;
    }
    
    /**
     * 
     * @return single static instance
     */
    public static DataParser get_instance(){
        
        if(instance == null){
            instance = new DataParser();
        }
        
        return instance;
    }
}
