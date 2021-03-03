/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import wncalculus.expr.Domain;

/**
 *
 * @author dell
 */
public class SN {
    
    private static ArrayList<Place> P = new ArrayList<>();
    private static ArrayList<Transition> T = new ArrayList<>();
    private static ArrayList<ColourClass> C = new ArrayList<>(List.of(new ColourClass("Neutral"))); //C.get(0) is the Neutral colour
    private static ArrayList<Domain> DC = new ArrayList<>();
    private static ArrayList<Variable> V = new ArrayList<>();
    private static Marking m0;
    //single instance
    private static SN instance = null;
    
    public SN(){
        
    }
    
    public void add_place(Place p){
        SN.P.add(p);
    }
    
    public void add_transition(Transition t){
        SN.T.add(t);
    }
    
    public void add_colorClass(ColourClass c){
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
    
    public ColourClass find_colorClass(String name){
        
        for(ColourClass c : SN.C){
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
    
    public Marking get_initial_marking(){
        return SN.m0;
    }
    
    public void update_nodes_via_arc(Place p, Transition t){
        
        try{
            P = (ArrayList<Place>) P.stream()
                    .filter(place -> place.get_name().equals(p.get_name()))
                    .map(place -> p)
                    .collect(Collectors.toList());

            T = (ArrayList<Transition>) T.stream()
                    .filter(transition -> transition.get_name().equals(t.get_name()))
                    .map(transition -> t)
                    .collect(Collectors.toList());
            
        }catch(Exception e){
            System.out.println(e + " while connecting arcs");
        }
    }
    
    public void SN_all_data(){
        
        try{
            System.out.println("ColourClasses:");
            SN.C.stream().forEach(x -> System.out.println(x.name())); 
            
            System.out.println("Domains:");
            SN.DC.stream().forEach(x -> System.out.println(x.name()));
            
            System.out.println("Variables/Projections:");
            SN.V.stream().forEach(x -> System.out.println(x.get_name()));
            
            System.out.println("Places:");
            SN.P.stream().forEach(x -> System.out.println(x.get_name()));
            
            System.out.println("Transitions:");
            SN.T.stream().forEach(x -> System.out.println(x.get_name()));
            
            System.out.println("initial Marking:");        
            SN.m0.get_all_marked_Places().stream().forEach(x -> System.out.println(x.get_name()));
            
        }catch(Exception e){
            System.out.println(e + " in SN");
        }
    }
    
    public static SN get_instance(){
        
        if(instance == null){
            instance = new SN();
        }
        
        return instance;
    }
}
