/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverjavafx;

import sglserver.servermain.CAMain;

/**
 *
 * @author nishimura
 */
public class ServerThread extends Thread{
  
    CAMain CAmain;
    
    @Override
    public void run() {  //(2)スレッド実行コードをrunメソッドに記載
      CAmain = new CAMain();
    }
    
    public void finish(){
        
        CAmain.exit();
    }
}
