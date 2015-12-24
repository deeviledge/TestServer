/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser;

public class TabPack {
    private javafx.scene.control.Tab Tab = new javafx.scene.control.Tab();
    private CustomBrowserTabController CBTC = new CustomBrowserTabController();
    private CustomBrowserController CBC;
    private TabPack nextTabPack;
    private TabPack beforeTabPack;
    public int number=0;
    public TabPack(){
    	//タブパック：デフォルトコンストラクタ
    	number=1;
    }
    
	public TabPack(javafx.scene.control.Tab Tab,CustomBrowserTabController CBTC) {
		this.Tab=Tab;
                this.CBTC=CBTC;
		//System.out.println("TabPackコンストラクタ");
		number=2;
	}
	
	public TabPack(javafx.scene.control.Tab Tab,CustomBrowserController CBC) {
		this.Tab=Tab;
		this.CBC=CBC;
		//System.out.println("1stTabPackコンストラクタ");
		number=3;
	}
	public javafx.scene.control.Tab Tab(){
		return Tab;
	}
	
	public CustomBrowserTabController CBTC(){
		return CBTC;
	}
	
	public javafx.scene.control.Tab getTab(){
		return this.Tab;
	}
	
	public void setTab(javafx.scene.control.Tab Tab){
		this.Tab=Tab();
	}
	public CustomBrowserController getCBC(){
		return this.CBC;
	}
	
	public CustomBrowserTabController getCBTC(){
		return this.CBTC;
	}
	
	public void setCBTC(CustomBrowserTabController CBTC){
		this.CBTC=CBTC;
	}
	
	public TabPack getNextTabPack(){
		return this.nextTabPack;
	}
	public void setNextTabPack(TabPack nextTabPack){
		this.nextTabPack=nextTabPack;
	}
	public TabPack getBeforeTabPack(){
		return this.beforeTabPack;
	}
	public void setBeforeTabPack(TabPack beforeTabPack){
		this.beforeTabPack=beforeTabPack;
	}
}