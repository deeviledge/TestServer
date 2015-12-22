package sglserver.servermain;

import java.math.BigInteger;
import java.util.ArrayList;

import sglserver.deletegroupinformation.DeleteGroupInfoXml;
import sglserver.groupinformation.GroupInformationXml;
import sglserver.groupinformation.ReadGroupInfoXML;

public class TestDeletGroup {
	static public void main(String[] args) throws Exception{
		String gname="groupC";
		String tname="gB";
		ArrayList userlist = new ArrayList();
		String option = "AntiDishonestInsider";
		String gid="1";
		String uname = "Bob";
		String uID="0001";
		ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
		//選択されたグループのメンバ全員のユーザIDを取得 
		ArrayList memberID =rgi.getGroupMember4(gname);
		String gID = rgi.getGroupID();
		System.out.println(gID);
		for(int i=0;i<memberID.size();i++){
		System.out.println(memberID.get(i));
		}
		System.out.println("グループ削除手続き1********************");
		
		//DeleteGroup.xmlに削除するグループメンバのユーザIDを保存
		DeleteGroupInfoXml dgi = new DeleteGroupInfoXml();
		dgi.makegroup2(gname, memberID);
	    dgi.saveFile();
	    
	    System.out.println("グループ削除手続き2********************");
		
		//DeleteGroup.xmlから申請してきたグループメンバのユーザIDを削除
		dgi.deleteuserid(gname, uID);
		dgi.saveFile();
		System.out.println("グループ削除手続き完了********************");
		
		System.out.println("グループ削除開始***********************");
		GroupInformationXml	ginfo = new GroupInformationXml();
		//GroupInformation.xmlを編集
		ginfo.DeleteGroup2(gID);
		ginfo.saveFile();
	}
}
