/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import albero_sintattico.Syntactic_guard;
import albero_sintattico.Syntactic_predicate;
import albero_sintattico.SyntaxTree;
import componenti.Variable_index_table;
import eccezioni.UnsupportedElementNameException;
import java.util.*;
import javax.xml.transform.TransformerException;
import struttura_sn.ArcAnnotation;
import struttura_sn.Marking;
import wncalculus.color.ColorClass;
import struttura_sn.*;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.classfunction.Projection;
import wncalculus.classfunction.Subcl;
import wncalculus.expr.Domain;
import wncalculus.expr.Interval;
import wncalculus.expr.Sort;
import wncalculus.wnbag.LinearComb;
import wncalculus.wnbag.TupleBag;
import wncalculus.wnbag.WNtuple;

/**
 *
 * @author dell
 */
//singleton
//will be a part of factory pattern with (struttura_unfolding_parziale)/XMLWriter
public class PartialGenerator {
    //usable maps to prevent redundant calculations/combinations of colour classes of a colour domain cd
    private HashMap<String, HashMap<String, String>> cd_all_places_filters; //contains pre-calculated cd's unfolded places names and their corresponding filters
    private HashMap<String, HashMap<String, ArrayList<String>>> cd_possible_combs; //es. colour-domain name ->{colour-class name -> 11, 12, ...}
    private HashMap<String, HashMap<Integer, ArrayList<String>>> cc_base_filters; //base filters of a colour class that follows ei multiplicity
    private HashMap<String, HashMap<Integer, ArrayList<String>>> subcc_predicates; //predicates of each subcc for 1 <= n <= ei, Note: each filter's predicates are in the same list
    private static XMLWriter xmlwriter;
    private static SN sn;
    //single instance
    private static PartialGenerator instance = null;
    
    private PartialGenerator() throws Exception{
        this.cd_possible_combs = new HashMap<>();
        this.cc_base_filters = new HashMap<>();
        this.subcc_predicates = new HashMap<>();
        this.cd_all_places_filters = new HashMap<>();
        xmlwriter = XMLWriter.get_instance("CPN");
        sn = SN.get_instance();
    }
    
    public void unfold_all_places() throws TransformerException{        
        Marking mk = Marking.get_instance();
        int[] place_xy = new int[]{40, 20}; //graphics points
        
        sn.get_P().stream().forEach(
                place -> {
                    
                    String prefix_name = place.get_name();
                    //unfold place
                    HashMap<String, String> all_places_combs_filter = this.unfold_place(place);
                    //write unfolded places
                    all_places_combs_filter.keySet().stream().forEach(
                            p_name -> {
                                //write place
                                ArrayList<String> place_data = new ArrayList<>();
                                place_data.add("id@=" + prefix_name + p_name);
                                place_data.add("type@=" + place.get_type());
                                
                                HashMap<ArrayList<LinearComb>, Integer> initial_marking = mk.get_marking_of_place(p_name);
                                //copy intitial marking of place in all unfolded places
                                if(!initial_marking.isEmpty()){
                                    place_data.add("hlinitialMarking@=" + this.get_place_initial_marking(initial_marking));
                                }
                                place_xy[1] += 20;
                                place_data.add("graphics@=" + "x=" + place_xy[0] + "y=" + place_xy[1]);
                                //add place to be written
                                xmlwriter.add_place(place_data);
                                //write place arcs with unfolded filter
                                this.write_unfolded_place_arcs(place, p_name, all_places_combs_filter.get(p_name));
                            }
                    );
                }
        );
        this.write_transitions();
//       HashMap<String, String> all_places_combs_filter = this.unfold_place(sn.get_P().get(1));
//
//       all_places_combs_filter.keySet().stream().forEach(
//                p -> {
//                    System.out.println(p + "," + all_places_combs_filter.get(p));
//                }
//        );
        xmlwriter.write_all_data();
    }
    
