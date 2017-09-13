package analysis.techs;

import analysis.pts.OperablePointContainer;

public abstract class PointOperation {

    protected OperablePointContainer container;

    public PointOperation(OperablePointContainer container) {
        this.container = container;
    }

    public double[] execute(OperablePointContainer channel) {
        return process();
    }

    abstract double[] process();


}
