/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanner;


import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author dell
 */
//singleton
public class ColorClass_scanner extends ElementScanner{
    
    private static ColorClass_scanner instance = null;
    
    /**
     * 
     * @param doc the document from which we scan colour classes
     */
    private ColorClass_scanner(final Document doc){
        super(doc);
    }
    
    /**
     * 
     * @param color_class arc-element's tag from which we retrieve colour class data
     * @throws NullPointerException if tags that contain colour class data don't exist
     */
    @Override
    public void scan_info(Element color_class) throws NullPointerException{
        
        if(color_class.getElementsByTagName("finiteenumeration").getLength()>0){
            this.scan_finiteenumeration(color_class);
            
        }else if(color_class.getElementsByTagName("partitionelement").getLength()>0){
            this.scan_partitionedclass(color_class);
            
        }else if(color_class.getElementsByTagName("finiteintrange").getLength()>0){
            this.scan_finiterange(color_class);
            
        }else if(color_class.getElementsByTagName("usersort").getLength()>0){
            //do nothing, will be handled by Domain_scanner
        }else{
            throw new NullPointerException("Can't find color class type: " + this.get_element_name(color_class));
        }
    }
    
    /**
     * Note: in case of colour-class's data exists in tag "finiteintrange"
     * @param color_class color_class's tag from which we retrieve colour class data
     */
    private void scan_finiterange(Element color_class){ //namedsort tag
        int rangStart, rangEnd;
        boolean circular = false;
        Node fing = color_class.getElementsByTagName("finiteintrange").item(0);
        
        if(fing.getNodeType() == Node.ELEMENT_NODE){
            Element finiteintrange = (Element) fing;
            rangStart = Integer.parseInt(finiteintrange.getAttribute("start"));
            rangEnd = Integer.parseInt(finiteintrange.getAttribute("end"));

            if(color_class.getAttribute("ordered").equals("true")){
                circular = true;
            } 
            //call DataParser function to create color class of extracted data
            dp.add_ColorClass(this.get_element_name(color_class), rangStart, rangEnd, circular);
        }
    }
    
    /**
     * Note: in case of colour-class's data exists in tag "feconstant"
     * @param color_class color_class's tag from which we retrieve colour class data
     */
    private void scan_finiteenumeration(Element color_class){ //namedsort tag
        boolean circular = false;
        NodeList finco_list = color_class.getElementsByTagName("feconstant");
        ArrayList<String> token_names = new ArrayList<>();
        
        for(var i = 0; i< finco_list.getLength(); i++){
            Node finco = finco_list.item(i);
            if(finco.getNodeType() == Node.ELEMENT_NODE){
                Element finiteenumeration = (Element) finco;
                token_names.add(finiteenumeration.getAttribute("id"));
            }
        }

        if(color_class.getAttribute("ordered").equals("true")){
          circular = true;
        } 
        //call DataParser function to create color class of extracted data
        dp.add_ColorClass(this.get_element_name(color_class), token_names, circular);
    }
    
    /**
     * Note: in case of colour-class's data exists in tag "partitionelement" (partitioned colour class "in subclasses")
     * @param color_class color_class's tag from which we retrieve colour class data
     */
    private void scan_partitionedclass(Element color_class) throws NullPointerException{ //partition tag
        NodeList partiel_list = color_class.getElementsByTagName("partitionelement");
        HashMap<String, ArrayList<String>> subclasses = new HashMap<>(); //subclass_name -> subclass elements
        boolean circular = false;
        
        for(var i = 0; i< partiel_list.getLength(); i++){
            Node partiel = partiel_list.item(i);
            
            if(partiel.getNodeType() == Node.ELEMENT_NODE){
                Element subclass = (Element) partiel;
                NodeList subclass_tokens = subclass.getElementsByTagName("useroperator"); //finiteenumeration
                ArrayList<String> subclass_elements = new ArrayList<>();
                
                for(var j = 0; j< subclass_tokens.getLength(); j++){
                    Node subclass_token = subclass_tokens.item(j);
                    
                    if(subclass_token.getNodeType() == Node.ELEMENT_NODE){
                        Element token = (Element) subclass_token;
                        subclass_elements.add(token.getAttribute("declaration"));
                    }
                }
                
                NodeList fing_nl = subclass.getElementsByTagName("finiteintrange");
                if(fing_nl.getLength() > 0){
                    int rangStart, rangEnd;
                    Node fing = fing_nl.item(0);
        
                    if(fing.getNodeType() == Node.ELEMENT_NODE){
                        Element finiteintrange = (Element) fing;
                        rangStart = Integer.parseInt(finiteintrange.getAttribute("start"));
                        rangEnd = Integer.parseInt(finiteintrange.getAttribute("end"));
                        //will be filtered in DataParser
                        subclass_elements.add("lb=" + rangStart);
                        subclass_elements.add("ub=" + rangEnd);
                    }
                }
                
                if(subclass_elements.isEmpty()){
                    throw new NullPointerException("Can't create subclass " + subclass.getAttribute("id") + " without its elements!");
                }
                subclasses.put(subclass.getAttribute("id"), subclass_elements);
            }
        }
        
        if(color_class.getAttribute("ordered").equals("true")){
          circular = true;
        } 
        //call DataParser function to create color class of extracted data
        dp.add_ColorClass(this.get_element_name(color_class), subclasses, circular);
    }
    
    //remove domain's declarations because they have the same tag "namesort" 
    @Override
    public void remove_from_tags_list(){ //not used yet/replaced by additional else-if in scan_info()
        element_tags.removeIf(namesort -> namesort.getElementsByTagName("productsort").getLength()>0); //remove from color class tags if it's a domain
    }
    
    /**
     * 
     * @param doc the document from which we scan colour classes
     * @return single static instance
     */
    public static ColorClass_scanner get_instance(Document doc){
        
        if(instance == null){
            instance = new ColorClass_scanner(doc);
        }
        
        return instance;
    }
}