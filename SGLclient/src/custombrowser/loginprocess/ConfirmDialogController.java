/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser.loginprocess;

import com.sun.javaws.Main;
import custombrowser.CustomBrowserController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;


/**
 * FXML Controller class
 *
 * @author nishimura
 */
public class ConfirmDialogController extends AnchorPane implements Initializable {
    
    @FXML
    Label name;
    @FXML
    Label mail;
    @FXML
    Label serverip;
    
    private CustomBrowserController application;
    
    public void setApp(CustomBrowserController application){
        this.application = application;
        //ここで色々セット
       
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // TODO
    }
    
    
    
    @FXML
    void FinishButtonAction(ActionEvent event) throws Exception{
        //finish_flg=true;
        ((Node)event.getSource()).getScene().getWindow().hide();
        
    }
    
    @FXML
    void ReturnButtonAction(ActionEvent event) throws Exception{
        if (application == null){
            // We are running in isolated FXML, possibly in Scene Builder.
            // NO-OP.
            return;
        }
        
        //application.Return();
    }
}
