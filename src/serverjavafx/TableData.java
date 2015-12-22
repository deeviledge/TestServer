/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverjavafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author nishimura
 */
public class TableData {
    private StringProperty leftColumn;
    private StringProperty rightColumn;
    
    
    public TableData(String left,String right){
	leftColumn = new SimpleStringProperty(left);
        rightColumn = new SimpleStringProperty(right);
    }
    
    
    public StringProperty leftColumnProperty(){return leftColumn;}
    public StringProperty rightColumnProperty(){return rightColumn;}
    
    //アクセッサメソッド
    public String getLeft(){
	return leftColumn.get();
    }
    
    
}
