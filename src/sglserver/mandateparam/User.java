package sglserver.mandateparam;

import java.util.ArrayList;
import java.util.List;

/**
 * ユーザを表すクラス
 * @author Oda
 * @version 1.1
 * @作成日: 2004/12/28
 * @最終更新日:2008/10/31
 */
class User {
	private String Name;//ユーザーの名前を格納する
	private int ID;//ユーザーのIDを格納する
	private String IP;//ユーザーのIPアドレスを格納する
	private String RelayIP;//ユーザーの公開鍵を格納する
	private Group ParentGroup;
	private int ALT;
	private List viceList;//代理リスト

	
	/**
	 * 引数無しコンストラクタ
	 */
	public User(){
		this(null,-1,null,null,null);
	}

	/**
	 * 	// コンストラクタ
	 * @param n ユーザーの名前
	 * @param id ユーザーのID
	 * @param ip ユーザーのIPアドレス
	 * @param p ユーザーのリレーするIP
	 * @param g ユーザーの直の親グループ
	 */
	public User(String n,int id,String ip,String p,Group g){
		Name = n;
		ID = id;
		IP = ip;
		RelayIP = p;
		this.ParentGroup = g;
		viceList = new ArrayList();
	}
	
	/**
	 * このクラス自身を返す
	 * @return
	 */
	public User getUser(){
		return(this);
	}
	
	/**
	 * ユーザーの名前Nameを引数の値に設定
	 * @param s
	 */
	public void setName(String s){
		Name = s;
	}
	
	/**
	 * ユーザーの名前Nameを返す
	 * @return
	 */
	public String getName(){
		return(Name);
	}
	
	/**
	 * ユーザーのIDを引数の値に設定
	 * @param n
	 */
	public void setID(int n){
		ID = n;
	}
	
	/**
	 * ユーザーのIDを返す
	 * @return
	 */
	public int getID(){
		return(ID);
	}
	
	/**
	 * ユーザーのIPアドレスを引数の値に設定
	 * @param s
	 */
	public void setIP(String s){
		IP = s;
	}
	
	/**
	 * ユーザーのIPアドレスを返す
	 * @return
	 */
	public String getIP(){
		return(IP);
	}
	
	/**
	 * ユーザーのRelayIPを引数の値に設定
	 * @param s
	 */
	public void setRelayIP(String s){
		RelayIP = s;
	}
	
	/**
	 * ユーザーの公開鍵PublicKeyを返す
	 * @return
	 */
	public String getRelayIP(){
		return(RelayIP);
	}
	
	/**
	 * ユーザーの直の親グループを返す。
	 * @return
	 */
	public Group getParentGroup(){
		return(ParentGroup);
	}
	
	/**
	 * 
	 * @param i
	 */
	public void setAlt(int i){
		ALT = i;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getAlt(){
		return ALT;
	}
	
	/**
	 * 代理しないと行けない人のリストに追加
	 * @param user
	 */
	public void addUserVice(User user){
		viceList.add(user);
	}
	
	/**
	 * 代理しないと行けない人を返す
	 * @param i
	 * @return
	 */
	public User getUserVice(int i){
		return (User)viceList.get(i);
	}
}
