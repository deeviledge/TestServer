package sglclient.myinformation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * MyInformationXMLを扱うクラス(read)
 * @author masato
 * @version 1.2
 * @作成日: 2007/7/19
 * @最終更新日:2008/11/07
 */
public class MyInformation{
	static String xmlfile = null;
	static Document document = null;
	Element root = null;
	
	/**
	 * コンストラクタ
	 * MyInformation.xmlを読み込む
	 */
	public MyInformation(){
		try{
			xmlfile = "src/sglclient/conf/usr/xml_files/MyInformation.xml";
			//ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			//ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			//パースを実行してDocumentオブジェクトを取得
			document = builder.parse(new BufferedInputStream(new FileInputStream(xmlfile)));
			//ルート要素を取得
			root = document.getDocumentElement();
		}catch(Exception e){
			System.err.println("can't read myinformation.xml!");
		}
	}
	
	/**
	 * ユーザIDを読み込む
	 * @return ユーザID
	 * @author masato
	 */
	public String getUsrID(){
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("UserID");
		Element userid = (Element)list2.item(0);
		String usrid =  userid.getFirstChild().getNodeValue(); 
		return usrid;
	}
	
	/**
	 * ユーザ名を読み込む
	 * @return ユーザ名
	 * @author masato
	 */
	public String getUsrName(){
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("UserName");
		Element username = (Element)list2.item(0);
		String usrname =  username.getFirstChild().getNodeValue();
		return usrname;
	}
	
	/**
	 * メールアドレスを読み込むクラス
	 * @return	MailAddress(ユーザのメールアドレス)
	 * @author kinbara 2008/03/03
	 */
	public String getUsrMailaddress(){
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("MailAddress");
		Element mailaddress = (Element)list2.item(0);
		String MailAddress =  mailaddress.getFirstChild().getNodeValue();
		return MailAddress;
	}
	
