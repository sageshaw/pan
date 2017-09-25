package analysis.ops;

/**
 * Defines the behavior of a point analysis technique (originally for template method, but currently only method
 * in this class is process(), so really inefficient interface at the moment
 */
public abstract class AnalysisOperation {

    //Executes template
    public double[] execute() {
        return process();
    }

    abstract double[] process();


}
