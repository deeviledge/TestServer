package sglserver.dynamicpeerinformation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.list;
import java.util.Comparator;

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
 * DynamicPeerInformation.xmlを扱うクラス
 * @author Hiroki,masato,hujino,kawata
 * @version 1.2
 * @作成日: 2005/01/17
 * @最終更新日:2009/1/5
 */
public class DynamicPeerInformation {
	
	private Document document;
	private Element root;
	private String filename;
	
	/**
	 * コンストラクタ
	 * conf/usr/DynamicPeerInformation.xmlを読み込みします。
	 */
	public DynamicPeerInformation(){
		this.filename = "src/sglserver/conf/usr/xml_files/DynamicPeerInformation.xml";
		try{
			//ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			//ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			//パースを実行してDocumentオブジェクトを取得
			document = builder.parse(new BufferedInputStream(new FileInputStream(filename)));
			//ルート要素を取得
			root = document.getDocumentElement();
		}catch(Exception e){
			System.err.println("can't read DynamicPeerInformation.xml!");
		}
	}
	
	/**
	 * ログインしたユーザの追加を行います
	 * @param id ユーザーID
	 * @param name ユーザー名
	 * @param ip ユーザーIP
	 * @param remoteip サーバーのIP
	 * @return 追加されたドキュメント
	 * @author hiroki
	 */
	Document appendUser(String id, String name, String ip, String remoteip){
		synchronized (this.root){
			if(this.isPeerID(id)){
				System.out.println("Erorr : User"+id+"は、すでにログインしています");
				return (this.document);
			}
			NodeList pInfolist = this.root.getElementsByTagName("PeerDynamicInformation");
			Element pInfo = (Element)pInfolist.item(0);
			pInfo = this.document.createElement("DynamicInformation");
			Attr idtag = this.document.createAttribute("xmlns:ID");
			idtag.setValue (id);
			pInfo.setAttributeNode(idtag);
			Element pName = this.document.createElement("PeerName");
			pName.appendChild(this.document.createTextNode(name));
			pInfo.appendChild(pName);
			Element pEddress = this.document.createElement("IP");
			pEddress.appendChild(this.document.createTextNode(ip));
			pInfo.appendChild(pEddress);
			Element pCerti = this.document.createElement("RemoteIP");
			pCerti.appendChild(this.document.createTextNode(remoteip));
			pInfo.appendChild(pCerti);
			this.root.appendChild(pInfo);
			try{
				this.saveFile();
				System.out.println("User"+id+" : ログインしました");
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Erorr : User"+id+"はログインに失敗しました");
			}
			return(this.document);
		}
	}
	
	/**
	 * ログアウトしたユーザの削除
	 * @param id 削除するユーザーのID
	 * @return 削除したドキュメント
	 * @author hiroki
	 */
	Document removeUser(String id){
		synchronized (this.root){
			if(!this.isPeerID(id)){
				System.out.println("Erorr : User"+id+"は、ログインしていません");
				return (this.document);
			}
			// IDからユーザーのElementを取得
			Element userElement = this.getUserElement(id);
			// ユーザーのElementから親のElementを取得
			Element parentElement = (Element)userElement.getParentNode();
			// parentElementに属するuserElementを削除
			parentElement.removeChild( userElement );
			try {
				this.saveFile();
				System.out.println("User"+id+" : ログオフしました。");
			} catch (Exception e) {
				System.out.println("ログオフに失敗しました。");
			}
			return (this.document);
		}
	}
	
	/**
	 * id文字列からユーザーの要素を返す
	 * @param id ユーザーID
	 * @return idと同じユーザーのElement要素
	 * @author masato
	 */
	Element getUserElement(String id){
		// ルート要素からGroupのノードリストを取得
		NodeList g = this.root.getElementsByTagName("DynamicInformation");
		// Groupの要素を、とりあえずルート要素にしておく
		Element gElement = this.document.getDocumentElement();
		// Gropuの数だけループ
		for(int i=0; i<g.getLength(); i++){
			// i番目のGroupを取得
			gElement = (Element)g.item(i);
			// Group名を取得
			String gn = gElement.getAttribute("xmlns:ID");
			// もし要求のGroup名ならば、
			if(gn.equals(id)){
				// Groupの要素を返す
				return( gElement );
			}
		}
		// ループの最後までGroupが見つからなければnullを返す
		return( null );
	}
	
