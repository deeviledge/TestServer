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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sglclient.certificate.SglCall;
import sglclient.myinformation.MyInformation;

/**
 * FXML Controller class
 *
 * @author nishimura
 */
public class SGLGroupController implements Initializable {
    
    private String[] groups;
    
    
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
    TableColumn statusColumn;//ログイン状態を表示する項目
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        final EventHandler event = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() == MouseButton.PRIMARY) {
                    //グループ情報読み取り
                    TableView.TableViewSelectionModel<TableData> selectionModel = groupview_left.getSelectionModel();
                    String group=selectionModel.getSelectedItem().getLeft();
                    MemberRead(group);
		}
				
            }
	};
	groupview_left.addEventHandler(MouseEvent.MOUSE_CLICKED,event);
        
        try {
            getGroupInfo();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SGLGroupController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //グループ作成ボタンが押された時に呼ばれる
    @FXML
    void handleMakeGroupAction(ActionEvent event) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("MakeGroupDialog.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        MakeGroupDialogController MGDcontroller = loader.getController();
        Scene scene = new Scene(root);
        Stage firstlogindialog = new Stage(StageStyle.UTILITY);
        firstlogindialog.setScene(scene);
        firstlogindialog.setTitle("グループ作成");
        firstlogindialog.showAndWait();
    }
    
    public void getGroupInfo() throws IOException, InterruptedException{
            grouplist.clear();
            SglCall sglCall = new SglCall();
            MyInformation mi = new MyInformation();
            groups = new String[mi.getGroupName().length];
            groups = mi.getGroupName();
            for(int i=0;i<groups.length;i++){
                grouplist.add(new TableData(groups[i],null));
            }
            gleftColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("leftColumn"));
            grightColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("rightColumn"));
            groupview_left.setItems(grouplist);
    }
    
    //グループメンバを右のテーブルビューに表示
    private void MemberRead(String group){
        
        SglCall sglCall = new SglCall();
        
        memberlist.clear();
        //全メンバーを配列に格納
        ArrayList members = sglCall.getGroupInformation(group);
        
        //全メンバーを配列に格納
        String[] usernames = null;
        String[] userids   = null;
        sglCall = new SglCall();
        usernames = sglCall.getLoginUserName();
        
        
        memberlist.clear();
        int index =0;
        boolean status = false;
        String statusText;
        for(int i=0;i<members.size();i++){
            for(int j=0;j<usernames.length;j++){
                if(members.get(i).equals(usernames[j])){
                    status = true;
                }
            }
            if(status == true)statusText = "ログイン中";
            else statusText = "";
            status=false;
            memberlist.add(new TableData(members.get(i).toString(),statusText));
            
            
        }
        
        grightColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("leftColumn"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("rightColumn"));
        
        groupview_right.setItems(memberlist);
        //groupview_right.setItems(list);
    }
    
    //"閉じる"ボタンを押されたら画面を閉じる
    @FXML
    void handleCloseButtonAction(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
    
    //"更新"ボタンを押されたら画面を閉じる
    @FXML
    void handleReViewButtonAction(ActionEvent event) throws IOException, InterruptedException{
        SglCall sglCall = new SglCall();
        sglCall.checkDeleteGroup();
        getGroupInfo();
    }
    
    //"削除"ボタンを押されたら画面を閉じる
    @FXML
    void handleDeleteButtonAction(ActionEvent event) throws IOException, InterruptedException{
        TableView.TableViewSelectionModel<TableData> selectionModel = groupview_left.getSelectionModel();
        String group=selectionModel.getSelectedItem().getLeft();
        if(group!=null){
            MyInformation MI = new MyInformation();
            String[] groups = MI.getGroupName();
            System.out.println("--------- グループ名一覧 ----------");
            for(int i=0;i<groups.length;i++){
                System.out.println(groups[i]);
            }
            System.out.println("--------------------------------");
            System.out.println("削除するグループ名を入力してください");
            System.out.print(">");
            
            boolean groupexist = false;
            for(int i=0;i<groups.length;i++){
                if(groups[i].equals(group)){
                    SglCall sglCall = new SglCall();
                    sglCall.groupDeleteNotificate(group, MI.getUsrID());
                    groupexist = true;
                    break;
                }
            }
            if(groupexist)System.out.println(group+"を削除しました");
            else System.out.println(group+"は存在しません");
            getGroupInfo();
            memberlist.clear();
            grightColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("leftColumn"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("rightColumn"));
            groupview_right.setItems(memberlist);
        }
    }
}
