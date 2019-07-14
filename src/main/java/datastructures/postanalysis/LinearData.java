package datastructures.postanalysis;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;

/**
 *  Simple AnalysisContainer data storage class that represents data as double array.
 */
public class LinearData implements AnalysisContainer {


    private BiMap<String, double[]> channels = HashBiMap.create();


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
}
