/*
 * 作成日: 2004/12/28
 *
 */
package sglserver.groupadmin;

/**
 * @author Oda Satoshi
 *
 */
public class GenerateMandateXML{
	
	public Group_Key_Infomation gki_g;
	public int MaxRound;
	
	public GenerateMandateXML(String GroupName){
		
		String filename = "src/sglserver/conf/usr/xml_files/groups/sekkei_" + GroupName + ".xml";
		String savename = "src/sglserver/conf/usr/xml_files/groups/Mandate_" + GroupName + ".xml";
		
		/*
		if (args.length > 2){
			System.out.println("usage:java GenerateMandateXML xmlfile");
		}
		*/
		
		//グループXMLファイルを読み込む。
		Group_Key_Infomation gki_g = new Group_Key_Infomation(filename);

		//グループXMLファイルを解析し、出力する。
		generateXML gxml = new generateXML(savename, gki_g, GroupName);
		MaxRound = gxml.DoneRound;
	}
	
}
