package sglclient.certificate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;

import sglclient.groupsigniture.Sign;
import sglclient.myinformation.MyInformation;
import sglclient.option.EditOptionXml;

/**
 * SGLサーバへ様々な要求を出すクラス
 * @auhor masato,kryuu
 * @version 1.3 
 * @作成日: 2007/7/10
 * @最終更新日:2008/11/13
 */
public class SglCall {
	String sglserverip = new EditOptionXml().getIP();
	String[] names = null;
	String[] gnames = null;
	//String[] groupname = null;
	static ArrayList gnames2 = new ArrayList();
	static Socket soc = null;
	static ObjectOutputStream oos = null;
	static ObjectInputStream ois = null;
	public static final int Normal = 1;
	public static final int Anti = 2;
	static String SGL_Call_Message_GetLoginUser = "getuser";
        static String SGL_Call_Message_GetLoginUser2 = "getuser2";
	static String SGL_Call_Message_GetUser = "user";
	static String SGL_Call_Message_GetGroupInformation = "getgroup";
	static String SGL_Call_Message_GetGroupInformation2 = "getgroup2";
	static String SGL_Call_Message_MakeGroup = "makegroup";
	static String SGL_Call_Message_EditGroup = "editgroup";
	static String SGL_Call_Message_DeleteGroup = "deletegroup";
	static String SGL_Call_Message_IncludeGroup = "Includegroup";
	static String SGL_Call_Message_IncludeGroup2 = "Includegroup2";
        static String SGL_Call_Message_CheckDelete = "check";
	
	/**
	 * コンストラクタ(SGLサーバに接続を開始)
	 *
	 */
	public SglCall() {
		try {
			soc = new Socket(sglserverip,12345);        //ソケットを生成する
			// 入出力ストリームを取得する
			oos = new ObjectOutputStream(soc.getOutputStream());
			ois = new ObjectInputStream(soc.getInputStream());	
		}catch(Exception e){}
	}
	
