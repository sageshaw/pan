package datastructures.postanalysis;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import datastructures.Batchable;

import java.util.Iterator;

/**
 *  Simple AnalysisContainer data storage class that represents data as double array.
 */
public class LinearData implements AnalysisContainer {

    String name;
    double[] data;
    private String batchKey;


    public LinearData(String name, double[] data) {
        this.name = name;
        this.data = data;
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
}
