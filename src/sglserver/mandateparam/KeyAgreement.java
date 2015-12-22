package sglserver.mandateparam;

import java.util.ArrayList;
import java.util.List;


/**
 * KeyAgreementクラス
 */
public class KeyAgreement {
	/**
	 * 鍵交換ID
	 */
	private int ID;
	/**
	 * 最終的に出来るグループの総数
	 */
	private int GroupCount;
	/**
	 * Peerクラスの変数
	 */
	private Peer peer;
	/**
	 * Peerクラスのリスト
	 */
	private List PeerList;
	
	/**
	 * コンストラクタ
	 */
	public KeyAgreement(){
		this(-1,-1,new Peer());
	}
	/**
	 * コンストラクタ
	 * @param id 鍵交換ID
	 * @param gc GroupCount
	 * @param p Peer
	 */
	public KeyAgreement(int id,int gc,Peer p){
		// IDの設定
		ID = id;
		GroupCount = gc;
		if(p==null){
			peer = new Peer();
		}else{
			peer = p;
		}
		PeerList = new ArrayList();
	}
	/**
	 * 自分自身を返す
	 * @return KeyAgreement
	 */
	public KeyAgreement getKeyAgreement(){
		return(this);
	}
	/**
	 * KeyAgreementのIDを設定する
	 * @param id 鍵交換ID
	 */
	public void setID(int id){
		ID = id;
	}
	/**
	 * KeyAgreementのIDを返す
	 * @return 鍵交換ID
	 */
	public int getID(){
		return(ID);
	}
	/**
	 * GroupのCountの設定
	 * @param count 最終的に出来るグループの総数
	 */
	public void setGroupCount(int count){
		GroupCount = count;
	}
	/**
	 * GroupのCountを返す
	 * @return 最終的に出来るグループの総数
	 */
	public int getGroupCount(){
		return(GroupCount);
	}
	/**
	 * KeyAgreementのPeerを設定
	 * @param p Peer
	 */
	public void setPeer(Peer p){
		peer = p;
	}
	/**
	 * KeyAgreementのPeerを返す
	 * @return Peer
	 */
	public Peer getPeer(){
		return(peer);
	}
	/**
	 *
	 */
	public Peer getPeer(int id){
		for(int i=0;i<PeerList.size();i++){
			if(getPeerList(i).getID() == id){
				return( getPeerList(i) );
			}
		}
		System.out.println("getPeer Exception : ID="+ id +" Peer NotFound.");
		return(null);
	}
	/**
	 * KeyAgreementのPeerのリストに追加
	 * @param p Peer
	 */
	public void addPeerList(Peer p){
		PeerList.add( p );
	}
	/**
	 * KeyAgreementのPeerのリストを返す
	 * @retrun Peerの配列
	 */
	public Peer[] getPeerList(){
		return( (Peer[])PeerList.toArray() );
	}
	/**
	 * KeyAgreementの特定のPeerを返す
	 * @param index Peerのindex
	 * @return Peer
	 */
	public Peer getPeerList(int index){
		return( (Peer)PeerList.get(index) );
	}
	/**
	 * 
	 */
	public int getPeerListSize(){
		return( PeerList.size() );
	}
}
