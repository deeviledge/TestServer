package sglserver.servermain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import sglserver.dynamicpeerinformation.DynamicPeerInformation;
import sglserver.peerbasicinformation.PeerBasicInformationEdit;

/**
 * ログインユーザ情報をクライアントに提供するクラス
 * @author masato
 * @version 1.2
 * @作成日: 2008/10/15
 * @最終更新日:2008/10/31
 */
public class GetUsrInfoProcess {
	
	/**
	 * コンストラクタ
	 * @param soc ソケット
	 * @param ois　インプットストリーム
	 * @param oos　アウトプットストリーム
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public GetUsrInfoProcess(Socket soc,ObjectInputStream ois,ObjectOutputStream oos) throws IOException, ClassNotFoundException
	{
		String res = null;
		//String uid = (String) ois.readObject();     //クライアントからユーザIDを受け取る
		String uname = (String) ois.readObject();     //クライアントからユーザ名を受け取る
		//認証を後まわし
		oos.writeObject("OK");	                  //クライアントにログイン認証サインを送る
		
		PeerBasicInformationEdit pb = new PeerBasicInformationEdit();
		boolean b = pb.isPeerName(uname);
		if(b == true){
			DynamicPeerInformation dyinfo = new DynamicPeerInformation();
			boolean b2 = dyinfo.isPeerID(dyinfo.getID(uname));
			if(b2 == true){
				res = "ON";
			}else{
				res = "OFF";
			}
		}else{
			res = null;
		}
		oos.writeObject(res);	                  //クライアントにユーザ名を通知する
	}
}
