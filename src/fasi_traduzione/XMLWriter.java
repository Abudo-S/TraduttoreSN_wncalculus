/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fasi_traduzione;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import writer.*;

/**
 *
 * @author dell
 */
//singleton
//part of factory pattern with (writer)
public class XMLWriter {
    
    private final String file_address; 
    private final Document doc;
    private final Document doc_pnpro;
    private final Place_writer pw;
    private final Transition_writer tw;
    private final Arc_writer aw;
    private final ColourClass_writer ccw;
    private final Variable_writer vw;
//    private final Domain_writer dw;
    private static Element declaration; //for pnml document
    //single instance
    private static XMLWriter instance = null;
    
    /**
     * 
     * @param file_address the address of file that will be generated
     */
    private XMLWriter(String file_address) throws ParserConfigurationException, SAXException, IOException{
        Pattern p = Pattern.compile("(.*)[.]pnml");
        Matcher m = p.matcher(file_address);
        
        if(m.find()){
            file_address = m.group(1);
        }
        
        this.file_address = file_address + "_generated";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        //create pnpro document
        this.doc_pnpro = builder.newDocument();
        Element prnpro = this.doc_pnpro.createElement("project");
        prnpro.setAttribute("name", "SN");
        prnpro.setAttribute("version", "121");
        this.doc_pnpro.appendChild(prnpro);
        
        //create pnml document
        this.doc = builder.newDocument();
        Element pnml = this.doc.createElement("pnml");
        pnml.setAttribute("xmlns", "http://www.pnml.org/version-2009/grammar/pnml");
        this.doc.appendChild(pnml);
        
        //initialize elements scanners
        this.pw = Place_writer.get_instance(this.doc, this.doc_pnpro);
        this.tw = Transition_writer.get_instance(this.doc, this.doc_pnpro);
        this.aw = Arc_writer.get_instance(this.doc, this.doc_pnpro);
        this.ccw = ColourClass_writer.get_instance(this.doc, this.doc_pnpro);
        this.vw = Variable_writer.get_instance(this.doc, this.doc_pnpro);
        //this.dw = Domain_writer.get_instance(this.doc);
    }
    
    /**
     * 
     * @return the address generated of file
     */
    public String get_file_address(){
        return this.file_address;
    }
    
    /**
     * write all necessary data in a pnml document, then create a file from them
     * @throws javax.xml.transform.TransformerException
     */
    public void write_all_data() throws TransformerException{ //pnml
        //set file address.pnml
        String modified_address = this.file_address + ".pnml";
        //create essential file tags
        Element net = this.doc.createElement("net");
        net.setAttribute("id", modified_address);
        net.setAttribute("type", "http://www.pnml.org/version-2009/grammar/symmetricnet");
        Element net_name = this.doc.createElement("name");
        net_name.setTextContent(modified_address);
        net.appendChild(net_name);
        
        //create declarations
//        Element declaration = this.doc.createElement("declaration");
//        Element structure = this.doc.createElement("structure");
//        Element declarations = this.doc.createElement("declarations");
//        
//        this.ccw.get_element_data().stream().forEach(
//                cc_data -> this.ccw.write_info(cc_data, declarations)
//        );
//        
//        this.dw.get_element_data().stream().forEach(
//                domain_data -> this.dw.write_info(domain_data, declarations)
//        );
//        
//        this.vw.get_element_data().stream().forEach(
//                var_data -> this.vw.write_info(var_data, declarations)
//        );
//        
//        structure.appendChild(declarations);
//        declaration.appendChild(structure);
//        
//        this.ccw.get_element_data().stream().forEach(
//                cc_data -> this.ccw.write_info(cc_data, declarations)
//        );
        
        //create components
        Element page = this.doc.createElement("page");
        
        this.pw.get_element_data().stream().forEach(
                place_data -> this.pw.write_info(place_data, page)
        );
        
        this.tw.get_element_data().stream().forEach(
                transition_data -> this.tw.write_info(transition_data, page)
        );
                
        this.aw.get_element_data().stream().forEach(
                arc_data -> this.aw.write_info(arc_data, page)
        );
        net.appendChild(this.doc.adoptNode(declaration));
        net.appendChild(page);
        this.doc.getDocumentElement().appendChild(net);
        this.doc.setXmlVersion("1.0");
        this.doc.setXmlStandalone(false);
        
        //transform doc in xml file
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource domsr = new DOMSource(this.doc);
        StreamResult streamResult = new StreamResult(new File(modified_address));
        transformer.transform(domsr, streamResult);
        
        System.out.println("File has been created under this name '" + modified_address + "'");
        this.write_all_data_pnpro(true);
    }
    
