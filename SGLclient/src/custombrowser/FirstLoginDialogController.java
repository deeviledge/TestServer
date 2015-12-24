/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.w3c.dom.Document;
import sglclient.certificate.SglLogin;
import sglclient.option.EditOptionXml;
import sglclient.pskey.PSKeyRecieveClient;
import sglclient.preparation.LoginClient;


/**
 * FXML Controller class
 *
 * @author nishimura
 */
public class FirstLoginDialogController  implements Initializable {
    
    @FXML
    Button joinButton;
    @FXML
    Button closeButton;
    @FXML
    TextField usernameText;
    @FXML
    TextField mailaddressText;
    @FXML
    Label messageLabel;
    
    
    public String username,mailaddress,serverip;
    
    private CustomBrowserController application;
    
    private boolean success = false;
    
    String crlf = System.getProperty("line.separator");
    
    /* public void setApp(CustomBrowserController application){
        this.application = application;
    }*/
    
    /**
     * Initializes the controller class.
     * @param url
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML
    void handleJoinButtonAction(ActionEvent event) throws Exception{
        if(success==false){
            if("".equals(usernameText.getText())){
                messageLabel.setText("ユーザー名を入力してください");
            }else if("".equals(mailaddressText.getText())){
                messageLabel.setText("メールアドレスを入力してください");
            }
            else{
                username = usernameText.getText();
                mailaddress = mailaddressText.getText();
                EditOptionXml eox = new EditOptionXml();
                serverip = eox.getIP();
                Document doc = eox.GenerateXml(serverip);         //サーバーIPを作成する
                try{
                    LoginClient LC = new LoginClient();     //事前準備とユーザ登録
                    LC.userLogin(serverip,username,mailaddress);
                    PSKeyRecieveClient pskc = new PSKeyRecieveClient(eox.getIP());	//認証とPreSharedKeyの受け取り
                    if(pskc.cert_flg == true) {
                        SglLogin Login = new SglLogin();
                        Login.Login();  //サーバーにログイン
                        
                        messageLabel.setText("ログインに成功しました。"+crlf+"画面を閉じてアプリケーションを開始してください");
                        closeButton.defaultButtonProperty();
                        success = true;
                    }
                    else messageLabel.setText("ログインに失敗しました。"+crlf+"画面を閉じてアプリケーションを開始してください");
                }catch (IOException | InterruptedException ex) {
                    messageLabel.setText("接続に失敗しました。"+crlf+"画面を閉じてアプリケーションを開始してください");
                    Logger.getLogger(CustomBrowserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
        }
            
    }
    
    @FXML
    void handleCloseButtonAction(ActionEvent event) throws Exception{
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
    
    
}
