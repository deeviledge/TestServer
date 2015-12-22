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
class UserBehavior{
	int id;
	int alt;
	private List represent;
	private List roundBehavior;
	
	/**
	 * 引数なしコンストラクタ
	 */
	public UserBehavior(){
		
	}
	/**
	 * コンストラクタ
	 * @param id_
	 * @param alt_
	 */
	public UserBehavior(int id_ ,int alt_){
		id = id_;
		alt = alt_;
		represent = new ArrayList();
		roundBehavior = new ArrayList();
	}
	
	/**
	 * 
	 * @param us
	 */
	public void addRepresent(User us){
		represent.add(us);
	}

	/**
	 * 
	 * @param rb
	 * @param i
	 */
	public void addBehavior(RoundBehavior rb, int i){
		while (roundBehavior.size() < i)
			roundBehavior.add(null);
		roundBehavior.add(i,rb);
	}
	
	/**
	 * 
	 * @return
	 */
	public List getBehavior(){
		return roundBehavior;
	}

	/**
	 * 
	 * @return
	 */
	public List getRepresent(){
		return represent;
	}
	
}