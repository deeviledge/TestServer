/**
 * 鍵配送指令書"Mandate.xml"に情報を書き込むクラス
 * 
 * @author fujino,kawata
 * @version 2.0
 * @作成日: 2008/11/17
 * @最終更新日:2009/1/5
 */

package sglserver.mandate;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import sglserver.dynamicpeerinformation.DynamicPeerInformation;
import sglserver.groupinformation.ReadGroupInfoXML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WriteMandateXML {

	private Document	document;
	private Element		root;
	
	//static String	groupname = "size5";
	static String	mandatefile;
	static ReadGroupInfoXML gInfo;
	
	static int	t_cnt = 0;	// ラウンドカウント用変数
	static int	N;			// グループメンバ数
	static int	T;			// 総ラウンド数
	
	static boolean	dummy;
	
	/**
	 * コンストラクタ
	 */
	public WriteMandateXML(){
		
	}
	
	/**
	 * コンストラクタ
	 * 
	 * @param t				ラウンドの深さ
	 * @param user_A		前半部分の1番目のユーザ
	 * @param user_B		後半部分の1番目のユーザ
	 * @param dummyflag		範囲内にダミーユーザが存在するかどうか
	 * @param filename		鍵配送指令書のファイル名
	 */
	public WriteMandateXML(int t, int user_A, int user_B, boolean dummyflag, ReadGroupInfoXML ginfo, String filename){
		try{
		
			mandatefile = filename;
			dummy = dummyflag;		// この範囲にダミーユーザがいるかどうか記憶
		
			// 指定したグループの情報読み込みクラス
			//ReadGroupInfoXML gInfo = new ReadGroupInfoXML(groupname);
			gInfo = ginfo;
			N = gInfo.getGroupValue();
			T = (int) Math.ceil( (Math.log(N)/Math.log(2)) );
		
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得
			document = builder.parse(new File(mandatefile));
			// ルート要素を取得（タグ名：Mandate）
			root = document.getDocumentElement();
			//KeyAgreementのリストを取得
			NodeList KeyAgreementlist = root.getElementsByTagName("KeyAgreement");
			//KeyAgreement要素を取得
			Element KeyAgreementElement = (Element)KeyAgreementlist.item(0);  
			//Peerのリストを取得
			NodeList Peerlist = KeyAgreementElement.getElementsByTagName("Peer");

			// エレメント等の宣言
			NodeList Roundlist = null;
			Element PeerElement = null;
			Element RoundElement = null;
			String behavior = null;
			
			/** @param cnt		範囲内で移動した数（1つずつ鍵交換のペアを移動させていくため）
			 *  @param user1	現在指定しているユーザのID
			 *  @param user2	user1の相手となるユーザのID */
			int cnt=0, user1=0, user2=0;
			
			// ユーザを 1 からuser_B(後半部分の1番目のユーザ)まで指定
			for(int ucnt=1; ucnt<user_B; ucnt++){
				
				// Waitするユーザの処理書き込み
				// 前半の範囲よりも前のユーザで鍵交換しないユーザは全てWaitさせる
				if(ucnt < user_A){
					// Peer,Roundの指定
					PeerElement = (Element)Peerlist.item(ucnt-1);
					Roundlist = PeerElement.getElementsByTagName("Round");
					RoundElement = (Element)Roundlist.item((T-t)-1);
					// "Behavior"が null だった場合に"Wait"を書き込む　（鍵交換するユーザには上書きしない）
					if(RoundElement.getElementsByTagName("Behavior").item(0).getFirstChild() == null){
						// "Wait"はそのラウンドでは鍵交換しないことを意味する
						behavior = "Wait";
						writeBehavior(RoundElement, behavior);
					}
				// 範囲内にいるユーザの処理
				}else{
					// 移動分の指定
					cnt = ucnt - user_A;

					// 自分側と相手側の両方に情報を書き込む
					for(int i=0; i<2; i++){
						switch(i){
						// 自分側の書き込む情報を与える
						case 0:
							// 書き込む場所を指定
							PeerElement = (Element)Peerlist.item(ucnt-1);
							Roundlist = PeerElement.getElementsByTagName("Round");
						
							// "Exchange"は鍵交換をすることを意味する
							behavior = "Exchange";
							user1 = user_A + cnt;	// 鍵交換ペアその1 （現在指定しているユーザ）
							user2 = user_B + cnt;	// 鍵交換ペアその2 （相手ユーザ）
					
							break;
						// 相手側の書き込む情報
						case 1:
							PeerElement = (Element)Peerlist.item(user_B+cnt-1);
							Roundlist = PeerElement.getElementsByTagName("Round");
						
							behavior = "Exchange";
							// 1回目（上の処理）とは逆になるように指定　（相手側に現在のユーザ情報を書き込むため）
							user1 = user_B + cnt;
							user2 = user_A + cnt;
					
							break;
						}
						// Round要素を取得
						RoundElement = (Element)Roundlist.item((T-t)-1);

						// "Behavior"に実際に情報を書き込むメソッド
						// "Behavior" :鍵交換を行うか、待つかが指定される 
						writeBehavior(RoundElement, behavior);

						// "SendTo"に実際に情報を書き込むメソッド
						// "SendTo" :鍵交換時に鍵を送信する相手の情報が書き込まれる
						writeSendTo(RoundElement, user1, user2, dummyflag);
					
						// "ReceiveFrom"に実際に情報を書き込むメソッド
						// "ReceiveFrom" :鍵交換時に鍵を受信する相手の情報が書き込まれる
						writeReceiveFrom(RoundElement, user2);
					
						// ダミーユーザは1回しか存在しないので、一度処理が終わればflagを折る
						// dummyflag は一時的な情報格納に使用している（実際にダミーがいるかは dummy に格納）
						if(dummyflag == true)
							dummyflag = false;
					}
				}
			}
			// ダミーユーザの通信相手の情報を書き込む
			// ダミーユーザは後半部分の最後のユーザ（"user_B+1"番目）となるので上の処理のループが終わった後に処理をしています
			// TODO :実際はダミーユーザの相手なので、"Dummy"を書き込むべきではないかも
			PeerElement = (Element)Peerlist.item(user_B+(cnt+1)-1);
			// 上書きを防ぐためそのユーザのエレメントが空だったときに書き込む
			if(dummy && (PeerElement != null)){
				// "Dummy" :ダミーユーザの通信相手　　鍵の受信のみを行って個人で共有鍵を計算する
				behavior = "Remainder";
				Roundlist = PeerElement.getElementsByTagName("Round");
				RoundElement = (Element)Roundlist.item((T-t)-1);
				writeBehavior(RoundElement, behavior);
				writeSendTo(RoundElement, user_B+(cnt+1), user_B+(cnt+1), dummyflag);
				writeReceiveFrom(RoundElement, user_A);
			}
		
			// XMLファイルの保存
			// DOMオブジェクトを文字列として出力 
			new SaveXML(filename, document);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * オプションを書き込むメソッド
	 */
	public void writeOption(){
		
	}
	
	/**
	 * グループIDを書き込むメソッド
	 */
	public void writeGroupID(){
		
	}
	
	/**
	 * グループ名を書き込むメソッド
	 */
	public void writeGroupName(){
		
	}

	/**
	 * "Behavior"（鍵交換を行うかどうか）を書き込むメソッド
	 * 
	 * @param RoundElement	書き込むべきユーザの"Round"のエレメント
	 * @param behavior		書き込む"Behavior"の情報　（"Exchange" or "Wait" or "Dummy"）
	 */
	public void writeBehavior(Element RoundElement, String behavior){
		
		((Element) RoundElement.getElementsByTagName("Behavior").item(0)).appendChild(document.createTextNode(behavior));
	}
	
	/**
	 * "SendTo"（鍵を送信する相手情報）を書き込むメソッド
	 * 自分がダミーのとき、ダミーの相手のときの書き込む全てが含まれています。　なので場合分けしてもいいかもしれないです。
	 * 
	 * @param RoundElement	書き込むべきユーザの"Round"のエレメント
	 * @param user_A
	 * @param user_B
	 * @param dummyflag
	 */
	public void writeSendTo(Element RoundElement, int user_A, int user_B, boolean dummyflag){
		
		DynamicPeerInformation dInfo = new DynamicPeerInformation();
		int	CNT=0;		// 繰り返し回数（ダミーユーザが存在するときの繰り返し用）
		int	i, user;	// ループ用の変数
		int remainderID;	// ダミーユーザの相手となるユーザのID
		
		// 指定したグループの情報を読み込むクラス
		//ReadGroupInfoXML gInfo = new ReadGroupInfoXML(groupname);
		
		// 書き込むべき"SendTo"の場所（エレメント）
		Element SendToElement = (Element)RoundElement.getElementsByTagName("SendTo").item(0);
		
		// ダミーユーザが存在するとき、ダミーユーザは相手分の情報も書き込むため2回目の処理をする
		if(dummyflag == true){
			CNT = 2;
			remainderID = user_B + (user_B-user_A);
		}
		// ダミーユーザが存在しないときは書き込みは1回のみでOK
		else{
			CNT = 1;
			remainderID = 0;	// 初期値 ：0 に意味はないです
		}
		
		// "SendTo"内の書き込み処理（2回目に行うリマインダーユーザはforの変化値で指定）
		for(i=1,user=user_B; i<=CNT; i++,user=remainderID){
			
			// Peerエレメントを作成・付加
			Element PeerElement = document.createElement("sPeer");
			SendToElement.appendChild(PeerElement);
			
			// IDエレメントを作成・付加
			Element IDElement = document.createElement("ID");
			PeerElement.appendChild(IDElement);
			// 自分がダミーのときには"-1"を入力します
			if(user_A == user_B)
				IDElement.appendChild(document.createTextNode(Integer.toString(-1)));
			else{
				IDElement.appendChild(document.createTextNode(Integer.toString(gInfo.getMemberID(user-1))));

				// altエレメントを作成・付加
				Element altElement = document.createElement("alt");
				PeerElement.appendChild(altElement);
				altElement.appendChild(document.createTextNode("1"));

				// IPエレメントを作成・付加
				Element IPElement = document.createElement("IP");
				PeerElement.appendChild(IPElement);
				IPElement.appendChild(document.createTextNode(dInfo.getIP(gInfo.getGroupMember(user-1))));

				// RelayIPエレメントを作成・付加
				Element RelayIPElement = document.createElement("RelayIP");
				PeerElement.appendChild(RelayIPElement);
				RelayIPElement.appendChild(document.createTextNode(dInfo.getRemoteIP(gInfo.getGroupMember(user-1))));
			}
		}
	}
	
	/**
	 * "RecieveFrom"（鍵を受信する相手情報）を書き込むメソッド
	 * 
	 * @param RoundElement	書き込むべきユーザの"Round"のエレメント
	 * @param user			受信相手のユーザの番号（何番目のユーザか　：IDではない）
	 */
	public void writeReceiveFrom(Element RoundElement, int user){

		DynamicPeerInformation dInfo = new DynamicPeerInformation();
		
		// ReceiveFromのリスト取得
		NodeList ReceiveFromlist = RoundElement.getElementsByTagName("ReceiveFrom");
		//ReceiveFromの要素取得
		Element ReceiveFromElement = (Element)ReceiveFromlist.item(0);
	      
		// IDエレメントを作成・付加
		Element IDElement = document.createElement("ID");
		ReceiveFromElement.appendChild(IDElement);
		IDElement.appendChild(document.createTextNode(Integer.toString(gInfo.getMemberID(user-1))));

		// altエレメントを作成・付加
		Element altElement = document.createElement("alt");
		ReceiveFromElement.appendChild(altElement);
		altElement.appendChild(document.createTextNode("1"));

		// IPエレメントを作成・付加
		Element IPElement = document.createElement("IP");
		ReceiveFromElement.appendChild(IPElement);
	 	IPElement.appendChild(document.createTextNode(dInfo.getIP(gInfo.getGroupMember(user-1))));

		// RelayIPエレメントを作成・付加
	 	Element RelayIPElement = document.createElement("RelayIP");
		ReceiveFromElement.appendChild(RelayIPElement);
		RelayIPElement.appendChild(document.createTextNode(dInfo.getRemoteIP(gInfo.getGroupMember(user-1))));

	}
	
}
