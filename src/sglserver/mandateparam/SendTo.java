package sglserver.mandateparam;

import java.util.ArrayList;
import java.util.List;

/**
 * SendToクラス
 */
public class SendTo {
	/**
	 * SendTo内のPeer sendToPeer
	 */
	private SendToPeer sendToPeer;
	/**
	 * Peerのリスト
	 */
	private List PeerList;
	/**
	 * コンストラクタ
	 */
	public SendTo(){
		this(new SendToPeer());
	}
	/**
	 * 引数付コンストラクタ
	 * @param stp sendToPeer
	 */
	public SendTo(SendToPeer stp){
		sendToPeer = stp;
		PeerList = new ArrayList();
	}
	/**
	 * 自分自身を返す
	 * @return SendTo
	 */
	public SendTo getSendTo(){
		return(this);
	}
	/**
	 * sendToPeerを設定
	 * @param stp 設定するSendToPeer
	 */
	public void setSendToPeer(SendToPeer stp){
		sendToPeer = stp;
	}
	/**
	 * SendToPeerを返す
	 * @return sendToPeer
	 */
	public SendToPeer getSendToPeer(){
		return(sendToPeer);
	}
	/**
	 * Peerのリストに追加
	 * @param p SendToPeer
	 */
	public void addPeerList(SendToPeer p){
		PeerList.add( p );
	}
	/**
	 * Peerのリストを返す
	 * @retrun SendToPeerの配列
	 */
	public SendToPeer[] getPeerList(){
		return( (SendToPeer[])PeerList.toArray() );
	}
	/**
	 * 特定のPeerを返す
	 * @param index Peerのindex
	 * @return SendToPeer
	 */
	public SendToPeer getPeerList(int index){
		return( (SendToPeer)PeerList.get(index) );
	}
}