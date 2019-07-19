package datastructures.analysis;


import datastructures.Batchable;

/**
 *  Defines behavior for an object that contains analysis result data.
 */

public interface DataContainer<T extends Number> extends Batchable {

    String getName();

    double[] getData();

    int getSize();

}
