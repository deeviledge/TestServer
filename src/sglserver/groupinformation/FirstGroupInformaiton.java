package sglserver.groupinformation;

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
import org.w3c.dom.NodeList;

/**
 * GroupInformation.xmlを新規作成する関数
 * このXMLをリセットし新しく使用したい場合に使用して下さい
 * @author masato
 * @value 1.1
 * @作成日: 2008/11/05
 * @最終更新日:2008/10/31
 */
public class FirstGroupInformaiton {
	
	private Document	document;		// グループ情報のドキュメント
	private Element		root;			//     〃       のルートノード	
	private int             GroupCount;
	String filename = "src/sglserver/conf/usr/xml_files/GroupInformation.xml";
	
	/**
	 * Root要素にGroupInformationを持ったXML文書を新規作成する
	 * @param filename	読み込みファイル
	 */
	public FirstGroupInformaiton(){
		try{
			//　ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
			//Documentオブジェクトを新規作成
			document = builder.newDocument();
			// ドキュメントにルート要素を作成
			root = document.createElement("GroupInformation");
			// ドキュメントにルート要素を追加
			document.appendChild(root);		
			// 所定のタグ名とともに、すべての Elements の NodeList を取得
			NodeList gInfolist = root.getElementsByTagName("GroupInformation");
			// gInfolistの0番目の要素としてgInfoを作成
			Element gInfo = (Element)gInfolist.item(0);			
			//　gcnt　：グループの総数
			GroupCount = 0;
			String gcnt = Integer.toString(GroupCount);			
			// gInfoにGroupCount要素を作成
			gInfo = document.createElement("GroupCount");
			// 指定された文字列を持つTextノードを作成
			gInfo.appendChild(document.createTextNode(gcnt));
			root.appendChild(gInfo);
			saveFile();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * このドキュメントを保存します
	 * @param filename 保存するファイル名(パスを含む)
	 * @throws Exception ファイルの保存時に起きたエラー
	 * @author masato
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
