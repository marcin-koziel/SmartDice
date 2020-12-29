package smartdice.model;

/**
 *
 *
 * @author Marcin Koziel
 */
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
