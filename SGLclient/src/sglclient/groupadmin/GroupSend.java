package sglclient.groupadmin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * MyInformationXMLを扱うクラス
 * 
 * @author masato
 * 2007/7/19
 */


public class GroupSend{
	static String xmlfile = null;
	static Document document = null;
	Element root = null;

	/**
	 * コンストラクタ
	 */
	public GroupSend(){
		try{
			xmlfile = "src/sglclient/conf/usr/xml_files/GroupSend.xml";
			//System.out.print(xmlfile);
			//ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			//ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			//パースを実行してDocumentオブジェクトを取得
			document = builder.parse(new BufferedInputStream(new FileInputStream(xmlfile)));
			//ルート要素を取得
			root = document.getDocumentElement();
			
			}catch(Exception e){
				System.err.println("can't read GroupSend.xml!");
			}
		}
	/**
	 * グループ名を読み込む
	 * 
	 */
	public String getGroupName(){
		NodeList list = root.getElementsByTagName("Group");
		Element group = (Element)list.item(0);
		String groupname =  group.getAttribute("xmlns:GroupName");
		return groupname;
	}
	/**
	 * グループIDを読み込む
	 * 
	 */
	public String getGroupID(){
		NodeList list = root.getElementsByTagName("Group");
		Element group = (Element)list.item(0);
		String groupname =  group.getAttribute("xmlns:ID");
		return groupname;
	}
	/**
	 * グループメンバーのユーザIDを読み込むクラス
	 * 
	 */
	public int[] getUsersID(){
		// ルート要素を取得
		Element root = document.getDocumentElement();
		// User要素のリストを取得
		NodeList u = root.getElementsByTagName("Peer");
		int[] IDs = new int[u.getLength()];
		for(int i=0;i<IDs.length;i++){
			// User要素を取得
			Element uElement = (Element)u.item(i);
			// ID要素のリストを取得
			NodeList idList = uElement.getElementsByTagName("ID");
			// ID要素を取得して、最初の子ノードの値を取得
			String sid = idList.item(0).getFirstChild().getNodeValue();
			// 文字から数字への変換
			IDs[i] = Integer.parseInt(sid);
		}
		return IDs;
	}
	/**
	 * ユーザIPを読み出すクラス
	 * 
	 */
	
	public String getUserIP(int id){
		String userIP=null;
		// ルート要素を取得
		Element root = document.getDocumentElement();
		// User要素のリストを取得
		NodeList u = root.getElementsByTagName("Peer");
		int[] IDs = new int[u.getLength()];
		for(int i=0;i<IDs.length;i++){
			// User要素を取得
			Element uElement = (Element)u.item(i);
			// ID要素のリストを取得
			NodeList idList = uElement.getElementsByTagName("ID");
			// ID要素を取得して、最初の子ノードの値を取得
			String sid = idList.item(0).getFirstChild().getNodeValue();
			// 文字から数字への変換
			IDs[i] = Integer.parseInt(sid);
			if(IDs[i]==id){
				NodeList idList2 = uElement.getElementsByTagName("IP");
				// ID要素を取得して、最初の子ノードの値を取得
				userIP = idList2.item(0).getFirstChild().getNodeValue();
			}
			
		}
		return userIP;
		
	}
	
	/**
	 * ユーザ名を読み出すクラス
	 * 
	 */
	
	public String[] getUserName(){
		
		// ルート要素を取得
		Element root = document.getDocumentElement();
		// User要素のリストを取得
		NodeList u = root.getElementsByTagName("Peer");
		String[] names = new String[u.getLength()];
		int[] IDs = new int[u.getLength()];
		for(int i=0;i<IDs.length;i++){
			// User要素を取得
			Element uElement = (Element)u.item(i);
			// ID要素のリストを取得
			NodeList idList = uElement.getElementsByTagName("ID");
			// ID要素を取得して、最初の子ノードの値を取得
			String sid = idList.item(0).getFirstChild().getNodeValue();
			// 文字から数字への変換
			IDs[i] = Integer.parseInt(sid);
			
				NodeList idList2 = uElement.getElementsByTagName("Name");
				// ID要素を取得して、最初の子ノードの値を取得
				names[i] = idList2.item(0).getFirstChild().getNodeValue();
			
			
		}
		return names;
		
	}
	/**
	 * グループ鍵（中間鍵）を取得
	 * 
	 */
	public String getKey(){
		// GroupCount要素のリストを取得
		NodeList gc = root.getElementsByTagName("SercretKey");
		// GroupCount要素を取得
		Element gcElement = (Element)gc.item(0);
		// GroupCount要素の最初の子ノードの値を取得
		String key = gcElement.getFirstChild().getNodeValue();
		
		return key;
	}
	/**
	 * グループ名からグループの人数を取得
	 * 
	 */
	public int getGroupValue(){
		// ルート要素を取得
		Element root = document.getDocumentElement();
		// User要素のリストを取得
		NodeList u = root.getElementsByTagName("Peer");
		int[] IDs = new int[u.getLength()];
		
		return IDs.length;
	}
	
	/**
	 * グループ名からグループの人数を取得
	 * 
	 */
	public BigInteger getGroupKey(String gname){
		BigInteger groupkey = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				groupkey = new BigInteger(group.getAttribute("xmlns:key"));
			}
		}
		return groupkey;
	}
	/**
	 * 中間鍵を保存
	 * @throws Exception 
	 * 
	 */
	public void SaveKey(String key) throws Exception{
		
		NodeList groupkeyinfo = root.getChildNodes();
		for(int i=0;i<groupkeyinfo.getLength();i++){
			if(groupkeyinfo.item(i).getNodeName().equals("SercretKey")){
				root.removeChild(groupkeyinfo.item(i));
			}
		}
		
		Element sk = document.createElement("SercretKey");
		sk.appendChild(document.createTextNode(key));
		root.appendChild(sk);
		
		//xml保存 変換
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		//xml保存
		DOMSource source = new DOMSource(document);
		File newXML = new File( xmlfile ); 
		FileOutputStream os = new FileOutputStream(newXML); 
		StreamResult result = new StreamResult(os); 
		transformer.transform(source, result);
	}
}
