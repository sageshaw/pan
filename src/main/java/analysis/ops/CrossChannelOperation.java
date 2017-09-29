package analysis.ops;

import analysis.data.OperablePointContainer;

public abstract class CrossChannelOperation extends AnalysisOperation {

    public abstract void init(OperablePointContainer first, OperablePointContainer second);

}
