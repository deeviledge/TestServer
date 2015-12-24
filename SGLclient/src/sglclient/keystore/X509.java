package sglclient.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * X.509ファイルを取り扱うクラス
 * @author Hiroki,kinbara,masato
 * @version 1.0
 * @作成日: 2005/01/03
 * @最終更新日:2008/10/31
 */
public class X509 {
	private KeyStore ks;
	
    /**
     * KeyStoreファイルを読み込んでX.509証明書を扱えるようにします
     * @param storepasswd KeyStoreファイルのパスワード
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public X509(String filepath, String filename, String storepasswd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
            //KeyStoreファイルのロード
            this.ks = KeyStore.getInstance("JKS");
            File file = new File(filepath, filename);     
            //System.out.println("file = " + filepath + "/" + filename);     
            FileInputStream fis = new FileInputStream(file);
            ks.load(fis,storepasswd.toCharArray());
    }
    
    /**
     * aliasが存在すればtrueを返す関数
     * @param String エイリアス
     * @throws KeyStoreException
     * @author kinbara 
     */
    public boolean isAlias(String alias) throws KeyStoreException{
    	return (ks.containsAlias(alias));
    }
    
    /**
     * 指定されたaliasにある証明書を読み込みます
     * @param alias エイリアス
     * @return aliasに格納されているCertificateオブジェクト
     * @throws KeyStoreException 指定されたaliasが存在しないときエラーを出します。
     * @author hiroki
     */
    public Certificate getCertificate(String alias) throws KeyStoreException{
    	return (this.ks.getCertificate(alias));
    }
    
    /**
     * 指定されたaliasにあるRSA公開鍵を返します
     * @param alias エイリアス
     * @return RSAPublicKeyオブジェクト
     * @throws KeyStoreException aliasが存在しないときエラーを出します。
     * @author hiroki
     */
    public RSAPublicKey getRSAPublicKey(String alias) throws KeyStoreException{
    	System.out.println("ks =" + this.ks.getCertificate(alias).getPublicKey());
    	//RSA公開鍵の取得
    	return ((RSAPublicKey)this.ks.getCertificate(alias).getPublicKey());
    }
    
    /**
     * RSA秘密鍵を返します。
     * @param alias エイリアス
     * @param keypasswd 秘密鍵のパスワード
     * @return RSAPriveteオブジェクト
     * @throws KeyStoreException aliasが存在しないときエラーを出します。
     * @throws NoSuchAlgorithmException 
     * @throws UnrecoverableKeyException パスワードが間違っている場合エラーを出します。
     * @author hiroki
     */
    public RSAPrivateKey getRSAPrivateKey(String alias, String keypasswd) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException{
    	//RSA秘密鍵の取得
    	System.out.println("ks =" + ks.getKey(alias, keypasswd.toCharArray()));
		return ((RSAPrivateKey)ks.getKey(alias, keypasswd.toCharArray()));
    }
    
    /**
     * ファイルをパスワードで保護して保存します。
     * @param storepasswd このパスワードで保護して保存します。
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @author hiroki
     */
    public void saveKeyStore(String filepath, String filename, String storepasswd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
   	   	//ファイルへの書き込み準備
       	FileOutputStream fos = new FileOutputStream(new File(filepath,filename));
		ks.store(fos,storepasswd.toCharArray());
		fos.close();
    }
    
	/**
	 * 指定されたエイリアスのX.509証明書をRSA秘密鍵を使ってSHA1で署名したbyte配列を返します
	 * @param alias エイリアス
	 * @param passwd 秘密鍵のパスワード
	 * @return X.509証明書を秘密鍵で署名したbyte配列
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @author hiroki
	 */
	public byte[] sign(String alias,String passwd) throws NoSuchAlgorithmException, SignatureException{
		//Signatureの初期化
		Signature sig = Signature.getInstance( "SHA1withRSA" );
		try{
			//秘密鍵の取得
			PrivateKey prikey = this.getRSAPrivateKey(alias,passwd);
			//証明書の取得
			Certificate cert1 = this.getCertificate(alias);
			
			//署名の生成
			sig.initSign(prikey);
			sig.update(cert1.getEncoded());
		}catch(Exception e){
			e.printStackTrace();
		}
		return (sig.sign());
	}
	
	/**
	 * 指定されたエイリアスのX.509証明書にある公開鍵(RSA)で指定された署名を検証します。
	 * @param alias 検証に使う証明書のエイリアス
	 * @param sign 検証するbyte配列
	 * @return 署名の真偽
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @author hiroki
	 */
	public boolean verify(String alias,byte[] sign) throws NoSuchAlgorithmException, SignatureException{
		//Signatureの初期化
		Signature sig = Signature.getInstance( "SHA1withRSA" );
		try{
			//公開鍵の取得
			PublicKey pubkey = this.getRSAPublicKey(alias);
			//証明書の取得
			Certificate cert1 = this.getCertificate(alias);
			//署名の検証
			sig.initVerify(pubkey);
			sig.update(cert1.getEncoded());
		}catch(Exception e){
			e.printStackTrace();
		}		
		return(sig.verify(sign));
	}
}
