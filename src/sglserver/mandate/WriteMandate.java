
/**
 * MandateXML(鍵配送指令書)に情報を書き込むクラス
 * 
 * @auther fujino
 * @version 2.0
 * @作成日: 2008/6/1
 * @最終更新日:2008/11/14
 */

package sglserver.mandate;

import sglserver.groupinformation.ReadGroupInfoXML;

public class WriteMandate{
	
	private static boolean	debag=false;
	
	static int	t_cnt = 0;	// ラウンドカウント用変数
	
	static int	N;	// グループメンバ数
	static int	T;	// 総ラウンド数

	//static String	groupname = "size5";
	static String	mandatefile;
	static ReadGroupInfoXML gInfo;
	
	/**
	 * コンストラクタ
	 * 
	 * @param	filename	鍵配送指令書(Mandate)のファイル名
	 * 
	 * @auther fujino
	 * @最終更新日:2008/11/14
	 */
	public WriteMandate(String groupname, String filename){
		try {			
			mandatefile = filename;
			
			// 指定したグループの情報を読み込むクラス
			gInfo = new ReadGroupInfoXML(groupname);
			N = gInfo.getGroupValue();
			T = (int) Math.ceil( (Math.log(N)/Math.log(2)) );
			
			// 鍵交換を行う2者を割り出すためのメソッド
			exchange2Peer();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 鍵交換を行うペアを割り出すためのメソッド
	 * ただのメソッドの名前別けです
	 */
	public void exchange2Peer(){
		pearSearch(1, N);
	}
	
	/**
	 * 鍵交換のペアを割り出すためのメソッド
	 * 半分に割った際の「前半の1番目のユーザ」と「後半の1番目のユーザ」を割り出す
	 * 
	 * @param head	現在の範囲の1番目のユーザ
	 * @param end	現在の範囲の最後のユーザ
	 */
	static void pearSearch(int head, int end){

		/**
		 * @param i		前半の head を示す
		 * @param j		前半の end  を示す
		 * @param k		後半の head を示す
		 * @param l		後半の end  を示す
		 * @param t		ラウンドの深さ
		 * 
		 * @param fhalf_member	前半のユーザの範囲（人数）
		 * @param fhalf_member	後半のユーザの範囲（人数）
		 */
		int		i, j, k, l, t;
		int		fhalf_member, lhalf_member;
		boolean dummyflag=false;			// ダミーユーザがその範囲にいるかどうかのflag
		
		// 最小の範囲になったら終了
		if(end-head == 0)
			return;
		
		// ラウンドの深さを求める
		t=t_cnt;
		t_cnt++;
		
		// 前半部分の処理
		i = head;
		j = head + (end-(head-1))/2 - 1;
		fhalf_member = j-i;
		// 再帰（前半）
		pearSearch(i, j);

		// 後半部分の処理
		k = head + (end-(head-1))/2 - 1 + 1;
		l = end;
		lhalf_member = l-k;
		
		// 再帰（後半）
		pearSearch(k, l);

		// ダミーユーザが存在するかの判定
		// 前半と後半のユーザ数が異なるときダミーユーザは存在
		if(Math.abs(fhalf_member-lhalf_member)!=0)// && fhalf_member!=0)
			dummyflag = true;
		else
			dummyflag = false;
		
		// ラウンド数と範囲のチェック
		if(debag)
			if(dummyflag == true)
				System.out.println(
					"(t: i, j) = " + "(" +
					t + ": [" + 
					i + ", " + 
					j + "], [" +
					k + ", " + 
					l + "]" + ")" +
					" dummys exist."
				);
			else
				System.out.println(
					"(t: i, j) = " + "(" +
					t + ": [" + 
					i + ", " + 
					j + "], [" +
					k + ", " + 
					l + "]" + ")"
				);
		
		// 求めたユーザの範囲から鍵配送指令書に情報を書き込む
		new WriteMandateXML(t, i, k, dummyflag, gInfo, mandatefile);
		
		t_cnt--;
		return;
	}
}
