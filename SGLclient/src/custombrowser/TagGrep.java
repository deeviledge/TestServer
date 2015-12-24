package custombrowser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TagGrep {

	
	public static String taggrep(String data){
				String target="";
				String regex="<(?i)meta.*?charset=([^\"']+)";
				Pattern ptn = Pattern.compile(regex, Pattern.DOTALL);
				Matcher matcher = ptn.matcher(data);
				while(matcher.find()){
					target+=matcher.group(1);
					}
				
			
		//System.out.println("文字コード:"+target);
		return target;
	}
}
