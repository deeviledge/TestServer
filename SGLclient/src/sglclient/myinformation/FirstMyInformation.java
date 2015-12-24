package sglclient.myinformation;

import java.io.File;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * MyInformation.xmlを新規作成する関数
 * このXMLをSGLサーバに直接渡し、ユーザ登録を行います
 * @author kinbara,masato
 * @value 1.1
 * @作成日: 2007/10/28
 * @最終更新日:2008/10/31
 */
public class FirstMyInformation {
	private Document	document;		// グループ情報のドキュメント
	private Element		root;			//     〃       のルートノード	
	String filename = "src/sglclient/conf/usr/xml_files/MyInformation.xml";
	
	/**
	 * Root要素にMyInformationを持ったXML文書を新規作成する
	 * @param filename	読み込みファイル
	 */
	public FirstMyInformation(){
		try{
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ユーザ情報(ユーザ名とメールアドレス)を作成
	 * @param Name　ユーザ名
	 * @param mailad　メールアドレス
	 * @author kinbara
	 */
	public void addUser(String Name,String mailad){
		try{
			Element user = document.createElement("User"); // 要素(User)を作成
			root.appendChild(user); // root(Test)の下にuserを付ける
			Element uId = document.createElement("UserID");
			uId.appendChild(document.createTextNode("0002"));
			user.appendChild(uId);
			Element uName = document.createElement("UserName"); // 要素(UserName)を作成
			uName.appendChild(document.createTextNode(Name)); // uNameにNameを付ける
			user.appendChild(uName); // userの下にuNameを付ける
			Element address = document.createElement("MailAddress");
			address.appendChild(document.createTextNode(mailad));
			user.appendChild(address);
			Element certification = document.createElement("Certification");
			String cert = "conf/key/ca/"+Name+".cer";
			certification.appendChild(document.createTextNode(cert));
			user.appendChild(certification);	
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

