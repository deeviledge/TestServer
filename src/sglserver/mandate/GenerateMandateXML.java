
/**
 * MandateXML(鍵配送指令書)を作成するメインクラス
 * @auther fujino
 * @version 2.0 
 * @作成日: 2008/11/17
 * @最終更新日:2008/11/17
 */

package sglserver.mandate;

import sglserver.groupinformation.ReadGroupInfoXML;

public class GenerateMandateXML{
	
	public GenerateMandateXML(String groupname){

		//String	groupname = "size5";					// グループ名 : ここでは直打ちですが、通常、グループ名指定で情報を受け取る
		String	mfilename = "src/sglserver/conf/usr/xml_files/groups/mandate_"+groupname+".xml";	// 鍵配送指令書XMLファイル名
		
		ReadGroupInfoXML gInfo = new ReadGroupInfoXML(groupname);

		int N = gInfo.getGroupValue();							// グループメンバ数
		int T = (int) Math.ceil( (Math.log(N)/Math.log(2)) );	// 総ラウンド数
		
		// Mandate.XMLの枠組みを作る
		new MandateXMLbase(N, T, mfilename, groupname);
		
		// 実際にMandate.xmlに情報を書き込む
		new WriteMandate(groupname, mfilename);
	}
}