/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.ArrayList;
import java.util.List;
import wncalculus.expr.Domain;
import wncalculus.color.ColorClass;

/**
 *
 * @author dell
 */
//singleton
public class SN {
    
    private static ArrayList<Place> P; //hashmap con domini
    private static ArrayList<Transition> T; //hashmap con variabili
    private static ArrayList<ColorClass> C;
    private static ArrayList<Domain> DC;
    private static ArrayList<Variable> V;
    private static Marking m0;
    //single instance
    private static SN instance = null;
    
    private SN(){
        P = new ArrayList<>();
        T = new ArrayList<>();
        C = new ArrayList<>(List.of(new ColorClass("Neutral"))); //C.get(0) is the Neutral colour
        DC = new ArrayList<>();
        V = new ArrayList<>();
        m0 = Marking.get_instance();
    }
    
    public void add_place(Place p){
        SN.P.add(p);
    }
    
    public void add_transition(Transition t){
        SN.T.add(t);
    }
    
    public void add_colorClass(ColorClass c){
        SN.C.add(c);
    }
    
    public void add_domain(Domain d){
        SN.DC.add(d);
    }
    
    public void add_variable(Variable v){
        SN.V.add(v);
    }
      
    public void set_initial_marking(Marking m0){
        SN.m0 = m0;
    }
    
    public Place find_place(String place_name){
        
        for (Place p : SN.P) {
            if(p.get_name().equals(place_name)){
                return p;
            }
        }
        return null;
    }
    
    public Transition find_transition(String transition_name){
        
        for(Transition t : SN.T){
            if(t.get_name().equals(transition_name)){
                return t;
            }
        }
        return null;
    }
    
    public ColorClass find_colorClass(String name){
        
        for(ColorClass c : SN.C){
            if(c.name().equals(name)){
                return c;
            }
        }
        return null;
    }
    
    public Domain find_domain(String domain_name){
        
        for(Domain d : SN.DC){
            if(d.name().equals(domain_name)){
                return d;
            }
        }
        return null;
    }
    
    public Variable find_variable(String variable_name){
        
        for(Variable v : SN.V){
            if(v.get_name().equals(variable_name)){
                return v;
            }
        }
        return null;
    }
    
    public ArrayList<Place> get_P(){
        return P;
    }
    
    public ArrayList<Transition> get_T(){
        return T;
    }
     
    public ArrayList<ColorClass> get_C(){
        return C;
    }
    
    public ArrayList<Domain> get_DC(){
        return DC;
    }
     
    public ArrayList<Variable> get_V(){
        return V;
    }
    
    public Marking get_initial_marking(){
        return SN.m0;
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
    
    public void update_variable_via_projection(Variable v){
        try{
            for(var i = 0; i < V.size(); i++){
                
                if(V.get(i).get_name().equals(v.get_name())){
                    V.set(i, v);
                    break;
                }
            }
            
        }catch(Exception e){
            System.out.println(e + " while update place: " + v.get_name());
        }
    }
    
    public void update_place(Place p){
        try{
            for(var i = 0; i < P.size(); i++){
                
                if(P.get(i).get_name().equals(p.get_name())){
                    P.set(i, p);
                    break;
                }
            }
            
        }catch(Exception e){
            System.out.println(e + " while update place: " + p.get_name());
        }
    }
    
    public void update_transition(Transition t){
        try{
            for(var i = 0; i < T.size(); i++){
                
                if(T.get(i).get_name().equals(t.get_name())){
                    T.set(i, t);
                    break;
                }
            }
            
        }catch(Exception e){
            System.out.println(e + " while update place: " + t.get_name());
        }
    }
    
//    public static void update_instance(SN ins){
//        instance = ins;
//    }
    
    public static SN get_instance(){
        
        if(instance == null){
            instance = new SN();
        }
        
        return instance;
    }
}
