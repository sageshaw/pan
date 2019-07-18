package datastructures.postanalysis;


import datastructures.Batchable;

import java.util.List;

/**
 *  Defines behavior for an object that contains analysis result data.
 */

public interface AnalysisContainer extends Iterable, Batchable {


    String key(double[] value);

    String[] keys();

    double[] get(String name);

    double[] remove(String name);

    boolean remove(double[] value);

    void add(String name, double[] data);

    int getSize();

}
