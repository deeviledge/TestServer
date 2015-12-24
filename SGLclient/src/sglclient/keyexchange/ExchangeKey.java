/*
 * 作成日: 2006/11/14
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 * 
 * 最終更新日: 2007/07/30
 */
package sglclient.keyexchange;

import java.math.BigInteger;
import java.util.Random;

/**
 * 鍵生成クラス
 */
class ExchangeKey {
//	private ServerRandom sr;
	private BigInteger sk;
	private static BigInteger pk;
	private BigInteger key=null;
	private static BigInteger bi_modp;
	public static final String base = "2";
	public static final String modp = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE65381FFFFFFFFFFFFFFFF";

	public ExchangeKey(){
		
	}
	
	/**
	 * 秘密鍵を生成
	 *
	 */
	public void generateSecretKey(){
//		String str_sk = sr.getRandomnumber();
//		sk = new BigInteger( str_sk,16 );
		sk = new BigInteger( 1024,10, new Random());
	}
	/**
	 * 公開鍵を計算
	 *
	 */
	public void calculatePublicKey(){
		bi_modp = new BigInteger( modp,16 );
		pk = new BigInteger( base );
		pk = pk.modPow( sk,bi_modp );
	}
	/**
	 * 共通鍵を計算
	 * @param s:受信した相手の公開鍵
	 */
	public void calculateKey(String s){
		BigInteger bs = new BigInteger(s);
		key = bs.modPow( sk,bi_modp );
	}
	/**
	 * 秘密鍵を取得
	 */
	public BigInteger getSecretKey(){
		return(sk);
	}
	/**
	 * 公開鍵を取得
	 */
	public BigInteger getPublicKey(){
		return(pk);
	}
	/**
	 * 共通鍵を取得
	 */
	public BigInteger getKey(){
		return(key);
	}
	/**
	 * 秘密鍵を更新
	 * @param key:途中の共通鍵
	 */
	public void refreshKey(BigInteger key){
		sk = key;
	}
	/**
	 *  共通鍵を更新
	 *  Waitのとき使用
	 *  
	 */
	public void renewalKey(){
		key = sk;
	}
}