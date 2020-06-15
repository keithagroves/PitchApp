package processing.test.radial_solfege_app;

import java.util.ArrayList;

public class Stats {
        Float sum = 0.0f;
        int points = 0;
        Float mean = 0.0f;
        ArrayList<Float> arr = new ArrayList<Float>();

        public void addValue(Float number) {
            arr.add(number);
            sum += number;
            points += 1;
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
            for (int i = 0; i < arr.size(); i++) {
                populationSum += Math.pow(mean - arr.get(i), 2);
            }
            //Square the result.
            //Add the results together.
            //Divide the result by the total number of numbers in the data set.
            return populationSum / points;
        }

        public void clear() {
            sum = 0.0f;
            points = 0;
            mean = 0.0f;
            arr.clear();
        }


    }