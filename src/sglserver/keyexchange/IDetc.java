package sglserver.keyexchange;

/**
 * SendToPeerとReceiveFromに共通する要素のクラス
 */
class IDetc {
	/**
	 * ID
	 */
	private int ID;
	/**
	 * alt
	 */
	private int alt;
	/**
	 * IP
	 */
	private String IP;
	/**
	 * RelayIP
	 */
	private String RelayIP;
	/**
	 * コンストラクタ
	 */
	public IDetc(){
		this(-1,-1,null,null);
	}
	/**
	 * 引数付コンストラクタ
	 * @param id ID
	 * @param al alt
	 * @param ip IP
	 * @param rip RelayIP
	 */
	public IDetc(int id,int al,String ip,String rip){
		ID = id;
		alt = al;
		IP = ip;
		RelayIP = rip;
	}
	/**
	 * IDの設定
	 * @param id ID
	 */
	public void setID(int id){
		ID = id;
	}
	/**
	 * IDを返す
	 * @return ID
	 */
	public int getID(){
		return(ID);
	}
	/**
	 * Peerのaltを設定する
	 * @param al alt
	 */
	public void setalt(int al){
		alt = al;
	}
	/**
	 * altを返す
	 * @return alt
	 */
	public int getalt(){
		return(alt);
	}
	/**
	 * IPの設定
	 * @param ip IP
	 */
	public void setIP(String ip){
		IP = ip;
	}
	/**
	 * IPを返す
	 * @return IP
	 */
	public String getIP(){
		return(IP);
	}
	/**
	 * RelayIPの設定
	 * @param rip RelayIP
	 */
	public void setRelayIP(String rip){
		RelayIP = rip;
	}
	/**
	 * RelayIPを返す
	 * @return RelayIP
	 */
	public String getRelayIP(){
		return(RelayIP);
	}
}