    /**
     * 
     * @param initial_marking HashMap of token-tuple's elements (ArrayList of linear combinartions) with their multiplicity
     * @return the equivalent String of initial marking map
     */
    private String get_place_initial_marking(HashMap<ArrayList<LinearComb>, Integer> initial_marking){
        String[] str_marking = new String[]{""};//wrapper pf marking
        Iterator it1 = initial_marking.keySet().iterator();
        
        while(it1.hasNext()){
            ArrayList<LinearComb> tuple_elements_list = (ArrayList<LinearComb>) it1.next();
            int tuple_mult = initial_marking.get(tuple_elements_list);

            if(tuple_mult != 1){
                str_marking[0] += tuple_mult;
            }
            str_marking[0] += "<";
            
            Iterator it2 = tuple_elements_list.iterator();
            while(it2.hasNext()){
                LinearComb tuple_element = (LinearComb) it2.next();
                Map<ElementaryFunction, Integer> linearcomb_map = (Map<ElementaryFunction, Integer>) tuple_element.asMap();
                ArrayList<ElementaryFunction> keys_list = new ArrayList<>(linearcomb_map.keySet()); //to use ArrayList's index

                for(var j = 0; j < keys_list.size(); j++){      
                    ElementaryFunction ef = keys_list.get(j);
                    int lc_element_mult = linearcomb_map.get(ef);

                    if(j != 0 && lc_element_mult > 0){ //don't add "+" mark before the first element of linear combination
                        str_marking[0] += "+";
                    }

                    if(lc_element_mult!= 1 && lc_element_mult != -1){
                        str_marking[0] += lc_element_mult;
                    }else if(lc_element_mult == -1){
                        str_marking[0] += "-";
                    }

                    //cast ef to Subcl | All | Token
                    if(ef instanceof Token){
                        Token t = (Token) ef;
                        str_marking[0] += t.get_Token_value() + " ";
                    }else if(ef instanceof Subcl){
                        Subcl con = (Subcl) ef;
                        str_marking[0] += con.getSort().getConstraint(con.index()).name() + " ";
                    }else{ //All
                        str_marking[0] += "All ";
                    }                                
                }
                
                //check if there's a tuple element
                if(it2.hasNext()){
                    str_marking[0] += ", ";
                }
            }
            
            str_marking[0] += "> ";
            
            //check if there's a next tuple
            if(it1.hasNext()){
                str_marking[0] += " + ";
            }
        }            
        
        return str_marking[0];
    }
    
    /**
     * 
     * @param p place that we want to write its arcs expanded with the filter resulted from place unfolding
     * @param place_name_suffix place combination
     * @param expanded_filter the generated filter of a combination
     * Note: the expanded filter will be added to each tuple written on arc
     */
    private void write_unfolded_place_arcs(Place p, String place_name_suffix, String expanded_filter){
        HashMap<Node, ArcAnnotation> next_nodes = p.get_next_nodes();
        HashMap<Node, ArcAnnotation> previous_nodes = p.get_previous_nodes();
        HashMap<Node, ArcAnnotation> inhibitored_nodes = p.get_inib_nodes();
        
        next_nodes.keySet().stream().forEach(
            next_node -> {
                //write arc
                ArrayList<String> arc_data = new ArrayList<>();
                String from = p.get_name() + place_name_suffix;
                String to = next_node.get_name(); //transition
                arc_data.add("id@=" + from + "_" + to);
                arc_data.add("source@=" + from);
                arc_data.add("target@=" + to);
                //add hlinscription
                arc_data.add("hlinscription@=" + this.get_tuples_expression(next_nodes.get(next_node).get_tuple_bag(), expanded_filter, to));
                //add arc to be written
                xmlwriter.add_arc(arc_data);
            }
        );
        
        previous_nodes.keySet().stream().forEach(
            previous_node -> {
                //write arc
                ArrayList<String> arc_data = new ArrayList<>();
                String from = previous_node.get_name(); //transition
                String to = p.get_name() + place_name_suffix;
                arc_data.add("id@=" + from + "_" + to);
                arc_data.add("source@=" + from);
                arc_data.add("target@=" + to);
                //add hlinscription
                arc_data.add("hlinscription@=" + this.get_tuples_expression(previous_nodes.get(previous_node).get_tuple_bag(), expanded_filter, from));    
                //add arc to be written
                xmlwriter.add_arc(arc_data);
            }
        );
        
        inhibitored_nodes.keySet().stream().forEach(
            inhibitored_node -> {
                //write arc
                ArrayList<String> arc_data = new ArrayList<>();
                String from = p.get_name() + place_name_suffix;
                String to = inhibitored_node.get_name(); //transition
                arc_data.add("id@=" + from + "_" + to);
                arc_data.add("source@=" + from);
                arc_data.add("target@=" + to);
                //add hlinscription
                arc_data.add("hlinscription@=" + this.get_tuples_expression(inhibitored_nodes.get(inhibitored_node).get_tuple_bag(), expanded_filter, to));
                arc_data.add("type@=inhibitor");
                //add arc to be written
                xmlwriter.add_arc(arc_data);
            }
        );        
        
    }
    
