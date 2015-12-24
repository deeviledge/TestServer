package sglclient.pskey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sglclient.keystore.X509;
import sglclient.myinformation.MyInformation;

/**
 * PreSharedKeyファイルを暗号化・復号する
 * @author kinbara
 * @version 1.1
 * @作成日: 2007/12/11
 * @最終更新日:2008/10/31
 */
public class XMLCryption {
	private String storePasswd; // MyKeyStoreのStorePassword
	private String keyPasswd; // MyKeyStoreのKeyPassword
	private MyInformation mi;
	private String ID; // ピアのID
	private String mode = "AES"; // 暗号化モード
	
	/**
	 * コンストラクタ
	 *
	 */
	public XMLCryption(){
		mi = new MyInformation();
		this.setPasswd(); // キーストアのパスワードを設定
		this.ID = mi.getUsrID();
	}
	
	/**
	 * 本来は用いないコンストラクタ
	 * @param myid 自分のid
	 */
	public XMLCryption(String myid){	
		this.setPasswd();
		this.ID = myid;
	}
	
	/**
	 * 暗号化
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws InvalidKeyException
	 * @author kinbara
	 */
	public void Encrypt() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, InvalidKeyException{
		X509 x509 = new X509("src/sglclient/conf/usr/ca","MyKeyStore",this.storePasswd); // キーストアを用いるため
		File file = new File("src/sglclient/conf/key/PreSharedKey" + ID + ".xml"); // 元のPreSharedKeyファイル
		File cryptedfile = new File("src/sglclient/conf/key/","CRYPT_"+ID); // 暗号化されたファイル
		FileInputStream fis;
	    FileOutputStream fos;
	    CipherInputStream cis;
	    //	  復号に使用する鍵の作成
		// TODO:128bitまでしかできていない
		RSAPrivateKey sk = x509.getRSAPrivateKey(mi.getUsrName(),this.keyPasswd); // ピアの秘密鍵
		BigInteger key = sk.getModulus();
		BigInteger key1 = new BigInteger(key.toString(16).substring(0,16),16);
		BigInteger key2 = new BigInteger(key.toString(16).substring(16),16);
	    key = key1.xor(key2); // 排他的論理和
	    //String mode2 = "AES";
		SecretKey sek = new SecretKeySpec(key.toByteArray(),0,16, mode);
		//	Cipherの作成
		Cipher desCipher = null;
		try {
			//アルゴリズム、モード、パディング方式の設定
			desCipher = Cipher.getInstance( mode );
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		desCipher.init(Cipher.ENCRYPT_MODE, sek); // 暗号化の初期化
		fis = new FileInputStream( file );
		cis = new CipherInputStream(fis, desCipher);
		fos = new FileOutputStream( cryptedfile ); // 暗号化した文字列をcryptedfileに書き込むためのもの
		byte[] b = new byte[16];
		int i = cis.read(b);
		while (i != -1) {
			fos.write(b, 0, i); // 書き出す
			i = cis.read(b);
		}
		System.out.println("暗号化終了");
		// ストリームを閉じる
		fis.close();
		cis.close();
		fos.close();

		if(file.delete() == false){
			System.out.println("削除できませんでした。");// ファイルを消す
		}else{
			System.out.println("削除できました。");
		}
	}
	
	/**
	 * 復号
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws InvalidKeyException
	 * @author kinbara
	 */
	public void Decrypt() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, InvalidKeyException{
		X509 x509 = new X509("src/sglclient/conf/usr/ca","MyKeyStore",this.storePasswd); // キーストアを用いるため
		File cryptedfile = new File("src/sglclient/conf/key/","CRYPT_"+ID); // 暗号化されたファイル
		File decfile = new File("src/sglclient/conf/key/","PreSharedKey"+ID+".xml"); // 復号されたファイル
		// ストリーム
		FileInputStream fis;
	    FileOutputStream fos;
	    CipherInputStream cis;
	    
	    //　復号に使用する鍵の作成
		// TODO:128bitまでしかできていない
		RSAPrivateKey sk = x509.getRSAPrivateKey(mi.getUsrName(),this.keyPasswd); // ピアの秘密鍵
		BigInteger key = sk.getModulus();
		BigInteger key1 = new BigInteger(key.toString(16).substring(0,16),16);
		BigInteger key2 = new BigInteger(key.toString(16).substring(16),16);
		key = key1.xor(key2); // 排他的論理和
		SecretKey sek = new SecretKeySpec(key.toByteArray(),0,16, mode);
		Cipher desCipher = null; // cipherの作成
		try {
			//アルゴリズム、モード、パディング方式の設定
			desCipher = Cipher.getInstance( mode );
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		// 復号化の設定
	    desCipher.init(Cipher.DECRYPT_MODE, sek); // 復号初期化
	    // ストリームの設定
	    fis = new FileInputStream( cryptedfile );
	    cis = new CipherInputStream(fis, desCipher);
	    fos = new FileOutputStream( decfile ); // 復号した文字列をcryptedfileに書き込むためのもの
	    
	    byte[] b = new byte[16];
	    int i = cis.read(b);
	    while (i != -1) {
	        fos.write(b, 0, i); // 書き出す
	        i = cis.read(b);
	    }
	    System.out.println("復号終了");
	    // ストリームを閉じる
		fis.close();
		cis.close();
		fos.close();
	}
	
	/**
	 * 復号されたPreSharedKeyファイルを消去するクラス
	 * @author kinbara
	 */
	public void deleteFile(){
		//	ファイル消去
		File file = new File("src/sglclient/conf/key/PreSharedKey" + ID + ".xml"); // 元のPreSharedKeyファイル
		if(file.delete() == false){
			System.out.println("削除できませんでした。");// ファイルを消す
		}else{
			System.out.println("削除できました。");
		}
	}
	
	/**
     * パスワードを設定します。
     * @author kinbara
     */
    private void setPasswd(){
        while(true){
            System.out.println("オフライン登録で使用した、パスワードを設定してください。");
            try{
                System.out.print("StorePasswd:");
                //this.storePasswd = keyin.readLine();
                this.storePasswd = "projecttheta";
                System.out.print("SecretKeyPasswd:");
                //this.keyPasswd = keyin.readLine();
                this.keyPasswd = "projecttest";
            }catch(Exception e){
                e.printStackTrace();
            }
			try {
                System.out.println(this.storePasswd);
                X509 x509 = new X509("src/sglclient/conf/usr/ca", "MyKeyStore", this.storePasswd);
                x509.getRSAPrivateKey(mi.getUsrName(),this.keyPasswd);
                break;
            } catch (Exception e) {
                System.out.println("設定したパスワードが間違っています。");
            }	
        }
    }
}
