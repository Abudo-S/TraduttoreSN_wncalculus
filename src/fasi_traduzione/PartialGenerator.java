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
    //usable maps to prevent redundant calculations/combinations of colour classes of a colour domain cd
    private HashMap<String, HashMap<String, ArrayList<String>>> cd_possible_combs; //es. colour-domain name ->{colour-class name -> 11, 12, ...}
    private HashMap<String, HashMap<Integer, ArrayList<String>>> cc_base_filters; //base filters of a colour class that follows ei multiplicity
    private HashMap<String, HashMap<Integer, ArrayList<ArrayList<String>>>> subcc_predicates; //predicates of each subcc for 1 <= n <= ei, Note: each filter's predicates are in the same list
    //single instance
    private static PartialGenerator instance = null;
    
    private PartialGenerator(){
        this.cd_possible_combs = new HashMap<>();
        this.cc_base_filters = new HashMap<>();
        this.subcc_predicates = new HashMap<>();
    }
    
    public void unfold_place(Place p){
        //to be completed
        //apply cartesian product on cd colour classes combinations & name resulting places with colour class name + combination, colour class name + combination ... (following cartesian product combinations)
        //combine each colour class base filter predicate(s) with each subclass predicate(s) in case of finding combination with N > 1 subclass-repetitions
        //create resulting place connected to their filtered arcs
    }
        
    /**
     * Note: unfolding a colour class of multiplicity > 9 in cd, may cause exceptions because of using one digit char while producing combinations
     * @param multiplied_cc HashMap of all colour classes and their multiplicities in a certain colour domain
     * @return HashMap of all colour classes with their combinations present in colour domain cd on which cartesian product will be applied
     */
    public HashMap<String, ArrayList<String>> unfold_colordomain(HashMap<ColorClass, Integer> multiplied_cc, String cd_name){ //prodotto di C_i^ei per i=1..n
        
        if(this.cd_possible_combs.containsKey(cd_name)){
            return this.cd_possible_combs.get(cd_name);
        }
        
        HashMap<String, ArrayList<String>> cd_base_filters = new HashMap<>();
        
        multiplied_cc.keySet().stream().forEach(
                cc -> {
                    cd_base_filters.put(cc.name(), this.unfold_multiplied_cc(cc, multiplied_cc.get(cc), cd_name));
                }
        );
        
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
    public ArrayList<String> unfold_multiplied_cc(ColorClass cc, int multiplicity, String cd_name) throws UnsupportedElementNameException, RuntimeException{ //C_i^ei
        Interval[] subs = cc.getConstraints();
        
        if(subs.length == 1 && subs[0].name().equals("Undefined interval")){ //case of unnamed single sub-colour-class
            //modify the single subclass name with parent colour class name
            subs[0].set_name(cc.name());
        }
        
        ArrayList<String> base_filters = new ArrayList<>();
        
        if(multiplicity == 1){ //C_i^1
            //generate n filters equal to the number of subclasses in C_i
            for(var i = 0; i < subs.length; i++){
                String subcc_name = subs[i].name();
                
                if(subcc_name.equals("Undefined interval")){
                    throw new UnsupportedElementNameException("Sub-colour-class name isn't defined yet! :" + subs[i].toString());
                }
                
                base_filters.add("@" + cc.name() + "[0] in " + subs[i].name());
            }
            
        }else if(this.cd_possible_combs.containsKey(cd_name) && this.cd_possible_combs.get(cd_name).containsKey(cc.name())){ //check if cc's combinations are already calculated before
            HashMap<Integer, ArrayList<String>> cc_mult_base_filters = this.cc_base_filters.get(cc.name()); 
            
            if(cc_mult_base_filters.containsKey(multiplicity)){
                return cc_mult_base_filters.get(multiplicity);
            }else{
                throw new RuntimeException("Can't find pre-calculated base filters of: " + cc.name());
            }
            
        }else{ //C_i^ei, ei > 1
            //generate all possible combinations of subclasses
            int combs_subcc = (int) Math.pow(subs.length, multiplicity) / subs.length; //all combinations/number of subclasses in cc
            ArrayList<String> cc_possible_combs = new ArrayList<>();
            
            String comb;
            for(var i = 0; i < subs.length; i++){
                comb = String.valueOf(i + 1);
                
                for(var j = 0; j < combs_subcc; j++){
                    int char_index = -1;

                    for(var k = multiplicity; k > 1; k--){ //size of each combination without first integer that is known as subclass index + 1
                        
                        if(comb.length() < multiplicity){ //first assignment of combination should equal the index of sublass+1 + "11111..."
                            comb = comb + "1";
                        }else{ //first combination is already added
                            char_index = k - 1;
                            int comb_num = Integer.parseInt(String.valueOf(comb.charAt(char_index)));
                            
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
            
            HashMap<String, ArrayList<String>> possible_combs;
            if(!this.cd_possible_combs.containsKey(cd_name)){
                possible_combs = new HashMap<>();
            }else{
                possible_combs = this.cd_possible_combs.get("cd_name");   
            }
            possible_combs.put(cc.name(), cc_possible_combs); 
            this.cd_possible_combs.put(cd_name, possible_combs);
        }
        
//        System.out.println(cc.name() + "," + multiplicity);
//        base_filters.stream().forEach(
//                base_filter -> System.out.println(base_filter)
//        );
        //create CS
        //ArrayList<Interval> CS = this.create_CS(cc, multiplicity);
        
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
    
    public ArrayList<String> find_cc_base_filters(ColorClass cc, ArrayList<String> cc_possible_combs){ //uses colour class possilbe combinations
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
                            this.unfold_sub_cc(subs[subcc_index], N, cc.name());
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

        HashMap<Integer, ArrayList<ArrayList<String>>> N_predicates;
        
        if(this.subcc_predicates.containsKey(subcc.name())){
            N_predicates = this.subcc_predicates.get(subcc.name());
        }else{
            N_predicates = new HashMap<>();
        }
        
        if(!N_predicates.containsKey(N)){
            ArrayList<ArrayList<String>> multi_predicates = this.assign(vg, 1, 1, min(N, this.calculate_K(subcc)), N, new ArrayList<>(), cc_name);
            N_predicates.put(N, multi_predicates);
            this.subcc_predicates.put(subcc.name(), N_predicates);
        }
    }
    
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
    public static PartialGenerator get_instance(){
        
        if(instance == null){
            instance = new PartialGenerator();
        }
        
        return instance;
    }
}
