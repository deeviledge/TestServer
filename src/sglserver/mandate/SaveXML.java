/**
 * XML保存するクラス
 * 
 * @author fujino
 * @version 2.0
 * @作成日: 2008/11/17
 * @最終更新日:2008/11/17
 */

package sglserver.mandate;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class SaveXML {

	public SaveXML(){
	
	}
	
	public SaveXML(String filename, Document document){
		
		try{
			// xml保存 変換
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			// xml保存
			DOMSource source = new DOMSource(document);
			File newXML = new File( filename ); 
			FileOutputStream os = new FileOutputStream(newXML); 
			StreamResult result = new StreamResult(os); 
			transformer.transform(source,result);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
