package sglserver.pskey;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

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

import sglserver.peerbasicinformation.PeerBasicInformationEdit;

/**
 * PreSharedKeyのXML文書を作成するクラス(サーバー用)
 * @author hiroki,kinbara2007/11/15
 * @version 1.2
 * @作成日: 2005/01/12
 * @最終更新日:2008/10/31
 */
public class PreSharedKeyGenerator {

	private Document document;	//XMLドキュメント
	private Element root;		//XMLのルートノード
	private int MAX_MEMBER;	//ID-NIKSの表の行数(または、列数)

	/**
	 * ID-NIKSの表を30×30とする(３０人より多い場合はユーザの人数×ユーザの人数)
	 */
	public PreSharedKeyGenerator(){
		PeerBasicInformationEdit peer = new PeerBasicInformationEdit();
		if(peer.getGroupLength() <= 30){ // 30人以下の場合
			MAX_MEMBER = 30;
		}else{ // それ以外
			MAX_MEMBER = peer.getGroupLength();
		}
	}

	/**
	 * ID-NIKSの表をmaxmember×maxmemberとする
	 * @param maxmember 
	 * @author hiroki
	 */
	public PreSharedKeyGenerator(int maxmember){
		MAX_MEMBER = maxmember;
	}

	/** 
	 * UserIDがsIDであるユーザーの
	 * PreSharedValuesを持ったXMLを作成し保存する
	 * @param sID 送る人側のID番号(SenderID)
	 * @param srandom 16進表記の文字列(サーバー乱数)
	 * @return conf/key/PreSharedValuesUser"+sID+".xmlファイルオブジェクト
	 * @author hiroki
	 */
	public File createUserPreSharedKeyXML(int sID,String srandom){
		//XML文書の初期化
		reset();
		//UserIDの文字列配列を確保
		StringBuffer[] userID = getUserBuffer();
		//ValidityDateの設定
		PreSharedKeyServer psk = new PreSharedKeyServer();
		this.setValidityDate(psk.getValidityDate());
		//SenderIDがsID，RecieverIDがiであるPreShareValueをXMLに追加
	 	for(int i=0;i<MAX_MEMBER;i++){
	 		this.setPreSharedValues(userID[sID].toString(),userID[i].toString(),srandom);
	 	}		
	 	//保存ファイル名の設定
	 	String filename = "src/sglserver/conf/key/PreSharedKey"+userID[sID]+".xml"; 	
	 	File file = new File(filename); 	
		//XMLの保存
		try {
			this.saveFile(filename);
			System.out.println( filename+"を作成できました");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("保存の際エラーが起きたようです");
		}		
		return (file);
	}
	
	/**
	 * サーバー用のPreSharedValuesを持ったXMLを作成し保存する
	 * @param srandom 16進表記の文字列(サーバー乱数)
	 * @return 保存されるファイル：
	 * conf/key/PreSharedValuesServerXML.xml
	 * @author hiroki
	 */
	public File createServerPreSharedKeyXML(String srandom){
		//XML文書の初期化
		reset();
		//UserIDの文字列配列を確保
		StringBuffer[] userID = getUserBuffer();
		//XML文書の作成
		//ValidityDateの設定
		this.setValidityDate();
		//SendFinishの設定
		this.setSend();
		//RootRandomValueの設定
		this.serRootRandomValue( srandom );
		//userValueを設定
	 	for(int i=0;i<MAX_MEMBER;i++){
	 		this.setPreShareValues(userID[i].toString(),srandom);
	 	}		
	 	//保存ファイル名の設定
	 	String filename = "src/sglserver/conf/key/PreSharedKeyServer.xml";
	 	File file = new File(filename);	 	
		//XMLの保存
		try {
			this.saveFile(filename);
			System.out.println(filename+"を作成できました");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("保存の際エラーが起きたようです");
		}
		return (file);
	}

	/**
	 * "0000"～"9999"の内、先頭からMAX_MEMBER分の文字列を作成
	 * @return "0000"～"9999"の内、先頭からMAX_MEMBER分の文字列を返す
	 * @author hiroki
	 */
	private StringBuffer[] getUserBuffer(){
		//ユーザーの例
		StringBuffer[] userID = new StringBuffer[MAX_MEMBER];
		int iD=0;
		//ユーザーIDを設定
		//TODO:このままだと1万人までのユーザーしか扱えない("0000"～"9999")
		for(int i=0; i<10 ;i++){
			for(int j=0; j<10 ;j++){
				for(int k=0; k<10 ;k++){
					for(int l=0; l<10 ;l++){
						//IDの設定
						iD = i*1000+j*100+k*10+l;
						if(iD>=MAX_MEMBER){
							break;
						}
						userID[iD] = new StringBuffer("");
						userID[iD].append(i);
						userID[iD].append(j);
						userID[iD].append(k);
						userID[iD].append(l);
					}
				}
			}
		}
		return (userID);
	}

