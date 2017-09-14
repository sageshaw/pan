package analysis.ops;

import analysis.Triple;
import analysis.data.Linear;

import java.util.List;

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
        List <Triple> points = ((Linear) container).getData();
        double[] result = new double[points.size()];

        //Some pre-initialized variables so we don't have to throw away references
        double minDist;
        Triple currentPoint;
        double currentDist;

        //Loop through construct, compare every point to every other point in pointList, record lowest array
        //Distance is calculated with traditional Euclidean distance formula (for three dimensions)
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
