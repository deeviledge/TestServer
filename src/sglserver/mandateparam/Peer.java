package sglserver.mandateparam;

import java.util.ArrayList;
import java.util.List;

/**
 * Peerクラス
 */
public class Peer {
	/**
	 * ピアID
	 */
	private int ID;
	/**
	 * alt
	 */
	private int alt;
	/**
	 * Roundクラス
	 */
	private Round round;
	/**
	 * Roundのリスト
	 */
	private List RoundList;
	
	/**
	 * コンストラクタ
	 */
	public Peer(){
		this(-1,-1,new Round());
	}
	/**
	 * コンストラクタ
	 * @param id ピアID
	 * @param al alt
	 * @param r Round
	 */
	public Peer(int id,int al,Round r){
		ID = id;
		alt = al;
		if(r==null){
			round = new Round();
		}else{
			round = r;
		}
		RoundList = new ArrayList();
	}
	/**
	 * 自分自身を返す
	 * @return Peer
	 */
	public Peer getPeer(){
		return(this);
	}
	/**
	 * PeerのIDを設定する
	 * @param id ピアID
	 */
	public void setID(int id){
		ID = id;
	}
	/**
	 * ピアIDを返す
	 * @return ピアID
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
	 * PeerのRoundを設定する
	 * @param r Round
	 */
	public void setRound(Round r){
		round = r;
	}
	/**
	 * PeerのRoundを返す
	 * @return Round
	 */
	public Round getRound(){
		return(round);
	}
	/**
	 * PeerのRoundのリストに追加
	 * @param r Round
	 */
	public void addRoundList(Round r){
		RoundList.add( r );
	}
	/**
	 * PeerのRoundのリストを返す
	 * @retrun Roundの配列
	 */
	public Round[] getRoundList(){
		return( (Round[])RoundList.toArray() );
	}
	/**
	 * Peerの特定のRoundを返す
	 * @param index Roundの番号
	 * @return Round
	 */
	public Round getRoundList(int index){
		return( (Round)RoundList.get(index) );
	}
	/**
	 * 
	 */
	public int getRoundSize(){
		return( RoundList.size() );
	}
}