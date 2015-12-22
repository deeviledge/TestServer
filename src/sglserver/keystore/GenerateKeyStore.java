package sglserver.keystore;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;

import sglserver.peerbasicinformation.PeerBasicInformationEdit;

/**
 * KeyStoreファイルを生成、鍵のインポートなどをする（SGLサーバ）
 * @author Kinbara
 * @version 1.0
 * @作成日: @2007/10/28
 * @最終更新日:2008/10/31
 */
public class GenerateKeyStore {

	public static PeerBasicInformationEdit peer;
	//　外部プロセスを実行する為、runtimeオブジェクトを取得する
	Runtime runtime = Runtime.getRuntime();
    Runtime runtime2 = Runtime.getRuntime();
    Runtime runtime3 = Runtime.getRuntime();
    Runtime runtime4 = Runtime.getRuntime();
    Runtime runtime5 = Runtime.getRuntime();
    Runtime runtime6 = Runtime.getRuntime();
    //	　キーボードからの読み込み用
	BufferedReader keyin = new BufferedReader(new InputStreamReader(System.in));
	//BufferedReaderクラスに渡す
	BufferedReader kin = new BufferedReader(keyin);
	File file = new File("src/sglserver/conf/key/ca/CAKeyStore");
	File file2 = new File("src/sglserver/conf/usr/xml_files/PeerBasicInformation.xml");
	Container cont;
	
//	 ディレクトリ作成コマンド
    
    //String command = "keytool -genkeypair -alias server -keyalg RSA -keysize 1024 -dname \"EmailAddress=G@jim.info.gifu-u.ac.jp, CN=server,C=JP\" -validity 365 -keystore src/sglserver/conf/key/ca/CAKeyStore -storepass projecttheta -keypass projecttest";
	//String command = "keytool -exportcert -alias server -keystore src/sglserver/conf/key/ca/CAKeyStore -file ca.cer -storepass projecttheta -rfc"; 
    /*
    String command2 ="keytool -export -v -rfc -alias server -file src/sglserver/conf/store/ca.cer " +
	"-keystore src/sglserver/conf/store/CAKeyStore -storepass projecttheta";
    
	String command5 ="keytool -delete -alias server -keystore src/sglserver/conf/store/CAKeyStore -storepass projecttheta";
	*/
	String gen_command11 = "keytool",
		   gen_command12 = "-genkeypair", 
		   gen_command13 = "-keyalg",
		   gen_command14 = "RSA",
		   gen_command15 = "-dname",
		   gen_command16 = "EmailAddress=hiroki@jim.info.gifu-u.ac.jp, CN=Project Theta,C=JP",
		   gen_command17 = "-alias" ,
		   gen_command18 = "server" ,
		   gen_command19 = "-keypass",
		   gen_command20 = "projecttest",
		   gen_command21 = "-keystore",
		   gen_command22 = "src/sglserver/conf/key/ca/CAKeyStore", 
		   gen_command23 = "-storepass", 
		   gen_command24 = "projecttheta", 
		   gen_command25 = "-validity",
		   gen_command26 = "365",
		   gen_command27 = "-keysize",
		   gen_command28 = "1024";
	 
	
	
    
    
    
	String command5 ="keytool -delete -alias server -keystore src/sglserver/conf/key/ca/CAKeyStore -storepass projecttheta";
	
	
	
	/**
	 * コンストラクタ
	 */
	public GenerateKeyStore(){ 
		try{
			peer = new PeerBasicInformationEdit();
		}catch(Exception e){
            e.printStackTrace();
        }       
	}
	
	public void ReloadFile(){
		peer = null;
		try{
			peer = new PeerBasicInformationEdit();
		}catch(Exception e){
            e.printStackTrace();
        }    
	}
	