	/**
	 * 新規作成用のXMLドキュメントとして初期化をします
	 * @author hiroki
	 */
	private void reset(){
		//変数の初期化
		try{
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
			// 新しいDocumentオブジェクトを生成
			this.document = builder.newDocument();
			//rootとして<Share_Key_Information>エレメントを作成
			this.root =this.document.createElement("PreShareValues");
			//rootとしてエレメントをdocumentに設定
			this.document.appendChild(this.root);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ValidityDateのXML文書を追加します
	 * @return 追加されたDocumentクラスを返す
	 * @author hiroki
	 */
	private Document setValidityDate(){
	    // 有効期限の設定
        Calendar ca = Calendar.getInstance();
        DateFormat dateformat = DateFormat.getDateInstance();
        ca.add(Calendar.DATE,30);// 30日先に設定
        Date validity = ca.getTime();
        String day = dateformat.format(validity);

        // ValidityDateを追加
		try{
			// 新規要素、ValidityDateタグの作成
			Element server = document.createElement("ValidityDate");
			// ValidityDate値を設定しておく
			server.appendChild( document.createTextNode( day ) );
			//XMLのルート要素に追加
			root.appendChild( server );
		}catch(Exception e){
			e.printStackTrace();
		}
		
	    return (document);
	}

	/**
	 * ValidityDateのXML文書を追加します
	 * @param ValidityDay
	 * @return 追加されたDocumentクラスを返す
	 * @author hiroki
	 */
	private Document setValidityDate(String validity){
	    
        // ValidityDateを追加
		try{
			// 新規要素、ValidityDateタグの作成
			Element server = document.createElement("ValidityDate");
			// ValidityDate値を設定しておく
			server.appendChild( document.createTextNode( validity ) );
			//XMLのルート要素に追加
			root.appendChild( server );
		}catch(Exception e){
			e.printStackTrace();
		}
	    return (document);
	}

	/**
	 * SendingFinishedタグを追加
	 * @return
	 * @author kinbara
	 */
	private Document setSend(){
		try{
			String Send = "no";
			Element server = document.createElement("SendingFinished");
			server.appendChild(document.createTextNode(Send));
			root.appendChild(server);
		}catch(Exception e){
			e.printStackTrace();
		}
		return (document);
	}
	
	/**
	 * UserVectorのXML文書を追加します
	 * @param sID 送る側のID文字列
	 * @param rID 受ける側のID文字列
	 * @param serverRandom 16進表記の文字列
	 * @return 追加されたDocumentクラスを返す
	 * @author hiroki
	 */
	private Document setPreSharedValues(String sID,String rID,String serverRandom){
		try{
			// ルート要素からShareKeyのノードリストを取得
			NodeList uveclist = this.root.getElementsByTagName("userVector");
			Element uVector = (Element)uveclist.item(0);
			if(uVector==null){
				// userVectorタグ作成
				uVector = this.document.createElement("userVector");
				// 属性ノードの作成
				Attr sIDtag = this.document.createAttribute("xmlns:SenderID");
				// 属性ノードの値
				sIDtag.setValue( sID );
				// 属性ノードを要素に追加
				uVector.setAttributeNode( sIDtag );
			}
			// 新規要素、PreSharedValueタグの作成
			Element user = this.document.createElement("PreSharedValue");
			// 属性ノードの作成
			Attr rIDtag = this.document.createAttribute("xmlns:RecieverID");
			// 属性ノードの値
			rIDtag.setValue( rID );
			// 属性ノードを要素に追加
			user.setAttributeNode( rIDtag );
			// PreSharedValueの計算
			user.appendChild(this.document.createTextNode(this.getHash(sID,rID,serverRandom)));
			// 親要素にPreSharedValueを追加
			uVector.appendChild( user );
			//XMLのルート要素に追加
			this.root.appendChild(uVector);
		}catch(Exception e){
			e.printStackTrace();
		}	
		return(this.document);
	}
	
	/**
	 * userValueのXML文書を追加します
	 * @param sID ID文字列
	 * @param serverRandom 16進表記の文字列
	 * @return 追加されたDocumentクラスを返す
	 * @author hiroki
	 */
	private Document setPreShareValues(String sID,String serverRandom){
		try{
			// 新規要素、userValueタグの作成
			Element user = this.document.createElement("userValue");
			// 属性ノードの作成
			Attr rIDtag = this.document.createAttribute("xmlns:ID");
			Attr send = this.document.createAttribute("xmlns:SendingFinished");
			// 属性ノードの値
			rIDtag.setValue(sID);
			send.setValue("no");
			// 属性ノードを要素に追加
			user.setAttributeNode( rIDtag );
			user.setAttributeNode(send);		
			// userValueの計算
			user.appendChild(this.document.createTextNode(this.getHash(sID,sID,serverRandom)));
			//XMLのルート要素に追加
			this.root.appendChild( user );
		}catch(Exception e){
			e.printStackTrace();
		}	
		return(this.document);
	}
	
	/**
	 * RootRandomValueのXML文書を追加します
	 * @param serverRandom 16進表記の文字列
	 * @return 追加されたDocumentクラスを返す
	 * @author hiroki
	 */
	private Document serRootRandomValue(String serverRandom){
		try{
			// 新規要素、RootRandomValueタグの作成
			Element server = this.document.createElement("RootRandomValue");
			// RootRandom値を設定しておく
			server.appendChild( this.document.createTextNode(serverRandom) );
			//XMLのルート要素に追加
			this.root.appendChild( server );
		}catch(Exception e){
			e.printStackTrace();
		}	
		return(this.document);
	}

	/**
	 * サーバーランダム値を作成
	 * @return
	 * @author kinbara
	 */
	public String GenerateServerRandomValue(){
		Random rand = new Random();
		BigInteger big = new BigInteger(960,rand);
		String hex = big.toString(16);
		return hex;
	}
	
	/**
	 * iD1とiD2の人のPreSharedValueを計算します
	 * @param iD1 文字列
	 * @param iD2 文字列
	 * @param srandom 16進表記の文字列(サーバーの乱数)
	 * @return SHA-256を使用して、
	 * iD1がiD2より小さいならば(iD1||iD2||srandom)をハッシュした値を、
	 * そうでないならば
	 * (iD2||iD1||srandom)をハッシュした値を返します
	 * @author hiroki
	 */
	private String getHash(String iD1,String iD2,String srandom){
 		//ハッシュ値を格納するバッファ
 		StringBuffer sb = new StringBuffer("");
		try{
 	 		//メッセージダイジェストのインスタンス作成
 	 		MessageDigest md = MessageDigest.getInstance("SHA-256");
 	 		StringBuffer sbtext = new StringBuffer();
 	 		//iD1<iD2のとき(iD1||iD2)
 	 		if(iD2.compareTo(iD1)>0){
 	 			sbtext.append(iD1);
 	 			sbtext.append(iD2);
 	 		}
 	 		//iD2<iD1のとき(iD2||iD1)
 	 		else{
 	 			sbtext.append(iD2);
 	 			sbtext.append(iD1);
 	 		}
 	 		byte[] sbytes = sbtext.toString().getBytes();
 	 		md.update(sbytes); 		
 	 		/*
 	 		 * データをbyte型に変換
 	 		 * 
 	 		 * 注意：	srandomの先頭が"00"だったら
 	 		 * 			ここで1byte減ると思われる。
 	 		 */
 	 		BigInteger bi = new BigInteger(srandom,16);
 	 		byte[] cleartext = bi.toByteArray();
 	 		//ダイジェストに追加
 	 		md.update( cleartext );
 	 		//メッセージダイジェストの計算
 	 		byte[] hash = md.digest();
 	 		//byteを16進数の文字列に変換
 	 		for (int i = 0; i < hash.length; i++) {
 	 			int val = hash[i] & 0xFF;
 	 			if (val < 16) {
 	 				sb.append("0");
 	 			}
 	 			sb.append( Integer.toHexString(val) );
 	 		}
   		}catch(Exception e){
  			e.printStackTrace();
  		}
		//ハッシュ値を返す
		return(sb.toString());
	}
	
	/**
	 * このクラスのドキュメントをXMLとして保存します
	 * @param filename 保存するファイル名
	 * @throws Exception
	 * @author hiroki
	 */
	private void saveFile(String filename) throws Exception {
		//xml保存 変換
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		//xml保存
		DOMSource source = new DOMSource(this.document);
		File newXML = new File( filename ); 
		FileOutputStream os = new FileOutputStream(newXML); 
		StreamResult result = new StreamResult(os); 
		transformer.transform(source, result);
	}
}
