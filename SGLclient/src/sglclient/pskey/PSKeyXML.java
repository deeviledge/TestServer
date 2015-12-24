package sglclient.pskey;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * PreSharedKey認証の暗号化の際に用いる(PreSharedKey)
 * PreSharedKeyを扱うクラス
 * @author kinbara
 * @version 1.1
 * @作成日: 2007/11/21
 * @最終更新日:2008/10/31
 */

public class PSKeyXML{
	static String xmlfile = null;
	static Document document = null;
	Element root = null;
	public static final int MESSAGE_LENGTH = 16; // バイトの長さなど
	String crypt_spec = "AES"; // 暗号化方式
	
	/**
	 * コンストラクタ
	 * 自分のidを引数にする
	 * @param int 自分のid
	 */
	public PSKeyXML(int myid){
		try{
			// 自分の持っているPreSharedKeyのXMLファイルを指定
			if(myid < 10){
				xmlfile = "src/sglclient/conf/key/PreSharedKey000" + myid  + ".xml";
			}else if(myid <100){
				xmlfile = "src/sglclient/conf/key/PreSharedKey00" + myid  + ".xml";
			}else if(myid <1000){
				xmlfile = "src/sglclient/conf/key/PreSharedKey0" + myid  + ".xml";
			}else{
				xmlfile = "src/sglclient/conf/key/PreSharedKey" + myid  + ".xml";
			}
			//ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			//ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			//パースを実行してDocumentオブジェクトを取得
			document = builder.parse(new BufferedInputStream(new FileInputStream(xmlfile)));
			//ルート要素を取得
			root = document.getDocumentElement();
			
			}catch(Exception e){
				if(myid < 10){
					System.err.println("can't read PreSharedKey000" + myid + ".xml!");
				}else if(myid <100){
					System.err.println("can't read PreSharedKey00" + myid + ".xml!");
				}else if(myid <1000){
					System.err.println("can't read PreSharedKey0" + myid + ".xml!");
				}else{
					System.err.println("can't read PreSharedKey" + myid + ".xml!");
				}
			}
		}
	
	/**
	 * 送信先IDに対応するPreSharedValueを返す関数
	 * @param int 送信先ID
	 * @author kinbara
	 */
	public String getPreSharedKey(int SendID){
		NodeList list = root.getElementsByTagName("userVector");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("PreSharedValue");
		Element presharedkey = (Element)list2.item(SendID);
		String presharedvalue = presharedkey.getFirstChild().getNodeValue(); // PreSharedValueを取得
		return presharedvalue; // 返す
	}
	
	/**
	 * 自分のID番号のPreSharedValueを返す関数
	 * @param myid 自分のID
	 * @return
	 * @author kinbara
	 */
	public String getMyPreSharedKey(int myid){
		NodeList list = root.getElementsByTagName("userVector");
		Element user = (Element)list.item(0);
		NodeList list2 = user.getElementsByTagName("PreSharedValue");
		Element presharedkey = (Element)list2.item(myid);
		String presharedvalue = presharedkey.getFirstChild().getNodeValue(); // PreSharedValueを取得
		return presharedvalue; // 返す
	}
	
	/**
	 * PreSharedKey認証 認証が成功したらtrue,失敗したらfalseを返す(使わない)
	 * @param MyID
	 * @param SendToID
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 * @author kinbara
	 */
	public boolean PreSharedKeyAttestation(int MyID, int SendToID,BufferedReader in,PrintWriter out) throws IOException{
		
		String value = new String(); // 相手に送信する文字列
		String recievevalue = ""; // 相手から受け取る文字列
		String psvalue = getPreSharedKey(SendToID); // 相手のIDに対応するPreSharedValue
		value = "PreSharedKey:";
		value += psvalue;
		out.println(value); // 相手に送信
		recievevalue = in.readLine(); // 相手から受信
		
		// 判定
		if(recievevalue.substring(0,13).equals("PreSharedKey:")){
			String recievekey = recievevalue.substring(13); // 相手の持っている自分に対するPreSharedValueを受信した文字列から抽出する
			if(psvalue.equals(recievekey)){ // 自分と相手のPreSharedValueが同一ならば
				return true; // trueを返す
			}
			else{ // 同一でないならば
				return false; // falseを返す
			}
		}else{ // 上手く受信できなかった場合
			return false; // falseを返す
		}	
	}
	
	/**
	 * 暗号化(こちらのメソッドから)
	 * @param SendToID 相手のID
	 * @param sendvalue 暗号化して送信したいもの(BigInteger型)
	 * @return 暗号化されたもの(String型)
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 * @author kinbara
	 */
	public String EncryptWithPreSharedKey(int SendToID,BigInteger sendvalue) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException{
		byte[] pck = new byte[MESSAGE_LENGTH];
		String secret_key = getPreSharedKey(SendToID); // PreSharedKeyを暗号化の鍵にする
		pck = encrypt(sendvalue, secret_key, crypt_spec); // 暗号化する
		String pck_s=PSKeyXML.dump(pck, MESSAGE_LENGTH); // 暗号化されたバイト列をString型に変換
		String input = "pk:"; // 相手に送信する文字列
		input += pck_s; // 暗号化された文字列を付加
		return input; // 返す
	}
	
