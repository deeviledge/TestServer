/**
 * グループ情報と鍵配送指令書を受け取るクラス
 * @author nishimura
 */


package sglclient.keyexchange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;



public class KEClient {
	
	private Socket socket = null;
	//public static final int Round_Port = 12766;
	
	public KEClient(Socket soc, BufferedReader in){

		try{
			String	groupFileName="src/sglclient/conf/usr/xml_files/GroupSend.xml";
			socket = soc;
			
			String line;
			
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		
			//　グループ情報を受信
			line = in.readLine();
			if( line.substring(0,10).equals("GroupName=") ){
			}
			line = in.readLine();
			if( line.substring(0,11).equals("GroupValue=") ){
			}
				
					
			//　グループ情報ファイルを受信＆保存
			line = in.readLine();
			if( line.substring(0,9).equals("FileName=") ){
				String gFileName = line.substring( line.indexOf("=")+1 );
				System.out.println("グループ情報ファイル:"+ gFileName + "\n\t-> "+groupFileName);
						
				// ファイル書き込みの準備
				BufferedWriter bw = new BufferedWriter(new FileWriter( groupFileName ));
				while( true ){
					line = in.readLine();
					//System.out.println("line=" + line);
					if( line.length() > 10 && line.substring(0,11).equals("[EndOfFile]") )
						break;
				
					//writeメソッドで書き込む
					bw.write( line );
					//newLineメソッドで改行
					bw.newLine();
				}
				//BufferedWriterを閉じる
				bw.close();
			}
			
			//　鍵配送指令書の受信＆保存（↑と同様：メソッド用意するとｽｯｷﾘしますね）
			line = in.readLine();
			String exchangeFileName = new String();
				
			if( line.substring(0,9).equals("FileName=") ){
				exchangeFileName = line.substring( line.indexOf("=")+1 );
				System.out.println("鍵交換指令書:"+ exchangeFileName);
				try{
					// ファイル書き込みの準備
					BufferedWriter bw = new BufferedWriter(new FileWriter( exchangeFileName ));
					while( true ){
						line = in.readLine();
						if( line.length() > 10 && line.substring(0,11).equals("[EndOfFile]") ){
							break;
						}
						//writeメソッドで書き込む
						bw.write( line );
						//newLineメソッドで改行
						bw.newLine();
					}
					//BufferedWriterを閉じる
					bw.close();
				
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			
			//in.close();
			out.close();
			
	
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}
}
