
/**
 * MandateXML(鍵配送指令書)の骨組みのみを作成するクラス
 * @auther fujino
 * @version 2.0
 * @作成日: 2008/11/17
 * @最終更新日:2008/11/17
 */

package sglserver.mandate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import sglserver.groupinformation.ReadGroupInfoXML;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class MandateXMLbase{

	private	Document	document;
	private	Element		root;
	
	
	/**
	 * コンストラクタ
	 * 
	 * @param	N			グループメンバ数
	 * @param	T			総ラウンド数
	 * @param	filename	読み込みファイル名
	 * 
	 * @author	fujino
	 */
	public MandateXMLbase(int N, int T, String filename, String groupname){

		// グループ情報読み込みクラス
		ReadGroupInfoXML gInfo = new ReadGroupInfoXML(groupname);
		
		try {
			// Document関連
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
			document = docbuilder.newDocument();
  
			//Elementなどの宣言
			Element PeerElement             = null;
			Element RoundElement		= null;
			Element BehaviorElement		= null;
			Element SendToElement		= null;
			Element ReceiveFromElement	= null;
			//Element OptionElement		= null;
			//Element GroupIDElement		= null;
			//Element GroupNameElement	= null;
			
			// root要素として Mandate要素を生成
			root = document.createElement("Mandate");
			// ノードをDocumentに追加
			document.appendChild(root);

			// KeyAgreementノードを作成 & Mandateの下に追加
			Element KeyAgreementElement = document.createElement("KeyAgreement");
			root.appendChild(KeyAgreementElement);									

			// Groupノード作成 & KeyAgreementの下に追加
			Element GroupElement = document.createElement("Group");
			KeyAgreementElement.appendChild(GroupElement);
			// Count属性の付加　(初期値:0)
			// グループ鍵は一つのグループ単位で作るのでグループ数の属性は消しておきます。必要なら復活してください。
			//Attr Groupattr = document.createAttribute("xmlns:Count");
			//Groupattr.setValue(String.valueOf(0));
			//GroupElement.setAttributeNode(Groupattr);
			
			// GroupName属性の付加
			Attr GroupNameAttr = document.createAttribute("xmlns:GroupName");
			GroupNameAttr.setValue(String.valueOf(groupname));
			GroupElement.setAttributeNode(GroupNameAttr);
			// Option属性の付加
			Attr GroupOptAttr = document.createAttribute("xmlns:Option");
			GroupOptAttr.setValue(String.valueOf(gInfo.getGroupOption()));
			GroupElement.setAttributeNode(GroupOptAttr);
			

			// グループメンバ数だけループ
			// 1 からはじめているのでxmlからの読み込み時は -1 します
			for(int peer=1; peer<=N; peer++){
				// Peerエレメントの追加
				PeerElement = document.createElement("Peer");
				
				//　Round数だけループ
				for(int rnd=1; rnd<=T; rnd++){
					//必要なエレメントを作成
					RoundElement		= document.createElement("Round");
					BehaviorElement		= document.createElement("Behavior");
					SendToElement		= document.createElement("SendTo");
					ReceiveFromElement	= document.createElement("ReceiveFrom");
					// Peerの上位に付加しました。　Peerごとに必要なら復活してください。
					//OptionElement		= document.createElement("Option");
					//GroupIDElement		= document.createElement("GroupID");
					//GroupNameElement	= document.createElement("GroupName");
					
					//KeyAgreementの下にPeerを付加
					KeyAgreementElement.appendChild(PeerElement);
					//Peerの属性IDを作成
					Attr Peerattr  = document.createAttribute("xmlns:ID");					
					//属性IDに値を入れる
					Peerattr.setValue(String.valueOf(gInfo.getMemberID(peer-1)));
					//Peerエレメントに属性IDを付加
					PeerElement.setAttributeNode(Peerattr);
					
					//Peerの属性altを作成
					Attr Peerattr2 = document.createAttribute("xmlns:alt");
					//属性altに値を入れる ("1"を入れているが用途不明)
					Peerattr2.setValue(String.valueOf(1));
					//Peerエレメントに属性altを付加
					PeerElement.setAttributeNode(Peerattr2);
					
					//Peerエレメントの下にRoundを付加
					PeerElement.appendChild(RoundElement);
					//Roundの属性roundを作成
					Attr Roundattr = document.createAttribute("round");
					//属性roundに値を入れる
					Roundattr.setValue(String.valueOf(rnd));
					//Roundエレメントに属性roundを付加
					RoundElement.setAttributeNode(Roundattr);
					
					//　Roundエレメントの下にいろいろ付加 ↓↓
					// <Option>, <Behavior>, <GroupID>, <GroupName>, <SendTo>, <RecieveFrom>
					RoundElement.appendChild(BehaviorElement);
					RoundElement.appendChild(SendToElement);
					RoundElement.appendChild(ReceiveFromElement);
					//RoundElement.appendChild(OptionElement);						
					//RoundElement.appendChild(GroupIDElement);
					//RoundElement.appendChild(GroupNameElement);
				}
			}
			
			// XMLファイルの保存
			// DOMオブジェクトを文字列として出力 
			new SaveXML(filename, document);

		} catch (ParserConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}