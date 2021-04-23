/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import eccezioni.UnsupportedElementNameException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import wncalculus.color.ColorClass;
import struttura_sn.Place;
import wncalculus.expr.Interval;

/**
 *
 * @author dell
 */
//singleton
//will be a part of factory pattern with (struttura_unfolding_parziale)
public class PartialGenerator {
    
    private HashMap<String, ArrayList<String>> domain_possible_combs; //es. domain_name/colour class name -> 11, 12, ...
    private HashMap<String, String> comb_filter;
    private HashMap<String, HashMap<Integer, ArrayList<ArrayList<String>>>> subcc_predicates; //predicates of each subcc for 1 <= n <= ei, Note: each filter's predicates are in the same list
    //single instance
    private static PartialGenerator instance = null;
    
    private PartialGenerator(){
        this.domain_possible_combs = new HashMap<>();
        this.comb_filter = new HashMap<>();
        this.subcc_predicates = new HashMap<>();
    }
    
    public void unfold_place(Place p){
        //to be completed
    }
        
    /**
     * 
     * @param multiplied_cc HashMap of all colour classes and their multiplicities in a certain colour domain
     */
    public void unfold_colordomain(HashMap<ColorClass, Integer> multiplied_cc){ //prodotto di C_i^ei per i=1..n
        //uses unfold_multiplied_cc()
        //to be completed
    }
    
    /**
     * 
     * @param cc the colour class that we want to unfold its subclasses
     * @param multiplicity the multiplicity of colour class in a certain cd/cc itself
     * @param cc_index the index of colour class in a certain colour domain based on place syntax
     */
    public void unfold_multiplied_cc(ColorClass cc, int multiplicity, int cc_index){ //C_i^ei
        //create CS
        Interval[] subs = cc.getConstraints();
        ArrayList<Interval> CS = this.create_CS(cc, multiplicity);
        //to be completed
    }
    
    /**
     * 
     * @param subcc the subclass that we want to know its predicates using function assign
     * @param N should be > 1 to call this function
     */
    private ArrayList<ArrayList<String>> unfold_sub_cc(Interval subcc, int N, String cc_name){
        vargrp vg = new vargrp();
        vg.V[1] = 1; // prima variabile assegnata al primo gruppo
        vg.grp[1][0] = 1; // primo gruppo ha 1 variabile
        vg.grp[1][1] = 1; // la prima variabile del gruppo 1 è la 1

        return this.assign(vg, 1, 1, min(N, this.calculate_K(subcc)), N, new ArrayList<>(), cc_name);
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
    
    /**
     * 
     * @param cc colour class that we want to extract its subclasses
     * @param multiplicity the multiplicity of colour class in a certain cd/cc itself
     * @return an ArrayList of intervals of size n_subclasses^multiplicity
     * @throws UnsupportedElementNameException if the name of sub-colour-class is undefined
     */
    private ArrayList<Interval> create_CS(ColorClass cc, int multiplicity) throws UnsupportedElementNameException{
        Interval[] subs = cc.getConstraints();
        ArrayList<Interval> CS = new  ArrayList<>();
        
        if(subs.length == 1 && subs[0].name().equals("Undefined interval")){
            //modify the single subclass name with parent colour class name
            subs[0].set_name(cc.name());
        }
        Arrays.stream(subs).forEach(
                subcc -> {

                    for(var i = 0; i < multiplicity; i++){
                        
                        if(subcc.name().equals("Undefined interval")){
                            throw new UnsupportedElementNameException("Sub-colour-class name isn't defined yet! :" + subcc.toString());
                        }
                        CS.add(subcc);
                    }
                }
        ); 
        
        return CS;
    }
    
    private ArrayList<ArrayList<String>> assign(vargrp part, int assigned, int taken, int maxgroup, int N, ArrayList<ArrayList<String>> multi_predicates, String cc_name){
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
            multi_predicates.add(filter_predicates);
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
    public static PartialGenerator get_instance(){
        
        if(instance == null){
            instance = new PartialGenerator();
        }
        
        return instance;
    }
}
