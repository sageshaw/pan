package analysis.ops;

import analysis.Triple;
import analysis.data.OperablePointContainer;


@OpScript(type = CrossChannelOperation.class, label = "Cross Nearest Neighbor")
public class CrossLinearNearestNeighbor extends CrossChannelOperation {

    OperablePointContainer operator;
    OperablePointContainer data;

    public CrossLinearNearestNeighbor(OperablePointContainer op, OperablePointContainer dat) {
        init(op, dat);
    }

    @Override
    public void init(OperablePointContainer first, OperablePointContainer second) {
        operator = first;
        data = second;
    }

    @Override
    public double[] process() {
        Triple[] opPoints = operator.getPoints();
        Triple[] datPoints = data.getPoints();
        double[] result = new double[opPoints.length];

        for (int i = 0; i < opPoints.length; i++) {
            Triple opPt = opPoints[i];
            double minDist = Double.MAX_VALUE;

            for (Triple datPt : datPoints) {
                if (opPt != datPt) {
                    double curDist = Math.sqrt(Math.pow(opPt.getX() - datPt.getX(), 2.0) +
                            Math.pow(opPt.getY() - datPt.getY(), 2.0) +
                            Math.pow(opPt.getZ() - datPt.getZ(), 2.0));

                    minDist = Math.min(curDist, minDist);
                }
            }

            result[i] = minDist;


        }

        return result;
    }
}
