/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import albero_sintattico.*;
import componenti.*;
import eccezioni.UnsupportedElementNameException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.TransformerException;
import struttura_sn.ArcAnnotation;
import struttura_sn.Marking;
import wncalculus.color.ColorClass;
import struttura_sn.*;
import test.PartialGenerator_DataTester;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.classfunction.Projection;
import wncalculus.classfunction.Subcl;
import wncalculus.expr.Domain;
import wncalculus.expr.Interval;
import wncalculus.expr.Sort;
import wncalculus.wnbag.LinearComb;
import wncalculus.wnbag.TupleBag;

/**
 *
 * @author dell
 */
//singleton
//will be a part of factory pattern with (struttura_unfolding_parziale)/XMLWriter
public class PartialGenerator {
    //usable maps to prevent redundant calculations/combinations of colour classes of a colour domain cd
    private final HashMap<String, HashMap<String, String>> cd_all_places_filters; //contains pre-calculated cd's unfolded places names and their corresponding filters
    private final HashMap<String, HashMap<String, ArrayList<String>>> cd_possible_combs; //es. colour-domain name ->{colour-class name -> 11, 12, ...}
    private final HashMap<String, HashMap<Integer, ArrayList<String>>> cc_base_filters; //base filters of a colour class that follows ei multiplicity
    private final HashMap<String, HashMap<Integer, ArrayList<String>>> subcc_predicates; //predicates of each subcc for 1 <= n <= ei, Note: each filter's predicates are in the same list
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
        int[] y_multiplier = new int[]{1};
        
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
                                
                                HashMap<ArrayList<LinearComb>, Integer> initial_marking = mk.get_marking_of_place(place.get_name());
                                //copy intitial marking of place in all unfolded places
                                String marking = "";
                                if(!initial_marking.isEmpty()){
                                    marking = this.get_place_initial_marking(initial_marking);
                                    place_data.add("hlinitialMarking@=" + marking);
                                }
                                place_xy[1] += 20;
                                place_data.add("graphics@=" + "x=" + place_xy[0] + "y=" + place_xy[1]);
                                //add place to be written in pnml
                                xmlwriter.add_place(place_data, false);
                                
                                place_data = new ArrayList<>();
                                place_data.add("name@=" + prefix_name + p_name);
                                place_data.add("domain@=" + place.get_type());
                  
                                if(!marking.equals("")){
                                   place_data.add("marking@=" + marking);
                                }

                                place_data.add("graphics@=" + "x=" + place_xy[0] + "y=" + String.valueOf(place_xy[1] - y_multiplier[0] * 10));
                                y_multiplier[0]++;
                                //add place to be written in pnpro
                                xmlwriter.add_place(place_data, true);
                                
                                //write place arcs with unfolded filter
                                this.write_unfolded_place_arcs(place, p_name, all_places_combs_filter.get(p_name));
                            }
                    );
                }
        );
        
        this.write_transitions();
        place_xy[1] += 20;
        int[] xy = this.write_colourclasses_domains_pnpro(place_xy, y_multiplier);
        this.write_variables_pnpro(xy, y_multiplier);
