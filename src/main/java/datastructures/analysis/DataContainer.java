package datastructures.analysis;


import datastructures.Annotatable;
import datastructures.Batchable;
import datastructures.Exportable;

/**
 *  Defines behavior for an object that contains analysis result data.
 */

public interface DataContainer<T extends Number> extends Batchable, Annotatable, Exportable {

    String getName();

    double[] getData();

    double[] getDataWithinRange(double low, double high);

    int getSize();

    double max();

    double min();

}
