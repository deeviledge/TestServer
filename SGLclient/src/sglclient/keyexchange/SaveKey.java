package sglclient.keyexchange;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;

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
 * 
 * @author fujino
 * 更新日 :7/20
 */

public class SaveKey {
	
	
	private Document	document;		// グループ情報のドキュメント
	private Element		root;			//     〃       のルートノード
	
	String filename = "src/sglclient/conf/usr/xml_files/MyInformation.xml";
	
	/**
	 * 
	 * @param gname
	 * @param gvalue
	 * @param group_key
	 * @throws Exception
	 */
	public SaveKey(String gname, String gvalue, String group_key) throws Exception{
		
		try{

			System.out.println("key saving...");
			
			byte[] dat;
			dat = getStringDigest(group_key);

			String key = new String(dat);
			
			
			byte[] a = key.getBytes();
			
			
			
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得(ファイル読み込み)
			document = builder.parse(new BufferedInputStream(new FileInputStream(filename)));
			// ルート要素を取得
			root = document.getDocumentElement();
			
			NodeList uInfolist = root.getElementsByTagName("User");
			Element uInfo = (Element)uInfolist.item(0);
			
			NodeList gInfolist = uInfo.getElementsByTagName("Group");
			Element gInfo = (Element )gInfolist.item(0);
			if( gInfo != null ){
				for(int i=0; i<gInfolist.getLength(); i++){
					String getgname = ((Element)gInfolist.item(i)).getAttribute("xmlns:Name");
					if(getgname.equals(gname)){
						uInfo.removeChild( (Element)gInfolist.item(i) );
						break;
					}
				}
			}
			
			Element group = document.createElement("Group");
			
			// グループ名
			Attr groupname  = document.createAttribute("xmlns:Name");
			// グループメンバ数
			Attr groupvalue = document.createAttribute("xmlns:value");
			// グループ鍵
			Attr groupkey   = document.createAttribute("xmlns:key");
			
			groupvalue.setValue(gvalue);
			groupname.setValue(gname);
			//groupkey.setValue(new String(a));
			groupkey.setValue(group_key);
			
			group.setAttributeNode(groupname);
			group.setAttributeNode(groupvalue);
			group.setAttributeNode(groupkey);
			
			uInfo.appendChild(group);
			
			
			// xml保存 変換
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			
			//xml保存
			DOMSource source = new DOMSource(document);
			File newXML = new File( filename ); 
			FileOutputStream os = new FileOutputStream(newXML); 
			StreamResult result = new StreamResult(os); 
			transformer.transform(source, result);
			
			System.out.println("グループ鍵を保存しました");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}
	
	public static byte[] getStringDigest(String data) throws Exception {
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] dat = data.getBytes();
		md.update(dat);         // dat配列からダイジェストを計算する
		
		return md.digest();
	}
}
