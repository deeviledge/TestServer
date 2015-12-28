package sglserver.keyexchange;
/**
 * グループメンバーにラウンド開始を通知する
 * 
 * @author nishimura
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import sglserver.dynamicpeerinformation.DynamicPeerInformation;

public class RoundServer{
	private Socket 	socket;
	@SuppressWarnings({ "unused", "rawtypes" })
	private List	lineList;
	
	
	
	public RoundServer(int KeyExchange_Port,String[] names,String command,int round) throws IOException{
		
		
		PrintWriter out;
		
		// ログインユーザ情報読み込み
		DynamicPeerInformation dpinfo = new DynamicPeerInformation();
		
                ThreadController tc=new ThreadController(names[0],names[1]);
                
		for(int i=0;i<names.length;i++){
			// グループメンバのIPアドレス取得
			String ip = dpinfo.getIP(names[i]);
			//　ソケット通信準備
			socket = new Socket(ip, KeyExchange_Port);
			out = new PrintWriter(socket.getOutputStream(), true);
			// ラウンド開始を伝える
			out.println(command);
			//次のラウンド数を伝える
			if(command.equals("roundstart"))out.println(String.valueOf(round));
                        //コントローラークラス
                }
                        
                        

                
	}
}

