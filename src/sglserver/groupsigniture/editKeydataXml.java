package sglserver.groupsigniture;


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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * key.XMLを扱うクラス
 * @auhor masato
 * @version 1.0 
 * @作成日: 2008/5/16
 * @最終更新日:2008/10/31
 */
public class editKeydataXml {

	static String xmlfile = null;						//xmlファイルのパス
	static Document document = null;					//xmlファイル内のドキュメント
	Element root = null;								//xmlファイル内のルート要素
	
	/**
	 * コンストラクタ
	 */
	public  editKeydataXml(){
		try{
			xmlfile = "src/sglserver/conf/usr/xml_files/groupsigniture/Key.xml";
			//ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			//ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			//パースを実行してDocumentオブジェクトを取得
			document = builder.parse(new BufferedInputStream(new FileInputStream(xmlfile)));
			//ルート要素を取得
			root = document.getDocumentElement();
			}catch(Exception e){
			}
		}
	
	/**
	 * 指定されたエレメントから子要素の内容を取得
	 * 
	 * @param   element 指定エレメント
	 * @param   tagName 指定タグ名
	 * @return  取得した内容
	 * @author masato
	 */
	public  String getChildren(Element element, String tagName) {
		try{
			NodeList list = element.getElementsByTagName(tagName);
			Element cElement = (Element)list.item(0);
			return cElement.getFirstChild().getNodeValue();
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * nを取得
	 * @return n
	 * @author masato
	 */
	public String getn(){
		try{
			NodeList list = root.getElementsByTagName("PublicKey");
			Element pKey = (Element)list.item(0);
			NodeList list2 = pKey.getElementsByTagName("n");
			Element nE = (Element)list2.item(0);
			String n = nE.getFirstChild().getNodeValue();
			//System.out.println("n="+n);
			return n;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * aを取得
	 * @return a
	 * @author masato
	 */
	public String geta(){
		try{
			NodeList list = root.getElementsByTagName("PublicKey");
			Element pKey = (Element)list.item(0);
			NodeList list2 = pKey.getElementsByTagName("a");
			Element aE = (Element)list2.item(0);
			String a = aE.getFirstChild().getNodeValue();
			//System.out.println("a="+a);
			return a;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * a0を取得
	 * @return a0
	 * @author masato
	 */
	public String geta0(){
		try{
			NodeList list = root.getElementsByTagName("PublicKey");
			Element pKey = (Element)list.item(0);
			NodeList list2 = pKey.getElementsByTagName("a0");
			Element a0E = (Element)list2.item(0);
			String a0 = a0E.getFirstChild().getNodeValue();
			//System.out.println("a0="+a0);
			return a0;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * yを取得
	 * @return y
	 * @author masato
	 */
	public String gety(){
		try{
			NodeList list = root.getElementsByTagName("PublicKey");
			Element pKey = (Element)list.item(0);
			NodeList list2 = pKey.getElementsByTagName("y");
			Element yE = (Element)list2.item(0);
			String y = yE.getFirstChild().getNodeValue();
			//System.out.println("y="+y);
			return y;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * gを取得
	 * @return g
	 * @author masato
	 */
	public String getg(){
		try{
			NodeList list = root.getElementsByTagName("PublicKey");
			Element pKey = (Element)list.item(0);
			NodeList list2 = pKey.getElementsByTagName("g");
			Element gE = (Element)list2.item(0);
			String g = gE.getFirstChild().getNodeValue();
			//System.out.println("g="+g);
			return g;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * hを取得
	 * @return h
	 * @author masato
	 */
	public String geth(){
		try{
			NodeList list = root.getElementsByTagName("PublicKey");
			Element pKey = (Element)list.item(0);
			NodeList list2 = pKey.getElementsByTagName("h");
			Element hE = (Element)list2.item(0);
			String h = hE.getFirstChild().getNodeValue();
			//System.out.println("h="+h);
			return h;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * p1を取得
	 * @return p1
	 * @author masato
	 */
	public String getp1(){
		try{
			NodeList list = root.getElementsByTagName("SecretKey");
			Element sKey = (Element)list.item(0);
			NodeList list2 = sKey.getElementsByTagName("p1");
			Element p1E = (Element)list2.item(0);
			String p1 = p1E.getFirstChild().getNodeValue();
			//System.out.println("p1="+p1);
			return p1;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * q1を取得
	 * @return p1
	 * @author masato
	 */
	public String getq1(){
		try{
			NodeList list = root.getElementsByTagName("SecretKey");
			Element sKey = (Element)list.item(0);
			NodeList list2 = sKey.getElementsByTagName("q2");
			Element q1E = (Element)list2.item(0);
			String q1 = q1E.getFirstChild().getNodeValue();
			//System.out.println("q1="+q1);
			return q1;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * xを取得
	 * @return x
	 * @author masato
	 */
	public String getx(){
		try{
			NodeList list = root.getElementsByTagName("SecretKey");
			Element sKey = (Element)list.item(0);
			NodeList list2 = sKey.getElementsByTagName("x");
			Element xE = (Element)list2.item(0);
			String x = xE.getFirstChild().getNodeValue();
			//System.out.println("x="+x);
			return x;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 公開鍵を設定する
	 * @param n
	 * @param a
	 * @param a0
	 * @param y
	 * @param g
	 * @param h
	 * @author masato
	 */
	public Document pKey(BigInteger n, BigInteger a, BigInteger a0, BigInteger y, BigInteger g, BigInteger h){
		try{
			Element pkey = document.createElement("PublicKey");
			Element nE = document.createElement("n");
			Element aE = document.createElement("a");
			Element a0E = document.createElement("a0");
			Element yE = document.createElement("y");
			Element gE = document.createElement("g");
			Element hE = document.createElement("h");
			nE.appendChild(document.createTextNode(String.valueOf(n)));
			aE.appendChild(document.createTextNode(String.valueOf(a)));
			a0E.appendChild(document.createTextNode(String.valueOf(a0)));
			yE.appendChild(document.createTextNode(String.valueOf(y)));
			gE.appendChild(document.createTextNode(String.valueOf(g)));
			hE.appendChild(document.createTextNode(String.valueOf(h)));
			pkey.appendChild(nE);
			pkey.appendChild(aE);
			pkey.appendChild(a0E);
			pkey.appendChild(yE);
			pkey.appendChild(gE);
			pkey.appendChild(hE);
			root.appendChild(pkey);
			return(document);
		}catch(Exception l){
			
			return null;
		}
	}
	
	/**
	 * 秘密鍵を設定する
	 * @param p1
	 * @param q1
	 * @param x
	 * @author masato
	 */
	public Document sKey(BigInteger p1,BigInteger q1,BigInteger x){
		try{
			Element sKey = document.createElement("SecretKey");
			Element p1E = document.createElement("p1");
			Element q1E = document.createElement("q2");
			Element xE = document.createElement("x");
			p1E.appendChild(document.createTextNode(String.valueOf(p1)));
			q1E.appendChild(document.createTextNode(String.valueOf(q1)));
			xE.appendChild(document.createTextNode(String.valueOf(x)));
			sKey.appendChild(p1E);
			sKey.appendChild(q1E);
			sKey.appendChild(xE);
			root.appendChild(sKey);
			return(document);
		}catch(Exception e){		
			return null;
		}
	}
	
	/**
	 * 公開鍵秘密鍵を削除する
	 * @return
	 * @author masato
	 */
	public Document deleteKey(){
		try{
			NodeList list = root.getElementsByTagName("PublicKey");
			Element pKey = (Element)list.item(0);
			NodeList list2 = root.getElementsByTagName("SecretKey");
			Element sKey = (Element)list2.item(0);
			root.removeChild(pKey);
			root.removeChild(sKey);
	
			return(document);
		}catch(Exception l){
			
			return null;
		}
	}

			
	/**
	 * このドキュメントを保存します。
	 * @param filename 保存するファイル名(パスを含む)
	 * @throws Exception ファイルの保存時に起きたエラー
	 * @author masato
	 */
	public synchronized void saveFile() throws Exception {
		//xml保存 変換
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		//xml保存
		DOMSource source = new DOMSource(document);
		File newXML = new File(xmlfile); 
		FileOutputStream os = new FileOutputStream(newXML); 
		StreamResult result = new StreamResult(os); 
		transformer.transform(source, result);
	}
}