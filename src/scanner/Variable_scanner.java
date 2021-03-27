/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author dell
 */
//singleton
public class Variable_scanner extends ElementScanner{
    
    private static Variable_scanner instance = null;
    
    /**
     * 
     * @param doc the document from which we scan elements
     */
    private Variable_scanner(final Document doc){
        super(doc);
    }
    
    /**
     * 
     * @param Variable_element variable-element's tag from which we retrieve variable data
     * @throws NullPointerException if the tag that contain domain data don't exist
     */
    @Override
    public void scan_info(Element Variable_element) throws NullPointerException{
        
        if(Variable_element.getElementsByTagName("usersort").getLength()>0){
            this.scan_variable_type(Variable_element);
        }else{
            throw new NullPointerException("Can't find variable type: " + this.get_element_name(Variable_element));
        }
    }
    
    /**
     * 
     * @param Variable_element place's tag from which we retrieve variable data
     */
    private void scan_variable_type(Element Variable_element){ //variabledecl tag
        Node decl = Variable_element.getElementsByTagName("usersort").item(0);
        
        if(decl.getNodeType() == Node.ELEMENT_NODE){
            Element usersort = (Element) decl;
            String var_type = usersort.getAttribute("declaration");
            //call DataParser function to create variable of extracted data
            dp.add_Variable(Variable_element.getAttribute("id"), var_type);
        }
    }
    
    /**
     * 
     * @param doc the document from which we scan variables
     * @return single static instance
     */
    public static Variable_scanner get_instance(Document doc){
        
        if(instance == null){
            instance = new Variable_scanner(doc);
        }
        
        return instance;
    }
}
