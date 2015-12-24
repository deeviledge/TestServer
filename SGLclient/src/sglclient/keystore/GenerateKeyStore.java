package sglclient.keystore;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;

import sglclient.myinformation.MyInformation;

/**
 * KeyStoreファイルを生成、鍵のインポートなどをする（クライアント）
 * KeyStoreファイルにはRSA公開鍵，RSA秘密鍵がそれぞれ格納されており
 * SGLサーバ―SGLクライアント間での通信を安全にするためのもの
 * @author Kinbara,masato
 * @version 1.0
 * @作成日: 2007/10/28
 * @最終更新日:2008/10/31
 */
public class GenerateKeyStore {

	public static MyInformation peer;
	// 外部プロセスを実行する為、runtimeオブジェクトを取得する
	static Runtime runtime = Runtime.getRuntime();
        static Runtime runtime2 = Runtime.getRuntime();
        Runtime runtime3 = Runtime.getRuntime();
        Runtime runtime4 = Runtime.getRuntime();
        Runtime runtime5 = Runtime.getRuntime();
    
	static File file = new File("src/sglclient/conf/usr/ca/MyKeyStore");
	static File file2 = new File("src/sglclient/conf/usr/xml_files/MyInformation.xml");
	File file3 = new File("src/sglclient/conf/key/ca/ca.cer");
	//File file4 = new File("src/sglclient/conf/usr/ca/MyStore");
	// コマンド
	
    
    static Container cont;
	
    /**
     *  コンストラクタ
     *	MyInformation.xmlを読み込む
     */
	public GenerateKeyStore(){ 
		try{
			peer = new MyInformation();
		}catch(Exception e){
            e.printStackTrace();
        }       
	}
	
