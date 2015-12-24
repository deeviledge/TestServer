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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

/*//////////////////////////////
*	ClassName:AddBookmarkInformationController
*	ブックマークの追加を確認、ブックマークの追加を行うクラス
*
*//////////////////////////////
public class AddBookmarkInformationController implements Initializable{
	@FXML
	public TextField addBookmarkTitle;
	@FXML
	public Label addBookmarkURL;
	@FXML
	private Button okButton;
	@FXML
	private Button cancelButton;
	@SuppressWarnings("unused")
	private DialogOption selectedOption = DialogOption.CANCEL;
	
	/*
	 *	OKボタンイベント:ブックマークにリンクファイル作成
	 */
	@FXML
	void handleOkButtonAction(ActionEvent event){
		handleCloseAction(DialogOption.OK);
		File newBookmark =new File("./Bookmark/"+addBookmarkTitle.getText()+".url");
		FileWriter filewriter;
		try {
			filewriter = new FileWriter(newBookmark);
			BufferedWriter bw = new BufferedWriter(filewriter);
			PrintWriter pw = new PrintWriter(bw);
			pw.println("[InternetShortcut]\n");
			pw.println("URL="+addBookmarkURL.getText());
			pw.close();
			try {
				newBookmark.createNewFile();
				//System.out.println("新しいブックマークの作成に成功しました");
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		
		
		
	}
	/*
	 *	Cancelボタンアクション：ブックマーク追加をキャンセル
	 */
	@FXML
	void handleCancelButtonAction(ActionEvent event){
		handleCloseAction(DialogOption.CANCEL);
	}
	
	/*
	 *	Closeボタン
	 */
	private void handleCloseAction(DialogOption selectedOption) {
	        this.selectedOption = selectedOption;
	        getWindow().hide();
	}
	
	/*
	 *	Windowを取得
	 */
	private Window getWindow(){
		return addBookmarkURL.getScene().getWindow();
	}
	/*
	 *イニシャライザ
	 */
        @Override
	public void initialize(URL url,ResourceBundle rb){
	}
}
