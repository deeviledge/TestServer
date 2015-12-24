package sglclient.certificate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import sglclient.myinformation.MyInformation;
import sglclient.option.EditOptionXml;

/**
 * SGLサーバにログインするクラス
 * @auhor masato,kryuu
 * @version 1.0 
 * @作成日: 2007/7/9
 * @最終更新日:2008/10/31
 */
public class SglLogin {
	String login = "login";        //SGLサーバへのログインワード
	String sglserverip = new EditOptionXml().getIP();
        public boolean login_flg;
        MyInformation mi;
        public String myid;
        public String[] deleteGroups;
	//BigInteger premastersecret; //プリマスタシークレット(ログイン認証時に取得。PreSharedKey配布に用いる)
	
	/**
	 * SGLサーバへログインします
	 * １．ユーザIDを送信
	 * ２．HandShakeProtocolを使用し認証を行う
	 * ３．PreSharedKeyを更新する
	 * ４．ログイン完了
	 * ５．グループ削除通知を受け取った場合はMyInformation.xmlからグループを削除する
	 */
	public SglLogin() {
            login_flg=false;
            mi = new MyInformation();
            myid = mi.getUsrID();
            deleteGroups = new String[1];
                
	}
        
        public void Login(){
            try {
			
			Socket soc = new Socket(sglserverip,12345);        //ソケットを生成する
			// 入出力ストリームを取得する
			ObjectOutputStream oos = new ObjectOutputStream(soc.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(soc.getInputStream());
			//ログイン要求
			oos.writeObject(login);     
			//IDを送信
			oos.writeObject(myid);    
			// 認証
			//LoginAttestationClient log = new LoginAttestationClient();
			//this.premastersecret = log.LoginAttestation(soc,ois,oos,this.sglserverip);
			//PreSharedKeyを更新
			//RegenerateKeyClient re = new RegenerateKeyClient();
			//re.RegeneratePSKeyClient(soc,ois,oos,sglserverip,this.premastersecret);
			//認証サイン(OK)を受け取る
			String result = (String)ois.readObject();   
			if(result.equals("OK")){
                            System.out.println("Log in SGLServer!");
                            login_flg=true;
                            //グループ削除
                            this.DeleteGroup(result,ois); 
			}
			else{
                            login_flg=false;
                            System.out.println("Can't rog in!");
			}
			oos.close();
			ois.close();      //ストリーム・ソケットを閉じる
			soc.close();
		}catch(Exception e){
			e.printStackTrace();
		}
        }
	
	/**
	 * グループ削除(他のユーザがグループを削除した場合)
	 * @param result
	 * @param ois
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @author kiryuu
	 */
	private void DeleteGroup(String result,ObjectInputStream ois) throws IOException, ClassNotFoundException{
            
            //GenerateXML gex = new GenerateXML();
            MyInformation MI = new MyInformation();
            deleteGroups = new String[MI.getGroupName().length];
            result = (String)ois.readObject();//受信
            if(result.substring(0, result.indexOf(":")).equals("delete")){
                int cnt=0;
                while(true){ // 探索
                    result = result.substring(result.indexOf(":"));
                    if(result.equals(":")==false){
			result = result.substring(1);
			String deletegroupname = result.substring( 0, result.indexOf(":") );// メッセージの抽出
			System.out.println("グループ" + deletegroupname + "を削除しました。");
			MI.removeGroupName(deletegroupname); // タグ削除
                        deleteGroups[cnt] = deletegroupname;
                    }else{
			System.out.println("グループ削除終了");
			break;
                    }
                    cnt++;
                }
            }else{
                System.out.println("削除の必要はありません");
            }
        }
}
