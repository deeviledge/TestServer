package sglclient.groupadmin;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import sglclient.myinformation.MyInformation;
import sglclient.option.EditOptionXml;



/**
 * 作成日: 2007/7/10
 * @auhor masato
 * @version 1.0 
 */

/**
 * SGLサーバへグループ作成要求を出す
 */

public class MakeGroupInformation {
	
	String sglserverip = new EditOptionXml().getIP();
	
	String[] names = null;
	String[] gnames = null;
	String[] groupname = null;
	static Socket soc = null;
	static ObjectOutputStream oos = null;
	static ObjectInputStream ois = null;

	public static final int Normal = 1;
	public static final int Anti = 2;
	
	public MakeGroupInformation() {
		
		try {
			soc = new Socket(sglserverip,12345);        //ソケットを生成する
			//System.out.println(sglserverip);
			// 入出力ストリームを取得する
			oos = new ObjectOutputStream(soc.getOutputStream());
			ois = new ObjectInputStream(soc.getInputStream());
				
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * SGLサーバからSGLサーバにログインしているユーザの名前を受け取る
	 * @return
	 */
	public String[] getLoginUser(){
		try{
			
			
			oos.writeObject("getuser");
			MyInformation mi = new MyInformation();
			String myid = mi.getUsrID();
			oos.writeObject(myid);
			//認証が必要だが後回し
			String result = (String)ois.readObject();   //認証サイン(OK)を受け取る
			if(result.equals("OK")){
				names = (String[])ois.readObject();   
			}
			else{
				System.out.println("認証できませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return names;
	}
	
	/**
	 * SGLサーバにグループ登録したいグループの情報を通知する
	 *
	 */
	public void groupInfoNotificate(String groupname, ArrayList userlist, int order){
		try{
			soc = new Socket(sglserverip,12345);        //ソケットを生成する
			//System.out.println(sglserverip);
			// 入出力ストリームを取得する
			oos = new ObjectOutputStream(soc.getOutputStream());
			ois = new ObjectInputStream(soc.getInputStream());
			
			String order2 = String.valueOf(order);
			oos.writeObject("makegroup");     //グループ作成要求
			String result = (String)ois.readObject();   //認証サイン(OK)を受け取る
			if(result.equals("OK")){
				System.out.println("グループ情報を通知しています");
				oos.writeObject(groupname); 
				oos.writeObject(userlist); 
				oos.writeObject(order2);
			}
			else{
				System.out.println("グループ作成許可が出ませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	//XML上でグループメンバの追加をする
	public void groupInfoNotificateadd(String groupname, ArrayList userlist, int order){
		try{
			String order2 = String.valueOf(order);
			oos.writeObject("makeaddgroup");     //グループ作成要求
			String result = (String)ois.readObject();   //認証サイン(OK)を受け取る
			if(result.equals("OK")){
				System.out.println("グループ情報を通知しています");
				oos.writeObject(groupname); 
				oos.writeObject(userlist); 
				oos.writeObject(order2);
			}
			else{
				System.out.println("グループ作成許可が出ませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	//グループ名を読み込みグループメンバの名前を呼び出してくる
	public String[] editgroup(String groupname1){
		try{
			//String order2 = String.valueOf(order);
			oos.writeObject("editgroup");     //グループ作成要求
			String result = (String)ois.readObject();   //認証サイン(OK)を受け取る
			if(result.equals("OK")){
				System.out.println("グループ情報を通知しています");
				oos.writeObject(groupname1); 
				gnames = (String[])ois.readObject();;

			}
			else{
				System.out.println("グループ作成許可が出ませんでした");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return gnames;
	}
	
	
	
}
