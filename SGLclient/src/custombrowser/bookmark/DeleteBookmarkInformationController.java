/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser.bookmark;

import custombrowser.DialogOption;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Window;

public class DeleteBookmarkInformationController implements Initializable{
	@FXML
	public Label deleteBookmarkTitle;
	@FXML
	public Label deleteBookmarkURL;
	@FXML
	private Button okButton;
	@FXML
	private Button cancelButton;
	@SuppressWarnings("unused")
	private DialogOption selectedOption = DialogOption.CANCEL;
        /*
        @FXML*/
	void handleOkButtonAction(ActionEvent event){
		handleCloseAction(DialogOption.OK);
		File deleteBookmark =new File("./Bookmark/"+deleteBookmarkTitle.getText());
		deleteBookmark.delete();
		//System.out.println("削除完了");
	}
	
	@FXML
	void handleCancelButtonAction(ActionEvent event){
		handleCloseAction(DialogOption.CANCEL);
	}
	
	private void handleCloseAction(DialogOption selectedOption) {
	        this.selectedOption = selectedOption;
	        getWindow().hide();
	}
	private Window getWindow(){
		return deleteBookmarkURL.getScene().getWindow();
	}
        @Override
	public void initialize(URL url,ResourceBundle rb){
	}
}
