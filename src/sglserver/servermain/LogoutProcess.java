package sglserver.servermain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import sglserver.dynamicpeerinformation.DynamicPeerInformationAdministrator;

/**
 * SGLユーザのログアウトを実行するクラス
 * @author masato
 * @version 1.2
 * @作成日: 2008/10/15
 * @最終更新日:2008/10/31
 */
public class LogoutProcess {

	/**
	 * コンストラクタ
	 * @param soc  ソケット
	 * @param ois  インプットストリーム
	 * @param oos  アウトプットストリーム
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public LogoutProcess(Socket soc,ObjectInputStream ois,ObjectOutputStream oos) throws IOException, ClassNotFoundException
	{
		String id = (String)ois.readObject();     //クライアントからユーザIDを受け取る
		DynamicPeerInformationAdministrator.removeEntry(id);
	}
}
