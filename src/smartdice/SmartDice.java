/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartdice;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import smartdice.controllers.SmartDiceController;

/**
 * SmartDice is an open-source JavaFX application in progress that uses an object-oriented approach
 * for a generation of data, in which case is "rolling dice" and updating user(s) profile data accordingly.
 * The "Smart" aspect is to implement a user-friendly feature that uses Machine Learning (ML) to explore the
 * realm of probability and display it in a human-interpretable manner when enabled.
 *
 * @author Marcin Koziel
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
//        Pane root = FXMLLoader.load(getClass().getResource("fxml/SmartDiceFXML.fxml"));
//        SmartDiceController smartDiceController = new SmartDiceController("/smartdice/fxml/SmartDiceFXML.fxml");
        Pane root = new SmartDiceController("/smartdice/fxml/SmartDiceFXML.fxml").getPane();
        stage.setTitle("Smart Dice");
        stage.setScene(new Scene(root));
        stage.setMinWidth(890);
        stage.setMinHeight(706);
        stage.show();
        
    }
    
}
