package containers;

import java.util.Iterator;
import java.util.List;

/* An space partitioning solution for faster nearest neigbor analysis
 *
 *       This is how the partitions are set:
 *
 *             +Y
 *              |
 *              |
 *              |
 *              |   1----------5
 *              | / |        / |
 *              |/  |       /  |
 *              0----------4   |
 *              |   |      |   |
 *              |   2- - - | - 6
 *              |  /       |  /
 *              | /        | /
 *              3----------7---------------+x
 *             /
 *            /
 *           /
 *          /
 *         /
 *       +Z
 */


public class Octree extends TripleContainer implements Iterable {


    List<Triple> points;
    Octree parent;
    Octree[] children;

    public Octree(String name, List<Triple> data) {
        super(name);
        points = data;
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
}
