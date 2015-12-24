/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser.groupinformation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author nishimura
 */
public class MemberTableData {
    private StringProperty memberColumn;
    
    public MemberTableData(String member){
        memberColumn = new SimpleStringProperty(member);
    }
    public StringProperty memberColumnProperty(){return memberColumn;}
}
