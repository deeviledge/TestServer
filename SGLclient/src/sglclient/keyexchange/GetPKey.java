package sglclient.keyexchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 自分がダミーのときに呼び出す
 * 公開鍵を受け取る
 * @author nishimura
 *
 */
public class GetPKey {
	private Socket socket;
	ServerSocket ssoc;
	public GetPKey(int roundport){
		 try {
			ssoc = new ServerSocket(roundport,100);
			
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param pk
	 * @return 受け取った公開鍵
	 */
	public String KeyExchange(){
		String line = null;
		try {
			socket = ssoc.accept();
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//公開鍵を受信
			line = in.readLine();
			System.out.println("Peer client :" + "受信:" + line);
			socket.close();
			ssoc.close();
			in.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return line;
		
	}
}
