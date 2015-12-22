package sglserver.groupadmin;
/**
 * 
 * 配送指令書を作成するクラス
 * comment:nishimura
 */
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class generateXML{
	private static int GroupID = 0;
	public Document document;
	private int MaxLevel;
	public int DoneRound = 0;
	//private int GroupID = 1;
	public generateXML(){
		//this("notitle.xml",null);
	};
	
	public generateXML(String FileName, Group_Key_Infomation gkinfo,String gname){
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();

			Document document = builder.newDocument();
			document = exportMandateXML(document,gkinfo,gname);
			
			saveFile(document , FileName);

		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	
	public Document exportMandateXML(Document doc, Group_Key_Infomation gkinfo,String gname){
/*
 * ここから保存部分。
 */
		Element GKI = (Element)doc.createElement("Mandate");
		doc.appendChild(GKI);


		Element keyagreement = doc.createElement("KeyAgreement");
		GKI.appendChild(keyagreement);
		
		Element gc = doc.createElement("Group");
		Attr gcattr = doc.createAttribute("xmlns:Count");
		gcattr.setValue(String.valueOf(gkinfo.getGroupCount()));
		gc.setAttributeNode(gcattr);
		
		keyagreement.appendChild(gc);
		
		//ピアを分離。ALTの設定。
		gkinfo = SetUserALT(gkinfo);
		//グループレベルを設定。小さい方から完成させていく。
		gkinfo = SetGroupsLevel(gkinfo);

		List UsersBehavior = new ArrayList();
		
		for(int k=0; k<gkinfo.getGroupSize(); k++){
			
		//ユーザー分の動作オブジェクトを作る
		for (int i=0 ; i < gkinfo.getGroup(k).getListSize() ; i++){
			UserBehavior userBehave = new UserBehavior(
					gkinfo.getGroup(k).getUser(i).getID() ,
					gkinfo.getGroup(k).getUser(i).getAlt());
			UsersBehavior.add(userBehave);
		};
		}
		setUsersBehavior(UsersBehavior,gkinfo);
		

		for(int k=0; k<gkinfo.getGroupSize(); k++){
		
		//Peerノードを作成
		for (int j=0 ; j < gkinfo.getGroup(k).getListSize() ; j++){
		Element peerElement = doc.createElement("Peer");
		Attr peerid = doc.createAttribute("xmlns:ID");
		peerid.setValue(String.valueOf(gkinfo.getGroup(k).getUser(j).getID()));
		Attr peeralt = doc.createAttribute("xmlns:alt");
		peeralt.setValue(String.valueOf(gkinfo.getGroup(k).getUser(j).getAlt()));
		peerElement.setAttributeNode(peerid);
		peerElement.setAttributeNode(peeralt);
		
		//ラウンドごとのピアの動作を付けていく。

		for (int r = 1; r < DoneRound + 1 ; r++){
			List behave = ((UserBehavior) UsersBehavior.get(j)).getBehavior();
				RoundBehavior r_b = null;
				if (behave.size() > r)
					r_b = (RoundBehavior) behave.get(r);
				Element roundnode = doc.createElement("Round");
				Attr roundattr = doc.createAttribute("round");
				roundattr.setValue(String.valueOf(r));
				roundnode.setAttributeNode(roundattr);
				//roundノード完成
				Element optionnode = doc.createElement("Option");
				optionnode.appendChild(doc.createTextNode("AntiDishonestInsider"));
				roundnode.appendChild(optionnode);
				
				Element behavenode = doc.createElement("Behavior");
				Element groupidnode = doc.createElement("GroupID");
				Element groupnamenode = doc.createElement("GroupName");
				
				Element sendtonode = doc.createElement("SendTo");
				Element recfromnode = doc.createElement("ReceiveFrom");
				
				
				if (r_b == null){
					behavenode.appendChild(doc.createTextNode("Wait"));
				}else{
					behavenode.appendChild(doc.createTextNode("Exchange"));
					
					for (int sen = 0 ; sen < r_b.getReceiver().size() ;sen ++){
					Element peerN = setPeerNode(doc,r_b,sen);
					sendtonode.appendChild(peerN);
					}
					
					if (r_b.getSender() != null){
					Element idnode = doc.createElement("ID");
					idnode.appendChild(doc.createTextNode(String.valueOf(r_b.getSender().getID())));

					Element altnode = doc.createElement("alt");
					altnode.appendChild(doc.createTextNode(String.valueOf(r_b.getSender().getAlt())));

					Element ipnode = doc.createElement("IP");
					ipnode.appendChild(doc.createTextNode(r_b.getSender().getIP()));
					
					Element ripnode = doc.createElement("RelayIP");
					ripnode.appendChild(doc.createTextNode(r_b.getSender().getRelayIP()));

					
					recfromnode.appendChild(idnode);
					recfromnode.appendChild(altnode);
					recfromnode.appendChild(ipnode);
					recfromnode.appendChild(ripnode);
					
					if (r_b.getGroupID() != 0)
					groupidnode.appendChild(doc.createTextNode(String.valueOf(r_b.getGroupID())));

					groupnamenode.appendChild(doc.createTextNode( gname ) );
					//groupnamenode.appendChild(doc.createTextNode("red"));
					}
					
					
					
				}
					
				
				roundnode.appendChild(behavenode);
				roundnode.appendChild(groupidnode);
				roundnode.appendChild(groupnamenode);
				roundnode.appendChild(sendtonode);
				roundnode.appendChild(recfromnode);
			peerElement.appendChild(roundnode);
			}
		
		
		keyagreement.appendChild(peerElement);
		
		};
		
		
		/*
  		System.out.println(gkinfo.getGroup(0).getGroupLevel());
		System.out.println(gkinfo.getGroup(1).getGroupLevel());
		System.out.println(gkinfo.getGroup(2).getGroupLevel());
		System.out.println(gkinfo.getGroup(3).getGroupLevel());
		 */
		//Element gpr = getTreeDocument(xmldoc,GKI,gki.getGroup(0));
		
		Attr kaattr=doc.createAttribute("xmlns:ID");
		kaattr.setValue(String.valueOf(doc.hashCode()));
		
		keyagreement.setAttributeNode(kaattr);
		
		}
		return doc;
	}
	
	
	public Group_Key_Infomation SetUserALT(Group_Key_Infomation gk){
		
		for(int k=0; k<gk.getGroupSize(); k++){
		
		ArrayList UniqueUserList = new ArrayList();
		ArrayList UniqueUserNum  = new ArrayList();
		int userCount = gk.getGroup(k).getListSize();

		for (int i=0 ; i < userCount ; i++){
			User us = gk.getGroup(k).getUser(i);
			if (UniqueUserList.indexOf(String.valueOf(us.getID())) == -1){
				UniqueUserList.add(String.valueOf(us.getID()));
				UniqueUserNum.add("1");
			}else{
				int num = Integer.parseInt((String) UniqueUserNum.get(UniqueUserList.indexOf(String.valueOf(us.getID()))));
				num++;
				
				UniqueUserNum.set(UniqueUserList.indexOf(String.valueOf(us.getID())) , String.valueOf(num));
			}
			
			us.setAlt(
					Integer.parseInt(
							(String) UniqueUserNum.get(UniqueUserList.indexOf(String.valueOf(us.getID())))));
							
		}
		
		/*
		System.out.println("0:" + (String) UniqueUserNum.get(0));
		System.out.println("1:" + (String) UniqueUserNum.get(1));
		System.out.println("2:" + (String) UniqueUserNum.get(2));
		*/	
		
		}
		return gk;
	}
/*
 * Group_Key_Infomationを与えると、所属するグループにレベルを与えて返す関数。
 * Childがいない、最深部が1で後は一つずつ増えていく。
 */
	public Group_Key_Infomation SetGroupsLevel(Group_Key_Infomation gk){
		
		int level = 1;
		boolean AllDone = false;
		
		for (int i=0 ; i < gk.getGroupSize(); i++){
			// childがいないのを調べる
			if (gk.getGroup(i).getGroupListSize() == 0){
				gk.getGroup(i).setGroupLevel(level);
		
			}
		}	
		while (!AllDone){
			AllDone = true;
			for (int j=0 ; j < gk.getGroupSize(); j++){
				if (gk.getGroup(j).getGroupLevel() == level){
					if (gk.getGroup(j).getParentGroup() != null)	
						if (gk.getGroup(j).getParentGroup().getGroupLevel() < level + 1){
							gk.getGroup(j).getParentGroup().setGroupLevel(level+1);
							AllDone = false;
						};
				};
			}

			level++;			
		}
		MaxLevel = level;
		
		return gk;
	}
	
	/*
	 * ユーザーの動作を設定するルート関数
	 */
	public List setUsersBehavior(List usersbe ,Group_Key_Infomation gk){
		
		for (int i=1; i<MaxLevel; i++)
		usersbe = setUsersBehaviorSameLeve(usersbe,gk,i);
		
		
		return usersbe;
	}
	
	/*
	 * あるLevelのグループに所属するための設計図を作る関数。
	 */
	public List setUsersBehaviorSameLeve(List usersbe ,Group_Key_Infomation gk,int lv){
		int thisMax = DoneRound;
		int MaxR = 0;
		
		for (int i=0 ; i<gk.getGroupSize(); i++){
			if (gk.getGroup(i).getGroupLevel() == lv){
				//該当するグループ.今後はこれに処理を加えていく。
				Group GP = gk.getGroup(i);
				//まず、直接所属するピアとグループのリストを取得する。
				List children = new ArrayList();
				
				
				//グループを追加
				for (int j=0 ; j<gk.getGroupSize(); j++){
					if (gk.getGroup(j).getParentGroup() != null){
						if (gk.getGroup(j).getParentGroup().getGroupName().equals(GP.getGroupName())){
							//自分の直の子供グループ
							int al = 1;
								for (int u=0; u<gk.getGroup(i).getListSize() ; u++){
									if ((gk.getGroup(j).getUser(u).getID() == gk.getGroup(i).getUser(u).getID())){
										if (gk.getGroup(i).getUser(u).getAlt() == al){
											children.add(gk.getGroup(i).getUser(u));
										}else{al++;}
										
									}
								}
							//一番最初のユーザを代表させる。
							}}
				}
				
				//ピアを追加
				for (int k=0 ; k<gk.getGroup(i).getListSize() ; k++){
					//
					if (gk.getGroup(i).getUser(k).getParentGroup() != null)
					
					//ピア全員に対して
					if (gk.getGroup(i).getUser(k).getParentGroup() != null)
						if (gk.getGroup(i).getUser(k).getParentGroup().getGroupName() == GP.getGroupName()){
							//自分の直の子供ピア
							children.add(gk.getGroup(i).getUser(k));
						}
					
				}
				
				//この時点でchildrenには、このグループを構成するために必要なグループとピアのリストがある。
				System.out.print("Group :" + GP.getGroupName() + "のサイズは");
				System.out.println(children.size());
				
				
				
				
				//ここから元の
				
/*			//ピア or グループの数を、2のべき乗にする。
				int beki;
				int bekiValue = 1;
				
				for (beki=0 ; bekiValue<children.size()+1 ; beki++)
					bekiValue = bekiValue * 2;
				
				bekiValue = bekiValue / 2;
				
				for (int unum=bekiValue+1 ; unum<children.size()+1; unum++){
					//2のべき上の枠に入りきらない人
					
					//uname の人の振る舞いは
					RoundBehavior rb1 = new 
					RoundBehavior(((User) children.get(unum-1)).getID() ,
							      ((User) children.get(unum-1)).getAlt());
					rb1.setSender(((User) children.get(unum - bekiValue-1)));
					rb1.addReciever(((User) children.get(unum - 1)));
					
					//uname - Value の人の振る舞いは
					RoundBehavior rb2 = new 
					RoundBehavior(((User) children.get(unum - bekiValue-1)).getID() ,
							      ((User) children.get(unum - bekiValue-1)).getAlt());
					rb2.setSender(((User) children.get(unum-1)));
					rb2.addReciever(((User) children.get(unum - bekiValue - 1)));
					//unameを今後代表する。
					((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) children.get(unum - bekiValue- 1))))).addRepresent((User) children.get(unum-1));
					
					// これらをどこに入れるか。
					((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) children.get(unum - 1))))).addBehavior(rb1,thisMax + 1);
					((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) children.get(unum - bekiValue-1))))).addBehavior(rb1,thisMax + 1);

					
				}*/
				
				//ここまで元の
				
				//ここから自分の
				
			
				int [][]A=new int[100000][2];
				
				int beki;
				int bekiValue = 1;
				
				int rnd;
				int k;
				int x;
				int j;
				int kabe;
				int a;
				
				A[1][0]=0;
				A[1][1]=children.size()-1;
			

				for (beki=0 ; bekiValue<children.size()+1 ; beki++)
					bekiValue = bekiValue * 2;
				//System.out.print(beki);
				//rnd=beki;
				
				//System.out.println(+rnd+"ラウンド");
				System.out.println("A[1][0]="+A[1][0]);
				System.out.println("A[1][1]="+A[1][1]);
				bekiValue = bekiValue / 2;
				//for(rnd=beki-1;rnd>0;rnd--){  //最大ラウンドから１ラウンドまで
					//System.out.println(+rnd+"ラウンド");
					for(k=1;k<beki;k++){  //ラウンド単位を増やす
						//System.out.println("iは"+i+"回目");
						
						//System.out.println(+(rnd-1)+"ラウンド");
						A[(int) Math.pow(2, k)][0]=0;
						System.out.println("A["+((int) Math.pow(2, k))+"][0]="+A[(int) Math.pow(2, k)][0]);
						//i=beki-rnd;
					
						for(x=0;x<((int) Math.pow(2,k));x++){//ラウンド単位の中で増やす
							
							if(x!=0){
								A[(int) Math.pow(2,k)+x][0]=A[(int) Math.pow(2,k)+x-1][1]+1;
								System.out.println("A["+((int) Math.pow(2,k)+x)+"][0]="+A[(int) Math.pow(2,k)+x][0]);
							}
							
							if(x%2==0){//もし
								
								if((A[(int) Math.pow(2,(k-1))+x/2][0]+A[(int) Math.pow(2,(k-1))+x/2][1])%2==0){
									
									A[(int) Math.pow(2,k)+x][1]=(A[(int) Math.pow(2,(k-1))+x/2][0]+A[(int) Math.pow(2,(k-1))+x/2][1])/2-1;
									
									System.out.println("A["+((int) Math.pow(2,k)+x)+"][1]="+A[(int) Math.pow(2,k)+x][1]);
								}
								
								else{
									A[(int) Math.pow(2,k)+x][1]=(A[(int) Math.pow(2,(k-1))+x/2][0]+A[(int) Math.pow(2,(k-1))+x/2][1]-1)/2;
									System.out.println("A["+((int) Math.pow(2,k)+x)+"][1]="+A[(int) Math.pow(2,k)+x][1]);
								}
								
								
							}
							
							else{
								A[(int) Math.pow(2,k)+x][1]=A[((int) Math.pow(2,k)+x-1)/2][1];
								System.out.println("A["+((int) Math.pow(2,k)+x)+"][1]="+A[(int) Math.pow(2,k)+x][1]);
							}
							
							
							
					
						
						
						
						
						
					}
						
					
				}
					
			
				
					//ここから実際に交換
					
					for(rnd=1;rnd<=beki;rnd++){
						
				
				for(j=1;j<=(int)Math.pow(2, beki)-1;j++){
					
					//もしピア数が2のi-1乗+1～2のi乗ならば
					
					if(((int)Math.pow(2, rnd-1)+1<=A[j][1]-A[j][0]+1)&&(A[j][1]-A[j][0]+1<=(int)Math.pow(2, rnd))){
					
						//もしピア数が偶数ならば
						
					if((A[j][0]+A[j][1])%2==0){
						kabe=(A[j][0]+A[j][1])/2;
						}
					
					//奇数なら
					else{
						kabe=(A[j][0]+A[j][1]+1)/2;
					}
					
					
					//もしピア数が一人でないなら
					if(A[j][0]!=A[j][1]){
						
						
						//順に交換
					for(a=0;A[j][0]+a<kabe;a++){
						
						System.out.println(+(A[j][0]+a)+"と"+(kabe+a)+"が交換");
							int userA=A[j][0]+a;
							int userB=kabe+a;
							int userC=A[j][1];
							RoundBehavior rbuserA = new 
							RoundBehavior(((User) children.get(userA)).getID() ,
									      ((User) children.get(userA)).getAlt());
							rbuserA.setSender(((User) children.get(userB)));
							rbuserA.addReciever(((User) children.get(userB)));
							//ダミーがいる時、いちばん左の人がダミーに送信する
							if(((A[j][1]-A[j][0])%2==0)&&(a==0)){
								
								rbuserA.addReciever(((User) children.get(userC)));
								}
							
							
							RoundBehavior rbuserB = new 
							RoundBehavior(((User) children.get(userB)).getID() ,
									      ((User) children.get(userB)).getAlt());
							rbuserB.setSender(((User) children.get(userA)));
							rbuserB.addReciever(((User) children.get(userA)));
							
							//Roundタグを表示する
							((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) children.get(userA))))).addBehavior(rbuserA ,thisMax + rnd);
							((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) children.get(userB))))).addBehavior(rbuserB ,thisMax + rnd);

							
						}
					
					//もし人数が奇数だったら一番右の人がダミーで、いちばん左の人から受け取る
					if((A[j][1]-A[j][0])%2==0){
						System.out.println(+A[j][1]+"が"+A[j][0]+"にもらう");
						int userC=A[j][1];
						int userA=A[j][0];
						
						
						Group G_Dummy = new Group();
						User dummy=new User("dummy", -1, ((User) children.get(userA)).getIP(), ((User) children.get(userC)).getRelayIP(), G_Dummy);
						
						
						
						RoundBehavior rbuserC = new 
						RoundBehavior(((User) children.get(userC)).getID() ,
								      ((User) children.get(userC)).getAlt());
						rbuserC.setSender((User) children.get(userA));
						rbuserC.addReciever(dummy);
						((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) children.get(userC))))).addBehavior(rbuserC ,thisMax + rnd);
					
						
						
						
						}
						
					
					
						
					}
					}
					}	
					}
				
					//ここまで自分の
					
				if (bekiValue != children.size())
				thisMax++;
				
				//今、2^{bekiValue} いる。これを組み立てる。
				int S2 = 1;
				
				for (rnd=1 ; rnd<beki; rnd++){
					
					//ここから元の
					
/*					for (int userC=0; userC<bekiValue; userC++){
					// rndラウンドでのiに関する動作
						if ((userC & (S2)) == 0){
							
						int senD = userC;
						int revD = (userC + S2) % bekiValue;
						
						
						RoundBehavior rbsenD = new 
						RoundBehavior(((User) children.get(userC)).getID() ,
								      ((User) children.get(userC)).getAlt());
						rbsenD.setSender(((User) children.get(revD)));
						rbsenD.addReciever(((User) children.get(revD)));
						//revDが代表している人をRecieverに追加
						List Reci1 = ((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) children.get(revD))))).getRepresent();
						//System.out.println(String.valueOf(revD) + "が代表するのは" + String.valueOf(Reci1.size()));
						
						for (int ad1 = 0 ; ad1 < Reci1.size();ad1++)
							rbsenD.addReciever((User) Reci1.get(ad1));
						
						RoundBehavior rbrevD = new 
						RoundBehavior(((User) children.get(revD)).getID() ,
								      ((User) children.get(revD)).getAlt());
						rbrevD.setSender(((User) children.get(senD)));
						rbrevD.addReciever(((User) children.get(senD)));

						List Reci2 = ((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) children.get(senD))))).getRepresent();
						//System.out.println(String.valueOf(senD) + "が代表するのは" + String.valueOf(Reci2.size()));
						for (int ad2 = 0 ; ad2 < Reci2.size();ad2++)
							rbrevD.addReciever((User) Reci2.get(ad2));

						((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) children.get(senD))))).addBehavior(rbsenD ,thisMax + rnd);
						((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) children.get(revD))))).addBehavior(rbrevD ,thisMax + rnd);

						//System.out.print("thisMax + rnd + 1 :");
						//System.out.println(thisMax + rnd + 1);

						//revDが代理している人のを設定。
						for (int ad3 = 0 ; ad3 < Reci1.size();ad3++)
						{
						RoundBehavior rbrevD2 = new 
						RoundBehavior(((User) Reci1.get(ad3)).getID() ,
								      ((User) Reci1.get(ad3)).getAlt());
						//userCから送られてきますよ。
						rbrevD2.setSender(((User) children.get(userC)));
						((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) Reci1.get(ad3))))).addBehavior(rbrevD2 ,thisMax + rnd);
						}
											
						//senDが代理している人のを設定。
						
						for (int ad4 = 0 ; ad4 < Reci2.size(); ad4++)
						{

							System.out.println("Reci2 = " + ((User) Reci2.get(ad4)).getID());
							
						RoundBehavior rbsenD2 = new 
						RoundBehavior(((User) Reci2.get(ad4)).getID() ,
								      ((User) Reci2.get(ad4)).getAlt());
						//userCから送られてきますよ。
						rbsenD2.setSender(((User) children.get(revD)));
						
						System.out.println("******  i=" + Reci2.size());
						((UserBehavior) usersbe.get(getChild2Num(i, gk, ((User) Reci2.get(ad4))))).addBehavior(rbsenD2 ,thisMax + rnd);
						}
						 
						}
				}*/
					//ここまで元の
					
					
					
					S2 = S2 * 2;
					MaxR = thisMax + rnd;
				}
				
				//ここから元の
				
				