	/**
	 * MyKeyStoreを作成、証明書をexportする
	 * @author kinbara
	 */
	public void MakeKeyStore(String name,String adress){ 
		try{
			//System.out.println("MyKeyStoreFileを作成します。");
			boolean ext = file.exists(); // MyKeyStoreファイルが存在していればtrue
			boolean al = false;
			boolean ext2 = file2.exists(); // MyInformation.xmlが存在していればtrue
			if(ext2 == true){ // MyInformation.xmlが存在すれば
				String alias = name; // xmlからpeernameを読み込む
				String mailaddress = adress;
				//System.out.println(mailaddress);
				if(ext == true){ // MyKeyStoreファイルが存在すれば
					X509 x509 = new X509("src/sglclient/conf/usr/ca","MyKeyStore","projecttheta");
					al = x509.isAlias(alias); // aliasがすでに登録されていればtrue
				}
				if(al == false){ // aliasが登録されていない場合
					String gen_command11 = "keytool",
							   gen_command12 = "-genkeypair", 
							   gen_command13 = "-keyalg",
							   gen_command14 = "RSA",
							   gen_command15 = "-dname",
							   gen_command16 = "EmailAddress=" + mailaddress +",CN=client,C=JP",
							   gen_command17 = "-alias" ,
							   gen_command18 = alias ,
							   gen_command19 = "-keypass",
							   gen_command20 = "projecttest",
							   gen_command21 = "-keystore",
							   gen_command22 = "src/sglclient/conf/usr/ca/MyKeyStore", 
							   gen_command23 = "-storepass", 
							   gen_command24 = "projecttheta", 
							   gen_command25 = "-validity",
							   gen_command26 = "365",
							   gen_command27 = "-keysize",
							   gen_command28 = "1024";
					//クライアントのキーストアを作成
					Process process = runtime.exec(new String[]{gen_command11,gen_command12,gen_command13,gen_command14,gen_command15,gen_command16,gen_command17,gen_command18,gen_command19,
		            		gen_command20,gen_command21,gen_command22, gen_command23, gen_command24, gen_command25,gen_command26, gen_command27,gen_command28});
					process.waitFor();
					BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			        String line = null;
			        while ((line = br.readLine()) != null) {
			            System.out.println(line);
			        }
					// 作成完了
					
			        String command ="src/sglclient/conf/usr/ca/" + alias + ".cer";
			        
					//クライアント証明書をキーストアに保存
					Process process2 = runtime2.exec(new String[]{"keytool" ,"-exportcert", "-alias" , alias ,"-file",command ,
							"-keystore", "src/sglclient/conf/usr/ca/MyKeyStore", "-storepass" ,"projecttheta"});
					process2.waitFor();
					// export完了
					System.out.println(alias+"を追加しました。");
					System.out.println("X.509証明書登録終了");
					//JOptionPane.showMessageDialog(cont,"鍵を作成しました。"); // ダイアログを表示
				}else{ // aliasが登録されている場合
					System.out.println(alias + "はすでに登録されています。");
					//JOptionPane.showMessageDialog(cont,alias + "はすでに登録されています。"); // ダイアログを表示
				}
			}else{ // MyInformation.xmlが存在しない場合
				System.out.println("MyInformation.xmlが存在しないため作成できません。");
				//JOptionPane.showMessageDialog(cont,"MyInformation.xmlが存在しないため作成できません。"); // ダイアログを表示
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * SGLサーバの鍵をimportする
	 * @author kinbara
	 */
	public void ImportKeyStore(){ 
		try{
			System.out.println("SGLサーバの公開鍵をimportします。");
			boolean ext = file.exists(); // MyKeyStoreが存在すればtrue
			boolean al = false;
			boolean ext2 = file3.exists(); // ca.cerが存在すればtrue
            if(ext == true){ // キーストアが存在すれば
            	X509 x509 = new X509("src/sglclient/conf/usr/ca","MyKeyStore","projecttheta");
    			al = x509.isAlias("server"); // CAがすでに登録されていればtrue
            }
            if(al == true || ext2 == false){ // CAが登録されているか、証明書が存在しない場合
            	System.out.println("SGLサーバの鍵がすでに登録されているか、証明書が存在しないためImportできませんでした。");
            	//JOptionPane.showMessageDialog(cont,"SGLサーバの鍵がすでに登録されているか、証明書が存在しないため追加できませんでした。"); // ダイアログを表示
            }else{
            	Process process3 = runtime3.exec(new String[]{ "keytool" ,"-importcert", "-alias" ,"server", "-file", "src/sglclient/conf/key/ca/ca.cer" ,
            			"-keystore", "src/sglclient/conf/usr/ca/MyKeyStore" ,"-storepass" ,"projecttheta"});
            	BufferedWriter cout = new BufferedWriter(new OutputStreamWriter(process3.getOutputStream()));
            	String line = "y";
            	cout.write(line);
            	cout.close();
            	process3.waitFor();
            	System.out.println("SGLサーバの鍵を登録しました。");
            	//JOptionPane.showMessageDialog(cont,"SGLサーバの鍵を追加しました。"); // ダイアログを表示
            	// import完了
            	System.out.println("X.509証明書登録終了");
            }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 鍵を削除する
	 * @author kinbara
	 */
	public void DeleteKey(){ 
		try{
			boolean ext = file.exists(); // MyKeyStoreが存在すればtrue
			boolean ext2 = file2.exists(); // MyInformation.xmlが存在すれば
			if(ext == true && ext2 == true){ // MyKeyStoreとMyInformation.xmlが存在する場合
				String alias = peer.getUsrName(); // xmlからpeernameを読み込む
				X509 x509 = new X509("src/sglclient/conf/usr/ca","MyKeyStore","projecttheta");
    			boolean al = x509.isAlias(alias); // aliasがすでに登録されていればtrue
    			boolean al2 = x509.isAlias("server"); // CAがすでに登録されていればtrue
    			if(al == true){ // aliasが登録されている場合
    				// alias(クライアント)の鍵を削除するコマンド
    				
    				Process process4 = runtime4.exec(new String[]{"keytool", "-delete", "-alias" , alias , "-keystore" ,"src/sglclient/conf/usr/ca/MyKeyStore",
    	    				"-storepass", "projecttheta"});
    				process4.waitFor();
    				// alias(クライアント)の鍵削除完了
    				System.out.println(alias + "の鍵を削除しました。");
    			}
    			if(al2 == true){ // SGLサーバの鍵がすでに登録されている場合
    				Process process5 = runtime5.exec(new String[]{"keytool", "-delete", "-alias", "server", "-keystore", "src/sglclient/conf/usr/ca/MyKeyStore",
    						"-storepass" ,"projecttheta"});
    				process5.waitFor();
    				// CAの鍵削除完了
    				System.out.println("SGLサーバの鍵を削除しました。");
    			}
    			if(al == false && al2 == false){ // 何も登録されていない場合
    				System.out.println("削除するものはありません。");
    				JOptionPane.showMessageDialog(cont,"削除するものはありません。"); // ダイアログを表示
    			}else{ // 何かを削除した場合
    				JOptionPane.showMessageDialog(cont,"鍵を削除しました。"); // ダイアログを表示
    			}
			}else{ // MyInformation.xmlが存在しないか、キーストアが存在しないとき
				System.out.println("MyInformation.xmlが存在しないか、キーストアが存在しないため削除できませんでした。");
				JOptionPane.showMessageDialog(cont,"MyInformation.xmlが存在しないか、キーストアが存在しないため削除できませんでした。"); // ダイアログを表示
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
