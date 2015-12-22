package sglserver.pskey;

import java.text.ParseException;
import sglserver.servermain.ServerConnect;

/**
 * ？？？？？？？？？？？？？
 * @author kinbara
 * @version 1.2
 * @作成日: 不明
 * @最終更新日:2008/10/31
 */
public class PSKeyServerXMLGenerateThread extends Thread{

	private boolean threadcheck = false;
	private ServerConnect s;
	
	/**
	 *  コンストラクタ
	 */
	public PSKeyServerXMLGenerateThread()
	{
		this.start();    // スレッドを開始する
	}
	
	/**
	 * スレッド部分
	 */
	public void run(){
		try {
			while(true){
				if(threadcheck == true){ // ServerConnectスレッドが動いている場合
					try {
						System.out.println("スレッドを止めます。");
						s.join(); // ServerConnectスレッドが動いている間は停止
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("スレッドを再開します。");
					this.threadcheck = false; // falseに
				}
				this.ReGeneratePSKeyServer(); // 更新
				try {
					//System.out.println("test");
					Thread.sleep(1000*3600*24); // スレッドを1日停止
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ServerConnectスレッドが動いている間このスレッドを停止させるためのメソッド
	 * @param se
	 */
	public void stopThread(ServerConnect se){
		this.s = se;
		this.threadcheck = true; // ServerConnectスレッドが動いている間このスレッドを停止させるためのフラグ
	}
	
	/**
	 * PreSharedKeyServer.xmlを更新する
	 * @throws ParseException
	 */
	private void ReGeneratePSKeyServer() throws ParseException{
		PreSharedKeyServer psks = new PreSharedKeyServer();
		//PeerBasicInformationEdit peer = new PeerBasicInformationEdit();
		//RegenerateKeyServer re = new RegenerateKeyServer();
		RegenerateKeyServer re = new RegenerateKeyServer();
		//if(psks.isChangeValidityValue() == true || psks.getMAX_MEMBER() < peer.getGroupLength()){ // PreSharedKeyの期限が切れているか、ユーザの数がPreSharedKeyのサイズを超えている場合
		if(psks.isChangeValidityValue() == true){ // PreSharedKeyの期限が切れていれば
			System.out.println("PreSharedKeyServer.xmlを更新します。");
			re.RegeneratePreSharedKeyServer(); // 更新
		}else{ // 切れていない場合
			//System.out.println("PreSharedKeyServerを更新する必要はありません。");
		}
	}
}
