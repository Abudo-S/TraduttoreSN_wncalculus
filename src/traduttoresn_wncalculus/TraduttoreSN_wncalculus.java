/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package traduttoresn_wncalculus;

import Componenti.UnsupportedFileException;
import Test.SN_DataTester;
import Test.SyntaxTree_DataTester;
import java.io.File;
import java.util.Scanner;
import operazioni_xml.*;

/**
 *
 * @author dell
 */
public class TraduttoreSN_wncalculus {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //test
        XMLScanner xml_sc = XMLScanner.get_instance(scan_file_name());
        xml_sc.scan_file_data();
        SemanticAnalyzer sa = SemanticAnalyzer.get_instance();
        sa.set_syntax_tree(DataParser.get_syntax_tree());
        sa.analyze_syntax_tree();
        SyntaxTree_DataTester st_dt = SyntaxTree_DataTester.get_instance(); //for data testing
        //st_dt.SyntaxTree_all_data();
        SN_DataTester sn_dt = SN_DataTester.get_instance(); //for data testing
        //sn_dt.SN_all_data();
        //sn_dt.print_nodes_connections();
    }
    
    private static String scan_file_name() throws UnsupportedFileException{
        String file_name = "CPN 1.pnml";
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file name with ext (.pnml) to be translated into unfolded symmetric net:");
        System.out.println("Note: if you want to use the default file name 'CPN 1', press \"1\":");        
        String str_name = sc.nextLine();
        
        if(!str_name.equals("1")){
            
            if(!str_name.contains("pnml")){
                throw new UnsupportedFileException("can't use a non pnml file!");
            }
            
            file_name = str_name;
        }
        
        return file_name;
    }
    
}
