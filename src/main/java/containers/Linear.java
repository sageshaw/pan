package containers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//containers.Linear object to hold point data + channel name
public class Linear extends TripleContainer {

  //our fields
  private List<Triple> points;

  public Linear(String channelName) {
    this(channelName, new ArrayList<>());
  }

  public Linear(String name, List<Triple> data) {
    super(name);
    points = data;
  }

  //returns a triplet containing the minimum x, y, z values in channel
  //if there are no points in the channel, will return a triple containing -1's
  public Triple getMin() {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int minZ = Integer.MAX_VALUE;

    for (Triple pt : points) {
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
  public void translate(int xOffset, int yOffset, int zOffset) {
    for (Triple pt : points) {
      pt.setX(pt.getX() + xOffset);
      pt.setY(pt.getY() + yOffset);
      pt.setZ(pt.getZ() + zOffset);
    }
  }

  @Override
  public Iterator<Triple> iterator() {
    return points.iterator();
  }

  @Override
  public void add(Object element) {
    points.add((Triple) element);
  }

  @Override
  public double[] analyzeNearestNeighbor() {
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


        currentDist = Math.sqrt(Math.pow(currentPoint.getX()-points.get(j).getX(),2.0) +
                Math.pow(currentPoint.getY()-points.get(j).getY(),2.0) +
                Math.pow(currentPoint.getZ()-points.get(j).getZ(), 2.0));

        minDist = Math.min(currentDist, minDist);
      }

      result[i] = minDist;
    }

    return result;
  }
}
