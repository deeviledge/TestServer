package sglserver.keyexchange;

/**
 * SendToPeerクラス
 */
class SendToPeer extends IDetc {
	/**
	 * コンストラクタ
	 */
	public SendToPeer(){
		super();
	}
	/**
	 * 引数付コンストラクタ
	 * @param id ID
	 * @param al alt
	 * @param ip IP
	 * @param rip RelayIP
	 */
	public SendToPeer(int id,int al,String ip,String rip){
		super(id,al,ip,rip);
	}
	/**
	 * 自分自身を返す
	 * @return SendToPeer
	 */
	public SendToPeer getSendToPeer(){
		return(this);
	}
}