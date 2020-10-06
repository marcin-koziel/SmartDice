package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiceGame {

    private Dice dice;
    private String id;
    private double diceRoll;
    private double betAmount;
    private double multiplier;
    private Date timeStamp;
    private double payout;
    private double profit;
    private double winChance;
    private double minRoll;
    private double maxRoll;
    private double rollOverNo;
    private boolean rollOverBool;
    private boolean rollWin;

    public DiceGame() {
        betAmount = 0.00000000;
        rollOverNo = 50;
        rollOverBool = false;
        maxRoll = 100;
    }
    
    public DiceGame(double betAmount, double rollOverNo, boolean rollOverBool, double maxRoll) {
        dice = new Dice();
        diceRoll = setDiceRoll(dice, maxRoll);
        this.betAmount = betAmount;
        this.rollOverNo = rollOverNo;
        this.rollOverBool = rollOverBool;
        rollWin = setRollWin(diceRoll, rollOverNo, rollOverBool);
        multiplier = setMultiplier(maxRoll, rollOverNo, rollOverBool);
        minRoll = 0.00;
        this.maxRoll = maxRoll;
        timeStamp = setTimeStamp();
        payout = setPayout(betAmount, multiplier);
        profit = setProfit(betAmount, payout);
        winChance = setWinChance(maxRoll, multiplier);
    }

    // Intended to return cloned object for reference
    public DiceGame getDiceGameSettings(){
        DiceGame diceGameClone = new DiceGame();
        diceGameClone.rollWin = true; // ex
        diceGameClone.betAmount = this.betAmount;
        diceGameClone.rollOverNo = this.rollOverNo;
        diceGameClone.rollOverBool = this.rollOverBool;
        diceGameClone.maxRoll = this.maxRoll;
        return diceGameClone;
    }

    // TODO: Look into validating methods with args within
    public void generateSettings(){
        //TODO: Work around calling Thread.sleep() and having timeStamp unique
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }
        dice = new Dice();
        timeStamp = setTimeStamp();
        id = setId();
        multiplier = setMultiplier(maxRoll, rollOverNo, rollOverBool);
        payout = setPayout(betAmount, multiplier);
        winChance = setWinChance(maxRoll, multiplier);
        diceRoll = setDiceRoll(dice, maxRoll);
        this.rollWin = true;
        profit = setProfit(betAmount, payout);
    }
    
    // TODO: Look into validating methods with args within
    public void generateResult(){
        //TODO: Work around calling Thread.sleep() and having timeStamp unique
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }
        dice = new Dice();
        timeStamp = setTimeStamp();
        id = setId();
        multiplier = setMultiplier(maxRoll, rollOverNo, rollOverBool);
        payout = setPayout(betAmount, multiplier);
        winChance = setWinChance(maxRoll, multiplier);
        diceRoll = setDiceRoll(dice, maxRoll);
        rollWin = setRollWin(diceRoll, rollOverNo, rollOverBool);
        profit = setProfit(betAmount, payout);
    }
    
    private String setId(){
        return Long.toString(Long.parseLong(Long.toString(timeStamp.getTime()), 10), 16);
    }

    public String getId() {
        return id;
    }
    
    public void setBetAmount(double betAmount) {
        this.betAmount = betAmount;
    }

    public double getBetAmount() {
        return betAmount;
    }

    private double setDiceRoll(Dice dice, double maxRoll) {
        return Math.floor(dice.getDiceRoll() * (maxRoll*maxRoll)) / maxRoll;
        
    }

    public double getDiceRoll() {
        return diceRoll;
    }

    private boolean setRollWin(double diceRoll, double rollOverNo, boolean rollOverBool) {
        if ((rollOverNo < diceRoll) && rollOverBool) {
            return true;
        } else if ((rollOverNo > diceRoll) && !rollOverBool) {
            return true;
        }
        return false;
    }

    public boolean isRollWin() {
        return rollWin;
    }
    
    public String getRollWin() {
        if(rollWin){
            return "Win";
        } else {
            return "Lose";
        }
    }

    private double setMultiplier(double maxRoll, double rollOverNo, boolean rollOverBool) {
        if (rollOverBool) {
            return maxRoll / (maxRoll - rollOverNo);
        }
        return maxRoll / rollOverNo;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getMultiplierStrFmt() {
        return String.format("%.4f", multiplier);
    }

    private double setPayout(double betAmount, double multiplier) {
        return betAmount * multiplier;
    }

    public double getPayout() {
        return payout;
    }

    private double setProfit(double betAmount, double payout) {
        double profitTemp;
        if(isRollWin()){
            profitTemp = payout - betAmount;
        } else {
            profitTemp = payout - betAmount;
            profitTemp -= profitTemp *2;
        }
        return profitTemp;
    }

    public double getProfit() {
        return profit;
    }
    
    public String getProfitStrFmt() {
        return String.format("%.8f", profit);
    }

    private double setWinChance(double maxRoll, double multiplier) {
        return (double) Math.round((maxRoll - maxRoll / multiplier) * 100) / 100;
    }

    public double getWinChance() {
        return winChance;
    }

    private Date setTimeStamp() {
        Date date = new Date();
        date.setTime(date.getTime());
        return date;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }
    
    public String getDateFmt(){
        return String.format("%s", timeStampToDateFmt(timeStamp));
    }
    
    private String timeStampToDateFmt(Date timeStamp){
        DateFormat dateFmt = new SimpleDateFormat("hh:mm:ss");
        return dateFmt.format(timeStamp);
    }
    
    /*
    Setting minimum not a feature yet / not implemented
    public void setMinRoll(double minRoll){
        this.minRoll = minRoll;
    }
    */
    
    public double getMinRoll() {
        return minRoll;
    }

    public void setMaxRoll(double maxRoll) {
        this.maxRoll = maxRoll;
    }

    public double getMaxRoll() {
        return maxRoll;
    }
    
    public void setRollOverNo(double rollOverNo) {
        this.rollOverNo = (double) Math.round(rollOverNo * 100) / 100;
    }

    public double getRollOverNo() {
        return rollOverNo;
    }
    
    public void setRollOverBool(boolean rollOverBool) {
        this.rollOverBool = rollOverBool;
    }

    public boolean isRollOverBool() {
        return rollOverBool;
    }
    
    public void toggleRollOverBool() {
        this.rollOverBool = !rollOverBool;
    }

    @Override
    public String toString() {
        return String.format("Dice Roll: %s%n"
                + "Bet Amount: %s%n"
                + "Roll Over Number: %s%n"
                + "Roll Over Boolean: %s%n"
                + "Roll Win: %s%n"
                + "Multiplier: %s%n"
                + "Minimum Roll: %s%n"
                + "Maximum Roll: %s%n"
                + "Time Stamp: %s%n"
                + "Payout: %s%n"
                + "Profit: %s%n"
                + "Win Chance: %s%n", diceRoll, getBetAmount(), getRollOverNo(),
                isRollOverBool(), isRollWin(), getMultiplier(), getMinRoll(), getMaxRoll(),
                getTimeStamp(), getPayout(), getProfit(), getWinChance());
    }

}
