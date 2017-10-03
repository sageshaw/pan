package analysis.util;

import java.util.Arrays;

public class StatUtilities {

    public static double mean(double[] data) {
        double sum = 0;
        for (double val : data) {
            sum += val;
        }

        return sum / data.length;
    }

    public static double sampleStandardDeviation(double[] data) {
        double sum = squareDifferenceSum(data);

        return Math.sqrt(sum / (data.length - 1));
    }

    public static double populationStandardDeviation(double[] data) {
        double sum = squareDifferenceSum(data);

        return Math.sqrt(sum / (data.length));
    }

    private static double squareDifferenceSum(double[] data) {
        double mean = mean(data);
        double sum = 0;
        for (double val : data) {
            sum += Math.pow(val - mean, 2);
        }
        return sum;
    }

    public static double median(double[] data) {
        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);
        final int len = copy.length;
        if (len % 2 == 0) {
            return mean(new double[]{copy[len / 2], copy[len / 2 - 1]});
        }

        return copy[len / 2];
    }

    public static double upperQuartileInc(double[] data) {
        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);
        final int len = copy.length;
        return median(Arrays.copyOfRange(copy, len / 2, len));
    }

    public static double upperQuartileExc(double[] data) {
        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);
        final int len = copy.length;
        if (len % 2 == 1) return median(Arrays.copyOfRange(copy, len / 2 + 1, len));
        return median(Arrays.copyOfRange(copy, len / 2, len));
    }

    public static double lowerQuartileInc(double[] data) {
        double[] copy = data;
        Arrays.sort(copy);
        final int len = copy.length;
        if (len % 2 == 1) return median(Arrays.copyOfRange(copy, 0, len / 2 + 1));
        return median(Arrays.copyOfRange(copy, 0, len / 2));
    }

    public static double lowerQuartileExc(double[] data) {

        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);
        final int len = copy.length;
        return median(Arrays.copyOfRange(copy, 0, len / 2));
    }


}
