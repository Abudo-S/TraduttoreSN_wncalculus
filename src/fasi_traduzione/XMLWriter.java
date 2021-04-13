/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import org.w3c.dom.Document;

/**
 *
 * @author dell
 */
//singleton
//part of factory pattern with (writer)
public class XMLWriter {
    
    private String file_address; 
    private Document doc;
    //single instance
    private static XMLWriter instance = null;
    
    /**
     * 
     * @param file_address the address of file that will be generated
     */
    private XMLWriter(String file_address){
        this.file_address = file_address + "_generated";
    }
    
    /**
     * 
     * @return the address generated of file
     */
    public String get_file_address(){
        return this.file_address;
    }
    
    /**
     * 
     * @param address the address of file that will be generated
     * @return single static instance
     */
    public static XMLWriter get_instance(final String address){
        
        if(instance == null){
            instance = new XMLWriter(address);
        }
        
        return instance;
    }
}
