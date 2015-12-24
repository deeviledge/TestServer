package sglclient.keyexchange;
/**
 * 自分が後に鍵を受け取る時呼び出す
 * 公開鍵の交換を行うクラス
 * 
 * @author nishimura
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class FromSend {
	private Socket socket;
	ServerSocket ssoc;
	public FromSend(int roundport){
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
	public String KeyExchange(BigInteger pk){
		String line = null;
		try {
			socket = ssoc.accept();
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//公開鍵を受信
			line = in.readLine();
			//System.out.println("Peer client :" + "受信:" + line);
			//公開鍵を送信
			String input = "pk:";
			input += ""+pk;
			out.println(input);
			//System.out.println("Peer client :" + "送信:" + input);
			socket.close();
			ssoc.close();
			in.close();
			out.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return line;
		
	}
}
