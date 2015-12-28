/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sglserver.keyexchange;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author tatoo
 */
public class ServerThread {
    ServerSocket serversoc;
    Socket socket;
    
    public ServerThread(String ip,int roundport){
        try {
                System.out.println("サーバソケットを生成するよ");
                serversoc=new ServerSocket(roundport);
                socket =serversoc.accept();//待機状態へ移行
                
                while(socket.isConnected())
                {//サーバからの接続要求を待機する
                    System.out.println("Socketからの接続要求を待機中...");
                }
                
                System.out.println("SGLクライアントIP="+ip+"："+socket.getInetAddress()+"との接続完了");//接続先アドレスを返して表示
            } catch (IOException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                    System.out.println("サーバーソケット生成中になんかあった");
            }
        
    }
    
    public void SavePK(){
    
    
    }
    
    public void XmlFlg(){
        
    }
    
}