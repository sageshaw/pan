package analysis.util;

import java.util.Arrays;

public class StatUtilities {

    public static double getMean(double[] data) {
        double sum = 0;
        for (double val : data) {
            sum += val;
        }

        return sum / data.length;
    }

    public static double getSampleStandardDeviation(double[] data) {
        double sum = getSquareDifferenceSum(data);

        return Math.sqrt(sum / (data.length - 1));
    }

    public static double getPopulationStandardDeviation(double[] data) {
        double sum = getSquareDifferenceSum(data);

        return Math.sqrt(sum / (data.length));
    }

    private static double getSquareDifferenceSum(double[] data) {
        double mean = getMean(data);
        double sum = 0;
        for (double val : data) {
            sum += Math.pow(val - mean, 2);
        }
        return sum;
    }

    public static double getMedian(double[] data) {
        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);
        final int len = copy.length;
        if (len % 2 == 0) {
            return getMean(new double[]{copy[len / 2], copy[len / 2 - 1]});
        }

        return copy[len / 2];
    }

    public static double getUpperQuartileInc(double[] data) {
        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);
        final int len = copy.length;
        return getMedian(Arrays.copyOfRange(copy, len / 2, len));
    }

    public static double getUpperQuartileExc(double[] data) {
        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);
        final int len = copy.length;
        if (len % 2 == 1) return getMedian(Arrays.copyOfRange(copy, len / 2 + 1, len));
        return getMedian(Arrays.copyOfRange(copy, len / 2, len));
    }

    public static double getLowerQuartileInc(double[] data) {
        double[] copy = data;
        Arrays.sort(copy);
        final int len = copy.length;
        if (len % 2 == 1) return getMedian(Arrays.copyOfRange(copy, 0, len / 2 + 1));
        return getMedian(Arrays.copyOfRange(copy, 0, len / 2));
    }

    public static double getLowerQuartileExc(double[] data) {

        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);
        final int len = copy.length;
        return getMedian(Arrays.copyOfRange(copy, 0, len / 2));
    }


}
