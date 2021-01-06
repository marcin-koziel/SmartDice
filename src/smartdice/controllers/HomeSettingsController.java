/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartdice.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import smartdice.model.DiceGame;
import smartdice.model.SmartDiceGame;

/**
 * FXML Controller class
 *
 * @author Marcin Koziel
 */
public class HomeSettingsController extends ClassController<HomeSettingsController> {

    @FXML
    private StackPane stackPaneHomeSettings;
    @FXML
    private VBox stackPaneHomeSettingsMainWin;
    @FXML
    private ImageView stackPaneHomeSettingsClose;

    private AnchorPane anchorPaneParent;
    @FXML
    private TextField inputBetAmount;
    @FXML
    private TextField profitOnWin;
    @FXML
    private TextField inputRollLoop;
    @FXML
    private TextField inputRollOverNo;
    @FXML
    private TextField inputPayout;
    @FXML
    private TextField inputWinChance;
    @FXML
    private StackPane scrollRollOverNoColorParent;
    @FXML
    private Rectangle scrollRollOverNoColorGreen;
    @FXML
    private ScrollBar scrollRollOverNo;
    @FXML
    private TextField inputMaxRoll;
    @FXML
    private Group homeSettingsSave;

    private ArrayList<StackPane> homeFieldList;

    private ArrayList<TextField> inputTextFieldList;

    private String stateName;

    private DiceGame diceGameTemp;
    private int rollLoopTemp;

    public HomeSettingsController(){
        super();
    }

    public HomeSettingsController(String path) {
        super(path);
    }

