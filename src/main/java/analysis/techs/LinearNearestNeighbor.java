package analysis.techs;

import analysis.Triple;
import analysis.pts.Linear;

import java.util.ArrayList;
import java.util.List;

public class LinearNearestNeighbor extends PointOperation {


    public LinearNearestNeighbor(Linear container) {
        super(container);
    }


    @Override
    public double[] process() {

        List <Double> distances = new ArrayList <>();
        List <Triple> points = ((Linear) container).getData();


        double[] result = new double[points.size()];

        double minDist;
        Triple currentPoint;
        double currentDist;

        for (int i = 0; i < result.length; i++) {
            currentPoint = points.get(i);
            minDist = Double.MAX_VALUE;

            for (int j = 0; j < points.size(); j++) {
                if (i == j) {
                    j++;
                    if (j == points.size()) break;
                }


                currentDist = Math.sqrt(Math.pow(currentPoint.getX() - points.get(j).getX(), 2.0) +
                        Math.pow(currentPoint.getY() - points.get(j).getY(), 2.0) +
                        Math.pow(currentPoint.getZ() - points.get(j).getZ(), 2.0));

                minDist = Math.min(currentDist, minDist);
            }

            result[i] = minDist;
        }

        return result;
    }

}
