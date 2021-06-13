/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartdice.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;

import javafx.scene.layout.StackPane;
import smartdice.model.PlayerProfile;
import smartdice.model.PlayerStat;
import smartdice.model.SmartDiceGame;

import javax.sound.sampled.Line;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Marcin Koziel
 */
public class DashboardController extends ClassController<DashboardController> {

    @FXML
    private StackPane chartPane;
    private LineChart<Number, Number> scatterChart;  // LineChart - Roll No.
    private XYChart.Series series;  // Roll No.

    PlayerProfile currentPlayerProfile;

    // TODO: Encapsulation
    private static DashboardController dashboardController;

    public DashboardController(){
        super();
    }

    public DashboardController(String path) {
        super(path);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        scatterChart = createChart();
        chartPane.getChildren().add(scatterChart);
        dashboardController = this; // TODO: Encapsulation
    }

    // TODO: Encapsulation
    public static DashboardController getInstance() {return dashboardController;}

    private LineChart<Number, Number> createChart() {

        currentPlayerProfile = SmartDiceGame.getInstance().getCurrentPlayerProfile();

        final NumberAxis xAxis = new NumberAxis(0, 100, 10);
        xAxis.setForceZeroInRange(false);
        xAxis.autoRangingProperty().set(true);
        final NumberAxis yAxis = new NumberAxis(0, 100,10);
        final LineChart<Number,Number> sc = new LineChart<>(xAxis,yAxis);

        // setup chart
        sc.setTitle("Line Chart");
        xAxis.setLabel("Dice Rounds");
        xAxis.setAnimated(false);
        yAxis.setLabel("Dice Range");
        yAxis.setAutoRanging(false);

        // add starting data
        series = new ScatterChart.Series<Number,Number>();
        series.setName("Roll Number");

        for (int i = 0; i < currentPlayerProfile.getPlayerStat().getDiceGameList().size(); i++) {
            series.getData().add(
                    new ScatterChart.Data<Number, Number>(
                            i+1,
                            currentPlayerProfile.getPlayerStat().getDiceGameList().get(i).getDiceRoll()
                    )
            );
        }

        sc.getData().add(series);
        return sc;
    }

    public void updateScatterChart() {

        currentPlayerProfile = SmartDiceGame.getInstance().getCurrentPlayerProfile();

        scatterChart.getData().get(0).getData().add(
                new ScatterChart.Data<>(
                        currentPlayerProfile.getPlayerStat().getDiceGameList().size(),
                        currentPlayerProfile.getPlayerStat().getLastDiceGameRoundRoll()
                )
        );

    }





    
}
