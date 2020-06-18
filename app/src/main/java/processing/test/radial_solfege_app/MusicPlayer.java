package processing.test.radial_solfege_app;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.sound.SinOsc;

public class MusicPlayer {
	SinOsc sine;
	PApplet app;
	int soundIncrement = 0;
	
	public MusicPlayer(PApplet app) {
		this.app = app;
		sine = new SinOsc(app);
		 
	}
	
	int lastNote = 0;
	long timeSince = 0;
	String lastNoteDetected = "Do";
	ArrayList<Long> list = new ArrayList<Long>();
	
	public void playSong(int frameCount, float[]notes, String [] solfege, String finalAns) {

		sine.play();
		if (frameCount % 60 == 0) {
			soundIncrement++;
		}
		int noteIndex = (soundIncrement) % (notes.length - 35) + 16;
		int solfegePos = noteIndex % solfege.length;
		String noteName = solfege[solfegePos];
		float note = notes[noteIndex];
		if (solfegePos != lastNote) {
			lastNote = solfegePos;
			timeSince = app.millis();
		}
		if (noteName.equals(finalAns) && !lastNoteDetected.equals(finalAns)) {
			list.add(app.millis() - timeSince);
			PApplet.println("Detect delay: " + (app.millis() - timeSince));
			lastNoteDetected = finalAns;
			long sum = 0;
			for (long l : list) {
				sum += l;
			}
			PApplet.println("delay AVG: " + sum / list.size());
		}
		sine.freq(note);
	}
	
	
    void play() {
		sine.play();
	}
    
    void freq(float frequency) {
    	sine.freq(frequency);
    }
	
    void stop() {
		sine.stop();
	}
	
}
