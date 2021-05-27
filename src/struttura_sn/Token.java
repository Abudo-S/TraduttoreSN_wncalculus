/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package struttura_sn;

import wncalculus.classfunction.ClassFunction;
import wncalculus.classfunction.ElementaryFunction;
import wncalculus.color.ColorClass;
import wncalculus.expr.Interval;

/**
 *
 * @author dell
 */
public class Token extends ElementaryFunction{
    
    private final String value; //element that exist in a predefined colorclass
//    private final ColorClass class_type;
   
    /**
     * 
     * @param value the value of token
     * @param type the colour class type of token
     */
    public Token(String value, ColorClass type){
        super(type);
        this.value = value;
//        this.class_type = type;
    }
    
    /**
     * 
     * @return the colour class type of token
     */
    public ColorClass get_type(){
//        return this.class_type;
        return this.getSort();
    }
    
    /**
     * 
     * @return the value of token
     */
    public String get_Token_value(){
        return this.value;
    }
    
    /*
    public void set_type(ColourClass type) throws Exception{
        if(this.domain_type != null){
            throw new Exception("Type conflict: Trying to assign same token to different types");
        }
        this.class_type = type;
    }
    
    public void set_type(Domain type) throws Exception{
        if(this.class_type != null){
            throw new Exception("Type conflict: Trying to assign same token to different types");
        }
         this.domain_type = type;
    }
    */
    
    /**
     *
     * @return the description of <tt>Colour token</tt>
     */
    @Override
    public String toString() {
        return this.get_Token_value();
    }
    
    @Override
    public Interval card() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <E extends ClassFunction> E copy(ColorClass newcc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <E extends ClassFunction> E setDefaultIndex() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int splitDelim() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