    /**
     * 
     * @param tb the tuple bag (Map of multiplied tuples) that we want to extract in a string
     * @param expanded_filter the unfolded generated filter that will be added to each tuple in tb
     * @return the resulting string of all filtered tuples combinations
     * @throws NullPointerException if a variable hasn't extracted from a projection
     */
    private String get_tuples_expression(TupleBag tb, String expanded_filter, String transition_name) throws NullPointerException{
        String[] str_marking = new String[]{""};//wrapper pf marking
        Map<WNtuple, Integer> multiplied_tuple = (Map<WNtuple, Integer>) tb.asMap();
        
        Iterator it1 = multiplied_tuple.keySet().iterator();
        
        while(it1.hasNext()){
            WNtuple tuple = (WNtuple) it1.next();
            List<LinearComb> tuple_elements_list = (List<LinearComb>) tuple.getComponents();
            int tuple_mult = multiplied_tuple.get(tuple);

            if(tuple_mult != 1){
                str_marking[0] += tuple_mult;
            }
            str_marking[0] += "<";
            
            Iterator it2 = tuple_elements_list.iterator();
            while(it2.hasNext()){
                LinearComb tuple_element = (LinearComb) it2.next();
                Map<ElementaryFunction, Integer> linearcomb_map = (Map<ElementaryFunction, Integer>) tuple_element.asMap();
                ArrayList<ElementaryFunction> keys_list = new ArrayList<>(linearcomb_map.keySet()); //to use ArrayList's index

                for(var j = 0; j < keys_list.size(); j++){      
                    ElementaryFunction ef = keys_list.get(j);
                    int lc_element_mult = linearcomb_map.get(ef);

                    if(j != 0 && lc_element_mult > 0){ //don't add "+" mark before the first element of linear combination
                        str_marking[0] += "+";
                    }

                    if(lc_element_mult!= 1 && lc_element_mult != -1){
                        str_marking[0] += lc_element_mult;
                    }else if(lc_element_mult == -1){
                        str_marking[0] += "-";
                    }

                    //cast ef to Subcl | All | Token
                    if(ef instanceof Projection){
                        Projection pro = (Projection) ef;
                        
                        String var_name = Variable_index_table.get_instance().get_var_name_at_index(transition_name, pro.getSort().name(), pro.getIndex());
                        
                        if(var_name == null){
                            throw new NullPointerException("Can't use a null variable for projection: " + pro.toString());
                        }
                        
                        switch (pro.getSucc()) {
                            case 1: //++
                                str_marking[0] += var_name + "++ ";
                                break;
                            case -1: //--
                                str_marking[0] += var_name + "-- ";
                                break;
                            default:
                                str_marking[0] += var_name + " ";
                                break;
                        }
                        
                    }else if(ef instanceof Subcl){
                        Subcl con = (Subcl) ef;
                        str_marking[0] += con.getSort().getConstraint(con.index()).name() + " ";
                    }else{ //All
                        str_marking[0] += "All ";
                    }                                
                }
                
                //check if there's a tuple element
                if(it2.hasNext()){
                    str_marking[0] += ", ";
                }
            }
            
            str_marking[0] += "> ";
            
            if(expanded_filter != null && !expanded_filter.equals("")){
                 str_marking[0] += "[" + expanded_filter + "] ";
            }
            
            //check if there's a next tuple
            if(it1.hasNext()){
                str_marking[0] += " + ";
            }
        }            
        
        return str_marking[0];
    }
    
    /**
     * write all transitions with different graphics points
     */
    private void write_transitions(){
        int[] transition_xy = new int[]{900, 200}; //graphics points
                
        SyntaxTree.get_instance().get_synt_transitions().stream().forEach(
                transition -> {
                    //write all transitions but modify their graphical points x,y
                    ArrayList<String> transition_data = new ArrayList<>();
                    transition_data.add("id@=" + transition.get_name());
                    String guard = "";
                    Syntactic_guard synt_guard = transition.get_syntactic_guard();
                    
                    if(synt_guard != null && !synt_guard.get_separated_predicates().isEmpty()){
                        boolean invert_guard = synt_guard.get_invert_guard();
                        
                        if(invert_guard == true){
                           guard = "!("; 
                        }
                        
                        LinkedHashMap<Syntactic_predicate, String> separated_predicates = synt_guard.get_separated_predicates();
                        
                        for(Syntactic_predicate synt_predicate : separated_predicates.keySet()){
                           ArrayList<String> predicate_elements = synt_predicate.get_predicate_elements(); 
                           boolean invert_predicate = synt_predicate.get_invert_guard();
                           
                            if(invert_predicate == true){
                               guard = "!("; 
                            }
                                                   
                            for(String predicate_element : predicate_elements){
                               guard += " " + predicate_element;
                            }
                            
                            if(invert_predicate == true){
                               guard += ")"; 
                            }
                        }
                        
                        if(invert_guard == true){
                           guard += ")"; 
                        }
                    }
                    
                    if(!guard.equals("")){
                        transition_data.add("condition@=" + guard);
                    }
                    
                    transition_xy[1] += 25;
                    transition_data.add("graphics@=" + "x=" + transition_xy[0] + "y=" + transition_xy[1]);
                    //add transition to be written
                    xmlwriter.add_transition(transition_data);
                }
        );
    }
    
