package processing.test.radial_solfege_app;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Set; 
import processing.sound.*; 
import java.util.Arrays; 
import java.nio.ByteBuffer; 
import java.util.ArrayList; 
import java.util.Collections; 
import java.util.HashMap; 
import java.util.List; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException;
import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

public class Radial_Solfege_App extends PApplet {
  int maVersion = Build.VERSION.SDK_INT;

  Activity act;


public static int A440 = 440;
FFT fft;
AudioIn in;
int bands = 16384;
int spectrumLength = 800;
float[] spectrum = new float[spectrumLength];
Float [] notes; 
HashMap<Float, String> map = new HashMap<Float, String>();
String [] solfege = {"Do", "Re", "Mi", "Fa", "Sol", "La", "Ti"};
//String [] solfege = {"Do", "Di", "Re", "Ri", "Mi", "Fa", "Fi", "Sol", "Si", "La", "Li", "Ti"};

int amplify = 18;
SinOsc sine;
float x = 0;
float y = 0;

float unitX = 0;
float unitY = 0;

void androidPermissions(){
    //Android stuff
    act = this.getActivity();
 // PermissionRequestor re;
  if (maVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {//verifier API
                if (!permissionsDejaAccordees()) {//ne pas redemander si la permission a déjà été accordée
                    demandePermissionParticuliere();//sinon, demander....
                }

 
}
}


public void saveData(byte [] writeData, String fileName) {
  if (writeData != null) {
    //println("Saving " + writeData.length + " Bytes");
    saveBytes(fileName, writeData);
  }
}

public float [] loadData(String fileName) {
  byte[] data =loadBytes(fileName);
  return ConversionUtils.convertByteArraytoFloatArray(data);
}

public void settings() {
fullScreen(P2D);
}

public void setup() {
  orientation(PORTRAIT);
  androidPermissions();
  background(255);
  unitX = width/1000.0f;
  unitY = height/1000.0f;
  loadInfo();
  fft = new FFT(this, bands);
  in = new AudioIn(this, 0);
  // start the Audio Input
  in.start();
  // patch the AudioIn
  fft.input(in);
  sine = new SinOsc(this);
  //frameRate(120);
  ballX = width/2;
  ballY = height/2+height/10;
  radius = (width - width/4)/2;

  setupBall();
  strokeCap(SQUARE);
}

public void setupBall() {
  int randomSolfege = 0;
  setDirection(randomSolfege);
}



float move = 0;
//increase speed as they hold the note?
float defSpeed = PI/45;
float speed = PI/45;
float start = 0;
float end = 0;
public void drawPaddle(String ans) {
  noFill();
  int i = findIndex(solfege, ans);
  float increments =(TWO_PI)/solfege.length;

  strokeWeight(15);
  stroke(0, 255, 100, 100);

  move+=speed;
  if (move > TWO_PI)
    move = 0;
  if (move < 0)
    move = TWO_PI;



  float point1 = move;
  float point2 = increments*i;
  if (Math.abs(point2-point1)>= defSpeed) {
    if (point2 > point1 && Math.abs(point2-point1)<= PI) {
      speed=defSpeed;
    } else if (point2 > point1 && Math.abs(point2-point1)> PI) {
      speed = -defSpeed;
    } else if (point1 > point2 && Math.abs(point2-point1)<= PI) {
      speed = -defSpeed;
    } else if (point1 > point2 && Math.abs(point2-point1)> PI) {
      speed = defSpeed;
    } else {
      speed = 0;
    }
  } else {
    speed = 0;
  }
  float offset = (HALF_PI-increments/2)+PI;
  strokeWeight(unitX(10));
  stroke(255);
  start =  move+offset+PI/14;
  end = increments +move+offset-PI/14;
  arc(width/2, height/2+height/10, 2*(width/3), 2*(width/3), start, end);
}


public float unitX(float units){
   return unitX * units; 
}

public float unitY(float units){
   return unitX * units; 
}

public static int findIndex(String arr[], String t) 
{ 
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
  String[] lines = loadStrings("cScale.txt");
  //println("there are " + lines.length + " notes");
  notes = new Float[lines.length];
  int i = 0; 
  for (String line : lines) {
    line = line.replaceAll("\\s", " ");
    String []noteInfo = line.trim().split(" ");
    map.put(Float.parseFloat(noteInfo[1]), noteInfo[0]);
    notes[i] = Float.parseFloat(noteInfo[1]);
    i++;
  }
}

int soundIncrement = 0;

float scale = 1.3455657492f;
int record = 0;
ArrayList<Float> spectrumData = new ArrayList<Float>();
PeakDetector peakDetector = new PeakDetector();
int lag = 50; //53 //45 //29
float z = 15;  //21 // 4.342  // start low and increase. depending on results
float bestZ = 0;
int bestLag = 0;
float influence = 0; //0.3
float bestInfluence = 1;
int countAns=0;
String prevAns = "Do";
String finalAns = "Do";
float ballX = 0;
float ballY = 0;
int sus = 31;
boolean complete = true;
int workTimes = 0;
public void draw() {
  getNote();
  //playSong();

  if (!mousePressed) {
    background(0xff1d1e30);
    drawBackground();
    drawPaddle(finalAns);
    drawBall();
  }
  if (mousePressed) {
    sine.play();
    int i = 0;
    while (!map.get(notes[i]).equals("C4")) {
      i++;
      //println(map.get(notes[i]));
    }
    sine.freq(notes[i]);
  } else {
    sine.stop();
    if (sus > 30) {
      sine.stop();
    } else if (sus <= 30) {
      //sus++;
    }
  }
}


int workLag = 3;
public void getNote() {
  if (frameCount % workLag == 0) {
    if (complete) {
      thread("getAnswer");
      workTimes++;
      if (workTimes>100 && workLag > 1) {
        println("work lag -- " + workLag);
        workLag--;
        workTimes=0;
      }
    } else {
      println("work lag ++" + workLag);
      workLag++;
    }
  }
}
int radius = 0;
float ballSpeedX =1;
float ballSpeedY =1;
float slope = 0;
float ballSpeed = 1.2f;
int lastChoice = 0;

public boolean intersects() {
  float centerX =cos((start+end)/2)*radius+ width/2;
  float centerY= sin((start+end)/2)*radius + height/2+height/10;
  //fill(255, 100);
  //ellipse(sin(start)*(2*width/3), cos(start)*2*(width/3),sin(end)*(2*width/3), cos(end)*2*(width/3))
  //The ball is close to the paddle? (the distance from the center of the paddle is less than the length of the paddle.)
  return dist(centerX, centerY, ballX, ballY) < dist(cos(start)*(width/3)+width/2, sin(start)*(width/3)+height/2+height/10,cos(end)*((width/3))+width/2, sin(end)*(width/3)+height/2+height/10);
}

int tail = 0;
public void drawBall() {
  noStroke();
  fill(255, 100);
  //stroke(255,255,0);
  //line(cos(start)*(width/3)+width/2, sin(start)*(width/3)+height/2+height/10,cos(end)*((width/3))+width/2, sin(end)*(width/3)+height/2+height/10);
  if (  dist(width/2, height/2+height/10, ballX+ballSpeedX, ballY+ballSpeedY) > ((width - width/3))/2-width/50 && intersects()) {
    int randomSolfege = 0;
    tail = 0;

    do {
      randomSolfege =  (lastChoice + (int)random(2, solfege.length-1))%solfege.length; // (int)random(0, 7);
      // println(randomSolfege);
    } while (randomSolfege == lastChoice);
    score+=10;
    //sine.play();
    //println("solfege"+randomSolfege);
    //sine.freq(notes[28+lastChoice]);
    lastChoice = randomSolfege;
    //find x and y
    setDirection(randomSolfege);

    sus = 0;
  } else if (dist(width/2, height/2+height/10, ballX+ballSpeedX, ballY+ballSpeedY)> radius) {
    ballX = width/2;
    ballY = height/2+height/10;

    setDirection(0);
    if (score > bestScore) {
      bestScore = score;
    }
    score = 10;
    //sine.play();
    //sine.freq(notes[35]);
    sus = 0;
  } else {
    ballX+=ballSpeedX;
    ballY+=ballSpeedY;
  }
  if (frameCount %3 ==0 && tail < 20) {
    tail++;
  }
  for (int i = tail; i >= 0; i--) {
    if (i > 10) {
      fill(255, (i-(i%10))*8);
    } else {
      fill(255, i*8);
    }
    ellipse(ballX-(ballSpeedX*i)*2, ballY-(ballSpeedY*i)*2, 20-i, 20-i);
  }
  fill(255);
  ellipse(ballX, ballY, 15, 15);
}
float newX = 0;
float newY = 0;
public void setDirection(int solfegeNote) {
  float increments = (TWO_PI)/solfege.length;
  float newAngle = -increments * solfegeNote+PI;
  //find x and y
  newX = sin(newAngle)*radius*0.9f+width/2;
  newY = cos(newAngle)*radius*0.9f+ height/2+height/10;
  float angle = atan2(newY-ballY, newX-ballX);
  ballSpeedX = cos(angle) * ballSpeed;
  ballSpeedY = sin(angle) * ballSpeed;
}
int checkCount = 0;
int cycles = 5;

public void getAnswer() {
  complete = false;
  fft.analyze(spectrum);
  String ans =peakDetection(lag, z, influence);
  drawAnswer(ans);
  checkCount++;
  checkCount = 0;
  complete = true;
}
int score = 10;
int bestScore = 0;
public void drawBackground() {
  float rad = width-width/6;
  strokeWeight(10);
  strokeCap(ROUND);

  stroke(0xff21f7ff);
  line(15, 15, 15, 36);
  line(30, 15, 30, 36);

  strokeCap(SQUARE);
  strokeWeight(5);
  textSize(width/20);
  fill(0xff21f7ff);
  //score++;
  text("SCORE", width/2-width/22, height/34);
  text("BEST", width-width/9, height/34);
  textSize(width/20);
  text(score, width/2-width/40, height/18);
  text(bestScore, width-width/9, height/18);
  for (int i= 0; i < solfege.length; i++) {
    fill(0xff21f7ff);
    textSize(width/17);
    if (finalAns.equals(solfege[i])) {
      fill(0xffffb72d);
    }
    text(solfege[i], cos((((TWO_PI)/solfege.length)*i)+HALF_PI+PI)*rad/2*1.02f+width/2-width/35, sin(((TWO_PI)/solfege.length)*i + HALF_PI+PI)*rad/2*1.01f+height/2+height/9);
  }
  noFill();
  circle(width/2, height/2+height/10, width - width/4);
}
public void drawAnswer(String ans) {
  if (ans.equals(prevAns) && !ans.equals("")) {
    countAns++;
  } else {
    countAns = 0;
    prevAns = ans;
  }

  if (countAns>=3) {
    finalAns = ans;
  }
}


//TESTING GROUNDS
//Not Working? //Maybe because of the other Thread?
int lastNote = 0;
long timeSince = 0;
String lastNoteDetected = "Do";
ArrayList<Long> list = new ArrayList<Long>();

public void playSong() {

  sine.play();
  if (frameCount%60 == 0) {
    soundIncrement++;
  }
  int noteIndex = (soundIncrement)%(notes.length-35)+16;
  int solfegePos = noteIndex%solfege.length;
  String noteName = solfege[solfegePos];
  float note = notes[noteIndex];
  if (solfegePos != lastNote) {
    lastNote = solfegePos;
    timeSince = millis();
  }
  if (noteName.equals(finalAns) && !lastNoteDetected.equals(finalAns)) {
    list.add(millis()-timeSince);
    println("Detect delay: " + (millis()-timeSince));
    lastNoteDetected = finalAns;
    long sum = 0;
    for (long l : list) {
      sum+=l;
    }
    println("delay AVG: " + sum/list.size());
  }
  sine.freq(note);
}

float averageZ = 0;
float averageCorrectZ = 0;
List<Float> filteredData;

public String peakDetection(int lag, float threshold, float influence) {
  spectrumData.clear();
  for (int i = 0; i < spectrum.length; i++) {
    if (filteredData != null)
    {
      spectrumData.add(Math.max(spectrum[i], .00026f));
    } else
      spectrumData.add(spectrum[i]);
  }
  HashMap<String, List> result = peakDetector.analyzeDataForSignals(spectrumData, lag, threshold, influence);
  HashMap<String, Float> noteFreq = new HashMap<String, Float>();
  List<Integer> signals = result.get("signals");
  List<Float> avg = result.get("avgFilter");
  List<Float> std = result.get("stdFilter");
  filteredData = result.get("filteredData");

  float highestZ = 0;
  for (int i =0; i < signals.size(); i++) {

    if ( signals.get(i) == 1) {
      String thing = solfege[(findClosest(notes, i*scale)) % solfege.length];
      if (noteFreq.get(thing)== null) {
        noteFreq.put(thing, spectrum[i]-avg.get(i));
      } else {
        noteFreq.put(thing, noteFreq.get(thing)+(spectrum[i]-avg.get(i)));
      }
      float zscore = (spectrum[i] - avg.get(i - 1))/ std.get(i - 1);
      if (zscore>highestZ) {
        highestZ = zscore;
      }
    }
  }
  if (averageZ == 0) {
    averageZ = highestZ;
  } else {
    averageZ += highestZ;
    averageZ = highestZ/2;
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
  //println(noteFreq);

  String filtered = overtoneFilter(noteFreq, highestFreq, secondHighest);
  if (filtered.equals(solfege[lastChoice])) {
    if (averageCorrectZ > 0) {
      if (averageCorrectZ == 0) {
        averageCorrectZ = averageZ;
      } else {
        averageCorrectZ += averageZ;
        averageCorrectZ = averageCorrectZ/2;
      }
      z =averageCorrectZ * 0.9f;
      println("new z"+z);
    }
  }
  return filtered;
}


public String overtoneFilter(HashMap<String, Float> noteFreq, String highest, String secondHighest) {
  for (int i = 0; i < noteFreq.entrySet().size(); i++) {
    if (secondHighest.equals(solfege[i]) && highest.equals(solfege[(i+(solfege.length/2+1))%solfege.length]) && noteFreq.get(highest) >= noteFreq.get(secondHighest)/2) {
      return secondHighest;
    }
  }
  return highest;
}


// Returns element closest to target in arr[] 
public int findClosest(Float arr[], float target) 
{ 
  int n = arr.length; 
  if (target <= arr[0]) 
    return 0; 
  if (target >= arr[n - 1]) 
    return n - 1; 
  int i = 0, j = n, mid = 0; 
  while (i < j) { 
    mid = (i + j) / 2; 
    if (arr[mid] == target) 
      return mid; 
    if (target < arr[mid]) { 
      if (mid > 0 && target > arr[mid - 1])  
        return getClosest(mid - 1, mid, target, arr); 
      j = mid;
    } else { 
      if (mid < n-1 && target < arr[mid + 1])  
        return getClosest(mid, 
          mid + 1, target, arr);                 
      i = mid + 1; // update i
    }
  } 
  return mid;
} 

public int getClosest(int val1, int val2, float target, Float [] arr) 
{ 
  if (target - arr[val1] >= arr[val2] - target)  
    return val2;         
  else 
  return val1;
} 

//RESULTAT:
 
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    switch (requestCode) {
        case 101:
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               println("permissions accordées");////now you can open your microphone
 
            } else {
                println("permissions not granted");
            }
            break;
        default:
            if (maVersion > Build.VERSION_CODES.LOLLIPOP_MR1)
            act.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

private boolean permissionsDejaAccordees() {
    if (maVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
        int result = act.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    return true;
};
 
private void demandePermissionParticuliere()  {
    if (maVersion > Build.VERSION_CODES.LOLLIPOP_MR1) //verifier API

        act.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 101);//+WRITE_EXTERNAL_STORAGE????
};
static class ConversionUtils {
  public static byte [] convertFloatArrayToByteArray(float [] arr) {
    byte [] output = null;
    for (int i = 0; i< arr.length; i++) {
      byte[] bytes = toByteArray(arr[i]);
      output = (output == null)? bytes : concat(output, bytes);
    }
    return output;
  }

  public static byte[] toByteArray(float value) {
    byte[] bytes = new byte[4];
    ByteBuffer.wrap(bytes).putFloat(value);
    return bytes;
  }

  public static float toFloat(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getFloat();
  } 

  //change to byte arr to float arr.
  public static float [] convertByteArraytoFloatArray(byte [] loadData) {
    byte [] floatData = new byte[Float.BYTES];
    float[] result = new float[loadData.length/Float.BYTES];
    int floatStart = 0;
    int index =0;
    while (floatStart < loadData.length) {
      for (int i = 0; i < Float.BYTES; i++) {
        floatData[i] = loadData[floatStart+i];
      }
      result[index]=toFloat(floatData);
      index++;
      floatStart+=Float.BYTES;
    }
    return result;
  }
}






public class PeakDetector {

    public HashMap<String, List> analyzeDataForSignals(List<Float> data, int lag, Float threshold, Float influence) {
        
        // init stats instance
        Stats stats = new Stats();

        // the results (peaks, 1 or -1) of our algorithm
        List<Integer> signals = new ArrayList<Integer>(Collections.nCopies(data.size(), 0));

        // filter out the signals (peaks) from our original list (using influence arg)
        List<Float> filteredData = new ArrayList<Float>(data);

        // the current average of the rolling window
        List<Float> avgFilter = new ArrayList<Float>(Collections.nCopies(data.size(), 0.0f));

        // the current standard deviation of the rolling window
        List<Float> stdFilter = new ArrayList<Float>(Collections.nCopies(data.size(), 0.0f));

        // init avgFilter and stdFilter
        for (int i = 0; i < lag; i++) {
            stats.addValue(data.get(i));
        }
        avgFilter.set(lag - 1, stats.getMean());
        stdFilter.set(lag - 1, sqrt(stats.getPopulationVariance())); // getStandardDeviation() uses sample variance
        stats.clear();

        // loop input starting at end of rolling window
        for (int i = lag; i < data.size(); i++) {             
            // if the distance between the current value and average is enough standard deviations (threshold) away
            if (data.get(i) - avgFilter.get(i - 1) > threshold * stdFilter.get(i - 1)) {

                // this is a signal (i.e. peak), determine if it is a positive or negative signal
                if (data.get(i) > avgFilter.get(i - 1)) {
                    signals.set(i, 1);
                } 

                // filter this signal out using influence
                filteredData.set(i, (influence * data.get(i)) + ((1 - influence) * filteredData.get(i - 1)));
            } else {
                // ensure this signal remains a zero
                signals.set(i, 0);
                // ensure this value is not filtered
                filteredData.set(i, data.get(i));
            }

            // update rolling average and deviation
            for (int j = i - lag; j < i; j++) {
                stats.addValue(filteredData.get(j));
            }
            avgFilter.set(i, stats.getMean());
            stdFilter.set(i, sqrt(stats.getPopulationVariance()));
            stats.clear();
        }

        HashMap<String, List> returnMap = new HashMap<String, List>();
        returnMap.put("signals", signals);
        returnMap.put("filteredData", filteredData);
        returnMap.put("avgFilter", avgFilter);
        returnMap.put("stdFilter", stdFilter);

        return returnMap;

    } // end
}

public class Stats {
  Float sum = 0.0f;
  int points =0;
  Float mean = 0.0f;
  ArrayList<Float> arr = new ArrayList<Float>();

  public void addValue(Float number) {
    arr.add(number);
    sum += number;
    points+=1;
  }

  public Float getMean() {
    mean = sum / points;
    return mean;
  }

  public float getPopulationVariance() {
    //Find the mean of the set of data.
    float localMean = this.mean;
    float populationSum = 0;
    //Subtract each number from the mean.
    for(int i = 0; i < arr.size(); i++) {
      populationSum += Math.pow(mean - arr.get(i), 2);
    }
    //Square the result.
    //Add the results together.
    //Divide the result by the total number of numbers in the data set.
    return populationSum/points;
  }

  public void clear() {
    sum = 0.0f;
    points = 0;
    mean = 0.0f;
    arr.clear();
  }



}

public class Tone {
    private Note note; 
    private Shift shift;
    private int octave;
    // with only getters, being immutable
    public Tone(Note note, Shift shift, int ocatve){
      this.note = note;
      this.shift = shift;
      this.octave = octave;
    }
}

public enum Note {
    C, D, E, F, G, A, B
}

public enum Shift {
    DoubleFlat, Flat, Natural, Sharp, DoubleSharp
}



public class C_Scale {
    ArrayList<Tone> scale = new ArrayList<Tone>();
    
    public C_Scale(){
    scale.add(new Tone(Note.C, Shift.Natural, 4));
    scale.add(new Tone(Note.C, Shift.Sharp, 4));
    scale.add(new Tone(Note.D, Shift.Natural, 4));
    scale.add(new Tone(Note.D, Shift.Sharp, 4));
    scale.add(new Tone(Note.E, Shift.Natural, 4));
    scale.add(new Tone(Note.F, Shift.Natural, 4));
    scale.add(new Tone(Note.F, Shift.Sharp, 4));
    scale.add(new Tone(Note.G, Shift.Natural, 4));
    scale.add(new Tone(Note.G, Shift.Sharp, 4));
    scale.add(new Tone(Note.A, Shift.Natural, 4));
    scale.add(new Tone(Note.A, Shift.Sharp, 4));
    scale.add(new Tone(Note.B, Shift.Natural, 4));
    scale.add(new Tone(Note.C, Shift.Natural, 5));
    }
}
class Demo{
  
  
public void practice() {
  stroke(0, 0, 0, 100);
  strokeWeight(1);
  for (int i =0; i < notes.length; i++) {
    line(notes[i], 0, notes[i], height);
  }
}

public void colorBars() {
  float spec = 27.5f;
  float prevSpec = 0;
  float interval = 27.5f;
  for (int i = 0; i < spectrum.length; i++) {
    strokeWeight(2);
    
   
    if (i > 27) {
      colorMode(HSB, spec);
      if (i >= spec+prevSpec) {
        prevSpec = spec;
        spec*=2;
      }
      int f = (int)i%(int)(spec*1.06f);
      if(i * scale > interval * 1.06f - (.5f*(interval-interval*1.6f))){
        stroke(f, spec, spec);
        interval*=1.06f;
      }
      line(i*scale, height, i*scale, height - spectrum[i]*height*amplify );
    }
    colorMode(RGB, 255);
  }
}



public void testCases(){
    background(255);
  playSong();
  int trueCount = 0;
 
  int len = (notes.length-37)+17;
  byte [] input = ConversionUtils.convertFloatArrayToByteArray(spectrum);
 // saveData(input, map.get(notes[(soundIncrement)%(notes.length-35)+15])+".dat");
  for (int i = 0; i < len; i++) {
   int notePlayedIndex = (i)%(notes.length-37)+17;
   spectrum = loadData( map.get(notes[notePlayedIndex])+".dat");
   //best lag is 66 //53?
  //  //best z is 21.4 // 21.2?
  //  //best influence
  String ans =peakDetection(lag, z, influence);
    if (solfege[notePlayedIndex%solfege.length].equals(ans)) {
      trueCount++;
    }
  }
    String ans =peakDetection(lag, z, influence);

  if(ans.equals(prevAns) && !ans.equals("")){
    countAns++;
  }
  else{
    countAns = 0;
    prevAns = ans;
  }
  
  if(countAns>1){
     finalAns = ans; 
  }
  fill(0);
  textSize(30);
  text(finalAns, 300, 100);
 
  //bestInfluenceTest(trueCount);
  //bestLagTest(trueCount);
  //bestZTest(trueCount);
  
}

int lastTrueCount = 0;
public void bestInfluenceTest(int trueCount) {
  if (trueCount > record) {
    record = trueCount;
    bestInfluence = influence;
  }
  println("current I: " + influence);
  println("count: " +trueCount);
  println("best I: " + bestInfluence);
  if (influence < 1) 
    influence +=0.001f;
}
}
}
