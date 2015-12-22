package sglserver.servermain;

import java.math.BigInteger;
import java.util.ArrayList;

import sglserver.deletegroupinformation.DeleteGroupInfoXml;
import sglserver.groupinformation.GroupInformationXml;
import sglserver.groupinformation.ReadGroupInfoXML;

public class TestEditGroupProcess {
	static public void main(String[] args) throws Exception{
		String gname = "groupB";
		String option = "AntiDishonestInsider";
		ArrayList acountlist = new ArrayList();//編集後のグループメンバ
		acountlist.add("Alice");
		acountlist.add("Hayashi");
		ArrayList dlist = new ArrayList();//削除されるメンバ
		dlist.add("Harada");
		BigInteger[][] key = new BigInteger[3][7];
		key[1][1] = new BigInteger("1111111111");		//n
		key[1][2] = new BigInteger("2222222222");		//a
		key[1][3] = new BigInteger("3333333333");		//a0
		key[1][4] = new BigInteger("4444444444");		//y
		key[1][5] = new BigInteger("5555555555");		//g
		key[1][6] = new BigInteger("6666666666");		//h
		key[2][1] = new BigInteger("7777777777");		//p1
		key[2][2] = new BigInteger("8888888888");		//q1
		key[2][3] = new BigInteger("9999999999");		//x		
		BigInteger[][] cer = new BigInteger[acountlist.size()][3];
		cer[0][1] = new BigInteger("11111");
		cer[0][2] = new BigInteger("22222");
		cer[1][1] = new BigInteger("33333");
		cer[1][2] = new BigInteger("44444");
		String uid="0030";
		
		
		
		
		GroupInformationXml	ginfo = new GroupInformationXml();
		ginfo.EditGroup(gname, option, acountlist, dlist, key, cer, uid);
		ginfo.saveFile();	
	}
}
