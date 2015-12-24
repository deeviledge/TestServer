/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser.groupinformation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sglclient.groupadmin.MakeGroupClient;
import sglclient.groupadmin.MakeGroupInformation;
import sglclient.option.Uniques;

/**
 * FXML Controller class
 *
 * @author nishimura
 */
public class MakeGroupDialogController implements Initializable {
    ObservableList<MemberTableData> list = FXCollections.observableArrayList();
    @FXML
    TextField groupNameField;   //グループ名を入力するテキストフィールド
    @FXML
    TextField memberField;      //メンバーを入力するテキストフィールド
    @FXML
    Label groupNameLabel;       //入力されたグループ名を表示するラベル
    @FXML
    Label infoLabel;            //色々な通知を表示するラベル
    @FXML
    TableView<MemberTableData> tableview; //登録メンバーを表示するテーブルビュー
    
    @FXML
    TableColumn memberColumn;//ユーザ情報テーブルの右側
    
    String[] users;     //ログインユーザを格納
    String groupname;   //グループネームを格納
    int cnt;            //登録したユーザ数
    String[] members;   //登録したメンバー
    String ServerIP;    //サーバーIP
    public final static int port = 12345;
    ArrayList userlist;
    
    MakeGroupInformation mgi;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mgi = new MakeGroupInformation();
        users = mgi.getLoginUser();
        members = new String[users.length];
        userlist = new ArrayList(users.length);
        cnt=0;
        ServerIP = new Uniques().getServerIP();
    }
    @FXML
    void handleSetGroupNameAction(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {  //EnterKeyが押されたら、グループ名を登録
            groupNameLabel.setText(groupNameField.getText());
            groupname = groupNameField.getText();
            groupNameField.setText(null);
        }
    }
     @FXML
    void handleAddGroupMemberAction(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {  //EnterKeyが押されたら、メンバーを追加
            
            boolean exist_login=false;
            boolean exist_member=false;
            list.clear();   
            String name = memberField.getText();
            for(int i=0;i<users.length;i++)
                if(users[i].equals(name))exist_login=true;
            if(exist_login){
                for(int j=0;j<cnt;j++){
                    if(members[j].equals(name)){
                        exist_member=true;
                        infoLabel.setText(name+"はすでに追加されています");
                    }
                }
            }
            if(exist_login==true && exist_member==false){
                members[cnt]=name;
                userlist.add(members[cnt]);
                cnt++;
                infoLabel.setText(name+"を登録しました");
            }
            if(!exist_login)infoLabel.setText(name+"はログインしていません");
            
            
            for(int k=0;k<cnt;k++){
                list.add(new MemberTableData(members[k]));
            }
            
            memberColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("memberColumn"));
            
            tableview.setItems(list);
            memberField.setText(null);
        }
    }
    
    @FXML
    void handleGroupMakeButtonAction(ActionEvent event) throws IOException, InterruptedException{
        /*for(int i=0;i<cnt;i++){
            System.out.println(userlist.get(i));
            System.out.println(members[i]);
        }*/
        String crlf = System.getProperty("line.separator");
        if(cnt<2 && groupNameLabel.getText().equals("")){
            infoLabel.setText("メンバーを二人以上登録してください"+crlf
                    +"グループ名を入力してください");
        }
        else if(cnt<2){
            infoLabel.setText("メンバーを二人以上登録してください");
        }
        else if(groupNameLabel.getText().equals("")){
            infoLabel.setText("グループ名を入力してください");
        }
        else{
            //System.out.println("見えてる？");
            infoLabel.setText("グループ鍵を作成しています");
            MakeGroupClient mgc = new MakeGroupClient(ServerIP,port,groupname, userlist);
            mgc.exit();
            //if(mgc.permission){
                
            //}
            //else{
            //    infoLabel.setText("ログイン状態でないメンバーが"+crlf+
            //            "が含まれていたため作成できませんでした");
            //}
            infoLabel.setText("グループを作成しました"+crlf
                    +"「閉じる」ボタンで終了してください");
        }
    }
    
     //"閉じる"ボタンを押されたら画面を閉じる
    @FXML
    void handleCloseButtonAction(ActionEvent event){
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
}
