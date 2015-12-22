package sglserver.keyexchange;

/**
 * 
 */
class KeyCheck extends Thread {
	
	private int id1;		// 鍵交換を行うペア1
	private int id2;		// 鍵交換を行うペア2
	private String id1Key;	// id1が持つ共通鍵
	private String id2Key;	// id2 　　〃
	private String check;	//　check :"equal" or "not equal"
	
	public KeyCheck(){
		id1 = -1;
		id2 = -1;
		id1Key = null;
		id2Key = null;
		check = null;
	}
	// 一人目のidとその人が持っているkeyをセットする
	public void setid1Key(int id,String key){
		id1 = id;
		id1Key = key;
	}
	// 二人目のidとkeyをセットする
	public void setid2Key(int id,String key){
		id2 = id;
		id2Key = key;
	}
	// checkの結果を返す
	public String getCheck(){
		return(check);
	}
	public void run(){
		try{
			while(true){
				// id1とid2のkeyが値をもつとき
				if(id1Key!=null && id2Key!=null){
					// id1，id2となる二人が持つkeyが同じかどうか
					if(id1Key.equals(id2Key)){
						System.out.println("ID:"+ id1 +" Key == ID:"+ id2 +" Key.");
						check = "equal.";
					}else{
						System.out.println("ID:"+ id1 +" Key != ID:"+ id2 +" Key.");
						check = "not equal.";
					}
					// keyを初期化
					id1Key = null;
					id2Key = null;
					break;
				}
				sleep(300);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}