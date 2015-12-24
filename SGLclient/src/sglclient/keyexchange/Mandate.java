/*
 * 作成日: 2006/11/14
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package sglclient.keyexchange;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 鍵交換指令書の読み込みと動作
 */
class Mandate {
	/**
	 * KeyAgreementクラスの変数
	 */
	private KeyAgreement keyag;
	/**
	 * コンストラクタ
	 * @param filename ファイル名
	 */
	public Mandate(String filename){
		try{
			readXML(filename);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 *
	 */
	private final boolean debug = false;
	/*
	 *
	 */
	public KeyAgreement getKeyAgreement(){
		return(keyag);
	}
	/**
	 * XMLファイル読み込み
	 * @param filename ファイル名
	 */
	public void readXML(String filename) throws Exception {
		// ドキュメントビルダーファクトリを生成
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// ドキュメントビルダーを生成
		DocumentBuilder builder = factory.newDocumentBuilder();
		// パースを実行してDocumentオブジェクトを取得
		Document document = builder.parse( filename );
		// ルート要素を取得
		Element root = document.getDocumentElement();

		// KeyAgreement要素のリストを取得
		NodeList ka = root.getElementsByTagName("KeyAgreement");
		// KeyAgreementのIDを取得
		String kaID = ((Element)ka.item(0)).getAttribute("xmlns:ID");
		if(debug) System.out.println("KeyAgreement ID = "+ kaID);

		// Group要素のリストを取得
		NodeList gr = ((Element)ka.item(0)).getElementsByTagName("Group");
		// GroupのCountを取得
		String gcount = ((Element)gr.item(0)).getAttribute("xmlns:Count");
		if(debug) System.out.println("\tGroup Count = "+ gcount);
		keyag = new KeyAgreement();
		keyag.setID( Integer.parseInt( kaID ) );
		keyag.setGroupCount( Integer.parseInt( gcount ) );

		// Peer要素のリストを取得
		NodeList pe = ((Element)ka.item(0)).getElementsByTagName("Peer");
		
		
			if(debug) System.out.println("pe.item(i).getParentNode().getNodeName():"+ pe.item(0).getParentNode().getNodeName());
			if(pe.item(0).getParentNode().getNodeName().equals("KeyAgreement")){
				// PeerのIDを取得
				String pID = ((Element)pe.item(0)).getAttribute("xmlns:ID");
				if(debug) System.out.println("\tPeer ID = "+ pID);
				// Peerのaltを取得
				String palt = ((Element)pe.item(0)).getAttribute("xmlns:alt");
				if(debug) System.out.println("\tPeer alt = "+ palt);
				Peer peer = new Peer();
				peer.setID( Integer.parseInt( pID ) );
				if(palt.equals("")){ palt = "-1"; }
				peer.setalt( Integer.parseInt( palt ) );
				
				// Round要素のリストを取得
				NodeList ro = ((Element)pe.item(0)).getElementsByTagName("Round");
				// Roundの数だけループ
				for(int j=0;j<ro.getLength();j++){
					// Roundのroundを取得
					String rround = ((Element)ro.item(j)).getAttribute("round");
					if(debug) System.out.println("\t\tRound round = "+ rround);
					
					// RoundのOption要素のリストを取得
					NodeList op = ((Element)ro.item(j)).getElementsByTagName("Option");
					// Option要素のノードの値を取得
					String option = op.item(0).getFirstChild().getNodeValue();
					// 前後の空白を取り除く
					option = option.trim();
					if(debug) System.out.println("\t\t\tOption = "+ option);
					
					// Roundのbehavior要素のリストを取得
					NodeList be = ((Element)ro.item(j)).getElementsByTagName("Behavior");
					// behavior要素のノードの値を取得
					String behavior = be.item(0).getFirstChild().getNodeValue();
					// 前後の空白を取り除く
					behavior = behavior.trim();
					if(debug) System.out.println("\t\t\tbehavior = "+ behavior);
					
					// RoundのGroupID要素のリストを取得
					//NodeList gid = ((Element)ro.item(j)).getElementsByTagName("GroupID");
					// GroupID要素のノードの値を取得
					//String groupid = gid.item(0).getFirstChild().getNodeValue();
					// 前後の空白を取り除く
					//groupid = groupid.trim();
					//if(debug) System.out.println("\t\t\tGroupID = "+ groupid);
					
					
					//GroupName部分　要修正
					
					// RoundのGroupName要素のリストを取得
					//NodeList gn = ((Element)ro.item(j)).getElementsByTagName("GroupName");
					// GroupName要素のノードの値を取得
					//String groupname = gn.item(0).getFirstChild().getNodeValue();
					// 前後の空白を取り除く
					//groupname = groupname.trim();
					//if(debug) System.out.println("\t\t\tGroupName = "+ groupname);
					Round round = new Round();
					round.setRoundNumber( Integer.parseInt( rround ) );
					round.setOption( option );
					
					round.setbehavior( behavior );
					//round.setGroupID( Integer.parseInt( groupid ) );
					//round.setGroupName( groupname );
					
					

					if(behavior.equals("Exchange")){
					
					// RoundのSendTo要素のリストを取得
					NodeList se = ((Element)ro.item(j)).getElementsByTagName("SendTo");
					SendTo sendto = new SendTo();
					// SendToのPeer要素のリストを取得
					NodeList sp = ((Element)se.item(0)).getElementsByTagName("Peer");
					// Peerの数だけループ
					for(int k=0;k<sp.getLength();k++){
						// PeerのID要素のリストを取得
						NodeList peid = ((Element)sp.item(k)).getElementsByTagName("ID");
						// ID要素のノードの値を取得
						String peID = peid.item(0).getFirstChild().getNodeValue();
						// 前後の空白を取り除く
						peID = peID.trim();
						if(debug) System.out.println("\t\t\t\tPeer ID = "+ peID);
						
						// Peerのalt要素のリストを取得
						NodeList peal = ((Element)sp.item(k)).getElementsByTagName("alt");
						// ID要素のノードの値を代入する変数の宣言
						String pealt = new String();
						if(peal.item(0)!=null){
							// ID要素のノードの値を取得
							pealt = peal.item(0).getFirstChild().getNodeValue();
							// 前後の空白を取り除く
							pealt = pealt.trim();
						}
						if(debug) System.out.println("\t\t\t\tPeer alt = "+ pealt);
						
						// PeerのIP要素のリストを取得
						NodeList peip = ((Element)sp.item(k)).getElementsByTagName("IP");
						// IP要素のノードの値を取得
						String peIP = peip.item(0).getFirstChild().getNodeValue();
						// 前後の空白を取り除く
						peIP = peIP.trim();
						if(debug) System.out.println("\t\t\t\tPeer IP = "+ peIP);
						
						// PeerのRelayIP要素のリストを取得
						NodeList perip = ((Element)sp.item(k)).getElementsByTagName("RelayIP");
						// RelayIP要素のノードの値を代入する変数の宣言
						String peRIP = new String();
						if(perip.item(0).getFirstChild()!=null){
							// RelayIP要素のノードの値を取得
							peRIP = perip.item(0).getFirstChild().getNodeValue();
							// 前後の空白を取り除く
							peRIP = peRIP.trim();
						}
						if(debug) System.out.println("\t\t\t\tPeer RelayIP = "+ peRIP);
						
						SendToPeer sendtopeer = new SendToPeer();
						sendtopeer.setID( Integer.parseInt( peID ) );
						if(pealt.equals("")){ pealt = "-1"; }
						sendtopeer.setalt( Integer.parseInt( pealt ) );
						sendtopeer.setIP( peIP );
						sendtopeer.setRelayIP( peRIP );
						sendto.addPeerList( sendtopeer );
					}
					round.setSendTo( sendto );
					
					// RoundのReceiveFrom要素のリストを取得
					NodeList rf = ((Element)ro.item(j)).getElementsByTagName("ReceiveFrom");
					ReceiveFrom receivefrom = new ReceiveFrom();
					// ReceiveFromのID要素のリストを取得
					NodeList rfid = ((Element)rf.item(0)).getElementsByTagName("ID");
					//ID要素のノードの値を代入する変数の宣言
					String rfID = new String();
					if(rfid.item(0) != null){
						// ID要素のノードの値を取得
						rfID = rfid.item(0).getFirstChild().getNodeValue();
						// 前後の空白を取り除く
						rfID = rfID.trim();
					}
					if(debug) System.out.println("\t\t\tReceiveFrom ID = "+ rfID);
					
					// ReceiveFromのalt要素のリストを取得
					NodeList rfal = ((Element)rf.item(0)).getElementsByTagName("alt");
					// alt要素のノードの値を代入する変数の宣言
					String rfalt = new String();
					if(rfal.item(0) != null){
						// alt要素のノードの値を取得
						rfalt = rfal.item(0).getFirstChild().getNodeValue();
						// 前後の空白を取り除く
						rfalt = rfalt.trim();
					}
					if(debug) System.out.println("\t\t\tReceiveFrom alt = "+ rfalt);
					
					// ReceiveFromのIP要素のリストを取得
					NodeList rfip = ((Element)rf.item(0)).getElementsByTagName("IP");
					// IP要素のノードの値を代入する変数の宣言
					String rfIP = new String();
					if(rfip.item(0) != null){
						// IP要素のノードの値を取得
						rfIP = rfip.item(0).getFirstChild().getNodeValue();
						// 前後の空白を取り除く
						rfIP = rfIP.trim();
					}
					if(debug) System.out.println("\t\t\tReceiveFrom IP = "+ rfIP);
					
					// ReceiveFromのRelayIP要素のリストを取得
					NodeList rfrip = ((Element)rf.item(0)).getElementsByTagName("RelayIP");
					// RelayIP要素のノードの値を代入する変数の宣言
					String rfRIP = new String();
					if(rfrip.item(0) != null && rfrip.item(0).getFirstChild() != null){
						// RelayIP要素のノードの値を取得
						rfRIP = rfrip.item(0).getFirstChild().getNodeValue();
						// 前後の空白を取り除く
						rfRIP = rfRIP.trim();
					}
					if(debug) System.out.println("\t\t\tReceiveFrom RelayIP = "+ rfRIP);
					
					if(rfID.equals("")){ rfID = "-1"; }
					receivefrom.setID( Integer.parseInt( rfID ) );
					if(rfalt.equals("")){ rfalt = "-1"; }
					receivefrom.setalt( Integer.parseInt( rfalt ) );
					receivefrom.setIP( rfIP );
					receivefrom.setRelayIP( rfRIP );
					
					round.setReceiveFrom( receivefrom );
					
					peer.addRoundList( round );
					}
					
					else{
						//System.out.println("Wait...");
						
						peer.addRoundList( round );
					}
					
				}
				keyag.addPeerList( peer );
			}
		
	}
}