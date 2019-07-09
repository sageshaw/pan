package analysis.ops;

import datastructures.OperablePointContainer;

/**
 * Defines behavior of a cross-channel operation.
 */
public abstract class BiOperation extends AnalysisOperation {

    public abstract void setChannel(OperablePointContainer first, OperablePointContainer second);

}
