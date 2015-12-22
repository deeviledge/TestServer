/**
 * "GroupInformation.xml"から指定したグループのデータを読み込むクラス
 * @author fujino, masato　,kawata
 * @version 1.5
 * @作成日: 2008/11/05
 * @最終更新日:2008/12/20
 */

package sglserver.groupinformation;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sglserver.option.Uniques;

public class ReadGroupInfoXML{
	
	private Document	document;		// グループ情報のドキュメント
	private Element		root;			//     〃       のルートノード
	static String		groupname;
	static Element		group;
	
	Uniques		uni = new Uniques();
	//String		serverip = uni.getServerIP();
	//String 	UserDataFile = uni.getDynamicinfoFileName(); //　ユーザ情報があるファイルパス
	String		filename = uni.getGroupFileName();

	/**
	 * コンストラクタ
	 * Root要素にGroupInformationを持ったXML文書を読み込む
	 * @param filename			読み込みファイル
	 * @author masato
	 */
	public ReadGroupInfoXML(String gname){
		//GroupList = new ArrayList();
		try{
			//　ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();	
			FileInputStream fis = new FileInputStream(filename);
			
			// パースを実行してDocumentオブジェクトを取得(ファイル読み込み)
			document = builder.parse(new BufferedInputStream(fis));
			// ルート要素を取得
			root = document.getDocumentElement();
			
			NodeList list = root.getElementsByTagName("Group");
			for(int i=0; i<list.getLength(); i++){
				if( gname.equals(((Element)list.item(i)).getAttribute("xmlns:GroupName")) ){
					groupname = gname;
					group = (Element)list.item(i);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * コンストラクタ
	 * Root要素にGroupInformationを持ったXML文書を読み込む
	 * @param filename			読み込みファイル
	 * @author masato
	 */
	public ReadGroupInfoXML(){
		//GroupList = new ArrayList();
		try{
			//　ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();	
			FileInputStream fis = new FileInputStream(filename);
			
			// パースを実行してDocumentオブジェクトを取得(ファイル読み込み)
			document = builder.parse(new BufferedInputStream(fis));
			// ルート要素を取得
			root = document.getDocumentElement();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/***************************************************************************************************
	 ***************************************************************************************************
	 ***************************************************************************************************/
	
	
	
	/**
	 * グループメンバの人数を返す
	 * @return　int グループメンバの人数
	 * @author masato
	 */
	//public int getGroupValue(){
	//	NodeList ulist = group.getElementsByTagName("Peer");
	//	
	//	return (ulist.getLength());
	//}
	
	/**
	 * グループメンバの人数を返す
	 * @return　int グループメンバの人数
	 * @author masato
	 */
	public int getGroupValue(){
		ArrayList ulist = getMemberName2(groupname);
		
		return (ulist.size());
	}
	/**
	 * 値(グループメンバ値)を受け取ってグループメンバのユーザ名を返す
	 * @param num 値(グループメンバ値)
	 * @return グループメンバのユーザ名
	 * @author masato
	 */
	//public String getGroupMember(int num){
//
	//	NodeList ulist = group.getElementsByTagName("Peer");
	//	Element peer = (Element)ulist.item(num);
	//	Element name = (Element )peer.getElementsByTagName("Name").item(0);
	//	
	//	return (name.getFirstChild().getNodeValue());
	//}
	
	/**
	 * 値(グループメンバ値)を受け取ってグループメンバのユーザ名を返す
	 * @param num 値(グループメンバ値)
	 * @return グループメンバのユーザ名
	 * @author kawata
	 */
	public String getGroupMember(int num){
		ArrayList ulist = getMemberName2(groupname);
		
		return ((String)ulist.get(num));
	}
	
	/**
	 * 値(グループメンバ値)を受け取ってグループメンバのユーザIDを返す
	 * @param num 値(グループメンバ値)
	 * @return グループメンバのユーザ名
	 * @author fujino
	 */
	//public int getMemberID(int num){
	//	NodeList ulist = group.getElementsByTagName("Peer");
	//	Element peer = (Element)ulist.item(num);
	//	Element uid = (Element )peer.getElementsByTagName("ID").item(0);
	//	
	//	return (Integer.parseInt(uid.getFirstChild().getNodeValue()));
	//}
	/**
	 * 値(グループメンバ値)を受け取ってグループメンバのユーザIDを返す
	 * @param num 値(グループメンバ値)
	 * @return グループメンバのユーザ名
	 * @author kawata
	 */
	public int getMemberID(int num){
		ArrayList ulist = getGroupMember4(groupname);
		
		return (Integer.parseInt((String)ulist.get(num)));
	}
	
	/**
	 * グループメンバの全てのユーザ名を返す
	 * @return 全てのユーザ名
	 * @author masato
	 */
	//public String[] getMemberName(){
	//	String[] names = null;
//
	//	NodeList ulist = group.getElementsByTagName("Peer");
	//	names = new String[ulist.getLength()];
	//	
	//	for(int j=0; j<ulist.getLength(); j++){
	//		Element peer = (Element)ulist.item(j);
	//		NodeList unamelist = peer.getElementsByTagName("Name");
	////		Element name = (Element)unamelist.item(0);
	//		names[j] = name.getFirstChild().getNodeValue();
	//	}
	//	return (names);
	//}
	
	/**
	 * 選択されたグループメンバ全員のIDを返す
	 * @return グループメンバ全員のID
	 * @author kiryuu
	 */
	//public String[] getGroupMember3(String names2){//グループ名取得
	//	String[] names = null;
	//	
	//	NodeList ulist = group.getElementsByTagName("Peer");
	//	names = new String[ulist.getLength()];
	//	
	//	for(int i =0; i<ulist.getLength(); i++){
	//		Element peer = (Element)ulist.item(i);
	//		NodeList list2 = peer.getElementsByTagName("ID");
	//		Element name = (Element)list2.item(0);
	///	}
	//	return (names);
	//}
	
	/**
	 * 選択されたグループのメンバの名前を返す
	 * @return グループのメンバの名前
	 * @author masato 2008/11/07
	 */
	//public ArrayList getGroupMember4(){//グループ名取得
	//	ArrayList names = new ArrayList();	
	//	NodeList ulist = group.getElementsByTagName("Peer");
		//names = new String[list1.getLength()];
	//	
	//	for(int a =0; a<ulist.getLength(); a++){//list1のエレメントの個数でループさせる
	//		Element peer = (Element)ulist.item(a);
	//		NodeList list2 = peer.getElementsByTagName("Name");	//Nameをlist2に入れる
	//		Element name = (Element)list2.item(0);//上から０番目のＮａｍｅをnameに入れる
	//		//names[a] = name.getFirstChild().getNodeValue();
	//		names.add(name.getFirstChild().getNodeValue());
	//	}
	//	
	//	return (names);
	//}
	
	/**
	 * クループ名を受け取ってグループのオプションを返す
	 * @return	String option	オプション
	 * @author	fujino
	 * @作成日: 2008/11/14
	 */
	public String getGroupOption(){
		
		Element Option = (Element )group.getElementsByTagName("Option").item(0);
		
		return (Option.getFirstChild().getNodeValue());
	}
	
	/**
	 * クループ名を受け取ってグループのリーダを返す
	 * @return	String Leader	リーダID
	 * @author	kawata
	 * @作成日: 2008/12/22
	 */
	public String getGroupLeader(){
		
		Element Leader = (Element )group.getElementsByTagName("GroupLeader").item(0);
		
		return (Leader.getFirstChild().getNodeValue());
	}
	
	/**
	 * ユーザIDを受け取ってIPアドレスを返す
	 * @param	userID			ユーザID
	 * @return	String uIP		IPアドレス
	 * @author	fujino
	 * @作成日: 2008/11/14
	 */
	//public String getUserIP(int userID){
	//	
	//	NodeList ulist = group.getElementsByTagName("Peer");
	//	for(int i=0; i<ulist.getLength(); i++){
	//		Element User = (Element)ulist.item(i);
	//		String uid = User.getAttribute("xmlns:ID");
	//		if(userID == (Integer.parseInt(uid))){
	//			return (User.getElementsByTagName("IP").item(0).getFirstChild().getNodeValue()); 
	//		}
	//	}
	//	return null;
	//}
	
	/**
	 * ユーザIDを受け取ってRelayIPアドレスを返す
	 * @param	userID			ユーザID
	 * @return	String rIP		RelayIPアドレス
	 * @author	fujino
	 * @作成日: 2008/11/14
	 */
	//public String getRelayIP(int userID){
	//	
	//	NodeList ulist = group.getElementsByTagName("Peer");
	//	for(int i=0; i<ulist.getLength(); i++){
	//		Element User = (Element)ulist.item(i);
	//		String uid = User.getAttribute("xmlns:ID");
	//		if(userID == (Integer.parseInt(uid))){
	//			return (User.getElementsByTagName("RelayIP").item(0).getFirstChild().getNodeValue()); 
	//		}
	//	}
	//	return null;
	//}

	
/***************************************************************************************************
 ***************************************************************************************************
 ***************************************************************************************************/
	
	
	/**
	 * グループ公開鍵nを取得
	 * @return　n
	 * @author masato
	 */
	public String getn(){
		String n = null;
		
		NodeList pklist = group.getElementsByTagName("PublicKey");
		Element PK = (Element)pklist.item(0);
		NodeList list3 = PK.getElementsByTagName("n");
		Element nE = (Element)list3.item(0);
		n = nE.getFirstChild().getNodeValue();
		
		return n;
	}
	
	/**
	 * グループ公開鍵aを取得
	 * @return　a
	 * @author masato
	 */
	public String geta(){
		String a = null;
		
		NodeList pklist = group.getElementsByTagName("PublicKey");
		Element PK = (Element)pklist.item(0);
		NodeList list3 = PK.getElementsByTagName("a");
		Element aE = (Element)list3.item(0);
		a = aE.getFirstChild().getNodeValue();
		
		return a;
	}
	
	/**
	 * グループ公開鍵a0を取得
	 * @return　a0
	 * @author masato
	 */
	public String geta0(){
		String a0 = null;
		
		NodeList pklist = group.getElementsByTagName("PublicKey");
		Element PK = (Element)pklist.item(0);
		NodeList list3 = PK.getElementsByTagName("a0");
		Element a0E = (Element)list3.item(0);
		a0 = a0E.getFirstChild().getNodeValue();
		
		return a0;
	}
	
	/**
	 * グループ公開鍵yを取得
	 * @return　y
	 * @author masato
	 */
	public String gety(){
		String y = null;
		
		NodeList pklist = group.getElementsByTagName("PublicKey");
		Element PK = (Element)pklist.item(0);
		NodeList list3 = PK.getElementsByTagName("y");
		Element yE = (Element)list3.item(0);
		y = yE.getFirstChild().getNodeValue();
		
		return y;
	}
	
	/**
	 * グループ公開鍵gを取得
	 * @return　g
	 * @author masato
	 */
	public String getg(){
		String g = null;
		
		NodeList pklist = group.getElementsByTagName("PublicKey");
		Element PK = (Element)pklist.item(0);
		NodeList list3 = PK.getElementsByTagName("g");
		Element gE = (Element)list3.item(0);
		g = gE.getFirstChild().getNodeValue();
		
		return g;
	}
	
	/**
	 * グループ公開鍵hを取得
	 * @return　h
	 * @author masato
	 */
	public String geth(){
		String h = null;
		
		NodeList pklist = group.getElementsByTagName("PublicKey");
		Element PK = (Element)pklist.item(0);
		NodeList list3 = PK.getElementsByTagName("h");
		Element hE = (Element)list3.item(0);
		h = hE.getFirstChild().getNodeValue();
		
		return h;
	}
	
	/**
	 * グループ秘密鍵p1を取得
	 * @return　p1
	 * @author masato
	 */
	public String getp1(){
		String p1 = null;
		
		NodeList sklist = group.getElementsByTagName("SecretKey");
		Element SK = (Element)sklist.item(0);
		NodeList list3 = SK.getElementsByTagName("p1");
		Element p1E = (Element)list3.item(0);
		p1 = p1E.getFirstChild().getNodeValue();
		
		return p1;
	}
	
	/**
	 * グループ秘密鍵q1を取得
	 * @return　q1
	 * @author masato
	 */
	public String getq1(){
		String q1 = null;
		
		NodeList sklist = group.getElementsByTagName("SecretKey");
		Element SK = (Element)sklist.item(0);
		NodeList list3 = SK.getElementsByTagName("q1");
		Element q1E = (Element)list3.item(0);
		q1 = q1E.getFirstChild().getNodeValue();
		
		return q1;
	}
	
	/**
	 * グループ秘密鍵xを取得
	 * @return　x
	 * @author masato
	 */
	public String getx(){
		String x = null;

		NodeList sklist = group.getElementsByTagName("SecretKey");
		Element SK = (Element)sklist.item(0);
		NodeList list3 = SK.getElementsByTagName("x");
		Element xE = (Element)list3.item(0);
		x = xE.getFirstChild().getNodeValue();
		
		return x;
	}
	
	/**
	 * 会員証明書Aiを取得
	 * @param uname ユーザ名
	 * @return　Ai
	 * @author masato
	 */
	/*public String getAi(String uname){
		String Ai = null;
		
		NodeList ulist = group.getElementsByTagName("Peer");
		for(int j=0;j<ulist.getLength();j++){
			Element Peer = (Element)ulist.item(j);
			System.out.println(ulist.getLength());
			NodeList list3 = Peer.getElementsByTagName("Name");
			Element PeerName = (Element)list3.item(0);
			String username = PeerName.getFirstChild().getNodeValue();
			if(uname.equals(username)){
				NodeList list4 = Peer.getElementsByTagName("MembershipCertificates");
				Element MCer = (Element)list4.item(0);
				NodeList list5 = MCer.getElementsByTagName("Ai");
				Element AiE = (Element)list5.item(0);
				Ai = AiE.getFirstChild().getNodeValue();
			}
		}
		return Ai;
	}*/
	
	/**
	 * 会員証明書Aiを取得
	 * (grnameのAiを読み取る)
	 * @param grname グループ名
	 * @param uname  ユーザ名
	 * @param unm  　　 再帰回数(最初は0)
	 * 
	 * @return　Ai
	 * @author kawata
	 * @作成日: 2008/12/20
	 */
	public String getAi(String grname,String uname,int num){
		String Ai = null;
		boolean bl = false;
		NodeList list = root.getElementsByTagName("Group");//Groupリスト作成
		for(int k=0; k<list.getLength(); k++){
			Element group = (Element)list.item(k);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(grname)){//grnameと同じグループ名の要素を取り出す
				NodeList ulist = group.getElementsByTagName("Peer");
				for(int j=0;j<ulist.getLength();j++){
					Element Peer = (Element)ulist.item(j);
					NodeList list3 = Peer.getElementsByTagName("Name");
					Element PeerName = (Element)list3.item(0);
					String username = PeerName.getFirstChild().getNodeValue();
					if(uname.equals(username)){
						bl=true;
						NodeList list4 = Peer.getElementsByTagName("MembershipCertificates");
						Element MCer = (Element)list4.item(0);
						NodeList list5 = MCer.getElementsByTagName("Ai");
						Element AiE = (Element)list5.item(num);
						Ai=AiE.getFirstChild().getNodeValue();
					}
				}
				if(bl==false){
					num++;
					ArrayList Includegroup = new ArrayList();
					Includegroup = getIncludegroup2(grname);
					for(int i=0;i<Includegroup.size();i++){
						Ai = getAi((String)Includegroup.get(i),uname,num);
						if(Ai!=null){
							break;
						}
					}
				}
			}
		}
		return Ai;
	}

	/**
	 * 会員証明書Aiを取得
	 * (grname以上のすべてのグループのAiを読み取る)
	 * @param grname グループ名
	 * @param uname  ユーザ名
	 * @param unm  　　 再帰回数(最初は0)
	 * 
	 * @return　Ai
	 * @author kawata
	 * @作成日: 2008/12/20
	 */
	public ArrayList getAi2(String grname,String uname,int num){
		ArrayList Ai = new ArrayList();
		boolean bl = false;
		NodeList list = root.getElementsByTagName("Group");//Groupリスト作成
		for(int k=0; k<list.getLength(); k++){
			Element group = (Element)list.item(k);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(grname)){//grnameと同じグループ名の要素を取り出す
				NodeList ulist = group.getElementsByTagName("Peer");
				for(int j=0;j<ulist.getLength();j++){
					Element Peer = (Element)ulist.item(j);
					NodeList list3 = Peer.getElementsByTagName("Name");
					Element PeerName = (Element)list3.item(0);
					String username = PeerName.getFirstChild().getNodeValue();
					if(uname.equals(username)){
						bl=true;
						NodeList list4 = Peer.getElementsByTagName("MembershipCertificates");
						Element MCer = (Element)list4.item(0);
						NodeList list5 = MCer.getElementsByTagName("Ai");
						for(int i=num;i<list5.getLength();i++){
							Element AiE = (Element)list5.item(i);
							Ai.add(AiE.getFirstChild().getNodeValue());
						}
					}
				}
				if(bl==false){
					num++;
					ArrayList Includegroup = new ArrayList();
					Includegroup = getIncludegroup2(grname);
					for(int i=0;i<Includegroup.size();i++){
						Ai = getAi2((String)Includegroup.get(i),uname,num);
						if(Ai.size()!=0){
							break;
						}
					}
				}
			}
		}
		return Ai;
	}
	
	/**
	 * 会員証明書eiを取得
	 * @param uname ユーザ名
	 * @return　ei
	 * @author masato
	 */
	/*public String getei(String uname){
		String ei = null;

		NodeList ulist = group.getElementsByTagName("Peer");
		for(int j=0;j<ulist.getLength();j++){
			Element Peer = (Element)ulist.item(j);
			NodeList list3 = Peer.getElementsByTagName("Name");
			Element PeerName = (Element)list3.item(0);
			String username = PeerName.getFirstChild().getNodeValue();
			if(uname.equals(username)){
				NodeList list4 = Peer.getElementsByTagName("MembershipCertificates");
				Element MCer = (Element)list4.item(0);
				NodeList list5 = MCer.getElementsByTagName("ei");
				Element eiE = (Element)list5.item(0);
				ei = eiE.getFirstChild().getNodeValue();
			}
		}
		
		return ei;
	}*/
	
	/**
	 * 会員証明書eiを取得
	 * (grnameのeiを読み取る)
	 * @param grname グループ名
	 * @param uname  ユーザ名
	 * @param unm  　　 再帰回数(最初は0)
	 * 
	 * @return　ei
	 * @author kawata
	 * @作成日: 2008/12/20
	 */
	public String getei(String grname,String uname,int num){
		String ei = null;
		boolean bl = false;
		NodeList list = root.getElementsByTagName("Group");//Groupリスト作成
		for(int k=0; k<list.getLength(); k++){
			Element group = (Element)list.item(k);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(grname)){//grnameと同じグループ名の要素を取り出す
				NodeList ulist = group.getElementsByTagName("Peer");
				for(int j=0;j<ulist.getLength();j++){
					Element Peer = (Element)ulist.item(j);
					NodeList list3 = Peer.getElementsByTagName("Name");
					Element PeerName = (Element)list3.item(0);
					String username = PeerName.getFirstChild().getNodeValue();
					if(uname.equals(username)){
						bl=true;
						NodeList list4 = Peer.getElementsByTagName("MembershipCertificates");
						Element MCer = (Element)list4.item(0);
						NodeList list5 = MCer.getElementsByTagName("ei");

						Element eiE = (Element)list5.item(num);
						ei=eiE.getFirstChild().getNodeValue();
					}
				}
				if(bl==false){
					num++;
					ArrayList Includegroup = new ArrayList();
					Includegroup = getIncludegroup2(grname);
					for(int i=0;i<Includegroup.size();i++){
						ei = getei((String)Includegroup.get(i),uname,num);
						if(ei!=null){
							break;
						}
					}
				}
			}
		}
		return ei;
	}

	/**
	 * 会員証明書eiを取得
	 * (grname以上のすべてのグループのeiを読み取る)
	 * @param grname グループ名
	 * @param uname  ユーザ名
	 * @param unm  　　 再帰回数(最初は0)
	 * 
	 * @return　ei
	 * @author kawata
	 * @作成日: 2008/12/20
	 */
	public ArrayList getei2(String grname,String uname,int num){
		ArrayList ei = new ArrayList();
		boolean bl = false;
		NodeList list = root.getElementsByTagName("Group");//Groupリスト作成
		for(int k=0; k<list.getLength(); k++){
			Element group = (Element)list.item(k);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(grname)){//grnameと同じグループ名の要素を取り出す
				NodeList ulist = group.getElementsByTagName("Peer");
				for(int j=0;j<ulist.getLength();j++){
					Element Peer = (Element)ulist.item(j);
					NodeList list3 = Peer.getElementsByTagName("Name");
					Element PeerName = (Element)list3.item(0);
					String username = PeerName.getFirstChild().getNodeValue();
					if(uname.equals(username)){
						bl=true;
						NodeList list4 = Peer.getElementsByTagName("MembershipCertificates");
						Element MCer = (Element)list4.item(0);
						NodeList list5 = MCer.getElementsByTagName("ei");
						for(int i=num;i<list5.getLength();i++){
							Element eiE = (Element)list5.item(i);
							ei.add(eiE.getFirstChild().getNodeValue());
						}
					}
				}
				if(bl==false){
					num++;
					ArrayList Includegroup = new ArrayList();
					Includegroup = getIncludegroup2(grname);
					for(int i=0;i<Includegroup.size();i++){
						ei = getei2((String)Includegroup.get(i),uname,num);
						if(ei.size()!=0){
							break;
						}
					}
				}
			}
		}
		return ei;
	}
	
	public int getGroupNum(){
		int num = 0;
		try{
			NodeList list = this.root.getElementsByTagName("Group");
			num = list.getLength();
		}catch(Exception e){
			System.err.println("can't get Group!!");
		}
		return num;
	}
	
	public String[] getGroupName() {
		String[] gnames = null;
		try{
			NodeList list = this.root.getElementsByTagName("Group");
			gnames = new String[list.getLength()];                   
			for(int i=0;i<list.getLength();i++){
				Element iElement = (Element)list.item(i);
				gnames[i]= iElement.getAttribute("xmlns:GroupName");
			}	
		}catch(Exception e){
			System.err.println("can't get GroupName!!");
		}
		return gnames;
	}
	
	/**
	 * グループ名を受け取ってグループメンバの全てのユーザ名を返す
	 * (GroupLinkがある場合にも対応)
	 * 
	 * @param grname     所属するグループのグループ名
	 * 
	 * @return names        リンクを含むgroupnameのグループメンバリスト
	 * @author kawata
	 * @作成日: 2008/11/17
	 */
	public ArrayList getMemberName2(String grname){
		ArrayList names = new ArrayList();
		boolean bl = false;

		NodeList list = root.getElementsByTagName("Group");//Groupリスト作成
		for(int i=0; i<list.getLength(); i++){
			Element group = (Element)list.item(i);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(grname)){//grnameと同じグループ名の要素を取り出す
				NodeList list2 = group.getElementsByTagName("Peer");//Peerリスト作成
				for(int j=0; j<list2.getLength(); j++){//メンバ数分繰り返す
					Element peer = (Element)list2.item(j);
					NodeList list3 = peer.getElementsByTagName("Name");//Nameリスト作成
					Element name = (Element)list3.item(0);
					names.add(name.getFirstChild().getNodeValue());
				}
				NodeList list4 = group.getElementsByTagName("GroupLink");//GroupLinkリスト作成
				for(int j=0; j<list4.getLength(); j++){//GroupLink数分繰り返す
					Element grouplink = (Element)list4.item(j);
					/*GroupLinkのグループ名を呼ぶ出す*/
					String glinkname =  grouplink.getAttribute("xmlns:GroupName");//グループ名取得
					ArrayList names2 = getMemberName2(glinkname);//names2にLinkメンバを代入
					/*すでに配列内にいる人かを判断*/
					for(int k=0; k < names2.size(); k++){
						bl=true;
						for(int g=0; g < names.size(); g++){
							if(names2.get(k).equals(names.get(g))==true){//同じ名前の人がいないかを判断
								bl=false;
								g=names.size();
							}
						}
						if(bl==true){
							names.add(names2.get(k));//かぶっていなかったら
						}
					}
				}

			}
		}	
		return (names);//names4を返す
	}

	/**
	 * グループ名を受け取って親グループ名を返す
	 * 
	 * @param grname     親グループリストを取得するグループのグループ名
	 * 
	 * @return Includegroup        親グループリスト
	 * @author kawata
	 * @作成日: 2008/12/1
	 */
	public ArrayList getIncludegroup(String grname){
		ArrayList Includegroup = new ArrayList();//親グループリスト
		NodeList list = root.getElementsByTagName("Group");//Groupリスト作成
		for(int i=0; i<list.getLength(); i++){
			Element group = (Element)list.item(i);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(grname)){//groupnameと同じグループ名の要素を取り出す
				NodeList list2 = group.getElementsByTagName("IncludeGroupLink");//IncludeGroupLinkリスト作成
				for(int j=0;j<list2.getLength();j++){
					Element Includegrouplink = (Element)list2.item(j);
					/*GroupLinkのグループ名を呼ぶ出す*/
					String Includeglinkname =  Includegrouplink.getAttribute("xmlns:GroupName");//グループ名取得
					Includegroup.add(Includeglinkname);
				}
			}
		}
		return (Includegroup);
	}
	
