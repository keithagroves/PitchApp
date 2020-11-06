package processing.test.radial_solfege_app;

import java.nio.ByteBuffer;

class ConversionUtils {
		public static byte[] convertFloatArrayToByteArray(float[] arr) {
			byte[] output = null;
			for (int i = 0; i < arr.length; i++) {
				byte[] bytes = toByteArray(arr[i]);
				output = (output == null) ? bytes : concat(output, bytes);
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

		//000
		//001
		//010
		//011
		//100
		
		//1
		//0
		//1
		//0

		
		// change to byte arr to float arr.
		public static float[] convertByteArraytoFloatArray(byte[] loadData) {
			byte[] floatData = new byte[Float.BYTES];
			float[] result = new float[loadData.length / Float.BYTES];
			int floatStart = 0;
			int index = 0;
			while (floatStart < loadData.length) {
				for (int i = 0; i < Float.BYTES; i++) {
					floatData[i] = loadData[floatStart + i];
				}
				result[index] = toFloat(floatData);
				index++;
				floatStart += Float.BYTES;
			}
			return result;
		}
		
		
		  static public byte[] concat(byte a[], byte b[]) {
			    byte c[] = new byte[a.length + b.length];
			    System.arraycopy(a, 0, c, 0, a.length);
			    System.arraycopy(b, 0, c, a.length, b.length);
			    return c;
			  }

	}
