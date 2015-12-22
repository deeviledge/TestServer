package sglserver.servermain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import sglserver.deletegroupinformation.DeleteGroupInfoXml;
import sglserver.dynamicpeerinformation.DynamicPeerInformation;
import sglserver.groupinformation.GroupInformationXml;
import sglserver.groupinformation.ReadGroupInfoXML;
import sglserver.groupsigniture.JoinServer;
import sglserver.groupsigniture.Open;
import sglserver.groupsigniture.SetUp;
import sglserver.groupsigniture.Verify;
import sglserver.groupsigniture.editKeydataXml;
import sglserver.mandate.GenerateMandateXML;
import sglserver.peerbasicinformation.PeerBasicInformationEdit;

/**
 * グループ編集を実行するクラス
 * @author masato
 * @version 1.6
 * @作成日: 2008/10/15
 * @最終更新日:2008/11/17
 */
public class EditGroupProcess {
	
	static String SGL_Accept_Message_EditGroup = "editgroup";	//グループ編集メッセージ
	static String clientip = null;				//SGLクライアントIP
	static Socket clientsoc = null;				//SGLクライアントソケット
	static ObjectOutputStream oos = null;		//アウトプットストリーム
	static ObjectInputStream ois = null;		//インプとストリーム
	static String req = "OK";					//SGLクライアントの認証反応
	static String res = "OK";					//グループ認証の総合判断
	static String username = null;				//グループメンバのユーザ名
	static String option = "AntiDishonestInsider";	//オプション設定
	private BigInteger[][] cer ;				//グループメンバの会員証明書
	private BigInteger[] sign ;					//グループ署名
	ArrayList beforeMember = new ArrayList();   //グループ編集後もグループに残るグループメンバリスト
	ArrayList newMember = new ArrayList();		//グループに新規追加されるメンバリスト
	