	/**
	 * グループ名を受け取って子グループ名を返す
	 * 
	 * @param grname     子グループリストを取得するグループのグループ名
	 * 
	 * @return Includegroup        子グループリスト
	 * @author kawata
	 * @作成日: 2008/12/1
	 */
	public ArrayList getIncludegroup2(String grname){
		ArrayList Includegroup = new ArrayList();//子グループリスト
		NodeList list = root.getElementsByTagName("Group");//Groupリスト作成
		for(int i=0; i<list.getLength(); i++){
			Element group = (Element)list.item(i);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(grname)){//groupnameと同じグループ名の要素を取り出す
				NodeList list2 = group.getElementsByTagName("GroupLink");//IncludeGroupLinkリスト作成
				for(int j=0;j<list2.getLength();j++){
					Element Includegrouplink = (Element)list2.item(j);
					/*GroupLinkのグループ名を呼ぶ出す*/
					String Includeglinkname =  Includegrouplink.getAttribute("xmlns:GroupName");//グループ名取得
					Includegroup.add(Includeglinkname);
				}
			}
		}
		return (Includegroup);
	}
	
	/**
	 * グループ名を受け取ってグループメンバの全てのユーザ名を返す
	 * (GroupLinkは無視する)
	 * 
	 * @param grname     ユーザリストを取得するグループのグループ名
	 * 
	 * @return names        (grouplinkを含まない)グループメンバリスト
	 * @author kawata
	 * @作成日: 2008/12/04
	 */
	public ArrayList getMemberName3(String grname){
		ArrayList names = new ArrayList();

		NodeList list = root.getElementsByTagName("Group");//Groupリスト作成
		for(int i=0; i<list.getLength(); i++){
			Element group = (Element)list.item(i);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(grname)){//groupnameと同じグループ名の要素を取り出す
				NodeList list2 = group.getElementsByTagName("Peer");//Peerリスト作成
				for(int j=0; j<list2.getLength(); j++){//メンバ数分繰り返す
					Element peer = (Element)list2.item(j);
					NodeList list3 = peer.getElementsByTagName("Name");//Nameリスト作成
					Element name = (Element)list3.item(0);
					names.add(name.getFirstChild().getNodeValue());
				}
			}
		}	
		return (names);//namesを返す
	}
	
	/**
	 * クループ名を受け取ってグループのIDを返す
	 * @return	String ID	ID
	 * @author	kawata
	 * @作成日: 2008/12/05
	 */
	public String getGroupID(){
		String gID =  group.getAttribute("xmlns:ID");
		return (gID);
	}
	
	/**
	 * グループ名を受け取ってグループメンバ全員のIDを返す
	 * (GroupLinkがある場合にも対応)
	 * 
	 * @param grname     所属するグループのグループ名
	 * 
	 * @return names        リンクを含むgroupnameのグループメンバIDリスト
	 * @author kawata
	 * @作成日: 2008/12/05
	 */
	public ArrayList getGroupMember4(String grname){
		ArrayList names = new ArrayList();
		boolean bl = false;

		NodeList list = root.getElementsByTagName("Group");//Groupリスト作成
		for(int i=0; i<list.getLength(); i++){
			Element group = (Element)list.item(i);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(grname)){//grnameと同じグループ名の要素を取り出す
				NodeList list2 = group.getElementsByTagName("Peer");//Peerリスト作成
				for(int j=0; j<list2.getLength(); j++){//メンバ数分繰り返す
					Element peer = (Element)list2.item(j);
					NodeList list3 = peer.getElementsByTagName("ID");//IDリスト作成
					Element name = (Element)list3.item(0);
					names.add(name.getFirstChild().getNodeValue());
				}
				NodeList list4 = group.getElementsByTagName("GroupLink");//GroupLinkリスト作成
				for(int j=0; j<list4.getLength(); j++){//GroupLink数分繰り返す
					Element grouplink = (Element)list4.item(j);
					/*GroupLinkのグループ名を呼ぶ出す*/
					String glinkname =  grouplink.getAttribute("xmlns:GroupName");//グループ名取得
					ArrayList names2 = getGroupMember4(glinkname);//names2にLinkメンバIDを代入
					/*すでに配列内にいる人かを判断*/
					for(int k=0; k < names2.size(); k++){
						bl=true;
						for(int g=0; g < names.size(); g++){
							if(names2.get(k).equals(names.get(g))==true){//同じIDの人がいないかを判断
								bl=false;
								g=names.size();
							}
						}
						if(bl==true){
							names.add(names2.get(k));//かぶっていなかったら
						}
					}
				}

			}
		}	
		return (names);//names4を返す
	}
	
	/**
	 * グループメンバの中にいるユーザの名前を受け取りXML上でどのグループの部分に書かれているかを調べる
	 * (GroupLink以下のグループにいる場合もあるので調べる)
	 * 
	 * @param grname     所属するグループのグループ名
	 * 
	 * @return names        リンクを含むgroupnameのグループメンバIDリスト
	 * @author kawata
	 * @作成日: 2008/12/20
	 */
	public ArrayList getGroupLinkName(String grname, String username){
		ArrayList userlist =this.getMemberName3(grname);
		ArrayList allgname = new ArrayList();
		boolean b1 = false;
		for(int i=0;i<userlist.size();i++){
			if(userlist.get(i).equals(username)){
				allgname.add(grname);
				b1=true;
			}
		}
		if(b1==false){
			ArrayList IncludeGroup = this.getIncludegroup2(grname);
			for(int i=0;i<IncludeGroup.size();i++){
				ArrayList allgname2 = this.getGroupLinkName((String)IncludeGroup.get(i),username);
				for(int k=0; k<allgname2.size();k++){
					allgname.add(allgname2.get(k));
				}
			}
		}
		
		return allgname;
	}
}
