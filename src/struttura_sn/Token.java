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
    
    private final String value; //in case of domain token then it will be value1,value2,...
//    private final ColorClass class_type;
   
    //can't be token of colour domain and colour class at the same time
    public Token(String value, ColorClass type){
         super(type);
        this.value = value;
//        this.class_type = type;
    }
    
    public ColorClass get_type(){
//        return this.class_type;
        return this.getSort();
    }
    
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
