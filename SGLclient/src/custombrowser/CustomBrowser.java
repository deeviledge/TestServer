/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sglclient.certificate.SglLogout;
import sglclient.groupadmin.ClientThread;



/**
 *
 * @author nishimura
 */
public class CustomBrowser extends Application {
    ClientThread CT;
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("CustomBrowser.fxml"));
        
        Scene scene = new Scene(root);
        stage.setTitle("CustomWebBrowser");
        stage.setScene(scene);
        stage.show();
        
        CT = new ClientThread();
        CT.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() {
        new SglLogout();
        CT.exit();
    }
    
}
