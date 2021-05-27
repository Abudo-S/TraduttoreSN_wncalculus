/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package traduttoresn_wncalculus;

import componenti.Token_estimator;
import fasi_traduzione.*;
import eccezioni.UnsupportedFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import test.SN_DataTester;
import test.SyntaxTree_DataTester;
import java.util.Scanner;
import struttura_sn.Marking;
import struttura_sn.SN;
import struttura_sn.Token;
import test.Semantic_DataTester;
import wncalculus.expr.Interval;
import wncalculus.wnbag.LinearComb;

/**
 *
 * @author dell
 */
public class TraduttoreSN_wncalculus {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        //test
        XMLScanner xml_sc = XMLScanner.get_instance(scan_file_name());
        xml_sc.scan_file_data();
        SyntaxTree_DataTester st_dt = SyntaxTree_DataTester.get_instance(); //for data testing
        //st_dt.SyntaxTree_all_data();
        SemanticAnalyzer sa = SemanticAnalyzer.get_instance();
        sa.set_syntax_tree(DataParser.get_syntax_tree());
        sa.analyze_syntax_tree();
        //Semantic_DataTester.get_instance().print_all_proj_indices();
        
        SN_DataTester sn_dt = SN_DataTester.get_instance(); //for data testing
        sn_dt.SN_all_data();
        //sn_dt.print_nodes_connections();
        sn_dt.print_transitions_connections();
        
        //estimate/find color tokens of all colour classes
        //estimate_all_cc_tokens();
        
        //apply unfolding algorithm
        System.out.println("----------------");
        PartialGenerator pg = PartialGenerator.get_instance();
        pg.unfold_all_places(); 
        //write original SN
        write_original_SN(pg);
    }
    
    /**
     * prints all estimated/found tokens
     */
    private static void estimate_all_cc_tokens(){
        //estimate all colorclasses tokens
        Token_estimator te = Token_estimator.get_instance();
        SN.get_instance().get_C().stream().forEach(
                cc -> {
                    print_estimated_tokens(cc.name(), te); 
                }
        );
        
        //estimate all sub-colorclasses colorclasses tokens
        SN.get_instance().get_C().stream().forEach(
                cc -> {
                    Interval[] subccs = cc.getConstraints();
                    
                    Arrays.stream(subccs).forEach(
                        subcc -> {
                            String subcc_name = subcc.name();
                            
                            if(!subcc_name.contains("Undefined interval")){
                                print_estimated_tokens(subcc_name, te); 
                            }
                            
                        } 
                    );
                }
        );
    }
    /**
     * 
     * @return String of scanned file name
     * @throws UnsupportedFileException if the address of file given doesn't have extension "pnml"
     */
    private static String scan_file_name() throws UnsupportedFileException{
        String file_name = "CPN 1.pnml";
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file name with ext (.pnml) to be translated into an unfolded symmetric net:");
        System.out.println("Note: if you want to use the default file name 'CPN 1.pnml', press \"1\":");        
        String str_name = sc.nextLine();
        
        if(!str_name.equals("1")){
            
            if(!str_name.contains("pnml")){
                throw new UnsupportedFileException("can't use a non pnml file!");
            }
            
            file_name = str_name;
        }
        
        return file_name;
    }
    
    /**
     * write all SN original components as a gspn in PNPRO file
     * @param pg partial generator object that will be used to write original SN components
     */
    private static void write_original_SN(PartialGenerator pg) throws Exception{
        SN sn = SN.get_instance();
        Marking mk = Marking.get_instance();
        int[] place_xy = new int[]{40, 20}; //graphics points
        int[] y_multiplier = new int[]{1};
        XMLWriter xmlwriter = XMLWriter.get_instance("original");
        //reset places pnpro list
        xmlwriter.reset_places_data_pnpro();
        //reset arcs pnpro list
        xmlwriter.reset_arcs_data_pnpro();
        
        sn.get_P().stream().forEach(
            place -> {
                //write place
                ArrayList<String> place_data = new ArrayList<>();
                HashMap<ArrayList<LinearComb>, Integer> initial_marking = mk.get_marking_of_place(place.get_name());
                //copy intitial marking of place in all unfolded places
                String marking;
                place_data.add("name@=" + place.get_name());
                place_data.add("domain@=" + place.get_type());

                if(!initial_marking.isEmpty()){
                   marking = pg.get_place_initial_marking(initial_marking);
                   place_data.add("marking@=" + marking);
                }
                place_xy[1] += 20;
                place_data.add("graphics@=" + "x=" + place_xy[0] + "y=" + String.valueOf(place_xy[1] - y_multiplier[0] * 10));
                y_multiplier[0]++;
                //add place to be written in pnpro
                xmlwriter.add_place(place_data, true);

                //write place arcs with unfolded filter
                pg.write_unfolded_place_arcs(place, "", "");
            }            
        );
        xmlwriter.write_all_data_pnpro(true);
        
    }
    /**
     * 
     * @param cc_subcc_name colour class/sub colour-class that we want to estimates its tokens
     * @param te Token_estimator that will be used for the process of estimation
     */
    private static void print_estimated_tokens(String cc_subcc_name, Token_estimator te){
        ArrayList<Token> tokens = te.get_estimated_cc_tokens(cc_subcc_name);
        
        System.out.println("Tokens(cc/subcc) of " + cc_subcc_name + ":");
        tokens.stream().forEach(
                token -> System.out.println(token.get_Token_value())
        );
    }
    
}
