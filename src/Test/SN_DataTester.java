/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import struttura_sn.SN;

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
            sn.get_DC().stream().forEach(x -> System.out.println(x.name()));
            
            System.out.println("Variables:");
            sn.get_V().stream().forEach(x -> System.out.println(x.get_name()));
            
            System.out.println("Places:");
            sn.get_P().stream().forEach(x -> System.out.println(x.get_name()));
            
            System.out.println("Transitions:");
            sn.get_T().stream().forEach(x -> System.out.println(x.get_name()));
            
            System.out.println("initial Marking:");        
            sn.get_initial_marking().get_all_marked_Places().stream().forEach(x -> System.out.println(x.get_name()));
            
        }catch(Exception e){
            System.out.println(e + " in SN_DataTester");
        }
    }
    
    public static SN_DataTester get_instance(){
        
        if(instance == null){
            instance = new SN_DataTester();
        }
        
        return instance;
    }
    
}
