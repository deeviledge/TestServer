package sglserver.pskey;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * サーバーのPreSharedKeyを扱うクラス
 * @author Hiroki,kinbara
 * @version 1.2
 * @作成日: 2005/01/12
 * @最終更新日:2008/10/31
 */
public class PreSharedKeyServer {
	private Document document;	//XMLドキュメント
	private Element root;		//XMLのルートノード
	private int MAX_MEMBER;	//ID-NIKSの表の行数(または、列数)
	private String filename = "src/sglserver/conf/key/PreSharedKeyServer.xml";

	/**
	 * ID-NIKSの表を30×30とする 
	 */
	public PreSharedKeyServer(){
		this.FileInput(filename);
	}
	
	/**
	 * XMLファイルの読み込む
	 * @param uri ファイルの場所
	 * @author hiroki
	 */
	private void FileInput(String uri){
		//XML文書の初期化
		try{
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得
			this.document = builder.parse( uri );
			// ルート要素を取得
			this.root = document.getDocumentElement();
			// MAX_MEMBERのセット
			NodeList uv = this.root.getElementsByTagName("userValue");
			this.MAX_MEMBER = uv.getLength();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * XMLファイルの読み込む
	 * @param file 読み出すファイルの場所
	 * @author hiroki
	 */
	void FileInput(File file){
		//XML文書の初期化
		try{
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得
			this.document = builder.parse( file );
			// ルート要素を取得
			this.root = document.getDocumentElement();
			// MAX_MEMBERのセット
			NodeList uv = this.root.getElementsByTagName("userValue");
			this.MAX_MEMBER = uv.getLength();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ValidityDateを読み出す
	 * @return ValidityDate
	 * @author hiroki
	 */
	public String getValidityDate(){
		NodeList validity = root.getElementsByTagName("ValidityDate");
	    Element validityElement = (Element)validity.item(0);
	    String validitydate = validityElement.getFirstChild().getNodeValue();

	    return(validitydate);
	}

	/**
	 * RootRandomValueを読み出す
	 * @return RootRandomValue
	 * @author hiroki
	 */
	public String getRootRandomValue(){

		NodeList rr = root.getElementsByTagName("RootRandomValue");
	    Element rrElement = (Element)rr.item(0);
	    String rootRandom = rrElement.getFirstChild().getNodeValue();

	    return(rootRandom);
	}

	/**
	 * PreSharedKeyを読み出す
	 * @param userID 通信相手のUserID
	 * @return UserIDが存在する場合：PreSharedKey、存在しない場合：null
	 * @author hiroki
	 */
	public String getPreSharedKey(String userID){
	    String preSharedKey = "";
		NodeList uv = this.root.getElementsByTagName("userValue");
		for(int i=0; i < uv.getLength();i++){
		    Element uvElement = (Element)uv.item(i);
			String uvID = uvElement.getAttribute("xmlns:ID");
			// XML文書にUserIDと同じ要素が存在したときループを抜ける
			if(uvID.equalsIgnoreCase(userID)){
			    preSharedKey = uvElement.getFirstChild().getNodeValue();
			    return (preSharedKey);
			}
		}

		return (null);
	}
	
	/**
	 * ユーザ全員にPreSharedKeyを送信しているのならtrueを返す
	 * @return boolean
	 * @author kinbara
	 */
	public boolean isSendFinished(){
		NodeList sf = root.getElementsByTagName("SendingFinished");
	    Element sfElement = (Element)sf.item(0);
	    String finished = sfElement.getFirstChild().getNodeValue();
	    if(finished.equalsIgnoreCase("yes")){
	    	return true;
	    }else{
	    	return false;
	    }
	}
	
	/**
	 * ユーザにPreSharedKeyが送信されていたらtrueを返す
	 * @param userID
	 * @return boolean
	 * @author kinbara
	 */
	public boolean isUserSendFinished(String userID){
		try{
			NodeList sendlist = this.root.getElementsByTagName("userValue");
			for(int i=0;i<sendlist.getLength();i++){
				Element send = (Element)sendlist.item(i);
				String uvID = send.getAttribute("xmlns:ID");
				if(uvID.equalsIgnoreCase(userID)){
					String uvsend = send.getAttribute("xmlns:SendingFinished");
					if(uvsend.equalsIgnoreCase("yes")){
						return true;
					}
				}
			}
			return false;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * ユーザ全員がPreSharedKeyを更新済みなのかを返す関数
	 * @return boolean
	 * @author kinbara
	 */
	public boolean CheckSendFinished(){
		// 更新したPreSharedkeyを送信したユーザの人数が限界に達したら(全員に送信済みなら)
		if(this.getSend_Members() == this.MAX_MEMBER){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * ユーザにPreSharedKeyファイルを配り終えたときにフラグを立てる関数(そのユーザに配ったのであればフラグは立て直さない)
	 * @param String userID ユーザID
	 * @return Document
	 * @author kinbara
	 */
	public Document setUserSendFinish(String userID){
		try{
			String value = "yes";
			NodeList sendlist = this.root.getElementsByTagName("userValue");
			for(int i=0;i<sendlist.getLength();i++){
				Element send = (Element)sendlist.item(i);
				String uvID = send.getAttribute("xmlns:ID");
				if(uvID.equalsIgnoreCase(userID)){
					String uvsend = send.getAttribute("xmlns:SendingFinished");
					if(uvsend.equalsIgnoreCase("no")){
						NamedNodeMap attributes = send.getAttributes();
						Node id = attributes.getNamedItem("xmlns:SendingFinished");
						id.setNodeValue(value);
						this.saveFile(filename);
						break;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return (document);
	}
	
	/**
	 * ユーザ全員にPreSharedKeyファイルを配り終えたときにフラグを立てる関数
	 * @return　document
	 * @author kinbara
	 */
	public Document setSendFinish(){
		try{
			String value = "yes";
			NodeList sendlist = this.root.getElementsByTagName("SendingFinished");
			Element send = (Element)sendlist.item(0);
			send.getChildNodes().item(0).setNodeValue(value);
			this.saveFile(filename);
		}catch(Exception e){
			e.printStackTrace();
		}
		return (document);
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
	
	/**
	 * ID-NIKSの表のメンバー数を返す
	 * @return ID-NIKSの表のメンバー数
	 * @author hiroki
	 */
	public int getMAX_MEMBER(){
		return (this.MAX_MEMBER);
	}

	/**
	 * PreSharedKeyが更新済みのユーザの人数を返す関数
	 * @return int 
	 * @author kinbara
	 */
	private int getSend_Members(){
		String value = "yes";
		int send_members = 0;
		NodeList sendlist = this.root.getElementsByTagName("userValue");
		for(int i=0;i<sendlist.getLength();i++){
			Element send = (Element)sendlist.item(i);
			String uvsend = send.getAttribute("xmlns:SendingFinished");
			if(uvsend.equalsIgnoreCase(value)){
				++send_members;
			}
		}
		return send_members;
	}
	
	/**
	 * ValidityDateが期限切れなのかを返す関数
	 * @return boolean
	 * @throws ParseException
	 * @author kinbara
	 */
	public boolean isChangeValidityValue() throws ParseException{
		Date date1 = new Date(); // now
		//System.out.println(date1);
		//String date = this.getValidityDate();
		Calendar ca = Calendar.getInstance();
        DateFormat dateformat = DateFormat.getDateInstance();
        //ca.add(Calendar.DATE,30);// 30日先に設定
        //Date validity = ca.getTime();
        //Date date1 = new Date(); // now
        Date date2 = dateformat.parse(this.getValidityDate()); // 有効期限
        //System.out.println(date2);
        /*
        ca.setTime(date2);
        ca.add(Calendar.DATE,30);
        Date validity = ca.getTime();
        */
        //System.out.println(validity);
        //String day = dateformat.format(validity);
        if(date2.before(date1) == true){ // 有効期限を過ぎた場合
			return true;
		}else{
			return false;
		}
        /*
		if(validity.before(date1) == true){
			return true;
		}else{
			return false;
		}
		*/
	}
}