	/**
	 * ユーザーが存在すればtrue、存在しなければfalseを返します
	 * @param id ユーザーID
	 * @return ユーザーの存在の真偽
	 * @author masato
	 */
	public boolean isPeerID(String id){
		boolean bool = false;
		Element user = this.getUserElement(id);
		if(user != null) bool = true;
		return (bool);
	}
	
	/**
	 * このドキュメントを保存します。
	 * @param filename 保存するファイル名(パスを含む)
	 * @throws Exception ファイルの保存時に起きたエラー
	 * @author hiroki
	 */
	synchronized void saveFile() throws Exception {
		//xml保存 変換
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		//xml保存
		DOMSource source = new DOMSource(this.document);
		File newXML = new File( this.filename ); 
		FileOutputStream os = new FileOutputStream(newXML); 
		StreamResult result = new StreamResult(os); 
		transformer.transform(source, result);
	}
	
	/**
	 * 指定されたエレメントから子要素の内容を取得
	 * @param   element 指定エレメント
	 * @param   tagName 指定タグ名
	 * @return  取得した内容
	 * @author masato
	 */
	public static String getChildren(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		Element cElement = (Element)list.item(0);
		return cElement.getFirstChild().getNodeValue();
	}
	
	/**
	 * 全てのユーザ数を取得します
	 * @return num 全てのユーザ数
	 * @author masato
	 */
	public int getPeerNum(){
		int num = 0;
		try{
			NodeList list = this.root.getElementsByTagName("DynamicInformation");
			//String numb = list.getLength();                   //ユーザ数
			num = list.getLength();
		}catch(Exception e){
			System.err.println("can't get id!!");
		}
		return num;
	}
	
	/**
	 * 全てのユーザのユーザIDを取得します
	 * @return id 全てのユーザID
	 * @author masato
	 */
	public String[] getPeerID() {
		String[] ids = null;
		try{
			NodeList list = this.root.getElementsByTagName("DynamicInformation");
			ids = new String[list.getLength()];                   //idの取得数
			for(int i=0;i<list.getLength();i++){
				Element iElement = (Element)list.item(i);
				ids[i]= iElement.getAttribute("xmlns:ID");				//ID取得
			}	
		}catch(Exception e){
			System.err.println("can't get id!!");
		}
		return ids;
	}
	
	/**
	 * 全てのユーザのユーザ名を取得します
	 * @return names 全てのユーザ名
	 * @author masato
	 */
	public String[] getPeerName() {
		String[] names = null;
		try{
			NodeList list = root.getElementsByTagName("DynamicInformation");
			names = new String[list.getLength()];                   //名前の取得数
			for(int i=0;i<list.getLength();i++){
				Element iElement = (Element)list.item(i);
				names[i] =  getChildren(iElement,"PeerName");;      //名前取得
			}			
		}catch(Exception e){
			System.err.println("can't get name!!");
		}
		return names;
	}
	
	/**
	 * 全てのユーザのユーザIPを取得します
	 * @return ip 全てのユーザIP
	 * @author masato
	 */
	public String[] getPeerIP() {
		String[] ip = null;
		try{
			NodeList list = root.getElementsByTagName("DynamicInformation");
			ip = new String[list.getLength()];                   //IP取得数
			for(int i=0;i<list.getLength();i++){
				Element iElement = (Element)list.item(i);
				ip[i]=  getChildren(iElement,"IP");;      //IP取得
			}	
		}catch(Exception e){
			System.err.println("can't get listitem!!");
		}
		return ip;
	}
	
	/**
	 * ユーザ名からそのログイン状況を取得します
	 *　@param String ユーザ名
	 * @return String  ログイン状況
	 * @author masato
	 */
	public String getLoginInformation(String uname){
		String log = "[ログオフ]";
		NodeList list = root.getElementsByTagName("DynamicInformation");
		for(int i=0;i<list.getLength();i++){
			Element dynamicpeer = (Element)list.item(i);
			NodeList list2 = dynamicpeer.getElementsByTagName("PeerName");
			Element peername = (Element)list2.item(0);
			String name = peername.getFirstChild().getNodeValue();
			if(name.equals(uname)){
				log = "[ログイン]";
			}
		}
		return log;
	}
	
