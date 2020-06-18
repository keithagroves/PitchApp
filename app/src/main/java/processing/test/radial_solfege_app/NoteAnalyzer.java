package processing.test.radial_solfege_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import processing.core.PApplet;
import processing.sound.AudioIn;
import processing.sound.CustomFFT;

public class NoteAnalyzer {
	public static final int BANDS = 16384;
	public static final int SPECTRUM_LENGTH = 400;
    public static float z = 15;  //21 // 4.342  // start low and increase. depending on results
	AudioIn in;
    CustomFFT fft;
    float[] spectrum = new float[SPECTRUM_LENGTH];
    HashMap<Float, String> map = new HashMap<Float, String>();
    ArrayList<Float> spectrumData = new ArrayList<Float>();
    List<Float> filteredData;
    PeakDetector peakDetector = new PeakDetector(100, 5f, .35f);
    String[] solfege = {"Do", "Re", "Mi", "Fa", "Sol", "La", "Ti"};
    float averageZ = 0;
    float scale = 1.3455657492f;
    Float[] notes;

    static int lastChoice = 0;

    float averageCorrectZ = 0;
    
    public NoteAnalyzer(PApplet app, Float [] notes) {
    	 fft = new CustomFFT(app, BANDS);
         in = new AudioIn(app, 0);
         // start the Audio Input
         in.start();
         // patch the AudioIn
         fft.input(in);	
         this.notes = notes;
         
         }
    //Default for testing 
    public NoteAnalyzer() {
    	
    }
    
    void analyze() {
        fft.analyze(spectrum);
    }
    

    public String peakDetection(int lag, float threshold, float influence) {
    
        spectrumData.clear();
        for (int i = 0; i < spectrum.length; i++) {
            if (filteredData != null) {
                spectrumData.add(Math.max(spectrum[i], .0002f));
            } else
                spectrumData.add(spectrum[i]);
        }
        HashMap<String, List<? extends Number>> result = peakDetector.analyzeDataForSignals(spectrumData, lag, threshold, influence);
        HashMap<String, Float> noteFreq = new HashMap<String, Float>();
        
        //TODO: not all are needed
        List<Integer> signals = (List<Integer>) result.get("signals");
        List<Float> avg = (List<Float>) result.get("avgFilter");
        List<Float> std = (List<Float>) result.get("stdFilter");
        filteredData = (List<Float>) result.get("filteredData");

        float highestZ = 0;
        for (int i = 90; i < signals.size(); i++) {

            if (signals.get(i) == 1) {
            	System.out.println((findClosest(notes, i * scale)));
            	System.out.println(i*scale);
            	System.out.println((findClosest(notes, i * scale)));
                String thing = solfege[(findClosest(notes, i * scale)) % solfege.length];
                if(thing.equals("Sol"))
                	System.out.println("break");
                if (noteFreq.get(thing) == null) {
                    noteFreq.put(thing, spectrum[i] - avg.get(i));
                } else {
                    noteFreq.put(thing, noteFreq.get(thing) + (spectrum[i] - avg.get(i)));
                }
                float zscore = (spectrum[i] - avg.get(i - 1)) / std.get(i - 1);
                if (zscore > highestZ) {
                    highestZ = zscore;
                }
            }
        }
        if (averageZ == 0) {
            averageZ = highestZ;
        } else {
            averageZ += highestZ;
            averageZ = highestZ / 2;
        }

        String highestFreq = "";
        String secondHighest = "";
        Float highest = 0.0f;
        for (String tempNote : noteFreq.keySet()) {
            if (noteFreq.get(tempNote) >= highest) {
                highest = noteFreq.get(tempNote);
                secondHighest = highestFreq;
                highestFreq = tempNote;
            }
        }
        System.out.println(noteFreq);

        String filtered = overtoneFilter(noteFreq, highestFreq, secondHighest);
        
        //TODO remove this
        if (filtered.equals(solfege[lastChoice])) {
            if (averageCorrectZ > 0) {
                if (averageCorrectZ == 0) {
                    averageCorrectZ = averageZ;
                } else {
                    averageCorrectZ += averageZ;
                    averageCorrectZ = averageCorrectZ / 2;
                }
                z = averageCorrectZ * 0.9f;
                System.out.println("new z" + z);
            }
        }
        return filtered;
    }
    
    
    /**
     * 
     * @param noteFreq A hashmap of solfege notes and the frequency of signals
     * @param highest The frequency with the strongest signal
     * @param secondHighest The frequency with second strongest signal.
     * @return
     */
    public String overtoneFilter(HashMap<String, Float> noteFreq, String highest, String secondHighest) {
        for (int i = 0; i < noteFreq.entrySet().size(); i++) {
            if (secondHighest.equals(solfege[i]) && highest.equals(solfege[(i + (solfege.length / 2 + 1)) % solfege.length]) && noteFreq.get(highest) >= noteFreq.get(secondHighest) / 2) {
                return secondHighest;
            }
        }
        return highest;
    }
    public int findClosest(Float arr[], float target) {
    	double calc = 57 + 12 * log2(target/440.0);
    	return Math.min(Math.max((int)Math.round(calc),0), arr.length-1);
    }

    public static final double log2(double f)
    {
        return (Math.log(f)/Math.log(2.0));
    }
   


}
