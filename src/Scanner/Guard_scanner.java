/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author dell
 */
public class Guard_scanner extends ElementScanner{
    
    public Guard_scanner(final Document doc){
        super(doc);
    }
    
    public HashMap<Boolean,HashMap<String, String>> scan_guard(Element Guard_element){
        //to be completed
        return null;
    }
    
    public boolean scan_invert_guard(Element Guard_element){
        //to be completed
        return false;
    }
    
    public HashMap<Boolean,HashMap<String, String>> scan_guard(String Guard){
        //to be completed
        return null;
    }
    
}
