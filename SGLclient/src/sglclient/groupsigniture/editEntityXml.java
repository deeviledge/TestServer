package sglclient.groupsigniture;

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
 * entity.xmlを扱うクラス
 * entity.xmlにはグループ署名を扱うための数値が保存されている
 * @auhor masato
 * @version 1.0 
 * @作成日: 200811/07
 * @最終更新日:2008/11/07
 */
public class editEntityXml {
	static String xmlfile = null;						//xmlファイルのパス
	static Document document = null;					//xmlファイル内のドキュメント
	Element root = null;								//xmlファイル内のルート要素
	
	/**
	 * コンストラクタ
	 * entity.xmlを読み込む
	 */
	public  editEntityXml(){
		try{
			xmlfile = "src/sglclient/conf/usr/xml_files/entity.xml";
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
			System.err.println("editEntityXml.javaのgetChildrenのエラー");
			return null;
		}
	}
	
	/**
	 * 数値eを取得
	 * @return e
	 * @author masato
	 */
	public String gete(){
		try{
			NodeList list = root.getElementsByTagName("SecurityParam");
			Element SP = (Element)list.item(0);
			NodeList list2 = SP.getElementsByTagName("e");
			Element eE = (Element)list2.item(0);
			String e =eE.getFirstChild().getNodeValue();
			//System.out.println("e="+e);
			return e;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgeteのエラー");
			return null;
		}
	}
	
	/**
	 * 数値kを取得
	 * @return k
	 * @author masato
	 */
	public String getk(){
		try{
			NodeList list = root.getElementsByTagName("SecurityParam");
			Element SP = (Element)list.item(0);
			NodeList list2 = SP.getElementsByTagName("k");
			Element kE = (Element)list2.item(0);
			String k =kE.getFirstChild().getNodeValue();
			//System.out.println("k="+k);
			return k;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgetkのエラー");
			return null;
		}
	}
	
	/**
	 * 数値lpを取得
	 * @return ip
	 * @author masato
	 */
	public String getlp(){
		try{
			NodeList list = root.getElementsByTagName("SecurityParam");
			Element SP = (Element)list.item(0);
			NodeList list2 = SP.getElementsByTagName("lp");
			Element lpE = (Element)list2.item(0);
			String lp =lpE.getFirstChild().getNodeValue();
			//System.out.println("lp="+lp);
			return lp;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgetlpのエラー");
			return null;
		}
	}
	
	/**
	 * 数値λ1を取得
	 * @return rmd1
	 * @author masato
	 */
	public String getrmd1(){
		try{
			NodeList list = root.getElementsByTagName("λ");
			Element rmdE = (Element)list.item(0);
			NodeList list2 = rmdE.getElementsByTagName("λ1");
			Element rmd1E = (Element)list2.item(0);
			String rmd1 = rmd1E.getFirstChild().getNodeValue();
			//System.out.println("rmd1="+rmd1);
			return rmd1;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgetrmd1のエラー");
			return null;
		}
	}
	
	/**
	 * 数値λ2を取得
	 * @return rmd2
	 * @author masato
	 */
	public String getrmd2(){
		try{
			NodeList list = root.getElementsByTagName("λ");
			Element rmdE = (Element)list.item(0);
			NodeList list2 = rmdE.getElementsByTagName("λ2");
			Element rmd2E = (Element)list2.item(0);
			String rmd2 = rmd2E.getFirstChild().getNodeValue();
			//System.out.println("rmd2="+rmd2);
			return rmd2;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgetrmd2のエラー");
			return null;
		}
	}
	
	/**
	 * 数値γ1を取得
	 * @return gnm1
	 * @author masato
	 */
	public String getgnm1(){
		try{
			NodeList list = root.getElementsByTagName("γ");
			Element gnmE = (Element)list.item(0);
			NodeList list2 = gnmE.getElementsByTagName("γ1");
			Element gnm1E = (Element)list2.item(0);
			String gnm1 = gnm1E.getFirstChild().getNodeValue();
			//System.out.println("gnm1="+gnm1);
			return gnm1;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgetgnm1のエラー");
			return null;
		}
	}
	
	/**
	 * 数値γ2を取得
	 * @return gnm1
	 * @author masato
	 */
	public String getgnm2(){
		try{
			NodeList list = root.getElementsByTagName("γ");
			Element gnmE = (Element)list.item(0);
			NodeList list2 = gnmE.getElementsByTagName("γ2");
			Element gnm2E = (Element)list2.item(0);
			String gnm2 = gnm2E.getFirstChild().getNodeValue();
			//System.out.println("gnm2="+gnm2);
			return gnm2;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgetgnm2のエラー");
			return null;
		}
	}
	
	/**
	 * 数値Λ1を取得
	 * @return RMD1
	 * @author masato
	 */
	public String getRMD1(){
		try{
			NodeList list = root.getElementsByTagName("Λ");
			Element RMD = (Element)list.item(0);
			NodeList list2 = RMD.getElementsByTagName("Λ1");
			Element RMD1E = (Element)list2.item(0);
			String RMD1 = RMD1E.getFirstChild().getNodeValue();
			//System.out.println("RMD1="+RMD1);
			return RMD1;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgetRMD1のエラー");
			return null;
		}
	}
	