	/**
	 * ユーザ名からIPを取得します
	 * @param String ユーザ名
	 * @return String IP
	 * @author masato
	 */
	public String getIP(String uname){
		String ip =null;//ipをnullにする
		NodeList list = root.getElementsByTagName("DynamicInformation");
		for(int i=0;i<list.getLength();i++){
			Element dynamicpeer = (Element)list.item(i);
			NodeList list2 = dynamicpeer.getElementsByTagName("PeerName");
			Element peername = (Element)list2.item(0);
			String name = peername.getFirstChild().getNodeValue();
			if(name.equals(uname)){
				NodeList list3 = dynamicpeer.getElementsByTagName("IP");
				Element peerip = (Element)list3.item(0);
				ip = peerip.getFirstChild().getNodeValue();
			}
		}
                System.out.println("ユーザ名からIPアドレスを取得しています。IPアドレス:"+ip);
		return ip;
	}
	
	/**
	 * ユーザ名からRemoteIPを取得します
	 * @param String ユーザ名
	 * @return String RemoteIP
	 * @author kawata
	 */
	public String getRemoteIP(String uname){
		String ip =null;
		NodeList list = root.getElementsByTagName("DynamicInformation");
		for(int i=0;i<list.getLength();i++){
			Element dynamicpeer = (Element)list.item(i);
			NodeList list2 = dynamicpeer.getElementsByTagName("PeerName");
			Element peername = (Element)list2.item(0);
			String name = peername.getFirstChild().getNodeValue();
			if(name.equals(uname)){
				NodeList list3 = dynamicpeer.getElementsByTagName("RemoteIP");
				Element peerip = (Element)list3.item(0);
				ip = peerip.getFirstChild().getNodeValue();
			}
		}
		return ip;
	}
	
	/**
	 * ユーザ名からユーザIDを取得
	 * @param String ユーザ名
	 * @return String ユーザID
	 * @author hujino
	 */
	public String getID(String uname){
		String id =null;
		NodeList list = root.getElementsByTagName("DynamicInformation");
		for(int i=0;i<list.getLength();i++){
			Element dynamicpeer = (Element)list.item(i);
			NodeList list2 = dynamicpeer.getElementsByTagName("PeerName");
			Element peername = (Element)list2.item(0);
			String name = peername.getFirstChild().getNodeValue();
			if(name.equals(uname)){
				Element iElement = (Element)list.item(i);
				id = iElement.getAttribute("xmlns:ID");				//ID取得
			}
		}
		return id;
	}
	
	/**
	 * ユーザIDからユーザ名を取得
	 * @param String ユーザID
	 * @return String ユーザ名
	 * @author hujino
	 */
	public String getUserName(String id){
		String uname =null;
		NodeList list = root.getElementsByTagName("DynamicInformation");
		for(int i=0;i<list.getLength();i++){
			Element dynamicpeer = (Element)list.item(i);
			String	uid = dynamicpeer.getAttribute("xmlns:ID");				//ID取得
			if(uid.equals(id)){
				uname = dynamicpeer.getElementsByTagName("PeerName").item(0).getFirstChild().getNodeValue();
			}
		}
		return uname;
	}
        
        /**
	*ユーザ名からユーザIDの数字のみを取得
        *@param  String ユーザ名       →引数にString型のユーザ名
        *@return String ユーザIDの数字 →返り値にString型のIDの数値（ID=0005なら5が返ってくる）
	*  @author okumura
	*/ 
	public String getValueOfID(String uname){
		String id =null;
                String ID_number=null;
                try{
                    NodeList list = root.getElementsByTagName("DynamicInformation");
                    System.out.println(uname+"のUserIDを取得します");
                    for(int i=0;i<list.getLength();i++){
			Element dynamicpeer = (Element)list.item(i);
			NodeList list2 = dynamicpeer.getElementsByTagName("PeerName");
			Element peername = (Element)list2.item(0);
			String name = peername.getFirstChild().getNodeValue();
			if(name.equals(uname)){
				Element iElement = (Element)list.item(i);
				id = iElement.getAttribute("xmlns:ID");	
                                ID_number=Integer.toString(i);//IDの値をint型で取得
			}
                    }
                }catch(Exception e){
                    System.out.println("◆◆◆DynamicPeerInformation.java内のgetIDメソッド処理で例外が発生しました。◆◆◆");
                    e.printStackTrace();
                    
                }
                System.out.println("getIDメソッド内での表示"+uname+"のUserID:"+id+"を取得しました→"+ID_number);
		return ID_number;
        }
        
}
