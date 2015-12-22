package sglserver.option;

/**
 * SGLサーバのIPを設定する
 * XMLファイルのパスを指定するクラス
 * @auhor masato,hujino
 * @version 1.0 
 * @作成日: 2007/7/9
 * @最終更新日:2008/10/31
 */
public class Uniques {

	String	serverIP = "133.66.164.60";
	
	String	ginfoFileName  = "src/sglserver/conf/usr/xml_files/GroupInformation.xml";
	String	udinfoFileName = "src/sglserver/conf/usr/xml_files/DynamicPeerInformation.xml";
	
	/**
	 * コンストラクタ
	 */
	public Uniques(){	
	}
	
	/**
	 * SGLサーバのIPを取得
	 * @return SGLサーバのIP
	 * @author hujino
	 */
	public String getServerIP(){
		return serverIP;
	}
	
	/**
	 * GroupInformation.xmlの場所を取得
	 * @return
	 * @author hujino
	 */
	public String getGroupFileName(){
		return ginfoFileName;
	}
	
	/**
	 * DynamicPeerInformation.xmlの場所を取得
	 * @return
	 * @author hujino
	 */
	public String getDynamicinfoFileName(){
		return udinfoFileName;
	}
}
