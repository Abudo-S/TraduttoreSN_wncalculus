/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import struttura_sn.Marking;
import struttura_sn.SN;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.expr.Sort;
import wncalculus.guard.Guard;
import wncalculus.wnbag.LinearComb;

/**
 *
 * @author dell
 */
//singleton
public class SN_DataTester {
    
    private static SN_DataTester instance = null;
    
    public void SN_all_data(){
        SN sn = SN.get_instance();
       
        try{
            System.out.println("ColorClasses:");
            sn.get_C().stream().forEach(x -> System.out.println(x.name())); 
            
            System.out.println("Domains:");
            sn.get_DC().stream().forEach(
                    x -> {
                        System.out.print(x.name() + ": ");
                        Map<? extends Sort,Integer> ccs = x.asMap();
                        
                        ccs.keySet().stream().forEach(
                                cc -> System.out.print(ccs.get(cc) + "*" + cc.name() + " ")
                        );
                        
                        System.out.println();
                    });
            
            System.out.println("Variables:");
            sn.get_V().stream().forEach(x -> System.out.println(x.get_name() + ": " + x.get_colourClass().name()));
            
            System.out.println("Places:");
            sn.get_P().stream().forEach(x -> System.out.println(x.get_name()));
            
            System.out.println("Transitions:");
            sn.get_T().stream().forEach(
                    x -> {
                        System.out.print(x.get_name());   
                        Guard g = x.get_guard();
                        
                        if(g != null){
                            System.out.print(": " + g.toString());
                        }
                        
                        System.out.println();
                    }
            );
            
            System.out.println("initial Marking:");    
            Marking m0 = sn.get_initial_marking();
            
            m0.get_all_marked_Places().stream().forEach(
                    x -> {
                        System.out.print(x.get_name() + ": ");
                        
                        HashMap<ArrayList<LinearComb>, Integer> x_mark = m0.get_marking_of_place(x);
                        x_mark.keySet().stream().forEach(
                                comb_list -> {
                                        System.out.print(x_mark.get(comb_list) + " *[");
                                        
                                        comb_list.stream().forEach(
                                                comb_element -> this.print_linear_comb(comb_element)
                                        );
                                }
                        );
                        
                        System.out.println("]");
                    }
            );
            
        }catch(Exception e){
            System.out.println(e + " in SN_DataTester");
        }
    }
    
    private void print_linear_comb(LinearComb comb){
        Map<? extends ElementaryFunction, Integer> comb_map = comb.asMap();
        System.out.print("(");
        
        comb_map.keySet().stream().forEach(
                comb_map_element -> {
                    System.out.print(comb_map.get(comb_map_element) + " * ");
                    System.out.print(comb_map_element.getClass().getName());
                }
        );
        
        System.out.print(")  ");
    }
    
    public static SN_DataTester get_instance(){
        
        if(instance == null){
            instance = new SN_DataTester();
        }
        
        return instance;
    }
    
}