    /**
     * 
     * @param p the place that we want to fold
     * @return HashMap of all places names and their corresponding filters
     */
    private HashMap<String, String> unfold_place(Place p){
        //apply cartesian product on cd colour classes combinations & name resulting places with colour class name + combination, colour class name + combination ... (following cartesian product combinations)
        Domain d = p.get_node_domain(); //cd
        String cd_name = d.name();
        //contains the results of cartesian product between cd_base_filters (of each colour class) and subclasses of cd elements from (subcc_predicates) 
        //in case of certain possible combinations (possible_combs) with N > 1 subclass repetitions
        HashMap<String, String> all_places_combs_filter = new HashMap<>();
            
        if(this.cd_all_places_filters.containsKey(cd_name)){
            all_places_combs_filter = this.cd_all_places_filters.get(cd_name);
        }else{ 
            Map<? extends Sort, Integer> d_map = d.asMap();

            if(cd_name.equals("Undefined domain")){ // one colour class domain
                d.set_name(d_map.keySet().iterator().next().name());
            }
            
            HashMap<String, HashMap<String, ArrayList<String>>> cd_combined_filters = new HashMap<>(); 
            HashMap<String, ArrayList<String>> ccs_base_filters = this.unfold_colordomain((Map<ColorClass, Integer>) d_map, cd_name);
            HashMap<String, ArrayList<String>> possible_combs = this.cd_possible_combs.get(cd_name);
            
            possible_combs.keySet().stream().forEach(
                    cc_name -> {
                        HashMap<String, ArrayList<String>> cc_all_combs = new HashMap<>();
                        Interval[] subs = sn.find_colorClass(cc_name).getConstraints();
                        ArrayList<String> cc_combs = possible_combs.get(cc_name);
                        
                        for(var k = 0; k < cc_combs.size(); k++){
                            String possible_comb = cc_combs.get(k);
                            ArrayList<String> cc_base_filters = new ArrayList<>(List.of(ccs_base_filters.get(cc_name).get(k)));
                            int[] subcc_repetitions = new int[subs.length];

                            for(var i = 0; i < possible_comb.length(); i++){
                                char c = possible_comb.charAt(i);
                                int N = 0, subcc_index = Integer.parseInt(String.valueOf(c)) - 1;

                                if(subcc_repetitions[subcc_index] == 0){ //check if subclass hasn't already been calculated before
                                    //calculate N repetitions of a certain subclass in possible combination
                                    for(var j = i; j < possible_comb.length(); j++){

                                        if(possible_comb.charAt(i) == possible_comb.charAt(j)){
                                            N++;
                                        }
                                    }
                                    subcc_repetitions[subcc_index] = N;
                                }
                            }
                            ArrayList<String> subs_predicates = this.get_cp_subccs_predicates(subcc_repetitions, subs);

                            if(!subs_predicates.isEmpty()){
                                //combine each colour class base filter predicate(s) with each subclass predicate(s) in case of finding combination with N > 1 subclass-repetitions
                                 cc_all_combs.put(possible_comb, this.apply_cartesian_product(cc_base_filters, subs_predicates));
                            }else{
                                cc_all_combs.put(possible_comb, cc_base_filters);
                            }
                        }
                        //System.out.println(Arrays.toString(cc_all_combs.values().toArray()));
                        cd_combined_filters.put(cc_name, cc_all_combs);
                    }
            );
            Iterator it1 = cd_combined_filters.keySet().iterator(), it2 = cd_combined_filters.keySet().iterator();
            
            if(it1.hasNext()){
                all_places_combs_filter = this.apply_cc_filters_cp(cd_combined_filters, it1);
            }
            
            //reserve calculated & unfolded places to use later if we face another place with the same cd name
            this.cd_all_places_filters.put(cd_name, all_places_combs_filter);
        }
        return all_places_combs_filter;
    }
    
