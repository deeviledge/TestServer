package custombrowser;


import custombrowser.bookmark.EditBookmarkInformationController;
import custombrowser.bookmark.DeleteBookmarkInformationController;
import custombrowser.bookmark.Bookmark;
import custombrowser.bookmark.AddBookmarkInformationController;
import custombrowser.cryptogram.AesDec;
import custombrowser.cryptogram.AesEnc;
import static custombrowser.CustomBrowserController.tabPane;
import custombrowser.groupinformation.SGLGroupController;
import java.awt.MouseInfo;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import sglclient.certificate.SglCall;
import sglclient.certificate.SglLogin;
import sglclient.certificate.SglLogout;
import sglclient.myinformation.MyInformation;
import sglclient.option.EditOptionXml;
import sglclient.preparation.LoginClient;
import sglclient.pskey.PSKeyRecieveClient;

public class CustomBrowserTabController extends CustomBrowser implements Initializable{
    
        private final int START_BROWSER = 0;    //ログイン時のメッセージ識別用
        private final int BUSY_BROWSER  = 1;    //0:起動時  1:ログインボタン押下時

	//private Desktop desktop = Desktop.getDesktop();
	// The FXMLLoader
	@SuppressWarnings("unused")
	private FXMLLoader fXMLLoader = new FXMLLoader();

	AesEnc enc;			//AesEncクラスのオブジェクトを宣言
	AesDec dec;			//AesEncクラスのオブジェクトを宣言
	XmlRead xmlread;	//XmlReadクラスのオブジェクトを宣言
	TagGrep taggrep;	

        
        @FXML
        private MenuItem LoginItem;    //SGLログインボタン
        
	@FXML
	private TextField urlField;	//URLText
	String SiteURL;
	String BeforeURL;
	@FXML
	private WebView webView = new WebView();	//WebView
	@FXML
	private WebEngine engine;	//BrowserEngine
	public static final String EVENT_TYPE_CLICK = "click";
	public static final String EVENT_TYPE_MOUSEOVER = "mouseover";
	public static final String EVENT_TYPE_MOUSEOUT = "mouseclick";
	boolean newTabFlag=true;
	//CustomBrowserTabController nextCBTC;
	@FXML
	private StackPane stackPane;
	@FXML
	private AnchorPane bookmarkAnchorPane;
	@FXML
	private AnchorPane webviewAnchorPane;
	@FXML
	private Button loadButton;
	@FXML
	private Button backButton;
	@FXML
	private Button forwardButton;

	@FXML
	private TitledPane bookmarkTitledPane;

	// テーブルに表示するブックマークのリスト
	ObservableList<Bookmark> bookmarks = FXCollections.observableArrayList();
	@FXML
	private TableView<Bookmark> bookmarkTableView;

	@FXML
	private TableColumn<Bookmark, String> siteColumn;
	@FXML
	private TableColumn<Bookmark, String> urlColumn;
	@FXML
	private Button addBookmarkButton;
	@FXML
	private Bookmark selectedBookmarkItem;	
	private boolean historyFlag=false;
	//@SuppressWarnings("rawtypes")
	//@FXML
	//private ComboBox historyComboBox;
	//@FXML
	//private Button encodeButton;
	//@FXML
	//private Button edecodeButton;

	@SuppressWarnings("rawtypes")
	@FXML
	private ComboBox encode_decodeGroupComboBox;
	@FXML
	private Tab encodeTab;
	@FXML
	private TitledPane encodeTitledPane;
	@FXML
	private TextArea encodedText;

	String EncodeFile_Path;
	String EncodeFile_Name;
	BufferedInputStream bis; // ファイルを読み込むための変数
	FileInputStream fis ;
	byte input_data[];
	String code;	//暗号化する時の文字コード
	String str ;	//読み込んだファイルの文字列を保存する変数
	boolean DecodedFlag=false;		//初期デコードフラグ
	boolean AutoDecodeFlag=false;	//自動デコードフラグ
        
        boolean LoginFlag=false;
        
	public CustomBrowserTabController() {
		//this.TabNumber=CustomBrowserController.TabNumbers;
	}

