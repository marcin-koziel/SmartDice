package model;

import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Dice {
    
    private double diceRoll;
    
    public Dice() {
        rollDice();
    }
    
    private void rollDice(){
        this.diceRoll = Math.random();
    }
    
    public double getDiceRoll(){
        return diceRoll;
    }
}
