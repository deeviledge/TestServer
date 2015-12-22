package sglserver.servermain;

import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

import sglserver.groupinformation.ReadGroupInfoXML;
import sglserver.pskey.*;


/**
 * SGLクライアントの要求に従って実行内容を分岐させるクラス
 * @author masato,kiryuu,kinbara
 * @version4.3
 * @作成日: 2007/2/28
 * @最終更新日:2008/11/13
 */
public class ServerConnect extends Thread {
	
	private Socket soc = null;	//Socketの生成
	private final String storePasswd; //キーストアのストアパス
	private final String keyPasswd; // キーストアのキーパス
	final String SGL_Accept_Message_Login = "login";
	final String SGL_Accept_Message_Logout = "logout";
	final String SGL_Accept_Message_Loginuser = "getuser";
        final String SGL_Accept_Message_Loginuser2 = "getuser2";
	final String SGL_Accept_Message_User = "user";
	final String SGL_Accept_Message_GroupInformation = "getgroup";
	final String SGL_Accept_Message_GroupInfoemation2 = "groupmember";
	final String SGL_Accept_Message_GroupInfoemation3 = "Includegroup";
	final String SGL_Accept_Message_GroupInfoemation4 = "Includegroup2";
	final String SGL_Accept_Message_GroupInformation5 = "getgroup2";
	final String SGL_Accept_Message_MakeGroup = "makegroup";
	final String SGL_Accept_Message_EditGroup = "editgroup";
	final String SGL_Accept_Message_DeleteGroup = "deletegroup";
	final String SGL_Accept_Message_Entry = "entry";
	final String SGL_Accept_Message_Preparation = "preparation";
	final String SGL_Accept_Message_Certification = "cert";
        final String SGL_Accept_Message_CheckDelete = "check";
	
	
	/**
	 * コンストラクタ
	 * @param soc　ソケット
	 */
	public ServerConnect(Socket soc, String storepass, String keypass)
	{
		this.soc = soc;
		storePasswd = storepass;
		keyPasswd 	= keypass;
		this.start();    // スレッドを開始する
	}
	
