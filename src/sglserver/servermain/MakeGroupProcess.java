package sglserver.servermain;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import sglserver.groupadmin.GenerateMandateXML;
//import sglserver.groupadmin.GroupInformationXml;
import sglserver.groupinformation.GroupInformationXml;
import sglserver.groupinformation.UserSorting;
import sglserver.keyexchange.KEControllServer;
import sglserver.keyexchange.KEServer;
import sglserver.keyexchange.RoundServer;
/**
 * 
 * 鍵交換処理の基本クラス（上位階層クラス）
 * @author nishimura,okumura
 */
public class MakeGroupProcess {
	/************************定数宣言***********************/
	public static final int INPUT_STREAM_BUFFER = 512;	//入力ストリーム格納バッファサイズ
	public static final int FILE_READ_BUFFER = 512;	//ファイル読み込みバッファサイズ
	
	private final int KeyExchangePort = 12765;
	private final int RoundControllPort = 12767;
	
	static OutputStream outStream;	//送信用ストリーム
	static InputStream inStream;	//受信用ストリーム
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	public MakeGroupProcess(Socket soc,ObjectInputStream ois,ObjectOutputStream oos) throws Exception{
		System.out.println("\n◇◇◇◇◇◇◇◇◇◇◇グループ鍵共有プロセスを開始します◇◇◇◇◇◇◇◇◇◇◇");
		System.out.println("グループ名を読み込みます");
		String gname = (String)ois.readObject();//グループ名を受け取る
                System.out.println("グループメンバを読み込みます");
		ArrayList<?> userlist = (ArrayList<?>)ois.readObject();   //グループメンバを受け取る
                
		
                System.out.println("グループ名を表示します");
		System.out.println(gname);
                System.out.println("グループメンバを表示します");
		System.out.println(userlist);
                System.out.println("userlist:グループメンバの並び替えを開始：DynamicPeerInformation.xmlに記載された順(ID順)に並び替えます");
                //userlistをDynamicPeerInformation.xmlに記載された順(ID順)に並び替えるクラス：get_user_IDを作成します
                UserSorting us=new UserSorting(userlist);
                us.get_ID_num();               //IDを取得するメソッドを呼び出し
                us.sort();                     //取得したIDの順にリストを並べ替え
                ArrayList<?> sorted_list =us.get_sorted();   //並べ替えたリストを取得する
                System.out.println("並び替えが完了しました"); 
                System.out.println("DynamicPeerInformation.xmlに記載された順は以下の並びです");
                System.out.println(sorted_list);

                //グループ設計書の作成
                System.out.println("\n***********グループ設計書作成***********");
		GroupInformationXml gix = new GroupInformationXml();
		gix.GroupInformationXmlGenarator(gname, "Normal", sorted_list);
		gix.saveFile();
		
		// 鍵配送指令書の作成
		System.out.println("\n************鍵配送指令書作成************");
		GenerateMandateXML gmx = new GenerateMandateXML(gname);
		
		// 鍵交換
		System.out.println("\n****************鍵交換****************");
		KEServer kes = new KEServer(gname,KeyExchangePort);		//グループ情報と鍵配送指令書をグループメンバーに送信
		ServerSocket ssocket = new ServerSocket(RoundControllPort,100);	
		if(kes.member_flg){                                             //グループメンバーが揃っていれば鍵交換開始
                        //oos.writeObject("OK");
			for(int i=0;i<gmx.MaxRound;i++){					//最大ラウンドまで繰り返す
				KEControllServer KCS = new KEControllServer();			
				//グループメンバー全員から通知を受けたら次のラウンドの開始を知らせる
				if(KCS.RoundControll(ssocket, kes.names)){
					new RoundServer(KeyExchangePort,kes.names,"roundstart",i);
				}
				else{
					break;
				}
				System.out.println("Round"+(i+1)+" 終了");
			}
			KEControllServer KCS = new KEControllServer();	
			if(KCS.RoundControll(ssocket, kes.names)){
				new RoundServer(KeyExchangePort,kes.names,"finish",gmx.MaxRound);
			}
			ssocket.close();
		}
                /*else{                                   //グループメンバーがそろっていない場合は作成拒否
                    oos.writeObject("NG");              //(クライアントはログインユーザからメンバーを選ぶから普通はありえないけど）
                    
                }*/
                oos.writeObject("Finish");
		System.out.println("鍵交換を終了します");
                System.out.println("\n◆◆◆◆◆◆◆◆◆◆◆グループ鍵共有プロセスを終了します◆◆◆◆◆◆◆◆◆◆◆");
	}

	
	
	public void Wait() {
	    try {
	        Thread.sleep(1 * 300);
	    } catch (InterruptedException e) {
	    }
	}
	
}
