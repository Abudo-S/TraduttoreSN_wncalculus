/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.ArrayList;
import java.util.List;
import wncalculus.classfunction.Subcl;
import wncalculus.expr.Domain;
import wncalculus.color.ColorClass;
import wncalculus.expr.Interval;

/**
 *
 * @author dell
 */
//singleton
public class SN {
    
    private static ArrayList<Place> P; //hashmap con domini, ma se il posto e' di tipo colorclass allora il dominio associato sarebbe null!, ma anche il dominio del posto esiste gia' dentro l'oggetto di place
    private static ArrayList<Transition> T; //hashmap con variabili, ma le variabili di una transizione esistono gia' negli oggetti di guardie/tuple
    private static ArrayList<ColorClass> C;
    //devono esistere in un'altra classe?
    private static ArrayList<Domain> DC;
    private static ArrayList<Variable> V;
    //
    private static Marking m0;
    //single instance
    private static SN instance = null;
    
    private SN(){
        P = new ArrayList<>();
        T = new ArrayList<>();
        C = new ArrayList<>(List.of(ColorClass.create_neutral_cc("Neutral"))); //C.get(0) is the Neutral colour
        DC = new ArrayList<>();
        V = new ArrayList<>();
        m0 = Marking.get_instance();
    }
    
    /**
     * 
     * @param p the place that will be added to places list
     */
    public void add_place(Place p){
        SN.P.add(p);
    }
    
    /**
     * 
     * @param t the transition that will be added to transitions list
     */
    public void add_transition(Transition t){
        SN.T.add(t);
    }
    
    /**
     * 
     * @param c the colour class that will be added to colour classes list
     */
    public void add_colorClass(ColorClass c){
        SN.C.add(c);
    }
    
    /**
     * 
     * @param d the domain that will be added to domains list
     */
    public void add_domain(Domain d){
        SN.DC.add(d);
    }
    
    /**
     * 
     * @param v the variable that will be added to variables list
     */
    public void add_variable(Variable v){
        SN.V.add(v);
    }
    
    /**
     * 
     * @param m0 the initial marking of some places existing in places list
     */
    public void set_initial_marking(Marking m0){
        SN.m0 = m0;
    }
    
    /**
     * 
     * @param place_name the name of place that we want to search
     * @return place p if found, null otherwise
     */
    public Place find_place(String place_name){
        
        for (Place p : SN.P) {
            if(p.get_name().equals(place_name)){
                return p;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param transition_name the name of transition that we want to search
     * @return transition t if found, null otherwise
     */
    public Transition find_transition(String transition_name){
        
        for(Transition t : SN.T){
            if(t.get_name().equals(transition_name)){
                return t;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param name the name of colour class that we want to search
     * @return colour class c if found, null otherwise
     */
    public ColorClass find_colorClass(String name){
        
        for(ColorClass c : SN.C){
            if(c.name().equals(name)){
                return c;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param name the name of colour class that we want to search
     * @return sub colour-class sub if found, null otherwise
     */
    public Interval find_subcolorclass(String name){
        
        for(ColorClass c : SN.C){
            Interval[] intervals = c.getConstraints();
            
            for (Interval interval : intervals) {
                
                if (interval.name().equals(name)) {
                    return interval;
                }
            }
        }
        
        return null;
    }
    
    /**
     * 
     * @param domain_name the name of domain that we want to search
     * @return domain d if found, null otherwise
     */
    public Domain find_domain(String domain_name){
        
        for(Domain d : SN.DC){
            if(d.name().equals(domain_name)){
                return d;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param variable_name the name of variable that we want to search
     * @return variable v if found, null otherwise
     */
    public Variable find_variable(String variable_name){
        
        for(Variable v : SN.V){
            if(v.get_name().equals(variable_name)){
                return v;
            }
        }
        return null;
    }
    
    /**
     * 
     * @return ArrayList of Places
     */
    public ArrayList<Place> get_P(){
        return P;
    }
    
    /**
     * 
     * @return ArrayList of transitions
     */
    public ArrayList<Transition> get_T(){
        return T;
    }
     
    /**
     * 
     * @return ArrayList of colour classes
     */
    public ArrayList<ColorClass> get_C(){
        return C;
    }
    
    /**
     * 
     * @return ArrayList of domains
     */
    public ArrayList<Domain> get_DC(){
        return DC;
    }
     
    /**
     * 
     * @return ArrayList of variables
     */
    public ArrayList<Variable> get_V(){
        return V;
    }
    
    /**
     * 
     * @return the initial marking pre-added
     */
    public Marking get_initial_marking(){
        return m0;
    }
    
//    public void update_nodes_via_arc(Place p, Transition t){
//        
//        try{
//            P = (ArrayList<Place>) P.stream()
//                    .filter(place -> place.get_name().equals(p.get_name()))
//                    .map(place -> p)
//                    .collect(Collectors.toList());
//
//            T = (ArrayList<Transition>) T.stream()
//                    .filter(transition -> transition.get_name().equals(t.get_name()))
//                    .map(transition -> t)
//                    .collect(Collectors.toList());
//            
//        }catch(Exception e){
//            System.out.println(e + " while connecting arcs");
//        }
//    }
    
    /**
     * 
     * @param v the variable that we want to add to variables list and will replace an existing variable by its name
     */
    public void update_variable_via_projection(Variable v){
        try{
            for(var i = 0; i < V.size(); i++){
                
                if(V.get(i).get_name().equals(v.get_name())){
                    V.set(i, v);
                    break;
                }
            }
            
        }catch(Exception e){
            System.out.println(e + " while updating variable: " + v.get_name());
        }
    }
    
    /**
     * 
     * @param p the place that we want to add to places list and will replace an existing place by its name
     */
    public void update_place(Place p){
        try{
            for(var i = 0; i < P.size(); i++){
                
                if(P.get(i).get_name().equals(p.get_name())){
                    P.set(i, p);
                    break;
                }
            }
            
        }catch(Exception e){
            System.out.println(e + " while updating place: " + p.get_name());
        }
    }
    
    /**
     * 
     * @param t the transition that we want to add to transitions list and will replace an existing transition by its name
     */
    public void update_transition(Transition t){
        try{
            for(var i = 0; i < T.size(); i++){
                
                if(T.get(i).get_name().equals(t.get_name())){
                    T.set(i, t);
                    break;
                }
            }
            
        }catch(Exception e){
            System.out.println(e + " while updating transition: " + t.get_name());
        }
    }
    
//    public static void update_instance(SN ins){
//        instance = ins;
//    }
    
    /**
     * 
     * @return single static instance
     */
    public static SN get_instance(){
        
        if(instance == null){
            instance = new SN();
        }
        
        return instance;
    }
}
