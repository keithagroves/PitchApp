package processing.test.radial_solfege_app;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

class MyTest {
	Float[] notes;
	HashMap<Float, String> map = new HashMap<Float, String>();

	void loadTest() {
		Scanner sc;
		try {
			sc = new Scanner(new File("notes.txt"));

			List<String> arr = new ArrayList<String>();
			while (sc.hasNextLine()) {
				arr.add(sc.nextLine());
			}
			String[] lines = arr.toArray(new String[0]);
			notes = new Float[lines.length];
			int i = 0;
			for (String line : lines) {
				line = line.replaceAll("\\s", " ");
				String[] noteInfo = line.trim().split(" ");
				map.put(Float.parseFloat(noteInfo[1]), noteInfo[0]);
				notes[i] = Float.parseFloat(noteInfo[1]);
				i++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("this is a fail");
		}
	}

	@Test
	void analyzerTest() {
		NoteAnalyzer n = new NoteAnalyzer();
		loadTest();
		assertTrue(map.keySet().size() > 0);
		assertTrue(map.get(65.41f).equals("C2"));
		for (float i = 0; i < 1000; i+=1) {
			//System.out.println(n.findClosest2(notes, (float)i));
			//assertEquals(notes[n.findClosest2(notes, (float) i)], notes[n.findClosest(notes, (float) i)]);
			System.out.print(map.get(notes[n.findClosest2(notes,  i)]) + " : ");
			System.out.println(map.get(notes[n.findClosest(notes,  i)]));
		}
	}

}