    /**
     * 
     * @param cd_combined_filters HashMap of all cd's colour classes combined filters of a possible combination
     * @param it1 iterator of colour class first key element (starts with the head of keys)
     * @return HashMap all generated places names with their corrisponding filters
     */
    private HashMap<String, String> apply_cc_filters_cp(HashMap<String, HashMap<String, ArrayList<String>>> cd_combined_filters, Iterator it1){    
        HashMap<String, String> all_places_combs_filter = new HashMap<>();
        String cc_name = (String)it1.next(), place_name;
        HashMap<String, ArrayList<String>> cc_internal_combs_filters = cd_combined_filters.get(cc_name);               
//        cd_combined_filters.keySet().stream().forEach(
//                cc -> {
//                    System.out.println(cc + "------");
//                    HashMap<String, ArrayList<String>> cc_pf = cd_combined_filters.get(cc);
//                    
//                    cc_pf.keySet().stream().forEach(
//                            pc -> {
//                                System.out.println(pc + "," + Arrays.toString(cc_pf.get(pc).toArray()));
//                            }
//                    );
//                }
//        );
        //subplace_name and its corresponding filter
        HashMap<String, String> pre_calculated_subtree = new HashMap<>(); //used to be assigned to a filter instead of calculating all its subtree cartesian product again 
        
        for(String possible_comb : cc_internal_combs_filters.keySet()){
            place_name = cc_name + possible_comb;
            ArrayList<String> filters = cc_internal_combs_filters.get(possible_comb);
            //System.out.println(possible_comb + "," + Arrays.toString(filters.toArray()));
            if(pre_calculated_subtree.isEmpty()){
                pre_calculated_subtree = this.calculate_all_places_c_f(cd_combined_filters, it1);   
            }
            //System.out.println(place_name + "," + pre_calculated_subtree.toString());
            
            if(filters.size() > 1){
                String place_name1;

                for(var i = 0; i < filters.size(); i++){
                    place_name1 = place_name + "_" + String.valueOf(i+1);
                    
                    if(pre_calculated_subtree.keySet().isEmpty()){
                        all_places_combs_filter.put(place_name1, filters.get(i));
                    }else{
                        for(String sub_p_name : pre_calculated_subtree.keySet()){
                            all_places_combs_filter.put(place_name1 + sub_p_name, filters.get(i) + " and " + pre_calculated_subtree.get(sub_p_name));
                        }
                    }
                } 
            }else{
                if(pre_calculated_subtree.keySet().isEmpty()){
                        all_places_combs_filter.put(place_name, filters.get(0));
                }else{
                    for(String sub_p_name : pre_calculated_subtree.keySet()){
                        all_places_combs_filter.put(place_name + sub_p_name, filters.get(0) + " and " + pre_calculated_subtree.get(sub_p_name)); 
                    }
                }
            }
        }

        return all_places_combs_filter;
    }
    
        /**
     * 
     * @param cd_combined_filters HashMap of all cd's colour classes combined filters of a possible combination
     * @param it1 iterator of colour class key element
     * @return HashMap all generated sub-places names with their corrisponding filters
     */
    private HashMap<String, String> calculate_all_places_c_f(HashMap<String, HashMap<String, ArrayList<String>>> cd_combined_filters, Iterator it1){
        HashMap<String, String> all_places_filters = new HashMap<>();

        if(it1.hasNext()){
            all_places_filters= this.apply_cc_filters_cp(cd_combined_filters, it1);
        }
        
        return all_places_filters;
    }
        
    /**
     * Note: unfolding a colour class with multiplicity > 9 in cd, may cause exceptions because of using one digit char while producing combinations
     * @param multiplied_cc HashMap of all colour classes and their multiplicities in a certain colour domain
     * @return HashMap of all colour classes with their combinations present in colour domain cd on which cartesian product will be applied
     */
    private HashMap<String, ArrayList<String>> unfold_colordomain(Map<ColorClass, Integer> multiplied_cc, String cd_name){ //prodotto di C_i^ei per i=1..n
        HashMap<String, ArrayList<String>> cd_base_filters = new HashMap<>();
                
        if(this.cd_possible_combs.containsKey(cd_name)){ //combinations are already calculated before
            
            multiplied_cc.keySet().stream().forEach(
                cc -> {
                    cd_base_filters.put(cc.name(), this.cc_base_filters.get(cc.name()).get(multiplied_cc.get(cc)));
                }
            );
        }else{
        
            multiplied_cc.keySet().stream().forEach(
                    cc -> {
                        cd_base_filters.put(cc.name(), this.unfold_multiplied_cc(cc, multiplied_cc.get(cc), cd_name));
                    }
            );
        }
        
        return cd_base_filters;
    }
    
