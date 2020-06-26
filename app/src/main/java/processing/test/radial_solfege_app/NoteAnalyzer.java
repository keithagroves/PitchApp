package processing.test.radial_solfege_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.sound.AudioIn;
import processing.sound.CustomFFT;

public class NoteAnalyzer {
	public static final int BANDS = 16384;
	public static final int SPECTRUM_LENGTH = 400;
	public static final int MIN_BAND = 90;
	public static final float SCALE = 1.3455657492f;
	float[] spectrum = new float[SPECTRUM_LENGTH];
	public static final String[] SOLFEGE = { "Do", "Di", "Re", "Ri", "Mi", "Fa", "Fi", "Sol", "Si", "La", "Li", "Ti" };
	public static final String[] SOLFEGE_MAJOR = { "Do", "Re",  "Mi", "Fa", "Sol", "La", "Ti" };
	HashMap<Float, String> map = new HashMap<Float, String>();
	AudioIn in;
	CustomFFT fft;
	static Float[] notes;

	static int lastChoice = 0;


	public NoteAnalyzer(PApplet app, Float[] notes) {
		fft = new CustomFFT(app, BANDS, SPECTRUM_LENGTH);
		in = new AudioIn(app, 0);
		// start the Audio Input
		in.start();
		// patch the AudioIn
		fft.input(in);
		this.notes = notes;

	}

	// Default for testing
	public NoteAnalyzer() {

	}

	public String analyze() {
		return fft.analyze(spectrum);
		
	}

	public static String peakDetection(float[] real, float[] imagined, float [] spectrum) {
		String highestFreq = "";
		String secondHighest = "";
		Float highest = 0.0f;
		HashMap<String, Float> noteFreq = new HashMap<String, Float>();
		for (int i = MIN_BAND; i < spectrum.length; i++) {
			spectrum[i] = 2 * (float) Math.sqrt((real[i] * real[i]) + (imagined[i] * imagined[i]));
			String thing = SOLFEGE[(findClosest(notes, i * SCALE)) % SOLFEGE.length];
			if ((noteFreq.get(thing) == null || noteFreq.get(thing) < spectrum[i]) && spectrum[i]> 0.0001) {
				noteFreq.put(thing, spectrum[i]);
				//get the highest amplitude
				if (spectrum[i] >= highest) {
					highest = spectrum[i];
					secondHighest = highestFreq;
					highestFreq = thing;
				}
			}
		}
		return overtoneFilter(noteFreq, highestFreq, secondHighest);
	}

	/**
	 * 
	 * @param noteFreq      A hashmap of solfege notes and the frequency of signals
	 * @param highest       The frequency with the strongest signal
	 * @param secondHighest The frequency with second strongest signal.
	 * @return
	 */
	public static String overtoneFilter(Map<String, Float> noteFreq, String highest, String secondHighest) {
		for (int i = 0; i < noteFreq.entrySet().size(); i++) {
			if (secondHighest.equals(SOLFEGE[i])
					&& highest.equals(SOLFEGE[(i + (SOLFEGE.length / 2 + 1)) % SOLFEGE.length])
					&& Math.pow(noteFreq.get(secondHighest), 1.9) >= noteFreq.get(highest)) {
				return secondHighest;
			}
		}
		return highest;
	}

	public static int findClosest(Float arr[], float target) {
		double calc = 57 + 12 * log2(target / 440.0);
		return Math.min(Math.max((int) Math.round(calc), 0), arr.length - 1);
	}

	public static final double log2(double f) {
		return (Math.log(f) / Math.log(2.0));
	}

}
