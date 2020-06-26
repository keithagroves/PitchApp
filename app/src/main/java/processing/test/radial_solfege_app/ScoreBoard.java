package processing.test.radial_solfege_app;

public class ScoreBoard {
	private int score = 10;
	private int bestScore = 0;

	public void givePoint() {
		score+=10;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getBestScore() {
		return bestScore;
	}
	
	public void reset() {
		if (score > bestScore) {
			bestScore = score;
		}
		score = 10;
	}
}
