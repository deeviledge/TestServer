/*
 * 作成日: 2004/12/28
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package sglserver.groupadmin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
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
import org.w3c.dom.NodeList;





/**
 * @author Satoshi
 *
 *GroupInformation.xml？を扱う？クラス
 *
 */
//グループ全体を持つクラス
//いろいろなデータを返す関数群
class Group_Key_Infomation {
	private int GroupCount;	//グループ数を格納
	private List GroupList;	//グループを格納するリスト
	
	// 引数無しコンストラクタ
	public Group_Key_Infomation(){
		GroupList = new ArrayList();
		// xml読み込み
		// 引数に指定したxmlファイルを読み込む
		//FileInput(FileName);
	}
	
	public Group_Key_Infomation(String FileName){
		GroupList = new ArrayList();
		// xml読み込み
		// 引数に指定したxmlファイルを読み込む
		FileInput(FileName);
	}

	/*
	public void newXmlFile(){
		try{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Group_Key_Infomation");
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	*/
	
	public void FileInput(String FileName){
		try{

		// ドキュメントビルダーファクトリを生成
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// ドキュメントビルダーを生成
		DocumentBuilder builder = factory.newDocumentBuilder();
		// パースを実行してDocumentオブジェクトを取得
//		Document document = builder.parse("sekkei.xml");
		Document document = builder.parse( FileName );
		// DOMツリーから各種要素の設定
		System.out.println(FileName);
		setData(document);
		
//		appendGroup(document,"root","blue");
//		removeGroup(document,"orange");
//		changeGroupName(document,"red","green");
		
//		appendUser(document,"blue","Rimu",11,"192.168.0.122","ABCDEF");
//		removeUser(document,"orange",0);
//		changeUserId(document, 6, 11);
//		changeUserName(document, 6, "Rimu");
//		changeUserIp(document, 6, "192.168.93.166");

/*
		//idからリストを返す
		List userList = new ArrayList();
		userList = getUser(document,6);
		for(int i=0;i<userList.size();i++){
			Element e = (Element)userList.get(i);
			NodeList nameList = e.getElementsByTagName("Name");
			String name = nameList.item(0).getFirstChild().getNodeValue();
			System.out.println("name:"+name);
		}
*/
		//保存
//		saveFile(document,"newXML.xml");
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	// DOMツリーから各種要素の設定
	public void setData(Document document){
		try{
		// ルート要素を取得
		Element root = document.getDocumentElement();
		
		// GroupCount要素のリストを取得
		NodeList gc = root.getElementsByTagName("GroupCount");
		// GroupCount要素を取得
		Element gcElement = (Element)gc.item(0);
		// GroupCount要素の最初の子ノードの値を取得
		String gcount = gcElement.getFirstChild().getNodeValue();
		// ノードの値をint型に変換して、グループ数GroupCountの値に設定
		GroupCount = Integer.parseInt( gcount );
		
		// グループの所属しているユーザーの設定
		// Group要素のリストを取得
		NodeList g = root.getElementsByTagName("Group");
		
		// Group要素の数だけループ
		int i;
		Element gElement;
		String gname;
		for(i=0; i<g.getLength(); i++){
			// Group要素を取得
			gElement = (Element)g.item(i);
			// GroupName属性の値を取得
			gname = gElement.getAttribute("xmlns:GroupName");
			//グループリストに新規グループを追加
			addGroup(new Group(gname));

			// 親ノードがGroupの場合
			if( g.item(i).getParentNode().getNodeName().equals( "Group" ) ){
				Element gpElement = (Element)g.item(i).getParentNode();
				String gpname = gpElement.getAttribute("xmlns:GroupName");
//				System.out.println("gname:"+ gname +",Parent gname:"+ gpname);
				getGroup(gpname).addGroup(getGroup(gname));
			}
		}

		for(i=0; i<g.getLength(); i++){
			gElement = (Element)g.item(i);
			gname = gElement.getAttribute("xmlns:GroupName");
	
			// User要素のリストを取得
//			NodeList u = gElement.getElementsByTagName("User");
			NodeList u = gElement.getElementsByTagName("Peer");
			// User要素の数だけループ
			for(int j=0; j<u.getLength(); j++){
				// User要素を取得
				Element uElement = (Element)u.item(j);
				Element gupElement = (Element)u.item(j).getParentNode();
				String gupname = gupElement.getAttribute("xmlns:GroupName");
//				System.out.println("gup:" + gupname);
//				System.out.println("g  :" + gname);
				// ユーザーを設定
				setUser(gname,gupname,uElement);
			}
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	// 文字列からGroupの要素を返す
	public Element getGroupElement(Document document, String gname){
		// ルート要素を取得
		Element eroot = document.getDocumentElement();
		// ルート要素からGroupのノードリストを取得
		NodeList g = eroot.getElementsByTagName("Group");
		// Groupの要素を、とりあえずルート要素にしておく
		Element gElement = document.getDocumentElement();
		// rootを返す
		if(gname.equals("root")){
			return( gElement );
		}
		// Gropuの数だけループ
		for(int i=0; i<g.getLength(); i++){
			// i番目のGroupを取得
			gElement = (Element)g.item(i);
			// Group名を取得
			String gn = gElement.getAttribute("xmlns:GroupName");
			// もし要求のGroup名ならば、
			if(gn.equals(gname)){
				// ループを終了させて
				i = g.getLength();
				// Groupの要素を返す
				return( gElement );
			}
		}
		// ループの最後までGroupが見つからなければnullを返す
		return( null );
	}
	// Groupを追加
	public void appendGroup(Document document, String root, String gname){
		// 文字列rootからElementを取得
		Element gElement = getGroupElement(document,root);

		// Groupタグ作成
		Element group = document.createElement("Group");
		// 属性ノードの作成
		Attr groupname = document.createAttribute("xmlns:GroupName");
		// 属性ノードの値
		groupname.setValue( gname );
		// 属性ノードを要素に追加
		group.setAttributeNode( groupname );
		if(gElement!=null){
			// 親要素にGroupを追加
			gElement.appendChild( group );
		}else{
			// 例外（仮）
			System.out.println("appendGroup Exception:グループが見つかりません。");
		}
	}
	// Groupの削除
	public void removeGroup(Document document, String gname){
		// 文字列gnameからグループのElementを取得
		Element groupElement = getGroupElement(document,gname);
		// グループのElementがnullでないならば、
		if(groupElement!=null){
			// gnameグループの要素を全て親要素に移動
			moveAllNode(document, groupElement, (Element)groupElement.getParentNode() );
			// gnameグループの削除
			groupElement.getParentNode().removeChild( groupElement );
		}else{
			// 例外（仮）
			System.out.println("removeGroup Exception:グループが見つかりません。");
		}
	}
	// Groupの置換
	public void changeGroupName(Document document, String gname1, String gname2){
		// 文字列gname1からElementを取得
		Element group1Element = getGroupElement(document,gname1);
		// グループgname1から親のElementを取得
		Element parentElement = (Element)group1Element.getParentNode();
		 
		// Groupタグ作成
		Element group2Element = document.createElement("Group");
		// 属性ノードの作成
		Attr groupname = document.createAttribute("xmlns:GroupName");
		// 属性ノードの値
		groupname.setValue( gname2 );
		// 属性ノードを要素に追加
		group2Element.setAttributeNode( groupname );
		
		if(group1Element!=null){
			// グループgname1の親にグループgname2を追加
			parentElement.appendChild( group2Element );
			// グループgname1の要素を全てグループgname2に移動
			moveAllNode(document, group1Element, group2Element);
			// グループgname1を削除
			parentElement.removeChild( group1Element );
		}else{
			// 例外（仮）
			System.out.println("replaceGroup Exception:グループが見つかりません。");
		}
	}
	// ある要素の全てのノードを別の要素に移動
	public void moveAllNode(Document document, Element element1, Element element2){
		// Elementがnullでないならば、
		if(element1!=null && element2!=null){
			// グループに所属する全てのノードを取得
			NodeList gl = element1.getChildNodes();
			// ノードの数だけ繰り返す
			int roop = gl.getLength();
			for(int i=0;i<roop;i++){
				// 全てのノードを別のノードに移動
				element2.appendChild( gl.item(0) );
			}
		}else{
			// 例外（仮）
			System.out.println("removeGroup Exception:Elementが見つかりません。");
		}
	}
	// ユーザーのIDからリストを返す
	public List getUser(Document document, int id){
		// ルート要素を取得
		Element root = document.getDocumentElement();
		// User要素のリストを取得
		NodeList u = root.getElementsByTagName("Peer");
		// 
		List UserList = new ArrayList();
		// User要素の数だけループ
		for(int j=0; j<u.getLength(); j++){
			// User要素を取得
			Element uElement = (Element)u.item(j);
			// ID要素のリストを取得
			NodeList idList = uElement.getElementsByTagName("ID");
			// ID要素を取得して、最初の子ノードの値を取得
			String sid = idList.item(0).getFirstChild().getNodeValue();
			// 文字から数字への変換
			int id2 = Integer.parseInt(sid);
			// もし指定されたIDなら
			if( id == id2 ){
				// リストにElementを追加
				UserList.add(uElement);
			}
		}
		return( UserList );
	}
	// ユーザーを追加
	public void appendUser(Document document, String root, String name, int id, String ip, String rIp){
		// 文字列rootからElementを取得
		Element gElement = getGroupElement(document,root);
		// 新規要素、タグ
		Element user = document.createElement("Peer");

		// Nameタグ作成
		Element ename = document.createElement("Name");
		// Nameタグのノード
		ename.appendChild(document.createTextNode( name ));
		// UserにNameを追加
		user.appendChild( ename );

		// IDタグ作成
		Element eid = document.createElement("ID");
		// idタグのノード
		eid.appendChild(document.createTextNode( id +"" ));
		// Userにidを追加
		user.appendChild( eid );

		// IPタグ作成
		Element eip = document.createElement("IP");
		// IPタグのノード
		eip.appendChild(document.createTextNode( ip ));
		// UserにIPを追加
		user.appendChild( eip );

		// RelayIPタグ作成
		Element erip = document.createElement("RelayIP");
		// RelayIPタグのノード
		erip.appendChild(document.createTextNode( rIp ));
		// UserにPublicKeyを追加
		user.appendChild( erip );

		if(gElement!=null){
			// 親要素にUserを追加
			gElement.appendChild( user );
//			System.out.println("apUser Name:"+ name +",ID:"+ id);
		}else{
			// 例外（仮）
			System.out.println("appendUser Exception:"+ root +"グループが見つかりません。");
		}
	}
	// ユーザーの削除
	public void removeUser(Document document, String groupname, int id){
		// IDからユーザーのElementを取得
		Element userElement = getUserElement(document,groupname,id);
		// ユーザーのElementから親のElementを取得
		Element parentElement = (Element)userElement.getParentNode();
		if(userElement==null){
			// 例外（仮）
			System.out.println("removeUser Exception:ユーザーが見つかりません。");
		}else if(parentElement==null){
			// 例外（仮）
			System.out.println("removeUser Exception:グループが見つかりません。");
		}else{
			// parentElementに属するuserElementを削除
			parentElement.removeChild( userElement );
		}
	}
	// ユーザーのNameの変更
	public void changeUserName(Document document, int id, String name){
		// ユーザーのリストの準備
		List userList = new ArrayList();
		// IDがidのユーザーのリストを取得
		userList = getUser(document, id);
		// リストのサイズだけ繰り返す
		for(int i=0; i<userList.size(); i++){
			// リストからユーザーの要素を取得
			Element uElement = (Element)userList.get(i);
			// 既存のNameのノードを取得
			NodeList nl = uElement.getElementsByTagName("Name");
			// 既存のNameを削除
			uElement.removeChild( nl.item(0) );
			// Nameタグ作成
			Element el = document.createElement("Name");
			// Nameタグのノード
			el.appendChild(document.createTextNode( name ));
			// IDのノードを取得
			NodeList nl2 = uElement.getElementsByTagName("ID");
			// ユーザーに新NameをIDの前に挿入
			uElement.insertBefore( el, nl2.item(0) );
		}
	}
	// ユーザーのIDの変更
	public void changeUserId(Document document, int id, int id2){
		// ユーザーのリストの準備
		List userList = new ArrayList();
		// IDがidのユーザーのリストを取得
		userList = getUser(document, id);
		// リストのサイズだけ繰り返す
		for(int i=0; i<userList.size(); i++){
			// リストからユーザーの要素を取得
			Element uElement = (Element)userList.get(i);
			// 既存のIDのノードを取得
			NodeList nl = uElement.getElementsByTagName("ID");
			// 既存のIDを削除
			uElement.removeChild( nl.item(0) );
			// IDタグ作成
			Element el = document.createElement("ID");
			// IDタグのノード
			el.appendChild(document.createTextNode( id2 +"" ));
			// IPのノードを取得
			NodeList nl2 = uElement.getElementsByTagName("IP");
			// ユーザーに新IDをIPの前に挿入
			uElement.insertBefore( el, nl2.item(0) );
		}
	}
	// ユーザーのIPの変更
	public void changeUserIp(Document document, int id, String ip){
		// ユーザーのリストの準備
		List userList = new ArrayList();
		// IDがidのユーザーのリストを取得
		userList = getUser(document, id);
		// リストのサイズだけ繰り返す
		for(int i=0; i<userList.size(); i++){
			// リストからユーザーの要素を取得
			Element uElement = (Element)userList.get(i);
			// 既存のIPのノードを取得
			NodeList nl = uElement.getElementsByTagName("IP");
			// 既存のIPを削除
			uElement.removeChild( nl.item(0) );
			// IPタグ作成
			Element el = document.createElement("IP");
			// IPタグのノード
			el.appendChild(document.createTextNode( ip ));
			// PublicKeyのノードを取得
			NodeList nl2 = uElement.getElementsByTagName("RelayIP");
			// ユーザーに新NameをIDの前に挿入
			uElement.insertBefore( el, nl2.item(0) );
		}
	}
	// ユーザーのPublicKeyの変更
	public void changeUserRelayIP(Document document, int id, String key){
		// ユーザーのリストの準備
		List userList = new ArrayList();
		// IDがidのユーザーのリストを取得
		userList = getUser(document, id);
		// リストのサイズだけ繰り返す
		for(int i=0; i<userList.size(); i++){
			// リストからユーザーの要素を取得
			Element uElement = (Element)userList.get(i);
			// 既存のRelatyIPのノードを取得
			NodeList nl = uElement.getElementsByTagName("RelayIP");
			// 既存のRelayIPを削除
			uElement.removeChild( nl.item(0) );
			// RelayIPタグ作成
			Element el = document.createElement("RelayIP");
			// RelayIPタグのノード
			el.appendChild(document.createTextNode( key ));
			// ユーザーに新Nameを末尾に挿入
			uElement.appendChild( el );
		}
	}
	// 指定されたあるグループに所属するユーザーのElementを返す
	public Element getUserElement(Document document, String groupname, int id){
		// 文字列groupnameからElementを取得
		Element gElement = getGroupElement(document,groupname);
		// 指定されたグループ内のユーザーの要素を取得
		NodeList u = gElement.getElementsByTagName("Peer");
		// ユーザー要素の数だけループ
		for(int j=0; j<u.getLength(); j++){
			// User要素を取得
			Element uElement = (Element)u.item(j);
			// ID要素のリストを取得
			NodeList idList = uElement.getElementsByTagName("ID");
			// ID要素を取得して、最初の子ノードの値を取得
			String sid = idList.item(0).getFirstChild().getNodeValue();
//			System.out.println("ID:"+ sid);
			// 文字列を数字に変換
			int uid = Integer.parseInt(sid);
			// もし指定されたユーザーならば、
			if( id == uid ){
				j = u.getLength();
				// そのユーザーのElementを返す
				return(uElement);
			}
		}
		// 指定されたユーザーが見つからなかった場合
		return null;
	}

	// あるグループに所属する全てのユーザーを、別のグループに移動
		public void moveGroupAllUser(Document document,Element groupElement,Element othergroupElement){
			// ユーザー要素を取得
			NodeList userNodeList = groupElement.getElementsByTagName("Peer");
			// User要素の数だけループ
			int roop = userNodeList.getLength();
			for(int i=0; i<roop; i++){
				// User要素を取得
				Element uElement = (Element)userNodeList.item(0);
				
				// Name要素のリストを取得
				NodeList nList = uElement.getElementsByTagName("Name");
				// Name要素を取得して、最初の子ノードの値を取得
				String name = nList.item(0).getFirstChild().getNodeValue();
				// ID要素のリストを取得
				NodeList idList = uElement.getElementsByTagName("ID");
				// ID要素を取得して、最初の子ノードの値を取得
				String id = idList.item(0).getFirstChild().getNodeValue();
				// IP要素のリストを取得
				NodeList ipList = uElement.getElementsByTagName("IP");
				// IP要素を取得して、最初の子ノードの値を取得
				String ip = ipList.item(0).getFirstChild().getNodeValue();
				// RelayIp要素のリストを取得
				NodeList rIpList = uElement.getElementsByTagName("RelayIP");
				// RelayIp要素を取得して、最初の子ノードの値を取得
				String rIp = rIpList.item(0).getFirstChild().getNodeValue();
				
				// Group名を取得
				String ogn = othergroupElement.getAttribute("xmlns:GroupName");
				if( othergroupElement.getNodeName().equals("Group_Key_Information") ){
					ogn = "root";
				}
	//			System.out.println(userNodeList.getLength() +"mvUser Name:"+ name +",ID:"+ id);
				// ユーザーの設定
				appendUser(document, ogn, name, Integer.parseInt(id), ip, rIp);
				// Group名を取得
				String gn = groupElement.getAttribute("xmlns:GroupName");
				if( groupElement.getNodeName().equals("Group_Key_Information") ){
					gn = "root";
				}
				// ユーザーの削除
				removeUser(document, gn, Integer.parseInt(id));
			}
		}
	// XMLファイルを保存する
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
	// ユーザーの設定
	// 第一引数の値はグループ名
	// 代に引数の値はUser要素
	public void setUser(String gname,String gpname,Element uElement){
		// Name要素のリストを取得
		NodeList nList = uElement.getElementsByTagName("Name");
		// Name要素を取得して、最初の子ノードの値を取得
		String name = nList.item(0).getFirstChild().getNodeValue();
		
		// ID要素のリストを取得
		NodeList idList = uElement.getElementsByTagName("ID");
		// ID要素を取得して、最初の子ノードの値を取得
		String id = idList.item(0).getFirstChild().getNodeValue();
		
		// IP要素のリストを取得
		NodeList ipList = uElement.getElementsByTagName("IP");
		// IP要素を取得して、最初の子ノードの値を取得
		String ip = ipList.item(0).getFirstChild().getNodeValue();
		
		// RelayIP要素のリストを取得
		NodeList rIpList = uElement.getElementsByTagName("RelayIP");
		// IP要素を取得して、最初の子ノードの値を取得
		String rIp = rIpList.item(0).getFirstChild().getNodeValue();
		// ユーザーの追加
		
		getGroup(gname).addUser(new User(name,Integer.parseInt(id),ip,rIp,getGroup(gpname)));
		
//		System.out.println("GroupName:"+ gname +",Name:"+ name +",ID:"+ id +",IP:"+ ip +",PublicKey:");
	}
	// グループ数GroupCountの値を設定
	public void setGroupSize(int i){
		GroupCount = i;
	}
	// グループ数GroupCountの値を返す
	public int getGroupSize(){
		return(GroupCount);
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
	// いろいろなデータを返す
	public void returnData(){
		boolean roop = true;//ループ用の変数
		BufferedReader br = new BufferedReader(new InputStreamReader( System.in ));//文字入力の準備
		String s1;//知りたい値の要素を代入
		String s2;//知りたい値の値を代入
		String s3;//知りたい値の対象を代入
		while(roop){
			try{
				// 入力
				System.out.print("---\n要素：");
				s1 = br.readLine();
				// exitが入力されたら終了
				if(s1.equalsIgnoreCase("exit")){
					roop = false;
					continue;
				}
				System.out.print("値：");
				s2 = br.readLine();
				System.out.print("対象：");
				s3 = br.readLine();
				// グループについて知りたい場合
				if(s1.equals("Group")){
					// あるグループの人数が知りたい場合
					if(s3.equalsIgnoreCase("num")){
						System.out.println("グループ"+ s2 +"の人数："+ getGroup(s2).getListSize() );
					// あるグループの人数以外が知りたい場合
					}else{
						for(int i=0; i<getGroup(s2).getListSize(); i++){
							// あるグループの名前の一覧が知りたい場合
							if(s3.equals("Name")){
								System.out.println("グループ"+ s2 +"の"+ s3 +"："+ getGroup(s2).getUser(i).getName() );
							// あるグループのIDの一覧が知りたい場合
							}else if(s3.equals("ID")){
								System.out.println("グループ"+ s2 +"の"+ s3 +"："+ getGroup(s2).getUser(i).getID() );
							// あるグループのIPアドレスの一覧が知りたい場合
							}else if(s3.equals("IP")){
								System.out.println("グループ"+ s2 +"の"+ s3 +"："+ getGroup(s2).getUser(i).getIP() );
							// あるグループのRelayIPの一覧が知りたい場合
							}else if(s3.equals("RelayIP")){
								System.out.println("グループ"+ s2 +"の"+ s3 +"："+ getGroup(s2).getUser(i).getRelayIP() );
							}
						}
						for(int i=0; i<getGroup(s2).getGroupListSize(); i++){
							if(s3.equals("Group")){
								System.out.println("グループ"+ s2 +"の"+ s3 +"："+ getGroup(s2).getGroup(i).getGroupName() );
							}
						}
					}
				// ユーザーについて知りたい場合
				}else{
//					System.out.println(s1 +":"+ s2 +"の"+ s3 +"："+ getElement(s1,s2,s3));
					String str = "";
					for(int i=0; i<GroupList.size(); i++){
						for(int j=0; j<getGroup(i).getListSize(); j++){
							if(s1.equals("Name")){
								if(getGroup(i).getUser(j).getName().equals(s2) || s2.equalsIgnoreCase("all")){
									if(s3.equals("ID")){
										str = ""+ getGroup(i).getUser(j).getID();
									}else if(s3.equals("IP")){
										str = getGroup(i).getUser(j).getIP();
									}else if(s3.equals("RelayIP")){
										str = getGroup(i).getUser(j).getRelayIP();
									}else if(s3.equals("Group")){
										str = getGroup(i).getGroupName();
									}
								}
							}else if(s1.equals("ID")){
								if(getGroup(i).getUser(j).getID() == Integer.parseInt(s2)){
									if(s3.equals("Name")){
										str = getGroup(i).getUser(j).getName();
									}else if(s3.equals("IP")){
										str = getGroup(i).getUser(j).getIP();
									}else if(s3.equals("RelayIP")){
										str = getGroup(i).getUser(j).getRelayIP();
									}else if(s3.equals("Group")){
										str = getGroup(i).getGroupName();
									}
								}
							}else if(s1.equals("IP")){
								if(getGroup(i).getUser(j).getIP().equals(s2)){
									if(s3.equals("Name")){
										str = getGroup(i).getUser(j).getName();
									}else if(s3.equals("ID")){
										str = ""+ getGroup(i).getUser(j).getID();
									}else if(s3.equals("RelayIP")){
										str = getGroup(i).getUser(j).getRelayIP();
									}else if(s3.equals("Group")){
										str = getGroup(i).getGroupName();
									}
								}
							}else if(s1.equals("RelayIP")){
								if(getGroup(i).getUser(j).getRelayIP().equals(s2)){
									if(s3.equals("Name")){
										str = getGroup(i).getUser(j).getName();
									}else if(s3.equals("ID")){
										str = ""+ getGroup(i).getUser(j).getID();
									}else if(s3.equals("IP")){
										str = getGroup(i).getUser(j).getIP();
									}else if(s3.equals("Group")){
										str = getGroup(i).getGroupName();
									}
								}
							}
							if(!str.equals("")){
								System.out.println(s1 +":"+ s2 +"の"+ s3 +"："+ str);
							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	// 文字列からグループを返す
	public Group getGroup(String s){
		for(int i=0; i< GroupList.size(); i++){
			if( getGroup(i).getGroupName().equals(s) ){
				return getGroup(i);
			}
		}
		return null;
	}
	public int getGroupCount(){
		return GroupCount;
	}
}




