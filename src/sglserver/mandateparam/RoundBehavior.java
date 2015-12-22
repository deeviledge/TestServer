package sglserver.mandateparam;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Oda
 * @version 1.1
 * @作成日: 2004/12/28
 * @最終更新日:2008/10/31
 */
class RoundBehavior{
	private int id;
	private int alt;
	private User sender;
	private List reciever;
	private int GroupID;
	private String GroupName;
	
	/**
	 * 引数なしコンストラクタ
	 */
	public RoundBehavior(){
		
	}

	/**
	 * コンストラクタ
	 * @param id_
	 * @param alt_
	 */
	public RoundBehavior(int id_ , int alt_){
		id = id_;
		alt = alt_;
		GroupName = "";
		reciever = new ArrayList();
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setGroupName(String name){
		GroupName = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getGroupName(){
		return GroupName;
	}
	
	/**
	 * 
	 * @param i
	 */
	public void setGroupID(int i){
		GroupID = i;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getGroupID(){
		return GroupID;
	}
	
	/**
	 * 
	 * @param send
	 */
	public void setSender(User send){
		sender = send;
	}
	
	/**
	 * 
	 * @return
	 */
	public User getSender(){
		return sender;
	}
	
	/**
	 * 
	 * @param rec
	 */
	public void addReciever(User rec){
		reciever.add(rec);
	}
	
	/**
	 * 
	 * @return
	 */
	public List getReceiver(){
		return reciever;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getAlt(){
		return alt;
	}
}