package analysis.pts;

import analysis.Triple;

public interface OperablePointContainer extends PointContainer {


    Triple getCentroid();

    Triple getMin();

    Triple getMax();


}
