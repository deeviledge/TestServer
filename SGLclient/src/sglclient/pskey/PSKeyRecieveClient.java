package sglclient.pskey;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sglclient.myinformation.*;




/**
 * PreSharedKey受信するクラス
 * 
 * @author hiroki
 * @version 1.2
 */
public class PSKeyRecieveClient {
	//TODO:クライアントランダム値、サーバーランダム値、プリマスターシークレットの使い方を考える。
	ObjectOutputStream oos;
	ObjectInputStream ois;
	Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private BufferedReader keyin;
	private BigInteger clientRandom;
	@SuppressWarnings("unused")
	private BigInteger serverRandom;
	private BigInteger preMastarSecret;
	private MyInformation peer;
	private Certificate cert;
	@SuppressWarnings("rawtypes")
	private List netDigest;
	private String storePasswd;
	private String keyPasswd;
	private int port = 12345;
        public boolean cert_flg = false;
	/**
	 * 引数なしのコンストラクタ
	 * 
	 * @param ipaddr 接続先のIPアドレス
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PSKeyRecieveClient(String ipaddr){
		

		try{
			
			
			keyin = new BufferedReader(new InputStreamReader(System.in));
			peer = new MyInformation();
			//パスワードの設定
			this.setPasswd();
			
			socket = new Socket(ipaddr,port);        //ソケットを生成する
			// 入出力ストリームを取得する
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
			//認証・ログイン要求
			oos.writeObject("cert");
		
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			String input = new String();
			String message = new String();
			String line = new String();
			netDigest = new LinkedList();

			// キーボートからの入力待ち
//			System.out.println("start:PreSharedKeyの配布を要求");
//			System.out.println("exit:システムの終了");
//			System.out.print("入力:");
//			while( (input = keyin.readLine()) != null ){
				/*
				 * random_bytes             32byte クライアントランダム値
			?	 * cipher_suites_length      1byte 使用可能な暗号アルゴリズムのリストのサイズ
			?	 * cipher_suites         1~255byte 暗号アルゴリズムのリスト。希望順
				 * user_id                   4byte ユーザID
				 */
				// PreSharedKeyの配布を要求
			input = "start";
			if(input.equals("start") ){
                            // Client_Helloの作成
                            out.println(this.setClientHello());// サーバーに送信
                            // サーバーからの受信待ち
                            while( (line = in.readLine()) != null ){
				//System.out.println(line);// 受信したメッセージを表示
				if( line.indexOf(":") != -1 ){
                                    message = line.substring( 0, line.indexOf(":") );// メッセージの抽出
                                    System.out.println(socket.getRemoteSocketAddress()+" = "+ message );
                                    netDigest.add( line );// ダイジェストの追加
				}
				// Server_Helloの受信
				if(message.equals("Server_Hello")){
                                    String str = line.substring(line.indexOf(":")+1);
                                    this.recieveServerHello(str);
				}
				// Server_Certificateの受信
				else if( message.equals("Server_Certificate") ){
                                    // サーバーの証明書を信頼できない場合
                                    if( !(this.recieveServerCertificate(line)) ){
					System.out.println("サーバーの証明書は危険です");
					break;
                                        
                                    }
                                    // Client_Key_Exchangeの作成
                                    out.println( this.setClientKeyExchange() );// サーバーに送信
                                    // Certificate_Verifyの作成
                                    out.println( this.setClientVerify() );// サーバーに送信
				}
				// Finishedの受信
				else if( message.equals( "Finished" )){
                                    this.recieveFinished(line);
                                    cert_flg = true;
                                    break;
				}
                            }
//                          break; // キーボード入力から抜ける
			}
                        //socket.close();
/*			// 終了
			else if(input.equals("exit")){
                            break;
			}
			// コマンドヘルプ
			else {
                            System.out.println("start:PreSharedKeyの配布を要求");
                            System.out.println("exit:システムの終了");
			}
                    }
*/                  // サーバーからの受信待ち
                    //while( (line = in.readLine()) != null );
			
                    }catch(Exception e){
			e.printStackTrace();
                    }
        }

    /**
     * サーバーに送るためのClient_Helloメッセージを作成します。
     * 
     * @return Client_Hello用の文字列を返します。
     */
    @SuppressWarnings("unchecked")
	private String setClientHello(){
		String output = "Client_Hello";
		clientRandom = new BigInteger(256,10,new SecureRandom());
		output += ":" + clientRandom;
		// cipher_suite
		// RSA AES 256 CBC HABAL
		// TODO:使い道は？
		output += ":" + "00000064";
		// UserIDを追加
		output += ":" + "ID=" + peer.getUsrID();
		netDigest.add( output );// ダイジェストの追加

		return (output);
    }
    
    /**
     * Server_Helloを受け取ったときの動作をします。
     * 
     * @param line サーバーから受信した文字列を入力とします。
     */
    private void recieveServerHello(String line){
		// サーバーランダム値
		// TODO:使い道は？
		String serverrand = line.substring( 0, line.indexOf(":") );
		serverRandom = new BigInteger( serverrand );
//		System.out.println("server_random = "+ serverRandom );

		// cipher
		// TODO:使い道は？
		line = line.substring(line.indexOf(":")+1);
		@SuppressWarnings("unused")
		String cipher = line.substring( 0, line.indexOf(":") );
//		System.out.println("cipher = "+ cipher );

		// ユーザー名
		line = line.substring(line.indexOf(":")+1);
		String userName = line.substring( line.indexOf("=")+1 );

		if(!userName.equals(peer.getUsrName())) System.out.println("返されたユーザー名が違います。");

		
//		System.out.println("Name = "+ userName);
    }
    
    /**
     * Server_Certificateを受信したときの動作をします。
     * 
     * @param line サーバーから受信した文字列を入力とします。
     * @return サーバーの証明書が、ピアの持っているものと一致するかどうかを返します。
     */
    @SuppressWarnings("unchecked")
	private boolean recieveServerCertificate(String line){
    	boolean cert_switch = false;
		// サーバーの証明書を検証
		try{
			byte[] sign = new byte[128];
			line = line.substring(line.indexOf(":")+1);
			@SuppressWarnings("unused")
			byte byte1 =0;
			int linelen = line.length()/2;
			for(int i=0; i < linelen; i++){
		        BigInteger big = new BigInteger(line.substring(0,2),16);
		        byte[] bytes0 = big.toByteArray();
		        sign[i] = bytes0[bytes0.length-1];
				line = line.substring(2);
			}
			String input;
			System.out.println("送られた証明書を表示します");
			//while((input = keyin.readLine()).length() > 0){
			// 証明書の表示を要求
				String str = this.setClientRequest();
				out.println(str);
				netDigest.add(str);
				boolean in_switch = false;
				BufferedWriter bw = null;
				try{
					// ファイル書き込みの準備
					bw = new BufferedWriter(new FileWriter( "src/sglclient/conf/key/ca/ca.cer" ));
					while( !in_switch ){
						str = in.readLine();
						System.out.println( str );
						// writeメソッドで書き込む
						bw.write( str );
						// newLineメソッドで改行
						bw.newLine();
						if( str.substring(0).equals("-----END CERTIFICATE-----") ){
							in_switch = true;
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					// BufferedWriterを閉じる
					if(bw != null){
						bw.close();
					}

				}
					
				// 証明書の表示
				FileInputStream fis = new FileInputStream("src/sglclient/conf/key/ca/ca.cer");
				BufferedInputStream bis = new BufferedInputStream(fis);
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				while (bis.available() > 0) {
					cert = cf.generateCertificate(bis);
					System.out.println(cert.toString());
				}
				fis.close();
				bis.close();

					
				
				
				
				
			//}
			
			// クライアントにあるサーバーの証明書で検証
			System.out.print("この証明書を検証した結果、");
			X509 x509 = new X509("src/sglclient/conf/usr/ca","MyKeyStore",this.storePasswd);
			if(x509.verify("server",sign)){
				System.out.println("信頼できる証明書です。");
				// severの証明書を設定
				this.cert = x509.getCertificate("server");	
			}else{
				System.out.print("持っているサーバーの証明書の有効期限が切れているか、");
				System.out.println("送られた証明書が危険なものです。");
                                
			}
			
		
			
				
			input = "Client_Answer:yes";
			out.println( input );
			netDigest.add( input );
			cert_switch = true;
				
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return (cert_switch);
    }
    
    /**
     * サーバーに送るためのClient_Key_Exchageメッセージを作成します。
     * 
     * @return Client_Key_Exchange用の文字列を返します。
     */
    @SuppressWarnings("unchecked")
	private String setClientKeyExchange(){
    	String output = "Client_Key_Exchange:";
		RSAPublicKey pk = (RSAPublicKey)cert.getPublicKey();
		// プリマスターシークレットをサーバーの公開鍵で暗号化
		preMastarSecret = new BigInteger(256,10,new SecureRandom());
		BigInteger cipher_pms = preMastarSecret.modPow( pk.getPublicExponent(), pk.getModulus() );
		output += cipher_pms.toString(16);
		netDigest.add( output );// ダイジェストに追加

		return (output);
    }
    
    /**
     * サーバーに送るためのClient_Verifyメッセージを作成します。
     * 
     * @return Client_Verify用メッセージを返します。
     */
    private String setClientVerify(){
    	String output = "Certificate_Verify:";
		// ここまでのダイジェストを文字列に変換
		@SuppressWarnings("rawtypes")
		ListIterator listIte = netDigest.listIterator();
    	String digest = new String("");
    	while(listIte.hasNext()){
    		 digest += listIte.next().toString();
    	}
//    	System.out.println("digest:"+digest);
    	// ハッシュ値をクライアントの秘密鍵で暗号化
		try{
			X509 x509 = new X509("src/sglclient/conf/usr/ca","MyKeyStore",this.storePasswd);
			RSAPrivateKey sk = x509.getRSAPrivateKey(peer.getUsrName(),this.keyPasswd);
			BigInteger digest1 = new BigInteger(this.getHash(digest),16);
			digest1 = digest1.modPow(sk.getPrivateExponent(),sk.getModulus());
			output += digest1.toString(16);
		}catch(Exception e){
			e.printStackTrace();
		}
    	
    	return (output);
    }
    
    /**
     * サーバー証明書の送信要求を作成します。
     * 
     * @return サーバー証明書の送信要求文字列を返します。
     */
    private String setClientRequest(){
    	String output = "Client_Request:";
    	return (output);
    }
    
    /**
     * Finishedを受け取ったときの動作をします。
     * 
     * @param line サーバーから受け取った文字列を入力とします。
     * @throws InvalidKeyException 有効でない鍵を渡したときスローされます。
     * @throws IOException　ファイルの入出力エラーをスローします。
     */
    private void recieveFinished(String line) throws InvalidKeyException, IOException{
		File cryptedfile = new File("src/sglclient/conf/key/","AES_"+peer.getUsrID());
		FileOutputStream fos = new FileOutputStream(cryptedfile);
		byte byte1 =0;
		
		line = line.substring(line.indexOf(":")+1);
		int linelen = line.length()/2;
		for(int i=0; i < linelen; i++){
	        BigInteger big = new BigInteger(line.substring(0,2),16);
	        byte[] bytes = big.toByteArray();
	        byte1 = bytes[bytes.length-1];
			fos.write(byte1);
			line = line.substring(2);
		}
		fos.close();
		File decfile = new File("src/sglclient/conf/key/","PreSharedKey"+peer.getUsrID()+".xml");
		this.decryptoPreSharedKey(cryptedfile,decfile,preMastarSecret);
		cryptedfile.delete();
		System.out.println("PreSharedKeyの受信完了");

    }
    
	/**
     * ハッシュ値を計算します。
     * @param input ハッシュしたい文字列
     * @return 16進表記の文字列を返します。
     */
    private String getHash(String input){
    	MessageDigest md = null;
    	try{
			//メッセージダイジェストの作成
    	    // TODO:ハッシュ値の種類の設定を考える。
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
     * 暗号化されたPreSharedKeyファイルを共有鍵で復号して保存します。
     * 
     * @param cryptedfile 暗号化されたファイル
     * @param decfile 復号したファイル
     * @param key 共有鍵
     * @throws InvalidKeyException 有効でない鍵を渡したときスローされます。
     * @throws IOException　ファイルの入出力エラーをスローします。
     */
    private void decryptoPreSharedKey(File cryptedfile, File decfile, BigInteger key) throws InvalidKeyException, IOException{
    	FileInputStream fis;
	    FileOutputStream fos;
	    CipherInputStream cis;
	    // TODO:暗号化モードの設定を考える
	    String mode = "AES";
		// 復号に使用する鍵の作成
		// TODO:128bitまでしかできていない
	    BigInteger key1 = new BigInteger(key.toString(16).substring(0,16),16);
	    BigInteger key2 = new BigInteger(key.toString(16).substring(16),16);
	    key = key1.xor(key2); //排他的論理和
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
			
		//復号化の設定
	    desCipher.init(Cipher.DECRYPT_MODE, sk);
	    fis = new FileInputStream( cryptedfile );
	    cis = new CipherInputStream(fis, desCipher);
	    fos = new FileOutputStream( decfile );
	    byte[] b = new byte[16];
	    int i = cis.read(b);
//		System.out.println("復号化開始");
	    while (i != -1) {
	        fos.write(b, 0, i);
	        i = cis.read(b);
	    }
//		System.out.println("復号化終了");
		fis.close();
		cis.close();
		fos.close();

    }
    
    /**
     * パスワードを設定します。
     */
    private void setPasswd(){
        /*while(true){
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
                X509 x509 = new X509("src/sglclient/conf/usr/ca","MyKeyStore",this.storePasswd);
                x509.getRSAPrivateKey(peer.getUsrName(),this.keyPasswd);
                break;
            } catch (Exception e) {
                System.out.println("設定したパスワードが間違っています。");
            }
			
        }*/
        this.storePasswd = "projecttheta";
        this.keyPasswd   = "projecttest";
    }
}
