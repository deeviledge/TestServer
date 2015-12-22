package sglserver.deletegroupinformation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * グループ削除された際に, グループメンバに削除通知を送信するためのID番号保管場所を扱うクラス
 * DeleteGroup.xml
 * @author kiryuu,masato
 * @version 1.1
 * @作成日: 2007/12/20
 * @最終更新日:2008/10/31
 */
public class DeleteGroupInfoXml {
	static String xmlfile = null;
	static Document document = null;
	static Element root = null;

	/**
	 * コンストラクタ
	 */
	public DeleteGroupInfoXml(){
		try{		
			xmlfile = "src/sglserver/conf/usr/xml_files/DeleteGroup.xml";
			//ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			//ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			//パースを実行してDocumentオブジェクトを取得
			document = builder.parse(new BufferedInputStream(new FileInputStream(xmlfile)));
			//ルート要素を取得
			root = document.getDocumentElement();
		}catch(Exception e){
			System.err.println("can't read DeleteGroup.xml!");
		}
	}
	
	/**
	 * ユーザ削除(他のユーザが削除した場合のときのためのメソッド)
	 * @param id ユーザID
	 * @param oos　直前で生成したObjectOutputStream
	 * @author kiryuu
	 */
	public void DeleteGroup(String id,ObjectOutputStream oos){
		boolean notdelete = false; // ユーザのidが削除リストに存在した際にtrue
		String usersend = "delete:";
		String[] deletegrouplist = this.getDeleteGroup(id); // ユーザのidからユーザの「削除されたグループ」を抽出
		for(int i=0;i<deletegrouplist.length;i++){
			if(deletegrouplist[i]==null){ // 空の場合
				if(i == 0){ // ユーザの属するグループがどこも削除されていなかった場合
					usersend = "notdelete:"; // ユーザに送信する文字列(削除しない)
					notdelete = true; // trueに
				}
				break; // ループから抜け出す
			}
			//	ユーザのIDが記述されていない削除グループがxmlにあればグループ自体を削除	
			this.deletegroup(deletegrouplist[i]);
			usersend += deletegrouplist[i] + ":";
		}
		if(notdelete == false){ // 削除した場合
			System.out.println("ユーザ:" + id + "のidを削除グループから削除しました。");
		}else{
			System.out.println("削除する必要はありません。");
		}	
		// ユーザのIDが記述されていない削除グループがxmlにあればグループ自体を削除	
		//this.deletegroup(gname)
		try {
			oos.writeObject(usersend);	//送信
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 削除されたユーザのグループを返す関数
	 * @param id ユーザID
	 * @return　削除されたグループ名の配列
	 * @author kiryuu
	 */
	public String[] getDeleteGroup(String id){
		NodeList list = root.getElementsByTagName("Group");
		String[] groupname = new String[list.getLength()]; // 取得したグループ名を格納する
		String name = ""; // 
		String deleteid = ""; // 削除されたグループに属しているユーザのid
		Node node;
		int addcount = 0;
		
		for(int i=0;i<list.getLength();i++){
			Element group = (Element)list.item(i);
			NodeList list2 = group.getElementsByTagName("ID");
			for(int j=0;j<list2.getLength();j++){
				Element delete = (Element)list2.item(j);
				deleteid = delete.getAttribute("xmlns:ID");
				if(deleteid.equalsIgnoreCase(id)){
					name = group.getAttribute("xmlns:GroupName");
					groupname[addcount] = name; // 消去されたグループ名を格納
					++addcount;
					node = list.item(i);
					node.removeChild(delete); // 格納したのでIDのノードを消す
				}
			}
		}
		try {
			this.saveFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupname;
	}
	
	/**DeleteGroup.xmlにグループを作る
	 * @param Name グループ名
	 * @param id メンバのID番号
	 * @author kiryuu
	 */
	public void makegroup(String Name,String[] id){
		try{
			// 要素(Group)を作成
			Element gname = document.createElement("Group"); 
			//所定の名前の Attr を作成します// AtterとはElement オブジェクトの属性の 1 つ
			Attr gtag = document.createAttribute("xmlns:GroupName");
			//groupに属性gtagを付ける
			gname.setAttributeNode(gtag);
			 // Nameをgtagに付ける
			gtag.setValue (Name);
			//root(DeleteGroupInfo)の下にグループ名を付ける
			root.appendChild(gname); 
			for(int i=0; i<id.length;i++){	
				Element uID = document.createElement("ID"); // 要素(ID)を作成
				//所定の名前の Attr を作成します// AtterとはElement オブジェクトの属性の 1 つ
				Attr idtag = document.createAttribute("xmlns:ID");
				//  uIDに属性idtagを付ける
				uID.setAttributeNode(idtag);
				 // idをidtagに付ける
				idtag.setValue (id[i]);
				 // userの下にuIDを付ける
				gname.appendChild(uID);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**DeleteGroup.xmlにグループを作る
	 * (ArrayList版)
	 * @param Name グループ名
	 * @param id メンバのID番号
	 * @author kawata
	 */
	public void makegroup2(String Name,ArrayList id){
		try{
			// 要素(Group)を作成
			Element gname = document.createElement("Group"); 
			//所定の名前の Attr を作成します// AtterとはElement オブジェクトの属性の 1 つ
			Attr gtag = document.createAttribute("xmlns:GroupName");
			//groupに属性gtagを付ける
			gname.setAttributeNode(gtag);
			 // Nameをgtagに付ける
			gtag.setValue (Name);
			//root(DeleteGroupInfo)の下にグループ名を付ける
			root.appendChild(gname); 
			for(int i=0; i<id.size();i++){	
				Element uID = document.createElement("ID"); // 要素(ID)を作成
				//所定の名前の Attr を作成します// AtterとはElement オブジェクトの属性の 1 つ
				Attr idtag = document.createAttribute("xmlns:ID");
				//  uIDに属性idtagを付ける
				uID.setAttributeNode(idtag);
				 // idをidtagに付ける
				idtag.setValue ((String)id.get(i));
				 // userの下にuIDを付ける
				gname.appendChild(uID);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 *DeleteGroup.xmlに追加されたグループからユーザーのID番号を削除する 
	 * @param gname 選択されたグループ名
	 * @param id ユーザーのID
	 * @author kiryuu
	 */
	public void deleteuserid(String gname ,String id){
		NodeList list = root.getElementsByTagName("Group");
		for(int i =0; i<list.getLength(); i++){
			Element group = (Element)list.item(i);
			String gnames =  group.getAttribute("xmlns:GroupName");
			if(gname.equals(gnames)){
				NodeList list1 = group.getElementsByTagName("ID");		
				for(int j =0; j<list1.getLength(); j++){
					Element uID = (Element)list1.item(j);
					String uid =  uID.getAttribute("xmlns:ID");
					if(id.equals(uid)){
						group.removeChild(uID);
					}
				}
			}
		}
	}

	 /**DeleteGroup.xmlに追加されたグループのID番号が全て削除されたらグループを削除する 
	 * @param gname 選択されたグループ名
	 * @param id ユーザーのID
	 * @author kiryuu
	 */
	public void deletegroup(String gname){
		NodeList list = root.getElementsByTagName("Group");
		for(int i =0; i<list.getLength(); i++){
			Element group = (Element)list.item(i);
			String gnames =  group.getAttribute("xmlns:GroupName");
			if(gname.equals(gnames)){		
				if (group.hasChildNodes()) {  					 
				}else{
					root.removeChild(group);
				}
			}
		}
		try {
			this.saveFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * このドキュメントを保存します
	 * @param filename 保存するファイル名(パスを含む)
	 * @throws Exception ファイルの保存時に起きたエラー
	 * @author kiryuu
	 */
	public void saveFile() throws Exception {
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
