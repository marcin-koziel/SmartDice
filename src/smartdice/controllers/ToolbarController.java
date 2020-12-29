package smartdice.controllers;

import java.net.URL;
import java.util.ResourceBundle;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import smartdice.model.PlayerProfile;
import smartdice.model.SmartDiceGame;

/**
 *
 * @author Marcin Koziel
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



//    // --- Debugging ----
//    PlayerProfile developerProfile =
//            SmartDiceGame.getInstance().getProfileContainer().createPlayerProfile("Developer");

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

    private void updateNameLabel(String toolbarName) {
        profileNameLabel.setText(toolbarName);
    }

    private void setGuestOptions(String toolbarName) {
        updateNameLabel(toolbarName);

        Label usernameLbl = new Label("Username");
        Label passwordLbl = new Label("Password");

        TextField usernameTxtfld = new TextField("Developer");
        TextField passwordTxtfld = new TextField("1234");

        Button signIn = new Button("Sign In");

        signIn.setOnMousePressed(l->{
            signIn(usernameTxtfld.getText(), passwordTxtfld.getText());
        });

        vboxToolBarItems.getChildren().clear();
        vboxToolBarItems.getChildren().addAll(usernameLbl, usernameTxtfld, passwordLbl, passwordTxtfld, signIn);
    }

    // TODO: More functional with arg(s)
    private void signIn(String username, String password) {
        PlayerProfile playerProfile = SmartDiceGame.getInstance().getProfileContainer().getPlayerProfile(username, password);
        SmartDiceGame.getInstance().setCurrentPlayerProfile(playerProfile);
        SmartDiceController.getInstance().updateSmartDiceWindow();
    }
    // TODO: More functional with arg(s)
    private void signOut() {
        SmartDiceGame.getInstance().setCurrentPlayerProfile(new PlayerProfile());
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

        // TODO: Throw into setListeners
        signOut.setOnMousePressed(l->{
            signOut();
        });
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
