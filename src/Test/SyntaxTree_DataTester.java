/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import Albero_sintattico.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author dell
 */
//singleton
public class SyntaxTree_DataTester {
    //single instance
    private static SyntaxTree_DataTester instance = null;
    
    private SyntaxTree_DataTester(){
        
    }
    
    public void SyntaxTree_all_data(){
        
    }
    
    public static SyntaxTree_DataTester get_instance(){
        
        if(instance == null){
            instance = new SyntaxTree_DataTester();
        }
        
        return instance;
    }
}