	/**
	 * CAの提供するサービス
	 */
	public void run()
	{
		try {		
			// 入出力ストリームを取得する
			ObjectInputStream ois = new ObjectInputStream(soc.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(soc.getOutputStream());
			String req = (String)ois.readObject();     //要求内容を受け取る		
			
			
			
			/**
			 * 要求内容が事前準備（初回ログイン）である場合
			 * @author nishimrua
			 */
			if(req.equals(SGL_Accept_Message_Preparation)){
				new PreparationProcess(soc);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}	
			/**
			 * 要求がユーザー認証である場合
			 * @author nishimura
			 */
			if(req.equals(SGL_Accept_Message_Certification)){
				new PSKeySendServerThread(soc, storePasswd, keyPasswd);
                                new LoginProcess(soc, ois, oos,storePasswd,keyPasswd);
				//System.out.println("end?");
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}
			
			/**
			 * 要求内容がログインである場合
			 * @author masato
			 *  (もういらないかも)　
			 *  @author nishimura
			 */
			if(req.equals(SGL_Accept_Message_Login)){
				new LoginProcess(soc, ois, oos,storePasswd,keyPasswd);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}	
			
			/**
			 * 要求内容がログアウトである場合
			 * @author masato
			 */
			else if(req.equals(SGL_Accept_Message_Logout)){
				new LogoutProcess(soc, ois, oos);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}		
			
			/**
			 * 要求内容がログインユーザ情報の場合
			 * @author masato
			 */
			else if(req.equals(SGL_Accept_Message_Loginuser)){
				new GetLoginUsrInfoProcess(soc, ois, oos);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}
                        /**
                         * 要求内容がログインユーザのIDの場合
                         */
                        else if(req.equals(SGL_Accept_Message_Loginuser2)){
				new GetLoginUsrInfoProcess2(soc, ois, oos);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}
			
			/**
			 * 要求内容がユーザ情報の場合
			 * @author masato
			 */
			else if(req.equals(SGL_Accept_Message_User)){
				new GetUsrInfoProcess(soc, ois, oos);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}
			
			/**
			 * 要求内容がグループ情報の場合
			 * @author masato
			 */
			else if(req.equals(SGL_Accept_Message_GroupInformation)){
				oos.writeObject("OK");
				String gname1 = (String)ois.readObject();   //グループ名を受け取る
				ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname1);
				ArrayList gnames = rgi.getMemberName2(gname1);
				oos.writeObject(gnames);
			}
			
			/**
			 * 要求内容がグループメンバとログイン状況とIPの場合
			 * @author masato
			 * （アプリとの連携）
			 */
			else if(req.equals(SGL_Accept_Message_GroupInfoemation2)){
				new GetGrpInfoProcess(soc, ois, oos);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}	
			
			/**
			 * 要求内容が親グループ名取得の場合
			 * @author kawata
			 */
			else if(req.equals(SGL_Accept_Message_GroupInfoemation3)){
				oos.writeObject("OK");
				String gname1 = (String)ois.readObject();   //グループ名を受け取る
				ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname1);
				ArrayList gnames = rgi.getIncludegroup(gname1);
				oos.writeObject(gnames);
			}	
			
			/**
			 * 要求内容が子グループ名取得の場合
			 * @author kawata
			 */
			else if(req.equals(SGL_Accept_Message_GroupInfoemation4)){
				oos.writeObject("OK");
				String gname1 = (String)ois.readObject();   //グループ名を受け取る
				ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname1);
				ArrayList gnames = rgi.getIncludegroup2(gname1);
				oos.writeObject(gnames);
			}	
			

			
			/**
			 * 要求内容がグループ情報の場合
			 * @author kawata
			 */
			else if(req.equals(SGL_Accept_Message_GroupInformation5)){
				oos.writeObject("OK");
				String gname1 = (String)ois.readObject();   //グループ名を受け取る
				ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname1);
				ArrayList gnames = rgi.getMemberName3(gname1);
				oos.writeObject(gnames);
                                
			}
			
			/**
			 * 要求内容がグループ新規作成の場合
			 * @author masato
			 */
			else if(req.equals(SGL_Accept_Message_MakeGroup)){
				//System.out.println("見えてる？");
				new MakeGroupProcess(soc, ois, oos);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}
			
			/**
			 * 要求内容がグループ編集の場合
			 * @author kiryu,masato
			 */
			else if(req.equals(SGL_Accept_Message_EditGroup)){
				//クライアントにログイン認証サインを送る
				oos.writeObject("OK");
				//グループ署名を受け取る
				BigInteger[] sign = (BigInteger[])ois.readObject();
				//グループ名を受け取る
				String gname = (String)ois.readObject();
				//編集前のグループメンバを受け取る
				ArrayList userlist = (ArrayList)ois.readObject(); 
				//編集後のグループメンバを受け取る
				ArrayList afteruserlist = (ArrayList)ois.readObject(); 
				//削除メンバリストを受け取る
				ArrayList dlist = (ArrayList)ois.readObject(); 	
				// オプション設定を受け取る
				String option = (String)ois.readObject();
				// 認証設定を受け取る
				String gsjudge = (String)ois.readObject();
				int gsjudge2=Integer.valueOf(gsjudge).intValue();
				//ユーザIDを受け取る
				String uid = (String)ois.readObject(); 
				//グループ関係を受け取る
				String tname = (String)ois.readObject();
				if(tname.equals("グループを関連付けしない")){
					tname=null;
				}
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
				new EditGroupProcess(userlist,afteruserlist,dlist,gname,option,gsjudge2,sign,uid,tname);
				
			}
			
			/**
			 * 要求内容がグループ削除の場合
			 * @author masato
			 */
			else if(req.equals(SGL_Accept_Message_DeleteGroup)){
				new DeleteGroupProcess(soc, ois, oos);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}
			
			
                        
                        /**
			 * 要求内容がグループ削除の場合
			 * @author masato
			 */
			else if(req.equals(SGL_Accept_Message_DeleteGroup)){
				new DeleteGroupProcess(soc, ois, oos);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}
                        
                        /**
			 * 要求内容がグループ削除の場合
			 * @author masato
			 */
			else if(req.equals(SGL_Accept_Message_CheckDelete)){
				new CheckDeleteProcess(soc, ois, oos);
				ois.close();
				oos.close();         // ストリーム・ソケットを閉じる
				soc.close();
			}
                        
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * パスワードを設定します。
	 * @author kinbara
	 */
	
}