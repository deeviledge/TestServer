package sglclient.keyexchange;
/**
 * ダミーユーザに公開鍵を送る
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;

public class SendPKey {
	private Socket socket;
	public SendPKey(String peerip,int roundport){
		try {
			socket = new Socket( peerip , roundport );
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
	public void KeyExchange(BigInteger pk){
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			//公開鍵を送信
			String input = "pk:";
			input += ""+pk;
			out.println(input);
			System.out.println("Peer client :" + "送信:" + input);
			socket.close();
			out.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		
	}
}
