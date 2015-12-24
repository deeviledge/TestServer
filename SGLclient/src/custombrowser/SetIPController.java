/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.w3c.dom.Document;

import sglclient.option.*;

/**
 * FXML Controller class
 *
 * @author nishimura
 */
public class SetIPController implements Initializable {
    
    @FXML
    TextField setTextField;
    @FXML
    Label messageLabel;
    @FXML
    Label setLabel;
    

    EditOptionXml eox = new EditOptionXml();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        setLabel.setText(eox.getIP());
    }
    
    @FXML
    void handleSetIPButtonAction(ActionEvent event) {
        if(setTextField.getText().equals("")){
            messageLabel.setText("SGLサーバーのIPアドレスを入力してください");
        }
        else{
            Document doc = eox.GenerateXml(setTextField.getText());         //ドキュメントを作成して、EditOptionXml内のGenerateXmlに渡す
            eox.saveXml(doc);   //保存
            messageLabel.setText(setTextField.getText()+"に設定しました");
            setLabel.setText(eox.getIP());
        }
    }
    @FXML
    void handleCloseButtonAction(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
        
    
}
