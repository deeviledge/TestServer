package sglserver.pskey;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * サーバーのスレッドを扱うクラス
 * @author hiroki
 * @version 1.1
 * @作成日: 2005/01/17
 * @最終更新日:2008/10/31
 */
public class PSKeySendServer {
	private ServerSocket serverSocket;
	private String storePasswd;
	private String keyPasswd;
	private int port =6001;

	/**
	 * コンストラクタ
	 */
	public PSKeySendServer(){
		try{
			new BufferedReader(new InputStreamReader(System.in));
			this.setPasswd();
			serverSocket = new ServerSocket( port );
			System.out.println("ServerPort="+ serverSocket.getLocalPort() );
			while(true){
				Socket socket = serverSocket.accept(); 	// サーバーソケットの待ち受け
				new PSKeySendServerThread(socket,this.storePasswd,this.keyPasswd).start();	// スレッドの開始
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if( serverSocket != null ){
					serverSocket.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
    /**
     * パスワードを設定します。
     * @author hiroki
     */
    private void setPasswd(){
    	this.storePasswd = "projecttheta";
    	this.keyPasswd = "projecttest";
/*    	
        while(true){
            System.out.println("オフライン登録で使用した、パスワードを設定してください。");
            try{
                System.out.print("StorePasswd:");
                this.storePasswd = keyin.readLine();
                System.out.print("SecretKeyPasswd:");
                this.keyPasswd = keyin.readLine();
            }catch(Exception e){
                e.printStackTrace();
            }
			try {
                X509 x509 = new X509("conf/usr/ca","MyKeyStore",this.storePasswd);
                x509.getRSAPrivateKey(peer.getPeerName(),this.keyPasswd);
                break;
            } catch (Exception e) {
                System.out.println("設定したパスワードが間違っています。");
            }
			
        }
*/    
    }
}