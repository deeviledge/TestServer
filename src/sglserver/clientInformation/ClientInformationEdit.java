package sglserver.clientInformation;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * SGLクライアントから送られてきたMyInformationXMLを扱うクラス
 * @author kinbara,masato
 * @version 1.1
 * @作成日: 2007/7/19
 * @最終更新日:2008/10/31
 */
public class ClientInformationEdit{
	static String xmlfile = null;
	static Document document = null;
	Element root = null;

	/**
	 * コンストラクタ
	 */
	public ClientInformationEdit(){
		try{
			xmlfile = "src/sglserver/conf/usr/xml_files/MyInformation.xml";
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
				System.err.println("can't read myinformation.xml!");
			}
		}
	
	/**
	 * ユーザのメールアドレスを返す関数
	 * @return String メールアドレス
	 * @author kinbara
	 */
	public String getMailAddress(){
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("MailAddress");
		Element usermail = (Element)list2.item(0);
		String usermailadd =  usermail.getFirstChild().getNodeValue();
		return usermailadd;
	}
	
	/**
	 * ユーザ名を読み込むクラス
	 * @return String ユーザ名
	 * @author kinbara
	 */
	public String getUsrName(){
		NodeList list = root.getElementsByTagName("User");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("UserName");
		Element username = (Element)list2.item(0);
		String usrname =  username.getFirstChild().getNodeValue();
		return usrname;
	}
}