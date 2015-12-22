package	sglserver.groupinformation;

import java.io.*;
import java.math.BigInteger;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import sglserver.dynamicpeerinformation.DynamicPeerInformation;
import sglserver.option.Uniques;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import java.util.*;

/**
 * GroupInformation.xmlを編集するクラス
 * またグループ作成時にsekkei_グループ名.xmlを作成するクラス
 * @author fujino,masato,kawata
 * @version 1.4
 * @作成日: 2006/11/17
 * @最終更新日:2008/12/10
 */
public class GroupInformationXml{

	private Document	document;		// グループ情報のドキュメント
	private Element		root;			//     〃       のルートノード
	private Document	u_document;		// ユーザ情報のドキュメント
	private Element		u_root;			//     〃    のルートノード
	private int		GroupCount;
	public List		GroupList;
	Uniques uni = new Uniques();
	String	serverip = uni.getServerIP();
	String UserDataFile = uni.getDynamicinfoFileName(); //　ユーザ情報があるファイルパス
	String filename = uni.getGroupFileName();
	boolean	newxml = true;		// true :GroupInfoTest.xmlを新しく作り直す場合

	/**
	 * Root要素にGroupInformationを持ったXML文書を読み込む
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

	/**
	 * グループを追加
	 * @param gname		グループ名
	 * @param option	鍵交換オプション(Normal, AntiDishonestInsider)
	 * @param userlist	ユーザIDリスト
	 * @return 追加されたドキュメントを返します
	 * @author hujino
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
			String gid=null;
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

	/**
	 * グループを追加
	 * 
	 * @param gname		グループ名
	 * @param option	鍵交換オプション(Normal, AntiDishonestInsider)
	 * @param userlist	ユーザIDリスト
	 * @param tname     包含関係を持つグループのグループ名
	 * 
	 * @return 追加されたドキュメントを返します
	 * @author kawata
	 */
	public String GroupInformationXmlGenarator(String gname, String option, ArrayList userlist,BigInteger[][] key,BigInteger[][]cer,String tname,String uid){

		String GroupName=null;

		try{
			// "Group"Elements の NodeList を取得			
			NodeList gInfolist = root.getElementsByTagName("Group");
			Element gInfo = (Element)gInfolist.item(0);

			//　現在のグループの総数を取得
			String gidcnt = Integer.toString(gInfolist.getLength());
			// グループID
			String gid=null,tid=null;

//			指定されたグループIDを持つ要素の位置を取得
			int l;
			NodeList g = root.getElementsByTagName("Group");
			// Groupの数だけループ
			for(l=0; l<g.getLength(); l++){
				String gn = ((Element)g.item(l)).getAttribute("xmlns:GroupName");
				// 要求のGroup名前だったとき
				if(gn.equals(tname)){
					break;	
				}
			}

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

			/*****************追加******************/
			// gInfoにGroupLeader要素を作成
			Element gleader = document.createElement("GroupLeader");
			// 指定された文字列を持つTextノードを作成
			gleader.appendChild(document.createTextNode(uid));
			//　gInfoの子ノードに追加
			gInfo.appendChild(gleader);

			// gInfoにPublicKey要素を作成
			Element pub = document.createElement("PublicKey");
			//　gInfoの子ノードに追加
			gInfo.appendChild(pub);	

			// gInfoにn要素を作成
			Element pubn = document.createElement("n");
			// 指定された文字列を持つTextノードを作成
			pubn.appendChild(document.createTextNode(String.valueOf(key[1][1])));
			//　PublicKeyの子ノードに追加
			pub.appendChild(pubn);

			// gInfoにa要素を作成
			Element puba = document.createElement("a");
			// 指定された文字列を持つTextノードを作成
			puba.appendChild(document.createTextNode(String.valueOf(key[1][2])));
			//　PublicKeyの子ノードに追加
			pub.appendChild(puba);

			// gInfoにa0要素を作成
			Element puba0 = document.createElement("a0");
			// 指定された文字列を持つTextノードを作成
			puba0.appendChild(document.createTextNode(String.valueOf(key[1][3])));
			//　PublicKeyの子ノードに追加
			pub.appendChild(puba0);

			// gInfoにy要素を作成
			Element puby = document.createElement("y");
			// 指定された文字列を持つTextノードを作成
			puby.appendChild(document.createTextNode(String.valueOf(key[1][4])));
			//　PublicKeyの子ノードに追加
			pub.appendChild(puby);

			// gInfoにg要素を作成
			Element pubg = document.createElement("g");
			// 指定された文字列を持つTextノードを作成
			pubg.appendChild(document.createTextNode(String.valueOf(key[1][5])));
			//　PublicKeyの子ノードに追加
			pub.appendChild(pubg);

			// gInfoにh要素を作成
			Element pubh = document.createElement("h");
			// 指定された文字列を持つTextノードを作成
			pubh.appendChild(document.createTextNode(String.valueOf(key[1][6])));
			//　PublicKeyの子ノードに追加
			pub.appendChild(pubh);

//			gInfoにSecretKey要素を作成
			Element Sec = document.createElement("SecretKey");
			//　gInfoの子ノードに追加
			gInfo.appendChild(Sec);	

			// gInfoにn要素を作成
			Element Secp1 = document.createElement("p1");
			// 指定された文字列を持つTextノードを作成
			Secp1.appendChild(document.createTextNode(String.valueOf(key[2][1])));
			//　SecretKeyの子ノードに追加
			Sec.appendChild(Secp1);

			// gInfoにa要素を作成
			Element Secq1 = document.createElement("q1");
			// 指定された文字列を持つTextノードを作成
			Secq1.appendChild(document.createTextNode(String.valueOf(key[2][2])));
			//　SecretKeyの子ノードに追加
			Sec.appendChild(Secq1);

			// gInfoにa0要素を作成
			Element Secx = document.createElement("x");
			// 指定された文字列を持つTextノードを作成
			Secx.appendChild(document.createTextNode(String.valueOf(key[2][3])));
			//　SecretKeyの子ノードに追加
			Sec.appendChild(Secx);

			// gInfoにConcern要素を作成
			Element gcon = document.createElement("Concern");
			// 指定された文字列を持つTextノードを作成
			gcon.appendChild(document.createTextNode("Normal"));
			//　gInfoの子ノードに追加
			gInfo.appendChild(gcon);

			if(tname==null){
				System.out.println("関係グループをもたない");
			}
			else{
				System.out.println("関係グループをもつ");
				//親グループをXMLに保存
				//"IncludeGroupLink"要素を生成
				Element igl = document.createElement("IncludeGroupLink");
				//　IDの作成
				Attr iglTag = document.createAttribute("xmlns:ID");
				//　gInfo（"Group"）の属性として設定
				igl.setAttributeNode(iglTag);
				//　属性の値を設定
				iglTag.setValue(tid);

				// 属性"xmlns:GroupName"を作成
				Attr tnameTag = document.createAttribute("xmlns:GroupName");
				// gInfoにgnameTag属性をつける
				igl.setAttributeNode(tnameTag);
				//　属性の値を設定
				tnameTag.setValue (tname);

				// ノード newChild をrootノードの子リストの末尾に追加
				gInfo.appendChild(igl);
			}
			


			/***************ここまで追加**************/	


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
			for(int j=0;j<userlist.size();j++){
				ArrayList Aiei = new ArrayList();//MembershipCertificates
				for(int k=0;k<2;k++){
					Aiei.add(String.valueOf(cer[j][k+1]));//Aieiにcerを追加
				}
				if(tname==null){
				}
				else{//もし関係グループを設定していたならば
					ReadGroupInfoXML rgi = new ReadGroupInfoXML(tname);
					ArrayList Ai = rgi.getAi2(tname,(String)userlist.get(j),0);//親のAi
					ArrayList ei = rgi.getei2(tname,(String)userlist.get(j),0);//親のei
					for(int k=0;k<Ai.size();k++){//親のAiとeiを交互に追加
						Aiei.add(Ai.get(k));
						Aiei.add(ei.get(k));
					}
				}
//				 //追加したグループにメンバーの要素を1人づつ追加
				this.AddUser2(gid, option, (String)userlist.get(j),Aiei);
			}
			// 追加したグループにメンバーの要素を追加
			//this.AddUser(Integer.toString(i), option, userlist,cer);

			//　設計用ファイルの保存メソッド
			saveSekkei( (Element)gInfolist.item(i) );

			/*******************追加********************/

			if(tname==null){
				System.out.println("包含関係のあるグループはありません.");
			}
			else{//関係グループがあるならば
				System.out.println("グループ[" +gname + "]は,グループ[" + tname + "]と包含関係を持ちます");
				//包含関係修正メソッド(親から指定したメンバを削除)
				this.includegroup(tname,userlist);
				//包含関係リンク追加メソッド(親に自分へのリンクを追加)
				this.makegrouplink(gname,tname,Integer.toString(i));
			}

			/***************ここまで追加**************/	



		}catch(Exception e){
			e.printStackTrace();
		}

		return(GroupName);
	}

