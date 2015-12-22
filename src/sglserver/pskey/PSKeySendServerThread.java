package sglserver.pskey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
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

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sglserver.dynamicpeerinformation.DynamicPeerInformationAdministrator;
import sglserver.keystore.X509;
import sglserver.peerbasicinformation.PeerBasicInformationEdit;

/**
 * サーバー用スレッドの実装クラス
 * @author hiroki
 * @version 1.3
 * @作成日: 2005/01/18
 * @最終更新日:2008/10/31
 */
public class PSKeySendServerThread extends Thread {
	private Socket socket;
	private BigInteger serverRandom;
	private BigInteger preMastarSecret;
	@SuppressWarnings("rawtypes")
	private List netDigest;
	private BufferedReader in;
	private PrintWriter out;
	private BigInteger rootRandomValue;
	private String userID;
	private String peerName = "";
	private String storePasswd;
	private String keyPasswd;
	
	/**
	 * コンストラクタです。
	 * @param soc 接続したソケット
	 * @param storepass CAKeyStoreファイルを開くパスワード
	 * @param keypass 秘密鍵を取り出すためのパスワード
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PSKeySendServerThread(Socket soc, String storepass, String keypass){
		this.socket = soc;
		this.storePasswd = storepass;
		this.keyPasswd = keypass;
		System.out.println(socket.getRemoteSocketAddress() +"と接続");
		System.out.println("ログイン認証を行います");
		try{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			netDigest = new LinkedList();
			String line;
			String message = "";
			boolean cert_switch = false;
			System.out.println("test");
			while( (line = in.readLine()) != null ){
				System.out.println(line);
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
					out.println( this.setServerHello() );
				// Server_Certificateの送信
					out.println( this.setServerCertificate() );
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
					this.recieveFinished(line);
				}
				// クライアントが認証できたらPreSharedKeyを配布
				if(cert_switch){
					out.println(this.setFinished());
					System.out.println(socket.getRemoteSocketAddress()+"に、PreSharedKeyの送信完了");
					cert_switch = false;
				}
				/* 
				 * クライアントからメッセージを受信するたびに
				 * このスレッドが占有しているCPUを解放(0.3秒)
				 */
				sleep(300);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * (非 Javadoc)
	 * @see java.lang.Runnable#run()
	 * スレッドで実際に動作するところです。
	 * @author hiroki
	 */
	
	
	
	/**
	 * Client_Helloを受け取ったときの動作をします。
	 * @param line クライアントから受信した文字列
	 * @author hiroki
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
	 * @author hiroki
	 */
	@SuppressWarnings("unchecked")
	private String setServerHello(){
	    String ans = "Server_Hello";
		serverRandom = new BigInteger( 256,10,new Random() );
		ans += ":" + serverRandom;
		// RSA AES 256 CBC HABAL
		ans += ":" + "00000064";
		// userIDからピア名を得る
		PeerBasicInformationEdit peer = new PeerBasicInformationEdit();
		peerName = peer.getPeerName(this.userID);
		ans += ":Name=" + peerName;
		// ダイジェストに追加
		netDigest.add( ans );
	    return (ans);
	}
	
	/**
	 * Server_Certificateメッセージを作成します。
	 * @return Server_Certificateメッセージの文字列
	 * @author hiroki
	 */
	@SuppressWarnings("unchecked")
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
	 * @author hiroki
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
	 * @author hiroki
	 */
	private void recieveClientKeyExchange(String line) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException{
		X509 x509 = new X509("src/sglserver/conf/key/ca","CAKeyStore",this.storePasswd);
		RSAPrivateKey sk = x509.getRSAPrivateKey("server",this.keyPasswd);
		// 暗号化されたプリマスターシークレットの抽出
		line = line.substring( line.indexOf(":")+1 );
		BigInteger preCipher = new BigInteger(line,16);
		// プリマスターシークレットを復号
		preMastarSecret = preCipher.modPow(sk.getPrivateExponent(),sk.getModulus());  
	}
	