	/**
	 * SGLサーバからSGLサーバにログイン中のユーザ名を受信する
	 * @return String[] ユーザ名
	 * @author masato
	 */
	public String[] getLoginUserName(){
		try{	
			//ユーザ情報取得要求
			oos.writeObject(SGL_Call_Message_GetLoginUser);
			//ユーザIDを読み込む
			MyInformation mi = new MyInformation();
			String myid = mi.getUsrID();
			//ユーザIDを送信
			oos.writeObject(myid);
			String result = (String)ois.readObject();   //了解サイン(OK)を受信
			if(result.equals("OK")){
				names = (String[])ois.readObject();   
			}
			else{
				System.out.println("ユーザ名を受信できませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(IOException | ClassNotFoundException e){}
		return names;
	}
        
        /**
	 * SGLサーバからSGLサーバにログイン中のユーザIDを受信する
	 * @return String[] ユーザ名
	 * @author masato
	 */
	public String[] getLoginUserID(){
                String[] IDs=null;
		try{	
			//ユーザ情報取得要求
			oos.writeObject(SGL_Call_Message_GetLoginUser2);
			//ユーザIDを読み込む
			MyInformation mi = new MyInformation();
			String myid = mi.getUsrID();
			//ユーザIDを送信
			oos.writeObject(myid);
			String result = (String)ois.readObject();   //了解サイン(OK)を受信
			if(result.equals("OK")){
				IDs = (String[])ois.readObject();   
			}
			else{
				System.out.println("ユーザIDを受信できませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(IOException | ClassNotFoundException e){}
		return IDs;
	}
	
	/**
	 * SGLサーバからSGLサーバにログイン中のユーザがユーザ登録＆ログインしているか確認する
	 * @param uname ユーザ名
	 * @return String　ユーザ登録していなければnull
	 * 				  ログインしていればON
	 * 			      ログインしていなければOFF
	 * @author masato
	 */
	public String getUser(String uname){
		String res = null;
		try{	
			//ユーザ情報取得要求
			oos.writeObject(SGL_Call_Message_GetUser);
			//ユーザIDを読み込む
			MyInformation mi = new MyInformation();
			String myid = mi.getUsrID();
			//ユーザIDを送信
			oos.writeObject(myid);
			oos.writeObject(uname);
			String result = (String)ois.readObject();   //了解サイン(OK)を受信
			if(result.equals("OK")){
				res = (String)ois.readObject();   
			}
			else{
				System.out.println("受信できませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(IOException | ClassNotFoundException e){}
		return res;
	}
	
	/**
	 * SGLサーバから自身が所属しているグループメンバのユーザ名をを受信する
	 * @param gname　自身が所属しているグループ名
	 * @return　グループに所属しているすべてのユーザ名
	 * @author masato 2008/11/07
	 */
	public ArrayList getGroupInformation(String gname){
		try{
			//グループ情報取得要求
			oos.writeObject(SGL_Call_Message_GetGroupInformation);    
			//了解サイン(OK)を受け取る
			String result = (String)ois.readObject();   
			if(result.equals("OK")){
				//System.out.println("グループ情報を通知しています");
				//自身が所属しているグループ名を送信
				oos.writeObject(gname);
				//そのグループのユーザ名を受信
				gnames2 = (ArrayList)ois.readObject();
			}
			else{
				System.out.println("グループ情報を受信できませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(IOException | ClassNotFoundException e){}
		return gnames2;
	}
	
	/**
	 * SGLサーバにグループ作成(新規登録)を申請する
	 * @String groupname グループ名
	 * @ArrayList userlist　グループメンバリスト
	 * @int order　オプション設定
	 * @author masato
	 */
	public void groupInfoNotificate(String gname, ArrayList userlist, String order,String entry, String tname,String uid){
		try{
			BigInteger[] sign = null;
			System.out.println("編集前のグループ="+userlist);
			//グループ作成要求
			oos.writeObject(SGL_Call_Message_MakeGroup);  
			//了解サイン(OK)を受け取る
			String result = (String)ois.readObject(); 
			System.out.println("tname="+tname);
			if(tname.equals("グループを関連付けしない")){
				//署名
			}else{
				//グループ署名Signを実行
				Sign s = new Sign(tname,SGL_Call_Message_EditGroup);
				sign =  s.sign();
			}
			if(result.equals("OK")){
				System.out.println("グループ情報を通知しています");
				oos.writeObject(uid); 
				oos.writeObject(gname); 
				oos.writeObject(userlist); 
				oos.writeObject(order);
				oos.writeObject(entry); 
				oos.writeObject(tname);
				oos.writeObject(sign);
			}
			else{
				System.out.println("グループ作成許可が出ませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(Exception e){}
	}
	
	/**
	 * SGLサーバにグループ編集を申請する
	 * @String groupname グループ名
	 * @ArrayList userList グループメンバ
	 * @ArrayList userlist　以前のグループメンバ
	 * @int order　オプション設定
	 * @author kiryuu
	 */
	public void groupInfoNotificate2(String gname,ArrayList userlist ,ArrayList beforeuserlist, String option){
		try{
			
			System.out.println("編集前のグループ="+userlist);
			//グループ編集要求
			oos.writeObject(SGL_Call_Message_EditGroup);     
			//了解サイン(OK)を受け取る
			String result = (String)ois.readObject();
			//ユーザIDを読み込む
			MyInformation mi = new MyInformation();
			String myid = mi.getUsrID();
			if(result.equals("OK")){
				System.out.println("グループ情報を通知しています");
				oos.writeObject(myid);
				oos.writeObject(gname); 
				oos.writeObject(beforeuserlist);
				oos.writeObject(userlist); 
				oos.writeObject(option);
			}
			else{
				System.out.println("グループ編集許可が出ませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(IOException | ClassNotFoundException e){}
	}
	
	/**
	 * SGLサーバにグループ編集を申請する(グループ認証バージョン)
	 * @String groupname グループ名
	 * @ArrayList userList グループメンバ
	 * @ArrayList userlist　以前のグループメンバ
	 * @int order　オプション設定
	 * @param gsjadge 認証設定
	 * @author masasto
	 */
	public void groupInfoNotificate2(String gname,ArrayList userlist, 
			ArrayList afteruserlist ,ArrayList dlist, String option,int gsjadge,String uid,String tname){
		try{
			//System.out.println("グループ名="+groupname);
			//System.out.println("編集前のグループ="+userlist);
			//System.out.println("編集後のグループ="+afteruserlist);
			//System.out.println("削除するメンバリスト="+dlist);
			
			//Signを実行
			Sign s = new Sign(gname,SGL_Call_Message_EditGroup);
			BigInteger[] sign =  s.sign();
			//認証設定をStringに変更
			String gsjadge2 = String.valueOf(gsjadge);	
			//グループ編集要求
			oos.writeObject(SGL_Call_Message_EditGroup);     
			//了解サイン(OK)を受け取る
			String result = (String)ois.readObject();   
			if(result.equals("OK")){
				System.out.println("グループ情報を通知しています");
				oos.writeObject(sign);
				oos.writeObject(gname); 
				oos.writeObject(userlist);
				oos.writeObject(afteruserlist);
				oos.writeObject(dlist);
				oos.writeObject(option);
				oos.writeObject(gsjadge2);
				oos.writeObject(uid);
				oos.writeObject(tname);
			}
			else{
				System.out.println("グループ編集許可が出ませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(Exception e){}
	}
	
	/**
	 * SGLサーバにグループ削除を申請する
	 * @String groupname グループ名
	 * @String uID ユーザID
	 * @author kiryuu,masato 2008/11/7
	 */
	public void groupDeleteNotificate(String gname,String uID){
		try{
			//Signを実行
			//Sign s = new Sign(gname,SGL_Call_Message_DeleteGroup);
			//BigInteger[] sign =  s.sign();
			//グループ削除要求
			oos.writeObject(SGL_Call_Message_DeleteGroup);     
			//了解サイン(OK)を受け取る
			String result = (String)ois.readObject();   
			if(result.equals("OK")){
				System.out.println("グループ名を通知しています");
				//oos.writeObject(sign); 
				oos.writeObject(gname); 
				oos.writeObject(uID);
				//String res = (String)ois.readObject();
				if(ois.readObject().equals("finish")){
					//Myinformation.xmlのgroupタグを削除
					new MyInformation().removeGroupName(gname);
				}
				
				
				
			}
			else{
				//System.out.println("グループ削除許可が出ませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(IOException | ClassNotFoundException e){}	
	}
	
        /**
	 * SGLサーバに削除されたグループを確認する
	 * @String groupname グループ名
	 * @String uID ユーザID
	 * @author kiryuu,masato 2008/11/7
	 */
	public void checkDeleteGroup(){
            try{
		//Signを実行
		//Sign s = new Sign(gname,SGL_Call_Message_DeleteGroup);
		//BigInteger[] sign =  s.sign();
		//グループ削除要求
                MyInformation MI = new MyInformation();
                String uID = MI.getUsrID();
		oos.writeObject(SGL_Call_Message_CheckDelete);
                oos.writeObject(uID);
		//了解サイン(OK)を受け取る
		String result = (String)ois.readObject();   
		if(result.equals("OK")){
                    DeleDteGroup(ois);
                }
                else{  
                    System.out.println("グループ削除許可が出ませんでした");
                }
		oos.close();
		ois.close();      //ストリーム・ソケットを閉じる
		soc.close();
            }catch(IOException | ClassNotFoundException e){}	
	}
        
        /**
	 * グループ削除(他のユーザがグループを削除した場合)
	 * @param result
	 * @param ois
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @author kiryuu
	 */
	private void DeleDteGroup(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		//GenerateXML gex = new GenerateXML();
		MyInformation MI = new MyInformation();
	
		String result = (String)ois.readObject();//受信
		if(result.substring(0, result.indexOf(":")).equals("delete")){
			while(true){ // 探索
				result = result.substring(result.indexOf(":"));
				if(result.equals(":")==false){
					result = result.substring(1);
					String deletegroupname = result.substring( 0, result.indexOf(":") );// メッセージの抽出
					System.out.println("グループ" + deletegroupname + "を削除しました。");
					MI.removeGroupName(deletegroupname); // タグ削除
				}else{
					System.out.println("グループ削除終了");
					break;
				}
			}
		}else{
			System.out.println("削除の必要はありません");
		}
	}
        
	/**
	 * SGLサーバから指定したグループの親グループ名を受け取る
	 * @param gname　指定グループ名
	 * @return　指定グループの親グループリスト
	 * @author kawata 2008/12/01
	 */
	public ArrayList getIncludeGroupInfo(String gname){
		try{
			//親グループ情報取得要求
			oos.writeObject(SGL_Call_Message_IncludeGroup);    
			//了解サイン(OK)を受け取る
			String result = (String)ois.readObject();
			if(result.equals("OK")){
				//System.out.println("親グループ情報を通知しています");
				//指定されたグループ名を送信
				oos.writeObject(gname);
				//そのグループの親グループリストを受信
				gnames2 = (ArrayList)ois.readObject();
			}
			else{
				System.out.println("グループ情報を受信できませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(IOException | ClassNotFoundException e){}
		return gnames2;
	}
	
	/**
	 * SGLサーバから指定したグループの子グループ名を受け取る
	 * @param gname　指定したグループ名
	 * @return　指定グループの子グループリスト
	 * @author kawata 2008/12/01
	 */
	public ArrayList getIncludeGroupInfo2(String gname){
		try{
			//子グループ情報取得要求
			oos.writeObject(SGL_Call_Message_IncludeGroup2);    
			//了解サイン(OK)を受け取る
			String result = (String)ois.readObject();
			if(result.equals("OK")){
				//System.out.println("子グループ情報を通知しています");
				//指定されたグループ名を送信
				oos.writeObject(gname);
				//そのグループの子グループリストを受信
				gnames2 = (ArrayList)ois.readObject();
			}
			else{
				System.out.println("グループ情報を受信できませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(IOException | ClassNotFoundException e){}
		return gnames2;
	}
	
	/**
	 * SGLサーバから自身が所属しているグループメンバのユーザ名を受信する
	 * (grouplinkのメンバは受信しない)
	 * 
	 * @param gname　自身が所属しているグループ名
	 * @return　グループに所属している(gropulink以外の)ユーザ名
	 * @author kawata 2008/12/01
	 */
	public ArrayList getGroupInformation2(String gname){
		try{
			//グループ情報取得要求
			oos.writeObject(SGL_Call_Message_GetGroupInformation2);    
			//了解サイン(OK)を受け取る
			String result = (String)ois.readObject();
			if(result.equals("OK")){
				//System.out.println("グループ情報を通知しています");
				//自身が所属しているグループ名を送信
				oos.writeObject(gname);
				//そのグループの(grouplink以外の)ユーザ名を受信
				gnames2 = (ArrayList)ois.readObject();
			}
			else{
				System.out.println("グループ情報を受信できませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(IOException | ClassNotFoundException e){}
		return gnames2;
	}
}
