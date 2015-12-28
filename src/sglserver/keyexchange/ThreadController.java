/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sglserver.keyexchange;

import java.net.ServerSocket;
import sglserver.dynamicpeerinformation.DynamicPeerInformation;

/**
 *
 * @author tatoo
 */
public class ThreadController {
    
    ServerSocket serversoc;
    String ip1,ip2;
    int roundport1,roundport2;
    public ThreadController(String name1,String name2){
        
        try{
            DynamicPeerInformation dpinfo = new DynamicPeerInformation();
            roundport1=Integer.parseInt("5"+dpinfo.getID(name1));//usernameからIDを取得して5と文字連結させてint型に変換
            roundport2=Integer.parseInt("5"+dpinfo.getID(name2));
            ip1=dpinfo.getIP(name1);//usernameからユーザのIPを取得
            ip2=dpinfo.getIP(name2);

            ServerThread st1=new ServerThread(ip1,roundport1);
            ServerThread st2=new ServerThread(ip2,roundport2);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("サーバースレッドが生成できないっぽいよ");
        }
        /*
        //xmlの書き込みが競合しないようにするフラグ管理
        st1.XmlFlg();
        st2.XmlFlg();

        
        try{
        //鍵の保存
        st1.SavePK();
        st2.SavePK();
        }catch(FileException e){
        
        }
        
        
        //xmlの書き込みが競合しないようにするフラグ管理
        st1.XmlFlg();
        st2.XmlFlg();
        
        try{
        //保存されている鍵の読み出し
        PK1=st1.ReadXml();
        PK2=st2.ReadXml();
        }catch(Exception e){
        
        }
        
        try{
        //本来のクライアントへ鍵を送信する
        st1.SendPK(PK2);
        st2.SendPK(PK1);
        }catch(Exception e){
        
        }
        
        */
        
        
        
    }
    
}