    /**
     * 
     * @param cc the colour class that we want to unfold its subclasses
     * @param multiplicity the multiplicity of colour class in a certain cd/cc itself
     * @param cd_name the name of a colour domain in a certain colour domain/colour class
     * @return base filters found during the unfolding of a certain colour class
     * @throws UnsupportedElementNameException if the name of sub-colour-class is undefined
     * @throws RuntimeException if cd_possible_combs Map signs that colour-class's combinations at multiplicity ei is calculated but base filters aren't found in cc_base_filters
     */
    private ArrayList<String> unfold_multiplied_cc(ColorClass cc, int multiplicity, String cd_name) throws UnsupportedElementNameException, RuntimeException{ //C_i^ei
        Interval[] subs = cc.getConstraints();
        ArrayList<String> cc_possible_combs = new ArrayList<>();
                    
        if(subs.length == 1 && subs[0].name().equals("Undefined interval")){ //case of unnamed single sub-colour-class
            //modify the single subclass name with parent colour class name
            subs[0].set_name(cc.name());
        }
        
        ArrayList<String> base_filters = new ArrayList<>();
        
        if(this.cd_possible_combs.containsKey(cd_name) && this.cd_possible_combs.get(cd_name).containsKey(cc.name())){ //check if cc's combinations are already calculated before
            HashMap<Integer, ArrayList<String>> cc_mult_base_filters = this.cc_base_filters.get(cc.name()); 
            
            if(cc_mult_base_filters.containsKey(multiplicity)){
                return cc_mult_base_filters.get(multiplicity);
            }else{
                throw new RuntimeException("Can't find pre-calculated base filters of: " + cc.name());
            }
            
        }else if(multiplicity == 1){ //C_i^1
            //generate n filters equal to the number of subclasses in C_i            
            for(var i = 0; i < subs.length; i++){
                String subcc_name = subs[i].name();
                
                if(subcc_name.equals("Undefined interval")){
                    throw new UnsupportedElementNameException("Sub-colour-class name isn't defined yet! :" + subs[i].toString());
                }
                
                base_filters.add("@" + cc.name() + "[0] in " + subs[i].name());           
                cc_possible_combs.add(String.valueOf(i + 1));
            }
        }else{ //C_i^ei, ei > 1
            //generate all possible combinations of subclasses
            int combs_subcc = (int) Math.pow(subs.length, multiplicity) / subs.length; //all combinations/number of subclasses in cc
            
            String comb;
            for(var i = 0; i < subs.length; i++){
                comb = String.valueOf(i + 1);
                
                for(var j = 0; j < combs_subcc; j++){

                    for(var k = multiplicity; k > 1; k--){ //size of each combination without first integer that is known as subclass index + 1
                        
                        if(comb.length() < multiplicity){ //first assignment of combination should equal the index of sublass+1 + "11111..."
                            comb = comb + "1";
                        }else{ //first combination is already added
                            int char_index = k - 1, comb_num = Integer.parseInt(String.valueOf(comb.charAt(char_index)));
                            
                            if(comb_num < subs.length){ //increment this char and retrieve this combination
                                StringBuilder comb_builder = new StringBuilder(comb);
                                comb_builder.setCharAt(char_index, (char) ((comb_num + 1) + '0'));                                
                                comb = this.replace_from(char_index +1, comb_builder);
                                break;
                            }
                        }
                    }
                    cc_possible_combs.add(comb);
                    //System.out.println(comb);
                }
            }
            base_filters = this.find_cc_base_filters(cc, cc_possible_combs);
        }
        
        //update cd with cc possible combs
        HashMap<String, ArrayList<String>> possible_combs;

        if(!this.cd_possible_combs.containsKey(cd_name)){
            possible_combs = new HashMap<>();
        }else{
            possible_combs = this.cd_possible_combs.get(cd_name);   
        }
        possible_combs.put(cc.name(), cc_possible_combs); 
        this.cd_possible_combs.put(cd_name, possible_combs);
//        System.out.println(cc.name() + "," + cc_possible_combs);
//        //System.out.println(cc.name() + "," + multiplicity);
//        base_filters.stream().forEach(
//                base_filter -> System.out.println(base_filter)
//        );
        //create CS
        //ArrayList<Interval> CS = this.create_CS(cc, multiplicity);
        //update  base filters
        HashMap<Integer, ArrayList<String>> ei_base_filters;
        if(this.cc_base_filters.containsKey(cc.name())){
            ei_base_filters = this.cc_base_filters.get(cc.name());
        }else{
            ei_base_filters = new HashMap<>();
        }
        
        ei_base_filters.put(multiplicity, base_filters);
        this.cc_base_filters.put(cc.name(), ei_base_filters);
        
        return base_filters;
    }
    
