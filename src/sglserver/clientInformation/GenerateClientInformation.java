package sglserver.clientInformation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * MyInformation.xmlを編集する関数
 * SGLサーバがユーザIDを決定するのでそれをMyInformation.xmlに追加してあげます
 * @author kinbara,masato
 * @version 1.1
 * @作成日: 2007/7/17
 * @最終更新日:2008/10/31
 */
public class GenerateClientInformation {
	private Document	document;		// グループ情報のドキュメント
	private Element		root;			//     〃       のルートノード
	String filename = "src/sglserver/conf/usr/xml_files/MyInformation.xml";
	boolean	newxml = false;		// true :xmlを新しく作り直す場合
	
	/**
	 * Root要素にmyinformationを持ったXML文書を新規作成する
	 * @param filename			読み込みファイル
	 */
	public GenerateClientInformation(){
		try{
			// グループ情報XMLを新規作成する
			if(newxml == true){
				//　ドキュメントビルダーファクトリを生成
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				// ドキュメントビルダーを生成
				DocumentBuilder builder = factory.newDocumentBuilder();
				//　Documentオブジェクトを新規作成
				document = builder.newDocument();
				// ドキュメントにルート要素を作成
				root = document.createElement("MyInformation");
				// ドキュメントにルート要素を追加
				document.appendChild(root);
				//既存のグループ情報ファイルを読み込む場合
			}else{
				// ドキュメントビルダーファクトリを生成
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				// ドキュメントビルダーを生成
				DocumentBuilder builder = factory.newDocumentBuilder();
				// パースを実行してDocumentオブジェクトを取得(ファイル読み込み)
				document = builder.parse(new BufferedInputStream(new FileInputStream(filename)));
				// ルート要素を取得
				root = document.getDocumentElement();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
		
	/**
	 * ユーザIDを付加する
	 * @param peerid　ユーザID
	 * @author kinbara
	 */
	public void addID(String peerid){
		try{
			// ルート要素からGroupのノードリストを取得
			NodeList g = root.getElementsByTagName("User");
			// Groupの要素を、とりあえずルート要素にしておく
			Element gElement = this.document.getDocumentElement();
			Element uID = document.createElement("UserID"); // 要素(UserID)を作成
			gElement = (Element)g.item(0);
			Attr idtag = document.createAttribute("ID");
			idtag.setValue (peerid); // idをidtagに付ける
			uID.setAttributeNode(idtag); // uIDに属性idtagを付ける
			gElement.appendChild(uID); // userの下にuIDを付ける
			idtag.setValue (peerid); // idをidtagに付ける
			gElement.appendChild(uID); // uIDに属性idtagを付ける
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * このドキュメントを保存します
	 * @param filename 保存するファイル名(パスを含む)
	 * @throws Exception ファイルの保存時に起きたエラー
	 * @author kinbara
	 */
	public void saveFile() throws Exception {
		//xml保存 変換
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		//xml保存
		DOMSource source = new DOMSource(document);
		File newXML = new File( filename ); 
		FileOutputStream os = new FileOutputStream(newXML); 
		StreamResult result = new StreamResult(os); 
		transformer.transform(source, result);	
	}
}
