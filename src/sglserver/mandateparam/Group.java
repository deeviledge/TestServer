package sglserver.mandateparam;

import java.util.ArrayList;
import java.util.List;

/**
 * グループを表すクラス
 * @author Oda
 * @version 1.1
 * @作成日: 2004/12/28
 * @最終更新日:2008/10/31
 */
public class Group {
	private String GroupName;//グループの名前を格納する
	private List GroupList;
	private List UserList;//所属するユーザーを格納するリスト
	private Group ParentGroup;//直接の親グループを返すリスト
	private User representUser;
	private int GroupLevel;
	
	/**
	 * 引数無しコンストラクタ
	 */
	public Group(){
		this( null );
	}
	
	/**
	 * コンストラクタ
	 * @param s グループの名前
	 */
	public Group(String s){
		GroupName = s;
		GroupList = new ArrayList();
		UserList = new ArrayList();
		representUser = null;
		GroupLevel = 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public Group getParentGroup(){
		return ParentGroup;
	}
	
	
	/**
	 * このクラス自身を返す
	 * @return
	 */
	public Group getGroup(){
		return(this);
	}
	
	/**
	 * グループの名前GroupNameを引数の値に設定
	 * @param s
	 */
	public void setGroupName(String s){
		GroupName = s;
	}
	
	/**
	 * グループの名前GroupNameを返す
	 * @return
	 */
	public String getGroupName(){
		return(GroupName);
	}
	
	/**
	 * グループのリストGroupListにグループGroupを追加する
	 * @param g
	 */
	public void addGroup(Group g){
		g.ParentGroup = this;
		GroupList.add(g);
	}
	
	/**
	 * グループのリストGroupListから、引数番目のグループGroupを返す
	 * @param i
	 * @return
	 */
	public Group getGroup(int i){
		return( (Group)GroupList.get(i) );
	}
	
	/**
	 * 文字列からグループを返す
	 * @param s
	 * @return
	 */
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
	
	/**
	 * グループのリストGroupListの要素数を返す
	 * @return
	 */
	public int getGroupListSize(){
		return( GroupList.size() );
	}
	
	/**
	 * ユーザーのリストUserListに、ユーザーUserを追加する
	 * @param u
	 */
	public void addUser(User u){
		UserList.add(u);
	}
	
	/**
	 * ユーザーのリストUserListから、引数番目のユーザーUserを返す
	 * @param i
	 * @return
	 */
	public User getUser(int i){
		return( (User)UserList.get(i) );
	}
	
	/**
	 * ユーザーのリストUserListの要素数を返す
	 * @return
	 */
	public int getListSize(){
		return(UserList.size());
	}
	
	/**
	 * 
	 * @param user
	 */
	public void setrepresentUser(User user){
		representUser = user;
	}
	
	/**
	 * 
	 * @return
	 */
	public User getrepresentUser(){
		return representUser;
	}
	
	/**
	 * 
	 * @param i
	 */
	public void setGroupLevel(int i){
		GroupLevel = i;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getGroupLevel(){
		return GroupLevel;
	}
}
