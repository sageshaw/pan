package structs;

public interface OperablePointContainer extends PointContainer {


    Triple getCentroid();

    Triple getMin();

    Triple getMax();

    double[] getNearestNeighborAnalysis();


}
