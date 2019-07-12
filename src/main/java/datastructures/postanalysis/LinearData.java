package datastructures.postanalysis;


/**
 *  Simple AnalysisContainer data storage class that represents data as double array.
 */
public class LinearData implements AnalysisContainer {

    private String name;
    private double[] data;

    public LinearData(String name, double[] data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double[] getResults() {
        return data;
    }


}
