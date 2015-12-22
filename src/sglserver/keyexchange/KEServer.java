/**
 * グループメンバに鍵交換開始を知らせて、鍵配送指令書をグループメンバに送る
 * 
 * @author nishimura
 *
 */
package sglserver.keyexchange;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sglserver.dynamicpeerinformation.DynamicPeerInformation;
import sglserver.groupadmin.GroupInformationXml;
//import sglserver.groupadmin.GroupInformationXml;

public class KEServer{
	private Socket 	socket;
	@SuppressWarnings({ "unused", "rawtypes" })
	private List	lineList;
	
	public String[] names = null;
	public boolean member_flg;	//グループメンバが全員いればtrueを格納 
	
	public KEServer(String GroupName,int KeyExchange_Port) throws SAXException, ParserConfigurationException{
		
		
		
		
		member_flg = false;
		
		PrintWriter out = null;
		
		
			
			
			// グループ情報読み込み
			GroupInformationXml ginfo = new GroupInformationXml();
			// グループメンバのユーザ名を取得
			
			names = ginfo.getMemberName(GroupName);
			
			// ログインユーザ情報読み込み
			DynamicPeerInformation dpinfo = new DynamicPeerInformation();
			
			
			//System.out.println("port="+ serverSocket.getLocalPort() +"で起動");
			
			int count = 0;	//グループメンバーのログインユーザの数をカウント
			
			for(int i=0;i<names.length;i++){
				
				// グループメンバのIPアドレス取得
				String ip = dpinfo.getIP(names[i]);
				int id = Integer.parseInt(dpinfo.getID(names[i]));
				//　ソケット通信準備
				try {
					try{
						socket = new Socket(ip, KeyExchange_Port);
					}catch(IOException  | SecurityException ex){
						socket.close();
						//out.close();
						System.out.println("User "+id+"はログインしていません");
					}
					// 鍵交換メソッドの開始を伝える
					out = new PrintWriter(socket.getOutputStream(), true);
					out.println("keyexchange");
					System.out.println(ip+"と接続しました");
					
					// グループ名を送信
					String st = "GroupName=" + GroupName;
					out.println(st);
					System.out.println("to " + socket.getRemoteSocketAddress() + "\n\t" + st);
					
					// グループメンバ数を送信
					GroupInformationXml gInfo = new GroupInformationXml();
					st = "GroupValue=" + Integer.toString( gInfo.getGroupValue(GroupName) );
					out.println(st);
					System.out.println("to " + socket.getRemoteSocketAddress() + "\n\t" + st);
					
					// SendGroupFile :clientに送るグループ情報ファイル
					String	SendGroupFile = "src/sglserver/conf/usr/xml_files/groups/"+"sekkei_" + GroupName + ".xml";
					// グループ情報ファイルを送信
					this.SendFile(SendGroupFile);
					//ユーザごとに鍵配送指令書を切り分ける
					makePersonnellMandate(GroupName,id);
					// FileName :Mandateファイル（鍵配送指令書）
					String	MandateUserFile = "src/sglserver/conf/usr/xml_files/groups/"+"Mandate_" + GroupName +"_"+String.valueOf(id) + ".xml";
                                        //String	MandateUserFile = "src/sglserver/conf/usr/xml_files/groups/"+"Mandate_" + GroupName + ".xml";
					String SendMandate = "src/sglclient/conf/usr/xml_files/groups/Mandate_" + GroupName + ".xml";
					// 鍵配送指令書を送信
					this.SendFile(SendMandate, MandateUserFile);
					socket.close();
					out.close();
				} catch (IOException e) {
					try {
						socket.close();
						out.close();
					} catch (IOException e1) {
						// TODO 自動生成された catch ブロック
						e1.printStackTrace();
					}
					
					break;
				}
				count++;
				
			}
			if(names.length==count){
				System.out.println("グループメンバーが揃いました");
				member_flg = true;
				
			}
			else{
				System.err.println("ログイン状態でないユーザが含まれます");
			}
				
			
			
		
	}
	/*
	 * ファイル送信メソッド 
	 */
	public void SendFile(String SendFile){
		
		PrintWriter out;
		
		try{
			
			// ｿｹｯﾄに書き込むストリーム
			out = new PrintWriter(socket.getOutputStream(), true);
		
			// ファイル名を送信
			String st = "FileName=" + SendFile;
			out.println(st);
			System.out.println("to " + socket.getRemoteSocketAddress() + "\n\t" + st);
			
		
			//　ファイルの読み出し準備
			int n;
			byte[] buff = new byte[1024];
			
			FileInputStream sendfile = new FileInputStream( SendFile );
			OutputStream outstr = socket.getOutputStream();
			
			//　ファイルの最後に改行を入れる
			// 前段階(作成時)で入れておくとよい
			FileOutputStream fos = new FileOutputStream(SendFile, true);
			String newline = "\n";
		    fos.write(newline.getBytes());
			fos.close();
			
			//　sendfile.read :ファイルの読み出し
			while((n = sendfile.read(buff)) > 0){
				//　ネットワーク経由で送信
				outstr.write(buff,0,n);
			}
			sendfile.close();
			//　ファイルの終わりを知らせる
			st = "[EndOfFile]";
			out.println(st);
			System.out.println("to " + socket.getRemoteSocketAddress() + "\n\t" + st);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ファイル送信メソッド
	 * 
	 * @param　SendFileName ：送信後の保存ファイル名
	 * @param　SendFile ：送信ファイル
	 * 
	 * */
	public void SendFile(String SendFileName, String SendFile){
		
		PrintWriter out;
		
		try{
			
			// ｿｹｯﾄに書き込むストリーム
			out = new PrintWriter(socket.getOutputStream(), true);
		
			// ファイル名を送信
			String st = "FileName=" + SendFileName;
			out.println(st);
			System.out.println("to " + socket.getRemoteSocketAddress() + "\n\t" + st);
			
		
			//　ファイルの読み出し準備
			int n;
			byte[] buff = new byte[1024];
			
			FileInputStream sendfile = new FileInputStream( SendFile );
			OutputStream outstr = socket.getOutputStream();
			
			//　ファイルの最後に改行を入れる
			// 前段階(作成時)で入れておくとよい
			FileOutputStream fos = new FileOutputStream(SendFile, true);
			String newline = "\n";
		    fos.write(newline.getBytes());
			fos.close();
			
			//　sendfile.read :ファイルの読み出し
			while((n = sendfile.read(buff)) > 0){
				//　ネットワーク経由で送信
				outstr.write(buff,0,n);
			}
			sendfile.close();
			//　ファイルの終わりを知らせる
			st = "[EndOfFile]";
			out.println(st);
			System.out.println("to " + socket.getRemoteSocketAddress() + "\n\t" + st);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 　ユーザごとに配送指令書を切り分ける
	 * @param groupName
	 * @param id
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public void makePersonnellMandate(String groupName, int id) throws SAXException, IOException, ParserConfigurationException{
		//　ドキュメントビルダーファクトリを生成
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// ドキュメントビルダーを生成
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document1;
		Element		root;
		FileInputStream fis = new FileInputStream("src/sglserver/conf/usr/xml_files/groups/Mandate_" + groupName + ".xml");
		document1 = builder.parse(new BufferedInputStream(fis));
		// ルート要素を取得
		root = document1.getDocumentElement();

		NodeList list = root.getElementsByTagName("Peer"); // Peerタグをまとめる
		for(int i=0; i<list.getLength(); i++){
			Element ele = (Element)list.item(i); // 元のMandate

			if(ele.getAttribute("xmlns:ID").equals(String.valueOf(id))){ // ここの引数をIDに変更する
				NodeList list2 = ele.getElementsByTagName("Round"); // ピアのIDが一致したときのRoundの集まり
				int round = list2.getLength(); // ラウンド数の決定

				// ここでxmlを再構築する
				DocumentBuilder documentBuilder = null;
				try {
					documentBuilder = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
				Document document = documentBuilder.newDocument();

				// XML文書の作成
				Element Mandate = document.createElement("Mandate"); // rootのタグを作成
				document.appendChild(Mandate);						 // rootタグを配置

				NodeList KA = root.getElementsByTagName("KeyAgreement");
				Element ka = (Element)KA.item(0);
				Element KeyAgreement = document.createElement("KeyAgreement");      // KeyAgreementタグを作成
				KeyAgreement.setAttribute("xmlns:ID", ka.getAttribute("xmlns:ID")); // 属性値の設定
				Mandate.appendChild(KeyAgreement);								    // KeyAgreementタグをrootタグの子として配置

				NodeList GR = root.getElementsByTagName("Group");
				Element gr  = (Element)GR.item(0);
				Element Group = document.createElement("Group"); // Groupタグを作成
				Group.setAttribute("xmlns:Count", gr.getAttribute("xmlns:Count")); 		 // 属性値の設定 （実際には(ry
				KeyAgreement.appendChild(Group);				 // GroupタグをKeyAgreementタグの子として配置

				Element Peer = document.createElement("Peer"); // Peer（自分自身）タグの作成
				Peer.setAttribute("xmlns:ID", String.valueOf(id));  		   // じｓｓ(ry
				Peer.setAttribute("xmlns:alt", "1"); 		   // (ry いらない
				KeyAgreement.appendChild(Peer);				   // PeerタグをKeyAgreementタグの子として配置



				for(int j=1; j <= round; j++){ // ラウンド数分ループ
					//本格的にxml作成
					// ラウンドjのitem( )　0:「Option」、　1:「Behavior」、　2:「GroupID」等
					NodeList list3 = list2.item(j-1).getChildNodes(); // ラウンドとインデックスは始まりの数値が違うので


					Element Round = document.createElement("Round");
					Round.setAttribute("round", Integer.toString(j));
					Peer.appendChild(Round);

					Element Option = document.createElement("Option");
					Option.appendChild(document.createTextNode(list3.item(0).getFirstChild().getNodeValue().toString()));
					Round.appendChild(Option);

					Element Behavior = document.createElement("Behavior");
					Behavior.appendChild(document.createTextNode(list3.item(1).getFirstChild().getNodeValue().toString()));
					Round.appendChild(Behavior);


					Element GroupID = document.createElement("GroupID");
					if(list3.item(2).hasChildNodes())
						GroupID.appendChild(document.createTextNode(list3.item(2).getFirstChild().getNodeValue().toString()));
					Round.appendChild(GroupID);

					Element GroupName = document.createElement("GroupName");
					if(list3.item(3).hasChildNodes())
						GroupName.appendChild(document.createTextNode(list3.item(3).getFirstChild().getNodeValue().toString()));
					Round.appendChild(GroupName);

					Element SendTo = document.createElement("SendTo");
					if(list3.item(4).hasChildNodes()){
						NodeList list4 = list3.item(4).getChildNodes(); 	// SendToの子の集まり(Peerしかない)
						for(int k=0; k<list4.getLength(); k++){ 			// 送信先の数だけループ
							NodeList list5 = list4.item(k).getChildNodes(); // Peerの子の集まり（ID等）

							Element SendPeer = document.createElement("Peer"); // Peer（送信先）タグの作成
							SendTo.appendChild(SendPeer);					   // PeerタグをSendToタグの子として配置

							Element ID = document.createElement("ID"); // IDタグの作成
							ID.appendChild(document.createTextNode(list5.item(0).getFirstChild().getNodeValue().toString()));
							SendPeer.appendChild(ID);				       // IDタグをPeerタグの子として配置

							Element alt = document.createElement("alt"); // いらないが、分割前と比較するとき見やすくするため
							alt.appendChild(document.createTextNode(list5.item(1).getFirstChild().getNodeValue().toString()));
							SendPeer.appendChild(alt);

							Element IP = document.createElement("IP");
							IP.appendChild(document.createTextNode(list5.item(2).getFirstChild().getNodeValue().toString()));
							SendPeer.appendChild(IP);

							Element RelayIP = document.createElement("RelayIP");
							RelayIP.appendChild(document.createTextNode(list5.item(3).getFirstChild().getNodeValue().toString()));
							SendPeer.appendChild(RelayIP);
						}
					}
					Round.appendChild(SendTo); // SendToタグをRoundタグの子として配置


					Element RecieveFrom = document.createElement("ReceiveFrom");
					if(list3.item(5).hasChildNodes()){ // 受信元が居れば
						NodeList list6 = list3.item(5).getChildNodes();
						
						Element ID = document.createElement("ID");
						ID.appendChild(document.createTextNode(list6.item(0).getFirstChild().getNodeValue().toString()));
						RecieveFrom.appendChild(ID);
						
						Element alt = document.createElement("alt");
						alt.appendChild(document.createTextNode(list6.item(1).getFirstChild().getNodeValue().toString()));
						RecieveFrom.appendChild(alt);
						
						Element IP = document.createElement("IP");
						IP.appendChild(document.createTextNode(list6.item(2).getFirstChild().getNodeValue().toString()));
						RecieveFrom.appendChild(IP);
						
						Element RelayIP = document.createElement("RelayIP");
						RelayIP.appendChild(document.createTextNode(list6.item(3).getFirstChild().getNodeValue().toString()));
						RecieveFrom.appendChild(RelayIP);
						
					}
					Round.appendChild(RecieveFrom);
					//}
				}
				// XMLファイルの作成
				File file = new File("src/sglserver/conf/usr/xml_files/groups/Mandate_" + groupName + "_" + id +".xml");
				write(file, document);
			}
		}
	}
	
	public static boolean write(File file, Document document) {

		// Transformerインスタンスの生成
		Transformer transformer = null;
		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return false;
		}

		// Transformerの設定
		transformer.setOutputProperty("indent", "yes"); //改行指定
		transformer.setOutputProperty("encoding", "UTF-8"); // エンコーディング

		// XMLファイルの作成
		try {
			transformer.transform(new DOMSource(document), new StreamResult(
					file));
		} catch (TransformerException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}




