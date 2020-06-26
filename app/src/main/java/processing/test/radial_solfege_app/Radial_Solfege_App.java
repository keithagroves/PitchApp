package processing.test.radial_solfege_app;

import java.util.Arrays;
import java.util.HashMap;

import processing.core.PApplet;

public class Radial_Solfege_App extends PApplet {
	private NoteAnalyzer noteAnalyzer;
	private ScoreBoard sb = new ScoreBoard();
	private int state = 0;
	// Activity act;
	private MusicPlayer player;
	private float move = 0;
	// increase speed as they hold the note?
	private float defSpeed = PI / 30;
	private float speed = PI / 30;
	private float start = 0;
	private float end = 0;
	private float tempWidth = 0;
	private float tempHeight = 0;
	private float aspectRatio = calculateAspectRatio();
	private float idealW = 426;
	private float idealH = 900;
	private float scalars;
	private Demo demo;
	private int tail = 0;
	private float z = 15; // 21 // 4.342 // start low and increase. depending on results
	private float influence = 0; // 0.3
	private int countAns = 0;
	private String prevAns = "Do";
	private String finalAns = "Do";
	private float ballX = 0;
	private float ballY = 0;
	private int sus = 31;
	private boolean complete = true;
	private float newX = 0;
	private float newY = 0;
	private Float[] notes;
	private float radius = 0;
	private float ballSpeedX = 1;
	private float ballSpeedY = 1;
	private float ballSpeed = 2f;
	private long lastHit = 0;

	HashMap<Float, String> map = new HashMap<Float, String>();

