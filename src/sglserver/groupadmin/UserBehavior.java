package sglserver.groupadmin;

import java.util.ArrayList;
import java.util.List;

class UserBehavior{
	int id;
	int alt;
	private List represent;
	private List roundBehavior;
	
	public UserBehavior(){
		
	}
	
	public UserBehavior(int id_ ,int alt_){
		id = id_;
		alt = alt_;
		represent = new ArrayList();
		roundBehavior = new ArrayList();
	}
	
	public void addRepresent(User us){
		represent.add(us);
	}

	public void addBehavior(RoundBehavior rb, int i){
		while (roundBehavior.size() < i)
			roundBehavior.add(null);
		roundBehavior.add(i,rb);
	}
	
	public List getBehavior(){
		return roundBehavior;
	}

	public List getRepresent(){
		return represent;
	}
	
}