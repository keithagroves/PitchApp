package processing.test.radial_solfege_app;

import java.util.HashMap;

import processing.core.PApplet;

class Demo {
	//duplicate variable on PApplet	
	float scale = 1.3455657492f;
	int amplify = 18;
	HashMap<Float, String> map;

	Demo(HashMap<Float, String> map){
		this.map = map;

	}

		public void practice(PApplet app, Float[] notes) {
			app.stroke(0, 0, 0, 100);
			app.strokeWeight(1);
			for (int i = 0; i < notes.length; i++) {
				app.text(map.get(notes[i]), notes[i], 300);

				app.line(notes[i], 0, notes[i], app.height);
			}
		}

		public void colorBars(PApplet app, NoteAnalyzer noteAnalyzer) {
			float spec = 27.5f;
			float prevSpec = 0;
			float interval = 27.5f;
			for (int i = 0; i < noteAnalyzer.spectrum.length; i++) {
				app.strokeWeight(2);

				if (i > 27) {
					app.colorMode(PApplet.HSB, spec);
					if (i >= spec + prevSpec) {
						prevSpec = spec;
						spec *= 2;
					}
					int f = (int) i % (int) (spec * 1.06f);
					if (i * scale > interval * 1.06f - (.5f * (interval - interval * 1.6f))) {
						app.stroke(f, spec, spec);
						interval *= 1.06f;
					}
					app.line(i * scale, app.height, i * scale, app.height - noteAnalyzer.spectrum[i] * app.height * amplify);
				}
				app.colorMode(PApplet.RGB, 255);
			}
		}

	}