/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sglserver.servermain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import sglserver.deletegroupinformation.DeleteGroupInfoXml;

/**
 *
 * @author nishimura
 */
public class CheckDeleteProcess {
    public CheckDeleteProcess(Socket soc,ObjectInputStream ois,ObjectOutputStream oos) throws IOException, ClassNotFoundException{
        
        String id = (String)ois.readObject();
        oos.writeObject("OK");
        DeleteGroupInfoXml dgix = new DeleteGroupInfoXml();
	dgix.DeleteGroup(id,oos); // 削除
    }
}
