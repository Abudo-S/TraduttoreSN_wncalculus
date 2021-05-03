/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author dell
 */
public class PartialGenerator_DataTester {
    //single instance
    private static PartialGenerator_DataTester instance = null;
    
    private PartialGenerator_DataTester(){
        
    }
    
    /**
     * 
     * @param p_name the name of place before unfolding 
     * @param all_places_combs_filter HashMap of all generated places with their corresponding filters
     */
    public void print_place_unfolded_places(String p_name, HashMap<String, String> all_places_combs_filter){
        System.out.println("the unfolding of " + p_name + ": ");
        
        all_places_combs_filter.keySet().stream().forEach(
                place -> System.out.println(place + " <---> " + all_places_combs_filter.get(place))
        );
        
        System.out.println("--------------");
    }
    /**
     * 
     * @param element_data ArraysList of all element's data that will be printed
     */
    public void print_element_data(ArrayList<String> element_data){
        System.out.println("element_data: " + Arrays.toString(element_data.toArray()));    
    }
    
    /**
     * 
     * @param cc_internal_combs_filters HashMap of possible combinations a their corresponding ArrayList of different filters
     */
    public void print_cc_combined_filters_combs(HashMap<String, ArrayList<String>> cc_internal_combs_filters){
        cc_internal_combs_filters.keySet().stream().forEach(
                pc -> {
                    System.out.println(pc + "------");
                    ArrayList<String> cc_pf = cc_internal_combs_filters.get(pc);
                    System.out.println(pc + "," + Arrays.toString(cc_pf.toArray()));
                }
        );
    }
    /**
     * 
     * @param cd_base_filters HashMap of all colour classes in a certain cd with their corresponding base filters 
     */
    public void print_cd_base_filters(HashMap<String, ArrayList<String>> cd_base_filters){
        cd_base_filters.keySet().stream().forEach(
                cc -> this.print_cc_filters_combs(cd_base_filters.get(cc), cc, false)
        );
    }
    /**
     * 
     * @param bases ArraysList of all base filters/possible combinations generated of a certain colour class
     * @param cc_name the name of colour class
     * @param isComb true if the passed list contains possible combinations, false if contains base filters
     */
    public void print_cc_filters_combs(ArrayList<String> bases, String cc_name, boolean isComb){
        
        if(isComb){
            System.out.println("possible combinations of " + cc_name + ": " + Arrays.toString(bases.toArray()));    
        }else{
            System.out.println("base filters of" + cc_name + ": " + Arrays.toString(bases.toArray()));    
        }
    }
    /**
     * 
     * @return single static instance
     */
    public static PartialGenerator_DataTester get_instance(){
        
        if(instance == null){
            instance = new PartialGenerator_DataTester();
        }
        
        return instance;
    }
}
