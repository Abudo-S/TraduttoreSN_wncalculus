/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package traduttoresn_wncalculus;

import operazioni_xml.XMLScanner;

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
    }
    
}
