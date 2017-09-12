package constructs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* An space partitioning solution for faster nearest neighbor analysis
 *
 * Splitting Rules:
 * >1 points in a space will result in a split
 *  If one dimension is only 1 micrometer wide, then we will result to quadtree (other branches will be empty)
 *  A space one cubic microcubic meter in volume will not split
 *
 *
 * This is how the partitions are set:
 *
 *             +Y
 *              |
 *              |
 *              |
 *              |   1----------5
 *              |  /|         /|
 *              |/  |       /  |
 *              0----------4   |
 *              |   |      |   |
 *              |   2- - - | - 6
 *              |  /       |  /
 *              |/         |/
 *              3----------7---------------+x
 *            /
 *          /
 *        /
 *      /
 *    /
 *  +Z
 *
 */


public class Octree extends OperableContainer {


    List<Triple> points;
    Octree parent;
    Octree[] children;



    private Octree(String name, List<Triple> data, Octree parent) {
        this(name, data);
        this.parent = parent;
    }

    @Override
    public Triple getMax() {
        return null;
    }



    public Octree(String name, List<Triple> data) {
        super(name);

        //Calculate dimensions of 'box'
        int xMax = 0;
        int yMax = 0;
        int zMax = 0;

        int xMin = Integer.MAX_VALUE;
        int yMin = Integer.MAX_VALUE;
        int zMin = Integer.MAX_VALUE;

        for (Triple pt : data) {
            xMax = Math.max(pt.getX(), xMax);
            yMax = Math.max(pt.getY(), yMax);
            zMax = Math.max(pt.getZ(), zMax);

            xMin = Math.min(pt.getX(), xMin);
            yMin = Math.min(pt.getY(), yMin);
            zMin = Math.min(pt.getZ(), zMin);
        }

        int xDem = xMax - xMin;
        int yDem = yMax - yMin;
        int zDem = zMax - zMin;

        //Start sorting our data into array of lists, which we be used as 'data' in next round of recursive Octree
        //instantiation. If we don't need to instantiate the new space, then the list will be left null
        //The index number in the 'partitions' list will correspond to child node index in Octree 'children' array

        ArrayList<Triple>[] partitions = new ArrayList[8];

        //Instantiate them all, we'll reset them to null if necessary next
        for (int i = 0; i < partitions.length; i++) {
            partitions[i] = new ArrayList <>();
        }

        //If xDem is dimensionless (which means xDem = 0, since dem is determined by difference),
        // turn this node into a quadtree using indices 0,1,2,3 (set others to
        //null
        if (xDem <= 0) {
            partitions[4] = null;
            partitions[5] = null;
            partitions[6] = null;
            partitions[7] = null;
        }

        //Use only 2, 3, 6, 7 if yDem is dimensionless
        if (yDem <= 0) {
            partitions[0] = null;
            partitions[1] = null;
            partitions[4] = null;
            partitions[5] = null;
        }

        //Use only 0, 3, 4, 7 if zDem is dimensionless

        if (zDem <= 0) {
            partitions[1] = null;
            partitions[2] = null;
            partitions[4] = null;
            partitions[6] = null;
        }

        //If we only have one point, then we can stop here (set all to null)
        if (data.size() <= 1) {
            for (int i = 0; i < partitions.length; i++) {
                partitions[i] = null;
            }
        }

        //Let's sort our points!



    }



    @Override
    public Triple getMin() {
        return null;
    }

    @Override
    public void add(Object element) {

    }

    @Override
    public void makeRelative() {

    }

    @Override
    public void translate(int xOffset, int yOffset, int zOffset) {

    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public double[] getNearestNeighborAnalysis() {
        return new double[0];
    }

    @Override
    public Triple getCentroid() {
        return null;
    }
}
