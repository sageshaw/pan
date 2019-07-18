package datastructures.postanalysis;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import datastructures.Batchable;

import java.util.Iterator;

/**
 *  Simple AnalysisContainer data storage class that represents data as double array.
 */
public class LinearData implements AnalysisContainer, Batchable {


    private BiMap<String, double[]> channels = HashBiMap.create();

    private String batchKey;

    @Override
    public String key(double[] value) {
        return channels.inverse().get(value);
    }

    @Override
    public String[] keys() {
        return channels.keySet().toArray(new String[0]);
    }

    @Override
    public double[] get(String name) {
        return channels.get(name);
    }

    @Override
    public double[] remove(String name) {
        return channels.remove(name);
    }

    @Override
    public boolean remove(double[] value) {
        return channels.remove(key(value), value);
    }

    @Override
    public void add(String name, double[] data) {
        channels.put(name, data);
    }

    @Override
    public int getSize() {
        return channels.size();
    }

    @Override
    public Iterator iterator() {
        return channels.values().iterator();
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