	/**
	 * CAKeyStoreを生成する
	 * @author kinbara
	 */
	public void MakeKeyStore(){
		try{
			boolean ext = file.exists(); // CAKeyStoreが存在すればtrue
			boolean al = false;
            if(ext == true){ // キーストアが存在すれば
            	X509 x509 = new X509("src/sglserver/conf/key/ca","CAKeyStore","projecttheta");
    			al = x509.isAlias("server"); // CAがすでに登録されていればtrue
            }
            if(al == true){// CAが登録されていれば
            	System.out.println("SGLサーバはすでに登録されています。");
            	//JOptionPane.showMessageDialog(cont,"SGLサーバがすでに登録されているため鍵は作成されませんでした。"); // ダイアログを表示
          }//else{ // CAが登録されていない場合
            System.out.println("CAKeyStoreFileを作成します。");
            Runtime r = Runtime.getRuntime();
            //String[] command = {gen_command11,gen_command12,gen_command13,gen_command14,gen_command15,gen_command16,gen_command17,gen_command18,gen_command19,gen_command20,gen_command21,gen_command22, gen_command23, gen_command24, gen_command25,gen_command26};
            Process p = r.exec(new String[]{gen_command11,gen_command12,gen_command13,gen_command14,gen_command15,gen_command16,gen_command17,gen_command18,gen_command19,
            		gen_command20,gen_command21,gen_command22, gen_command23, gen_command24, gen_command25,gen_command26, gen_command27,gen_command28});
            p.waitFor();

            
            	
            	Process process2 = runtime2.exec(new String[]{"keytool", "-exportcert", "-alias", "server", "-keystore", "src/sglserver/conf/key/ca/CAKeyStore", "-file", "src/sglserver/conf/key/ca/ca.cer" ,"-storepass" ,"projecttheta" ,"-rfc"});
            	process2.waitFor();
            	System.out.println("SGLサーバの公開鍵を作成しました。");
            	//JOptionPane.showMessageDialog(cont,"SGLサーバの鍵を作成しました。"); // ダイアログを表示
           // }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * クライアントの鍵をimportする
	 * @author kinbara
	 */
	public void ImportKeyStore(String alias){ 
		try{
			//int i = 1;
			String list = ""; // importしたaliasを書き込む
			String res = ""; // 更新される前のlist
			String resal = ""; // 最後にimportしたalias名
			System.out.println("クライアントの公開鍵をimportします。");
			boolean ext = file.exists(); // CAKeyStoreが存在すればtrue
			boolean al = false;
			boolean im = false; // importした場合true
			boolean ext2 = file2.exists(); // PeerBasicInformation.xmlが存在すればtrue
			if(ext2 == true){ // PeerBasicInformation.xmlが存在する場合
				//while(i<peer.getGroupLength()){ // PeerBasicInformation.xmlに登録されているpeerの数だけ探索する
					//String alias = peer.getPeerName(i); // XMLからaliasを読み込む
					File file3 = new File("src/sglserver/conf/usr/ca/" + alias + ".cer");
					boolean ext3 = file3.exists(); // aliasの証明書が存在する場合true
					if(ext3 == false){ // aliasの証明書が存在しない場合
						System.out.println(alias + "の証明書は存在しません。");
						//++i;
						//continue; // importせずに次の探索へ
					}
					if(ext == true){ // CAKeyStoreが存在すれば
						X509 x509 = new X509("src/sglserver/conf/key/ca","CAKeyStore","projecttheta");
						al = x509.isAlias(alias); // aliasがすでに登録されていればtrue
					}
					if(al == true){ // aliasが登録されていれば
						System.out.println(alias + "はすでに登録されています。");
						//++i;
						//continue; // importせずに次の探索へ
					}
					// importコマンド
					
					/*Process process3 = runtime3.exec(new String[]{ "keytool" ,"-importcert", "-alias" ,"server", "-file", "src/sglclient/conf/key/ca/ca.cer" ,
	            			"-keystore", "src/sglclient/conf/usr/ca/MyKeyStore" ,"-storepass" ,"projecttheta"});
	            	BufferedWriter cout = new BufferedWriter(new OutputStreamWriter(process3.getOutputStream()));
	            	String line = "y";
	            	cout.write(line);
	            	cout.close();
	            	process3.waitFor();*/
					
					String command = "src/sglserver/conf/usr/ca/"+alias+".cer";
					
					Process process3 = runtime3.exec(new String[]{ "keytool" ,"-importcert", "-alias" ,alias, "-file", command ,
	            			"-keystore", "src/sglserver/conf/key/ca/CAKeyStore" ,"-storepass" ,"projecttheta"});
					BufferedWriter sout = new BufferedWriter(new OutputStreamWriter(process3.getOutputStream()));
					String line = "y";
					sout.write(line);
					sout.close();
					process3.waitFor();
					// import完了
					System.out.println(alias+"の証明書を追加しました。");
					im = true;
					resal = alias;
					res = list;
					list += alias + ",";
					//++i;
				//}
				if(im == true){
					System.out.println(res + resal + "の鍵を追加しました。");
					//JOptionPane.showMessageDialog(cont,res + resal + "の鍵を追加しました。"); // ダイアログを表示
				}else{
					System.out.println("追加できる鍵がありませんでした。");
					//JOptionPane.showMessageDialog(cont,"追加できる鍵がありませんでした。"); // ダイアログを表示
				}
			}else{
				System.out.println("PeerBasicInformation.xmlが存在しないためImportできませんでした。");
				JOptionPane.showMessageDialog(cont,"PeerBasicInformation.xmlが存在しないためImportできませんでした。"); // ダイアログを表示
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
			int i = 0;
			boolean ext = file.exists(); // CAKeyStoreが存在すればtrue
			boolean al = false;
			boolean al3 = false; // 削除した場合true
			boolean ext2 = file2.exists(); // PeerBasicInformation.xmlが存在すればtrue
			if(ext == true && ext2 == true){ // CAKeyStoreとPeerBasicInformation.xmlが存在する場合
				X509 x509 = new X509("src/sglserver/conf/key/ca","CAKeyStore","projecttheta");
				while(i<peer.getGroupLength()){ // PeerBasicInformation.xmlに登録されているpeerの数だけ探索する
					String alias = peer.getPeerName(i); // XMLからaliasを読み込む
					al = x509.isAlias(alias); // aliasがすでに登録されていればtrue
					if(al != true){ // aliasが登録されていなければ
						++i;
						continue; // deleteせずに次の探索へ
					}
					System.out.print(alias);
					// deleteコマンド
					
				
					Process process4 = runtime4.exec(new String[]{"keytool" ,"-delete", "-alias" , alias , "-keystore" ,"src/sglserver/conf/key/ca/CAKeyStore " ,
							"-storepass" ,"projecttheta"});
					process4.waitFor();
					System.out.println("の鍵を削除しました。");
					al3 = true;
					// delete完了
					++i;
				}
				boolean al2 = x509.isAlias("server"); // CAがすでに登録されていればtrue
				if(al2 == true){ // CAがすでに登録されている場合
					Process process5 = runtime5.exec(command5);
					process5.waitFor();
					System.out.println("SGLサーバの鍵を削除しました。");
				}
				if(al3 == true || al2 == true){
					JOptionPane.showMessageDialog(cont,"鍵を削除しました。"); // ダイアログを表示
				}else{
					JOptionPane.showMessageDialog(cont,"削除するものがありませんでした。"); // ダイアログを表示
				}
			}else{
				System.out.println("キーストアが存在しないか、PeerBasicInformation.xmlが存在しないため削除できませんでした。");
				JOptionPane.showMessageDialog(cont,"キーストアが存在しないか、PeerBasicInformation.xmlが存在しないため削除できませんでした。"); // ダイアログを表示
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
