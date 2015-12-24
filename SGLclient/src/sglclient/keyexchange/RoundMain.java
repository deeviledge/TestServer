package sglclient.keyexchange;

import java.math.BigInteger;

import sglclient.groupadmin.GroupSend;
import sglclient.myinformation.MyInformation;

public class RoundMain {
	
	public int round;
	private final int Round_Port = 12766;
	ExchangeKey exKey;
	GroupSend gs;
	/**
	 *  コンストラクタ
	 * @param round ラウンド数
	 * @param middlekey 中間鍵
	 */
	public RoundMain(int round){
		gs = new GroupSend();
		this.round = round;
		exKey = new ExchangeKey();//→ExchangeKey()クラス、コンストラクタなし
		if(this.round==0){
			//秘密鍵の計算
			exKey.generateSecretKey();
		}
		else{
			BigInteger sk = new BigInteger(gs.getKey());
			//前のラウンドの共有鍵を秘密鍵にする
			exKey.refreshKey(sk);
		}
	}
	
	/**
	 * 鍵配送指令書に従ってユーザ間で鍵交換
	 * 
	 * @return 共有鍵
	 */
	public void KeyExchange(){
		
		String exchangeFileName = "src/sglclient/conf/usr/xml_files/groups/Mandate_" + gs.getGroupName() + ".xml";
		System.out.println(gs.getGroupName());
		//　鍵配送指令書の解析
		Mandate m = new Mandate( exchangeFileName );
		MyInformation mi = new MyInformation();
		int myid = Integer.parseInt(mi.getUsrID());
		
		
		//Peerの呼び出し
		Peer peer = m.getKeyAgreement().getPeer(myid);
		String SendToIP;
		String ReceiveFromIP;
		int SendToID;
		int ReceiveFromID;
		//もっとエレガントに書けそう。気が向いたら、書き直します。
		if(peer.getRoundList( round ).getbehavior().equals("Exchange")){
			System.out.println("Round "+(round+1)+":Exchange");
                        
                        
			//送信するPeerの数が一人のとき
			if(peer.getRoundList(round).getSendTo().getListSize()==1){					
				SendToID = peer.getRoundList(round).getSendTo().getPeerList(0).getID();	//送信する相手のIDを取得
				SendToIP = peer.getRoundList(round).getSendTo().getPeerList(0).getIP();	//IPを取得
                                
                                //自分がダミーのとき
				if(SendToID<0){		
					System.out.println("ダミーユーザです。公開鍵受け取り待ち");
					GetPKey GP = new GetPKey(Round_Port);
					String line = GP.KeyExchange();
					//　受信したものが公開鍵と確認できれば共通鍵の計算
					if( line.substring(0,3).equals("pk:") ){
						System.out.println("秘密鍵の作成中...");
						String key = line.substring(3);
						exKey.calculateKey(key);	//公開鍵から共有鍵を計算
						//デバッグ目的で出力してるけど、最終的には消さないと鍵がバレる
						//System.out.println("共通鍵:"+ exKey.getKey());
					}
				}
				
				//先に自分の公開鍵を送る
				else if(myid<SendToID){
					System.out.println("User "+SendToID+"と交換します");
					Wait();	//通信相手ポートをオープンするまで少し待つ
					exKey.calculatePublicKey();	//公開鍵を計算
					SendFrom SF = new SendFrom(SendToIP,Round_Port);		//公開鍵を交換
					String line = SF.KeyExchange(exKey.getPublicKey());		//受信した公開鍵を取得
					//　受信したものが公開鍵と確認できれば共通鍵の計算
					if( line.substring(0,3).equals("pk:") ){
						System.out.println("秘密鍵の作成中...");
						String key = line.substring(3);
						exKey.calculateKey(key);	//公開鍵から共有鍵を計算
						//デバッグ目的で出力してるけど、最終的には消さないと鍵がバレる
						//System.out.println("共通鍵:"+ exKey.getKey());
					}
				}
                                
				//先に相手の公開鍵を受け取る
				else if(myid>SendToID){
					System.out.println("User "+SendToID+"と交換します");
					exKey.calculatePublicKey();	//公開鍵を計算
					FromSend FS = new FromSend(Round_Port);		//公開鍵を交換
					String line = FS.KeyExchange(exKey.getPublicKey());		//受信した公開鍵を取得
					//　受信したものが公開鍵と確認できれば共通鍵の計算
					if( line.substring(0,3).equals("pk:") ){
						System.out.println("秘密鍵の作成中...");
						String key = line.substring(3);
						exKey.calculateKey(key);	//公開鍵から共有鍵を計算
						//デバッグ目的で出力してるけど、最終的には消さないと鍵がバレる
						//System.out.println("共通鍵:"+ exKey.getKey());
					}
				}
			}
                        
                        
			//公開鍵を送信すべき相手が二人いるとき
			else if(peer.getRoundList(round).getSendTo().getListSize()==2){
				ReceiveFromID = peer.getRoundList(round).getReceiveFrom().getID();	//受信する相手のIDを取得
				ReceiveFromIP = peer.getRoundList(round).getReceiveFrom().getIP();	//受信する相手のIPを取得
				//先に自分の公開鍵を送る
				if(myid<ReceiveFromID){
					System.out.println("User "+ReceiveFromID+"と交換します");
					Wait();	//通信相手がポートをオープンするまで少し待つ
					exKey.calculatePublicKey();	//公開鍵を計算
					SendFrom SF = new SendFrom(ReceiveFromIP,Round_Port);		//公開鍵を交換
					String line = SF.KeyExchange(exKey.getPublicKey());		//受信した公開鍵を取得
					//　受信したものが公開鍵と確認できれば共通鍵の計算
					if( line.substring(0,3).equals("pk:") ){
						System.out.println("秘密鍵の作成中...");
						String key = line.substring(3);
						exKey.calculateKey(key);	//公開鍵から共有鍵を計算
						//デバッグ目的で出力してるけど、最終的には消さないと鍵がバレる
						//System.out.println("共通鍵:"+ exKey.getKey());
					}
				}
				//先に相手の公開鍵を受け取る
				else if(myid>ReceiveFromID){
					System.out.println("User "+ReceiveFromID+"と交換します");
					exKey.calculatePublicKey();	//公開鍵を計算
					FromSend FS = new FromSend(Round_Port);		//公開鍵を交換
					String line = FS.KeyExchange(exKey.getPublicKey());		//受信した公開鍵を取得
					//　受信したものが公開鍵と確認できれば共通鍵の計算
					if( line.substring(0,3).equals("pk:") ){
						System.out.println("秘密鍵の作成中...");
						String key = line.substring(3);
						exKey.calculateKey(key);	//公開鍵から共有鍵を計算
						//デバッグ目的で出力してるけど、最終的には消さないと鍵がバレる
						//System.out.println("共通鍵:"+ exKey.getKey());
					}
				}
				//ダミーユーザに鍵を送る
				System.out.println("ダミーユーザに鍵を送信します");
				SendToIP = peer.getRoundList(round).getSendTo().getPeerList(1).getIP();	//送信相手のIPを取得
				SendPKey SPK = new SendPKey(SendToIP,Round_Port);
				SPK.KeyExchange(exKey.getPublicKey());
				
				
			}
		}
		else if(peer.getRoundList( round ).getbehavior().equals("Wait")){
			exKey.renewalKey();	//秘密鍵をそのまま、次のラウンドの秘密鍵にする(秘密鍵を共通鍵として保存).
			System.out.println("Round "+(round+1)+":Wait");
		}
		
		try {
			gs.SaveKey(""+exKey.getKey());	//GroupSend.xmlに中間鍵を保存
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	/**
	 * 待ち時間をつくる
	 * @author nishimura
	 */
	public void Wait() {
	    try {
	        Thread.sleep(1 * 300);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
	
}
