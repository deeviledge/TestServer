package sglserver.groupadmin;

import java.util.ArrayList;
import java.util.List;

//グループをあらわすクラス
class Group {
	private String GroupName;//グループの名前を格納する
	private List GroupList;
	private List UserList;//所属するユーザーを格納するリスト
	private Group ParentGroup;//直接の親グループを返すリスト
	private User representUser;
	private int GroupLevel;
	
	// 引数無しコンストラクタ
	public Group(){
		this( null );
	}
	// コンストラクタ
	// 引数はグループの名前
	public Group(String s){
		GroupName = s;
		GroupList = new ArrayList();
		UserList = new ArrayList();
		representUser = null;
		GroupLevel = 0;
	}
	
	public Group getParentGroup(){
		return ParentGroup;
	}
	// このクラス自身を返す
	public Group getGroup(){
		return(this);
	}
	// グループの名前GroupNameを引数の値に設定
	public void setGroupName(String s){
		GroupName = s;
	}
	// グループの名前GroupNameを返す
	public String getGroupName(){
		return(GroupName);
	}
	// グループのリストGroupListにグループGroupを追加する
	public void addGroup(Group g){
		g.ParentGroup = this;
		GroupList.add(g);
	}
	// グループのリストGroupListから、引数番目のグループGroupを返す
	public Group getGroup(int i){
		return( (Group)GroupList.get(i) );
	}
	// 文字列からグループを返す
	public Group getGroup(String s){
		for(int i=0; i<GroupList.size(); i++){
			System.out.println(String.valueOf(i) + ":" + s);
			if( getGroup(i).getGroupName().equals(s) ){
				return getGroup(i);
			}
		}
		System.out.println(s);
		
		return null;
	}
	// グループのリストGroupListの要素数を返す
	public int getGroupListSize(){
		return( GroupList.size() );
	}
	// ユーザーのリストUserListに、ユーザーUserを追加する
	public void addUser(User u){
		UserList.add(u);
	}
	// ユーザーのリストUserListから、引数番目のユーザーUserを返す
	public User getUser(int i){
		return( (User)UserList.get(i) );
	}
	// ユーザーのリストUserListの要素数を返す
	public int getListSize(){
		return(UserList.size());
	}
	
	public void setrepresentUser(User user){
		representUser = user;
	}
	
	public User getrepresentUser(){
		return representUser;
	}
	
	public void setGroupLevel(int i){
		GroupLevel = i;
	}
	
	public int getGroupLevel(){
		return GroupLevel;
	}
}
