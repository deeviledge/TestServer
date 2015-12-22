package sglserver.servermain;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SGLサーバ（CA）を起動させるクラス 
 * @auhor masato
 * @version 1.1 
 * @作成日: 2007/7/9
 * @最終更新日:2008/11/07
 */
public class CAMain {
	
	private static String storePasswd;
	private static String keyPasswd;
        
        Socket soc;
	
	/** 
	 * コンストラクタ
	 */
	public	CAMain() {
		
		//TODO:パスワード設定をどうするか考える
		setPasswd();
		
		try {
                    @SuppressWarnings("resource")
                    ServerSocket ssoc = new ServerSocket(12345,100);    //サーバソケットを生成する(12345番ポート)
                    System.out.print("SGL CA start...");
                    System.out.println("Waiting for connection from sglclient...");
                    while(true) {
                        try { 		  
                            soc = ssoc.accept();   // SGLクライアントからの接続を待つ
                            new ServerConnect(soc,storePasswd,keyPasswd);    // 処理をServerConnectに任せる
                        }catch(Exception e){}
                    }
		} catch(Exception e){}
	}
	
        public void exit(){
            try {
                soc.close();
            } catch (IOException ex ) {
                Logger.getLogger(CAMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
	private static void setPasswd(){
		//シークレットキーのパスワードをログイン時に要求するが良いかも？
		storePasswd = "projecttheta";
		keyPasswd = "projecttest";
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

