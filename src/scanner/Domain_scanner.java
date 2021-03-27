/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 *
 * @author dell
 */
//singleton
public class Domain_scanner extends ElementScanner{
    
    private static Domain_scanner instance = null;
    
    /**
     * 
     * @param doc the document from which we scan domains
     */
    private Domain_scanner(final Document doc){
        super(doc);
    }
    
    /**
     * 
     * @param Domain_element domain-element's tag from which we retrieve domain data
     * @throws NullPointerException if the tag that contain domain data don't exist
     */
    @Override
    public void scan_info(Element Domain_element) throws NullPointerException{
        
        if(Domain_element.getElementsByTagName("usersort").getLength()>0){
            this.scan_domain_classes(Domain_element);
        }else if(Domain_element.getElementsByTagName("finiteintrange").getLength()>0 ||
                 Domain_element.getElementsByTagName("finiteenumeration").getLength()>0){
            //do nothing, will be handled by ColorClass_scanner
        }else{
            throw new NullPointerException("Can't find domain type: " + this.get_element_name(Domain_element));
        }
    }
    
    /**
     * 
     * @param Domain_element domain's tag from which we retrieve domain data
     */
    private void scan_domain_classes(Element Domain_element){ //namedsort tag
        NodeList classes_decl = Domain_element.getElementsByTagName("usersort");
        ArrayList<String> classes = new ArrayList<>();
        
        for(var i = 0; i< classes_decl.getLength(); i++){
            Node class_decl = classes_decl.item(i);
            if(class_decl.getNodeType() == Node.ELEMENT_NODE){
                Element color_class = (Element) class_decl;
                classes.add(color_class.getAttribute("declaration"));
            }
        }
        //call DataParser function to create Domain of extracted data
        dp.add_Domain(Domain_element.getAttribute("id"), classes);
    }
    
    //remove color-class's declarations because they have the same tag "namesort" 
    @Override
    public void remove_from_tags_list(){ //not used yet/replaced by additional else-if in scan_info()
        element_tags.removeIf(namesort -> namesort.getElementsByTagName("productsort").getLength() == 0); //remove from domain tags if it's a color class
    }
    
    /**
     * 
     * @param doc the document from which we scan domains
     * @return single static instance
     */
    public static Domain_scanner get_instance(Document doc){
        
        if(instance == null){
            instance = new Domain_scanner(doc);
        }
        
        return instance;
    }
}
