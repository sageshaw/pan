package filters;

@Deprecated
public class MaxCutoff implements PanFilter {

    private double max;

    public MaxCutoff(double max) {
        this.max = max;
    }

    @Override
    public double[] filter(double[] data) {
        int resultLength = data.length;
        for (double val : data) {
            if (val > max) resultLength--;
        }

        double[]result = new double[resultLength];

        int i = 0;
        for (double val : data) {
            if (val <= max) {
                result[i] = val;
                i++;
            }
        }

        return result;
    }


}
