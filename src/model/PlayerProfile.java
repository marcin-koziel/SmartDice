package model;

/**
 *
 * @author Marcin Koziel
 */
public class PlayerProfile {

    // private int id;
    private String name;
    private String username;
    private String password;
    private String email;
    private double balance;
    private PlayerStat playerStat;

    public PlayerProfile() {
        name = null;
        username = null;
        password = null;
        email = null;
        balance = 0;
        playerStat = new PlayerStat();
    }

    public PlayerProfile(String username) {
        name = "unknown";
        this.username = username;
        password = "1234";
        email = "unknown";
        balance = 1;
        playerStat = new PlayerStat();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void updateBalance() {
        balance += playerStat.getLastDiceGameRound().getProfit();
    }

    public PlayerStat getPlayerStat() {
        return playerStat;
    }

    @Override
    public String toString() {
        return String.format("Name: %s\nUsername: %s\nPassword: %s\nEmail: %s\nBalance: %s",
                getName(), getUsername(), getPassword(), getEmail(), getBalance());
    }
}