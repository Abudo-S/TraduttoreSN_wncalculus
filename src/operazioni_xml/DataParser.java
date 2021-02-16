/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operazioni_xml;

import java.util.*;
import struttura_sn.*;

/**
 *
 * @author dell
 */
public class DataParser {
    private static SN sn = new SN();
    
    public DataParser(){
       
    }
    
    public void add_ColorClass(String class_name, int start, int end, boolean circular){
        
    }
      
    public void add_ColorClass(String class_name, ArrayList token_names, boolean circular){
          
    }
    
    public void add_ColorClass(String class_name, HashMap<String, ArrayList<String>> subclasses){
          
    }
    
    public void add_Variable(String variable_name, String variable_type){
        
    }      
//    public void add_colourClass(String class_name, String[] available_tokens, boolean circular){
//        sn.add_colourClass(new ColourClass(class_name, available_tokens, circular));
//    }
//    
//    public void add_colourClass(String class_name, String[] available_tokens, boolean circular,
//    String[] subclasses_names, ArrayList<String[]> subclasses_tokens){
//        ColourClass c = new ColourClass(class_name, available_tokens, circular);
//        
//        for(var i = 0; i < subclasses_names.length; i++){
//            c.add_subclass(c.new SubColourClass(subclasses_names[i], subclasses_tokens.get(i)));
//            sn.add_colourClass(c);
//        }
//    }
//    
//    public void add_domain(String domain_name, String[] Colourclasses_names){
//        sn.add_domain(new Domain(domain_name, this.extract_colourclasses(Colourclasses_names)));
//    }
//    
//    private ColourClass[] extract_colourclasses(String[] Colourclasses_names){
//        ColourClass[] classes = new ColourClass[Colourclasses_names.length];
//        
//        for(var i = 0; i < Colourclasses_names.length; i++){
//            classes[i] = sn.find_colourClass(Colourclasses_names[i]);
//        }
//        return classes;
//    }
//    
//    public void add_variable(String variable_name, String class_name){
//        sn.add_variable(new Variable(variable_name,sn.find_colourClass(class_name)));
//    }
//    
//    public void add_place(String place_name, String ColourClass_name){
//       sn.add_place(new Place(place_name, sn.find_colourClass(ColourClass_name)));
//    }
//    
//    public void add_transition(String transition_name){
//        sn.add_transition(new Transition(transition_name, null));
//    }
//    
//    public void add_transition(String transition_name, boolean invert, ArrayList<Predicate> predicates,
//    ArrayList<String> op_seperation){ //with guard that has no variable tuple
//        sn.add_transition(new Transition(transition_name, this.create_guard(invert, predicates, op_seperation, null)));
//    }
//    
//    //note: Case (normal_arch)-> "from/to" can be a place name or a transiton name
//    //note: Case (inhibitor)-> "from" will be a place name, "to" will be a transition name
//    public void connect_nodes_via_arc(String from, String to, Arc arc) throws NullPointerException{
//        Place p;
//        Transition t;
//        
//        
//        switch(arc.getClass().getName()){
//            
//            case "tarc":   
//                p = sn.find_place(from);
//                if(p == null){
//                    t = sn.find_transition(from);
//                    p = sn.find_place(to);
//                    t.add_next_Node((TArc) arc, p);
//                    sn.update_nodes_via_arc(p, t);
//                }else{ 
//                    t = sn.find_transition(to);
//                    p.add_next_Node((TArc) arc, t);
//                    sn.update_nodes_via_arc(p, t);
//                }
//                break;
//                
//            case "inhibitor":
//                p = sn.find_place(from);
//                t = sn.find_transition(to);
//                p.add_next_Node((Inhibitor) arc, t);
//                sn.update_nodes_via_arc(p, t);
//                break;
//                
//            default:
//                throw new NullPointerException("Arc connecting error: " + from + "," + to);
//        }
//    }
//    
//    //colour arc
//    public Arc create_arc(String arc_type, String arc_name, String[] variables_names ,int[] multiplicity) throws NullPointerException{
//        Arc arc = null;
//        
//         switch(arc_type){
//            
//            case "tarc": 
//                arc = new TArc(arc_name, 0); //lvl 0 will be modified 
//                arc = (TArc) this.add_arc_muliplicity(arc, variables_names, multiplicity);
//                break;
//                
//            case "inhibitor":
//                arc = new Inhibitor(arc_name, 0); //lvl 0 will be modified
//                arc = (Inhibitor) this.add_arc_muliplicity(arc, variables_names, multiplicity);
//                break;  
//         }
//         
//         if(arc == null){
//             throw new NullPointerException("Arc type isn't found: "+arc_type);
//         }
//        return arc;
//    }
//    
//    //domain arc
//    public Arc create_arc(String arc_type, String arc_name, String[][] variables_names ,int[] multiplicity) throws NullPointerException{
//        Arc arc = null;
//        
//         switch(arc_type){
//            
//            case "tarc": 
//                arc = new TArc(arc_name, 0); //lvl 0 will be modified 
//                arc = (TArc) this.add_arc_muliplicity(arc, variables_names, multiplicity);
//                break;
//                
//            case "inhibitor":
//                arc = new Inhibitor(arc_name, 0); //lvl 0 will be modified
//                arc = (Inhibitor) this.add_arc_muliplicity(arc, variables_names, multiplicity);
//                break;  
//         }
//         
//         if(arc == null){
//             throw new NullPointerException("Arc type isn't found: "+arc_type);
//         }
//        return arc;
//    }
//    
//    public Arc add_arc_guard(Arc arc, boolean invert, ArrayList<Predicate> predicates, ArrayList<String> op_seperation, Variable[] tuple){
//        arc.add_guard_colourClassORdomain(this.create_guard(invert, predicates, op_seperation, tuple));
//        
//        return arc;
//    }
//    
//    private Guard create_guard(boolean invert, ArrayList<Predicate> predicates, ArrayList<String> op_seperation, Variable[] tuple){
//        Guard g = new Guard(invert, tuple);
//              g.set_Allpredicates(predicates);
//              g.set_Allseparations(op_seperation);
//        return g;
//    }
//    
//    private Arc add_arc_muliplicity(Arc arc, String[] variables_names ,int[] multiplicity){
//        
//        for(var i = 0; i< multiplicity.length; i++){
//            arc.add_mult_varOfcolourClass(sn.find_variable(variables_names[i]), multiplicity[i]);
//        }
//        return arc;
//    }
//    
//    private Arc add_arc_muliplicity(Arc arc, String[][] variables_names ,int[] multiplicity){
//        Variable[] vars;
//        
//        for(var i = 0; i< variables_names.length; i++){
//            vars = new Variable[variables_names[0].length];
//            
//            for(var j = 0; j< variables_names[i].length; j++){
//                vars[j] = sn.find_variable(variables_names[i][j]);
//            }
//            arc.add_mult_varsOfdomain(vars, multiplicity[i]);
//        }
//        return arc;
//    }
//    
//    //for coloured/neutral places
//    public void add_initial_Marking(String place_name, String[] tokens_names, int[] multiplicity){
//        try{
//            Place p = sn.find_place(place_name);
//            ColourClass place_typeC = sn.find_colourClass(p.get_type());
//            Token[] tokens = new Token[tokens_names.length];
//
//            for(var i = 0; i< tokens.length; i++){
//                tokens[i] = place_typeC.find_token(tokens_names[i]);
//                tokens[i].set_current_place(p);
//            }
//
//            Marking m0 = new Marking();
//            m0.mark_place(p, tokens, multiplicity);
//            sn.set_initial_marking(m0);   
//            
//        }catch(Exception e){
//            System.out.println(e + " ,Can't mark " + place_name);
//        }
//    }
//    
//    //for domained places
//    public void add_initial_marking(String place_name, String[][] tokens_names, int[] multiplicity){  
//        try{
//            Place p = sn.find_place(place_name);
//            ColourClass place_typeC = sn.find_colourClass(p.get_type());
//            Token[][] tokens = new Token[tokens_names.length][tokens_names[0].length];
//            
//            for(var i = 0; i< tokens.length; i++){
//                for(var j = 0; j< tokens[i].length; j++){
//                    tokens[i][j] = place_typeC.find_token(tokens_names[i][j]);
//                    tokens[i][j].set_current_place(p);
//                }
//            }
//            Marking m0 = new Marking();
//            m0.mark_place(p, tokens, multiplicity);
//            sn.set_initial_marking(m0);   
//            
//        }catch(Exception e){
//            System.out.println(e + " ,Can't mark " + place_name);
//        }
//    }
//    
//    
//    public SN get_sn(){
//        return sn;
//    }
}
