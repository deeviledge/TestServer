package sglserver.servermain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import sglserver.keystore.X509;
import sglserver.peerbasicinformation.PeerBasicInformationEdit;

/**
 * ログイン認証(handshake)　（SGLサーバ）
 * @author kinbara
 * @version 1.1
 * @作成日: 2007/12/11
 * @最終更新日:2008/10/31
 */
public class LoginAttestation extends Thread {
	private Socket socket;
	private BigInteger serverRandom;
	private BigInteger preMastarSecret = null;
	private List netDigest;
	private String userID;
	private String peerName = "";
	private String storePasswd;
	private String keyPasswd;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	/**
	 * コンストラクタ
	 *
	 */
	public LoginAttestation(){}
	
	/**
	 * ログイン認証するメソッド(プリマスタシークレットを返す)
	 * 
	 * @param soc 接続したソケット
	 * @param ois ObjectInputStream
	 * @param oos ObjectOutputStream
	 * @param storepass CAKeyStoreファイルを開くパスワード
	 * @param keypass 秘密鍵を取り出すためのパスワード
	 * @return プリマスタシークレット
	 * @author kinbara
	 */
	public BigInteger LoginAttestationServer(Socket s,ObjectInputStream i,ObjectOutputStream o,String storepass, String keypass){
		// キーストアのパスの設定
		this.storePasswd = storepass;
		this.keyPasswd = keypass;
		try{
			// ソケットなどの設定
			socket = s;
			System.out.println(socket.getRemoteSocketAddress() +"と接続");
			in = i;
			out = o;
			netDigest = new LinkedList();
			String line; // 受信する文字列を格納するもの
			String message = ""; // 受信した文字列をさらに分類するためのもの
			boolean cert_switch = false; // クライアントが認証できたかのフラグ
			System.out.println("start");
			while( (line = (String) in.readObject()) != null ){
				if( line.indexOf(":") != -1 ){
					// ダイジェストの追加
					netDigest.add(line);
					// メッセージの抽出
					message = line.substring( 0, line.indexOf(":") );
					System.out.println(socket.getRemoteSocketAddress()+"="+ message );
				}
				// Client_Helloの受信
				if(message.equals("Client_Hello")){
				    this.recieveClientHello(line);				
				// Server_Helloの送信
					out.writeObject( this.setServerHello() );
				// Server_Certificateの送信
					out.writeObject( this.setServerCertificate() );
				}
				// Client_Requestを受信
				else if(message.equals("Client_Request")){
					this.recieveClientRequest();
				}
				// Client_Answerの受信
				else if(message.equals("Client_Answer")){
					line = line.substring(line.indexOf(":")+1);
					if( (line.equals("no")) ) break; // クライアントとの接続を切断
				}
				// Client_Key_Exchangeの受信
				else if(message.equals("Client_Key_Exchange")){
				    this.recieveClientKeyExchange(line);
				}
				// Certificate_Verifyの受信
				else if(message.equals("Certificate_Verify")){
				    cert_switch = this.recieveCertificateVerify(line);
				    // クライアントが信頼できない場合、終了
				    if(!cert_switch) break;
				}
				// Finishedの受信
				else if(message.equals("Finished")){
					System.out.println(userID + "はログイン認証されました。");
					break;
				}
				// クライアントが認証できたらFinishedを送信
				if(cert_switch){
					out.writeObject(this.setFinished());
					System.out.println(socket.getRemoteSocketAddress()+"に、送信完了");
					cert_switch = false;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return this.preMastarSecret;
	}
	

	/**
	 * Client_Helloを受け取ったときの動作をします。
	 * @param line クライアントから受信した文字列
	 * @author kinbara
	 */
	private void recieveClientHello(String line){
		String str = line.substring(line.indexOf(":")+1);
		// クライアントランダム値
		String clientrand = str.substring( 0, str.indexOf(":") );
		//this.clientRandom = new BigInteger( clientrand );
		new BigInteger( clientrand );
		// cipher_suite(暗号化の方式/RSA AES 256 CBC HABAL)
		str = str.substring(str.indexOf(":")+1);
		// TODO:どうやって同意しているか未定
		//String cipher_suite = str.substring( 0, str.indexOf(":") );
		str.substring( 0, str.indexOf(":") );		
		// ユーザーIDの確認
		str = str.substring(str.indexOf(":")+1);
		// TODO:登録リストと比較・検証がまだ
		this.userID = str.substring( str.indexOf("=")+1 );
	}
	
	/**
	 * Server_Helloメッセージを作成します。
	 * @return Server_Helloメッセージの文字列
	 * @author kinbara
	 */
	private String setServerHello(){

	    String ans = "Server_Hello";
		serverRandom = new BigInteger( 256,10,new Random() );
		ans += ":" + serverRandom;
		// RSA AES 256 CBC HABAL
		ans += ":" + "00000064";
		// userIDからピア名を得る
		PeerBasicInformationEdit peer = new PeerBasicInformationEdit();
		peerName = peer.getPeerName(this.userID);
		//System.out.println("peername = " + peerName);
		ans += ":Name=" + peerName;
		// ダイジェストに追加
		netDigest.add( ans );
	    return (ans);
	}
	
	/**
	 * Server_Certificateメッセージを作成します。
	 * @return Server_Certificateメッセージの文字列
	 * @author kinbara
	 */
	private String setServerCertificate(){
		String ans = "Server_Certificate";
		ans += ":";
		// 証明書の送信
		// サーバーが自分の証明書に署名
		try{
			X509 x509 = new X509("src/sglserver/conf/key/ca","CAKeyStore",this.storePasswd);
			byte[] sign = x509.sign("server",this.keyPasswd);//署名
			for (int i = 0; i < sign.length; i++) {
				int val = sign[i] & 0xFF;
				if (val < 16) {
				    ans += "0";
				}
				ans += Integer.toHexString(val);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		// ダイジェストに追加
		netDigest.add( ans );
		return (ans);
	}
	
	/**
	 * ClientRequestを受け取ったときの動作をします。
	 * @author kinbara
	 */
	private void recieveClientRequest(){
		// 証明書の送信
		int n;
		byte[] buff = new byte[1024];
		FileInputStream sendfile = null;
		try{
			sendfile = new FileInputStream("src/sglserver/conf/key/ca/ca.cer"); //ファイルの読み出し準備
			OutputStream outstr = socket.getOutputStream();
			while((n = sendfile.read(buff)) > 0){ //ファイルを読み出し、
				outstr.write(buff,0,n); //ネットワーク経由でサーバに送ります
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(sendfile != null){
				try{
					sendfile.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * Client_Key_Exchangeを受け取ったときの動作をします。
	 * @param line クライアントから受信した文字列
	 * @throws CertificateException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 * @author kinbara
	 */
	private void recieveClientKeyExchange(String line) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException{
	    
		X509 x509 = new X509("src/sglserver/conf/key/ca","CAKeyStore",this.storePasswd);
		RSAPrivateKey sk = x509.getRSAPrivateKey("server",this.keyPasswd);
		// 暗号化されたプリマスターシークレットの抽出
		line = line.substring( line.indexOf(":")+1 );
		BigInteger preCipher = new BigInteger(line,16);
		// プリマスターシークレットを復号
		preMastarSecret = preCipher.modPow(sk.getPrivateExponent(),sk.getModulus());
                //（事前共有鍵）＝（秘密鍵）. modPow（（s乗：すなわちaのs乗）,（元:すなわちg））
                 //b = a.modPow(s, n); a mod n のs乗がbに代入される
	}
	
	/**
	 * Certificate_Verify(証明書)を受け取ったときの動作をします。
	 * @param line クライアントから受信した文字列
	 * @return クライアントの証明書の真偽
	 * @author kinbara
	 */
	private boolean recieveCertificateVerify(String line){
	    boolean cert_verify = false;
		// サーバー側のダイジェストのハッシュ値
    	ListIterator listIte = netDigest.listIterator();
    	String digest = "";
    	while(listIte.hasNext()){
    		 digest += listIte.next().toString();
    	}
    	digest = digest.substring(0,digest.indexOf("Certificate_Verify"));
		BigInteger digest1 = new BigInteger(this.getHash(digest),16);
    	// クライアントの公開鍵で復号
		BigInteger digest2 = new BigInteger(line.substring(line.indexOf(":")+1),16);
		try{
			X509 x509 = new X509("src/sglserver/conf/key/ca","CAKeyStore",this.storePasswd);
			// TODO:(注意)ユーザーの公開鍵をpeerNameでCAKeyStoreに登録している場合のみ有効
			RSAPublicKey pk = x509.getRSAPublicKey(peerName);
			digest2 = digest2.modPow(pk.getPublicExponent(),pk.getModulus());//復号
                        //b = a.modPow(s, n); →　a mod n のs乗がbに代入される
                        //pk.getPublicExponent()→公開指数
                        //pk.getModulus()→pkのmod値
		}catch(Exception e){
                    
			e.printStackTrace();
		}
		if(digest1.compareTo(digest2) == 0){
			System.out.println(socket.getRemoteSocketAddress()+"は、信頼できます。");
			cert_verify = true;
		}else{
			System.out.println(socket.getRemoteSocketAddress()+"は、危険です。");
		}
	    return(cert_verify);
	}
	
	/**
	 * Finishedメッセージを作成します。
	 * @return 16進数表記の文字列
	 * @throws IOException 入出力エラーをスローします。
	 * @author kinbara
	 */
	public String setFinished() throws IOException{
		// クライアントに正常に終了したことを通知。
		String ans = "Finished:";
	    return (ans);
	}
	
    /**
     * ハッシュ値を計算します。
     * @param input ハッシュしたい文字列
     * @return 16進表記の文字列
	 * @author kinbara
     */
    private String getHash(String input){
    	MessageDigest md = null;
    	try{
			//メッセージダイジェストの作成
    	    // TODO:ハッシュ値の計算にSHA-1しか使っていない。
			md = MessageDigest.getInstance( "SHA-1" );
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		//データをbyte型に変換
		byte[] cleartext = input.getBytes();
		//ダイジェストに追加
		md.update( cleartext );
		//メッセージダイジェストの計算
		byte[] hash = md.digest();
		//byteを16進数の文字列に変換
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < hash.length; i++) {
			int val = hash[i] & 0xFF;
			if (val < 16) {
				sb.append("0");
			}
			sb.append( Integer.toString(val,16) );
			//sb.append(" ");
		}
		return (sb.toString());
    }	
}