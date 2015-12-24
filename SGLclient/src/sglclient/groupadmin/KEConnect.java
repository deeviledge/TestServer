package sglclient.groupadmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import sglclient.keyexchange.KEClient;
import sglclient.keyexchange.RoundMain;
import sglclient.keyexchange.SaveKey;
import sglclient.myinformation.MyInformation;
import sglclient.option.EditOptionXml;

public class KEConnect extends Thread{
	
	private Socket socket = null;
	String serverip = new EditOptionXml().getIP();
	static ObjectOutputStream oos = null;
	private final String name;
	MyInformation MI;
	Socket informsocket;//交換終了通知メソッドのオブジェクト
	private final int RoundControllPort = 12767;
	
	public KEConnect(Socket socket){
		this.socket = socket;
		MI = new MyInformation();
		name = MI.getUsrName();
		
		this.start();
	}
	
	public void run(){
		
		try {
			String input;
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			input = in.readLine();
			// 鍵交換開始
			if(input.equals("keyexchange")){
				System.out.println("鍵交換を開始します");
				new KEClient(socket,in);	//グループ情報と配送指令書を受け取る
				InformServer();//Socketの生成、入出力ストリームの取得、サーバーに対して交換終了通知、公開鍵の提出
				socket.close();
				in.close();
			}
			//ラウンドを進める
			if(input.equals("roundstart")){
				int round = Integer.parseInt(in.readLine());
				System.out.println("Round:"+(round+1)+"を開始します");
				RoundMain rm = new RoundMain(round);
				rm.KeyExchange();//RoundMain.javaのKeyExchangeメソッドの実行
                                //鍵配送指令所の解析、Peerの呼び出し、条件分岐による交換処理
                                
				InformServer();//Socketの生成、入出力ストリームの取得、交換終了通知、公開鍵の提出
				socket.close();
				in.close();
			}
			//鍵を保存
			if(input.equals("finish")){
				System.out.println("鍵交換を終了します");
				GroupSend gs = new GroupSend();
				try {
					//最終ラウンドを終えたら鍵を保存
					new SaveKey(gs.getGroupName(), String.valueOf(gs.getGroupValue()),gs.getKey());
				} catch (Exception e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				
				
				socket.close();
				in.close();
			}
			
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
	}
	
        
        //交換終了通知用ソケットメソッド
	private void InformServer(){
		try {
			Thread.sleep(1 * 200);	//もしかしたら、いらないかも
			informsocket = new Socket(serverip,RoundControllPort);        //ソケットを生成する
			// 入出力ストリームを取得する
			oos = new ObjectOutputStream(informsocket.getOutputStream());
			oos.writeObject(name);			//交換終了を知らせる
                        
			//ここで,(PSKで暗号化した)公開鍵をサーバーに提出するようにする
			informsocket.close();
			oos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
