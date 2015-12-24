/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser.loginprocess;

/**
 *
 * @author nishimura
 */
public class User {
    private String username;
    private String mailaddress;
    private String serverip;
    
    public void setName(String text){
        username = text;
    }
    
    public void setAddress(String text){
        mailaddress = text;
    }
    
    public void setServerIP(String text){
        serverip = text;
    }
    
    public String getName(){
        return username;
    }
    
    public String getAddress(){
        return mailaddress;
    }
    
    public String getServerIP(){
        return serverip;
    }
}
