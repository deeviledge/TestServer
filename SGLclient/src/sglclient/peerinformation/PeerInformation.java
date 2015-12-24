package sglclient.peerinformation;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * PeerInformation.xmlを扱うクラス
 * @author toru
 * @version 1.1
 * @作成日: 2005/01/08
 * @最終更新日:2005/01/08
 */
public class PeerInformation {
	private Document document;
	private Element root;
	
	/**
	 * ファイルを読み込んでこのクラスを初期化
	 * @param uri ファイルの場所(ファイル名を含む)
	 */
	public PeerInformation(String uri){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.document = builder.parse( uri );
			this.root = this.document.getDocumentElement();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 読み込んだファイルのPeerIDを返します
	 * @return PeerID
	 * @author toru
	 */
	public String getPeerID(){
		// ルート要素からGroupのノードリストを取得
		NodeList g = this.root.getElementsByTagName("BasicInformation");
		Element gElement = (Element)g.item(0);
		return( gElement.getAttribute("xmlns:ID") );
	}
	
	/**
	 * 読み込んだファイルのPeerNameを返します
	 * @return PeerName
	 * @author toru
	 */
	public String getPeerName(){
		// ルート要素からGroupのノードリストを取得
		NodeList g = root.getElementsByTagName("BasicInformation");
		Element userElement = (Element)g.item(0);
		NodeList peer = userElement.getElementsByTagName("UserName");
		return( peer.item(0).getFirstChild().getNodeValue() );
	}
	
	/**
	 * 読み込んだファイルのCertificationを返します
	 * @return Certification
	 * @author toru
	 */
	public String getPeerCertification(){
		// ルート要素からGroupのノードリストを取得
		NodeList g = root.getElementsByTagName("BasicInformation");
		Element userElement = (Element)g.item(0);
		NodeList peer = userElement.getElementsByTagName("Certification");
		return( peer.item(0).getFirstChild().getNodeValue() );
	}
}
