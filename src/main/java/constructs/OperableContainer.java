package constructs;

public abstract class OperableContainer extends PointContainer {

    public OperableContainer(String name) {
        super(name);
    }

    public abstract Triple getCentroid();

    public abstract Triple getMin();

    public abstract Triple getMax();

    public abstract double[] getNearestNeighborAnalysis();


}
