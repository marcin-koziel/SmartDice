/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartdice;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.css.Style;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.PlayerProfile;
import model.SmartDiceGame;

/**
 * FXML Controller class
 *
 * @author chuko
 */
public class ToolbarController implements Initializable {

    @FXML
    private VBox vboxToolBarItems;
    @FXML
    private StackPane toolBarMain;
    @FXML
    private StackPane playerProfileNameNode;
    @FXML
    private AnchorPane playerProfileListNode;
    @FXML
    private Label profileNameLabel;
    @FXML
    private ImageView profileNameImg;
    @FXML
    private GridPane toolBarGridPane;

    private static ToolbarController instance;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        setListeners();
    }

    public static ToolbarController getInstance(){
        return instance;
    }

//    public void addLabel() {
//
//    }

    private void updateNameLabel(String toolbarName) {
        profileNameLabel.setText(toolbarName);
    }

    private void setGuestOptions(String toolbarName) {
        updateNameLabel(toolbarName);

        Label usernameLbl = new Label("Username");
        Label passwordLbl = new Label("Password");

        TextField usernameTxtfld = new TextField();
        TextField passwordTxtfld = new TextField();

        // --- DEBUGGING ---
        Label developerLbl = new Label("Sign In as Developer");
        developerLbl.setCursor(Cursor.HAND);
        developerLbl.setStyle("-fx-font-weight: bold;");
        developerLbl.setOnMousePressed(l->{
            PlayerProfile developerProfile;
            developerProfile = SmartDiceGame.getInstance().getProfileContainer().createPlayerProfile("Developer");
            developerProfile.setName("ProfileName");
            SmartDiceGame.getInstance().getProfileContainer().createPlayerProfile("Marcin");
            SmartDiceGame.getInstance().getProfileContainer().createPlayerProfile("Martha");
            SmartDiceGame.getInstance().getProfileContainer().createPlayerProfile("Paul");
            SmartDiceGame.getInstance().setCurrentPlayerProfile(developerProfile);
            signIn();
            SmartDiceGame.getInstance().getProfileContainer().writeProfileContainer();
            SmartDiceGame.getInstance().getProfileContainer().readPlayerContainer();
        });
        vboxToolBarItems.getChildren().clear();
        vboxToolBarItems.getChildren().addAll(usernameLbl, usernameTxtfld, passwordLbl, passwordTxtfld, developerLbl);
    }

    private void signIn(){
        SmartDiceController.getInstance().updateSmartDiceWindow();
    }

    private void setUserOptions(String toolbarName) {
        updateNameLabel(toolbarName);

        Label profile = new Label("Profile");
        Label signOut = new Label("Sign Out");

        profile.setCursor(Cursor.HAND);
        signOut.setCursor(Cursor.HAND);

        vboxToolBarItems.getChildren().clear();
        vboxToolBarItems.getChildren().addAll(profile, signOut);
    }

    public void updateToolbar(PlayerProfile loadedProfile){
        if(loadedProfile.getUsername() != null){
            setUserOptions(loadedProfile.getName());
        } else {
            setGuestOptions("Sign In");
        }
    }

    private void togglePlayerProfileList() {
        playerProfileListNode.setVisible(!playerProfileListNode.isVisible());
        playerProfileListNode.setDisable(!playerProfileListNode.isVisible());
        if (playerProfileListNode.isVisible()) {
            profileNameImg.setRotate(180);
        } else {
            profileNameImg.setRotate(0);
        }
    }

    private void setListeners() {

        //
        playerProfileNameNode.setOnMousePressed(l -> {
            togglePlayerProfileList();
        });

        playerProfileListNode.setOnMousePressed(l -> {
            togglePlayerProfileList();
        });

        // If hover is false, toggle to close/hide profilePlayerListNode
        toolBarMain.setOnMouseExited(l -> {
            if (playerProfileListNode.isVisible()) {
                if (!playerProfileNameNode.isHover() || !playerProfileListNode.isHover()) {
                    togglePlayerProfileList();
                }
            }
        });



    }



}
