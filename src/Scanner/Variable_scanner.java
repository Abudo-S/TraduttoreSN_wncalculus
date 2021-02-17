/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scanner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author dell
 */
public class Variable_scanner extends ElementScanner{
    
    public Variable_scanner(final Document doc){
        super(doc);
    }
    
    @Override
    public void scan_info(Element Variable_element){
        
        if(Variable_element.getElementsByTagName("usersort").getLength()>0){
            this.scan_variable_type(Variable_element);
        }else{
            throw new NullPointerException("Can't find variable type: " + this.get_element_name(Variable_element));
        }
    }
    
    private void scan_variable_type(Element Variable_element){ //variabledecl tag
        Node decl = Variable_element.getElementsByTagName("usersort").item(0);
        
        if(decl.getNodeType() == Node.ELEMENT_NODE){
            Element usersort = (Element) decl;
            String var_type = usersort.getAttribute("declaration");
            //call DataParser function to create variable of extracted data
            dp.add_Variable(Variable_element.getAttribute("id"), var_type);
        }
    }
}
