package analysis.ops;

import datastructures.points.OperablePointContainer;

/**
 * Defines behavior of a single-channel operation.
 */
@Deprecated
public abstract class UniOperation extends AnalysisOperation {

    public abstract void setChannel(OperablePointContainer data);
}
