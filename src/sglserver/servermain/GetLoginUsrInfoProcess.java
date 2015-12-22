package sglserver.servermain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import sglserver.dynamicpeerinformation.DynamicPeerInformation;

/**
 * ログインユーザ情報をクライアントに提供するクラス
 * @author masato
 * @version 1.2
 * @作成日: 2008/10/15
 * @最終更新日:2008/10/31
 */
public class GetLoginUsrInfoProcess {
	
	/**
	 * コンストラクタ
	 * @param soc ソケット
	 * @param ois　インプットストリーム
	 * @param oos　アウトプットストリーム
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public GetLoginUsrInfoProcess(Socket soc,ObjectInputStream ois,ObjectOutputStream oos) throws IOException, ClassNotFoundException
	{
		ois.readObject();     //クライアントからユーザIDを受け取る
		//認証を後まわし
		oos.writeObject("OK");	                  //クライアントにログイン認証サインを送る
		DynamicPeerInformation dyinfo = new DynamicPeerInformation();
		int num = dyinfo.getPeerNum();
		String[] names = new String[num];         //ユーザ名取得(DynamicUserInformation.xmlから)
		names = dyinfo.getPeerName();
		oos.writeObject(names);	                  //クライアントにユーザ名を通知する
	}
}