//       HashMap<String, String> all_places_combs_filter = this.unfold_place(sn.get_P().get(1));
//
//       all_places_combs_filter.keySet().stream().forEach(
//                p -> {
//                    System.out.println(p + "," + all_places_combs_filter.get(p));
//                }
//        );
        xmlwriter.write_all_data(false);
    }
    
    /**
     * 
     * @param initial_marking HashMap of token-tuple's elements (ArrayList of linear combinations) with their multiplicity
     * @return the equivalent String of initial marking map
     */
    public String get_place_initial_marking(HashMap<ArrayList<LinearComb>, Integer> initial_marking){
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
    public void write_unfolded_place_arcs(Place p, String place_name_suffix, String expanded_filter){
        HashMap<Node, ArcAnnotation> next_nodes = p.get_next_nodes();
        HashMap<Node, ArcAnnotation> previous_nodes = p.get_previous_nodes();
        HashMap<Node, ArcAnnotation> inhibitored_nodes = p.get_inib_nodes();
        //PartialGenerator_DataTester pg_dt = PartialGenerator_DataTester.get_instance();
        
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
                ArcAnnotation arc = next_nodes.get(next_node);
                String ex = this.get_tuples_expression(expanded_filter, arc.get_name());
                arc_data.add("hlinscription@=" + ex);
                //pg_dt.print_element_data(arc_data);
                //add arc to be written in pnml
                xmlwriter.add_arc(arc_data, false);
                
                arc_data = new ArrayList<>();
                arc_data.add("head@=" + to);
                arc_data.add("tail@=" + from);
                arc_data.add("kind@=INPUT");
                arc_data.add("mult@=" + ex);
                //add arc to be written in pnpro
                xmlwriter.add_arc(arc_data, true);
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
                ArcAnnotation arc = previous_nodes.get(previous_node);
                String ex = this.get_tuples_expression(expanded_filter, arc.get_name());
                arc_data.add("hlinscription@=" + ex);    
                //pg_dt.print_element_data(arc_data);
                //add arc to be written
                xmlwriter.add_arc(arc_data, false);
            
                arc_data = new ArrayList<>();
                arc_data.add("head@=" + to);
                arc_data.add("tail@=" + from);
                arc_data.add("kind@=OUTPUT");
                arc_data.add("mult@=" + ex);
                //add arc to be written in pnpro
                xmlwriter.add_arc(arc_data, true);
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
                ArcAnnotation arc = inhibitored_nodes.get(inhibitored_node);
                String ex = this.get_tuples_expression(expanded_filter, arc.get_name());
                arc_data.add("hlinscription@=" + ex);
                arc_data.add("type@=inhibitor");
                //pg_dt.print_element_data(arc_data);
                //add arc to be written
                xmlwriter.add_arc(arc_data, false);
                
                arc_data = new ArrayList<>();
                arc_data.add("head@=" + from);
                arc_data.add("tail@=" + to);
                arc_data.add("kind@=INHIBITOR");
                arc_data.add("mult@=" + ex);
                //add arc to be written in pnpro
                xmlwriter.add_arc(arc_data, true);
            }
        );        
        
    }
    
