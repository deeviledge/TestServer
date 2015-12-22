package sglserver.pskey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sglserver.keystore.X509;

/**
 * PreSharedKeyの更新するクラス
 * @author kinbara
 * @version 1
 * @作成日: 2007/12/03
 * @最終更新日:2008/10/31
 */
public class RegenerateKeyServer {
	private BigInteger preMastarSecret;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private BigInteger rootRandomValue;
	private String userID;
	private String storePasswd;
	private String keyPasswd;
	PreSharedKeyServer psk = new PreSharedKeyServer();
	
	/**
	 * コンストラクタ
	 */
	public RegenerateKeyServer(){
		
	}
	
	/**
	 * ユーザのPreSharedKeyを更新するのかをユーザに送信、更新する(同じソケットを用いる場合)
	 * @param soc ソケット
	 * @param ois ObjectInputStream
	 * @param oos ObjectOutputStream
	 * @param id 自身のID
	 * @param プリマスタシークレット
	 * @throws IOException
	 * @throws ParseException 
	 * @author kinbara
	 */
	public void RegeneratePSKeyClient(Socket soc,ObjectInputStream ois,ObjectOutputStream oos,String id, BigInteger pre, String storepass, String keypass) throws IOException, ParseException{
		//PreSharedKeyServer_Pre psks = new PreSharedKeyServer_Pre();
		this.storePasswd = storepass;
		this.keyPasswd = keypass;
		if(psk.isSendFinished() == false){ // PreSharedKeyServer.xmlが更新されていて、ユーザ全員にPreSharedKeyを配布していない場合
			if(psk.isUserSendFinished(id) == false){ // コネクトしているユーザがPreSharedKeyを更新していない場合		
				oos.writeObject("send"); // 文字列をユーザへ送信
				this.RegeneratePreSharedKey(soc, ois, oos, id, pre); // PreSharedKeyを更新
				psk.setUserSendFinish(id); // ユーザにPreSharedKeyを送信したのでフラグを立てる
				System.out.println("ユーザ:" + id + "にPreSharedKeyを送信しました。");
				if(psk.CheckSendFinished() == true){ // ユーザ全員のPreSharedKeyが更新されたのなら
					psk.setSendFinish(); // 「全員に送信した」というフラグを立てる
				}
			}else{ // 更新済みの場合
				oos.writeObject("notsend"); // 文字列を送信
				System.out.println("ユーザ:" + id + "にはPreSharedKeyファイルをすでに送信してあります。");
			}
		}else{
			oos.writeObject("notsend");
		}
	}
	
	/**
	 * クライアントのPreSharedKeyを更新、送信するメソッド
	 * @param soc 接続しているソケット
	 * @param i ObjectInputStream
	 * @param o ObjectOutputStream
	 * @param ID クライアントのID
	 * @author kinbara
	 */
	public void RegeneratePreSharedKey(Socket soc,ObjectInputStream i,ObjectOutputStream o,String ID, BigInteger pre){
		try{
			new BufferedReader(new InputStreamReader(System.in));
			//this.setPasswd();
			// ソケットなどの設定
			Socket socket = soc;
			in = i;
			out = o;
			this.preMastarSecret = pre;	
			//String line = (String) in.readObject(); // 受信
			//if( line.substring(0,line.indexOf(":")).equals("Client_Key_Exchange") ){ // 受信した文字列の書き出しが正しいなら
				//String recievekey = line.substring(line.indexOf(":")+1); // プリマスターシークレットを抽出
			userID = ID; // userIDを設定
			System.out.println(userID);
				//this.recieveClientKeyExchange(recievekey); // プリマスターシークレットを復号
			out.writeObject(this.GeneratePreSharedKeyClient()); // PreSharedkeyをユーザに送信など
			System.out.println(socket.getRemoteSocketAddress()+"に、PreSharedKeyの送信完了");
			//}
	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
	}
	
	/**
	 * Client_Key_Exchangeを受け取ったときの動作をします。(使わない)
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
		RSAPrivateKey sk = x509.getRSAPrivateKey("server",this.keyPasswd); // serverの秘密鍵を設定
		BigInteger preCipher = new BigInteger(line,16);
		// プリマスターシークレットを復号
		preMastarSecret = preCipher.modPow(sk.getPrivateExponent(),sk.getModulus());
		System.out.println( "PreMastarSecret:"+ preMastarSecret.toString(16) );
	    
	}
	
	/**
	 * PreSharedKeyServer.xmlを作成(更新)
	 * @author kinbara
	 */
	public void RegeneratePreSharedKeyServer(){
		PreSharedKeyGenerator pskg = new PreSharedKeyGenerator();
		String arand = pskg.GenerateServerRandomValue(); // ランダム値を作成
		System.out.println(arand);
		pskg.createServerPreSharedKeyXML(arand); // PreSharedKeyServer.xmlを作成(更新)
	}
	
	/**
	 * クライアントのPreSharedKeyファイルを更新、Finishedメッセージを返します。
	 * @return 16進数表記の文字列
	 * @throws IOException 入出力エラーをスローします。
	 * @author kinbara
	 */
	private String GeneratePreSharedKeyClient() throws IOException{
		// クライアントに正常に終了したことを通知。
		String ans = "Finished:";
	    // PreSharedKeyの暗号化・送信
		PreSharedKeyGenerator pskg = new PreSharedKeyGenerator(psk.getMAX_MEMBER());
		// 認証したクライアントのXMLを作成・保存
		// TODO:サーバーランダム値の設定時期を考える
		rootRandomValue = new BigInteger(psk.getRootRandomValue(),16);
		//userID = "0003";
		System.out.println("userID=" + userID );
		System.out.println("parseInt(userID) = " + Integer.parseInt(userID));
		File file = pskg.createUserPreSharedKeyXML(Integer.parseInt(userID),rootRandomValue.toString(16)); // クライアントのPreSharedKeyファイルを作成
		
		File cryptedfile = new File(file.getParent(),"AES_"+userID);
 		try {
            // TODO:共有鍵をプリマスターシークレットとしているので変更する必要がある
            this.cryptoPreSharedKey(file,cryptedfile,preMastarSecret); // 暗号化
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

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
		// 作成したPreSharedKeyファイルを削除
		if(file.delete() == false){ //消せなかった場合
			System.out.println("削除できませんでした。");
		}else{ // 消せた場合
			System.out.println("削除できました。");
		}
		cryptedfile.delete(); // 暗号化したファイルも消す
	    return (ans);
	}
	
	/**
     * 指定された共通鍵とモードで、指定されたファイルを暗号化し保存します。
     * @param file 暗号化するファイル
     * @param cryptedfile 暗号化したデータの保存ファイル
     * @param key 暗号化に使用する共通鍵
     * @throws InvalidKeyException
     * @throws IOException
	 * @author kinbara
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
	
    /**
     * パスワードを設定します。
	 * @author kinbara
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