	/**
	 * Certificate_Verifyを受け取ったときの動作をします。
	 * @param line クライアントから受信した文字列
	 * @return クライアントの証明書の真偽
	 * @author hiroki
	 */
	private boolean recieveCertificateVerify(String line){
	    boolean cert_verify = false;
		// サーバー側のダイジェストのハッシュ値
    	@SuppressWarnings("rawtypes")
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
	 * @author hiroki
	 */
	private String setFinished() throws IOException{
		// クライアントに正常に終了したことを通知。
		String ans = "Finished:";
		//PreSharedKeyの暗号化・送信
		PreSharedKeyServer psk = new PreSharedKeyServer();
		PreSharedKeyGenerator pskg = new PreSharedKeyGenerator(psk.getMAX_MEMBER());
		// 認証したクライアントのXMLを作成・保存
		// TODO:サーバーランダム値の設定時期を考える
		rootRandomValue = new BigInteger(psk.getRootRandomValue(),16);
		File file = pskg.createUserPreSharedKeyXML(Integer.parseInt(userID),rootRandomValue.toString(16));
		File cryptedfile = new File(file.getParent(),"AES_"+userID);
 		try {
            // TODO:共有鍵をプリマスターシークレットとしているので変更する必要がある
            this.cryptoPreSharedKey(file,cryptedfile,preMastarSecret);
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        // 作成したPreSharedKeyファイルを削除
        if(!file.delete()) System.out.println(file.toString()+"は削除されませんでした。");
 		FileInputStream fis = null;
		try{
			fis = new FileInputStream(cryptedfile);
			byte[] bytes = new byte[1024];
			int i = fis.read(bytes);
			while (i != -1) {
				for (int j = 0; j < i; j++) {
					int val = bytes[j] & 0xFF;
					if (val < 16) {
					    ans += "0";
					}
					ans += Integer.toHexString(val);
				}
				i = fis.read(bytes);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		    if(fis != null)	fis.close();
		}
		cryptedfile.delete();
	    return (ans);
	}

	/**
	 * Finishedを受け取ったときの動作をします。
	 * @param line クライアントから受信した文字列
	 * @author hiroki
	 */
	private void recieveFinished(String line){
		line = line.substring(line.indexOf(":")+1);
		String remoteip = line.substring(0,line.indexOf(":"));
		// ログイン情報に追加
		DynamicPeerInformationAdministrator.addEntry(socket,userID, peerName,remoteip);
	}
	
    /**
     * ハッシュ値を計算します。
     * @param input ハッシュしたい文字列
     * @return 16進表記の文字列
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

    /**
     * 指定された共通鍵とモードで、指定されたファイルを暗号化し保存します。
     * @param file 暗号化するファイル
     * @param cryptedfile 暗号化したデータの保存ファイル
     * @param key 暗号化に使用する共通鍵
     * @throws InvalidKeyException
     * @throws IOException
	 * @author hiroki
     */
    private void cryptoPreSharedKey(File file, File cryptedfile, BigInteger key) throws InvalidKeyException, IOException{
    	FileInputStream fis;
	    FileOutputStream fos;
	    CipherInputStream cis;
		// TODO:暗号化モードの種類の設定時期を考える(String mode="DES/ECB/PKCS5Padding")
	    String mode = "AES";
		// 暗号化に使用する鍵の作成
		// TODO:128bitまでしかできていない
	    BigInteger key1 = new BigInteger(key.toString(16).substring(0,16),16);
	    BigInteger key2 = new BigInteger(key.toString(16).substring(16),16);
	    key = key1.xor(key2);
		SecretKey sk = new SecretKeySpec(key.toByteArray(), 0, 16, mode);
		//Cipherの作成
		Cipher desCipher = null;
		try {
			//アルゴリズム、モード、パディング方式の設定
			desCipher = Cipher.getInstance( mode );
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		//暗号化の設定
		desCipher.init(Cipher.ENCRYPT_MODE, sk);
		//データをbyteに変化
		fis = new FileInputStream( file );
		cis = new CipherInputStream(fis, desCipher);
		fos = new FileOutputStream( cryptedfile );
		byte[] b = new byte[16];
		int i = cis.read(b);
		while (i != -1) {
			fos.write(b, 0, i);
			i = cis.read(b);
		}
		fis.close();
		cis.close();
		fos.close();
    }
}