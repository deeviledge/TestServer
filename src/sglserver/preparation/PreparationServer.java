package sglserver.preparation;

import java.io.File;

import sglserver.keystore.GenerateKeyStore;
import sglserver.peerbasicinformation.*;




public class PreparationServer {
	

	public PreparationServer(){
		
		File file1 = new File("src/sglserver/conf/usr/xml_files/PeerBasicInformation.xml");
		File file2 = new File("src/sglserver/conf/key/ca/CAKeyStore");
		//とりあえず直で突っ込む
		String serverMail = "hiroki@jim.info.gifu-u.ac.jp";
		
		//PeerBasicInformationを作成
		if(file1.exists() == false){
			PeerBasicInformationXml pbi = new PeerBasicInformationXml();
			pbi.peerBasicInformationXmlGenarator("0000", "server", serverMail, "src/sglserver/conf/key/ca/ca.cer");
			try{
				pbi.saveFile(); // 保存
				System.out.println("PeerBasicInformation.xmlを作成しました");
			}catch(Exception e1){
				System.err.println(e1);
			}
		}
		
		//サーバのキーストアと公開鍵証明書を作成
		if(file2.exists() == false){
			GenerateKeyStore Store;
			Store = new GenerateKeyStore(); // キーストアを作成
			Store.MakeKeyStore();			//サーバー証明書を作成してキーストアに保存
		}
	}
	
}