	public static void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc",
				"processing.test.radial_solfege_app.Radial_Solfege_App" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}

	@Override
	public void settings() {
		size(426, 900);
		loadInfo();
		// size(900, 500);
		fullScreen();
	}
	@Override
	public void setup() {
		background(255);
		androidSetup();
		tempWidth = idealW;
		tempHeight = idealH;
		tempHeight = Math.min(width / aspectRatio, height);
		tempWidth = tempHeight * aspectRatio;
		scalars = tempWidth / idealW;
		idealW = tempWidth;
		idealH = tempHeight;
		this.translate((width - idealW) / 2, 0);
		player = new MusicPlayer(this);
		
		noteAnalyzer = new NoteAnalyzer(this, notes);

		
		//frameRate(60);
		ballX = idealW / 2;
		ballY = height / 2 + height / 10;
		radius = (idealW - idealW / 4) / 2;

		setupBall();
		strokeCap(SQUARE);
		demo = new Demo(map);
		scale(scalars);
	}



	@Override
	public void draw() {
		player.playSong(frameCount, notes, NoteAnalyzer.SOLFEGE, finalAns);
		if (state == 0) {
			this.translate((width - idealW) / 2, 0);
			getNote();
			if (!mousePressed) {
				background(0xff1d1e30);
				drawBackground();
				drawPaddle(finalAns);
				drawBall();
			}
			if (mousePressed) {
				player.play();
				int i = 0;
				while (!map.get(notes[i]).equals("C4")) {
					i++;
					// println(map.get(notes[i]));
				}
				player.freq(notes[i]);
			} else {
				//player.stop();
				//if (sus > 30) {
				//	player.stop();
				//} else if (sus <= 30) {
				//	// sus++;
				//}
			}

		} else {
			background(200);
			getNote();
			demo.practice(this, notes);
			demo.colorBars(this, noteAnalyzer);

		}
		textSize(100);
		text(finalAns, 100, 100);
		textSize(10);
	}

	private float calculateAspectRatio() {
		return 426f / 900; // w/h * h = w w/h * w = h w/h*w = 1/h
	}

	void androidSetup() {
		// orientation(PORTRAIT);
		// AndroidPermissionHelper perm = new AndroidPermissionHelper();
		// perm.androidPermissions(this);
	}

	public void saveData(byte[] writeData, String fileName) {
		if (writeData != null) {
			saveBytes(fileName, writeData);
		}
	}

	public float[] loadData(String fileName) {
		byte[] data = loadBytes(fileName);
		return ConversionUtils.convertByteArraytoFloatArray(data);
	}

	public void setupBall() {
		int randomSolfege = 0;
		setDirection(randomSolfege);
	}

	public void drawPaddle(String ans) {
		noFill();
		int i = findIndex(NoteAnalyzer.SOLFEGE, ans);
		float increments = (TWO_PI) / NoteAnalyzer.SOLFEGE.length;

		strokeWeight(15);
		stroke(0, 255, 100, 100);

		move += speed;
		if (move > TWO_PI)
			move = 0;
		if (move < 0)
			move = TWO_PI;

		float point1 = move;
		float point2 = increments * i;
		if (Math.abs(point2 - point1) >= defSpeed) {
			if (point2 > point1 && Math.abs(point2 - point1) <= PI) {
				speed = defSpeed;
			} else if (point2 > point1 && Math.abs(point2 - point1) > PI) {
				speed = -defSpeed;
			} else if (point1 > point2 && Math.abs(point2 - point1) <= PI) {
				speed = -defSpeed;
			} else if (point1 > point2 && Math.abs(point2 - point1) > PI) {
				speed = defSpeed;
			} else {
				speed = 0;
			}
		} else {
			speed = 0;
		}
		float offset = (HALF_PI - increments / 2) + PI;
		strokeWeight(5 * scalars);
		stroke(255);
		start = move + offset + PI / 40;
		end = increments + move + offset - PI / 40;
		arc(idealW / 2, idealH / 2 + idealH / 10, 2 * (idealW / 3), 2 * (idealW / 3), start, end);
	}

	public static int findIndex(String arr[], String t) {
		if (arr == null) {
			return -1;
		}
		int len = arr.length;
		int i = 0;
		while (i < len) {
			if (arr[i].equals(t)) {
				return i;
			} else {
				i = i + 1;
			}
		}
		return -1;
	}

	public void loadInfo() {
		String[] lines = loadStrings("notes.txt");
		notes = new Float[lines.length];
		int i = 0;
		for (String line : lines) {
			line = line.replaceAll("\\s", " ");
			String[] noteInfo = line.trim().split(" ");
			map.put(Float.parseFloat(noteInfo[1]), noteInfo[0]);
			notes[i] = Float.parseFloat(noteInfo[1]);
			i++;
		}
	}

	public  void getNote() {
		
		if (complete) {
			thread("getAnswer");
		}
	}

	public boolean intersects() {
		float centerX = cos((start + end) / 2) * radius + idealW / 2;
		float centerY = sin((start + end) / 2) * radius + idealH / 2 + idealH / 10;
		// fill(255, 100);
		// ellipse(sin(start)*(2*idealW/3),
		// cos(start)*2*(idealW/3),sin(end)*(2*idealW/3), cos(end)*2*(idealW/3))
		// The ball is close to the paddle? (the distance from the center of the paddle
		// is less than the length of the paddle.)
		if (millis() - lastHit > 1000)
			lastHit = millis();
		else
			return false;
		return dist(centerX, centerY, ballX, ballY) < dist(cos(start) * (idealW / 3) + idealW / 2,
				sin(start) * (idealW / 3) + idealH / 2 + idealH / 10, cos(end) * ((idealW / 3)) + idealW / 2,
				sin(end) * (idealW / 3) + idealH / 2 + idealH / 10);
	}

	public void drawBall() {
		noStroke();
		fill(255, 100);

		if (dist(idealW / 2, idealH / 2 + idealH / 10, ballX + ballSpeedX,
				ballY + ballSpeedY) > ((idealW - idealW / 3)) / 2 - idealW / 50 && intersects()) {
			int randomSolfege = 0;
			tail = 0;

			do {
				randomSolfege = (NoteAnalyzer.lastChoice + (int) random(2, NoteAnalyzer.SOLFEGE.length - 1))
						% NoteAnalyzer.SOLFEGE.length;
			} while (randomSolfege == NoteAnalyzer.lastChoice);
			sb.givePoint();
			// sine.play();
			// println("solfege"+randomSolfege);
			// sine.freq(notes[28+lastChoice]);
			NoteAnalyzer.lastChoice = randomSolfege;
			// find x and y
			setDirection(randomSolfege);

			sus = 0;
		} else if (dist(idealW / 2, idealH / 2 + idealH / 10, ballX + ballSpeedX, ballY + ballSpeedY) > radius) {
			ballX = idealW / 2;
			ballY = idealH / 2 + idealH / 10;

			setDirection(0);
			sb.reset();

			// sine.play();
			// sine.freq(notes[35]);
			sus = 0;
		} else {
			ballX += ballSpeedX;
			ballY += ballSpeedY;
		}
		if (frameCount % 3 == 0 && tail < 20) {
			tail++;
		}
		for (int i = tail; i >= 0; i--) {
			if (i > 10) {
				fill(255, (i - (i % 10)) * 8);
			} else {
				fill(255, i * 8);
			}
			ellipse(ballX - (ballSpeedX * i) * 2, ballY - (ballSpeedY * i) * 2, 20 - i, 20 - i);
		}
		fill(255);
		ellipse(ballX, ballY, 15, 15);
	}

	public void setDirection(int solfegeNote) {
		float increments = (TWO_PI) / NoteAnalyzer.SOLFEGE.length;
		float newAngle = -increments * solfegeNote + PI;
		newX = sin(newAngle) * radius * 0.9f + idealW / 2;
		newY = cos(newAngle) * radius * 0.9f + idealH / 2 + idealH / 10;
		float angle = atan2(newY - ballY, newX - ballX);
		ballSpeedX = cos(angle) * ballSpeed;
		ballSpeedY = sin(angle) * ballSpeed;
	}

	public synchronized void getAnswer() {
		complete = false;
		noteAnalyzer.analyze();
		String ans = noteAnalyzer.peakDetection(z, influence);
		bufferAnswer(ans);
		complete = true;
	}

	public void drawBackground() {
		float rad = idealW - idealW / 6;
		strokeWeight(10);
		strokeCap(ROUND);

		stroke(0xff21f7ff);
		line(15, 15, 15, 36);
		line(30, 15, 30, 36);

		strokeCap(SQUARE);
		strokeWeight(5);
		textSize(idealW / 20);
		fill(0xff21f7ff);
		text("SCORE", idealW / 2 - idealW / 22, idealH / 34);
		text("BEST", idealW - idealW / 9, idealH / 34);
		textSize(idealW / 20);
		text(sb.getScore(), idealW / 2 - idealW / 40, idealH / 18);
		text(sb.getBestScore(), idealW - idealW / 9, idealH / 18);
		for (int i = 0; i < NoteAnalyzer.SOLFEGE.length; i++) {
			if (Arrays.asList(NoteAnalyzer.SOLFEGE_MAJOR).contains(NoteAnalyzer.SOLFEGE[i])) {
				fill(0xff21f7ff);
				textSize(idealW / 17);
				if (finalAns.equals(NoteAnalyzer.SOLFEGE[i])) {
					fill(0xffffb72d);
				}
				text(NoteAnalyzer.SOLFEGE[i],
						cos((((TWO_PI) / NoteAnalyzer.SOLFEGE.length) * i) + HALF_PI + PI) * rad / 2 * 1.02f
								+ idealW / 2 - idealW / 35,
						sin(((TWO_PI) / NoteAnalyzer.SOLFEGE.length) * i + HALF_PI + PI) * rad / 2 * 1.01f + idealH / 2
								+ idealH / 9);
			}
		}
		noFill();
		ellipse(idealW / 2, idealH / 2 + idealH / 10, idealW - idealW / 4, idealW - idealW / 4);
	}

	public void bufferAnswer(String ans) {
		if (ans.equals(prevAns) && !ans.equals("")) {
			countAns++;
		} else {
			countAns = 0;
			prevAns = ans;
		}
		if (countAns >= 2) {
			finalAns = ans;
		}
	}

}