	/**
	 * 復号化(こちらのメソッドから)
	 * @param SendToID 相手のID
	 * @param recievekey 受信した暗号化された文字列
	 * @return 復号化されたもの(BigInteger型)
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 * @author kinbara
	 */
	public BigInteger DecryptWithPreSharedKey(int SendToID,String recievekey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException{
		byte[] reKey = new byte[MESSAGE_LENGTH];
		String secret_key = getPreSharedKey(SendToID); // PreSharedKeyを暗号化の鍵にする
		reKey = toByteArray(recievekey, MESSAGE_LENGTH); // 暗号化された文字列をバイト列に変換
		BigInteger key = decrypt(reKey, secret_key, crypt_spec); // 復号化する
		return key; // 返す
	}
	
	/**
	 * 暗号化の際に用いるメソッド(実際に暗号化をしているメソッド)
	 * @param text 暗号化するもの
	 * @param secret_key 暗号化に用いる鍵
	 * @param crypt_spec 暗号化の形式(AES)
	 * @return 暗号化されたもの(バイト)
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 * @throws BadPaddingException
	 * @author kinbara
	 */
	private byte[] encrypt(BigInteger text, String secret_key, String crypt_spec)
	        throws InvalidKeyException, IllegalBlockSizeException, IOException,
	        BadPaddingException {

		byte[] key = new byte[MESSAGE_LENGTH];
		key = toByteArray(secret_key,16); // secret_keyをバイトへ変換
		SecretKey sKey = new SecretKeySpec(key, 0, 16, crypt_spec); // 鍵を作成
	    byte[] secret = new byte[MESSAGE_LENGTH];

	    try {
	        Cipher cipher = Cipher.getInstance(crypt_spec); // cipherを生成
	        cipher.init(Cipher.ENCRYPT_MODE, sKey); // 鍵を使用して初期化
	        secret = cipher.doFinal(text.toByteArray()); // 暗号化
	    } catch (Exception e) { // エラーの時
	        System.out.println(this.getClass().getName()
	                + ".encrypt: Exception:");
	        e.printStackTrace();
	    }
	    return secret;
	}

	/**
	 * 復号化の際に用いるメソッド(実際に復号化をしているメソッド)
	 * @param encrypt_byte 暗号化されたバイト列
	 * @param secret_key 暗号化に用いる鍵
	 * @param crypt_spec 暗号化の形式(AES)
	 * @return 復号化されたもの
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 * @throws BadPaddingException
	 * @author kinbara
	 */
	private BigInteger decrypt(byte[] encrypt_byte, String secret_key, String crypt_spec)
	        throws InvalidKeyException, IllegalBlockSizeException, IOException,
	        BadPaddingException {

		byte[] key = new byte[MESSAGE_LENGTH];
		//key = secret_key.getBytes();
		key = toByteArray(secret_key,16); // secret_keyをバイトへ変換
	    SecretKey sKey = new SecretKeySpec(key, 0, 16, crypt_spec); // 鍵を生成
	    byte[] secret = new byte[MESSAGE_LENGTH];

	    try {
	        Cipher cipher = Cipher.getInstance(crypt_spec); // cipherを生成
	        cipher.init(Cipher.DECRYPT_MODE, sKey); // 鍵を使用して初期化
	        secret = cipher.doFinal(encrypt_byte); // 復号化
	    } catch (Exception e) { // エラーの時
	        System.out.println(this.getClass().getName()
	                + ".decrypt: Exception:");
	        e.printStackTrace();
	    }
	    return new BigInteger(secret);
	}
    
	/**
	 * バイト列をString型に変換するメソッド
	 * @param bytes 
	 * @param radix 基数
	 * @return バイト列をString型に変換したもの
	 * @author kinbara
	 */
    public static String dump(byte[] bytes, int radix) {
        return new java.math.BigInteger(1, bytes).toString(radix); // 変換、そして返す
    }

    /**
     * String型の文字列をバイト列に変換するメソッド
     * @param string
     * @param radix 基数
     * @return
     * @author kinbara
     */
    public static byte[] toByteArray(String string, int radix) {
    	
        byte[] bytes = new BigInteger(string, radix).toByteArray(); // 文字列をバイト列に変換
        if (bytes.length != 0 && bytes[0] == 0) {
            byte[] abs = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, abs, 0, abs.length);
            return abs;
        }
        else {
            return bytes;
        }
    }
}