package analysis.ops;

import analysis.Triple;
import analysis.data.Linear;

/**
 * An implementation of a NearestNeighbor search for a Linear container (which means it is a linear search).
 */

public class LinearNearestNeighbor extends AnalysisOperation {


    //Must pass in a Linear container to operate on
    public LinearNearestNeighbor(Linear container) {
        super(container);
    }


    //Returns double array of all nearest neighbor relationships
    @Override
    public double[] process() {

        //Create array for results and list to grab point data from Linear object
        Triple[] points = container.getPoints();
        double[] result = new double[points.length];

        //Some pre-initialized variables so we don't have to throw away references
        double minDist;
        Triple currentPoint;
        double currentDist;

        //Loop through construct, compare every point to every other point in pointList, record lowest array
        //Distance is calculated with traditional Euclidean distance formula (for three dimensions)
        for (int i = 0; i < result.length; i++) {
            currentPoint = points[i];
            minDist = Double.MAX_VALUE;

            for (int j = 0; j < points.length; j++) {
                if (i == j) {
                    j++;
                    if (j == points.length) break;
                }
                Triple pt = points[j];
                currentDist = Math.sqrt(Math.pow(currentPoint.getX() - pt.getX(), 2.0) +
                        Math.pow(currentPoint.getY() - pt.getY(), 2.0) +
                        Math.pow(currentPoint.getZ() - pt.getZ(), 2.0));

                minDist = Math.min(currentDist, minDist);
            }

            result[i] = minDist;
        }

        return result;
    }

}
