package sglserver.keyexchange;
/**
 * グループメンバからあるラウンドの鍵交換終了の通知を受けとる
 * @author nishimura
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class KEControllServer {
	Socket soc;
	ObjectInputStream round_ois;
	public KEControllServer(){
		
	}
	/**
	 * ユーザからラウンド終了の通知を受け取る
	 * 
	 * @param ssoc
	 * @param users
	 * @return true:正しいグループメンバ false:グループメンバでないユーザが含まれる
	 * @throws Exception
	 */
	public boolean RoundControll(ServerSocket ssoc,String[] users) throws Exception{
		boolean member_flg=false;
		int cnt=0;
        while(true) {
        	try { 		  
        		soc = ssoc.accept();   // SGLクライアントからの接続を待つ
        		// 入出力ストリームを取得する
        		round_ois = new ObjectInputStream(soc.getInputStream());
        		String name = (String)round_ois.readObject();	//ここもPSKを使うと安全
        		for(int i=0;i<users.length;i++){
        			if(users[i].equals(name))member_flg=true;
        		}
        		if(member_flg==false){
                            System.out.println(name);
        			System.out.println("グループメンバーでないユーザが含まれます");
        			soc.close();	
        			round_ois.close();
        			return false;
        		}
        		else{
        			System.out.println(name+"から通知");
        		}
        	}catch(Exception e){
        		break;
        	}
        	member_flg=false;
        	cnt++;
        	if(cnt==users.length)break;
        }
        soc.close();	
		round_ois.close();
        return true;
	}
	
}
