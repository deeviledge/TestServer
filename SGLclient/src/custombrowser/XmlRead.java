/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


class XmlRead {
	String[] groups; //所属しているグループを保存する変数
	String group;    //選択したグループを保存する変数
	int num;		 //グループ数を保存する変数
	String hash_key; //ハッシュ関数で処理した鍵を保存する変数
	String key;      //グループ鍵を保存する変数
	byte[] bytekey;  //byte型に変換したグループ鍵を保存する変数
	
	Document doc;    
	Element root;   
	NodeList nodelist;
	
	public XmlRead(){
		//System.out.println("XmlReadコンストラクタ");
	}
	
	public XmlRead(CustomBrowserTabController customBrowserTabController) {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	//xmlファイルを読み込む関数
	public void Read(){
	    try {
                System.out.println("ここは？");
	    	/**xmlファイルを読み込む準備**/
	    	DocumentBuilderFactory factory 
                 = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            //相対パスでxmlファイルを読み込む
            File f = new File( "src/sglclient/conf/usr/xml_files/MyInformation.xml" );
            if(!f.exists()){
            	return;
            }
            System.out.println("xmlファイルを読み込みました");
            doc = builder.parse( f );
            root = doc.getDocumentElement();
            nodelist = root.getElementsByTagName("Group");
            num = nodelist.getLength();
            groups = new String[num];
            //System.out.println("グループ数："+num);
            for( int i=0; i<num; i++ ) {
            	Node node = nodelist.item(i);
            	NamedNodeMap attrs = node.getAttributes(); 
            	Node attr = attrs.getNamedItem("xmlns:Name");                                      
            	groups[i] = attr.getNodeValue();  //所属しているグループを配列に保存
            }            
	    }
	    catch( ParserConfigurationException e ) {
       	}
	    catch( SAXException e ) {
       	}
	    catch( IOException e ) {
       	}
	}
	
	//グループ鍵を取得する関数
	public byte[] GetKey(){
		//System.out.println("num:"+num);
		//System.out.println("グループ："+group);
		for(int i=0; i<num; i++){
			//System.out.println("groupi="+groups[i]);
			if(groups[i].matches(group)){
				//System.out.println("ifぶんにははいってます");
				Node node = nodelist.item(i);
				NamedNodeMap attrs = node.getAttributes();
				Node attr = attrs.getNamedItem("xmlns:key");
				key = attr.getNodeValue();
                                
				System.out.println("取得した鍵："+key);
			}
		}
		hash_key = sha256(key);  //グループ鍵をハッシュ関数で処理する
		//System.out.println("変換した鍵："+sha256(key));
		
		/***グループ鍵をbyte型に変換***/
		bytekey = new byte[hash_key.length()/2];
		//System.out.println(bytekey.length);
		for(int i=0; i<bytekey.length; i++){
			bytekey[i] = (byte)(Integer.parseInt(hash_key.substring(i*2,(i+1)*2),16));			
		}
		return bytekey;  //byte型に変換したグループ鍵を返す		
	}
	
	//入力された文字列のSHA-256ハッシュ値を得る
	public static String sha256(String input){
		return hash(input,"SHA-256");
	}
	
	//ハッシュ値を計算
	private static String hash(String input, String algorithm) {

		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance(algorithm);
			digest.update(input.getBytes());
		}
		catch (NoSuchAlgorithmException e) {
			return "";
		}
		return byte2HexString(digest.digest());
	}
	
	//バイトコードを16進数文字列に変換
	public static String byte2HexString(byte[] input) {

		StringBuffer buff = new StringBuffer();
		int count = input.length;
		for(int i= 0; i< count; i++){
			buff.append(Integer.toHexString( (input[i]>> 4) & 0x0F ) );
			buff.append(Integer.toHexString( input[i] & 0x0F ) );
		}
		return buff.toString();
	}

}
