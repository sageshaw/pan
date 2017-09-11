package containers;

public interface Operable {

    Triple getCentroid();

    Triple getDimensions();

    Triple getMin();

    Triple getMax();

    double[] getNearestNeighborAnalysis();


}
