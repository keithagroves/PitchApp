package processing.test.radial_solfege_app;
public class Tone {
	private Note note;
	private Shift shift;
	private int octave;

	// with only getters, being immutable
	public Tone(Note note, Shift shift, int ocatve) {
		this.note = note;
		this.shift = shift;
		this.octave = octave;
	}
}

