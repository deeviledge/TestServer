package sglserver.peerbasicinformation;

import java.io.File;
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

import sglserver.clientInformation.GenerateClientInformation;

/**
 * conf/usr/PeerBasicInformation.xmlを読み込んでデータを操作するクラス
 * @author toru,kinbara,masato
 * @version 1.4
 * @作成日: 2005/01/07
 * @最終更新日:2008/10/31
 */
public class PeerBasicInformationEdit {
	
	private Document document;
	private Element root;
	private String filename;
	
	/**
	 * conf/usr/PeerBasicInformation.xmlを読み込むコンストラクタです。
	 */
	public  PeerBasicInformationEdit(){
		this.filename = "src/sglserver/conf/usr/xml_files/PeerBasicInformation.xml";
		try{
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得
			this.document = builder.parse( filename );
			// ルート要素を取得
			this.root = this.document.getDocumentElement();	
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * ユーザーの追加を行います
	 * @param id ユーザーID
	 * @param name ユーザー名
	 * @param eddress E-mailアドレス
	 * @param certification X.509ファイルの保存場所
	 * @return 追加されたドキュメントを返します
	 * @author toru
	 */
	public void appendUser(String id, String name, String eddress, String certification){

		if(this.isPeerID(id)){
			System.out.println("すでにUser"+id+"は存在しています。");
		}
		try{
			NodeList pInfolist = this.root.getElementsByTagName("BasicInformation");
			Element pInfo = (Element)pInfolist.item(0);			
			pInfo = this.document.createElement("BasicInformation");
			Attr idtag = this.document.createAttribute("xmlns:ID");
			idtag.setValue (id);
			pInfo.setAttributeNode(idtag);		
			Element pName = this.document.createElement("PeerName");
			pName.appendChild(this.document.createTextNode(name));
			pInfo.appendChild(pName);
			Element pEddress = this.document.createElement("MailAddress");
			pEddress.appendChild(this.document.createTextNode(eddress));
			pInfo.appendChild(pEddress);
			Element pCerti = this.document.createElement("Certification");
			pCerti.appendChild(this.document.createTextNode(certification));
			pInfo.appendChild(pCerti);	
			this.root.appendChild(pInfo);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("User"+id+"を追加しました。");
	}
	
	/**
	 * ユーザーの削除
	 * @param id 削除するユーザーのID
	 * @return 削除したドキュメント
	 * @author toru
	 */
	public Document removeUser(String id){
		if(!this.isPeerID(id)){
			// 例外（仮）
			System.out.println("removeUser Exception:ユーザーが見つかりません。");
			return (this.document);
		}
		// IDからユーザーのElementを取得
		Element userElement = this.getUserElement(id);
		// ユーザーのElementから親のElementを取得
		Element parentElement = (Element)userElement.getParentNode();
		// parentElementに属するuserElementを削除
		parentElement.removeChild( userElement );
		
		System.out.println("User"+id+"を削除しました。");
		return (this.document);
	}
	
	/**
	 * id文字列からユーザーの要素を返す
	 * @param id ユーザーID
	 * @return idと同じユーザーのElement要素
	 * @author toru
	 */
	public Element getUserElement(String id){
		// ルート要素を取得
		Element eroot = this.document.getDocumentElement();
		// ルート要素からGroupのノードリストを取得
		NodeList g = eroot.getElementsByTagName("BasicInformation");
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
	 * ユーザーIDからユーザー名を返します
	 * @param id ユーザーID
	 * @return ユーザー名
	 * @author toru
	 */
	public String getPeerName(String id){
		if(!this.isPeerID(id)){
			System.out.println("User"+id+"が存在しません。");
			return (null);
		}
		Element userElement = this.getUserElement(id);
		NodeList peer = userElement.getElementsByTagName("PeerName");
		String peerName = peer.item(0).getFirstChild().getNodeValue();
		return(peerName);
	}
	

	
	/**
	 * ユーザーIDからユーザーのX.509ファイルの保存場所を返します
	 * @param id ユーザーID
	 * @return ユーザーのX.509ファイルの保存場所
	 * @author toru
	 */
	public String getCertification(String id){
		if(!this.isPeerID(id)){
			System.out.println("User"+id+"が存在しません。");
			return (null);
		}
		Element userElement = this.getUserElement(id);
		NodeList peer = userElement.getElementsByTagName("Certification");
		String certification = peer.item(0).getFirstChild().getNodeValue();
		return(certification);
	}
	
	/**
	 * ユーザーIDのピア用のXML文書を作成します。(保存先：conf/usr/"+id+"/PeerInformation.xml)
	 * @param id　ユーザーID
	 * @author toru
	 */
	public void getPeerXML(String id){
		if(this.isPeerID(id)){
			PeerBasicInformationXml XML = new PeerBasicInformationXml();
			Element userElement = this.getUserElement(id);		
			NodeList pn = userElement.getElementsByTagName("PeerName");
			String peerName = pn.item(0).getFirstChild().getNodeValue();		
			NodeList ma = userElement.getElementsByTagName("MailAddress");
			String eddress = ma.item(0).getFirstChild().getNodeValue();		
			NodeList c = userElement.getElementsByTagName("Certification");
			String certification = c.item(0).getFirstChild().getNodeValue();	
			XML.peerBasicInformationXmlGenarator(id, peerName, eddress, certification);	
			// XMLファイルの保存
			// 保存するディレクトリの作成
			File file = new File("conf/usr/"+id);
			file.mkdir();
			// 保存するファイル名
			String filename = "conf/usr/"+id+"/PeerInformation.xml";
			try{
				XML.saveFile();
				System.out.println(filename+"を作成しました。");
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("保存の際にエラーが起きたようです01。");
			}
		}else{
			System.out.println("User"+id+"が存在しません。");
		}
	}
	
	/**
	 * ユーザが存在すればtrue、存在しなければfalseを返します
	 * @param id ユーザID
	 * @return ユーザの存在の真偽
	 * @author kinbara
	 */
	private boolean isPeerID(String id){
		boolean bool = false;
		Element user = this.getUserElement(id);
		if(user != null) bool = true;
		return (bool);
	}
	
	/**
	 * ユーザ名が存在すればtrue、存在しなければfalseを返します
	 * @param s　ユーザ名
	 * @return　ユーザ名の存在の真偽
	 * @author kinbara
	 */
	public boolean isPeerName(String s){
		int i = 0;
		boolean ext = false;
		while(i<getGroupLength()){
			String name = getPeerName(i);
			if(s.equals(name) == true){
				ext = true;
				break;
			}
			++i;
		}
		return ext;
	}
	
	/**
	 * i番目のgroup名を取得する
	 * @param　i 
	 * @return Element　グループ名
	 * @author kinbara
	 */
	public Element getUserElement(int i){
		//ルート要素を取得
		Element eroot = this.document.getDocumentElement();
		// ルート要素からGroupのノードリストを取得
		NodeList g = eroot.getElementsByTagName("BasicInformation");
		// Groupの要素を、とりあえずルート要素にしておく
		Element gElement = this.document.getDocumentElement();
		for(int s = i; s<g.getLength(); s++){
			// i番目のGroupを取得
			gElement = (Element)g.item(s);
			// Group名を取得
			return( gElement );
		}
		return null;
	}
	
	/**
	 * i番目のpeernameを取得する
	 * @param　i
	 * @return String peername
	 * @author kinbara
	 */
	public String getPeerName(int i){
		Element userElement = this.getUserElement(i);
		NodeList peer = userElement.getElementsByTagName("PeerName");
		String peerName = peer.item(0).getFirstChild().getNodeValue();
		return(peerName);
	}
	
	/**
	 * group(BasicInformation)の長さを返す
	 * @return int 長さ
	 * @author kinbara
	 */
	public int getGroupLength(){
		// ルート要素を取得
		Element eroot = this.document.getDocumentElement();
		// ルート要素からGroupのノードリストを取得
		NodeList g = eroot.getElementsByTagName("BasicInformation");
		System.out.println(g.getLength());
		return g.getLength(); 
	}
	
	/**
	 * ユーザーの追加を行います
	 * @param id ユーザーID
	 * @param name ユーザー名
	 * @param eddress E-mailアドレス
	 * @param certification X.509ファイルの保存場所
	 * @return 追加されたドキュメントを返します
	 * @author kinbara
	 */
	public Document appendUser(String name,String address){

		try{			
			NodeList pInfolist = this.root.getElementsByTagName("BasicInformation");
			Element pInfo = (Element)pInfolist.item(0);
			String peerid = "";
			int id = getGroupLength()+1;
			String idnum=String.valueOf(id);
			if(id < 10){
				peerid = "000"+idnum;
			}else if(id < 100){
				peerid = "00"+idnum;
			}else if(id < 1000){
				peerid = "0"+idnum;
			}else if(id < 10000){
				peerid = idnum;
			}
			pInfo = this.document.createElement("BasicInformation");
			Attr idtag = this.document.createAttribute("xmlns:ID");
			idtag.setValue (peerid);
			pInfo.setAttributeNode(idtag);
			//System.out.println("ID:"+id);
			
			Element pName = this.document.createElement("PeerName");
			pName.appendChild(this.document.createTextNode(name));
			pInfo.appendChild(pName);
			//System.out.println("NAME:"+name);
		
			Element pEddress = this.document.createElement("MailAddress");
			pEddress.appendChild(this.document.createTextNode(address));
			pInfo.appendChild(pEddress);
			//System.out.println("MailAddress:"+eddress);
		
			Element pCerti = this.document.createElement("Certification");
			String certification = "conf/usr/ca/"+name+".cer";
			pCerti.appendChild(this.document.createTextNode(certification));
			pInfo.appendChild(pCerti);
			//System.out.println("Certification:"+certification);
		
			this.root.appendChild(pInfo);
			//GenerateClientInformationXML gcl = new GenerateClientInformationXML();
			GenerateClientInformation gmi = new GenerateClientInformation();
			//gmi.addUser(name,peerid); // MyInformationを作成
			gmi.addID(peerid);
			gmi.saveFile(); // 保存
			//gcl.addUser(peerid);
			//gcl.saveFile();
			System.out.println(name+"を追加しました。");
		}catch(Exception e){
			e.printStackTrace();
		}
		return(this.document);
	}
	
	/**
	 * このドキュメントを保存します。
	 * @param filename 保存するファイル名(パスを含む)
	 * @throws Exception ファイルの保存時に起きたエラー
	 * @author toru
	 */
	public void saveFile() throws Exception {
		//xml保存 変換
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		//xml保存
		DOMSource source = new DOMSource(this.document);
		File newXML = new File( this.filename ); 
		FileOutputStream os = new FileOutputStream(newXML); 
		StreamResult result = new StreamResult(os); 
		transformer.transform(source, result);
		System.out.println("PeerBasicInformation.xmlを保存しました。");
	}
	
	/**
	 * 指定されたエレメントから子要素の内容を取得
	 * @param   element 指定エレメント
	 * @param   tagName 指定タグ名
	 * @return  取得した内容
	 * @author harada
	 */
	public static String getChildren(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		Element cElement = (Element)list.item(0);
		return cElement.getFirstChild().getNodeValue();
	}
	

	
	/**
	 * 全てのユーザのユーザ名を取得します
	 * @return names 全てのユーザ名
	 * @author harada
	 */
	public String[] getPeerNames() {
		String[] names = null;
		try{
			NodeList list = root.getElementsByTagName("BasicInformation");
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
	 * 全てのユーザ数を取得します
	 * @return num 全てのユーザ数
	 * @author harada
	 */
	public int getPeerNum(){
		int num = 0;
		try{
			NodeList list = this.root.getElementsByTagName("BasicInformation");
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
	 * @author harada
	 */
	public String[] getPeerID() {
		String[] ids = null;
		try{
			NodeList list = this.root.getElementsByTagName("BasicInformation");
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
			NodeList list = root.getElementsByTagName("BasicInformation");
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


}
