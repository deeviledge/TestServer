package sglserver.keyexchange;

/**
 * ReceiveFromクラス
 */
class ReceiveFrom extends IDetc {
	/**
	 * コンストラクタ
	 */
	public ReceiveFrom(){
		super();
	}
	/**
	 * 引数付コンストラクタ
	 * @param id ID
	 * @param al alt
	 * @param ip IP
	 * @param rip RelayIP
	 */
	public ReceiveFrom(int id,int al,String ip,String rip){
		super(id,al,ip,rip);
	}
	/**
	 * 自分自身を返す
	 * @return ReceiveFrom
	 */
	public ReceiveFrom getReceiveFrom(){
		return(this);
	}
}