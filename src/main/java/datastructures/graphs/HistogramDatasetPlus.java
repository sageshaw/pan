package datastructures.graphs;

import datastructures.Annotatable;
import datastructures.Batchable;
import datastructures.Exportable;
import org.jfree.data.statistics.HistogramDataset;

import java.util.HashMap;
import java.util.Map;

public class HistogramDatasetPlus extends HistogramDataset implements Batchable, Annotatable, Exportable {

    private String batchKey;

    private Map<String, Double> annotations;


    public HistogramDatasetPlus() {
        super();
        annotations = new HashMap<>();
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
            result += key + "\t" + annotations.get(key) + "\n";
        }

        //    result += "X(range)\tY(value)";

        return result;
    }

    @Override
    public String body() {

//        String result = "";
//
//        for (int i = 0; i < this.getItemCount(0); i++) {
//            result += this.getStartXValue(0, i) + "-" + this.getEndXValue(0, i) + "\t";
//            result += this.getYValue(0, i) + "\n";
//        }
//
//        return result;

        return "";
    }
}