    private ArrayList<String> find_cc_base_filters(ColorClass cc, ArrayList<String> cc_possible_combs){ //uses colour class possilbe combinations
        ArrayList<String> base_filters = new ArrayList<>();
        Interval[] subs = cc.getConstraints();
        
        cc_possible_combs.stream().forEach(
                possible_comb -> {      
                    String base_filter = "";
                    int[] subcc_repetitions = new int[subs.length];
                    
                    for(var i = 0; i < possible_comb.length(); i++){
                        char c = possible_comb.charAt(i);
                        int N = 0, subcc_index = Integer.parseInt(String.valueOf(c)) - 1;
                        //generate base filter predicate
                        base_filter += "@" + cc.name() + "[" + i +"] in " + subs[subcc_index].name(); 
                        
                        if(i != possible_comb.length() -1){ //add "and" operation 
                            base_filter += " and ";
                        }
                        
                        if(subcc_repetitions[subcc_index] == 0){ //check if subclass hasn't already been calculated before
                            //calculate N repetitions of a certain subclass in possible combination
                            for(var j = i; j < possible_comb.length(); j++){

                                if(possible_comb.charAt(i) == possible_comb.charAt(j)){
                                    N++;
                                }
                            }
                            subcc_repetitions[subcc_index] = N;
                            
                            if(N > 1){
                                 this.unfold_sub_cc(subs[subcc_index], N, cc.name());
                            }
                        }
                    }
                    
                    base_filters.add(base_filter);
                }
        );
        
        return base_filters;
    }
    
    /**
     * 
     * @param subcc the subclass that we want to know its predicates using function assign
     * @param N should be > 1 to call this function //numero variabili (ripetizioni della sottoclasse in cd)
     * @param cc_name the name of colour class used to generate filter variables starting with @
     * @return ArrayList of each filter's predicate in case of a certain N
     */
    private void unfold_sub_cc(Interval subcc, int N, String cc_name){
        vargrp vg = new vargrp();
        vg.V[1] = 1; // prima variabile assegnata al primo gruppo
        vg.grp[1][0] = 1; // primo gruppo ha 1 variabile
        vg.grp[1][1] = 1; // la prima variabile del gruppo 1 è la 1

        HashMap<Integer, ArrayList<String>> N_predicates;
        
        if(this.subcc_predicates.containsKey(subcc.name())){
            N_predicates = this.subcc_predicates.get(subcc.name());
        }else{
            N_predicates = new HashMap<>();
        }
        
        if(!N_predicates.containsKey(N)){
            ArrayList<String> multi_predicates = this.assign(vg, 1, 1, min(N, this.calculate_K(subcc)), N, new ArrayList<>(), cc_name);
            
//            System.out.println("------------------");
//            multi_predicates.stream().forEach(
//                    preds -> System.out.println(Arrays.toString(preds.toArray()))
//            );
            
            N_predicates.put(N, multi_predicates);
            this.subcc_predicates.put(subcc.name(), N_predicates);
        }
    }
   
    private ArrayList<String> apply_cartesian_product(ArrayList<String> l1, ArrayList<String> l2){
        ArrayList<String> res = new ArrayList<>();
        
        l1.stream().forEach(
                f1 -> {
                    
                    if(l2.isEmpty()){
                        res.add(f1);
                    }else{
                        l2.stream().forEach(
                                f2 -> {
                                    res.add(f1 + " and " + f2);
                                }
                        );
                    }
                    
                }
        );
        return res;
    }
    /**
     * Note: all subclasses used in this method should have N > 1
     * @param subcc_repetitions array of subclasses repetitions in a certain cd
     * @param subs arrays of all subclasses of a certain cc
     * @return Cartesian product between subclasses combined predicates and other subclasses of same colour class
     */
    private ArrayList<String> get_cp_subccs_predicates(int[] subcc_repetitions, Interval[] subs){
        ArrayList<String> cart_product_subccs = new ArrayList<>();
        ArrayList<String> calculated_cp = new ArrayList<>(), subcc1_filters = new ArrayList<>();
        boolean calculated = false;
        
        for(var i = 0; i < subcc_repetitions.length; i++){
            
            if(subcc_repetitions[i] > 1){
                subcc1_filters = this.subcc_predicates.get(subs[i].name()).get(subcc_repetitions[i]);
                //calculate cartesian product between any existing different subclasses with N > 1
                for(String subcc1_filter : subcc1_filters){
                     
                    for(var j = i+1; j < subcc_repetitions.length; j++){
                        ArrayList<String> subcc2_filters = this.subcc_predicates.get(subs[j].name()).get(subcc_repetitions[j]);
                        
                        if(subcc_repetitions[j] > 1){
                            String res_filter;
                            
                            if(!calculated_cp.contains(String.valueOf(j) + String.valueOf(i))){
                                calculated = true;
                                
                                for(String subcc2_filter : subcc2_filters){
                                    res_filter = subcc1_filter + " and " + subcc2_filter;
                                    cart_product_subccs.add(res_filter);
                                }
                                calculated_cp.add(String.valueOf(i) + String.valueOf(j));
                            }
                        }
                    }
                }
            }
        }
        
        if(!calculated){
            return subcc1_filters;
        }
        
        return cart_product_subccs;
    }
    /**
     * 
     * @param str_index starting index from which we want to set '1'
     * @param sb StringBuilder on which we elaborate replacing chars with '1'
     * @return elaborated StringBuilder
     */
    private String replace_from(int str_index, StringBuilder sb){
        
        for(var i = str_index; i < sb.length(); i++){
            sb.setCharAt(i, '1');
        }
        
        return sb.toString();
    }
    /**
     * 
     * @param subcc
     * @return K the dimension of static subclass
     */
    private int calculate_K(Interval subcc){
        int K = 0;
        
        if(subcc.lb() == subcc.ub()){ //exact number of elements
            K = subcc.lb();
        }else{
            K = subcc.size();
        }
        
        return K;
    }
    
//    /**
//     * 
//     * @param subs the subclasses of a certain colour class
//     * @param multiplicity the multiplicity of colour class in a certain cd/cc itself
//     * @return an ArrayList of intervals of size n_subclasses^multiplicity
//     * @throws UnsupportedElementNameException if the name of sub-colour-class is undefined
//     */
//    private ArrayList<Interval> create_CS(Interval[] subs , int multiplicity) throws UnsupportedElementNameException{
//        ArrayList<Interval> CS = new  ArrayList<>();
//        
//        Arrays.stream(subs).forEach(
//                subcc -> {
//
//                    for(var i = 0; i < multiplicity; i++){
//                        
//                        if(subcc.name().equals("Undefined interval")){
//                            throw new UnsupportedElementNameException("Sub-colour-class name isn't defined yet! :" + subcc.toString());
//                        }
//                        CS.add(subcc);
//                    }
//                }
//        ); 
//        
//        return CS;
//    }
    