	@FXML
	private Button saveButton;
	//LoadButtonAction
	@FXML
	void handleLoadButtonAction(ActionEvent event) {
		String url = urlField.getText();
		WebEngine engine=webView.getEngine();
		engine.load(url);																								//URL_LOAD

	}

        
        @FXML
	void handleLoginButtonAction(ActionEvent event) throws IOException, InterruptedException {
           boolean cert_flg = false;
           EditOptionXml eox = new EditOptionXml();
           LoginClient LC = new LoginClient();	//事前準備（証明書の作成と交換）
            try{
                ServerSocket ssoc = new ServerSocket(12345,100);//接続
                ssoc.close();
            }
            catch (UnknownHostException e) {
                System.err.println(e);
            }
            catch (IOException e) { //SGLサーバが起動していれば実行
                    
                if(LC.first_flg==true){     //MyInformation.xmlが存在しなければユーザ登録をする)  
                    FXMLLoader loader=new FXMLLoader(getClass().getResource("FirstLoginDialog.fxml"));
                    loader.load();
                    Parent root = loader.getRoot();
                    FirstLoginDialogController FLDcontroller = loader.getController();
                    Scene scene = new Scene(root);
                    Stage firstlogindialog = new Stage(StageStyle.UTILITY);
                    firstlogindialog.setScene(scene);
                    firstlogindialog.setTitle("初期設定");
                    firstlogindialog.showAndWait();
                    MyInformation mi = new MyInformation();
                    File file = new File("src/sglclient/conf/key/PreSharedKey"+mi.getUsrID()+".xml");
                    LoginFlag = file.exists();
                }
                
            }
            if(LC.first_flg==false){
                try {
                FXMLLoader loader2=new FXMLLoader(getClass().getResource("LoginDialog.fxml"));
                loader2.load();
                Parent root2 = loader2.getRoot();
                LoginDialogController LDLcontroller = loader2.getController();
                Scene scene2 = new Scene(root2);
                Stage logindialog = new Stage(StageStyle.UTILITY);
                logindialog.setScene(scene2);
                logindialog.setTitle("確認");
                PSKeyRecieveClient pskc = new PSKeyRecieveClient(eox.getIP());	//認証とPreSharedKeyの受け取り
                SglLogin Login = new SglLogin();
                if(pskc.cert_flg == true){  //認証が完了すればログイン
                    Login.Login();
                    //SGLサーバにログイン
                    if(LoginFlag==false && Login.login_flg==true)LDLcontroller.setDialogMessage_Success(eox.getIP(),true,BUSY_BROWSER);
                    else LDLcontroller.setDialogMessage_Success(eox.getIP(),false,BUSY_BROWSER);
                    
                } 
                else LDLcontroller.setDialogMessage_Failure(eox.getIP());
                logindialog.showAndWait();
                LoginFlag = Login.login_flg;
                } catch (IOException ex) {
                    Logger.getLogger(CustomBrowserController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            LoginButtonChangeName();
            
	}
       
        
        //SGLのログアウトボタンの処理
        @FXML
	void handleLogoutButtonAction(ActionEvent event) {
            try{
                ServerSocket ssoc = new ServerSocket(12345,100);//接続
                ssoc.close();
                FXMLLoader loader2=new FXMLLoader(getClass().getResource("LoginDialog.fxml"));
                loader2.load();
                Parent root2 = loader2.getRoot();
                LoginDialogController LDLcontroller = loader2.getController();
                Scene scene2 = new Scene(root2);
                Stage logindialog = new Stage(StageStyle.UTILITY);
                logindialog.setScene(scene2);
                logindialog.setTitle("確認");
                LDLcontroller.setDialogMessage_CanNotLogout();
                logindialog.show();
            }
            catch (UnknownHostException e) {
                System.err.println(e);
            }
            catch (IOException e) { //SGLサーバが起動していれば実行
                    
                new SglLogout();
                LoginFlag=false;
                LoginButtonChangeName();
                
                
                try {
                    FXMLLoader loader=new FXMLLoader(getClass().getResource("LoginDialog.fxml"));
                    loader.load();
                    Parent root = loader.getRoot();
                    LoginDialogController LDLcontroller = loader.getController();
                    Scene scene = new Scene(root);
                    Stage logindialog = new Stage(StageStyle.UTILITY);
                    logindialog.setScene(scene);
                    logindialog.setTitle("確認");
                    LDLcontroller.setDialogMessage_Finish_Logout();
                    logindialog.show();
                } catch (IOException ex) {
                    Logger.getLogger(CustomBrowserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
	}
        
        //ログインユーザをサーバに問い合わせて表示する
        /*  いらないw
        @FXML
	void handleLoginUserInfoButtonAction(ActionEvent event) throws IOException {
            try{
                ServerSocket ssoc = new ServerSocket(12345,100);//接続
                ssoc.close();
                FXMLLoader loader2=new FXMLLoader(getClass().getResource("LoginDialog.fxml"));
                loader2.load();
                Parent root2 = loader2.getRoot();
                LoginDialogController LDLcontroller = loader2.getController();
                Scene scene2 = new Scene(root2);
                Stage logindialog = new Stage(StageStyle.UTILITY);
                logindialog.setScene(scene2);
                logindialog.setTitle("確認");
                LDLcontroller.setDialogMessage_Cannot_GetLoginUser();
                logindialog.show();
            }
            catch (UnknownHostException e) {
                System.err.println(e);
            }
            catch (IOException e) { //SGLサーバが起動していれば実行
                
                    FXMLLoader loader=new FXMLLoader(getClass().getResource("LookLoginUser.fxml"));
                    loader.load();
                    Parent root = loader.getRoot();
                    LookLoginUserController LLUcontroller = loader.getController();
                    Scene scene = new Scene(root);
                    Stage logindialog = new Stage(StageStyle.UTILITY);
                    logindialog.setScene(scene);
                    logindialog.setTitle("ログインユーザ一覧");
                    logindialog.showAndWait();
                
            }
            
	}
        */
        
        @FXML
	void handleGroupEditButtonAction(ActionEvent event) throws IOException {
            try{
                ServerSocket ssoc = new ServerSocket(12345,100);//接続
                ssoc.close();
                FXMLLoader loader2=new FXMLLoader(getClass().getResource("LoginDialog.fxml"));
                loader2.load();
                Parent root2 = loader2.getRoot();
                LoginDialogController LDLcontroller = loader2.getController();
                Scene scene2 = new Scene(root2);
                Stage logindialog = new Stage(StageStyle.UTILITY);
                logindialog.setScene(scene2);
                logindialog.setTitle("確認");
                LDLcontroller.setDialogMessage_Group();
                logindialog.showAndWait();
                
            }
            catch (UnknownHostException e) {
                System.err.println(e);
            }
            catch (IOException e) { 
                FXMLLoader loader=new FXMLLoader(getClass().getResource("groupinformation/SGLGroup.fxml"));
                loader.load();
                Parent root = loader.getRoot();
                SGLGroupController SGcontroller = loader.getController();
                Scene scene = new Scene(root);
                Stage groupdialog = new Stage(StageStyle.UTILITY);
                groupdialog.setScene(scene);
                groupdialog.setTitle("グループ管理");
                groupdialog.showAndWait();
                
            }
            
            
	}
        
        
        private void LoginButtonChangeName() {
            if(LoginFlag)LoginItem.setText("ログイン(済)");
            else LoginItem.setText("ログイン(未)");
	}
        
        @FXML
	void handleServerIPButtonAction(ActionEvent event) {
            try {
                FXMLLoader loader=new FXMLLoader(getClass().getResource("SetIP.fxml"));
                loader.load();
                Parent root = loader.getRoot();
                SetIPController sIPcontroller = loader.getController();
                Scene scene = new Scene(root);
                Stage setIP = new Stage(StageStyle.UTILITY);
                setIP.setScene(scene);
                setIP.setTitle("設定");
                setIP.showAndWait();
            } catch (IOException ex) {
                ex.printStackTrace();
                //System.out.println("catch");
            }
	}
        
	//ForwarButtonAction
	@FXML
	void handleForwardButtonAction(ActionEvent event){    
		WebEngine engine = webView.getEngine();
		final WebHistory history=engine.getHistory();																	//GET_History
		//ObservableList<WebHistory.Entry> entryList=history.getEntries();												//GET_HistoryList
		//int currentIndex=history.getCurrentIndex();																	//Now_state_History
		////System.out.println("currentIndex= "+currentIndex);
		////System.out.println(entryList.toString().replace("],","]\n"));
		Platform.runLater(new Runnable() { public void run() { history.go(1); } });										//PageBack<UseHistory


	}

	//BackButtonAction
	@FXML
	void handleBackButtonAction(ActionEvent event) {
		WebEngine engine = webView.getEngine();
		final WebHistory history=engine.getHistory();																	//GET_History
		//ObservableList<WebHistory.Entry> entryList=history.getEntries();												//GET_HistoryList
		//int currentIndex=history.getCurrentIndex();																	//Now_state_History
		////System.out.println("currentIndex= "+currentIndex);
		////System.out.println(entryList.toString().replace("],","]\n"));
		Platform.runLater(new Runnable() { public void run() { history.go(-1); } });					//PageBack<UseHistory
	}

	//addBookmarkButton
	@FXML
	void handleAddBookmarkButtonAction(ActionEvent event){
		WebEngine engine=webView.getEngine();
		String addBookmarkURL = engine.getLocation();
		String addBookmarkTitle = engine.getTitle();
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getResource("bookmark/AddBookmarkInformation.fxml"));
			loader.load();
			Parent root = loader.getRoot();
			AddBookmarkInformationController ABICcontroller = loader.getController();
			Scene scene = new Scene(root);
			Stage addBookmarkInformation = new Stage(StageStyle.UTILITY);
			addBookmarkInformation.setScene(scene);
			addBookmarkInformation.setTitle("確認");
			ABICcontroller.addBookmarkTitle.setText(addBookmarkTitle);
			ABICcontroller.addBookmarkURL.setText(addBookmarkURL);
			addBookmarkInformation.showAndWait();
		} catch (IOException ex) {
			Logger.getLogger(CustomBrowserTabController.class.getName()).
			log(Level.SEVERE, "読み込み失敗", ex);
		}
		BookmarkRead();
	}


	//HistoryComboBoxAction
	@FXML
	void selectHistoryComboBoxAction(ActionEvent event){
		if(CustomBrowserController.historyComboBox.getValue() instanceof Entry){
			Entry selectedEntry = (Entry) CustomBrowserController.historyComboBox.getValue();
			engine.load(selectedEntry.getUrl());
		}

	}
	
	//EncodeButtonAction
	@FXML
	void handleEncodeButtonAction(ActionEvent event){

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("HTML", "*.html")
				);
		File file=fileChooser.showOpenDialog(null);		
		if (file != null) {
			getEncodingFile(file);
			//System.out.println("ファイル読み込みOK");
			encrypt(file);
			SingleSelectionModel<Tab> TabSelectionModel =tabPane.getSelectionModel();
			TabSelectionModel.select(encodeTab);
			encodeTab.setDisable(false);

		}
	}
	 
	
	//DecodeButtonAction
	@FXML
	void handleDecodeButtonAction(ActionEvent event) throws TransformerException{
		//グループの取得
		xmlread.group = (String)encode_decodeGroupComboBox.getValue();
		dec.keys = xmlread.GetKey(); // グループ鍵を保存
		WebEngine engine=webView.getEngine();
		decode(engine);	//decode
		DecodedFlag=true;

	}
	 
	
	//getfile
	private void getEncodingFile(File file) {
		try {
			//desktop.open(file);
			EncodeFile_Path = file.getPath (); // ファイルパスの取得
			EncodeFile_Name = file.getName();
			encodeTitledPane.setText(EncodeFile_Name + " - " + EncodeFile_Path);
			fis = new FileInputStream (EncodeFile_Path);
			bis = new BufferedInputStream (fis);
			long datalength = 0;
			byte [] data = new byte [200000]; // 大きめの配列を用意
			do{
				datalength = bis.read(data,0,data.length);	//dataにファイルをbyte型で読み込む
				if(datalength == -1){	// データがない部分に0 を代入しておく
					datalength = 0;
				}
			}while(datalength>200000);
			//dataを読み込むときに余分についた0をとる
			byte []data2=new byte[(int)datalength];
			for(int i=0; i<datalength; i++){
				data2[i]=data[i];
			}
			input_data=data2;
			//System.out.println(new String (input_data));	// 読み込んだファイルを表示
			fis.close();
			bis.close ();

			//desktop.open(file);

		} catch (IOException ex) {
			Logger.getLogger(
			CustomBrowser.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	 
	
	//Encrypt
	private void encrypt(File file){

		xmlread.group =(String)encode_decodeGroupComboBox.getValue();	//グループの取得

		enc.keys = xmlread.GetKey();						//グループ鍵を保存
		code=TagGrep.taggrep(new String (input_data));	//文字コードの取得
		////System.out.println("文字コードOK");
		//System.out.println(code);
		if(code=="") code="UTF-8";
		//文字コードを指定してString型に変換
		try{
			str = new String (input_data,code);
		}catch ( UnsupportedEncodingException e1){
			e1.printStackTrace();
		}
		//System.out.println(str);	//読み込んだデータの表示
		encodedText.setText("");

		AesEnc.input = str;
		enc.Aes_enc();
		encodedText.setText(enc.base64_enc);

	}
	
	//Decode
	@SuppressWarnings("static-access")
	private void decode(WebEngine engine){

		NodeList htmlBody = engine.getDocument().getElementsByTagName("BODY");
		org.w3c.dom.Node itemBody = htmlBody.item(0);
		String decodingHtml = itemBody.getTextContent();
		//System.out.println(decodingHtml);
		AesDec.input = new String(decodingHtml);
		dec.Aes_dec(); //AES_encの呼び出し
		engine.loadContent(new String(dec.plain));
		////System.out.println(System.getProperty("user.dir"));
		
	}
	 
	/*public void Tabreturn(){

		ObservableList<Tab> TabList = this.tabPane.getTabs();
		//int Tabindex = tabPane.getSelectionModel().getSelectedIndex();
		//System.out.println("ok");
	}*/
	
	private void SaveFile(String content, File file){
		try {
			FileWriter fileWriter = null;
			if(!file.getName().contains(".")) {
				fileWriter = new FileWriter(file+".html");	//拡張子追加
			}else{
				fileWriter = new FileWriter(file);
			}
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
			Logger.getLogger(CustomBrowserTabController.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
	 
	//webviewのエンジンを取得
	public WebEngine getEngine(){
		return this.engine;
	}
	//webviewのUrlをロード
	public void loadURL(String URL){
		//System.out.println("URLをロードします。");
		webView.getEngine().load(URL);
	}
	// ファイル内容をを文字列化するメソッド。
	public static String fileToString(File file) throws IOException {
		BufferedReader br = null;
		try {
			// ファイルを読み込むバッファドリーダを作成します。
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			// 読み込んだ文字列を保持するストリングバッファを用意します。
			StringBuffer sb = new StringBuffer();
			// ファイルから読み込んだ一文字を保存する変数です。
			int c;
			// ファイルから１文字ずつ読み込み、バッファへ追加します。
			while ((c = br.read()) != -1) {
				sb.append((char) c);
			}
			// バッファの内容を文字列化して返します。
			return sb.toString();
		} finally {
			// リーダを閉じます。
			br.close();
		}
	}

	//正規表現でURLを取得
	public String URLgrep(String BookmarkData){
		String target="";

		String regex="(http://|https://|file:///C:/){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+";
		Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = ptn.matcher(BookmarkData);
		while(matcher.find()){
			target+=matcher.group();
		}
		//System.out.println(target);
		return target;
	}
	//正規表現でメールアドレスを取得
	public String MailAdressgrep(String str){
		String target="";

		String regex="[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+";
		Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = ptn.matcher(str);
		while(matcher.find()){
			target+=matcher.group();
		}
		////System.out.println(target);
		return target;
	}

	//Bookmark読み込み
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void BookmarkRead(){
		//ブックマークファイル管理
		File dir =new File("./Bookmark");
		if(!dir.exists()){
			return;
		}
		File[] files = dir.listFiles();
		String bookmarkURL="";
		bookmarks.clear();
		for(File file : files){
			//System.out.println(file);
			try {
				String BookmarkData=fileToString(file);
				bookmarkURL=URLgrep(BookmarkData);
				//System.out.println(BookmarkData);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			bookmarks.add(
					new Bookmark(file.getName(),bookmarkURL)
					);
		}

		// カラムとBookmarkクラスのプロパティの対応付け
		siteColumn.setCellValueFactory(new PropertyValueFactory("site"));
		urlColumn.setCellValueFactory(new PropertyValueFactory("url"));
		bookmarkTableView.setItems(bookmarks);
	}

	//Initialize//
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initialize(URL url, ResourceBundle rb) {
            
            

		//System.out.println(stackPane.getChildren());
		engine = webView.getEngine();																					//Load_Engine
		engine.load("http://www.google.jp/");	//Load_URL
		
		//bookmarktableのサイズ設定
				siteColumn.prefWidthProperty().bind(bookmarkTableView.widthProperty().divide(2));
				urlColumn.prefWidthProperty().bind(bookmarkTableView.widthProperty().divide(2));
		//StackPane入れ替えアクション
		bookmarkTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(bookmarkTitledPane.isExpanded()==true){
					Node node = stackPane.getChildren().remove(stackPane.getChildren().size() - 1);
					stackPane.getChildren().add(0, node);
					//System.out.println("ブックマークを前に");
				}else{
					Node node = stackPane.getChildren().remove(stackPane.getChildren().size() - 1);
					stackPane.getChildren().add(0, node);
					//System.out.println("ウェブビューを前に");
				}
			}
		});
		
		//ブックマーク読み込み
		BookmarkRead();
		//ブックマークのポップアップメニュー
		final ContextMenu popup = new ContextMenu();
		MenuItem popupItemEdit = new MenuItem("編集");
		MenuItem popupItemDelete = new MenuItem("削除");
		//ブックマーク編集
		popupItemEdit.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				//System.out.println("編集します");
				String editBookmarkTitle =selectedBookmarkItem.getSite();
				String editBookmarkURL = selectedBookmarkItem.getURL();
				try{
					FXMLLoader loader=new FXMLLoader(getClass().getResource("bookmark/EditBookmarkInformation.fxml"));
					loader.load();
					Parent root = loader.getRoot();
					EditBookmarkInformationController EBICcontroller = loader.getController();
					Scene scene = new Scene(root);
					Stage editBookmarkInformation = new Stage(StageStyle.UTILITY);
					editBookmarkInformation.setScene(scene);
					editBookmarkInformation.setTitle("ブックマーク編集");
					EBICcontroller.editBookmarkTitle.setText(editBookmarkTitle);
					EBICcontroller.editBookmarkURL.setText(editBookmarkURL);
					EBICcontroller.Fileget();
					editBookmarkInformation.showAndWait();
				} catch (IOException ex) {
					Logger.getLogger(CustomBrowserTabController.class.getName()).
					log(Level.SEVERE, "読み込み失敗", ex);
				}
				BookmarkRead();
			}
		});
		popupItemDelete.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				//System.out.println("削除します");
				String deleteBookmarkTitle =selectedBookmarkItem.getSite();
				String deleteBookmarkURL = selectedBookmarkItem.getURL();
				try{
					FXMLLoader loader=new FXMLLoader(getClass().getResource("bookmark/DeleteBookmarkInformation.fxml"));
					loader.load();
					Parent root = loader.getRoot();
					DeleteBookmarkInformationController DBICcontroller = loader.getController();
					Scene scene = new Scene(root);
					Stage deleteBookmarkInformation = new Stage(StageStyle.UTILITY);
					deleteBookmarkInformation.setScene(scene);
					deleteBookmarkInformation.setTitle("ブックマーク削除");
					DBICcontroller.deleteBookmarkTitle.setText(deleteBookmarkTitle);
					DBICcontroller.deleteBookmarkURL.setText(deleteBookmarkURL);
					deleteBookmarkInformation.showAndWait();
				} catch (IOException ex) {
					Logger.getLogger(CustomBrowserTabController.class.getName()).
					log(Level.SEVERE, "読み込み失敗", ex);
				}
				BookmarkRead();
			}
		});
		popup.getItems().addAll(popupItemEdit,popupItemDelete);

		//URL表示//
		String default_url = engine.getLocation();																		//Get URLLocation_now
		urlField.setText(default_url);																					//SetURLLocation_now>>URLText
		////////////
		
		enc = new AesEnc ( this ); 			//AesEncクラスのオブジェクトを生成
		
		dec = new AesDec ( this ); 			//AesDecクラスのオブジェクトを生成
		xmlread = new XmlRead ( this ); 	//XmlReadクラスのオブジェクトを生成
		xmlread . Read ();	//xmlファイルの読み込み
		/*
		encode_decodeGroupComboBox.getItems().clear();
		encode_decodeGroupComboBox.getItems().addAll((Object[])xmlread.groups);
		 */


		//WebView更新時の処理
		Worker<Void> loadWorker = engine.getLoadWorker();																//LoadWorker<for ChangeListener
		loadWorker.stateProperty().addListener(new ChangeListener<Worker.State>() {										//ChangeListener:Check WebView_Changed
			@Override
			public void changed(ObservableValue<? extends Worker.State> ov, Worker.State old, Worker.State next) {		//_Changed_WebView>Action
				Tab Tab = CustomBrowserController.tabPane.getSelectionModel().getSelectedItem();
				if (next == Worker.State.SCHEDULED) {																	//If Load Start
					//FADEOUT//					
					FadeTransition fadeOut = new FadeTransition(Duration.millis(1_000), webView);						//Set_FadeOut WebView
					fadeOut.setToValue(0.0);																			//
					fadeOut.play();																						//FadeOut WebView            	    
					///////////////

					//URL表示//                    
				
					if(engine.getLocation().matches("")==false){
						SiteURL = engine.getLocation();																	//Get URLLocation_now
					}
					urlField.setText(SiteURL);																				//SetURLLocation_now>>URLText					
					///////////////

				} else if (next == Worker.State.SUCCEEDED) {															//If Load Complete
					NodeList titleTag=null;
					String checktitle = null;

					//ハイパーリンク処理
					EventListener listener = new EventListener() {
						@Override
						public void handleEvent(Event ev) {
							String domEventType = ev.getType();
							//System.err.println("EventType: " + domEventType);
							if (domEventType.equals(EVENT_TYPE_CLICK)) {
								String href = ((Element)ev.getTarget()).getAttribute("href");
								//System.out.println(href);
								String MailAdress = MailAdressgrep(href);
								/*if(MailAdress!=""){
                                                                    try {
                                                                        //System.out.println(MailAdress);
                                                                        String mailURIStr=String.format(href,MailAdress);
                                                                        URI mailURI = new URI(mailURIStr);
                                                                        //desktop.browse(mailURI);
                                                                    } catch (URISyntaxException ex) {
                                                                        Logger.getLogger(CustomBrowserTabController.class.getName()).log(Level.SEVERE, null, ex);
                                                                    }
									
								}*/
							}
						}
					};
					Document doc = webView.getEngine().getDocument();
					NodeList nodeList = doc.getElementsByTagName("a");
					for (int i = 0; i < nodeList.getLength(); i++) {
						((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_CLICK, listener, false);
						//((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_MOUSEOVER, listener, false);
						//((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_MOUSEOVER, listener, false);
					}
					try{
						titleTag=engine.getDocument().getElementsByTagName("title");
						org.w3c.dom.Node itemtitle = titleTag.item(0);
						checktitle = itemtitle.getTextContent();

						//System.out.println("タイトル:"+checktitle);
					}catch(NullPointerException e){
						e.printStackTrace();
					}

					if(DecodedFlag==true){
						DecodedFlag=false;
						AutoDecodeFlag=true;
						//System.out.println("オートでコーディングフラッグおん");
					}else if(AutoDecodeFlag==true && checktitle==null){
						//グループの取得
						//System.out.println("group:"+CustomBrowserController.getGroupComboBoxItem());
						xmlread.group = CustomBrowserController.getGroupComboBoxItem();
						dec.keys = xmlread.GetKey(); // グループ鍵を保存
						decode(engine);
						//System.out.println("オートデコーディング");
						DecodedFlag=true;
					}else{
						AutoDecodeFlag=false;
					}
					//履歴更新処理
					if(historyFlag==true){
						Object addEntry = engine.getHistory().getEntries().get(engine.getHistory().getEntries().size()-1);
						////System.out.println("addEntrylist;"+engine.getHistory().getEntries());
						////System.out.println("addEntry"+addEntry);
						CustomBrowserController.setEntry(addEntry);
						historyFlag=false;
					}
					//FADEIN//
					FadeTransition fadeIn = new FadeTransition(Duration.millis(1_000), webView);	//Set_FadeIn WebView
					fadeIn.setToValue(1.0);
					fadeIn.play();			//FadeIn WebView
					////////////

					//タブのタイトル表示//
					String pagetitle=engine.getTitle();
					if(newTabFlag==false){
						Tab.setText(pagetitle);
					}else{ newTabFlag= false;}
					//System.out.println(pagetitle);
					//URL表示//
					if(SiteURL!=null){
						//System.out.println("SiteURL:"+SiteURL);
					}
					else if (engine.getLocation().matches("")){
						SiteURL=BeforeURL;
					}
					else{
						SiteURL = engine.getLocation();			//Get URLLocation_now
						BeforeURL=SiteURL;
					}
					urlField.setText(SiteURL);				//SetURLLocation_now>>URLText
					//Tabreturn();
					

					//｛戻る,進む｝ボタン{活性化,非活性化}////////////////////////
					final WebHistory history=engine.getHistory();				//GET_History
					ObservableList<WebHistory.Entry> entryList=history.getEntries();	//GET_HistoryList
					int currentIndex=history.getCurrentIndex();				//Now_state_History
					int lastIndex=entryList.size();						//HistoryListSize_now
					////System.out.println("lastIndex= "+lastIndex);

					if(currentIndex==0) backButton.setDisable(true);			//If there is no_page for Page_Back,Set BackButton disable
					else backButton.setDisable(false);					//If there is page Page_Back,Set BackButton active

					if(currentIndex==lastIndex-1)	forwardButton.setDisable(true);         //If there is no_page for Page_Forward,Set ForwardButton disable
					else forwardButton.setDisable(false);                                   //If there is page for Page_Forward,Set ForwardButton active
					//////////////////////////////////////////////////////////////
				}
			}
		}
				);

		
		// テーブルの選択位置が変化したら、WebViewでそのサイトを表示
		TableView.TableViewSelectionModel<Bookmark> selectionModel = bookmarkTableView.getSelectionModel();
		selectionModel.selectedItemProperty().addListener(new ChangeListener<Bookmark>() {
			@Override
			public void changed(ObservableValue<? extends Bookmark> value, Bookmark old, Bookmark next) {
				String url = next.getURL();
				engine.load(url);
			}
		});

		bookmarkTableView.setEditable(false);
		final EventHandler event = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				//クリックでページジャンプ
				if (me.getButton() == MouseButton.PRIMARY) {
					TableView.TableViewSelectionModel<Bookmark> selectionModel = bookmarkTableView.getSelectionModel();
					String url=selectionModel.getSelectedItem().getURL();
					engine.load(url);
				}
				//右クリックでポップアップ表示
				if(me.getButton()==MouseButton.SECONDARY){

					TableView.TableViewSelectionModel<Bookmark> selectionModel = bookmarkTableView.getSelectionModel();
					selectedBookmarkItem=selectionModel.getSelectedItem();
					int x=(int)MouseInfo.getPointerInfo().getLocation().getX();
					int y=(int)MouseInfo.getPointerInfo().getLocation().getY();
					popup.show(bookmarkTableView, x, y);
				}
			}
		};
		bookmarkTableView.addEventHandler(MouseEvent.MOUSE_CLICKED,event);
		//履歴更新時HistoryComboBoxの中身を変更
		engine.getHistory().getEntries().addListener(new ListChangeListener<Entry>(){

			@Override
			public void onChanged(ListChangeListener.Change<? extends Entry> change) {
				if(historyFlag==false){
					historyFlag=true;
					change.next();
				}
			}           
		});


	}
}