	/**
	 * コンストラクタ
	 * @param userlist
	 * @param afteruserlist
	 * @param dlist
	 * @param gname
	 * @param option
	 * @param gsjudge
	 * @param sign
	 * @throws Exception
	 */
	public EditGroupProcess(ArrayList userlist, ArrayList afteruserlist, 
			ArrayList dlist,String gname,String option,int gsjudge,BigInteger[] sign,String uid,String tname) throws Exception {
		
		boolean b1 = false;		
		req ="OK"; //初期段階の総合判断
		int okcount = 0;  //グループ認証判断が多数決の場合のOKカウント
		int ngcount = 0;   //グループ認証判断が多数決の場合のNGカウント
		System.out.println("編集前のグループ="+userlist);
		System.out.println("編集後のグループ="+afteruserlist);
		System.out.println("削除するメンバリスト="+dlist);
		
		//検証(VERIFY)を実行
		System.out.println("検証(VERIFY)しています***********************");
		Verify v = new Verify(gname,sign,SGL_Accept_Message_EditGroup);
		String verify = v.resverify();
		System.out.println("検証(VERIFY)結果"+verify+"***********************");
		if(verify.equals("OK")){
			//グループメンバの要求であれば以下を実行
			//新規ユーザとそれ以前のグループメンバに分類
			for(int i=0;i<afteruserlist.size();i++){
				b1 = false;
				String uname = (String) afteruserlist.get(i);
				for(int j=0;j<userlist.size();j++){
					String beforeuname = (String)userlist.get(j);
					if(uname.equals(beforeuname))
						b1 = true;
				}
				if(b1==true){
					beforeMember.add(uname);
				}else{
					newMember.add(uname);
				}
			}
			if(gsjudge==0|gsjudge==1){
				System.out.println(userlist);
				//認証方法が全会一致or過半数であれば以下を実行
				//編集以前のグループメンバ全員にグループ認証を実行
				for(int n=0; n<userlist.size(); n++){
					//グループメンバのIPを取得
					username = (String) userlist.get(n);
					System.out.println(username);
					DynamicPeerInformation dpi = new DynamicPeerInformation();
					clientip = dpi.getIP(username);
					//以前のグループメンバ一人の認証結果を受け取る
					req = ConnectGMember(gname,option,afteruserlist,gsjudge,tname);
					if(gsjudge==0){
						//認証方法が全会一致のときは認証結果がNGの場合、総合判断をNGに
						if(req.equals("NG"))
							res = "NG";
					}else if(gsjudge==1){
						//認証方法が過半数のときは認証結果がOKの場合、OKカウント1追加
						//                   認証結果がNGの場合、NGカウント1追加  
						if(req.equals("OK"))
							okcount++;
						else if(req.equals("NG"))
							ngcount++;				
					}
				}
				//認証方法が過半数のときはOKカウントとNGカウントの総数比較により総合判断を下す
				if(gsjudge==1){
					if(okcount==ngcount)
						res = "NG";
					else if(okcount>ngcount)
						res = "OK";
					else if(okcount<ngcount)
						res = "NG";
				}	
				
			}else if(gsjudge==2){	
				//認証方法がグループリーダによる判断であれば以下を実行
				//グループ編集申請者がグループリーダかどうかを確認するためOPENを実行
				System.out.println("開示(OPEN)しています***********************");
				Open open = new Open(gname,sign,SGL_Accept_Message_EditGroup); 
				String op = open.open();
				System.out.println("開示結果、署名者は"+op+"です");
				//グループリーダ名を取得
				ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
				String greaderid = rgi.getGroupLeader();
				PeerBasicInformationEdit pbi = new PeerBasicInformationEdit();
				String greader = pbi.getPeerName(greaderid);
				System.out.println("greader="+greader);
				if(greader.equals(op)){
					//グループリーダの正当な要求であれば以下を実行
					//総合判断をOKに
					res = "OK";
					//グループ編集前のグループメンバ全員にグループ編集を通知する
					for(int n=0; n<beforeMember.size(); n++){
						//グループ編集以前のグループメンバのIPを取得
						username = (String) beforeMember.get(n);
						DynamicPeerInformation dpi = new DynamicPeerInformation();
						clientip = dpi.getIP(username);
						ConnectGMember2(gname,option,afteruserlist,tname);
					}	
				}else{
					System.out.println("グループリーダーの権限なので実行できません");
					res = "NG";
				}
			}
			//新規追加ユーザにグループ編集の認証を実行
			for(int n=0; n<newMember.size(); n++){
				//グループメンバのIPを取得
				username = (String) newMember.get(n);
				DynamicPeerInformation dpi = new DynamicPeerInformation();
				clientip = dpi.getIP(username);
				req = ConnectNewMember(gname,option,afteruserlist,gsjudge,tname);
				//認証の結果、グループ編集がNGの場合、総合判断をNGに
				if(req.equals("NG"))
					res = "NG";
			}
			
			//グループ編集を実行するか、総合判断がOKであれば実行
			System.out.println("総合判断"+res+"***********************");
			if(res.equals("OK")){
				if(gsjudge==2){
					//認証方法がグループリーダによる判断であれば以下を実行
					//削除されたグループメンバにログイン時に通知するようIDをXMLに保存
					System.out.println("グループ削除通知リストに削除メンバを追加**********************************");
					String[] duserlist = new String[dlist.size()];
					for(int i=0;i<dlist.size();i++){ 
						duserlist[i] = (String) dlist.get(i);
						System.out.println(duserlist[i]);
					}
					DeleteGroupInfoXml dgi = new DeleteGroupInfoXml();
					dgi.makegroup(gname, duserlist);
					dgi.saveFile();
				}	
				//グループ公開鍵、秘密鍵を読み込む
				System.out.println("グループ公開鍵・秘密鍵を読み込んでいます**********************************");
				BigInteger[][] key = new BigInteger[3][7];
				cer = new BigInteger[userlist.size()+1][3];
				editKeydataXml ek = new editKeydataXml();
				key[1][1] = new BigInteger(ek.getn());		//n
				key[1][2] = new BigInteger(ek.geta());		//a
				key[1][3] = new BigInteger(ek.geta0());		//a0
				key[1][4] = new BigInteger(ek.gety());		//y
				key[1][5] = new BigInteger(ek.getg());		//g
				key[1][6] = new BigInteger(ek.geth());		//h
				key[2][1] = new BigInteger(ek.getp1());		//p1
				key[2][2] = new BigInteger(ek.getq1());		//q1
				key[2][3] = new BigInteger(ek.getx());		//x		
				//グループメンバとJoinを実行
				System.out.println("JOIN実行します***************************************************");
				JoinServer js = new JoinServer(afteruserlist,key,gname,String.valueOf(userlist.size()));
				cer = js.Join();	
				System.out.println("グループ管理ファイル作成***********************************************");
				// GroupInformation.xmlを作成
				GroupInformationXml	ginfo = new GroupInformationXml();
				ginfo.EditGroup(gname, option, afteruserlist, dlist, key, cer, uid);
				ginfo.saveFile();	
	
				// 鍵配送指令書の作成
				System.out.println("\n************鍵配送指令書作成*********************************");
				new GenerateMandateXML(gname);			
				// 鍵交換
				//System.out.println("\n****************鍵交換***************************************");
				//new KeyExchangeServer(gname);	
				//グループ署名用のグループ公開鍵・秘密鍵を補充
				editKeydataXml ekd = new editKeydataXml();
				ekd.deleteKey();
				//グループ署名(SetUp)実行
				System.out.println("SETUP実行します**************************************************");	
				SetUp su = new SetUp();
				su.Setup2();          //グループ公開鍵＆グループ秘密鍵	
				su.Setup2(); 
				System.out.println("終了***********************************************************");	
			}else{			
				//一人でも否定した場合はその場で終了、各SGLクライアントに通知
				System.out.println("グループ編集がキャンセルされました*************************************");
				for(int n=0; n<userlist.size(); n++){
					System.out.println(userlist);
					//グループメンバのIPを取得
					username = (String) userlist.get(n);
					DynamicPeerInformation dpi = new DynamicPeerInformation();
					clientip = dpi.getIP(username);
					//SGLクライアントに接続
					clientsoc = new Socket(clientip, 12346);
					//入出力ストリームを取得する
					oos = new ObjectOutputStream(clientsoc.getOutputStream());
					ois = new ObjectInputStream(clientsoc.getInputStream());
					oos.writeObject("editGroupcancel");	
				}	
			}
		}else{
			System.out.println("グループメンバからのグループ編集要求ではありません**********");
		}
	}
	
