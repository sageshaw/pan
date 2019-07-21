package datastructures.analysis;


import datastructures.Annotatable;
import datastructures.Batchable;

/**
 *  Defines behavior for an object that contains analysis result data.
 */

public interface DataContainer<T extends Number> extends Batchable, Annotatable {

    String getName();

    double[] getData();

    int getSize();

}