    /*
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        diceGameTemp = SmartDiceGame.getInstance().getActiveDiceGame().getDiceGameSettings();
        rollLoopTemp = SmartDiceGame.getInstance().getRollLoop();
        setListeners();
        updateAllTextFields();
    }

    public void setHomeFields(StackPane stackPane) {
        homeFieldList.add(stackPane);
    }

    // TODO: Not used
    private void groupObjects() {
        inputTextFieldList.add(inputBetAmount);
        inputTextFieldList.add(inputRollLoop);
        inputTextFieldList.add(inputRollOverNo);
//        inputTextFieldList.add(profitOnWin);
//        inputTextFieldList.add(inputPayout);
//        inputTextFieldList.add(inputWinChance);
//        inputTextFieldList.add(inputMaxRoll);
    }

    private void setInputBetAmount(String string) {
        inputBetAmount.setText(string);
    }

    private void setProfitOnWin(String string) {
        profitOnWin.setText(string);
    }

    private void setInputRollLoop(String string) {
        inputRollLoop.setText(string);
    }

    private void setInputRollOverNo(String string) {
        inputRollOverNo.setText(string);
    }

    private void setInputPayout(String string) {
        inputPayout.setText(string);
    }

    private void setInputWinChance(String string) {
        inputWinChance.setText(string);
    }

    private void setInputMaxRoll(String string) {
        inputMaxRoll.setText(string);
    }

    public Group getHomeSettingsSave() {
        return homeSettingsSave;
    }

    private void updateAllTextFields() {
        diceGameTemp.generateSettings();
        setInputBetAmount(String.format("%.8f", diceGameTemp.getBetAmount()));
        setProfitOnWin(String.format("%.8f", diceGameTemp.getProfit()));
        setInputRollLoop(String.format("%d", rollLoopTemp));
        setInputRollOverNo(String.format("%.2f", diceGameTemp.getRollOverNo()));
        setInputPayout(String.format("%.4f", diceGameTemp.getPayout()));
        setInputWinChance(String.format("%.2f", diceGameTemp.getWinChance()));
    }

    private boolean isDiceGameCloneUpdated() {
        double inputBetAmountTemp = Double.parseDouble(inputBetAmount.getText());
        double inputRollOverNoTemp = Double.parseDouble(inputRollOverNo.getText());
        double inputRollLoopTemp = Double.parseDouble(inputRollLoop.getText());

        if (diceGameTemp.getBetAmount() != inputBetAmountTemp) {
            if (SmartDiceGame.getInstance().getCurrentPlayerProfile().getBalance() < inputBetAmountTemp) {
                diceGameTemp.setBetAmount(SmartDiceGame.getInstance().getActiveDiceGame().getBetAmount());
                return true;
            } else {
                // Update DiceGameTemp to text-field value
                diceGameTemp.setBetAmount(inputBetAmountTemp);
                return true;
            }
        }
        if (diceGameTemp.getRollOverNo() != inputRollOverNoTemp) {
            // Update DiceGameTemp to text field value
            diceGameTemp.setRollOverNo(inputRollOverNoTemp);
            // Set ScrollBar visual to represent text field value
            double pixelWidth = (inputRollOverNoTemp * 98) / scrollRollOverNo.maxProperty().getValue();
            pixelWidth = pixelWidth * 3.7;
            scrollRollOverNoColorGreen.widthProperty().set(pixelWidth);
            // Set ScrollBar value to text field value
            scrollRollOverNo.setValue(inputRollOverNoTemp);
            return true;
        }
        if (rollLoopTemp != inputRollLoopTemp) {
            // Update DiceGameTemp to text field value
            rollLoopTemp = (int) inputRollLoopTemp;
            return true;
        }
        return false;
    }

    private void setListeners() {
        // Closed button removes HomeSettings child object
        stackPaneHomeSettingsClose.setOnMouseClicked(l -> {
            if (anchorPaneParent == null) {
                anchorPaneParent = (AnchorPane) stackPaneHomeSettings.getParent();
            }
            anchorPaneParent.getChildren().remove(anchorPaneParent.getChildren().size() - 1);
        });
        // Scrollbar visual updates based on value
        scrollRollOverNo.valueProperty().addListener(l -> {
            double pixelWidth = (scrollRollOverNo.getValue() * 98) / scrollRollOverNo.maxProperty().getValue();
            pixelWidth = pixelWidth * 3.7;
            inputRollOverNo.setText(String.format("%.2f", scrollRollOverNo.valueProperty().getValue()));
            scrollRollOverNoColorGreen.widthProperty().set(pixelWidth);
        });
        // Scrollbar - dragged
        scrollRollOverNo.setOnMouseReleased(l -> {
            diceGameTemp.setRollOverNo(Double.parseDouble(String.format("%.2f", scrollRollOverNo.valueProperty().getValue())));
            updateAllTextFields();
        });
        // Scrollbar - clicked
        scrollRollOverNo.setOnMouseClicked(l -> {
            diceGameTemp.setRollOverNo(Double.parseDouble(String.format("%.2f", scrollRollOverNo.valueProperty().getValue())));
            updateAllTextFields();
        });
        // Bet Amount - Released Enter
        inputBetAmount.setOnKeyReleased(l -> {
            if (KeyCode.ENTER == l.getCode() || KeyCode.TAB == l.getCode()) {
                if (isDiceGameCloneUpdated()) {
                    updateAllTextFields();
                }
            }
        });
        // Roll Over No. - Released Enter
        inputRollOverNo.setOnKeyReleased(l -> {
            if (KeyCode.ENTER == l.getCode() || KeyCode.TAB == l.getCode()) {
                if (isDiceGameCloneUpdated()) {
                    updateAllTextFields();
                }
            }
        });
        // Roll Loop - Released Enter
        inputRollLoop.setOnKeyReleased(l -> {
            if (KeyCode.ENTER == l.getCode() || KeyCode.TAB == l.getCode()) {
                if (isDiceGameCloneUpdated()) {
                    updateAllTextFields();
                }
            }
        });
        // Bet Amount - Mouse Pressed
        inputBetAmount.setOnMousePressed(l -> {
            if (isDiceGameCloneUpdated()) {
                updateAllTextFields();
            }
        });
        // Roll Over No. - Mouse Pressed
        inputRollOverNo.setOnMousePressed(l -> {
            if (isDiceGameCloneUpdated()) {
                updateAllTextFields();
            }
        });
        // Roll Loop - Mouse Pressed
        inputRollLoop.setOnMousePressed(l -> {
            if (isDiceGameCloneUpdated()) {
                updateAllTextFields();
            }
        });
        // HomeSettings - Mouse Pressed
        stackPaneHomeSettingsMainWin.setOnMousePressed(l -> {
            if (isDiceGameCloneUpdated()) {
                updateAllTextFields();
            }
        });
        // HomeSettings Save Button - Mouse Pressed
        homeSettingsSave.setOnMousePressed(l -> {
            if (isDiceGameCloneUpdated()) {
                updateAllTextFields();
            }
            SmartDiceGame.getInstance().getActiveDiceGame().setBetAmount(diceGameTemp.getBetAmount());
            SmartDiceGame.getInstance().getActiveDiceGame().setRollOverNo(diceGameTemp.getRollOverNo());
            SmartDiceGame.getInstance().setRollLoop(rollLoopTemp);

        });

    }

}
