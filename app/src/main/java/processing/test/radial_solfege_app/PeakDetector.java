package processing.test.radial_solfege_app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class PeakDetector {
	
		int lag;
		Float threshold;
		Float influence;
		public PeakDetector(int lag, Float influence, Float threshold) {
			// TODO Auto-generated constructor stub
		}

        public HashMap<String, List<? extends Number>> analyzeDataForSignals(List<Float> data, int lag, Float threshold, Float influence) {
        	int signalCount = 0;
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
            stdFilter.set(lag - 1, (float)Math.sqrt(stats.getPopulationVariance())); // getStandardDeviation() uses sample variance
            //stats.clear();

            // loop input starting at end of rolling window
            for (int i = lag; i < data.size(); i++) {
                // if the distance between the current value and average is enough standard deviations (threshold) away
                if (data.get(i) - avgFilter.get(i - 1) > threshold * stdFilter.get(i - 1)) {
                  
                    signals.set(i, 1);
                    signalCount++;
                    // filter this signal out using influence
                    filteredData.set(i, (influence * data.get(i)) + ((1 - influence) * filteredData.get(i - 1)));
                } else {
                    // ensure this signal remains a zero
                    signals.set(i, 0);
                    // ensure this value is not filtered
                    filteredData.set(i, data.get(i));
                }

    
                stats.addValue(filteredData.get(i));
               
                avgFilter.set(i, stats.getMean());
                stdFilter.set(i, (float)Math.sqrt(stats.getPopulationVariance()));
                stats.clear();
            }
            
            //decrease/increase threshold to get the target number of signals
            if(signalCount > 50) {
            	threshold *=.9f;
            } else {
            	threshold *=1.1f;
            }
            HashMap<String, List<? extends Number>> returnMap = new HashMap<String, List<? extends Number>>();
            returnMap.put("signals", signals);
            returnMap.put("filteredData", filteredData);
            returnMap.put("avgFilter", avgFilter);
            returnMap.put("stdFilter", stdFilter);

            return returnMap;

        } // end
    }