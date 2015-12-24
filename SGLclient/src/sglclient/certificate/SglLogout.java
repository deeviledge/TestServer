package sglclient.certificate;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import sglclient.myinformation.MyInformation;
import sglclient.option.EditOptionXml;

/**
 * SGLサーバからログアウトするクラス
 * @auhor masato
 * @version 1.0
 * @作成日: 2007/7/9 
 * @最終更新日:2007/7/9
 */
public class SglLogout {
	String logout = "logout";        //SGLサーバへのログアウトワード
	String sglserverip = new EditOptionXml().getIP();
	
	/**
	 * SGLサーバからログアウトする
	 */
	public SglLogout() {
		try {
			MyInformation mi = new MyInformation();
			String myid = mi.getUsrID();
			//ソケットを生成する
			Socket soc = new Socket(sglserverip,12345);        
			// 入出力ストリームを取得する
			ObjectOutputStream oos = new ObjectOutputStream(soc.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(soc.getInputStream());
			//ログアウト要求
			oos.writeObject(logout);     
			//ユーザIDを送信
			oos.writeObject(myid);    
			System.out.println("Logout SGLServer!");
			//ストリーム・ソケットを閉じる	
			oos.close();
			ois.close();     
			soc.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}