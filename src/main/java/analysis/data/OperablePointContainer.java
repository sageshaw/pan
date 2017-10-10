package analysis.data;

import analysis.Triple;

/**
 * Defines a PointContainer that can be operated on via the analysis operations in analysis.ops
 */

public interface OperablePointContainer extends PointContainer {


    Triple getCentroid();

    Triple getMin();

    Triple getMax();

    Triple[] getPoints();


}
