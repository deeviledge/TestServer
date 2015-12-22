/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sglserver.servermain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import sglserver.dynamicpeerinformation.DynamicPeerInformation;

/**
 *
 * @author nishimura
 */
public class GetLoginUsrInfoProcess2 {
    /**
	 * コンストラクタ
	 * @param soc ソケット
	 * @param ois　インプットストリーム
	 * @param oos　アウトプットストリーム
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public GetLoginUsrInfoProcess2(Socket soc,ObjectInputStream ois,ObjectOutputStream oos) throws IOException, ClassNotFoundException
	{
		ois.readObject();     //クライアントからユーザIDを受け取る
		//認証を後まわし
		oos.writeObject("OK");	                  //クライアントにログイン認証サインを送る
		DynamicPeerInformation dyinfo = new DynamicPeerInformation();
		int num = dyinfo.getPeerNum();
                String[] ids = new String[num];
                ids = dyinfo.getPeerIP();
		
		oos.writeObject(ids);	                  //クライアントにユーザ名を通知する
	}
}
