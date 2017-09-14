package analysis.ops;

import analysis.data.OperablePointContainer;


/**
 * Defines the behavior of a point analysis technique (originally for template method, but currently only method
 * in this class is process(), so really inefficient interface at the moment
 */
public abstract class AnalysisOperation {

    //Represents the container that must be worked on
    protected OperablePointContainer container;


    public AnalysisOperation(OperablePointContainer container) {
        this.container = container;
    }

    //Executes template
    public double[] execute(OperablePointContainer channel) {
        return process();
    }

    abstract double[] process();


}