//    /**
//     * 
//     * @param tb the tuple bag (Map of multiplied tuples) that we want to extract in a string
//     * @param expanded_filter the unfolded generated filter that will be added to each tuple in tb
//     * @return the resulting string of all filtered tuples combinations
//     * @throws NullPointerException if a variable hasn't extracted from a projection
//     */
//    private String get_tuples_expression(TupleBag tb, String expanded_filter, String transition_name) throws NullPointerException{
//        String[] str_marking = new String[]{""};//wrapper pf marking
//        Map<WNtuple, Integer> multiplied_tuple = (Map<WNtuple, Integer>) tb.asMap();
//        
//        Iterator it1 = multiplied_tuple.keySet().iterator();
//        
//        while(it1.hasNext()){
//            WNtuple tuple = (WNtuple) it1.next();
//            List<LinearComb> tuple_elements_list = (List<LinearComb>) tuple.getComponents();
//            int tuple_mult = multiplied_tuple.get(tuple);
//
//            if(tuple_mult != 1){
//                str_marking[0] += tuple_mult;
//            }
//    
//            tuple.filter();
//            str_marking[0] += "<";
//            
//            Iterator it2 = tuple_elements_list.iterator();
//            while(it2.hasNext()){
//                LinearComb tuple_element = (LinearComb) it2.next();
//                Map<ElementaryFunction, Integer> linearcomb_map = (Map<ElementaryFunction, Integer>) tuple_element.asMap();
//                ArrayList<ElementaryFunction> keys_list = new ArrayList<>(linearcomb_map.keySet()); //to use ArrayList's index
//
//                for(var j = 0; j < keys_list.size(); j++){      
//                    ElementaryFunction ef = keys_list.get(j);
//                    int lc_element_mult = linearcomb_map.get(ef);
//
//                    if(j != 0 && lc_element_mult > 0){ //don't add "+" mark before the first element of linear combination
//                        str_marking[0] += "+";
//                    }
//
//                    if(lc_element_mult!= 1 && lc_element_mult != -1){
//                        str_marking[0] += lc_element_mult;
//                    }else if(lc_element_mult == -1){
//                        str_marking[0] += "-";
//                    }
//
//                    //cast ef to Subcl | All | Projection
//                    if(ef instanceof Projection){
//                        Projection pro = (Projection) ef;
//                        
//                        String var_name = Variable_index_table.get_instance().get_var_name_at_index(transition_name, pro.getSort().name(), pro.getIndex());
//                        
//                        if(var_name == null){
//                            throw new NullPointerException("Can't use a null variable for projection: " + pro.toString());
//                        }
//                        
//                        switch (pro.getSucc()) {
//                            case 1: //++
//                                str_marking[0] += var_name + "++ ";
//                                break;
//                            case -1: //--
//                                str_marking[0] += var_name + "-- ";
//                                break;
//                            default:
//                                str_marking[0] += var_name + " ";
//                                break;
//                        }
//                        
//                    }else if(ef instanceof Subcl){
//                        Subcl con = (Subcl) ef;
//                        str_marking[0] += con.getSort().getConstraint(con.index()).name() + " ";
//                    }else{ //All
//                        str_marking[0] += "All ";
//                    }                                
//                }
//                
//                //check if there's a tuple element
//                if(it2.hasNext()){
//                    str_marking[0] += ", ";
//                }
//            }
//            
//            str_marking[0] += "> ";
//            
//            if(expanded_filter != null && !expanded_filter.equals("")){
//                 str_marking[0] += "[" + expanded_filter + "] ";
//            }
//            
//            //check if there's a next tuple
//            if(it1.hasNext()){
//                str_marking[0] += " + ";
//            }
//        }            
//        
//        return str_marking[0];
//    }
    
     /**
     * 
     * @param expanded_filter the unfolded generated filter that will be added to each tuple in tb
     * @param arc_name the name of arc to which an arc expression is associated
     * @return the resulting string of all filtered tuples combinations
     * @throws NullPointerException if there's no syntactic arc found with the name passed
     */
    private String get_tuples_expression(String expanded_filter, String arc_name) throws NullPointerException{
        String str_expr = "";
        Syntactic_arc synt_arc = SyntaxTree.get_instance().find_syntactic_arc_ByName(arc_name);
        
        if(synt_arc == null){
            throw new NullPointerException("Can't find syntactic arc: " + arc_name);
        }
        
        LinkedHashMap<Syntactic_tuple, Integer>  multiplied_tuples = synt_arc.get_all_tuples();
        Iterator it1 = multiplied_tuples.keySet().iterator();
        
        while(it1.hasNext()){
            Syntactic_tuple synt_tuple = (Syntactic_tuple) it1.next();
            String[] t_elements = synt_tuple.get_tuple_elements();
            int mult = multiplied_tuples.get(synt_tuple);
            
            if(mult > 1){
                str_expr += mult;
            }
            
            Syntactic_guard synt_guard = synt_tuple.get_syntactic_guard();
            
            if(synt_guard != null && !synt_guard.get_separated_predicates().isEmpty()){
                str_expr += "[";
                boolean invert_g = synt_guard.get_invert_guard();
                
                if(invert_g){
                    str_expr += "!(";
                }
                
                LinkedHashMap<Syntactic_predicate, String> separated_predicates = synt_guard.get_separated_predicates();
                 
                for(Syntactic_predicate synt_predicate : separated_predicates.keySet()){
                   boolean invert_p = synt_predicate.get_invert_guard();
                   
                   if(invert_p){
                    str_expr += "!(";
                   }
                   
                   for(String p_element : synt_predicate.get_predicate_elements()){
                       str_expr += p_element + " ";
                   }
                   
                   if(invert_p){
                    str_expr += ")";
                   }
                   
                   String sep_op = separated_predicates.get(synt_predicate);
                   
                   if(sep_op != null){   
                      
                        if(sep_op.equals("and")){
                           str_expr += " && ";
                        }else{
                           str_expr += " || ";
                        }
                   }
                }
                
                if(invert_g){
                    str_expr += ")";
                }
                str_expr += "]";
            }
            
            str_expr += "<";
            
            for(var i = 0; i < t_elements.length; i++){
                str_expr += t_elements[i];
                //check if there's a next tuple element
                if(i < t_elements.length - 1){
                    str_expr += ", ";
                }
            }
            
            str_expr += ">";            
            Syntactic_guard synt_filter = synt_tuple.get_syntactic_filter();
            
            if(synt_filter != null && !synt_filter.get_separated_predicates().isEmpty()){
                str_expr += "[";
                boolean invert_g = synt_filter.get_invert_guard();
                
                if(invert_g){
                    str_expr += "!(";
                }
                
                LinkedHashMap<Syntactic_predicate, String> separated_predicates = synt_filter.get_separated_predicates();
                 
                for(Syntactic_predicate synt_predicate : separated_predicates.keySet()){
                   boolean invert_p = synt_predicate.get_invert_guard();
                   
                   if(invert_p){
                    str_expr += "!(";
                   }
                   
                   for(String p_element : synt_predicate.get_predicate_elements()){
                       str_expr += p_element + " ";
                   }
                   
                   if(invert_p){
                    str_expr += ")";
                   }
                   
                   String sep_op = separated_predicates.get(synt_predicate);
                   
                   if(sep_op != null){   
                       
                       if(sep_op.equals("and")){
                           str_expr += " && ";
                       }else{
                           str_expr += " || ";
                       }
                   }
                }
                
                if(invert_g){
                    str_expr += ")";
                }
                
                if(!expanded_filter.equals("")){
                    str_expr += "&&" + expanded_filter + "]";
                }else{
                    str_expr += "]";
                }
            }else if(!expanded_filter.equals("")){
                str_expr += "[" + expanded_filter +"]";
            }
            //check if there's a next tuple
            if(it1.hasNext()){
                str_expr += " + ";
            }
        }
        
        return str_expr;
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
                    transition_data.add("graphics@=" + "x=" + transition_xy[0] + "y=" +  transition_xy[0]);
                    //add transition to be written
                    xmlwriter.add_transition(transition_data, false);
                    
                    transition_data = new ArrayList<>();
                    transition_data.add("name@=" + transition.get_name());
                    
                    if(!guard.equals("")){
                        transition_data.add("guard@=" + guard);
                    }
                    transition_data.add("graphics@=" + "x=" + String.valueOf(transition_xy[0] - 800) + "y=" + String.valueOf(transition_xy[1] -200));
                    //add transition to be written in pnpro
                    xmlwriter.add_transition(transition_data, true);
                }
        );
    }
    
    /**
     * @param place_xy array of last place graphical points occupied, that will be used for creating colour class graphical points
     * @param y_multiplier vertical multiplier point
     * @return last graphics points occupied
     * write all domains and colour classes from SN in pnpro
     */
    private int[] write_colourclasses_domains_pnpro(int[] place_xy, int[] y_multiplier){
       Token_estimator te = Token_estimator.get_instance();
       Place_syntax_table pst = Place_syntax_table.get_instance();
       
       sn.get_C().stream().forEach(
               cc -> {
                  if(!cc.name().equals("Neutral")){ 
                    ArrayList<String> cc_data = new ArrayList<>();
                    cc_data.add("name@=" + cc.name());
                    String definition = "definition@=";

                    Interval[] subs = cc.getConstraints();

                    if(subs.length == 1){ //partitioned cc
                      ArrayList<Token> tokens = te.get_estimated_cc_tokens(cc.name());
                      definition += "enum {";

                      for(var i = 0; i < tokens.size(); i++){
                          definition += tokens.get(i).get_Token_value();

                          if(i < tokens.size() - 1){
                              definition += ",";
                          }
                      }

                      definition += "}";

                    }else{ //Non partitioned cc
                        subcc_token_index_table subcc_limits_t = subcc_token_index_table.get_instance();
                        boolean expl_non_param;
                                
                        for(var i = 0; i < subs.length; i++){
                          expl_non_param = subcc_limits_t.check_if_has_subcc(subs[i].name());
                          ArrayList<Token> tokens = te.get_estimated_cc_tokens(subs[i].name());
                          
                          if(expl_non_param){
                            String prefix = te.find_token_prefix(tokens.get(0).get_Token_value(), subs[i]);
                            Integer[] limits = subcc_limits_t.get_subcc_limits_indices(subs[i].name());
                            
                            definition += prefix + "{" + limits[0] + ".." + limits[1] + "} is " + subs[i].name();
                          }else if(subs[i].lb() == subs[i].ub()){ //explicit elements insertion
                            definition += "enum {";

                            for(var j = 0; j < tokens.size(); j++){
                                definition += tokens.get(j).get_Token_value();

                                if(j < tokens.size() - 1){
                                    definition += ",";
                                }
                            }

                            definition += "} is " + subs[i].name();
                          }else{
                            String prefix = te.find_token_prefix(tokens.get(0).get_Token_value(), subs[i]);
                            
                            definition += prefix + "{" + subs[i].lb() + ".." + subs[i].ub() + "} is " + subs[i].name();
                          }
                          
                          if(i < subs.length -1){
                              definition += " + ";
                          }
                        }
                    }

                    cc_data.add(definition);
                    //update graphics points
                    place_xy[1] += 3;
                    cc_data.add("graphics@=" + "x=" + place_xy[0] + "y=" + String.valueOf(place_xy[1] - y_multiplier[0] * 10));
                    //add colour class to be written
                    xmlwriter.add_colourclass(cc_data, true);
                  }
               }         
       );

       sn.get_DC().stream().forEach(
               d -> {
                    ArrayList<String> domain_data = new ArrayList<>();
                    domain_data.add("name@=" + d.name());
                    ArrayList<String> values = pst.get_cd_values(d.name());
                    String definition = "definition@=";
                            
                    for(var i = 0; i < values.size(); i++){
                            definition += values.get(i);
                        
                            if(i < values.size() - 1){
                                definition += " * ";
                            }
                    }
                    domain_data.add("definition@=" + definition);
                    //update graphics points
                    place_xy[1] += 3;
                    domain_data.add("graphics@=" + "x=" + place_xy[0] + "y=" + String.valueOf(place_xy[1] - y_multiplier[0] * 10));
                    //add colour class to be written
                    xmlwriter.add_colourclass(domain_data, true);
               }
       );
       
       return place_xy;
    }
    
    /**
     * @param place_xy array of last place graphical points occupied, that will be used for creating variable graphical points
     * @param y_multiplier vertical multiplier point
     * write all variables from SN in pnpro
     */
    private void write_variables_pnpro(int[] place_xy, int[] y_multiplier){
        
        sn.get_V().stream().forEach(
                v -> {
                    ArrayList<String> var_data = new ArrayList<>();
                    var_data.add("name@=" + v.get_name());
                    var_data.add("domain@=" + v.get_colourClass().name());
                    //update graphics points
                    place_xy[1] += 3;
                    var_data.add("graphics@=" + "x=" + place_xy[0] + "y=" + String.valueOf(place_xy[1] - y_multiplier[0] * 10));
                    //add colour class to be written
                    xmlwriter.add_variable(var_data, true);
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
        Map<? extends Sort, Integer> d_map = d.asMap();

        if(cd_name.equals("Undefined domain")){ // one colour class domain
            cd_name = d_map.keySet().iterator().next().name();
            d.set_name(cd_name);
        }
        //contains the results of cartesian product between cd_base_filters (of each colour class) and subclasses of cd elements from (subcc_predicates) 
        //in case of certain possible combinations (possible_combs) with N > 1 subclass repetitions
        HashMap<String, String> all_places_combs_filter = new HashMap<>();
            
        if(this.cd_all_places_filters.containsKey(cd_name)){
            all_places_combs_filter = this.cd_all_places_filters.get(cd_name);
        }else{ 
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
                            ArrayList<String> subs_predicates = this.get_cp_subccs_predicates(subcc_repetitions, subs, possible_comb);
//                            System.out.println(possible_comb + ":");
//                            System.out.println(cc_base_filters.toString());
//                            System.out.println(subs_predicates.toString());
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
            Iterator it1 = cd_combined_filters.keySet().iterator();
            
            if(it1.hasNext()){
                all_places_combs_filter = this.apply_cc_filters_cp(cd_combined_filters, it1);
            }
            
            //reserve calculated & unfolded places to use later if we face another place with the same cd name
            this.cd_all_places_filters.put(cd_name, all_places_combs_filter);
        }
        
//        PartialGenerator_DataTester pg_dt = PartialGenerator_DataTester.get_instance();
//        pg_dt.print_place_unfolded_places(cd_name, all_places_combs_filter);
        
        return all_places_combs_filter;
    }
    
    /**
     * 
     * @param cd_combined_filters HashMap of all cd's colour classes combined filters of a possible combination
     * @param it1 iterator of colour class first key element (starts with the head of keys)
     * @return HashMap all generated places names with their corresponding filters
     */
    private HashMap<String, String> apply_cc_filters_cp(HashMap<String, HashMap<String, ArrayList<String>>> cd_combined_filters, Iterator it1){    
        HashMap<String, String> all_places_combs_filter = new HashMap<>();
        String cc_name = (String)it1.next(), place_name;
        HashMap<String, ArrayList<String>> cc_internal_combs_filters = cd_combined_filters.get(cc_name);               
//        PartialGenerator_DataTester pg_dt = PartialGenerator_DataTester.get_instance();
//        pg_dt.print_cc_combined_filters_combs(cc_internal_combs_filters);

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
                            all_places_combs_filter.put(place_name1 + sub_p_name, filters.get(i) + " && " + pre_calculated_subtree.get(sub_p_name));
                        }
                    }
                } 
            }else{
                if(pre_calculated_subtree.keySet().isEmpty()){
                        all_places_combs_filter.put(place_name, filters.get(0));
                }else{
                    for(String sub_p_name : pre_calculated_subtree.keySet()){
                        all_places_combs_filter.put(place_name + sub_p_name, filters.get(0) + " && " + pre_calculated_subtree.get(sub_p_name)); 
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
        
//        PartialGenerator_DataTester pg_dt = PartialGenerator_DataTester.get_instance();
//        pg_dt.print_cc_filters_combs(cc_possible_combs, cd_name, true);
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
    
    /**
     * 
     * @param cc colour class that we want to know its base filters
     * @param cc_possible_combs array list of all possible combinations of a certain colour class
     * @return array list of base filters
     */
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
                            base_filter += " && ";
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
        
//        PartialGenerator_DataTester pg_dt = PartialGenerator_DataTester.get_instance();
//        pg_dt.print_cc_filters_combs(base_filters, cc.name(), false);
        
        return base_filters;
    }
    
    private int[] find_subcc_positions_in_combination(int subcc_index, int N, String cc_possible_comb){
        int[] subcc_positions = new int[N];
        
        int j = 0;
        for(var i = 0; i < cc_possible_comb.length(); i++){
            
            if(cc_possible_comb.charAt(i) == subcc_index + '0'){
                subcc_positions[j] = i;
                j++;
            }
            
        }
        //System.out.println(cc_possible_comb + "," + subcc_index + ":" + Arrays.toString(subcc_positions));
        return subcc_positions;
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
//                    preds -> System.out.println(preds)
//            );
//            
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
                                    res.add(f1 + " && " + f2);
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
    private ArrayList<String> get_cp_subccs_predicates(int[] subcc_repetitions, Interval[] subs, String possible_comb){
        ArrayList<String> cart_product_subccs = new ArrayList<>();
        ArrayList<String> calculated_cp = new ArrayList<>(), subcc1_filters = new ArrayList<>();
        boolean calculated = false;
        
        for(var i = 0; i < subcc_repetitions.length; i++){
            
            if(subcc_repetitions[i] > 1){
                subcc1_filters = this.subcc_predicates.get(subs[i].name()).get(subcc_repetitions[i]);
                //System.out.println(subs[i].name() + ":" + subcc1_filters.toString());
                int[] positions = this.find_subcc_positions_in_combination(i + 1, subcc_repetitions[i], possible_comb);
                
                //calculate cartesian product between any existing different subclasses with N > 1
                for(String subcc1_filter : subcc1_filters){
                     
                    for(var j = i+1; j < subcc_repetitions.length; j++){
                        ArrayList<String> subcc2_filters = this.subcc_predicates.get(subs[j].name()).get(subcc_repetitions[j]);
                        int[] positions2 = this.find_subcc_positions_in_combination(j + 1, subcc_repetitions[j], possible_comb);
                        
                        if(subcc_repetitions[j] > 1){
                            String res_filter;
                            
                            if(!calculated_cp.contains(String.valueOf(j) + String.valueOf(i))){
                                calculated = true;
                                
                                for(String subcc2_filter : subcc2_filters){
                                    res_filter = this.change_filter_var_indices(subcc1_filter, positions) + " && " + this.change_filter_var_indices(subcc2_filter, positions2);
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
     * @param filter filter that we want to replace its variables indices corresponding to a certain subclass positions in combination
     * @param positions array of available positions
     * @return the modified filter
     */
    private String change_filter_var_indices(String filter, int[] positions){
        Pattern p = Pattern.compile("[\\[](\\d)[\\]]");
        Matcher m = p.matcher(filter);
        
        while(m.find()){
            String prev_index = m.group(1);
            filter = filter.replaceFirst(prev_index, String.valueOf(positions[Integer.parseInt(prev_index)]));
        }
        
        return filter;
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
        int K;
        
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
    
    /**
     * 
     * @param part *
     * @param assigned *
     * @param taken *
     * @param maxgroup *
     * @param N multiplicity of a sub-colour class
     * @param multi_predicates an initial empty array list that will be filled recursively
     * @param cc_name the name of colour class, usable to create filters
     * @return array list of all complementary filters available
     */
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
                filter += " && ";
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
     * @throws java.lang.Exception
     */
    public static PartialGenerator get_instance() throws Exception{
        
        if(instance == null){
            instance = new PartialGenerator();
        }
        
        return instance;
    }
}
