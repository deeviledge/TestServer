/*
 * 作成日: 2006/11/17
 *
 */

package	sglserver.groupadmin;

import java.io.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;

import sglserver.dynamicpeerinformation.DynamicPeerInformation;
import sglserver.option.Uniques;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import java.util.*;

/**
 * XMLを新規作成するクラス
 * 
 * @author fujino
 * @version 1.4
 * 
 */
public class GroupInformationXml{
	
	private Document	document;		// グループ情報のドキュメント
	private Element		root;			//     〃       のルートノード
	private Document	u_document;		// ユーザ情報のドキュメント
	private Element		u_root;			//     〃    のルートノード
	
	private int			GroupCount;
	private List		GroupList;
	
	
	Uniques uni = new Uniques();
	
	String	serverip = uni.getServerIP();
	
	//　ユーザ情報があるファイルパス
	String UserDataFile = uni.getDynamicinfoFileName(); 
	
	//　キーボード入力用
	BufferedReader keyin = new BufferedReader(new InputStreamReader(System.in));
	//BufferedReaderクラスに渡す
	BufferedReader in = new BufferedReader(keyin);
	
	String filename = uni.getGroupFileName();
	//boolean	newxml = true;		// true :GroupInfoTest.xmlを新しく作り直す場合
	
	
	
	
	/**
	 * Root要素にGroupInformationを持ったXML文書を
	 * 新規作成する
	 * 
	 * @param filename			読み込みファイル
	 */
	public GroupInformationXml(String filename){
		
		GroupList = new ArrayList();
		
		try{
			//　ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
				
			FileInputStream fis = new FileInputStream(filename);

			// グループ情報XMLを新規作成する
			if(fis == null){
				//　Documentオブジェクトを新規作成
				document = builder.newDocument();
				// ドキュメントにルート要素を作成
				root = document.createElement("GroupInformation");
				// ドキュメントにルート要素を追加
				document.appendChild(root);
			
				// 所定のタグ名とともに、すべての Elements の NodeList を取得
				NodeList gInfolist = root.getElementsByTagName("GroupInformation");
				// gInfolistの0番目の要素としてgInfoを作成
				Element gInfo = (Element)gInfolist.item(0);
				
				//　gcnt　：グループの総数
				GroupCount = 0;
				String gcnt = Integer.toString(GroupCount);
				
				// gInfoにGroupCount要素を作成
				gInfo = document.createElement("GroupCount");
				// 指定された文字列を持つTextノードを作成
				gInfo.appendChild(document.createTextNode(gcnt));
				root.appendChild(gInfo);

			//　既存のグループ情報ファイルを読み込む場合
			}else{
				// パースを実行してDocumentオブジェクトを取得(ファイル読み込み)
				document = builder.parse(new BufferedInputStream(fis));
				// ルート要素を取得
				root = document.getDocumentElement();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Root要素にGroupInformationを持ったXML文書を
	 * 新規作成する
	 * 
	 * @param filename			読み込みファイル
	 */
	public GroupInformationXml(){
		
		GroupList = new ArrayList();
		
		try{
			//　ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
				
			FileInputStream fis = new FileInputStream(filename);

			// グループ情報XMLを新規作成する
			if(fis == null){
				//　Documentオブジェクトを新規作成
				document = builder.newDocument();
				// ドキュメントにルート要素を作成
				root = document.createElement("GroupInformation");
				// ドキュメントにルート要素を追加
				document.appendChild(root);
			
				// 所定のタグ名とともに、すべての Elements の NodeList を取得
				NodeList gInfolist = root.getElementsByTagName("GroupInformation");
				// gInfolistの0番目の要素としてgInfoを作成
				Element gInfo = (Element)gInfolist.item(0);
				
				//　gcnt　：グループの総数
				GroupCount = 0;
				String gcnt = Integer.toString(GroupCount);
				
				// gInfoにGroupCount要素を作成
				gInfo = document.createElement("GroupCount");
				// 指定された文字列を持つTextノードを作成
				gInfo.appendChild(document.createTextNode(gcnt));
				root.appendChild(gInfo);

			//　既存のグループ情報ファイルを読み込む場合
			}else{
				// パースを実行してDocumentオブジェクトを取得(ファイル読み込み)
				document = builder.parse(new BufferedInputStream(fis));
				// ルート要素を取得
				root = document.getDocumentElement();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
/***********************************************************************************************************
 ***********************************************************************************************************
 *********************************************************************************************************** */
	
	
	/**
	 * グループを追加
	 * 
	 * @param gname		グループ名
	 * @param option	鍵交換オプション(Normal, AntiDishonestInsider)
	 * @param userlist	ユーザIDリスト
	 * 
	 * @return 追加されたドキュメントを返します
	 */
	public String GroupInformationXmlGenarator(String gname, String option, ArrayList userlist){

		String GroupName=null;
		
		try{
			// "Group"Elements の NodeList を取得			
			NodeList gInfolist = root.getElementsByTagName("Group");
			Element gInfo = (Element)gInfolist.item(0);
			
			//　現在のグループの総数を取得
			String gidcnt = Integer.toString(gInfolist.getLength());
			// グループID
			String gid;


			// グループをグループIDが昇順になるように追加する
			// グループの数だけループ
			int i;
			for(i=0; i<=Integer.parseInt(gidcnt); i++){
				// i番目のノードを取得
				gInfo = (Element)gInfolist.item(i);

				// gInfoがnullのとき(=ノードの末尾に達したとき)
				if(gInfo == null){
					// "Group"要素を生成
					gInfo = document.createElement("Group");
					//　属性の作成
					Attr gidTag = document.createAttribute("xmlns:ID");
					//　gInfo（"Group"）の属性として設定
					gInfo.setAttributeNode(gidTag);
					//　gid　：グループID　をString型で取得
					gid = Integer.toString(i);
					//　属性の値を設定
					gidTag.setValue(gid);
					
					// 属性"xmlns:GroupName"を作成
					Attr gnameTag = document.createAttribute("xmlns:GroupName");
					// gInfoにgnameTag属性をつける
					gInfo.setAttributeNode(gnameTag);
					//　属性の値を設定
					gnameTag.setValue (gname);
					
					// ノード newChild をrootノードの子リストの末尾に追加
					root.appendChild(gInfo);

					break;
				}
				// "Group"要素が存在しているとき
				// "Group"要素があるとき、同じグループ名のグループが重複しないような処理が必要（後回しになってます）
				else{
					//　指定した属性の値を取得
					gid = gInfo.getAttribute("xmlns:ID");
					
					// 穴の空いている番号があった場合
					if(i != Integer.parseInt(gid)){
						// "Group"要素を追加
						gInfo = document.createElement("Group");
						Attr gidTag = document.createAttribute("xmlns:ID");
						gidTag.setValue(Integer.toString(i));
						gInfo.setAttributeNode(gidTag);
						
						// 属性"xmlns:GroupName"を作成
						Attr gnameTag = document.createAttribute("xmlns:GroupName");
						gInfo.setAttributeNode(gnameTag);
						gnameTag.setValue (gname);
						
						// i番目のノードの前にgInfoを挿入
						root.insertBefore(gInfo, (Element)gInfolist.item(i));
						
						break;
					}
				}
			}
			
			
			
			
			// gInfoにOption要素を作成
			Element gopt = document.createElement("Option");
			// 指定された文字列を持つTextノードを作成
			gopt.appendChild(document.createTextNode(option));
			//　gInfoの子ノードに追加
			gInfo.appendChild(gopt);
			

			//　"GroupCount"要素を更新
			//　指定した要素のリストを取得
			NodeList gcntlist = root.getElementsByTagName("GroupCount");
			//　新しく"GroupCount"要素を作成
			Element gcnt = document.createElement("GroupCount");
			//　グループの数をString型で設定し、テキストノードとして追加
			GroupCount = gInfolist.getLength();
			gcnt.appendChild( document.createTextNode(Integer.toString(GroupCount)) );
			// gcntを0番目の要素と入れ替える
			root.replaceChild(gcnt, (Element)gcntlist.item(0));
			
			GroupName = ((Element)gInfolist.item(i)).getAttribute("xmlns:GroupName");
			
			// 追加したグループにメンバーの要素を追加
			this.AddUser(Integer.toString(i), option, userlist);
			
			//　設計用ファイルの保存メソッド
			saveSekkei( (Element)gInfolist.item(i) );

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return(GroupName);
	}
	/////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	//選択されたグループに選択されたオンラインメンバを追加する
	public String GroupInformationXmlAdd(String gname, String option, ArrayList userlist){
		System.out.println("hey");
		String GroupName=null;
		String [] menber = null;
		for(int i=0;i<userlist.size();i++){//userlistに入っているメンバをmenberにいれる
			menber[i]= userlist.get(i).toString();
			}
		System.out.println(menber[0]);
		NodeList list = root.getElementsByTagName("Group");
		for(int i =0; i<list.getLength(); i++){//listのエレメントの個数でループさせる
			Element group = (Element)list.item(i);
			String gnames =  group.getAttribute("xmlns:GroupName"); 
			System.out.println(gname);
			if(gnames.equals(gname)){
				try{
					Element peer = document.createElement("Peer"); // 要素(Peer)を作成
					root.appendChild(peer); // root(Test)の下にpeerを付ける
					//Element uID = document.createElement("UserID"); // 要素(UserID)を作成
					//所定の名前の Attr を作成します// AtterとはElement オブジェクトの属性の 1 つ
					//Attr idtag = document.createAttribute("ID");
				//	idtag.setValue (id); // idをidtagに付ける
					//uID.setAttributeNode(idtag); // uIDに属性idtagを付ける
					//user.appendChild(uID); // userの下にuIDを付ける
					Element name = document.createElement("Name"); // 要素(Name)を作成
					for(int j=0;j<menber.length;j++){
						name.appendChild(document.createTextNode(menber[j])); // Nameにmenberを付ける
					}
					peer.appendChild(name); // peerの下にnameを付ける	
					Element userAcount = document.createElement("UserAcount"); // 要素(Name)を作成
					peer.appendChild(userAcount); // peerの下にnameを付ける
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return(GroupName);
	}
	

/***********************************************************************************************************
 ***********************************************************************************************************
 *********************************************************************************************************** */
	
	/**
	 * ユーザを追加
	 * 
	 * @param gid		グループID
	 * @param option	鍵交換オプション(Normal, AntiDishonestInsider)
	 * @param userlist	ユーザIDリスト
	 * 
	 * @return 追加されたドキュメントを返します
	 */
	public Document AddUser(String gid, String option, ArrayList acountlist){
		
		int	addcnt = 50;
		
		//　指定されたグループIDを持つグループが存在しない場合
		if(this.getGroupElement(gid) == null){
			System.out.println("Group" + gid + "は存在しません。");
			return (this.document);
		}else{
                    System.out.println("Group" + gid + "は存在します。");
                }
		
		try{
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory u_factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = u_factory.newDocumentBuilder();
			
			//　ユーザ情報用のdocumentとroot
			// パースを実行してDocumentオブジェクトを取得(ファイル読み込み)	
			u_document = builder.parse(new BufferedInputStream(new FileInputStream(UserDataFile)));
			u_root = u_document.getDocumentElement();

			//　ユーザ情報から指定された要素のリストを取得
			NodeList uInfolist = u_root.getElementsByTagName("DynamicInformation");
			System.out.println((Element)uInfolist.item(0));
			
			ArrayList userlist = new ArrayList(addcnt);
			for(int n=0; n<acountlist.size(); n++){
				for(int m=0; m<uInfolist.getLength(); m++){
					Element acInfo = (Element)uInfolist.item(m);
					String acname =
						(acInfo.getElementsByTagName("PeerName")).item(0).getFirstChild().getNodeValue(); 
					if( acname.equals(acountlist.get(n)) ){
						userlist.add( acInfo.getAttribute("xmlns:ID") );
					}
				}
			}
			
			
			//　指定されたグループの場所を探す
			int l;
			NodeList g = root.getElementsByTagName("Group");
			// Groupの数だけループ
			for(l=0; l<g.getLength(); l++){
				String gn = ((Element)g.item(l)).getAttribute("xmlns:ID");
				// 要求のGroupIDだったとき
				if(gn.equals(gid)){
					break;
				}
			}
			
			//　gInfoに指定されたグループを設定
			NodeList gInfolist = root.getElementsByTagName("Group");
			Element gInfo = (Element)gInfolist.item(l);

			/*
			NodeList memberlist = gInfo.getElementsByTagName("Peer");
			Element mInfo = (Element)memberlist.item(0);
			if(mInfo == null){
				// gInfoにMember要素を作成
				Element member = document.createElement("Peer");
				mInfo = member;
			}
			*/
			
			//　"Peer"要素（グループメンバー）のリストを取得
			NodeList peer = gInfo.getElementsByTagName("Peer");
			System.out.println("userlist=" + userlist);

			//　RelayIPの取得
			System.out.println("RelayIP = " + serverip);
			//String rip = in.readLine();
			String rip = serverip;
			
			//　ユーザを追加
			//　追加したいユーザの数だけ繰り返す
			for(int j=0; j<userlist.size(); j++){
				
				//　userlistからj番目のユーザIDを取得
				String uid = (String)(userlist.get(j));
				
				//　現在のユーザの数だけ繰り返す
				for(int i=0; i<=peer.getLength(); i++){
					// peerのi番目がnullのとき(=ノードの末尾に達したとき)
					if(peer.item(i) == null){
						//各要素を作成
						Element mInfo = document.createElement("Peer");
						Element pID = document.createElement("ID");
						
						//　"Peer"要素にID属性を追加する
						Attr gidTag = document.createAttribute("xmlns:ID");
						mInfo.setAttributeNode(gidTag);
						gidTag.setValue(uid);
						
						//　今は2つ"ID"を表すものを作ってます。（削除可）
						// "ID"要素を設定
						pID.appendChild(document.createTextNode(uid));
						mInfo.appendChild(pID);
						gInfo.appendChild(mInfo);
						
						//　ユーザ情報のXMLファイルのユーザの数だけ繰り返す
						for(int k=0; k<uInfolist.getLength(); k++){
							//　k番目の要素を取得
							Element uInfo = (Element)uInfolist.item(k);
							
							String gn = uInfo.getAttribute("xmlns:ID");
							//　追加したいユーザのIDとユーザ情報のIDが一致したら
							if(gn.equals(uid)){
								//　ユーザ情報ファイルから"UserName"を取得
								DynamicPeerInformation dpinfo = new DynamicPeerInformation();
								String	pname = dpinfo.getUserName(uid);
								System.out.println((k+1)+"番目の処理:追加するユーザ名は"+pname+"です");
                                                                
								//　グループ情報ファイルにユーザ名を作成
								Element uName = document.createElement("Name");
								uName.appendChild(document.createTextNode(pname));
								mInfo.appendChild(uName);
                                                                System.out.println((k+1)+"番目の処理:グループ設計書にユーザ名："+uName+"のタグ情報を追加しました");
								
								//　グループ情報ファイルにユーザ名を作成
								Element uAcount = document.createElement("UserAcount");
								uAcount.appendChild(document.createTextNode( (String)(acountlist.get(j)) ));
								mInfo.appendChild(uAcount);
								System.out.println((k+1)+"番目の処理:グループ設計書にユーザアカウント："+uAcount+"のタグ情報を追加しました");
								
								//　"IP"要素を作成
								Element uIP = document.createElement("IP");
								//　IPアドレスを取得
								String uip = dpinfo.getIP(pname);
                                                                
								// uIPにIPアドレスを設定
								uIP.appendChild(document.createTextNode(uip));
								mInfo.appendChild(uIP);
                                                                System.out.println((k+1)+"番目の処理:グループ設計書にユーザIP："+uIP+"のタグ情報を追加しました");
                                                                
                                                                
								//　"RelayIP"要素を作成
								Element rIP = document.createElement("RelayIP");
                                                                
								// rIPにRelayIPを設定
								rIP.appendChild(document.createTextNode(rip));
								mInfo.appendChild(rIP);
                                                                System.out.println((k+1)+"番目の処理:グループ設計書にRelayIP："+rIP+"のタグ情報を追加しました");
                                                                
								break;
							}
						}
						break;
					}
					//　追加したいユーザのIDと現在いるユーザのIDが同じ場合
					else if( uid.equals( ((Element)peer.item(i)).getAttribute("xmlns:ID") ) ){
						System.out.println("グループ"+gid + "：  ユーザ"+uid+"は存在しています。");
						break;
					}
					//　ユーザIDに穴があいている場合、昇順になるように追加
					//　追加したいユーザIDよりi番目のID属性の数値が大きければ
					else if(Integer.parseInt( ((Element)peer.item(i)).getAttribute("xmlns:ID")) > Integer.parseInt(uid) ){

						//各要素を作成
						Element mInfo = document.createElement("Peer");
						Element pID = document.createElement("ID");

						//　"Peer"要素にID属性を追加する
						Attr gidTag = document.createAttribute("xmlns:ID");
						mInfo.setAttributeNode(gidTag);
						gidTag.setValue(uid);

						// "ID"要素を設定
						pID.appendChild(document.createTextNode(uid));
						mInfo.appendChild(pID);
						gInfo.insertBefore(mInfo, (Element)peer.item(i));

						//　ユーザ情報のXMLファイルのユーザの数だけ繰り返す
						for(int k=0; k<uInfolist.getLength(); k++){
							//　k番目の要素を取得
							Element uInfo = (Element)uInfolist.item(k);
							
							String gn = uInfo.getAttribute("xmlns:ID");
							//　追加したいユーザのIDとユーザ情報のIDが一致したら
							if(gn.equals(uid)){
								//　ユーザ情報ファイルから"UserName"を取得
								DynamicPeerInformation dpinfo = new DynamicPeerInformation();
								String	pname = dpinfo.getUserName(uid);
								
								//　グループ情報ファイルにユーザ名を作成
								Element uName = document.createElement("Name");
								uName.appendChild(document.createTextNode(pname));
								//gInfo.insertBefore(uName, (Element)peer.item(i+1));
								mInfo.appendChild(uName);
								
								//　グループ情報ファイルにユーザ名を作成
								Element uAcount = document.createElement("UserAcount");
								uAcount.appendChild(document.createTextNode( (String)(acountlist.get(j)) ));
								mInfo.appendChild(uAcount);

								
								//　"IP"要素を作成
								Element uIP = document.createElement("IP");
								//　IPアドレスを取得
								String uip = dpinfo.getIP(pname);
								System.out.println("pname :" + "IP=" + uip);
								
								// uIPにIPアドレスを設定
								uIP.appendChild(document.createTextNode(uip));
								mInfo.appendChild(uIP);

								//　"RelayIP"要素を作成
								Element rIP = document.createElement("RelayIP");
								// rIPにRelayIPを設定
								rIP.appendChild(document.createTextNode(rip));
								mInfo.appendChild(rIP);
								break;
							}
						}
						break;
					}
				}
			}
			
			//　オプションの変更メソッド
			ChangeOption(gid, option);

			//　設計用ファイルの保存メソッド
			saveSekkei( (Element)gInfolist.item(l) );
			
		}catch(Exception e){
			e.printStackTrace();
		}
	return(document);
	}
	

/***********************************************************************************************************
 ***********************************************************************************************************
 *********************************************************************************************************** */
	
	/**
	 * id文字列からユーザーの要素を返す
	 * 
	 * @param gid	グループID
	 * 
	 * @return idと同じユーザーのElement要素
	 */
	public Element getGroupElement(String gid){
		// ルート要素を取得
		Element eroot = this.document.getDocumentElement();
		// ルート要素からGroupのノードリストを取得
		NodeList g = eroot.getElementsByTagName("Group");
		// Groupの要素を、とりあえずルート要素にしておく
		Element gElement = this.document.getDocumentElement();
		
		// Groupの数だけループ
		for(int i=0; i<g.getLength(); i++){
			// i番目のGroupを取得
			gElement = (Element)g.item(i);
			// GroupIDを取得
			String gn = gElement.getAttribute("xmlns:ID");
			// 要求のGroupIDだったとき
			if(gn.equals(gid)){
				// Groupの要素を返す
				return(gElement);
			}
		}
		// ループの最後までGroupが見つからなければnullを返す
		return( null );
	}
	
	

/***********************************************************************************************************
 ***********************************************************************************************************
 *********************************************************************************************************** */
	
	
	/**
	 * グループの削除
	 * 
	 * @param gid		グループID
	 * 
	 * @return 追加されたドキュメントを返します
	 */
	public Document DeleteGroup(String gid){

		//　指定されたグループIDを持つエレメントを取得
		Element gInfo=this.getGroupElement(gid);
		//　指定されたグループがないとき
		if(gInfo == null){
			System.out.println("Group" + gid + "は存在しません。");
			return (this.document);
		}
		
		try{
			//　指定されたグループIDを持つ要素の位置を取得
			int l;
			NodeList g = root.getElementsByTagName("Group");
			// Groupの数だけループ
			for(l=0; l<g.getLength(); l++){
				String gn = ((Element)g.item(l)).getAttribute("xmlns:ID");
				// 要求のGroupIDだったとき
				if(gn.equals(gid)){
					break;
				}
			}
			
			//　"Group"要素のリストを取得
			NodeList gInfolist = root.getElementsByTagName("Group");
			
			//　設計用のファイルを指定
			File file = new File("src/sglserver/conf/usr/xml_files/sekkei_"
					+ ((Element)gInfolist.item(l)).getAttribute("xmlns:GroupName")
					+ ".xml");
			//　設計用ファイルの削除
			file.delete();
			
			//　グループ情報から指定されたグループ要素を削除
			root.removeChild(gInfo);

			//　"GroupCount"の更新
			NodeList gcntlist = root.getElementsByTagName("GroupCount");
			Element gcnt = document.createElement("GroupCount");
			GroupCount = gInfolist.getLength();
			gcnt.appendChild( document.createTextNode(Integer.toString(GroupCount)) );
			root.replaceChild(gcnt, (Element)gcntlist.item(0));

		}catch(Exception e){
			e.printStackTrace();
		}
	return(document);
	}

	

/***********************************************************************************************************
 ***********************************************************************************************************
 *********************************************************************************************************** */
	
	
	/**
	 * ユーザの削除
	 * 
	 * @param gid		グループID
	 * @param option	鍵交換オプション(Normal, AntiDishonestInsider)
	 * @param userlist	ユーザIDリスト
	 * 
	 * @return 追加されたドキュメントを返します
	 */
	public Document RemoveUser(String gid, String option, ArrayList userlist){

		//　指定されたグループIDを持つグループがない場合
		if(this.getGroupElement(gid) == null){
			System.out.println("Group" + gid + "は存在しません。");
			return (this.document);
		}
		
		try{
			//　指定されたグループIDを持つ要素の位置を取得
			int l;
			NodeList g = root.getElementsByTagName("Group");
			// Groupの数だけループ
			for(l=0; l<g.getLength(); l++){
				String gn = ((Element)g.item(l)).getAttribute("xmlns:ID");
				// 要求のGroupIDだったとき
				if(gn.equals(gid)){
					break;
				}
			}
			
			//　"Group"要素のリストを取得
			NodeList gInfolist = root.getElementsByTagName("Group");
			//　指定グループの要素を取得
			Element gInfo = (Element)gInfolist.item(l);

			//NodeList memberlist  = ((Element)(gInfo.getElementsByTagName("Peer")).item(0)).getElementsByTagName("ID");
			//NodeList memberlist2 = ((Element)(gInfo.getElementsByTagName("Peer")).item(0)).getElementsByTagName("UserName");
			NodeList memberlist  = gInfo.getElementsByTagName("Peer");
			
			//　ユーザの削除
			//　追加したいユーザの数だけ繰り返す
			for(int j=0; j<userlist.size(); j++){
				//　現在のユーザの数だけ繰り返す
				for(int i=0; i<memberlist.getLength(); i++){
					//Element peer  = (Element)memberlist.item(i);
					//Element peer2 = (Element)memberlist2.item(i);
					Element peer  = (Element)memberlist.item(i);
					
					//　i番目の要素が存在しているとき
					if(peer != null){
						//　i番目のユーザのIDを取得
						String peerID = peer.getAttribute("xmlns:ID");
						//　i番目のユーザIDと指定されたユーザIDが同じだったとき
						if( peerID.equals(userlist.get(j)) ){
							//((Element)(gInfo.getElementsByTagName("Peer")).item(0)).removeChild(peer);
							//((Element)(gInfo.getElementsByTagName("Peer")).item(0)).removeChild(peer2);
							//　ユーザ要素を削除
							gInfo.removeChild(peer);
							System.out.println("ユーザ"+userlist.get(j) + "を外しました。");
							break;
						}
					}
					//　ノードの末尾に達したとき
					else{
						System.out.println("ユーザ"+userlist.get(j) + "は存在しません。");
					}
				}
			}

			//　オプションの変更メソッド
			ChangeOption(gid, option);
			
			//　設計用のファイル保存メソッド
			saveSekkei( (Element)gInfolist.item(l) );
			
		}catch(Exception e){
			e.printStackTrace();
		}
	return(document);
	}


/***********************************************************************************************************
 ***********************************************************************************************************
 *********************************************************************************************************** */
		

	/**
	 * オプションの表示
	 * 
	 * @param gid		グループID
	 * 
	 * @return 指定されたグループIDの要素を返す
	 */
	public Element getOption(String gid){

		//　指定されたグループIDを持つグループがないとき
		if(this.getGroupElement(gid) == null){
			System.out.println("Group" + gid + "は存在しません。");
			return ( null );
		}
		
		//　指定されたグループIDを持つ要素の位置を取得
		int l;
		NodeList g = root.getElementsByTagName("Group");
		// Groupの数だけループ
		for(l=0; l<g.getLength(); l++){
			String gn = ((Element)g.item(l)).getAttribute("xmlns:ID");
			// 要求のGroupIDだったとき
			if(gn.equals(gid)){
				break;
			}
		}
		
		//　"Group"要素のリストを取得
		NodeList gInfolist = root.getElementsByTagName("Group");
		//　指定されたグループの要素を取得
		Element gInfo = (Element)gInfolist.item(l);

		// "Option"要素を取得
		Element opt = (Element)(gInfo.getElementsByTagName("Option")).item(0);
		//　現在のオプションを表示
		System.out.println("グループ"+gid + "の現在のオプション　："+(opt.getFirstChild().getNodeValue()));

		return(opt);
	}
	
	/*---------------------------------------------------------------------------------*/
	
	/**
	 * オプションの変更
	 * 
	 * @param gid		グループID
	 * @param option	鍵交換オプション(Normal, AntiDishonestInsider)
	 * 
	 * @return 追加されたドキュメントを返します
	 */
	public void ChangeOption(String gid, String option){

		System.out.println("Changed Option　=　" + option);
		

		//　指定されたグループIDを持つ要素の位置を取得
		int l;
		NodeList g = root.getElementsByTagName("Group");
		// Groupの数だけループ
		for(l=0; l<g.getLength(); l++){
			String gn = ((Element)g.item(l)).getAttribute("xmlns:ID");
			// 要求のGroupIDだったとき
			if(gn.equals(gid)){
				break;
			}
		}
		
		try{
			// 指定されたグループ要素を取得
			Element gInfo = (Element)(root.getElementsByTagName("Group")).item(l);

			//　"Option"要素のリストを取得
			NodeList optlist = gInfo.getElementsByTagName("Option");
			//　"Option"要素を作成
			Element gopt = document.createElement("Option");
			//　"Option"の値をテキストノードで追加
			gopt.appendChild(document.createTextNode(option));
			// オプションの更新
			gInfo.replaceChild(gopt, (Element)optlist.item(0));
			
			//　設計用のファイル保存メソッド
			saveSekkei( (Element)root.getElementsByTagName("Group").item(l) );
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	


/************************************************************************************************************
 ************************************************************************************************************
 ************************************************************************************************************ */
		
	/**
	 * このドキュメントを保存します
	 * 
	 * @param filename 保存するファイル名(パスを含む)
	 * 
	 * @throws Exception ファイルの保存時に起きたエラー
	 */
	public void saveFile() throws Exception {
		
		//xml保存 変換
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		
		//xml保存
		DOMSource source = new DOMSource(document);
		File newXML = new File( filename ); 
		FileOutputStream os = new FileOutputStream(newXML); 
		StreamResult result = new StreamResult(os); 
		transformer.transform(source, result);
	}
	
/* ----------------------------------------------------------------------------------------------------------- */


	/**
	 * 設計用XMLファイルとして追加・変更されたグループを保存
	 * 
	 * @param groot		保存するグループの"Group"要素
	 * 
	 * @throws Exception ファイルの保存時に起きたエラー
	 */
	public void saveSekkei(Element groot){
		
		try{
			//　新規にドキュメントを作成
			//　ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//　ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
			//　設計用Documentを作成
			Document s_document = builder.newDocument();
			//　ルートの要素を設定
			Element doc_root = s_document.createElement("Group_Key_Information");
			s_document.appendChild(doc_root);

			
			//　groot要素を取得
			Element n = (Element)s_document.importNode(groot, true);
			//　設計用Documentに追加
			doc_root.appendChild(n);
			
			//　グループ名を取得
			String gn = groot.getAttribute("xmlns:GroupName");
			
			//　"Group"要素のリストを取得
			NodeList gInfolist = doc_root.getElementsByTagName("Group");
			
			//　"GroupCount"要素を作成
			Element gcnt = s_document.createElement("GroupCount");
			//　"GroupCount"要素の設定
			gcnt.appendChild( s_document.createTextNode(Integer.toString(gInfolist.getLength())) );
			doc_root.insertBefore(gcnt, (Element)doc_root.getElementsByTagName("Group").item(0));
			
			
			//xml保存 変換
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			
			// xml保存
			DOMSource source = new DOMSource(s_document);
			//　ファイル名にグループ名をつけて保存
			String filename = "src/sglserver/conf/usr/xml_files/groups/sekkei_" + gn + ".xml";
			File newXML = new File( filename ); 
			FileOutputStream os = new FileOutputStream(newXML); 
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	

/***********************************************************************************************************
 ***********************************************************************************************************
 *********************************************************************************************************** */
	
	
	/**
	 * @return document を戻します
	 */
	public Document getDocument() {
		return document;
	}

		
	
	
	/**
	 * ドキュメント、Root要素の設定
	 * 
	 * @param doc セットするドキュメント
	 */
	public void setDocument(Document doc){
		this.document = doc;
		this.setRoot(doc.getDocumentElement());
	}

	/**
	 * @return root を戻します。
	 */
	public Element getRoot() {
		return root;
	}
	
	/**
	 * @param root root を設定。
	 */
	private void setRoot(Element root) {
		this.root = root;
	}
	
//	 グループ数GroupCountの値を設定
	public void setGroupSize(int i){
		GroupCount = i;
	}
	// グループ数GroupCountの値を返す
	public int getGroupSize(){
		return(GroupCount);
	}
	public int getGroupCount(){
		return GroupCount;
	}
	// グループのリストGroupListにグループGroupを追加する
	public void addGroup(Group g){
		GroupList.add(g);
	}
	// グループのリストGroupListから、引数番目のグループGroupを返す
	public Group getGroup(int i){
		return( (Group)GroupList.get(i) );
	}
	// グループのリストGroupListの要素数を返す
	public int getListSize(){
		return( GroupList.size() );
	}
	
	/**
	 * クループ名を受け取ってグループメンバの人数を返す
	 * @return
	 */
	public int getGroupValue(String groupname){
		int num = 0;
		
		NodeList list = root.getElementsByTagName("Group");
		for(int i=0;i<list.getLength();i++){
			Element group = (Element)list.item(i);
			String gname =  group.getAttribute("xmlns:GroupName");
			
			if(gname.equals(groupname)){
				NodeList list2 = group.getElementsByTagName("Peer");
				num = list2.getLength(); 
			}
		}
		return num;
	}
	
	/**
	 * グループ名と値(グループメンバ値)を受け取ってグループメンバのユーザ名を返す
	 * 
	 */
	public String getGroupMember(String groupname,int num){
		String uname = null;
		
		NodeList list = root.getElementsByTagName("Group");
		for(int i=0;i<list.getLength();i++){
			Element group = (Element)list.item(i);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(groupname)){
				NodeList list2 = group.getElementsByTagName("Peer");
				Element peer = (Element)list2.item(num);
				NodeList list3 = peer.getElementsByTagName("Name");
				Element name = (Element )list3.item(0);
				uname = name.getFirstChild().getNodeValue();
			}
		}
		return uname;
	}
	
	/**
	 * グループ名を受け取ってグループメンバの全てのユーザ名を返す
	 * 
	 */
	public String[] getMemberName(String groupname){
		String[] names = null;
		
		NodeList list = root.getElementsByTagName("Group");
		for(int i=0; i<list.getLength(); i++){
			Element group = (Element)list.item(i);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			if(gname.equals(groupname)){
				NodeList list2 = group.getElementsByTagName("Peer");
				names = new String[list2.getLength()];
				for(int j=0; j<list2.getLength(); j++){
					Element peer = (Element)list2.item(j);
					NodeList list3 = peer.getElementsByTagName("Name");
					Element name = (Element)list3.item(0);
					names[j] = name.getFirstChild().getNodeValue();
					//System.out.println("test :" + names[j]);
				}
			}
		}
		return (names);
	}
//選択されたグループのメンバの名前を返す
	public String[] getGroupMember2(String names2){//グループ名取得
		String[] names = null;
		
		NodeList list = root.getElementsByTagName("Group");
		for(int i =0; i<list.getLength(); i++){//listのエレメントの個数でループさせる
			Element group = (Element)list.item(i);
			String gname =  group.getAttribute("xmlns:GroupName"); 
			System.out.println(gname);
			if(gname.equals(names2)){
				System.out.println("hello2");
				NodeList list1 = group.getElementsByTagName("Peer");
				names = new String[list1.getLength()];
				for(int a =0; a<list1.getLength(); a++){//list1のエレメントの個数でループさせる
					Element peer = (Element)list1.item(a);
					NodeList list2 = peer.getElementsByTagName("Name");	//Nameをlist2に入れる
					Element name = (Element)list2.item(0);//上から０番目のＮａｍｅをnameに入れる
					names[a] = name.getFirstChild().getNodeValue();
				}
			}
		}
		return (names);
	}

}
