package sglserver.peerbasicinformation;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/**
 * conf/usr/PeerBasicInformation.xmlを新規作成するクラス
 * @author toru
 * @version 1.4
 * @作成日: 2005/01/07
 * @最終更新日:2008/10/31
 */
public class PeerBasicInformationXml {
	
	private Document document;
	private Element root;
	
	/**
	 * Root要素にPeerBasicInformationを持ったXML文書を新規作成します
	 */
	public PeerBasicInformationXml(){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
			root = document.createElement("PeerBasicInformation");
			document.appendChild(root);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ユーザーを追加します 
	 * @param id ユーザーID
	 * @param name ユーザー名
	 * @param eddress E-mailアドレス
	 * @param certification X.509ファイルの保存場所
	 * @return 追加されたドキュメントを返します
	 * @author toru
	 */
	public void peerBasicInformationXmlGenarator(String id, String name, String eddress, String certification){
		try{			
			NodeList pInfolist = root.getElementsByTagName("BasicInformation");
			Element pInfo = (Element)pInfolist.item(0);
			pInfo = document.createElement("BasicInformation");
			Attr idtag = document.createAttribute("xmlns:ID");
			idtag.setValue (id);
			pInfo.setAttributeNode(idtag);
			Element pName = document.createElement("PeerName");
			pName.appendChild(document.createTextNode(name));
			pInfo.appendChild(pName);
			Element pEddress = document.createElement("MailAddress");
			pEddress.appendChild(document.createTextNode(eddress));
			pInfo.appendChild(pEddress);
			Element pCerti = document.createElement("Certification");
			pCerti.appendChild(document.createTextNode(certification));
			pInfo.appendChild(pCerti);
			root.appendChild(pInfo);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * このドキュメントを保存します
	 * @param filename 保存するファイル名(パスを含む)
	 * @throws Exception ファイルの保存時に起きたエラー
	 * @author toru
	 */
	public void saveFile() throws Exception {
		//xml保存 変換
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		//xml保存
		DOMSource source = new DOMSource(document);
		File newXML = new File( "src/sglserver/conf/usr/xml_files/PeerBasicInformation.xml" ); 
		FileOutputStream os = new FileOutputStream(newXML); 
		StreamResult result = new StreamResult(os); 
		transformer.transform(source, result);
	}
	
	/**
	 * @return document を戻します
	 * @author toru
	 */
	Document getDocument() {
		return document;
	}

	/**
	 * ドキュメント、Root要素の設定
	 * @param doc セットするドキュメント
	 * @author toru
	 */
	void setDocument(Document doc){
		this.document = doc;
		this.setRoot(doc.getDocumentElement());
	}

	/**
	 * @return root を戻します。
	 * @author toru 
	 */
	Element getRoot() {
		return root;
	}
	
	/**
	 * @param root root を設定。
	 * @author toru
	 */
	private void setRoot(Element root) {
		this.root = root;
	}
}