	/**
	 * グループ編集前のグループメンバ一人の認証結果を受け取る
	 * @param gname		グループ編集後のグループ名
	 * @param option	グループ編集後のオプション設定
	 * @param userlist	グループ編集後のグループメンバ
	 * @return req 		認証結果
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public String ConnectGMember(String gname,String option,ArrayList userlist,int gsjudge,String tname) throws UnknownHostException, IOException, ClassNotFoundException{
		String gsjudge2 = null;
		if(gsjudge==0){
			gsjudge2 = "全会一致で決定する";
		}else if(gsjudge==1){
			gsjudge2 = "多数決(過半数)によって決定する";
		}
		//SGLクライアントに接続
		clientsoc = new Socket(clientip, 12346);
		//入出力ストリームを取得する
		oos = new ObjectOutputStream(clientsoc.getOutputStream());
		ois = new ObjectInputStream(clientsoc.getInputStream());
		oos.writeObject("EditgroupTOGroupmember");		//メッセージ送信
		oos.writeObject(username);						//送信先のユーザ名
		oos.writeObject(gname);							//編集後のグループ名
		oos.writeObject(option);						//編集後のオプション設定
		oos.writeObject(gsjudge2);						//グループ認証方法設定
		oos.writeObject(tname);							//グループ関係
		oos.writeObject(userlist);						//グループメンバ
		//署名内容を受け取る
		sign = new BigInteger[7];
		sign = (BigInteger[])ois.readObject();			//署名受信
		String message = (String)ois.readObject();		//署名メッセージ受信
		//検証(VERIFY)を実行
		System.out.println("検証(VERIFY)しています*****************************");
		Verify v = new Verify(gname,sign,message);
		String verify = v.resverify();
		System.out.println("検証結果"+verify+"*******************************");
		if(verify.equals("OK")){
			if(message.equals("NO")){
				req = "NG";
			}
		}else{
			System.err.println("グループメンバからの認証ではありません***************");
			req = "NG";
		}
		System.out.println(username+"の認証結果"+req+"***********************");
		return req;
	}
	
	/**
	 * 新規追加ユーザ一人にグループ編集の認証を実行
	 * @param gname  	グループ編集後のグループ名
	 * @param option　	グループ編集後のオプション設定
	 * @param userlist　	グループ編集後のグループメンバ
	 * @return req 		認証結果
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public String ConnectNewMember(String gname,String option,ArrayList userlist,int gsjudge,String tname) throws UnknownHostException, IOException, ClassNotFoundException{
		String gsjudge2 = null;
		if(gsjudge==0){
			gsjudge2 = "全会一致で決定する";
		}else if(gsjudge==1){
			gsjudge2 = "多数決(過半数)によって決定する";
		}
		//SGLクライアントに接続
		clientsoc = new Socket(clientip, 12346);
		//入出力ストリームを取得する
		oos = new ObjectOutputStream(clientsoc.getOutputStream());
		ois = new ObjectInputStream(clientsoc.getInputStream());
		oos.writeObject("EditgroupTONewmember");	//メッセージ送信
		oos.writeObject(username);					//送信先のユーザ名
		oos.writeObject(gname);						//編集後のグループ名
		oos.writeObject(option);					//編集後のオプション設定
		oos.writeObject(gsjudge2);					//グループ認証方法設定
		oos.writeObject(tname);						//グループ関係
		oos.writeObject(userlist);					//編集後のグループメンバ
		if(tname==null){
			//グループ作成の反応を受信
			req = (String)ois.readObject();
		}else{
			//署名内容を受け取る
			sign = new BigInteger[7];
			sign = (BigInteger[])ois.readObject();			//署名受信
			String message = (String)ois.readObject();		//署名メッセージ受信
			//検証(VERIFY)を実行
			System.out.println("検証(VERIFY)しています*****************************");
			Verify v = new Verify(tname,sign,message);
			String verify = v.resverify();
			System.out.println("検証結果"+verify+"*******************************");
			if(verify.equals("OK")){
				if(message.equals("NO")){
					req = "NG";
				}
			}else{
				System.err.println("グループメンバからの認証ではありません***************");
				req = "NG";
			}
		}
		return req;
	}
	
	/**
	 * グループ編集前のグループメンバ一人にグループ編集を通知する
	 * @param gname		グループ編集後のグループ名
	 * @param option	グループ編集後のオプション設定
	 * @param userlist	グループ編集後のグループメンバ
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void ConnectGMember2(String gname,String option,ArrayList userlist,String tname) throws UnknownHostException, IOException, ClassNotFoundException{
		//SGLクライアントに接続
		clientsoc = new Socket(clientip, 12346);
		//入出力ストリームを取得する
		oos = new ObjectOutputStream(clientsoc.getOutputStream());
		ois = new ObjectInputStream(clientsoc.getInputStream());
		oos.writeObject("EditgroupTOGroupmember2");				//メッセージ送信
		oos.writeObject(username);								//送信先のユーザ名
		oos.writeObject(gname);									//編集後のグループ名
		oos.writeObject(option);								//編集後のオプション設定		
		oos.writeObject(tname);									//グループ関係
		oos.writeObject(userlist);								//編集後のグループメンバ
	}
}

//System.out.println("グループ名="+gname);
//System.out.println("編集前のグループ="+userlist);
//System.out.println("編集後のグループ="+afteruserlist);
//System.out.println("削除するメンバリスト="+dlist);
//System.out.println("グループメンバ="+beforeMember);
//System.out.println("新規ユーザ="+newMember);