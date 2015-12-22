package sglserver.servermain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import sglserver.dynamicpeerinformation.DynamicPeerInformation;
import sglserver.groupinformation.ReadGroupInfoXML;


/**
 * グループのグループメンバとログイン状況とIPをクライアントに提供するクラス
 * @author masato
 * @version 1.2
 * @作成日: 2008/10/15
 * @最終更新日:2008/10/31
 */
public class GetGrpInfoProcess {
	
	/**
	 * コンストラクタ
	 * @param soc ソケット
	 * @param ois　インプットストリーム
	 * @param oos　アウトプットストリーム
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public GetGrpInfoProcess(Socket soc,ObjectInputStream ois,ObjectOutputStream oos) throws IOException, ClassNotFoundException
	{
		ois.readObject();     //ユーザIDを受け取る(認証)
		String groupname = (String)ois.readObject();     //グループ名を受け取る
		ReadGroupInfoXML rgi = new ReadGroupInfoXML(groupname);
		DynamicPeerInformation pdi = new DynamicPeerInformation();
		int value = rgi.getGroupValue();         //グループメンバ数を取得
		String[][] groupmember = null;
		groupmember = new String[value][3];
		for(int i=0;i<value;i++){
			groupmember[i][0] = rgi.getGroupMember(i);                         //ユーザ名
			groupmember[i][1] = pdi.getLoginInformation(groupmember[i][0]);             //ログイン情報
			if(groupmember[i][1] != null)
				groupmember[i][2] = pdi.getIP(groupmember[i][0]);                       //IP
			else{
				groupmember[i][2] = null; 
			}
		}
		oos.writeObject(groupmember);                //グループメンバ送信
	}
}
