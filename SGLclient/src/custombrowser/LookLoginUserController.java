/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser;

import custombrowser.groupinformation.TableData;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sglclient.certificate.SglCall;



/**
 * FXML Controller class
 *
 * @author nishimura
 */
public class LookLoginUserController implements Initializable {
    ObservableList<TableData> list = FXCollections.observableArrayList();
    @FXML
    TableView<TableData> tableview;
     @FXML
    TableColumn leftColumn; //ユーザ情報テーブルの左側
    @FXML
    TableColumn rightColumn;//ユーザ情報テーブルの右側
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getLoginUser();
    }    
    //テーブルビューに登録ユーザ表示
    private void getLoginUser(){
       
        leftColumn.setText("ID");
        rightColumn.setText("NAME");
        
        
        
        list.clear();
        //全メンバーを配列に格納
        String[] usernames = null;
        String[] userids   = null;
        SglCall sglCall = new SglCall();
        usernames = sglCall.getLoginUserName();
        sglCall = new SglCall();
        userids   = sglCall.getLoginUserID();
        
        
        for(int i=0;i<userids.length;i++){
            list.add(new TableData(userids[i],usernames[i]));
        }
        leftColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("leftColumn"));
        rightColumn.setCellValueFactory(new PropertyValueFactory<TableData, String>("rightColumn"));
        tableview.setItems(list);
    }
}
