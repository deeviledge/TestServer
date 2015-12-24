package sglclient.groupadmin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MakeGroupClient {
	
	//定数宣言
	public static final int INPUT_STREAM_BUFFER = 512;
	public static final int FILE_WRITE_BUFFER = 512;
		
		
	//グローバル変数極力減らしたい
	static String command;	//受け付けたコマンドを格納
	static OutputStream outStream;	//送信用ストリーム	
	static InputStream inStream;	//受信用ストリーム 
	static String[] getArgs; //区切ったあとのコマンド入力を格納
	FileOutputStream fileOutStream;
	FileInputStream fileInStream;	
	byte[] fileBuff = new byte[FILE_WRITE_BUFFER];	    //サーバからのファイル出力を受け取る
	int recvFileSize;	    //InputStreamから受け取ったファイルのサイズ
	int waitCount = 0;      //タイムアウト用
	static Socket sock;
        //public boolean permission;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	@SuppressWarnings("rawtypes")
	public MakeGroupClient(String IPAdress,int serverport ,String groupname,ArrayList users) throws IOException, InterruptedException{
		try
		{
                        //permission = false;
			sock = new Socket( IPAdress , serverport );
		
			//ソケットが生成できたらストリームを開く
			outStream =  sock.getOutputStream();
			inStream  =  sock.getInputStream();
			
			oos = new ObjectOutputStream(sock.getOutputStream());
			ois = new ObjectInputStream(sock.getInputStream());
			//グループ作成の要求
			oos.writeObject("makegroup"); 
			
			//グループ名とメンバーを送信
			oos.writeObject(groupname); 
			oos.writeObject(users);
                        //if(ois.readObject().equals("OK"))permission=true;
                        while(!ois.readObject().equals("Finish")){}
			
		}
		catch(Exception e)
		{
			//例外表示
			System.err.println(e);
			exit();
		}
	}
	
	
	
	public void exit() throws IOException, InterruptedException{
		//ストリームのクローズ	
		outStream.close();
		inStream.close();
		sock.close();
	}
}
