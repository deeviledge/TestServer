package sglserver.groupadmin;

import java.util.ArrayList;
import java.util.List;

class RoundBehavior{
	private int id;
	private int alt;
	private User sender;
	private List reciever;
	private int GroupID;
	private String GroupName;
	
	public RoundBehavior(){
		
	}

	public RoundBehavior(int id_ , int alt_){
		id = id_;
		alt = alt_;
		GroupName = "";
		reciever = new ArrayList();
	}
	public void setGroupName(String name){
		GroupName = name;
	}
	
	public String getGroupName(){
		return GroupName;
	}
	
	public void setGroupID(int i){
		GroupID = i;
	}
	public int getGroupID(){
		return GroupID;
	}
	
	public void setSender(User send){
		sender = send;
	}
	
	public User getSender(){
		return sender;
	}
	
	public void addReciever(User rec){
		reciever.add(rec);
	}
	
	public List getReceiver(){
		return reciever;
	}
	
	public int getID(){
		return id;
	}
	
	public int getAlt(){
		return alt;
	}
}