    private ArrayList<String> assign(vargrp part, int assigned, int taken, int maxgroup, int N, ArrayList<String> multi_predicates, String cc_name){
        ArrayList<String> filter_predicates = new ArrayList<>();
        int i,j;
        
        if(assigned == N){// stampa l'assegnazione variabile - sottoinsieme
           System.out.printf("\n");
            
            for(i = 1;i <= N; i++)
              System.out.printf("@%s[%d] in grp %d\n", cc_name, i-1, part.V[i]);
            for(i = 1; i <= taken; i++){ //printf("g%d : ",i);
                
              for(j = 2; j <= part.grp[i][0]; j++){
                System.out.printf("(@%s[%d] == @%1$s[%d])", cc_name, part.grp[i][1]-1, part.grp[i][j]-1);
                filter_predicates.add("@" + cc_name + "[" + String.valueOf(part.grp[i][1] - 1) + "]" + " == " + "@" + cc_name + "[" + String.valueOf(part.grp[i][j] - 1) + "]");
              }
              
              if(part.grp[i][0] > 1) System.out.printf(" \n");
            }
            for(i = 1; i <= taken; i++)
               for (j = i+1; j <= taken; j++){
                System.out.printf("(@%s[%d] != @%1$s[%d])",cc_name, part.grp[i][1]-1, part.grp[j][1]-1);
                filter_predicates.add("@" + cc_name + "[" + String.valueOf(part.grp[i][1] - 1) + "]" + " != " + "@" + cc_name + "[" + String.valueOf(part.grp[j][1] - 1) + "]");
               }
            System.out.printf(" \n");
            multi_predicates.add(this.set_and_between_predicates(filter_predicates));
        }else{ // richiamo ricorsivo
            int g, newtaken;
            assigned++;

            for(g = 1; g <= min(taken+1,maxgroup); g++){
               part.V[assigned] = g;
               part.grp[g][0]++;
               part.grp[g][part.grp[g][0]] = assigned;

               if(g > taken) newtaken = g; else newtaken = taken;
               
               this.assign(part, assigned, newtaken, maxgroup, N, multi_predicates, cc_name);
               part.grp[g][part.grp[g][0]] = 0;
               part.grp[g][0]--;
            }
        }
        
        return multi_predicates;
    }
    
    private String set_and_between_predicates(ArrayList<String> filter_predicates){
        String filter = "";
        
        for(var i = 0; i < filter_predicates.size(); i++){
            filter += filter_predicates.get(i);
            
            if(i != filter_predicates.size() -1){
                filter += " and ";
            }
        }
        
        return filter;
    }
    /**
     * Note: used in assign()
     * @param a first number
     * @param b second number
     * @return the minimum one between them
     */
    private int min(int a, int b){
        if(a < b) return a;
        else return b; 
    }
    
    private class vargrp{
        int[] V = new int[20]; // assegnazione delle variabili ai gruppi
        int[][] grp = new int[20][20]; // quante e quali variabili in ciascun gruppo 
    }
    /**
     * 
     * @return single static instance
     */
    public static PartialGenerator get_instance() throws Exception{
        
        if(instance == null){
            instance = new PartialGenerator();
        }
        
        return instance;
    }
}
