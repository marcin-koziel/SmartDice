package smartdice.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import smartdice.model.DiceGame;
import smartdice.model.ProfileContainer;
import smartdice.model.SmartDiceGame;

/**
 *
 * @author Marcin Koziel
 */
public class SettingsController implements Initializable {
    
    public TextField txtFieldFileChooser;
    public Button btnSelFileChooser;
    public Group btnSaveFile;

    private SmartDiceGame smartDiceGame;

    private static String exportPath;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        smartDiceGame = SmartDiceGame.getInstance();
        initExport();
    }

    public void initExport(){
        FileChooser fileChooser = new FileChooser();
        File file = new File("data.csv");

        fileChooser.setInitialDirectory(new  File("").getAbsoluteFile());
        fileChooser.setTitle("Export");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extensionFilter);

        txtFieldFileChooser.setText(file.getAbsolutePath().toString());

        btnSelFileChooser.setOnAction(l -> {
            File selectedFile = fileChooser.showSaveDialog(null);
            txtFieldFileChooser.setText(selectedFile.getAbsolutePath().toString());
        });
        btnSaveFile.setOnMouseClicked(l -> {
            File selectedFile = new File(txtFieldFileChooser.getText());
            exportFile(selectedFile);
        });
    }

    private void exportFile(File file){

        try {
            FileWriter fileWriter = new FileWriter(file);

            for (DiceGame diceGame : smartDiceGame.getCurrentPlayerProfile().getPlayerStat().getDiceGameList()){
                fileWriter.append(diceGame.getId()).append(",");
                fileWriter.append(diceGame.getTimeStamp().toString()).append(",");
                fileWriter.append(String.format("%.4f",diceGame.getMultiplier())).append(",");
                fileWriter.append(Double.toString(diceGame.getRollOverNo())).append(",");
                fileWriter.append(diceGame.getRollWin()).append(",");
                fileWriter.append(Double.toString(diceGame.getDiceRoll())).append(",");
                fileWriter.append(String.format("%.8f",diceGame.getProfit()));
                fileWriter.append("\n");
            }

            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }




    
}
