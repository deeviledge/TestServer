package sglserver.myinformation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;

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
 * MyInformationXMLを編集するクラス(Write)
 * @author kinbara,masato,kiryuu
 * @version 1.1
 * @作成日: 2007/07/17
 * @最終更新日:2008/10/31
 */
public class MyInformationEdit {
	private Document	document;		// グループ情報のドキュメント
	private Element		root;			//     〃       のルートノード
	String filename;
	boolean	newxml = false;		// true :GroupInfoTest.xmlを新しく作り直す場合
	
	/**
	 * MyInformation.xmlを読み込みする
	 * 新規に作成したい人はnewxmlをtrueにしてください
	 * @param filename			読み込みファイル
	 * @author masato
	 */
	public MyInformationEdit(String id){
            filename = "src/sglserver/conf/usr/xml_files/MyInformation/MyInformation_"+id+".xml";
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
	 * グループ情報を作成(グループ署名を扱う場合です)
	 * @param cer　　グループ署名の会員書とグループ公開鍵
	 * @param gname　グループ名
	 * @param gvalue　グループメンバー数
	 * @author masato
	 */
	public void addGroupCertificate(BigInteger[][] cer,String gname, String gvalue){
		try{
			
			// グループ情報を書き込むところを探す
			NodeList gInfolist = root.getElementsByTagName("User");
			Element usrE = (Element)gInfolist.item(0);
			NodeList glist = usrE.getElementsByTagName("Group");
			for(int i=0;i<glist.getLength();i++){
				Element gE = (Element)glist.item(i);
				if(gname.equals(gE.getAttribute("xmlns:Name"))){
					usrE.removeChild(gE);
				}
			}
			Element group = document.createElement("Group"); // 要素(GroupPublicKey)を作成
			Attr groupname  = document.createAttribute("xmlns:Name");
			Attr groupvalue = document.createAttribute("xmlns:value");
			groupname.setValue(gname);
			groupvalue.setValue(gvalue);
			group.setAttributeNode(groupname);
			group.setAttributeNode(groupvalue);
			Element MC = document.createElement("MembershipCertificates"); // 要素(MembershipCertificates)を作成
			Element GP = document.createElement("GroupPublicKey"); // 要素(GroupPublicKey)を作成
			Element AiE = document.createElement("Ai"); // 要素(AiE)を作成
			Element eiE = document.createElement("ei"); // 要素(eiE)を作成
			Element xiE = document.createElement("xi"); // 要素(xiE)を作成
			Element nE = document.createElement("n"); // 要素(nE)を作成
			Element aE = document.createElement("a"); // 要素(aE)を作成
			Element a0E = document.createElement("a0"); // 要素(a0E)を作成
			Element yE = document.createElement("y"); // 要素(yE)を作成
			Element gE = document.createElement("g"); // 要素(gE)を作成
			Element hE = document.createElement("h"); // 要素(hE)を作成
			AiE.appendChild(document.createTextNode(String.valueOf(cer[2][1]))); 
			eiE.appendChild(document.createTextNode(String.valueOf(cer[2][2]))); 
			xiE.appendChild(document.createTextNode(String.valueOf(cer[2][3])));
			nE.appendChild(document.createTextNode(String.valueOf(cer[1][1]))); 
			aE.appendChild(document.createTextNode(String.valueOf(cer[1][2]))); 
			a0E.appendChild(document.createTextNode(String.valueOf(cer[1][3]))); 
			yE.appendChild(document.createTextNode(String.valueOf(cer[1][4]))); 
			gE.appendChild(document.createTextNode(String.valueOf(cer[1][5]))); 
			hE.appendChild(document.createTextNode(String.valueOf(cer[1][6]))); 
			MC.appendChild(AiE); 
			MC.appendChild(eiE); 
			MC.appendChild(xiE);
			GP.appendChild(nE); 
			GP.appendChild(aE); 
			GP.appendChild(a0E); 
			GP.appendChild(yE); 
			GP.appendChild(gE); 
			GP.appendChild(hE); 
			group.appendChild(MC);
			group.appendChild(GP);
			usrE.appendChild(group);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
/*	*//**
	 * グループ情報を作成(グループ署名なし)
	 * @param gname　グループ名
	 * @param gvalue　グループメンバ数
	 * @author masato
	 *//*
	public void addGroup(String gname,String gvalue){
		try{
			// グループ情報を書き込むところを探す
			NodeList gInfolist = root.getElementsByTagName("User");
			Element gInfo = (Element)gInfolist.item(0);
			Element group = document.createElement("Group"); // 要素(Group)を作成
			Attr groupname = document.createAttribute("xmlns:Name"); // 属性(xmlns:Name)を作成
			Attr groupvalue = document.createAttribute("xmlns:value"); // 属性(xmlns:value)を作成
			groupname.setValue(gname); // GroupNameをgroupnameに付ける
			groupvalue.setValue(gvalue); // Valueをgroupvalueに付ける
			group.setAttributeNode(groupname); // groupに属性groupnameを付ける
			group.setAttributeNode(groupvalue); // groupに属性groupvalueを付ける
			gInfo.appendChild(group); // gInfoの下にgroupを付ける
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
	/**
	 * groupタグを削除する関数
	 * @param groupname 削除したいグループの名前
	 * @author kiryuu
	 */
	public void removeGroupName(String groupname){
		try{
			NodeList list = root.getElementsByTagName("User");
			Element user = (Element)list.item(0);
			NodeList list2 = user.getElementsByTagName("Group");
			Node node = list.item(0);
			String Name = ""; // タグに付加されているグループ名を格納するためのもの
			for(int i=0;i<list2.getLength();i++){
				Element group = (Element)list2.item(i);
				Name = group.getAttribute("xmlns:Name"); // グループ名を取得
				if(Name.equalsIgnoreCase(groupname)){ // 削除したいグループの名前とタグに付加されているグループ名が一致したら
					System.out.println("Groupタグを消去します。");
					node.removeChild(group); // 消去
					break;
				}	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * groupタグを削除する関数
	 * 削除した場合OKを返す
	 * @param groupname グループ名
	 * @return　result 結果
	 * @author kiryuu
	 */
	public String deleteMyinformation(String groupname){
		try{
			NodeList list = root.getElementsByTagName("User");
			Element user = (Element)list.item(0);
			NodeList list1 = user.getElementsByTagName("Group");
			for(int i=0; i<list1.getLength();i++){
				Element group = (Element)list1.item(i);
				String gname = group.getAttribute("xmlns:Name");
				if(groupname.equals(gname)){
					user.removeChild(group);
				}
			}
			String result = "OK!";
			return result;
		}catch(Exception e){
			e.printStackTrace();
			String result = "ＮＧ";
			return result;
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
		//System.out.println("filename = " + filename);
	}
}
