/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package traduttoresn_wncalculus;

import Test.SN_DataTester;
import Test.SyntaxTree_DataTester;
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
        XMLScanner xml_sc = XMLScanner.get_instance("CPN 1.pnml");
        xml_sc.scan_file_data();
        SemanticAnalyzer sa = SemanticAnalyzer.get_instance();
        sa.set_syntax_tree(DataParser.get_syntax_tree());
        sa.analyze_syntax_tree();
        SyntaxTree_DataTester st_dt = SyntaxTree_DataTester.get_instance();
        //st_dt.SyntaxTree_all_data();
        SN_DataTester sn_dt = SN_DataTester.get_instance();
        //sn_dt.SN_all_data();
       //sn_dt.print_nodes_connections();
    }
    
}