	/**
	 * 全てのグループ名を読み込む
	 * @return 所属している全てのグループのグループ名
	 * @author masato
	 */
	@SuppressWarnings("unchecked")
	public String[] getGroupName(){
		@SuppressWarnings("rawtypes")
		ArrayList groupname2 = new ArrayList();
		boolean bl = true;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			for(int j=0;j<groupname2.size();j++){
				if(group.getAttribute("xmlns:Name").equals(groupname2.get(j))){
					bl=false;
					break;
				}
				else{
					bl=true;
				}
			}
			if(bl==true){
				groupname2.add(group.getAttribute("xmlns:Name"));
			}
		}
		String[] groupname = new String[groupname2.size()];
		for(int i=0;i<groupname2.size();i++){
			groupname[i]=(String)groupname2.get(i);
		}
		return groupname;
	}
	
	/**
	 * グループ名からグループの人数を取得
	 * @param gname　グループ名
	 * @return　	グループメンバ総数
	 * @author masato
	 */
	public String getGroupValue(String gname){
		String groupvalue = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				groupvalue = group.getAttribute("xmlns:value");
			}
		}
		return groupvalue;
	}
	
	/**
	 * グループ名からグループ鍵を取得
	 * @param gname　グループ名
	 * @return　グループ鍵
	 * @author masato
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
				groupkey = new BigInteger(group.getAttribute("xmlns:key"), 16);
			}
		}
		return groupkey;
	}
	
	/**
	 * グループ公開鍵nを取得
	 * @param gname　グループ名
	 * @return　n
	 * @author masato
	 */
	public String getn(String gname){
		String n = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				NodeList list3 = group.getElementsByTagName("GroupPublicKey");
				Element GPKey = (Element)list3.item(0);
				NodeList list4 = GPKey.getElementsByTagName("n");
				Element nE = (Element)list4.item(0);
				n = nE.getFirstChild().getNodeValue();
			}
		}
		return n;
	}
	
	/**
	 * グループ公開鍵aを取得
	 * @param gname　グループ名
	 * @return　a
	 * @author masato
	 */
	public String geta(String gname){
		String a = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				NodeList list3 = group.getElementsByTagName("GroupPublicKey");
				Element GPKey = (Element)list3.item(0);
				NodeList list4 = GPKey.getElementsByTagName("a");
				Element aE = (Element)list4.item(0);
				a = aE.getFirstChild().getNodeValue();
			}
		}
		return a;
	}
	
	/**
	 * グループ公開鍵a0を取得
	 * @param gname　グループ名
	 * @return　a0
	 * @author masato
	 */
	public String geta0(String gname){
		String a0 = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				NodeList list3 = group.getElementsByTagName("GroupPublicKey");
				Element GPKey = (Element)list3.item(0);
				NodeList list4 = GPKey.getElementsByTagName("a0");
				Element a0E = (Element)list4.item(0);
				a0 = a0E.getFirstChild().getNodeValue();
			}
		}
		return a0;
	}
	
	/**
	 * グループ公開鍵yを取得
	 * @param gname　グループ名
	 * @return　y
	 * @author masato
	 */
	public String gety(String gname){
		String y = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				NodeList list3 = group.getElementsByTagName("GroupPublicKey");
				Element GPKey = (Element)list3.item(0);
				NodeList list4 = GPKey.getElementsByTagName("y");
				Element yE = (Element)list4.item(0);
				y = yE.getFirstChild().getNodeValue();
			}
		}
		return y;
	}
	
	/**
	 * グループ公開鍵gを取得
	 * @param gname　グループ名
	 * @return　g
	 * @author masato
	 */
	public String getg(String gname){
		String g = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				NodeList list3 = group.getElementsByTagName("GroupPublicKey");
				Element GPKey = (Element)list3.item(0);
				NodeList list4 = GPKey.getElementsByTagName("g");
				Element gE = (Element)list4.item(0);
				g = gE.getFirstChild().getNodeValue();
			}
		}
		return g;
	}
	
	/**
	 * グループ公開鍵hを取得
	 * @param gname　グループ名
	 * @return　h
	 * @author masato
	 */
	public String geth(String gname){
		String h = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				NodeList list3 = group.getElementsByTagName("GroupPublicKey");
				Element GPKey = (Element)list3.item(0);
				NodeList list4 = GPKey.getElementsByTagName("h");
				Element hE = (Element)list4.item(0);
				h = hE.getFirstChild().getNodeValue();
			}
		}
		return h;
	}
	
	/**
	 * 会員証明書Aiを取得
	 * @param gname　グループ名
	 * @return　Ai
	 * @author masato
	 */
	public String getAi(String gname){
		System.out.println("gname="+gname);
		String Ai = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				//System.out.println("groupname="+groupname);
				NodeList list3 = group.getElementsByTagName("MembershipCertificates");
				Element MCer = (Element)list3.item(0);
				NodeList list4 = MCer.getElementsByTagName("Ai");
				Element AiE = (Element)list4.item(0);
				Ai = AiE.getFirstChild().getNodeValue();
			}
		}
		return Ai;
	}
	
	/**
	 * 会員証明書eiを取得
	 * @param gname　グループ名
	 * @return　ei
	 * @author masato
	 */
	public String getei(String gname){
		String ei = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				NodeList list3 = group.getElementsByTagName("MembershipCertificates");
				Element MCer = (Element)list3.item(0);
				NodeList list4 = MCer.getElementsByTagName("ei");
				Element eiE = (Element)list4.item(0);
				ei = eiE.getFirstChild().getNodeValue();
			}
		}
		return ei;
	}
	
	/**
	 * 会員証明書xiを取得
	 * @param gname　グループ名
	 * @return　ei
	 * @author masato
	 */
	public String getxi(String gname){
		String xi = null;
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("Group");
		for(int i=0;i<list2.getLength();i++){
			Element group = (Element)list2.item(i);
			String groupname = group.getAttribute("xmlns:Name");
			if(groupname.equals(gname)){
				NodeList list3 = group.getElementsByTagName("MembershipCertificates");
				Element MCer = (Element)list3.item(0);
				NodeList list4 = MCer.getElementsByTagName("xi");
				Element xiE = (Element)list4.item(0);
				xi = xiE.getFirstChild().getNodeValue();
			}
		}
		return xi;
	}
        
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
			saveFile();
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
		File newXML = new File( xmlfile ); 
		FileOutputStream os = new FileOutputStream(newXML); 
		StreamResult result = new StreamResult(os); 
		transformer.transform(source, result);
	}
}