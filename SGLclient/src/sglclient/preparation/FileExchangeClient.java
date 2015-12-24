package sglclient.preparation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import sglclient.option.EditOptionXml;

import sglclient.pskey.PSKeyRecieveClient;


public class FileExchangeClient {
	//定数宣言
	public static final int INPUT_STREAM_BUFFER = 512;
	public static final int FILE_WRITE_BUFFER = 512;
	public String serverIP;
	
	
	static String command;	//受け付けたコマンドを格納
	static OutputStream outStream;	//送信用ストリーム	
	static InputStream inStream;	//受信用ストリーム 
	static String[] getArgs; //区切ったあとのコマンド入力を格納
	FileOutputStream fileOutStream;	//ファイルをポートに書き込むためのストリーム
	FileInputStream fileInStream;	//ファイルをポートから読み込むためのストリーム
	byte[] fileBuff = new byte[FILE_WRITE_BUFFER];	    //サーバからのファイル出力を受け取る
	int recvFileSize;	    //InputStreamから受け取ったファイルのサイズ
	int waitCount = 0;      //タイムアウト用
	static Socket sock;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	
	//コンストラクタ
	public FileExchangeClient(String IPAdress,int serverport) throws IOException, InterruptedException{
		socketOpen(IPAdress,serverport);
	}
	
	public void socketOpen(String IPAdress,int serverport) throws IOException, InterruptedException{
		try
		{
			sock = new Socket( IPAdress , serverport );
                        //System.out.println("Test:"+IPAdress);
			//ソケットが生成できたらストリームを開く
			outStream =  sock.getOutputStream();
			inStream  =  sock.getInputStream();
			
			oos = new ObjectOutputStream(sock.getOutputStream());
			ois = new ObjectInputStream(sock.getInputStream());
			//事前準備の要求
			oos.writeObject("preparation"); 
                        
                        
			
		}
		catch(Exception e)
		{
			//例外表示
			System.err.println(e);
			exit();
		}
	}
	
	//サーバーにクライアントの証明書を送る
	public void SendCer(String username) throws IOException{
		//サーバーに証明書を要求する
		String command = "SendCer "+username;
		outStream.write( command.getBytes() , 0 , command.length() );
		
		//ファイルを読み込むストリーム作成	
		fileInStream = new FileInputStream("src/sglclient/conf/usr/ca/"+ username +".cer");
		int fileLength = 0;

		//最大data長まで読み込む(終端に達し読み込むものがないとき-1を返す)
		while( (fileLength = fileInStream.read(fileBuff)) != -1 )
		{
			//出力ストリームに書き込み	
			outStream.write( fileBuff , 0 , fileLength );
		}

		//ファイルの読み込みを終える
		//System.out.println("Close stream " + getArgs[1] );
		fileInStream.close();
	}
	
	//サーバー証明書を取得する
	public void GetCer() throws IOException, InterruptedException{
		
		//サーバーに証明書を要求する
		String command = "getCer test";
		outStream.write( command.getBytes() , 0 , command.length() );
		
		//引数で指定されたファイルを保存するためのストリーム
		fileOutStream = new FileOutputStream( "src/sglclient/conf/key/ca/ca.cer" );
		waitCount = 0;
		
		//ストリームからの入力をファイルとして書き込む
		while( true )
		{
			//ストリームから読み込める時
			if( inStream.available() > 0 )
			{
				//受け取ったbyteをファイルに書き込み
				recvFileSize = inStream.read(fileBuff);
				fileOutStream.write( fileBuff , 0 , recvFileSize );
						
			}
					
			//タイムアウト処理
			else
			{
				waitCount++;
				Thread.sleep(100);
				if (waitCount > 10)break;
			}
		}
				
		//ファイルの書き込みを閉じる
		fileOutStream.close();
				
				
		//書き込み完了表示
		System.out.println( "Download "+ "ca.cer" + " done" );
	}
	
	//サーバーにMyInformationの作成を要求し、取得する
	public void GetMyInformation(String name,String mailAdress) throws IOException, InterruptedException{
		//サーバーに証明書を要求する
		String command = "getInfo "+name+" "+mailAdress;
		outStream.write( command.getBytes() , 0 , command.length() );
		
		//引数で指定されたファイルを保存するためのストリーム
				fileOutStream = new FileOutputStream( "src/sglclient/conf/usr/xml_files/MyInformation.xml" );
				waitCount = 0;
				
				//ストリームからの入力をファイルとして書き込む
				while( true )
				{
					//ストリームから読み込める時
					if( inStream.available() > 0 )
					{
						//受け取ったbyteをファイルに書き込み
						recvFileSize = inStream.read(fileBuff);
						fileOutStream.write( fileBuff , 0 , recvFileSize );
					}
							
					//タイムアウト処理
					else
					{
						waitCount++;
						Thread.sleep(100);
						if (waitCount > 10)break;
					}
				}
						
				//ファイルの書き込みを閉じる
				fileOutStream.close();
						
						
				//書き込み完了表示
				System.out.println( "Download "+ "MyInformation.xml" + " done" );
		
	}
	
	public void PSKReciever(){
		@SuppressWarnings("unused")
                        EditOptionXml eox=new EditOptionXml();
                serverIP=eox.getIP();
		PSKeyRecieveClient prc = new PSKeyRecieveClient(serverIP);
	}
	
	
	
	public void exit() throws IOException, InterruptedException{
		//String command = "getPSK";	//PreSharedKeyを要求
		//outStream.write( command.getBytes() , 0 , command.length() );
		//Thread.sleep(300);	//少し待つ
		//ストリームのクローズ	
		outStream.close();
		inStream.close();
		sock.close();
	}
	

}
