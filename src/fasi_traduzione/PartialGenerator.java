/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import wncalculus.color.ColorClass;
import struttura_sn.Place;

/**
 *
 * @author dell
 */
//singleton
//will be a part of factory pattern with (place_partial_unfolding)
public class PartialGenerator {
    //single instance
    private static PartialGenerator instance = null;
    
    private PartialGenerator(){
        
    }
    
    public void unfold_colordomain(){ //prodotto di C_i^ei per i=1..n
        //uses unfold_multiplied_cc()
        //to be completed
    }
    
    /**
     * 
     * @param cc the colour class that we want to unfold its subclasses
     * @param multiplicity the multiplicity of colour class in a certain cd/cc itself
     */
    public void unfold_multiplied_cc(ColorClass cc, int multiplicity){ //C_i^ei
        //to be completed
        int N = 2;  // numero variabili
        int K = 3;  // dimensione sottoclasse statica
        vargrp vg = new vargrp();
        vg.V[1] = 1; // prima variabile assegnata al primo gruppo
        vg.grp[1][0] = 1; // primo gruppo ha 1 variabile
        vg.grp[1][1] = 1; // la prima variabile del gruppo 1 è la 1

        assign(vg, 1, 1, min(N, K), N);
    }
    
    public void unfold_place(Place p){
        //to be completed
    }
    
    private void assign(vargrp part, int assigned, int taken, int maxgroup, int N){
        int i,j;
        
        if(assigned == N){// stampa l'assegnazione variabile - sottoinsieme
            System.out.printf("\n");
            
            for(i = 1;i <= N; i++)
              System.out.printf("v%d in grp %d\n",i,part.V[i]);
            for(i = 1; i <= taken; i++){ //printf("g%d : ",i);
                
              for(j = 2; j <= part.grp[i][0]; j++)
                System.out.printf("(v%d == v%d)",part.grp[i][1],part.grp[i][j]);
              
              if(part.grp[i][0] > 1) System.out.printf(" \n");
            }
            for(i = 1; i <= taken; i++)
               for (j = i+1; j <= taken; j++)
                System.out.printf("(v%d != v%d)",part.grp[i][1],part.grp[j][1]);
              System.out.printf(" \n");
            }else{ // richiamo ricorsivo
                int g, newtaken;
                assigned++;

                for(g = 1; g <= min(taken+1,maxgroup); g++){
                   part.V[assigned] = g;
                   part.grp[g][0]++;
                   part.grp[g][part.grp[g][0]] = assigned;

                   if(g > taken) newtaken = g; else newtaken = taken;

                   this.assign(part, assigned, newtaken, maxgroup, N);
                   part.grp[g][part.grp[g][0]] = 0;
                   part.grp[g][0]--;
            }
        }
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
