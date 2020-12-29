/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartdice.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import smartdice.model.PlayerProfile;
import smartdice.model.SmartDiceGame;

/**
 *
 * @author Marcin Koziel
 */
public class SmartDiceController implements Initializable {

    @FXML
    private BorderPane smartDiceMain;
    @FXML
    private BorderPane smartDiceInteractive;
    @FXML
    private AnchorPane setNavBarItem;
    @FXML
    private Font x3;
    @FXML
    private Color x4;

    private ArrayList<AnchorPane> navBarPaneList;
    private ArrayList<ToggleButton> navBarList;
    private ArrayList<TranslateTransition> ttEnterList;
    private ArrayList<TranslateTransition> ttExitList;

    private ObservableList<AnchorPane> navBarSideObserv;
    private ListView<AnchorPane> navBarSideList;

    private static SmartDiceController smartDiceController;
    private static ToolbarController toolbarController;
    private static SideBarNavigationController sideBarNavigationController;

    private StackPane toolbarMain;
    private VBox sideBarNavMain;

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

    // Setting the interactive section on the main window
    public void setMainPane(Pane pane) {
        setNavBarItem.getChildren().setAll(pane);
    }

    // Initialize panes and controllers for Toolbar and SideBarNav.
    private void initSmartDiceWindow() {
        sideBarNavMain = (VBox) getPaneFXMLPath("/smartdice/fxml/SideBarNavigationFXML.fxml");
        sideBarNavigationController = SideBarNavigationController.getInstance();
        toolbarMain = (StackPane) getPaneFXMLPath("/smartdice/fxml/ToolbarFXML.fxml");
        toolbarController = ToolbarController.getInstance();
        // Setting border anchor panes left and top
        smartDiceMain.setLeft(sideBarNavMain);
        smartDiceInteractive.setTop(toolbarMain);
        // Update view to current model properties
        updateSmartDiceWindow();
    }

    // Updates relevant panes to current model properties
    public void updateSmartDiceWindow() {
        PlayerProfile loadProfile = SmartDiceGame.getInstance().getCurrentPlayerProfile();

        toolbarController.updateToolbar(loadProfile);

        //TODO: Profile Username is referred to for now (intending to use ID)
        if (loadProfile.getUsername() != null) {
            setNavBarItem.setDisable(false);
        } else {
            setNavBarItem.setDisable(true);
        }

        //TODO: Switch for Enum state, effectively not updating all panes, just the active one
        sideBarNavigationController.getHomeController().updateStats();

        // --- DEBUGGING ---
//        System.out.println(SmartDiceGame.getInstance().getCurrentPlayerProfile().toString());

    }

    // Used to crunch down fxml loader code and return the pane based on fxmlPath
    private Pane getPaneFXMLPath(String fxmlPath) {
        Pane pane = null;
        FXMLLoader controller = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            URL location = getClass().getResource(fxmlPath);
            loader.setLocation(location);
            pane = loader.load(location.openStream());
//            controller = loader.getController();
            // TODO: redo exception
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pane;
    }

}
