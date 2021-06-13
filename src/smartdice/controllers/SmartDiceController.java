/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartdice.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import smartdice.SmartDice;
import smartdice.model.PlayerProfile;
import smartdice.model.SmartDiceGame;

/**
 *
 * @author Marcin Koziel
 */
public class SmartDiceController extends ClassController<SmartDice> {

    @FXML
    private BorderPane smartDiceRootBorderPane;
    @FXML
    private BorderPane smartDiceCenterBorderPane;
    @FXML
    private AnchorPane smartDiceCenterAnchorPane;
    @FXML
    private static ToolbarController toolbarController;
    @FXML
    private static SideBarNavigationController sideBarNavigationController;
    @FXML
    private StackPane toolbarPane;
    @FXML
    private VBox sideBarNavPane;

    private static SmartDiceController smartDiceController;

    private ArrayList<AnchorPane> navBarPaneList;
    private ArrayList<ToggleButton> navBarList;
    private ArrayList<TranslateTransition> ttEnterList;
    private ArrayList<TranslateTransition> ttExitList;

    private ObservableList<AnchorPane> navBarSideObserv;
    private ListView<AnchorPane> navBarSideList;

    public SmartDiceController(){
        super();
    }

    public SmartDiceController(String fxml){
        super(fxml);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        smartDiceController = this;
        initSmartDiceWindow();
    }

    // Return the main SmartDice controller instance
    public static SmartDiceController getInstance() {
        return smartDiceController;
    }

    // Initialize panes and controllers for Toolbar and SideBarNav.
    private void initSmartDiceWindow() {
        sideBarNavigationController = new SideBarNavigationController("/smartdice/fxml/SideBarNavigationFXML.fxml");
        toolbarController = new ToolbarController("/smartdice/fxml/ToolbarFXML.fxml");

//        sideBarNavigationController.addClassTable("smartDice" ,smartDiceController);
//        toolbarController.addClassTable("smartDice" ,smartDiceController);

        sideBarNavPane = (VBox) sideBarNavigationController.getPane();
        sideBarNavigationController = sideBarNavigationController.getController();

        toolbarPane = (StackPane) toolbarController.getPane();
        toolbarController = toolbarController.getController();

        // Setting border anchor panes left and top
        smartDiceRootBorderPane.setLeft(sideBarNavPane);
        smartDiceCenterBorderPane.setTop(toolbarPane);
        // sideBarNavigationController.setDefaultTab();

        // Update view based on login status
        updateLoginStatus();
    }

    // Update the according panes for view based on login status
    public void updateLoginStatus() {
        PlayerProfile loadProfile = SmartDiceGame.getInstance().getCurrentPlayerProfile();

        toolbarController.updateToolbar(loadProfile);

        //TODO: Profile Username is referred to for now (intend to use ID) + Encapsulation
        if (loadProfile.getUsername() != null) {
            smartDiceCenterAnchorPane.setDisable(false);
            sideBarNavPane.setDisable(false);
        } else {
            smartDiceCenterAnchorPane.setDisable(true);
            sideBarNavPane.setDisable(true);
        }

        sideBarNavigationController.getHomeController().updateStats();
    }

    // Setting the interactive section on the main window
    public void setMainPane(Pane pane) {
        smartDiceCenterAnchorPane.getChildren().setAll(pane);
    }

}