/*				//このグループは完成。
				boolean rep = true;
				UserBehavior ub = null;
				for (int n=0; n < gk.getGroup(i).getListSize() ;n++){
					if (gk.getGroup(i).getUser(n).getParentGroup().getGroupName() == gk.getGroup(i).getGroupName()){
						((RoundBehavior) ((UserBehavior) usersbe.get(n)).getBehavior().get(MaxR)).setGroupID(GroupID);
						((RoundBehavior) ((UserBehavior) usersbe.get(n)).getBehavior().get(MaxR)).setGroupName(gk.getGroup(i).getGroupName());
						if (rep){
							rep = false;
							ub = (UserBehavior) usersbe.get(n);
						}else{
							if (ub.getRepresent().indexOf(gk.getGroup(i).getUser(n)) == -1){
								ub.addRepresent(gk.getGroup(i).getUser(n));
							}
						}
					}
	
				}
				
				GroupID++;
							*/
				
				
				//ここまで元の
			}
			
			if (MaxR > DoneRound)
				DoneRound = MaxR;
		}		
		System.out.println("MaxR" + String.valueOf(DoneRound));
		return usersbe;
	}
	
	public int getChild2Num(int k, Group_Key_Infomation gk,User us){

		int rel = -1;
		
		for (int i=0 ; i < gk.getGroup(k).getListSize(); i++){
			if ((gk.getGroup(k).getUser(i).getID() == us.getID()) &
					(gk.getGroup(k).getUser(i).getAlt() == us.getAlt())){
				rel = i;
			}
			//System.out.println("ID=" + gk.getGroup(k).getUser(i).getID() + " usID=" + us.getID() + "  rel=" + rel);
		}
		
		
		if (rel == -1){
			System.out.println("usID=" + us.getID());
			System.out.println("usalt=" + us.getAlt());

			for (int j=0 ; j < gk.getGroup(k).getListSize(); j++){
				System.out.println("ID=" + gk.getGroup(k).getUser(j).getID());
				System.out.println("alt=" + gk.getGroup(k).getUser(j).getAlt());
			}
		}
		
		return rel;
	}
	
	public Element setPeerNode(Document document, RoundBehavior rob, int i){
		Element peernode = document.createElement("Peer");
		Element idnode = document.createElement("ID");
		idnode.appendChild(document.createTextNode(String.valueOf(((User) rob.getReceiver().get(i)).getID())));
		
		Element altnode = document.createElement("alt");
		altnode.appendChild(document.createTextNode(String.valueOf(((User) rob.getReceiver().get(i)).getAlt())));

		Element ipnode = document.createElement("IP");
		ipnode.appendChild(document.createTextNode( ((User) rob.getReceiver().get(i)).getIP()));
		
		Element ripnode = document.createElement("RelayIP");
		ripnode.appendChild(document.createTextNode( ((User) rob.getReceiver().get(i)).getRelayIP()));
		
		
		
		peernode.appendChild(idnode);
		peernode.appendChild(altnode);
		peernode.appendChild(ipnode);
		peernode.appendChild(ripnode);
		
		return peernode;
	}
	
	public void saveFile(Document document, String FileName) throws Exception {
		//xml保存 変換
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		//xml保存
		DOMSource source = new DOMSource(document);
		File newXML = new File( FileName ); 
		FileOutputStream os = new FileOutputStream(newXML); 
		StreamResult result = new StreamResult(os); 
		transformer.transform(source, result);
	}


}	
