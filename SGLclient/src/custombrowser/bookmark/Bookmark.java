package custombrowser.bookmark;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
 * ブックマーククラス
 * 	サイト名,url,各コンストラクタ、各get,set
 */
public class Bookmark {
	private StringProperty site = new SimpleStringProperty();
    private StringProperty url = new SimpleStringProperty();
	public Bookmark(String site,String url){
		this.site.set(site);
		this.url.set(url);
	}
	
	public StringProperty siteProperty(){
		return site;
	}
	
	public StringProperty urlProperty(){
		return url;
	}
	
	public String getSite(){
		return site.get();
	}
	
	public void setSite(String site){
		this.site.set(site);
	}
	
	public String getURL(){
		return url.get();
	}
	
	public void setURL(String url){
		this.url.set(url);
	}

}
