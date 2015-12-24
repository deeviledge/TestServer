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
import sglclient.certificate.SglLogin;
import sglclient.myinformation.MyInformation;

/**
 * FXML Controller class
 * 色々な処理結果をダイアログで表示する
 * (もはやログインダイアログではない。。。笑)
 * @author nishimura
 */
public class LoginDialogController implements Initializable {
    
    @FXML
    Label messageLabel;
    @FXML
    Label detailsLabel;
    private final int START_BROWSER = 0;
    private final int BUSY_BROWSER = 1;
    String crlf;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        crlf = System.getProperty("line.separator");
    }    
    
    //認証に成功したとき呼ばれる
    public void setDialogMessage_Success(String ServerIp,boolean login_flg,int when){
        SglLogin Login = new SglLogin();
        messageLabel.setText("認証完了");
        
        if(login_flg){
            if(when==START_BROWSER)detailsLabel.setText(ServerIp + "に接続しました"+crlf+"画面を閉じてアプリケーションを開始してください");
            else if(when==BUSY_BROWSER)detailsLabel.setText(ServerIp + "に接続しました");
        }
        else{
            if(when==START_BROWSER)detailsLabel.setText("ID:"+Login.myid+"はすでログインしています"+crlf+"画面を閉じてアプリケーションを開始してください");
            else if(when==BUSY_BROWSER)detailsLabel.setText("ID:"+Login.myid+"はすでログインしています");
        }
        //System.out.println(ServerIp);
    }
    
    //認証に失敗したとき呼ばれる
    public void setDialogMessage_Failure(String ServerIp){
        messageLabel.setText("ログイン失敗");
        detailsLabel.setText(ServerIp + "への接続に失敗しました"); 
    }
    
    //認証に失敗したとき呼ばれる
    public void setDialogAllready(){
        MyInformation MI = new MyInformation();
        messageLabel.setText("ログイン失敗");
        detailsLabel.setText("User:"+MI.getUsrID() + " はすでにログインしています"); 
    }
    
    //アプリケーション起動時サーバーに接続できなかったとき呼ばれる
    public void setDialogMessage_NO_Server(String ServerIp){
        messageLabel.setText("ログイン失敗");
        detailsLabel.setText(ServerIp + "への接続に失敗しました"+crlf+"画面を閉じてアプリケーションを開始してください"); 
    }
    
    //グループ画面が開けないことを通知する(サーバ立ち上がっていないため)
    public void setDialogMessage_Group(){
        messageLabel.setText("can not open GroupWindow.");
        detailsLabel.setText("SGLサーバが起動していないためグループを編集できません"); 
    }
    
    //ログアウト処理が出来ないことを通知する(サーバ立ち上がっていないため)
    public void setDialogMessage_CanNotLogout(){
        messageLabel.setText("ログアウト不可");
        detailsLabel.setText("SGLサーバが起動していないためログアウト処理を実行できません"); 
    }
    
    //ログアウトが完了したことを通知する
    public void setDialogMessage_Finish_Logout(){
        messageLabel.setText("ログアウト完了");
        detailsLabel.setText("SGLサーバからログアウトしました"); 
    }
    
    //ログインユーザが表示できないことを通知する(サーバ立ち上がっていないため)
    public void setDialogMessage_Cannot_GetLoginUser(){
        messageLabel.setText("can not get login users.");
        detailsLabel.setText("サーバーにログインしていないためログインユーザを取得できません"); 
    }
    
    //"OK"ボタンを押されたら閉じる
    @FXML
    void handleOKButtonAction(ActionEvent event){
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
    
    
}
