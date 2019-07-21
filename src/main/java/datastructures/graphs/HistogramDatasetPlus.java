package datastructures.graphs;

import datastructures.Annotatable;
import datastructures.Batchable;
import org.jfree.data.statistics.HistogramDataset;

import java.util.HashMap;
import java.util.Map;

public class HistogramDatasetPlus extends HistogramDataset implements Batchable, Annotatable {

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
}
