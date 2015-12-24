/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser.bookmark;

import custombrowser.DialogOption;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class EditBookmarkInformationController implements Initializable{
	@FXML
	public TextField editBookmarkTitle;
	@FXML
	public TextField editBookmarkURL;
	@FXML
	private Button okButton;
	@FXML
	private Button cancelButton;
	@SuppressWarnings("unused")
	private DialogOption selectedOption = DialogOption.CANCEL;
	File editBookmark;
	
	@FXML
	void handleOkButtonAction(ActionEvent event){
		handleCloseAction(DialogOption.OK);
		File editedBookmark;
		editedBookmark =new File("./Bookmark/"+editBookmarkTitle.getText());
		if(!editedBookmark.getName().contains(".")) {
			editedBookmark =new File("./Bookmark/"+editBookmarkTitle.getText()+".url");
		}
		
		if(editBookmark.renameTo(editedBookmark)){
			//System.out.println("ファイル名変更完了");
			FileWriter filewriter;
			try {
				filewriter = new FileWriter(editedBookmark);
				BufferedWriter bw = new BufferedWriter(filewriter);
				PrintWriter pw = new PrintWriter(bw);
				pw.println("[InternetShortcut]\n");
				pw.println("URL="+editBookmarkURL.getText());
				pw.close();
				
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		}else{
			//System.out.println("ファイル名変更失敗");
		}
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
		return editBookmarkURL.getScene().getWindow();
	}
	public void Fileget(){
		editBookmark =new File("./Bookmark/"+editBookmarkTitle.getText());
	}
	
	public void initialize(URL url,ResourceBundle rb){
		
	}
}