package sglserver.groupadmin;

import java.util.ArrayList;
import java.util.List;

//あるユーザーをあらわすクラス
class User {
	private String Name;//ユーザーの名前を格納する
	private int ID;//ユーザーのIDを格納する
	private String IP;//ユーザーのIPアドレスを格納する
	private String RelayIP;//ユーザーの公開鍵を格納する
	private Group ParentGroup;
	private int ALT;
	private List viceList;//代理リスト

	
	// 引数無しコンストラクタ
	public User(){
		this(null,-1,null,null,null);
	}
	// コンストラクタ
	// 第一引数の値はユーザーの名前
	// 第二引数の値はユーザーのID
	// 第三引数の値はユーザーのIPアドレス
	// 第四引数の値はユーザーのリレーするIP
	// 第五引数の値はユーザーの直の親グループ
	
	public User(String n,int id,String ip,String p,Group g){
		Name = n;
		ID = id;
		IP = ip;
		RelayIP = p;
	//	System.out.println(g.getGroupName());
		this.ParentGroup = g;
	//	System.out.println(ParentGroup.getGroupName());
		viceList = new ArrayList();
	}
	// このクラス自身を返す
	public User getUser(){
		return(this);
	}
	// ユーザーの名前Nameを引数の値に設定
	public void setName(String s){
		Name = s;
	}
	// ユーザーの名前Nameを返す
	public String getName(){
		return(Name);
	}
	// ユーザーのIDを引数の値に設定
	public void setID(int n){
		ID = n;
	}
	// ユーザーのIDを返す
	public int getID(){
		return(ID);
	}
	// ユーザーのIPアドレスを引数の値に設定
	public void setIP(String s){
		IP = s;
	}
	// ユーザーのIPアドレスを返す
	public String getIP(){
		return(IP);
	}
	// ユーザーのRelayIPを引数の値に設定
	public void setRelayIP(String s){
		RelayIP = s;
	}
	// ユーザーの公開鍵PublicKeyを返す
	public String getRelayIP(){
		return(RelayIP);
	}
	//ユーザーの直の親グループを返す。
	public Group getParentGroup(){
		return(ParentGroup);
	}
	
	public void setAlt(int i){
		ALT = i;
	}
	
	public int getAlt(){
		return ALT;
	}
	
	// 代理しないと行けない人のリストに追加
	public void addUserVice(User user){
		viceList.add(user);
	}
	
	//代理しないと行けない人を返す
	public User getUserVice(int i){
		return (User)viceList.get(i);
	}
}
