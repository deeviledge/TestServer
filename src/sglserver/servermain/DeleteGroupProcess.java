package sglserver.servermain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import sglserver.deletegroupinformation.DeleteGroupInfoXml;
import sglserver.groupinformation.GroupInformationXml;
import sglserver.groupinformation.ReadGroupInfoXML;

/**
 * グループ削除を実行するクラス
 * @author masato,nishimura
 * @version 1.1
 * @作成日: 2008/11/07
 * @最終更新日:2008/11/17
 */
public class DeleteGroupProcess {
	
	//static String SGL_Accept_Message_DeleteGroup = "deletegroup";
	boolean group_exist;
	
	/**
	 * コンストラクタ
	 * @param soc  ソケット
	 * @param ois  インプットストリーム
	 * @param oos  アウトプットストリーム
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws Exception 
	 */
	public DeleteGroupProcess(Socket soc,ObjectInputStream ois,ObjectOutputStream oos) 
	throws Exception 
	{
		oos.writeObject("OK");
		//グループ署名を受け取る
		//BigInteger[] sign = (BigInteger[])ois.readObject();
		//グループ名を受け取る
		String gname = (String)ois.readObject();   
		//ユーザIDを受け取る
		String uID = (String)ois.readObject();  
		
		//OPENを実行しグループリーダかどうかを確認
		//Open open = new Open(gname,sign,SGL_Accept_Message_DeleteGroup); 
		//String op = open.open();
		//System.out.println("開示完了！署名者は"+op+"です");
		//グループリーダ名を取得
		
		ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
		ArrayList memberID =rgi.getGroupMember4(gname);
		String gID = rgi.getGroupID();
		
		group_exist = false;
		
		
		for(int i=0;i<memberID.size();i++){
			if(memberID.get(i).equals(uID)){
				//選択されたグループのメンバ全員のユーザIDを取得 
				
				
				System.out.println("DeleteGroup.xmlにグループを登録********************");
				
				//DeleteGroup.xmlに削除するグループメンバのユーザIDを保存
				DeleteGroupInfoXml dgi = new DeleteGroupInfoXml();
				dgi.makegroup2(gname, memberID);
			    dgi.saveFile();
			    
			    System.out.println("申請したユーザを削除********************");
				
				//DeleteGroup.xmlから申請してきたグループメンバのユーザIDを削除
				dgi.deleteuserid(gname, uID);
				dgi.saveFile();
				oos.writeObject("finish");
				System.out.println("グループ削除手続き完了********************");
				
				
				GroupInformationXml	ginfo = new GroupInformationXml();
				//GroupInformation.xmlを編集
				ginfo.DeleteGroup2(gID);
				ginfo.saveFile();
				group_exist = true;
				System.out.println("グループを削除しました***********************");
				break;
			}
		}
		
		
		
			
			
	}
		
	
}
