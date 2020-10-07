package model;

/**
 *
 * @author Marcin Koziel
 */
public class SmartDiceGame {


    private ProfileContainer profileContainer;
    private PlayerProfile currentPlayerProfile;
    private DiceGame currentDiceGame;
    private int rollLoop;

    private static SmartDiceGame instance = new SmartDiceGame();

    public SmartDiceGame() {
        profileContainer = new ProfileContainer();
        currentDiceGame = new DiceGame();
        rollLoop = 10;
        currentPlayerProfile = new PlayerProfile();
        // --- DEBUGGING ---
//        profileContainer.createPlayerProfile("Marcin");
//        currentPlayerProfile = profileContainer.getPlayerProfile("Marcin");
    }
    
    public static SmartDiceGame getInstance(){
        return instance;
    }

    public void setRollLoop(int rollLoop){
        this.rollLoop = rollLoop;
    }

    public int getRollLoop(){
        return rollLoop;
    }

    public void setCurrentPlayerProfile(PlayerProfile playerProfile){
        currentPlayerProfile = playerProfile;
    }

    public PlayerProfile getCurrentPlayerProfile(){
        return currentPlayerProfile;
    }
    
    public DiceGame getActiveDiceGame(){
        return currentDiceGame;
    }

    public ProfileContainer getProfileContainer(){
        return profileContainer;
    }

    public void playRound() {
        DiceGame diceGame = currentDiceGame.getDiceGameSettings();
        diceGame.generateResult();
        currentPlayerProfile.getPlayerStat().addDiceGame(diceGame);
        updateSmartDiceGame();
    }

    public void updateSmartDiceGame() {
        currentPlayerProfile.updateBalance();
    }



//    public DiceGame getLatestDiceGame() {
//        return diceGameList.get(diceGameList.size() - 1);
//    }
    
}
