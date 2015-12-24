/**
 * 
 * 鍵交換用のポートを開いて、処理の裏で待ち受ける
 * @author nishimura
 */

package sglclient.groupadmin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ClientThread extends Thread{
	
	private final int KeyExchangePort = 12765;
	private Socket socket; 
	
	public void run(){
		try {
			@SuppressWarnings("resource")
			ServerSocket ssoc = new ServerSocket(KeyExchangePort,100);
            while(true) {
            	
            	socket = ssoc.accept();   
            	new KEConnect(socket);
            }
        } catch(Exception e){}
	}
	
	public void exit(){
        try {
            
            socket.close();
        } catch (IOException ex ) {
            System.err.println(ex);
        }
    }
	
}
