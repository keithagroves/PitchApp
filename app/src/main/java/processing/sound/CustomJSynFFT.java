package processing.sound;

import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.FixedRateMonoWriter;

import processing.test.radial_solfege_app.NoteAnalyzer;

import java.util.Arrays;

public class CustomJSynFFT extends FixedRateMonoWriter {

	private FloatSample buffer;
	private float[] real;
	private float[] imaginary;

	protected CustomJSynFFT(int bufferSize) {
		super();
		this.buffer = new FloatSample(bufferSize);
		this.real = new float[bufferSize];
		this.imaginary = new float[bufferSize];
		// write any connected input into the output buffer ad infinitum
		this.dataQueue.queueLoop(this.buffer);
	}

	protected String calculateMagnitudes(float[] target) {
		// get position currently being written to
		int pos = (int) this.dataQueue.getFrameCount() % this.buffer.getNumFrames();
		for (int i = 0; i < this.buffer.getNumFrames(); i++) {
			this.real[i] = (float) this.buffer.readDouble((pos + i) % this.buffer.getNumFrames());
		}
		Arrays.fill(this.imaginary, 0);
		CustomFourierMath.fft(this.real.length, this.real, this.imaginary);
		return NoteAnalyzer.peakDetection(this.real, this.imaginary, target);
	}
}
