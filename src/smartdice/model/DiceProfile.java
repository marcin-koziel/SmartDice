package smartdice.model;

import java.util.ArrayList;

/**
 *
 * @author Marcin Koziel
 */
public class DiceProfile {

	private ArrayList<DiceProfile> smartDicePlayers = new ArrayList<>();
	private int wins;
	private int losses;
	private int streak;
	private int highestStreak;
	private double betAmount;
	private double profitAmount;

	private void generateRoll() {
		// TODO - implement DiceProfile.generateRoll
		throw new UnsupportedOperationException();
	}

	private int getWins() {
		return this.wins;
	}

	private int getLosses() {
		return this.losses;
	}

	private int getStreak() {
		return this.streak;
	}

	private int getHighestStreak() {
		return this.highestStreak;
	}

	private double getBetAmount() {
		return this.betAmount;
	}

	private double getProfitAmount() {
		return this.profitAmount;
	}

}