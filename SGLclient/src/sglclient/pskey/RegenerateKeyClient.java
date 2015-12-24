package sglclient.pskey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sglclient.keystore.X509;
import sglclient.myinformation.MyInformation;

/**
 * PreSharedKeyを更新する（クライアント）
 * @author kinbara,masato
 * @version 1.1
 * @作成日: 2007/12/03
 * @最終更新日:2008/10/31
 */
public class RegenerateKeyClient {
	//TODO:クライアントランダム値、サーバーランダム値、プリマスターシークレットの使い方を考える。
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private BigInteger preMastarSecret;
	//private PeerInformation peer;
	private Certificate cert;
	private String storePasswd;
	private String keyPasswd;
	private MyInformation mi;
	int l;
	BigInteger serverRandom;
	
	/**
	 * コンストラクタ
	 *
	 */
	public RegenerateKeyClient(){
		
	}
	
	/**
	 * PreSharedKeyを更新するか判断し、更新する場合は更新するメソッド
	 * @param soc
	 * @param ois
	 * @param oos
	 * @param sglserverip
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @author kinbara
	 */
	public void RegeneratePSKeyClient(Socket soc,ObjectInputStream ois,ObjectOutputStream oos,String sglserverip, BigInteger pre) throws IOException, ClassNotFoundException{
		String sendps = (String)ois.readObject(); // 更新するか判断する文字列をサーバから受け取る
		if(sendps.equalsIgnoreCase("send")){ // 更新する場合
			System.out.println("更新");
			this.RegeneratePreSharedKeyClient(soc, ois, oos, sglserverip, pre);
		}
	}
	
	/**
	 * PreSharedKeyの更新
	 * @param soc
	 * @param i
	 * @param o
	 * @param ipaddr
	 * @author kinbara
	 */
	public void RegeneratePreSharedKeyClient(Socket soc,ObjectInputStream i,ObjectOutputStream o,String ipaddr, BigInteger pre){
		try{		
			mi = new MyInformation();
			//パスワードの設定
			this.setPasswd();
			// ソケットなどの設定
			socket = soc;
			System.out.println(socket.getRemoteSocketAddress() +"に接続");
			in = i;
			out = o;
			this.preMastarSecret = pre;
			String line = new String();
			line = (String) in.readObject(); // サーバーから受信
			this.recieveGenerateFinished( line ); // PreSharedKeyの復号、保存など
			XMLCryption xc = new XMLCryption();
			xc.Encrypt(); // 受信したファイルを自分の秘密鍵で暗号化して保存	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
	}
	
	/**
     * サーバーに送るためのClient_Key_Exchageメッセージを作成します。
     * @return Client_Key_Exchange用の文字列を返します。
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 * @author kinbara
     */
    private String setClientKeyExchange() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
    	String output = "Client_Key_Exchange:";
    	X509 x509 = new X509("src/sglclient/conf/usr/ca","MyKeyStore",this.storePasswd);
    	this.cert = x509.getCertificate("server");
		RSAPublicKey pk = (RSAPublicKey)cert.getPublicKey();
		// プリマスターシークレットをサーバーの公開鍵で暗号化
		preMastarSecret = new BigInteger(256,10,new SecureRandom());
		BigInteger cipher_pms = preMastarSecret.modPow( pk.getPublicExponent(), pk.getModulus() );
		output += cipher_pms.toString(16);
		return (output);
    }
	
    /**
     * Finishedを受け取ったときの動作をします。
     * @param line サーバーから受け取った文字列を入力とします。
     * @throws InvalidKeyException 有効でない鍵を渡したときスローされます。
     * @throws IOException　ファイルの入出力エラーをスローします。
     * @author kinbara
     */
    private void recieveGenerateFinished(String line) throws InvalidKeyException, IOException{	
    	File cryptedfile = new File("src/sglclient/conf/key/","AES_"+mi.getUsrID()); //暗号化ファイル
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
    	fos.close(); // 閉じる
    	File decfile = new File("src/sglclient/conf/key/","PreSharedKey"+mi.getUsrID()+".xml"); // 復号されたファイル
    	this.decryptoPreSharedKey(cryptedfile,decfile,preMastarSecret); // 復号、保存
    	cryptedfile.delete(); // 暗号化ファイルを削除
    	System.out.println("PreSharedKeyの受信完了");
    	
    }
    
    /**
     * 暗号化されたPreSharedKeyファイルを共有鍵で復号して保存します。
     * @param cryptedfile 暗号化されたファイル
     * @param decfile 復号したファイル
     * @param key 共有鍵
     * @throws InvalidKeyException 有効でない鍵を渡したときスローされます。
     * @throws IOException　ファイルの入出力エラーをスローします。
     * @author kinbara
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
