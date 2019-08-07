package datastructures.analysis;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Simple DataContainer data storage class that represents data as double array.
 */
public class LinearData implements DataContainer {

    String name;
    double[] data;
    private String batchKey;

    private Map<String, Double> annotations;


    public LinearData(String name, double[] data) {
        this.name = name;
        this.data = data;
        annotations = new HashMap<>();
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public double[] getData() {
        return data;
    }

    @Override
    public double[] getDataWithinRange(double low, double high) {
        List<Double> resultList = new ArrayList();

        for (double datum : data) {
            if (datum >= low && datum <= high) {
                resultList.add(datum);
            }
        }

        return resultList.stream().mapToDouble(Double::doubleValue).toArray();
    }

    @Override
    public int getSize() {
        return data.length;
    }

    @Override
    public void setBatchKey(String key) {
        batchKey = key;
    }

    @Override
    public String getBatchKey() {
        return batchKey;
    }

    @Override
    public boolean isBatched() {
        return batchKey != null;
    }

    @Override
    public void removeFromBatch() {
        batchKey = null;
    }

    @Override
    public void addEntry(String entryName, double val) {
        annotations.put(entryName, val);
    }

    @Override
    public String[] getEntryNames() {
        return annotations.keySet().toArray(new String[0]);
    }

    @Override
    public double value(String entryName) {
        return annotations.get(entryName);
    }

    @Override
    public String header() {

        String result = "Annotations\n";

        for (String key : annotations.keySet()) {
            result += key + "\t" + annotations.get(key) + System.lineSeparator();
        }

        result += "Value";

        return result;
    }

    @Override
    public String body() {
        String result = "";


        for (double val : data) {
            result += val + System.lineSeparator();
        }

        return result;
    }

    @Override
    public double max() {
        double maxVal = Double.MIN_VALUE;
        for (double datum : data) {
            maxVal = Math.max(maxVal, datum);
        }

        return maxVal;
    }

    @Override
    public double min() {
        double minVal = Double.MAX_VALUE;
        for (double datum : data) {
            minVal = Math.min(minVal, datum);
        }

        return minVal;
    }

}
