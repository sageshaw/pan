package datastructures.postanalysis;


import datastructures.Batchable;

import java.util.List;

/**
 *  Defines behavior for an object that contains analysis result data.
 */

public interface AnalysisContainer extends Batchable {

    String getName();

    double[] getData();

    int getSize();

}
