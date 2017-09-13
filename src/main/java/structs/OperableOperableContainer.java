package structs;

public abstract class OperableOperableContainer extends PointContainer {

    public OperableOperableContainer(String name) {
        super(name);
    }

    public abstract Triple getCentroid();

    public abstract Triple getMin();

    public abstract Triple getMax();

    public abstract double[] getNearestNeighborAnalysis();


}
