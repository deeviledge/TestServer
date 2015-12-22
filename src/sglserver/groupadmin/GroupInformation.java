/*
 * 作成日: 2006/11/17
 *
 */

package	sglserver.groupadmin;

import java.io.*;

import java.util.*;



public class GroupInformation{
	
	public GroupInformation(){
		;
	}
	
	public String GroupInformationEdit(){

		int	addcnt = 50;			//　userlistの追加上限数
		
		String		gid;			//　グループID		（数値）
		String		option=null;	//　オプションの文字列
		ArrayList 	acountlist;		//　アカウントリスト		（数値）
		ArrayList 	userlist;		//　ユーザリスト		（数値）
		String 		uid;			//　ユーザID		（数値）
		
		String		filename="src/sglserver/conf/usr/xml_files/GroupInformation.xml";
		String		GroupName=null;

		boolean		optCheck=false;
		//boolean	newfile = true;		// true :GroupInfoTest.xmlを新しく作り直す場合
		
		try{
			//　キーボードからの読み込み用
			BufferedReader keyin = new BufferedReader(new InputStreamReader(System.in));
			//BufferedReaderクラスに渡す
			BufferedReader in = new BufferedReader(keyin);
			
			// info :各機能を選択
			System.out.println("1:グループの追加　　　2：メンバーの追加  　 　3:グループの削除　　　4:メンバーの削除　　　5:オプションの変更");
			int	info = Integer.parseInt(in.readLine());

			GroupInformationXml ginfo = new GroupInformationXml(filename);
			
			switch(info){
				// グループの追加
				case 1:
					//　追加したいグループの名前を入力
					System.out.print("GroupName=");
					String gname = in.readLine();
					
					//　オプションの設定
					// optionが正しく入力されるまで繰り返す
					while(!optCheck){
						System.out.println("Option (Normal or AntiDishonestInsider)");
						System.out.print("'N'or'A' =");
						option = in.readLine();
						// 'n'または'N'が入力されたときは"Normal"
						if(option.toUpperCase().equals("N")){
							option = "Normal";
							optCheck = true;
						}
						// 'a'または'A'が入力されたときは"AntiDishonestInsider"
						else if(option.toUpperCase().equals("A")){
							option = "AntiDishonestInsider";
							optCheck = true;
						}
						// それ以外は受け付けない
						else
							optCheck = false;
					}
					
					System.out.println("メンバーのユーザID");
					System.out.println("'end'で終了");
					acountlist = new ArrayList(addcnt);
					
					// ユーザIDをuserlistに追加していく
					for(int i=0; i<addcnt; i++){
						System.out.print("UserAcount=");
						uid = in.readLine();
						// "end"が入力されれば終了
						if(uid.equals("end"))
							break;
						// "end"以外ならばuserlistに追加
						else
							acountlist.add(uid);
					}
					// グループ追加を行うのメソッド
					GroupName = ginfo.GroupInformationXmlGenarator(gname, option, acountlist);
					// できたグループ情報ファイルを保存
					ginfo.saveFile();
					break;
					
				// 既存グループのユーザの追加
				case 2:
					// 追加したいグループのIDを入力
					System.out.print("GroupID=");
					gid = in.readLine();

					// 追加したいユーザのユーザIDを入力
					System.out.println("追加したいメンバーのユーザID");
					System.out.println("'end'で終了");
					userlist = new ArrayList(addcnt);
					for(int i=0; i<addcnt; i++){
						System.out.print("UseID=");
						uid = in.readLine();
						if(uid.equals("end"))
							break;
						else
							userlist.add(uid);
					}

					// オプションの設定
					while(!optCheck){
						System.out.println("Option (Normal or AntiDishonestInsider)");
						System.out.print("'N'or'A' =");
						option = in.readLine();
						if(option.toUpperCase().equals("N")){
							option = "Normal";
							optCheck = true;
						}
						else if(option.toUpperCase().equals("A")){
							option = "AntiDishonestInsider";
							optCheck = true;
						}
						else
							optCheck = false;
					}
					
					//　ユーザの追加を行うメソッド
					ginfo.AddUser(gid, option, userlist);
					ginfo.saveFile();
					break;
					
				//　グループの削除
				case 3:
					//　削除したいグループのIDを入力
					System.out.print("GroupID=");
					gid = in.readLine();
					
					//　グループを削除するメソッド
					ginfo.DeleteGroup(gid);
					ginfo.saveFile();
					break;
					
				//　既存グループのユーザの削除
				case 4:
					//　削除したいメンバーがいるグループのグループIDを入力
					System.out.print("GroupID=");
					gid = in.readLine();

					//　削除したいユーザの入力
					System.out.println("削除したいメンバーのユーザID");
					System.out.println("'end'で終了");
					userlist = new ArrayList(addcnt);
					for(int i=0; i<addcnt; i++){
						System.out.print("UseID=");
						uid = in.readLine();
						if(uid.equals("end"))
							break;
						else
							userlist.add(uid);
					}

					//　オプションの設定
					while(!optCheck){
						System.out.println("Option (Normal or AntiDishonestInsider)");
						System.out.print("'N'or'A' =");
						option = in.readLine();
						if(option.toUpperCase().equals("N")){
							option = "Normal";
							optCheck = true;
						}
						else if(option.toUpperCase().equals("A")){
							option = "AntiDishonestInsider";
							optCheck = true;
						}
						else
							optCheck = false;
					}
					
					//　ユーザを削除するメソッド
					ginfo.RemoveUser(gid, option, userlist);
					ginfo.saveFile();
					break;
					
				//　オプションの変更
				case 5:
					//　変更したいグループのグループIDを入力
					System.out.print("GroupID=");
					gid = in.readLine();
					
					//　＊getOption :　オプションを表示，グループがないときはnullを返す
					if(ginfo.getOption(gid) != null){
						//　変更後のオプションを設定
						while(!optCheck){
							System.out.println("Option (Normal or AntiDishonestInsider)");
							System.out.print("'N'or'A' =");
							option = in.readLine();
							if(option.toUpperCase().equals("N")){
								option = "Normal";
								optCheck = true;
							}
							else if(option.toUpperCase().equals("A")){
								option = "AntiDishonestInsider";
								optCheck = true;
							}
							else
								optCheck = false;
						}
						
						//　オプションの変更を行うメソッド
						ginfo.ChangeOption(gid, option);
						ginfo.saveFile();
						break;
					}
				
				default :
					break;
			}
		}catch(Exception e){
			;
		}
	
		return (GroupName);
	}
}
