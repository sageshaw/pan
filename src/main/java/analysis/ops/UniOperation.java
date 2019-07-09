package analysis.ops;

import datastructures.OperablePointContainer;

/**
 * Defines behavior of a single-channel operation.
 */
public abstract class UniOperation extends AnalysisOperation {

    public abstract void setChannel(OperablePointContainer data);
}
