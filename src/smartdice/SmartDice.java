/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartdice;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author Chukozy
 */
public class SmartDice extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception{
        Pane root = FXMLLoader.load(getClass().getResource("SmartDiceFXML.fxml"));
        stage.setTitle("Smart Dice");
        stage.setScene(new Scene(root));
        stage.setMinWidth(890);
        stage.setMinHeight(706);
        stage.show();
        
    }
    
}
