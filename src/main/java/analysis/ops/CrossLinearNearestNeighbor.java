package analysis.ops;

import analysis.Triple;
import analysis.data.ListPointContainer;

public class CrossLinearNearestNeighbor extends AnalysisOperation {

    ListPointContainer operator;
    ListPointContainer data;

    public CrossLinearNearestNeighbor(ListPointContainer op, ListPointContainer dat) {
        operator = op;
        data = dat;
    }

    @Override
    double[] process() {
        Triple[] opPoints = operator.getPoints();
        Triple[] datPoints = data.getPoints();
        double[] result = new double[opPoints.length];

        for (int i = 0; i < opPoints.length; i++) {
            Triple opPt = opPoints[i];
            double minDist = Double.MAX_VALUE;

            for (Triple datPt : datPoints) {
                double curDist = Math.sqrt(Math.pow(opPt.getX() - datPt.getX(), 2.0) +
                        Math.pow(opPt.getY() - datPt.getY(), 2.0) +
                        Math.pow(opPt.getZ() - datPt.getZ(), 2.0));

                minDist = Math.min(curDist, minDist);
            }

            result[i] = minDist;


        }

        return result;
    }
}