    /**
     * write all necessary data in a pnpro document, then create a file from them
     * @param write_in_xml true means that doc_pnpro will be transformed in xml, false means that there will be other gspn nodes that might be added
     * @throws javax.xml.transform.TransformerException
     */
    public void write_all_data_pnpro(boolean write_in_xml) throws TransformerException{ //pnpro
        String gspn_name = "original";
        //set file address.pnpro
        String modified_address = this.file_address + ".PNPRO";
        //create essential file tags
        Element gspn = this.doc_pnpro.createElement("gspn");
        
        if(write_in_xml){
            gspn_name = "partial_unfolding";
        }
        
        gspn.setAttribute("name", gspn_name);
        gspn.setAttribute("show-fluid-cmd", "false");
        gspn.setAttribute("show-timed-cmd", "false");
        gspn.setAttribute("view-rates", "false");
        gspn.setAttribute("zoom", "100");
        Element nodes = this.doc_pnpro.createElement("nodes");
        
        this.ccw.get_element_data_pnpro().stream().forEach(
                cc_data -> this.ccw.write_info_pnpro(cc_data, nodes)
        );
        
        this.vw.get_element_data_pnpro().stream().forEach(
                var_data -> this.vw.write_info_pnpro(var_data, nodes)
        );
        
        this.pw.get_element_data_pnpro().stream().forEach(
                place_data -> this.pw.write_info_pnpro(place_data, nodes)
        );
        
        this.tw.get_element_data_pnpro().stream().forEach(
                transition_data -> this.tw.write_info_pnpro(transition_data, nodes)
        );
        
        Element edges = this.doc_pnpro.createElement("edges"); 
        
        this.aw.get_element_data_pnpro().stream().forEach(
                arc_data -> this.aw.write_info_pnpro(arc_data, edges)
        );
        gspn.appendChild(nodes);
        gspn.appendChild(edges);
        this.doc_pnpro.getDocumentElement().appendChild(gspn);
        
        if(write_in_xml){
            //transform doc_pnpro in xml file
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domsr = new DOMSource(this.doc_pnpro);
            StreamResult streamResult = new StreamResult(new File(modified_address));
            transformer.transform(domsr, streamResult);

            System.out.println("File has been created under this name '" + modified_address + "'");
        }
    }
    
    /**
     * 
     * @param net_decl set unmodified net declarations
     */
    public void set_net_declaration(Element net_decl){
        declaration = net_decl;   
    }
    /**
     * delegator
     * @param element_info ArrayList of all element data that's ready to be added
     * @param PNPRO true if we are writing a PNPRO file
     */
    public void add_place(ArrayList<String> element_info, boolean PNPRO){
        
        if(PNPRO){
            this.pw.add_element_data_pnpro(element_info);
        }else{
            this.pw.add_element_data(element_info);
        }
    }
    
    /**
     * delegator
     * @param element_info ArrayList of all element data that's ready to be added
     * @param PNPRO true if we are writing a PNPRO file
     */
    public void add_transition(ArrayList<String> element_info, boolean PNPRO){
        
        if(PNPRO){
            this.tw.add_element_data_pnpro(element_info);
        }else{
            this.tw.add_element_data(element_info);
        }
    }
    
    /**
     * delegator
     * @param element_info ArrayList of all element data that's ready to be added
     * @param PNPRO true if we are writing a PNPRO file
     */
    public void add_arc(ArrayList<String> element_info, boolean PNPRO){
        
        if(PNPRO){
            this.aw.add_element_data_pnpro(element_info);
        }else{
            this.aw.add_element_data(element_info);
        }
    }
    
    /**
     * delegator
     * @param element_info ArrayList of all element data that's ready to be added
     * @param PNPRO true if we are writing a PNPRO file
     */
    public void add_colourclass(ArrayList<String> element_info, boolean PNPRO){
        
        if(PNPRO){
            this.ccw.add_element_data_pnpro(element_info);
        }else{
            this.ccw.add_element_data(element_info);
        }
    }
    
    /**
     * delegator
     * @param element_info ArrayList of all element data that's ready to be added
     * @param PNPRO true if we are writing a PNPRO file
     */
    public void add_variable(ArrayList<String> element_info, boolean PNPRO){
        
        if(PNPRO){
            this.vw.add_element_data_pnpro(element_info);
        }else{
            this.vw.add_element_data(element_info);
        }
    }
        
    /**
     * 
     * @param address the address of file that will be generated
     * @return single static instance
     */
    public static XMLWriter get_instance(final String address) throws ParserConfigurationException, SAXException, IOException{
        
        if(instance == null){
            instance = new XMLWriter(address);
        }
        
        return instance;
    }
}
