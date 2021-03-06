package datastructures.points;

import datastructures.Batchable;
import display.Displayable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A ListPointContainer object to hold point data in a list. This is mainly accessible by list. Also Displayable (can be
 * used with ImgGenerator)
 */
public class Linear<T extends Triple> implements OperablePointContainer, Displayable {


    //List container to store Triple points
    protected List<T> points;

    public Linear() {
        points = new ArrayList<>();
    }

    public Linear(List<T> points) {
        this.points = points;
    }


    @Override
    public Triple getMax() {
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;

        for (T pt : points) {
            maxX = Math.max(pt.getX(), maxX);
            maxY = Math.max(pt.getY(), maxY);
            maxZ = Math.max(pt.getZ(), maxZ);
        }

        if (maxX == Integer.MIN_VALUE) maxX = -1;
        if (maxY == Integer.MIN_VALUE) maxY = -1;
        if (maxZ == Integer.MIN_VALUE) maxZ = -1;

        return new Triple(maxX, maxY, maxZ);
    }

    @Override
    public Triple[] getPoints() {
        return points.toArray(new Triple[0]);
    }

    @Override
    public Triple getDimensions() {

        Triple mins = getMin();
        Triple maxes = getMax();

        return new Triple(maxes.getX() - mins.getX(), maxes.getY() - mins.getY(), maxes.getZ() - mins.getZ());
    }


    //returns a triplet containing the minimum x, y, z values in channel
    //if there are no points in the channel, will return a triple containing -1's
    public Triple getMin() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;

        for (T pt : points) {
            minX = Math.min(pt.getX(), minX);
            minY = Math.min(pt.getY(), minY);
            minZ = Math.min(pt.getZ(), minZ);
        }

        if (minX == Integer.MAX_VALUE) minX = -1;
        if (minY == Integer.MAX_VALUE) minY = -1;
        if (minZ == Integer.MAX_VALUE) minZ = -1;

        return new Triple(minX, minY, minZ);
    }

    @Override
    public void makeRelative() {
        Triple mins = getMin();
        translate(-mins.getX(), -mins.getY(), -mins.getZ());
    }

    @Override
    public void translate(double xOffset, double yOffset, double zOffset) {
        for (T pt : points) {
            pt.setX(pt.getX() + xOffset);
            pt.setY(pt.getY() + yOffset);
            pt.setZ(pt.getZ() + zOffset);
        }
    }

    @Override
    public int getSize() {
        return points.size();
    }

    @Override
    public Iterator<T> iterator() {
        return points.iterator();
    }


    @Override
    public Triple getCentroid() {
        int x = 0, y = 0, z = 0;
        for (T pt : points) {
            x += pt.getX();
            y += pt.getY();
            z += pt.getZ();
        }

        int numPts = points.size();

        return new Triple(x / numPts, y / numPts, z / numPts);
    }


    @Override
    public void add(Triple pt) {
        points.add((T) pt);
    }

    @Override
    public boolean remove(Triple pt) {
        return points.remove(pt);
    }

}
