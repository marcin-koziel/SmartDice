package smartdice.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import smartdice.model.DiceGame;
import smartdice.model.SmartDiceGame;

/**
 * FXML Controller class
 *
 * @author Marcin Koziel
 */
public class HomeController extends ClassController<HomeController> {

    @FXML
    private AnchorPane anchorPaneHomeParent;
    @FXML
    private StackPane stackPaneHomeSettings;
    @FXML
    private ImageView stackPaneHomeSettingsClose;
    @FXML
    private Label homeScrollText;
    @FXML
    private StackPane homeScrollBar;
    @FXML
    private Rectangle homeScrollProgressBar;
    @FXML
    private StackPane homeScrollRectangle;
    @FXML
    private Label homeBalance;
    @FXML
    private Label homeBetAmount;
    @FXML
    private Label homeProfit;
    @FXML
    private Label homeStatPercent;
    @FXML
    private Label homeStatWins;
    @FXML
    private Label homeStatLosses;
    @FXML
    private Label homeCurrRoll;
    @FXML
    private Label homeStatRollNo;
    @FXML
    private Label homeStatCurrStreak;
    @FXML
    private Label homeStatHighStreak;
    @FXML
    private TableView<DiceGame> homeTable;
    @FXML
    private TableColumn<DiceGame, Double> idColumn;
    @FXML
    private TableColumn<DiceGame, String> timeColumn;
    @FXML
    private TableColumn<DiceGame, Double> multiplierColumn;
    @FXML
    private TableColumn<DiceGame, Double> gameColumn;
    @FXML
    private TableColumn<DiceGame, Boolean> resultColumn;
    @FXML
    private TableColumn<DiceGame, Double> rollColumn;
    @FXML
    private TableColumn<DiceGame, Double> profitColumn;
    @FXML
    private Label homeShowMore;

    private ObservableList<DiceGame> viewDiceGameList;

//    private HomeSettingsController homeSettingsController;

    public HomeController(){
        super();
    }

    public HomeController(String path) {
        super(path);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTable();
        updateStats();
        setListeners();
    }

    public HomeController me(){
        return this;
    }

    private void initTable() {
        // TODO: Make this more "readable" ? 
        homeTable.setPlaceholder(new Label("No Record of Dice Games found."));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("dateFmt"));
        multiplierColumn.setCellValueFactory(new PropertyValueFactory<>("multiplierStrFmt"));
        gameColumn.setCellValueFactory(new PropertyValueFactory<>("rollOverNo"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("rollWin"));
        rollColumn.setCellValueFactory(new PropertyValueFactory<>("diceRoll"));
        profitColumn.setCellValueFactory(new PropertyValueFactory<>("profitStrFmt"));
        viewDiceGameList = FXCollections.observableArrayList();
        homeTable.setItems(viewDiceGameList);
        viewDiceGameList.setAll(SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getDiceGameList());
        idColumn.setSortType(TableColumn.SortType.DESCENDING);
        homeTable.getSortOrder().add(idColumn);
        homeTable.refresh();
    }

    @FXML
    private void setRollDice(MouseEvent event) throws RuntimeException {
        if (SmartDiceGame.getInstance().isPlayable()) {
            SmartDiceGame.getInstance().playRound();
            updateStats();
            viewDiceGameList.add(SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getLastDiceGameRound());
            homeTable.sort();
        } else {
            //TODO: throw a error message to client
            throw new RuntimeException();
        }
    }

    @FXML
    private void setAutoRoll(MouseEvent event) {
        try {
            for (int i = 0; i < SmartDiceGame.getInstance().getRollLoop(); i++) {
                setRollDice(event);
            }
        } catch (RuntimeException e) {
            //TODO: throw a error message to client
        }

    }

    @FXML
    private void setSetupGame(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL location = getClass().getResource("/smartdice/fxml/HomeSettingsFXML.fxml");
            loader.setLocation(location);
            StackPane stackPane = loader.load(location.openStream());
            HomeSettingsController controller = loader.getController();
            /*
            TODO: (Suggestion) Consider making the homeSettings disabled and
             invisible so the code below can merge with setListeners method.
             */
            controller.getHomeSettingsSave().setOnMouseClicked(l -> {
                double pixelMultiplier = homeScrollBar.getMaxWidth() / 100;
                double homeScrollProgressBarWidth = SmartDiceGame.getInstance().getActiveDiceGame().getRollOverNo();
                homeScrollProgressBar.setWidth(homeScrollProgressBarWidth * pixelMultiplier);
                homeBetAmount.setText(String.format("%.8f",
                        SmartDiceGame.getInstance().getActiveDiceGame().getBetAmount()));
            });
            anchorPaneHomeParent.getChildren().add(stackPane);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    @FXML
    private void toggleShowMore(MouseEvent event) {

    }

    private void setListeners() {
        homeCurrRoll.textProperty().addListener(l -> {
            double pixelMultiplier = homeScrollBar.getMaxWidth() / 100;
            double rollRecTranslateXPos;
            rollRecTranslateXPos = Double.parseDouble(homeCurrRoll.textProperty().getValue());
            rollRecTranslateXPos = rollRecTranslateXPos * pixelMultiplier - homeScrollBar.getMaxWidth() / 2;
            homeScrollRectangle.translateXProperty().set(rollRecTranslateXPos);
        });

        homeShowMore.setOnMousePressed(l -> {
            if (homeTable.getLayoutY() == 0) {
                homeTable.setLayoutY(-265);
                homeTable.setMinHeight(485);
                homeShowMore.setText("Show Less");
            } else {
                homeTable.setLayoutY(0);
                homeTable.setMinHeight(220);
                homeShowMore.setText("Show More");
            }
        });
    }

    public void updateStats() {
        homeBalance.setText(String.format("%.8f", SmartDiceGame.getInstance().getCurrentPlayerProfile().getBalance()));
        homeBetAmount.setText(String.format("%.8f", SmartDiceGame.getInstance().getActiveDiceGame().getBetAmount()));
        homeProfit.setText(String.format("%.8f", SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getProfit()));

        homeStatPercent.setText(String.format("%.2f",
                SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getWLPercent()));
        homeStatWins.setText(Integer.toString(
                SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getWins()));
        homeStatLosses.setText(Integer.toString(
                SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getLosses()));
        homeCurrRoll.setText(String.format(
                "%.2f", SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getLastDiceGameRoundRoll()));
        homeStatRollNo.setText(Integer.toString(
                SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getTotalRolls()));
        homeStatCurrStreak.setText(Integer.toString(
                SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getStreak()));
        homeStatHighStreak.setText(Integer.toString(
                SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getHighStreak()));
        homeScrollText.setText(String.format(
                "%.2f", SmartDiceGame.getInstance().getCurrentPlayerProfile().getPlayerStat().getLastDiceGameRoundRoll()));
    }

}
