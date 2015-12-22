package sglserver.servermain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;

import sglserver.deletegroupinformation.DeleteGroupInfoXml;
import sglserver.dynamicpeerinformation.DynamicPeerInformationAdministrator;

import sglserver.peerbasicinformation.PeerBasicInformationEdit;

/**
 * SGLユーザのログインを実行するクラス
 * @author masato
 * @version 1.2
 * @作成日: 2008/10/15
 * @最終更新日:2008/10/31
 */
public class LoginProcess extends Thread {
	
	private BigInteger premastersecret;
	
	/**
	 * コンストラクタ
	 * @param soc  ソケット
	 * @param ois  インプットストリーム
	 * @param oos  アウトプットストリーム
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 * @throws ClassNotFoundException
	 */
	public LoginProcess(Socket soc,ObjectInputStream ois,ObjectOutputStream oos,String storePasswd,String keyPasswd) 
	throws IOException, ParseException, ClassNotFoundException 
	{
		//クライアントからユーザIDを受け取る
		String id = (String)ois.readObject();
		//System.out.println("id="+id);
		//ユーザIDからユーザ名を読み込む（PeerBasicInformation.xmlから）
		PeerBasicInformationEdit pbie = new PeerBasicInformationEdit(); 
		String uname =pbie.getPeerName(id);       
		//クライアントにログイン認証サインを送る
		oos.writeObject("OK");
		// ユーザ削除
		DeleteGroupInfoXml dgix = new DeleteGroupInfoXml();
		dgix.DeleteGroup(id,oos); // 削除
		//IPの更新(後回し)
		InetAddress inet = InetAddress.getLocalHost();// InetAddressクラスの取得(自分のIP取得)
		String remoteip = inet.getHostAddress();            //remoteIPの取得
		//DynamicUserInformation.xmlに書き込む（ユーザ名、ID、IP）
		DynamicPeerInformationAdministrator.addEntry(soc,id,uname,remoteip);
	}
}
