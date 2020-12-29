package smartdice.model;

import java.util.ArrayList;

/**
 *
 * @author Marcin Koziel
 */
public class PlayerStat {
    
    private int wins;
    private int losses;
    private int streak;
    private int highStreak;
    private double profit;
    
    private ArrayList<DiceGame> diceGameList;
    
    public PlayerStat(){
        wins = 0;
        losses = 0;
        streak = 0;
        highStreak = 0;
        profit = 0;
        diceGameList = new ArrayList<>();
    }

    private void addWin(){
        wins += 1;
    }
    
    private void addLosses(){
        losses += 1;
    }
    
    private void addStreak(){
        streak += 1;
    }
    
    private void resetStreak(){
        streak = 0;
    }
    
    private void addHighStreak(){
        highStreak += 1;
    }
    
    public int getWins(){
        return wins;
    }
    
    public int getLosses(){
        return losses;
    }
    
    public int getStreak(){
        return streak;
    }
    
    public int getHighStreak(){
        return highStreak;
    }
    
    public double getProfit(){
        return profit;
    }

    private void setProfit(double profit) {
        this.profit += profit;
    }
    
    private void updateStat(DiceGame diceGame){
        if(diceGame.isRollWin()){
            addWin();
            addStreak();

            if(getHighStreak() < getStreak()){
                addHighStreak();
            }
        } else {
            addLosses();
            resetStreak();
        }
        setProfit(diceGame.getProfit());
    }
    
    public void addDiceGame(DiceGame diceGame){
        updateStat(diceGame);
        diceGameList.add(diceGame);
    }
    
    public ArrayList<DiceGame> getDiceGameList(){
        return diceGameList;
    }
    
    public double getLastDiceGameRoundRoll(){
        if(!diceGameList.isEmpty()){
            return diceGameList.get(diceGameList.size()-1).getDiceRoll();
        }
        return 0;
    }
    
    public DiceGame getLastDiceGameRound(){
        if(!diceGameList.isEmpty()){
            return diceGameList.get(diceGameList.size()-1);
        } else {
            return new DiceGame();
        }
    }
    
    public double getWLPercent(){
        if((getWins()+getLosses()) > 0){
            return ((double)getWins() / ((double)getWins() + (double)getLosses())) * 100;
        }
        return 0;
    }
    
    public int getTotalRolls(){
        return wins + losses;
    }
    
}
