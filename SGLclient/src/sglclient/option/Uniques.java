package sglclient.option;

/**
 * SGLサーバのIPを設定する
 * XMLファイルのパスを指定するクラス
 * @auhor masato,hujino,harada
 * @version 1.1
 * @作成日: 2007/7/9
 * @最終更新日:2008/12/09
 */
public class Uniques {

	String	serverIP = "133.66.164.55";
	String	myinfoFileName = "src/sglserver/conf/usr/xml_files/MyInformation.xml";
	
	EditOptionXml Eox = new EditOptionXml();
	String caIP = Eox.getIP();
	String Myinfo = Eox.getPass();
	
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
		if(serverIP != caIP	){
			serverIP = caIP;
			//System.out.println("serverIP:"+serverIP);
		}
			return serverIP;
	}
	/**
	 * Myinfomation.xmlのパスを取得
	 * @return Myinfomation.xmlのパス
	 * @author harada
	 */
	public String getMyinfoFileName(){
		if(myinfoFileName != Myinfo){
			myinfoFileName = Myinfo;
			//System.out.println("Myinfo:"+Myinfo);
		}
		return myinfoFileName;
		
	}
}
