package datastructures.graphs;

import datastructures.Annotatable;
import datastructures.Batchable;
import datastructures.Exportable;
import org.jfree.data.statistics.HistogramDataset;

import java.util.Arrays;
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
    public String[] header() {

        String[] keys = annotations.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        return keys;
    }

    @Override
    public String[][] body() {
        String[] keys = annotations.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        String[][] vals = new String[1][keys.length];

        for (int i = 0; i < keys.length; i++) {
            vals[0][i] = "" + annotations.get(keys[i]);
        }


        return vals;

    }
}
