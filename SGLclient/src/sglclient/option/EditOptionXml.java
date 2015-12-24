package sglclient.option;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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
 * 作成日: 2007/7/27
 * @auhor masato
 * @version 1.0 
 * 設定XMLを扱うクラス
 */

public class EditOptionXml {

	static String xmlfile = null;						//xmlファイルのパス
	static Document document = null;					//xmlファイル内のドキュメント
	Element root = null;								//xmlファイル内のルート要素
	
	/**
	 * コンストラクタ
	 */
	public  EditOptionXml(){
		try{
			xmlfile = "src/sglclient/conf/usr/xml_files/option.xml";
			//ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			//ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			//パースを実行してDocumentオブジェクトを取得
			document = builder.parse(new BufferedInputStream(new FileInputStream(xmlfile)));
			//ルート要素を取得
			root = document.getDocumentElement();
			}catch(Exception e){
				System.err.println("EditOptionXml.javaのコンストラクタ内のエラー");
				System.err.println("option.xmlファイルを開けません");
			}
		}
	
	/**
	 * 指定されたエレメントから子要素の内容を取得
	 * 
	 * @param   element 指定エレメント
	 * @param   tagName 指定タグ名
	 * @return  取得した内容
	 */
	public  String getChildren(Element element, String tagName) {
		try{
			NodeList list = element.getElementsByTagName(tagName);
			Element cElement = (Element)list.item(0);
			return cElement.getFirstChild().getNodeValue();
		}catch(Exception e){
			System.err.println("EditOptionXml.javaのgetChildrenのエラー");
			return null;
		}
	}
	
	/**
	 * SGLサーバのIPを取得
	 * @return ip
	 */
	public String getIP(){
		try{
			NodeList list = root.getElementsByTagName("SGLServerIP");
			Element eip = (Element)list.item(0);
			String ip = eip.getFirstChild().getNodeValue();
			return ip;
		}catch(Exception e){
			System.err.println("EditOptionXml.javaのgetIPのエラー");
			return null;
		}
	}
	
	/**
	 * MyInformation.xmlのPassを取得
	 * @return pass
	 */
	public String getPass(){
		try{
			NodeList list = root.getElementsByTagName("Pass");
			Element epass = (Element)list.item(0);
			String pass = epass.getFirstChild().getNodeValue();
			return pass;
		}catch(Exception e){
			System.err.println("EditOptionXml.javaのgetPassのエラー");
			return null;
		}
	}
	
	/**
	 * IPを設定する
	 * @param ip SGLServerのIP
	 * @return document　編集した内容
	 */
	public Document GenerateXml(String ip){
		try{
			NodeList list = root.getElementsByTagName("SGLServerIP");
			Element eip = (Element)list.item(0);
			root.removeChild(eip);	
			Element eip2 = document.createElement("SGLServerIP");
			eip2.appendChild(document.createTextNode(ip));
			root.appendChild(eip2);
			return(document);
		}catch(Exception e){
			System.err.println("EditOptionXml.javaのGenerateXmlのエラー");
			return null;
		}
	}
	/**
	 * passを設定する
	 * @param pass
	 * @return document　編集した内容
	 */
	public Document GenerateXml2(String pass){
		try{
			NodeList list = root.getElementsByTagName("Pass");
			Element epass = (Element)list.item(0);
			root.removeChild(epass);
			Element epass2 = document.createElement("Pass");
			epass2.appendChild(document.createTextNode(pass));
			root.appendChild(epass2);
			return(document);
		}catch(Exception e){
			System.err.println("EditOptionXml.javaのGenerateXml2のエラー");
			return null;
		}
	}
			
	/**
	 * XMLに保存する
	 * @param doc　保存する内容
	 */
	public void saveXml(Document doc){
		try{
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			File newXML = new File( xmlfile ); 
			FileOutputStream os = new FileOutputStream(newXML); 
			StreamResult result = new StreamResult(os); 
			transformer.transform(source, result);
		}catch(Exception e){
			System.err.println("EditOptionXml.javaのsaveXmlのエラー");
		}
	}
}
