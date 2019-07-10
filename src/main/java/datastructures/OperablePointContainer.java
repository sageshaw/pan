package datastructures;

/**
 * Defines a PointContainer that can be operated on via the analysis operations in analysis.ops
 */

public interface OperablePointContainer extends PointContainer {


    Triple getCentroid();

    Triple getMin();

    Triple getMax();

    void translate(double xOffset, double yOffset, double zOffset);

    void makeRelative();
}