	/**
	 * 数値Λ2を取得
	 * @return RMD2
	 * @author masato
	 */
	public String getRMD2(){
		try{
			NodeList list = root.getElementsByTagName("Λ");
			Element RMD = (Element)list.item(0);
			NodeList list2 = RMD.getElementsByTagName("Λ2");
			Element RMD2E = (Element)list2.item(0);
			String RMD2 = RMD2E.getFirstChild().getNodeValue();
			//System.out.println("RMD2="+RMD2);
			return RMD2;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgetRMD2のエラー");
			return null;
		}
	}
	
	/**
	 * 数値Γ１を取得
	 * @return GNM1
	 * @author masato
	 */
	public String getGNM1(){
		try{
			NodeList list = root.getElementsByTagName("Γ");
			Element GAM = (Element)list.item(0);
			NodeList list2 = GAM.getElementsByTagName("Γ1");
			Element GAM1 = (Element)list2.item(0);
			String GNM1 = GAM1.getFirstChild().getNodeValue();
			//System.out.println("="+GNM1);
			return GNM1;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgetGNM1のエラー");
			return null;
		}
	}
	
	/**
	 * 数値Γ2を取得
	 * @return GNM2
	 * @author masato
	 */
	public String getGNM2(){
		try{
			NodeList list = root.getElementsByTagName("Γ");
			Element GAM = (Element)list.item(0);
			NodeList list2 = GAM.getElementsByTagName("Γ2");
			Element GAM2 = (Element)list2.item(0);
			String GNM2 = GAM2.getFirstChild().getNodeValue();
			//System.out.println("="+GNM2);
			return GNM2;
		}catch(Exception e){
			System.err.println("editEntityXml.javaのgetGNM2のエラー");
			return null;
		}
	}
	/**
	 * セキュリティーパラメータを設定する
	 * @param e　数値e
	 * @param k　数値k
	 * @param lp　数値lp
	 * @return Document　ドキュメント
	 * @author masato
	 */
	public Document securityParam(int e, int k, int lp){
		try{
			Element sec = document.createElement("SecurityParam");
			Element eE = document.createElement("e");
			Element kE = document.createElement("k");
			Element lpE = document.createElement("lp");
			String ee = String.valueOf(e);
			String kk = String.valueOf(k);
			String ll = String.valueOf(lp);
			eE.appendChild(document.createTextNode(ee));
			kE.appendChild(document.createTextNode(kk));
			lpE.appendChild(document.createTextNode(ll));
			sec.appendChild(eE);
			sec.appendChild(kE);
			sec.appendChild(lpE);
			root.appendChild(sec);
			return(document);
		}catch(Exception l){
			System.err.println("editEntityXml.javaのsecurityParamのエラー");
			return null;
		}
	}
	
	/**
	 * λ,γを設定する
	 * @param rmd1 数値λ1
	 * @param rmd2　数値λ2
	 * @param gnm1　数値γ1
	 * @param gnm2　数値γ2
	 * @return Document　ドキュメント
	 * @author masato
	 */
	public Document rmdgnm(int rmd1,int rmd2,int gnm1,int gnm2){
		try{
			Element ram = document.createElement("λ");
			Element gam = document.createElement("γ");
			Element rmd1E = document.createElement("λ1");
			Element rmd2E = document.createElement("λ2");
			Element gnm1E = document.createElement("γ1");
			Element gnm2E = document.createElement("γ2");
			rmd1E.appendChild(document.createTextNode(String.valueOf(rmd1)));
			rmd2E.appendChild(document.createTextNode(String.valueOf(rmd2)));
			gnm1E.appendChild(document.createTextNode(String.valueOf(gnm1)));
			gnm2E.appendChild(document.createTextNode(String.valueOf(gnm2)));
			ram.appendChild(rmd1E);
			ram.appendChild(rmd2E);
			gam.appendChild(gnm1E);
			gam.appendChild(gnm2E);
			root.appendChild(ram);
			root.appendChild(gam);
			return(document);
		}catch(Exception e){
			System.err.println("editEntityXml.javaのrmdgnmのエラー");
			return null;
		}
	}
	
	/**
	 * Λ,Γを設定する
	 * @param RMD1 数値Λ1
	 * @param RMD2 数値Λ2
	 * @param GNM1 数値Γ1
	 * @param GNM2 数値Γ2
	 * @return Document ドキュメント
	 * @author masato
	 */
	public Document RMDGNM(BigInteger RMD1,BigInteger RMD2,BigInteger GNM1,BigInteger GNM2){
		try{
			Element RAM = document.createElement("Λ");
			Element GAM = document.createElement("Γ");
			Element RAM1E = document.createElement("Λ1");
			Element RAM2E = document.createElement("Λ2");
			Element GAM1E = document.createElement("Γ1");
			Element GAM2E = document.createElement("Γ2");
			RAM1E.appendChild(document.createTextNode(String.valueOf(RMD1)));
			RAM2E.appendChild(document.createTextNode(String.valueOf(RMD2)));
			GAM1E.appendChild(document.createTextNode(String.valueOf(GNM1)));
			GAM2E.appendChild(document.createTextNode(String.valueOf(GNM2)));
			RAM.appendChild(RAM1E);
			RAM.appendChild(RAM2E);
			GAM.appendChild(GAM1E);
			GAM.appendChild(GAM2E);
			root.appendChild(RAM);
			root.appendChild(GAM);
			return(document);
		}catch(Exception e){
			System.err.println("editEntityXml.javaのRMDGNMのエラー");
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