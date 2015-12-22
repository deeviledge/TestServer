/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverjavafx;

import java.io.IOException;
import java.net.URL;
import java.security.Provider;
import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import sglserver.preparation.PreparationServer;
import sglserver.dynamicpeerinformation.DynamicPeerInformation;
import sglserver.groupinformation.ReadGroupInfoXML;
import sglserver.peerbasicinformation.PeerBasicInformationEdit;

/**
 *
 * @author nishimura
 */
public class SGLServerController implements Initializable {
    
    ObservableList<TableData> list = FXCollections.observableArrayList();
    ObservableList<TableData> grouplist = FXCollections.observableArrayList();
    ObservableList<TableData> memberlist = FXCollections.observableArrayList();
    @FXML
    TableView<TableData> tableview;
    
    @FXML
    TableView<TableData> groupview_left;
    @FXML
    TableColumn gleftColumn; //グループ情報テーブルの左側
    @FXML
    TableView<TableData> groupview_right;
    @FXML
    TableColumn grightColumn;//グループ情報テーブルの右側
    
    @FXML
    TableColumn leftColumn; //ユーザ情報テーブルの左側
    @FXML
    TableColumn rightColumn;//ユーザ情報テーブルの右側
    @FXML
    MenuItem set_login; //メニューボタン（ログインユーザ）
    @FXML
    MenuItem set_join;  //メニューボタン(登録ユーザ)
    @FXML
    TextArea textarea;  //更新情報の表示用
    @FXML
    Button renewal_button;//グループ情報の更新ボタン
    @FXML
    MenuButton menubutton;//メニューボタン本体
     
    //改行文字
    String crlf = System.getProperty("line.separator");
    
    
    static class AcceptService extends Service {

        public AcceptService(Provider provider, String type, String algorithm, String className, List<String> aliases, Map<String, String> attributes) {
            super(provider, type, algorithm, className, aliases, attributes);
            
        }
        
    }
    
    @FXML
    void handleLoginUserButtonAction(ActionEvent event) {
	LoginUserRead();
    }
    
    @FXML
    void handleJoinUserButtonAction(ActionEvent event) {
	JoinUserRead();
    }
    
    @FXML
    void handleRenewalButtonAction(ActionEvent event) throws IOException {
	GroupRead();
    }
    
    //表中のグループが選択されたら呼び出される
    //選択されたグループのメンバを右のテーブルに表示する
    @FXML
    void handleGroupTableAction(ActionEvent event) throws IOException {
        
        //グループ情報読み取り
        ReadGroupInfoXML RGX = new ReadGroupInfoXML();
        
        list.clear();
        //全グループを配列に格納
        String[] groups = RGX.getGroupName();
        
        ObservableList<TableData> selected = FXCollections.observableArrayList(groupview_left.getSelectionModel()
                .getSelectedItems());

        //System.out.println(selected.toString());

        
        list.clear();
        for(int i=0;i<RGX.getGroupNum();i++){
            list.add(new TableData(groups[i],null));
            
            
        }
        
        gleftColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("leftColumn"));
        grightColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("rightColumn"));
        groupview_left.setItems(list);
        //groupview_right.setItems(list);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        leftColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("leftColumn"));
        rightColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("rightColumn"));
        
        
       
        // サンプルデータを1行追加
        //tableview.getItems().add(new TableData("test","test"));
       
        //PeerBasicInformation.xmlとサーバのキーストアを作成
	new PreparationServer();
        textarea.setText("SGLサーバーを起動しました"+crlf);
        
       
	final EventHandler event = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                //クリックでページジャンプ
                if (me.getButton() == MouseButton.PRIMARY) {
                    //グループ情報読み取り
                    TableView.TableViewSelectionModel<TableData> selectionModel = groupview_left.getSelectionModel();
                    String group=selectionModel.getSelectedItem().getLeft();
                    MemberRead(group);
		}
				
            }
	};
	groupview_left.addEventHandler(MouseEvent.MOUSE_CLICKED,event);
        
        
        //tableのサイズ設定
        rightColumn.prefWidthProperty().bind(tableview.widthProperty().divide(2));
	leftColumn.prefWidthProperty().bind(tableview.widthProperty().divide(2));
        //bookmarktableのサイズ設定
	//leftColumn.prefWidthProperty().bind(tableview.widthProperty().divide(2));
	//rightColumn.prefWidthProperty().bind(tableview.widthProperty().divide(2));
        LoginUserRead();    //ログインユーザを左のテーブルに表示
        try {
            GroupRead();    //グループ情報を右テーブルに表示
        } catch (IOException ex) {
            Logger.getLogger(SGLServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableview.setEditable(false);
    }
    
    //テーブルビューにログインユーザを表示
    private void LoginUserRead(){
        menubutton.setText(set_login.getText());
        leftColumn.setText("ユーザ");
        rightColumn.setText("IPアドレス");
        DynamicPeerInformation DPI = new DynamicPeerInformation();
        
        list.clear();
        String[] IDs = DPI.getPeerID();
       
        list.clear();
        for(int i=0;i<DPI.getPeerNum();i++){
            list.add(new TableData("ID:"+IDs[i]+" name:"+DPI.getUserName(IDs[i]),DPI.getIP(DPI.getUserName(IDs[i]))));
            
            
        }
        leftColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("leftColumn"));
        rightColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("rightColumn"));
        tableview.setItems(list);
        
    }
    
    //テーブルビューに登録ユーザ表示
    private void JoinUserRead(){
        menubutton.setText(set_join.getText());
        leftColumn.setText("ID");
        rightColumn.setText("NAME");
        
        PeerBasicInformationEdit PBI = new PeerBasicInformationEdit();
        
        list.clear();
        String[] IDs = PBI.getPeerID();
       
        list.clear();
        for(int i=0;i<PBI.getPeerNum();i++){
            list.add(new TableData(IDs[i],PBI.getPeerName(IDs[i])));
            
            
        }
        leftColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("leftColumn"));
        rightColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("rightColumn"));
        tableview.setItems(list);
    }
    
    private void GroupRead() throws IOException{
        gleftColumn.setText("グループ");
        grightColumn.setText("メンバ");
        
        //グループ情報読み取り
        ReadGroupInfoXML RGX = new ReadGroupInfoXML();
        
        grouplist.clear();
        //全グループを配列に格納
        String[] groups = RGX.getGroupName();
       
        
        for(int i=0;i<RGX.getGroupNum();i++){
            grouplist.add(new TableData(groups[i],null));
            
            
        }
        gleftColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("leftColumn"));
        grightColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("rightColumn"));
        groupview_left.setItems(grouplist);
        //groupview_right.setItems(list);
        
        
    }
    
    //グループメンバを右のテーブルビューに表示
    private void MemberRead(String group){
        
        //グループ情報読み取り
        ReadGroupInfoXML RGX = new ReadGroupInfoXML();
        PeerBasicInformationEdit PBI = new PeerBasicInformationEdit();
        
        memberlist.clear();
        //全メンバーを配列に格納
        ArrayList members = RGX.getGroupMember4(group);
        
       
        memberlist.clear();
        for(int i=0;i<members.size();i++){
            memberlist.add(new TableData(null,PBI.getPeerName(members.get(i).toString())));
            
            
        }
        gleftColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("leftColumn"));
        grightColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("rightColumn"));
        groupview_right.setItems(memberlist);
        //groupview_right.setItems(list);
    }
    
}
