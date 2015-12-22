package sglserver.keyexchange;

/**
 * Roundクラス
 */
class Round {
	/**
	 * ラウンド番号
	 */
	private int RoundNumber;
	/**
	 * 鍵交換基本オプション("Nomal","AntiDishonestInsider")
	 */
	private String Option;
	/**
	 * 動作("Exchange","Wait")
	 */
	private String Behavior;
	/**
	 * 結果として出来るグループID
	 */
	private int GroupID;
	/**
	 * 結果として出来るグループ名
	 */
	private String GroupName;
	/**
	 * SendToクラス
	 */
	private SendTo sendTo;
	/**
	 * ReceiveFromクラス
	 */
	private ReceiveFrom receiveFrom;
	/**
	 * コンストラクタ
	 */
	public Round(){
		this(-1,null,null,-1,null,null,null);
	}
	/**
	 * コンストラクタ
	 * @param ro ラウンド番号
	 * @param op 鍵交換基本オプション("Nomal","AntiDishonestInsider")
	 * @param be 動作("Exchange","Wait")
	 * @param gi 結果として出来るグループID
	 * @param gn 結果として出来るグループ名
	 * @param st SendToクラス
	 * @param rf ReceiveFromクラス
	 */
	public Round(int ro,String op,String be,int gi,String gn,SendTo st,ReceiveFrom rf){
		RoundNumber = ro;
		Option = op;
		Behavior = be;
		GroupID = gi;
		GroupName = gn;
		sendTo = st;
		receiveFrom = rf;
	}
	/**
	 * 自分自身を返す
	 * @return Round
	 */
	public Round getRound(){
		return(this);
	}
	/**
	 * ラウンド番号を設定
	 * @param ro ラウンド番号
	 */
	public void setRoundNumber(int ro){
		RoundNumber = ro;
	}
	/**
	 * ラウンド番号を返す
	 * @return ラウンド番号
	 */
	public int getRoundNumber(){
		return(RoundNumber);
	}
	/**
	 * 鍵交換基本オプション("Normal","AntiDishonestInsider")を設定
	 * @param op 鍵交換基本オプション("Nomal","AntiDishonestInsider")
	 */
	public void setOption(String op){
		if(op.equals("Normal") || op.equals("AntiDishonestInsider")){
			Option = op;
		}else{
			System.out.println("setOption Exception : "+ op +" is NotFound Option.");
		}
	}
	/**
	 * 鍵交換基本オプション("Nomal","AntiDishonestInsider")を返す
	 * @return 鍵交換基本オプション("Nomal","AntiDishonestInsider")
	 */
	public String getOption(){
		return(Option);
	}
	/**
	 * 動作("Exchange","Wait")の設定
	 * @param be 動作("Exchange","Wait")
	 */
	public void setbehavior(String be){
		if(be.equals("Exchange") || be.equals("Wait")){
			Behavior = be;
		}else{
			System.out.println("setbehavior Exception : "+ be +" is NotFound behavior.");
		}
	}
	/**
	 * 動作("Exchange","Wait")を返す
	 * @return 動作("Exchange","Wait")
	 */
	public String getbehavior(){
		return(Behavior);
	}
	/**
	 * 結果として出来るグループIDの設定
	 * @param gi 結果として出来るグループID
	 */
	public void setGroupID(int gi){
		GroupID = gi;
	}
	/**
	 * 結果として出来るグループIDを返す
	 * @return 結果として出来るグループID
	 */
	public int getGroupID(){
		return(GroupID);
	}
	/**
	 * 結果として出来るグループ名の設定
	 * @param gn 結果として出来るグループ名
	 */
	public void setGroupName(String gn){
		GroupName = gn;
	}
	/**
	 * 結果として出来るグループ名を返す
	 * @return 結果として出来るグループ名
	 */
	public String getGroupName(){
		return(GroupName);
	}
	/**
	 * SendToクラスの設定
	 * @param st 設定するSendToクラス
	 */
	public void setSendTo(SendTo st){
		sendTo = st;
	}
	/**
	 * SendToクラスを返す
	 * @return SendToクラス
	 */
	public SendTo getSendTo(){
		return(sendTo);
	}
	/**
	 * ReceiveFromクラスの設定
	 * @param rf 設定するReceiveFromクラス
	 */
	public void setReceiveFrom(ReceiveFrom rf){
		receiveFrom = rf;
	}
	/**
	 * ReceiveFromクラスを返す
	 * @return ReceiveFromクラス
	 */
	public ReceiveFrom getReceiveFrom(){
		return(receiveFrom);
	}
}