package sglclient.preparation;

import java.io.File;
import java.io.IOException;

import sglclient.keystore.GenerateKeyStore;


public class LoginClient {
	
	public static String UserName;
	public static String MailAddress;
	public static String ServerIP;
	public final static int port = 12345; //とりあえずポート番号
        public boolean first_flg;
	
	//コンストラクタ
	public LoginClient() throws IOException, InterruptedException{
	
            File file = new File("src/sglclient/conf/usr/xml_files/MyInformation.xml");
		
            first_flg = !file.exists();
		
		
	}
        
        public void userLogin(String serverip,String username,String mailaddress) throws IOException, InterruptedException{
            
            setServerIP(serverip);
            setUserInfo(username,mailaddress);
            
            FileExchangeClient fec = new FileExchangeClient(ServerIP, port);
		
            if(first_flg==true){//MyInformation.xmlが存在しなければ
                fec.GetCer();	//サーバー証明書取得
                fec.GetMyInformation(UserName, MailAddress);		//MyInformationをサーバーからもらう
                
                GenerateKeyStore Store = new GenerateKeyStore(); // 証明書関係のクラスを生成
                Store.MakeKeyStore(UserName,MailAddress); // ユーザの証明書を作成してキーストアに保存

                fec.SendCer(UserName);	//ユーザーの証明書をサーバーに送る
                    
                Store.ImportKeyStore();	//サーバー証明書を保存
		
                    
            }
            fec.exit();		//ソケット切断
        }
	
	private static void setUserInfo(String username,String mailaddress){
		
		UserName    = username;
		MailAddress = mailaddress;
		
	}
	
	private static void setServerIP(String serverip){
		ServerIP = serverip;
	}
	
}
