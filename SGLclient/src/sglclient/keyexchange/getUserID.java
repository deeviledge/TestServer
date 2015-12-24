package	sglclient.keyexchange;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


class getUserID{

	private Document	document;		// グループ情報のドキュメント
	private Element		root;			//     〃       のルートノード
	
	
	getUserID(String filename){
		try{
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = factory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得(ファイル読み込み)
			document = builder.parse(new BufferedInputStream(new FileInputStream(filename)));
			// ルート要素を取得
			root = document.getDocumentElement();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	String getID(String username){
		
		String id=null;
		
		try{
			NodeList gInfolist = root.getElementsByTagName("Group");
			Element gInfo = (Element)gInfolist.item(0);
			
			NodeList unamelist = gInfo.getElementsByTagName("Peer");
			
			
			for(int m=0; m<unamelist.getLength(); m++){
				Element acInfo = (Element)unamelist.item(m);
				String acname =
					(acInfo.getElementsByTagName("Name")).item(0).getFirstChild().getNodeValue(); 
				if( acname.equals(username) ){
					id = acInfo.getAttribute("xmlns:ID");
					System.out.println("ID = "+id);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return (id);
	}
}
