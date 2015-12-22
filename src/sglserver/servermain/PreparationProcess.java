package sglserver.servermain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import sglserver.keystore.GenerateKeyStore;
import sglserver.myinformation.FirstMyInformation;
import sglserver.peerbasicinformation.PeerBasicInformationEdit;

public class PreparationProcess  extends Thread {
	/************************定数宣言***********************/
	public static final int INPUT_STREAM_BUFFER = 512;	//入力ストリーム格納バッファサイズ
	public static final int FILE_READ_BUFFER = 512;	//ファイル読み込みバッファサイズ
	
	static String userID = "0000";	//Serverが決定したIDを格納
									//とりあえず"0000"で初期化しておく
	private Socket socket;
	static OutputStream outStream;	//送信用ストリーム
	static InputStream inStream;	//受信用ストリーム
	private GenerateKeyStore store;
	

	
	
	public PreparationProcess(Socket soc){
		
		System.out.println("事前準備を行います");
		
		//PeerBasicInformation.xmlとサーバのキーストアを作成
		//new PreparationServer(); 
		
		try {
			this.socket = soc;
			System.out.println(socket.getRemoteSocketAddress() +"と接続");
			
			//入出力ストリーム設定
			outStream =  socket.getOutputStream();
			inStream =  socket.getInputStream();
		} catch (IOException e) {
		}
		
		FileInputStream fileInStream;
		FileOutputStream fileOutStream;
		byte[] inputBuff = new byte[INPUT_STREAM_BUFFER];	//クライアントからのコマンド入力を受け取る
		byte[] fileBuff = new byte[FILE_READ_BUFFER];	//ファイルから読み込むバッファ
		int waitCount;      //タイムアウト用
		int recvByteLength;
		int recvFileSize;	    //InputStreamから受け取ったファイルのサイズ
		
		
		try
		{
			//コマンドの入力があった場合に行う処理(1文字以上読みこんだとする)
			while( (recvByteLength = inStream.read(inputBuff))  != -1 )
			{
				//受け取ったbyte列をStringに変換(構文解析のため)
				String buff = new String(inputBuff , 0 , recvByteLength);
 
				//スペースで区切り格納し直す(sscanfのような)
				String[] getArgs = buff.split("\\s");
 
 
				/************************コマンド解析*********************/
				
 
				//サーバー証明書をクライアントに送る
				if( getArgs[0].equals("getCer") )
				{				
					//受け取ったファイル名のファイルを読み込むストリーム作成	
					fileInStream = new FileInputStream("src/sglserver/conf/key/ca/ca.cer");
					int fileLength = 0;
					//System.out.println("Create stream " + getArgs[1] );
 
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
				
				//サーバーからユーザーの証明書を受け取る
				//キースストアに格納
				if(getArgs[0].equals("SendCer")){
					//引数で指定されたファイルを保存するためのストリーム
					fileOutStream = new FileOutputStream( "src/sglserver/conf/usr/ca/"+getArgs[1]+".cer" );
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
					System.out.println( "Download "+ getArgs[1]+".cer"  + " done" );
					
					
					store = new GenerateKeyStore();
					store.ImportKeyStore(getArgs[1]);			//ユーザの証明書をすべて保存
					//GenerateKeyStore store2 = new GenerateKeyStore();
					//store2.ImportKeyStore();
					
				}
				
				//MyInformationを作成してクライアントに送る
				//同時にPeerInformationにユーザー情報を追加
				if(getArgs[0].equals("getInfo")){
					
					//PeerInformationにユーザを追加
					AddUser(getArgs[1],getArgs[2]);
					
					//MyInformationを作成
					FirstMyInformation gexf = new FirstMyInformation(userID);
					gexf.addUser(userID ,getArgs[1], getArgs[2]);
					try{
						gexf.saveFile(); // 保存
						System.out.println("ID"+userID+"MyInformationを作成しました");
					}catch(Exception e1){
						System.err.println(e1);
					}
					
					//作成したmyinformationを読み込むストリーム作成	
					fileInStream = new FileInputStream("src/sglserver/conf/usr/xml_files/MyInformation/MyInformation_"+userID+".xml");
					int fileLength;
					//System.out.println("Create stream " + getArgs[1] );
 
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
			}
		}
		//例外処理
		catch( IOException | InterruptedException e )
		{
		}finally{
			try{
				if( socket != null ){
					socket.close();
				}
			}catch(Exception e){
			}
			System.out.println(socket.getRemoteSocketAddress() +"との接続を切断");
			System.out.println("事前準備を終了します");
		}
		
	}
	
        
        //ユーザを追加するメソッド
	public static  void AddUser(String userName,String mailAdress){
		PeerBasicInformationEdit pbie = new PeerBasicInformationEdit();
		String IDs[] = pbie.getPeerID();		//全ユーザーIDを取得
		int i=0;
		int IDs_int[] = new int[IDs.length];	//取得したIDを数値で格納する
		int id = 0,cmp;						//新規ユーザーの決定したＩＤを格納する
		boolean flg=false;						//空いている最小のＩＤを見つけたらtrueにする
		
		//空いている最小のＩＤを探す
		while(i<IDs.length){
			IDs_int[i] = Integer.parseInt(IDs[i]);
			if(i>0 && flg==false){
				cmp = IDs_int[i]-IDs_int[i-1];
				if(cmp!=1){
					id = IDs_int[i] - (cmp-1);
					flg=true;
				}
			}
			i++;
		}
                
		if(IDs.length==1)id=1;	//サーバしか登録されてなければ、0001を割り当てる
		else if(flg==false)id=IDs.length;	//隙間がなければ一番後ろに割り当てる
		
		userID = "000"+id;	
		userID = userID.substring(userID.length()-4,userID.length());//４桁の文字列に変換
		
		//PeerBasicInformationにユーザーを追加する
		String certification = "src/sglserver/conf/usr/ca/"+userName+".cer";
		pbie.appendUser(userID, userName, mailAdress, certification);
                //↑PeerBasicInformationEdit.java内の３０２行目付近のappendUserメソッドを利用
		
                try{
			pbie.saveFile(); // 保存
		}catch(Exception e1){
			System.err.println(e1);
		}
	}
}