	/**
	 * 指定したグループからacountlistのメンバを削除する
	 * (tnameからacountlistのメンバのみを削除する)
	 * 
	 * @param tname      包含関係を持つグループ名
	 * @param acountlist 削除するメンバ
	 * 
	 * @return 修正したドキュメントを返す
	 * @author kawata
	 */
	public Document includegroup(String tname, ArrayList acountlist){
		int y=1;
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

			ArrayList userlist = new ArrayList();
			for(int n=0; n<acountlist.size(); n++){
				for(int m=0; m<uInfolist.getLength(); m++){
					Element acInfo = (Element)uInfolist.item(m);
					String acname =
						(acInfo.getElementsByTagName("PeerName")).item(0).getFirstChild().getNodeValue();
					if( acname.equals(acountlist.get(n)) ){//登録しているuserだったら
						userlist.add( acInfo.getAttribute("xmlns:ID") );//userlistに追加
					}
				}
			}


//			指定されたグループIDを持つ要素の位置を取得
			int l;
			NodeList g = root.getElementsByTagName("Group");
			// Groupの数だけループ
			for(l=0; l<g.getLength(); l++){
				String gn = ((Element)g.item(l)).getAttribute("xmlns:GroupName");
				// 要求のGroup名前だったとき
				if(gn.equals(tname)){
					break;	
				}
			}
			//　"Group"要素のリストを取得
			NodeList gInfolist = root.getElementsByTagName("Group");

			//　指定グループの要素を取得
			Element gInfo = (Element)gInfolist.item(l);
			NodeList memberlist  = gInfo.getElementsByTagName("Peer");
			//　ユーザの削除
			//　追加したいユーザの数だけ繰り返す
			for(int j=0; j<userlist.size(); j++){
				//　現在のユーザの数だけ繰り返す
				for(int i=0; i<memberlist.getLength(); i++){
					Element peer  = (Element)memberlist.item(i);
					//　i番目の要素が存在しているとき
					if(peer != null){
						//　i番目のユーザのIDを取得
						String peerID = peer.getAttribute("xmlns:ID");
						//　i番目のユーザIDと指定されたユーザIDが同じだったとき
						if( peerID.equals(userlist.get(j)) ){
							//　ユーザ要素を削除
							gInfo.removeChild(peer);
							System.out.println("ユーザ"+userlist.get(j) + "を外しました。");
							y++;
							break;
						}
					}
					//　ノードの末尾に達したとき
					else{
						System.out.println("ユーザ"+userlist.get(j) + "は存在しません。");
					}

				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return(document);

	}
	/******************************************************************************/
	/******************************************************************************/

	/**
	 * 子グループリンク追加
	 * (tnameからgnameへのGroupLinkを追加)
	 * 
	 * @param tname      包含関係を持つ親グループ名
	 * @param gname　　　　　包含関係を持つ子グループ名
	 * @param gid        子のグループID
	 * 
	 * @return 修正したドキュメントを返す
	 * @author kawata
	 */
	public Document makegrouplink(String gname, String tname, String gid){
		try{
			System.out.println("子:" + gname + "  子のID:" + gid +"  親:"+ tname);

			//　指定されたグループIDを持つ要素の位置を取得
			int l;
			NodeList g = root.getElementsByTagName("Group");
			// Groupの数だけループ
			for(l=0; l<g.getLength(); l++){
				String gn = ((Element)g.item(l)).getAttribute("xmlns:GroupName");
				// 要求のGroup名前だったとき
				if(gn.equals(tname)){
					break;	
				}
			}
			Element gInfo = (Element)g.item(l);
			//　"Group"要素を生成
			Element gIn = document.createElement("GroupLink");
			//　属性の作成
			Attr gidTag = document.createAttribute("xmlns:ID");
			//　gInfo（"Group"）の属性として設定
			gIn.setAttributeNode(gidTag);
			//　属性の値を設定
			gidTag.setValue(gid);

			// 属性"xmlns:GroupName"を作成
			Attr gnameTag = document.createAttribute("xmlns:GroupName");
			// gInfoにgnameTag属性をつける
			gIn.setAttributeNode(gnameTag);
			//　属性の値を設定
			gnameTag.setValue (gname);
			boolean bl = false;
			NodeList glinklist = gInfo.getElementsByTagName("GroupLink");
			for(l=0; l<glinklist.getLength(); l++){
				String gn = ((Element)glinklist.item(l)).getAttribute("xmlns:ID");
				int n1= Integer.parseInt(gn);
				int n2= Integer.parseInt(gid);
				// 要求のGroupLinkの名前だったとき
				if(n1>n2){//IDが昇順になるように追加
					bl=true;
					break;	
				}
			}
			if(bl==true){
				gInfo.insertBefore(gIn, (Element)glinklist.item(l));	
			}
			else{
				gInfo.appendChild(gIn);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return(document);
	}

	/**
	 * 子グループリンク削除
	 * (tnameからgnameへのGroupLinkを削除)
	 * 
	 * @param tname      リンクを持つ親グループ名
	 * @param gname　　　　　包含関係を持つ子グループ名(リンクを削除されるグループ)
	 * 
	 * @return 修正したドキュメントを返す
	 * @author kawata
	 */
	public Document deletegrouplink(String gname, String tname){
		try{
//			指定されたグループIDを持つ要素の位置を取得
			int l;
			NodeList g = root.getElementsByTagName("Group");
			// Groupの数だけループ
			for(l=0; l<g.getLength(); l++){
				String gn = ((Element)g.item(l)).getAttribute("xmlns:GroupName");
				// 要求のGroup名前だったとき
				if(gn.equals(tname)){
					break;	
				}
			}
			Element gInfo = (Element)g.item(l);//指定グループのグループ要素
			NodeList glinklist = gInfo.getElementsByTagName("GroupLink");
			//GroupLinkの数だけループ
			for(l=0; l<glinklist.getLength(); l++){
				String gn = ((Element)glinklist.item(l)).getAttribute("xmlns:GroupName");
				// 要求のGroupLinkの名前だったとき
				if(gn.equals(gname)){
					break;	
				}
			}
			Element glinkInfo = (Element)glinklist.item(l);//指定削除グループのグループ要素
			gInfo.removeChild(glinkInfo);//GroupよりGroupLinkを削除
		}catch(Exception e){
			e.printStackTrace();
		}
		return(document);
	}

	/**
	 * グループの削除
	 * @param gid		グループID
	 * @return 追加されたドキュメントを返します
	 * @author hujino
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
			File file = new File("src/sglserver/conf/usr/xml_files/groups/sekkei_"
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

	/**
	 * グループの削除
	 * (変更版)IncludeGroupLinkが１つしかできない場合のみ
	 * @param gid		グループID
	 * @return 追加されたドキュメントを返します
	 * @author kawata
	 */
	public Document DeleteGroup2(String gid){
		String tname = null;//親グループの名前
		String tid = null;//親グループのID
		String gname = null;
		Element group = null;//削除グループのエレメント
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
				group = (Element)g.item(l);
				gname = ((Element)g.item(l)).getAttribute("xmlns:GroupName");
				// 要求のGroupIDだったとき
				if(gn.equals(gid)){
					break;
				}
			}					
			NodeList list = group.getElementsByTagName("IncludeGroupLink");//IncludeGroupLinkリスト作成
			for(int i=0; i<list.getLength(); i++){//IncludeGroupLink"数分繰り返す
				tname = ((Element)list.item(i)).getAttribute("xmlns:GroupName");
			}
			ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
			ArrayList userlist = rgi.getMemberName2(gname);//削除グループのリンク以外のグループメンバを取得
			if(tname!=null){//親グループが存在したら
				/*親グループへメンバを追加する*/
				for(int j=0;j<userlist.size();j++){
					ArrayList Aiei = new ArrayList();
					ReadGroupInfoXML rgt = new ReadGroupInfoXML(tname);
					tid = rgt.getGroupID();
					String option = rgt.getGroupOption();
					//Aieiを作成し
					ArrayList Ai = rgt.getAi2(tname,(String)userlist.get(j),0);
					ArrayList ei = rgt.getei2(tname,(String)userlist.get(j),0);
					for(int k=0;k<Ai.size();k++){
						Aiei.add(Ai.get(k));
						Aiei.add(ei.get(k));
					}
					//１人づつメンバを親グループへ追加
					this.AddUser2(tid, option, (String)userlist.get(j),Aiei);
				}
				
				System.out.println("OK");
				//グループリンクを削除
				this.deletegrouplink(gname,tname);
			}
			
			//gidより下のグループをすべて削除
			this.DeleteGroup3(gid);
			
			//　"GroupCount"の更新
			//　"Group"要素のリストを取得
			NodeList gInfolist = root.getElementsByTagName("Group");
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
	
	/**
	 * グループの削除
	 * (子グループもすべて削除)
	 * @param gid		グループID
	 * @return 追加されたドキュメントを返します
	 * @author kawata
	 */
	public Document DeleteGroup3(String gid){
		Element gInfo=this.getGroupElement(gid);
		Element group = null;
		String gname = null;
		if(gInfo == null){
			System.out.println("Group" + gid + "は存在しません。");
			return (this.document);
		}	
		try{
//			　指定されたグループIDを持つ要素の位置を取得
			int l;
			NodeList g = root.getElementsByTagName("Group");
			// Groupの数だけループ
			for(l=0; l<g.getLength(); l++){
				String gn = ((Element)g.item(l)).getAttribute("xmlns:ID");
				group = (Element)g.item(l);
				gname = ((Element)g.item(l)).getAttribute("xmlns:GroupName");
				// 要求のGroupIDだったとき
				if(gn.equals(gid)){
					break;
				}
			}
			ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
			ArrayList IncludeGroup = rgi.getIncludegroup2(gname);//gnameの子を読み取る
			
			for(int i=0;i<IncludeGroup.size();i++){
				ReadGroupInfoXML rgt = new ReadGroupInfoXML((String)IncludeGroup.get(i));
				String tid = rgt.getGroupID();
				this.DeleteGroup3(tid);//すべての子グループで再帰
			}
			
			//　"Group"要素のリストを取得
			NodeList gInfolist = root.getElementsByTagName("Group");	
			//　設計用のファイルを指定
			File file = new File("src/sglserver/conf/usr/xml_files/groups/sekkei_"
					+ ((Element)gInfolist.item(l)).getAttribute("xmlns:GroupName")
					+ ".xml");
			//　設計用ファイルの削除
			file.delete();
			
			//　グループ情報から指定されたグループ要素を削除
			root.removeChild(gInfo);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return(document);
	}

	/**
	 * グループの編集
	 * @param gname		 グループ名
	 * @param option	 鍵交換オプション(Normal, AntiDishonestInsider)
	 * @param acountlist ユーザ名リスト
	 * @param dlist      削除されるメンバリスト
	 * @param key        変更後のPublicKeyとSercretKey
	 * @param cer        変更後のAiとei
	 * @param uid        変更後の	Groupleader
	 * 
	 * @return 追加されたドキュメントを返します
	 * @author kawata
	 */
	public Document EditGroup(String gname, String option, ArrayList acountlist,ArrayList dlist,BigInteger[][] key,BigInteger[][]cer ,String uid){	
		try{
			ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
			ArrayList IncludeGroup = rgi.getIncludegroup(gname);
			ArrayList beforeuserlist = rgi.getMemberName2(gname);//変更前のメンバリスト取得
			NodeList gnamelist2 = root.getElementsByTagName("Group");//Group名取得
			for(int i=0; i<gnamelist2.getLength();i++){
				Element group = (Element)gnamelist2.item(i);
				String dname =  group.getAttribute("xmlns:GroupName");
				if(dname.equals(gname)){//gnameを見つけたら
					//PublicKeyとSercretKeyの更新
					NodeList list7 = group.getElementsByTagName("PublicKey");
					Element PK = (Element)list7.item(0);
					NodeList list8 = PK.getElementsByTagName("n");
					NodeList list9 = PK.getElementsByTagName("a");
					NodeList list10 = PK.getElementsByTagName("a0");
					NodeList list11 = PK.getElementsByTagName("y");
					NodeList list12 = PK.getElementsByTagName("g");
					NodeList list13 = PK.getElementsByTagName("h");
					NodeList list14 = group.getElementsByTagName("SecretKey");
					Element SK = (Element)list14.item(0);
					NodeList list15 = SK.getElementsByTagName("p1");
					NodeList list16 = SK.getElementsByTagName("q1");
					NodeList list17 = SK.getElementsByTagName("x");
					
					
					Element Nn = document.createElement("n");
					Element Na = document.createElement("a");
					Element Na0 = document.createElement("a0");
					Element Ny = document.createElement("y");
					Element Ng = document.createElement("g");
					Element Nh = document.createElement("h");
					Element Np1 = document.createElement("p1");
					Element Nq1 = document.createElement("q1");
					Element Nx = document.createElement("x");
					
					Nn.appendChild( document.createTextNode(String.valueOf(key[1][1])));
					PK.replaceChild(Nn, (Element)list8.item(0));
					Na.appendChild( document.createTextNode(String.valueOf(key[1][2])));
					PK.replaceChild(Na, (Element)list9.item(0));
					Na0.appendChild( document.createTextNode(String.valueOf(key[1][3])));
					PK.replaceChild(Na0, (Element)list10.item(0));
					Ny.appendChild( document.createTextNode(String.valueOf(key[1][4])));
					PK.replaceChild(Ny, (Element)list11.item(0));
					Ng.appendChild( document.createTextNode(String.valueOf(key[1][5])));
					PK.replaceChild(Ng, (Element)list12.item(0));
					Nh.appendChild( document.createTextNode(String.valueOf(key[1][6])));
					PK.replaceChild(Nh, (Element)list13.item(0));
					Np1.appendChild( document.createTextNode(String.valueOf(key[2][1])));
					SK.replaceChild(Np1, (Element)list15.item(0));
					Nq1.appendChild( document.createTextNode(String.valueOf(key[2][2])));
					SK.replaceChild(Nq1, (Element)list16.item(0));
					Nx.appendChild( document.createTextNode(String.valueOf(key[2][3])));
					SK.replaceChild(Nx, (Element)list17.item(0));
				
					//GroupLeaderの更新
					NodeList list18 = group.getElementsByTagName("GroupLeader");
					
					Element gleader = document.createElement("GroupLeader");
					gleader.appendChild( document.createTextNode(uid));
					group.replaceChild(gleader, (Element)list18.item(0));
				}
			}
			//acountlistの人数分繰り返す
			for(int i=0; i<acountlist.size(); i++){
				boolean b1 = false;
				for(int k=0; k<beforeuserlist.size(); k++){
					if(acountlist.get(i).equals(beforeuserlist.get(k))){//編集前も後にもいるuserを探す
						b1=true;
					}
				}
				if(b1==false){//新たに加えられるｕｓｅｒの場合
					this.Moveuser(gname,  option, (String)acountlist.get(i), cer, i);
				}
				else{//編集後も移動がないuserの場合
					//userがGroupInfomation上でどのグループに書かれているかを取得
					ArrayList allgroup = rgi.getGroupLinkName(gname, (String)acountlist.get(i));
					//そのuserのgnameでのAiを取得(gnameでの以前のAi)
					String Ai = rgi.getAi(gname,(String)acountlist.get(i),0);
					NodeList gnamelist = root.getElementsByTagName("Group");
					for(int k=0; k<gnamelist.getLength(); k++){
						Element group = (Element)gnamelist.item(k);
						String dataname =  group.getAttribute("xmlns:GroupName"); 
						for(int j=0;j<allgroup.size();j++){
							if(dataname.equals(allgroup.get(j))){//grnameと同じグループ名の要素を取り出す
								NodeList ulist = group.getElementsByTagName("Peer");
								for(int l=0;l<ulist.getLength();l++){
									Element Peer = (Element)ulist.item(l);
									NodeList list3 = Peer.getElementsByTagName("Name");
									Element PeerName = (Element)list3.item(0);
									String username = PeerName.getFirstChild().getNodeValue();
									//userと一致する要素を取得
									if(acountlist.get(i).equals(username)){
										NodeList list4 = Peer.getElementsByTagName("MembershipCertificates");
										Element MCer = (Element)list4.item(0);
										NodeList list5 = MCer.getElementsByTagName("Ai");
										NodeList list6 = MCer.getElementsByTagName("ei");
										for(int m=0;m<list5.getLength();m++){
											Element AiE = (Element)list5.item(m);
											String Ai2=AiE.getFirstChild().getNodeValue();
											//以前のAiと一致したら
											if(Ai.equals(Ai2)){
												//　Aiとeiを新しいcerに変更
												Element NAi = document.createElement("Ai");
												Element Nei = document.createElement("ei");
												NAi.appendChild( document.createTextNode(String.valueOf(cer[i][1])));
												MCer.replaceChild(NAi, (Element)list5.item(m));
												Nei.appendChild( document.createTextNode(String.valueOf(cer[i][2])));
												MCer.replaceChild(Nei, (Element)list6.item(m));
											}
										}
									}
								}
							}
						}
					}
				}
			}
			//削除メンバを削除する
			for(int i=0;i<dlist.size();i++){
				this.Moveuser2(gname, option, (String)dlist.get(i));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return(document);
	}
	
	/**
	 * ユーザを追加
	 * @param gid		グループID
	 * @param option	鍵交換オプション(Normal, AntiDishonestInsider)
	 * @param userlist	ユーザIDリスト
	 * @return 追加されたドキュメントを返します
	 * @author hujino
	 */
	public Document AddUser(String gid, String option, ArrayList acountlist){	
		int	addcnt = 50;	
		//　指定されたグループIDを持つグループが存在しない場合
		if(this.getGroupElement(gid) == null){
			System.out.println("Group" + gid + "は存在しません。");
			return (this.document);
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
                        System.out.println(uInfolist);
                        
			ArrayList userlist = new ArrayList(addcnt);
                        System.out.println("ユーザリストの中身"+userlist);
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
			//　RelayIPの取得
			//String rip = in.readLine();
			String rip = serverip;		
			//　ユーザを追加
			//　追加したいユーザの数だけ繰り返す
			for(int j=0; j<userlist.size(); j++){			
				//　userlistからj番目のユーザIDを取得
				String uid = (String)(userlist.get(j));	
                                System.out.println("j<userlist.size():▽▽▽▽▽▽▽▽▽▽▽");
				//　現在のユーザの数だけ繰り返す
				for(int i=0; i<=peer.getLength(); i++){
					// peerのi番目がnullのとき(=ノードの末尾に達したとき)
                                    
                                         System.out.println("i<=peer.getLength()▽▽▽▽▽▽▽▽▽▽▽");
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
                                                    System.out.println("k<uInfolist.getLength()▽▽▽▽▽▽▽▽▽▽▽");
							Element uInfo = (Element)uInfolist.item(k);						
							String gn = uInfo.getAttribute("xmlns:ID");
							//　追加したいユーザのIDとユーザ情報のIDが一致したら
                                                        System.out.println("gn:"+gn+"\nuid:"+uid);
							if(gn.equals(uid)){
								//　ユーザ情報ファイルから"UserName"を取得
                                                                
								DynamicPeerInformation dpinfo = new DynamicPeerInformation();
								String	pname = dpinfo.getUserName(uid);
                                                                System.out.println("\n▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
                                                                System.out.println("▼▼▼▼GroupInformation.xmlに"+k+"番目のユーザ"+pname+"のタグ情報追加処理を行います▼▼▼▼");
                                                                
								//　グループ情報ファイルにユーザ名を作成
								Element uName = document.createElement("Name");
								uName.appendChild(document.createTextNode(pname));
								mInfo.appendChild(uName);	
                                                                System.out.println("GroupInformationXml:AddUserメソッド："+k+"番目のユーザのNameタグを追加");
                                                                
								//　グループ情報ファイルにユーザ名を作成
								Element uAcount = document.createElement("UserAcount");
								uAcount.appendChild(document.createTextNode( (String)(acountlist.get(j)) ));
								mInfo.appendChild(uAcount);	
                                                                System.out.println("GroupInformationXml:AddUserメソッド："+k+"番目のユーザのUserAcountタグを追加");
                                                                
                                                                
								//　"IP"要素を作成
								Element uIP = document.createElement("IP");
								//　IPアドレスを取得
								String uip = dpinfo.getIP(pname);

								// uIPにIPアドレスを設定
								uIP.appendChild(document.createTextNode(uip));
								mInfo.appendChild(uIP);
                                                                System.out.println("GroupInformationXml:AddUserメソッド："+k+"番目のユーザのIPタグを追加");

								//　"RelayIP"要素を作成
								Element rIP = document.createElement("RelayIP");
								// rIPにRelayIPを設定
								rIP.appendChild(document.createTextNode(rip));
								mInfo.appendChild(rIP);
                                                                System.out.println("GroupInformationXml:AddUserメソッド："+k+"番目のユーザのRelayタグを追加");
                                                                System.out.println("\n▲▲▲▲GroupInformation.xmlへの"+k+"番目のユーザ"+pname+"のタグ情報追加処理の終了▲▲▲▲");
                                                                System.out.println("▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲");
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
								break;
							}
						}
						break;
					}
				}
			}
                        System.out.println("グループ設計書作成ループの終了");
			//　オプションの変更メソッド
			ChangeOption(gid, option);
			//　設計用ファイルの保存メソッド
			saveSekkei( (Element)gInfolist.item(l) );			
		}catch(Exception e){
			e.printStackTrace();
		}
		return(document);
	}


	/**
	 * ユーザを追加
	 * (1人づつ追加する)
	 * 
	 * @param gid		グループID
	 * @param option	鍵交換オプション(Normal, AntiDishonestInsider)
	 * @param acount    追加するユーザ
	 * @param Aiei      グループ認証
	 * 
	 * @return 追加されたドキュメントを返します
	 * @author kawata
	 */
	public Document AddUser2(String gid, String option, String acount,ArrayList Aiei){

		int	addcnt = 50;
		
		//指定されたグループIDを持つグループが存在しない場合
		if(this.getGroupElement(gid) == null){
			System.out.println("Group" + gid + "は存在しません。");
			return (this.document);
		}

		try{
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory u_factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = u_factory.newDocumentBuilder();
                        System.out.println("ドキュメントビルダーファクトリを生成しました");
			//　ユーザ情報用のdocumentとroot
			// パースを実行してDocumentオブジェクトを取得(ファイル読み込み)	
			u_document = builder.parse(new BufferedInputStream(new FileInputStream(UserDataFile)));
			u_root = u_document.getDocumentElement();

			//　ユーザ情報から指定された要素のリストを取得
			NodeList uInfolist = u_root.getElementsByTagName("DynamicInformation");

			ArrayList userlist = new ArrayList(addcnt);
			for(int m=0; m<uInfolist.getLength(); m++){
				Element acInfo = (Element)uInfolist.item(m);
				String acname =
					(acInfo.getElementsByTagName("PeerName")).item(0).getFirstChild().getNodeValue();
				if( acname.equals(acount) ){
					userlist.add( acInfo.getAttribute("xmlns:ID") );
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

			//　"Peer"要素（グループメンバー）のリストを取得
			NodeList peer = gInfo.getElementsByTagName("Peer");

			//　RelayIPの取得
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
                                                System.out.println("Peerタグを生成しました");
						Element pID = document.createElement("ID");
                                                System.out.println("IDタグを生成しました");

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
                                                                System.out.println((k+1)+"番目のユーザ名は"+pname+"です");
                                                                
								//　グループ情報ファイルにユーザ名を作成
								Element uName = document.createElement("Name");
								uName.appendChild(document.createTextNode(pname));
								mInfo.appendChild(uName);
                                                                System.out.println((k+1)+"番目のユーザ:"+uName+"のタグ情報をグループ設計書に追加しました");
								
                                                                //　グループ情報ファイルにユーザ名を作成
								Element uAcount = document.createElement("UserAcount");
								uAcount.appendChild(document.createTextNode( acount ));
								mInfo.appendChild(uAcount);
                                                                System.out.println((k+1)+"番目のユーザアカウント:"+uAcount+"のタグ情報をグループ設計書に追加しました");

								//　"IP"要素を作成
								Element uIP = document.createElement("IP");
								//　IPアドレスを取得
								String uip = dpinfo.getIP(pname);

								// uIPにIPアドレスを設定
								uIP.appendChild(document.createTextNode(uip));
								mInfo.appendChild(uIP);
                                                                System.out.println((k+1)+"番目のユーザのIPアドレス:"+uIP+"のタグ情報をグループ設計書に追加しました");

								//　"RelayIP"要素を作成
								Element rIP = document.createElement("RelayIP");
								// rIPにRelayIPを設定
								rIP.appendChild(document.createTextNode(rip));
								mInfo.appendChild(rIP);
                                                                System.out.println((k+1)+"番目のユーザのRelayIP:"+rIP+"のタグ情報をグループ設計書に追加しました");

								/***************追加********************/
								//　"MembershipCertificates"要素を作成
								Element mCF = document.createElement("MembershipCertificates");
								// mCFにMembershipCertificatesを設定
								mInfo.appendChild(mCF);
								for(int m=0;m<Aiei.size();m++){
									if(m%2==0){
										//　"Ai"要素を作成
										Element mCFAi = document.createElement("Ai");
										// mCFにMembershipCertificatesを設定
										mCFAi.appendChild(document.createTextNode((String)Aiei.get(m)));
										mCF.appendChild(mCFAi);
									}
									else if(m%2==1){
										//　"Ai"要素を作成
										Element mCFei = document.createElement("ei");
										// mCFにMembershipCertificatesを設定
										mCFei.appendChild(document.createTextNode((String)Aiei.get(m)));
										mCF.appendChild(mCFei);
									}
								}
								/*************ここまで*****************/

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
								mInfo.appendChild(uName);

								//　グループ情報ファイルにユーザ名を作成
								Element uAcount = document.createElement("UserAcount");
								uAcount.appendChild(document.createTextNode( acount ));
								mInfo.appendChild(uAcount);

								//　"IP"要素を作成
								Element uIP = document.createElement("IP");
								//　IPアドレスを取得
								String uip = dpinfo.getIP(pname);

								// uIPにIPアドレスを設定
								uIP.appendChild(document.createTextNode(uip));
								mInfo.appendChild(uIP);

								//　"RelayIP"要素を作成
								Element rIP = document.createElement("RelayIP");
								// rIPにRelayIPを設定
								rIP.appendChild(document.createTextNode(rip));
								mInfo.appendChild(rIP);


								/***************追加********************/
								//　"MembershipCertificates"要素を作成
								Element mCF = document.createElement("MembershipCertificates");
								// mCFにMembershipCertificatesを設定
								mInfo.appendChild(mCF);

								for(int m=0;m<Aiei.size();m++){
									if(m%2==0){
										//　"Ai"要素を作成
										Element mCFAi = document.createElement("Ai");
										// mCFにMembershipCertificatesを設定
										mCFAi.appendChild(document.createTextNode((String)Aiei.get(m)));
										mCF.appendChild(mCFAi);
									}
									else if(m%2==1){
										//　"Ai"要素を作成
										Element mCFei = document.createElement("ei");
										// mCFにMembershipCertificatesを設定
										mCFei.appendChild(document.createTextNode((String)Aiei.get(m)));
										mCF.appendChild(mCFei);
									}
								}
								/*************ここまで*****************/

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
	
	/**
	 * ユーザを追加
	 * 
	 * @param gid		グループID
	 * @param option	鍵交換オプション(Normal, AntiDishonestInsider)
	 * @param userlist	ユーザIDリスト
	 * 
	 * @return 追加されたドキュメントを返します
	 * @author kawata
	 */
	/*public Document AddUser(String gid, String option, ArrayList acountlist,BigInteger[][] cer){

		int	addcnt = 50;

		//　指定されたグループIDを持つグループが存在しない場合
		if(this.getGroupElement(gid) == null){
			System.out.println("Group" + gid + "は存在しません。");
			return (this.document);
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
					//System.out.println(acountlist.get(n));
					if( acname.equals(acountlist.get(n)) ){
						userlist.add( acInfo.getAttribute("xmlns:ID") );
						//System.out.println(userlist.get(n) + "    kok");
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
				System.out.println("ID=" + uid);
				//　現在のユーザの数だけ繰り返す
				for(int i=0; i<=peer.getLength(); i++){
					// peerのi番目がnullのとき(=ノードの末尾に達したとき)
					//System.out.println("peer:" + peer.item(i));
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

								//　グループ情報ファイルにユーザ名を作成
								Element uName = document.createElement("Name");
								uName.appendChild(document.createTextNode(pname));
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

								//　"MembershipCertificates"要素を作成
								Element mCF = document.createElement("MembershipCertificates");
								// mCFにMembershipCertificatesを設定
								mInfo.appendChild(mCF);

								//　"Ai"要素を作成
								Element mCFAi = document.createElement("Ai");
								// mCFにMembershipCertificatesを設定
								mCFAi.appendChild(document.createTextNode(String.valueOf(cer[j][1])));
								mCF.appendChild(mCFAi);

								//　"Ai"要素を作成
								Element mCFei = document.createElement("ei");
								// mCFにMembershipCertificatesを設定
								mCFei.appendChild(document.createTextNode(String.valueOf(cer[j][2])));
								mCF.appendChild(mCFei);

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

								//　"MembershipCertificates"要素を作成
								Element mCF = document.createElement("MembershipCertificates");
								// mCFにMembershipCertificatesを設定
								mInfo.appendChild(mCF);

								//　"Ai"要素を作成
								Element mCFAi = document.createElement("Ai");
								// mCFにMembershipCertificatesを設定
								mCFAi.appendChild(document.createTextNode("77777777"));
								mCF.appendChild(mCFAi);

								//　"Ai"要素を作成
								Element mCFei = document.createElement("ei");
								// mCFにMembershipCertificatesを設定
								mCFei.appendChild(document.createTextNode("88888888"));
								mCF.appendChild(mCFei);

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
	}*/
	
	/**
	 * ユーザの移動(親から子へ)
	 * (IncludeGroupLinkが１つしかない場合にしか使えない)
	 * 
	 * @param gname		子のグループ名
	 * @param option    オプション
	 * @param username	ユーザーの名前
	 * @param cer　　　　　　
	 * 
	 * @return 追加されたドキュメントを返します
	 * @author kawata
	 */
	public Document Moveuser(String gname,  String option, String username, BigInteger[][] cer, int i){
		//　指定されたグループIDを持つグループが存在しない場合
		ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
		String gid = rgi.getGroupID();
		String tname = null;
		if(this.getGroupElement(gid) == null){
			System.out.println("Group" + gid + "は存在しません。");
			return (this.document);
		}
		try{
			ArrayList IncludeGroup = rgi.getIncludegroup(gname);//gnameの親の名前を持ってくる
			ArrayList Aiei = new ArrayList();
			for(int k=0;k<2;k++){
				Aiei.add(String.valueOf(cer[i][k+1]));//Aiとeiを入れる
			}
			for(int k=0;k<IncludeGroup.size();k++){
				tname = (String)IncludeGroup.get(k);
				break;
			}
			if(tname != null){
				ArrayList Ai = rgi.getAi2(tname,username,0);//親のAi
				ArrayList ei = rgi.getei2(tname,username,0);//親のei
				for(int k=0;k<Ai.size();k++){
					Aiei.add(Ai.get(k));
					Aiei.add(ei.get(k));
				}
			}
			ArrayList Acount = new ArrayList();
			Acount.add(username);//userをAcountにいれる
			if(tname != null){
				this.includegroup(tname, Acount);//tnameにいるusernameを削除する
			}

			this.AddUser2(gid, option, username ,Aiei);//gnameにusernameを追加する
		}catch(Exception e){
			e.printStackTrace();
		}
		return(document);
	}
	
	/**
	 * ユーザの移動(子から親へ)
	 * (IncludeGroupLinkが１つしかない場合にしか使えない)
	 * 
	 * @param gname		子のグループ名
	 * @param option    オプション
	 * @param username	ユーザーの名前　　　　　
	 * 
	 * @return 追加されたドキュメントを返します
	 * @author kawata
	 */
	public Document Moveuser2(String gname,  String option, String username){
		//　指定されたグループIDを持つグループが存在しない場合
		ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
		String gid = rgi.getGroupID();
		if(this.getGroupElement(gid) == null){
			System.out.println("Group" + gid + "は存在しません。");
			return (this.document);
		}
		try{
			ArrayList IncludeGroup = rgi.getIncludegroup(gname);//gnameの親の名前を持ってくる
			ArrayList Aiei = new ArrayList();
			ArrayList Ai = new ArrayList();
			ArrayList ei = new ArrayList();
			for(int k=0; k<IncludeGroup.size();k++){
				Ai = rgi.getAi2((String)IncludeGroup.get(k),username,0);//親のAi
				ei = rgi.getei2((String)IncludeGroup.get(k),username,0);//親のei
			}
			for(int k=0;k<Ai.size();k++){
				Aiei.add(Ai.get(k));
				Aiei.add(ei.get(k));
			}
			ArrayList Acount = new ArrayList();
			Acount.add(username);//userをAcountにいれる
			this.includegroup(gname, Acount);//gnameにいるusernameを削除する
			for(int k=0;k<IncludeGroup.size();k++){
				ReadGroupInfoXML rgt = new ReadGroupInfoXML((String)IncludeGroup.get(k));
				String tid = rgt.getGroupID();
				this.AddUser2(tid, option, username ,Aiei);//tnameにusernameを追加する
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return(document);
	}
	
	/**
	 * id文字列からユーザーの要素を返す
	 * @param gid	グループID
	 * @return idと同じユーザーのElement要素
	 * @author hujino
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

	/**
	 * オプションの変更
	 * @param gid		グループID
	 * @param option	鍵交換オプション(Normal, AntiDishonestInsider)
	 * @return 追加されたドキュメントを返します
	 * @author hujino
	 */
	public void ChangeOption(String gid, String option){
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
			saveSekkei( (Element)root.getElementsByTagName("Group").item(l));				
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * このドキュメントを保存します
	 * @param filename 保存するファイル名(パスを含む)
	 * @throws Exception ファイルの保存時に起きたエラー
	 * @author hujino
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

	/**
	 * 設計用XMLファイルとして追加・変更されたグループを保存
	 * @param groot		保存するグループの"Group"要素
	 * @throws Exception ファイルの保存時に起きたエラー
	 * @author hujino
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
			Element gInfo = (Element)gInfolist.item(0);
			// "GroupLeader"要素の削除
			NodeList read  = gInfo.getElementsByTagName("GroupLeader");
			if(read.getLength()!=0){
				Element leader  = (Element)read.item(0);
				gInfo.removeChild(leader);
			}
			//"PublicKey"要素の削除
			NodeList PublicKey  = gInfo.getElementsByTagName("PublicKey");
			if(PublicKey.getLength()!=0){
				Element pub  = (Element)PublicKey.item(0);
				gInfo.removeChild(pub);
			}
			//"SecretKey"要素の削除
			NodeList SecretKey  = gInfo.getElementsByTagName("SecretKey");
			if(SecretKey.getLength()!=0){
				Element sec  = (Element)SecretKey.item(0);
				gInfo.removeChild(sec);
			}
			NodeList Peer  = gInfo.getElementsByTagName("Peer");
			for(int i=0; i<Peer.getLength(); i++){
				Element p  = (Element)Peer.item(i);
				NodeList MembershipCertificates  = p.getElementsByTagName("MembershipCertificates");
				if(MembershipCertificates.getLength()!=0){
					Element MC  = (Element)MembershipCertificates.item(0);
					p.removeChild(MC);
				}
			}
			
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
}
