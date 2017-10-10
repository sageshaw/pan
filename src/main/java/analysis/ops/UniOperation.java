package analysis.ops;

import analysis.data.OperablePointContainer;

/**
 * Defines behavior of a single-channel operation.
 */
public abstract class UniOperation extends AnalysisOperation {

    public abstract void setChannel(OperablePointContainer data);
}
