package containers;

import java.util.ArrayList;
import java.util.List;

//containers.Linear object to hold point data + channel name
public class Linear implements TripleContainer {

  //our fields
  private List<Triple> points;
  private String name;

  public Linear(String channelName) {
    this(channelName, new ArrayList<>());
  }

  public Linear(String channelName, List<Triple> data) {
    name = channelName;
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

  //Some getters + setters
  public List<Triple> getPoints() {
    return points;
  } //TODO: replace with iterator

  public String getName() {
    return name;
  }

  @Override
  public void makeRelative() {
    Triple mins = getMin();
    translate(-mins.getX(), -mins.getY(), -mins.getZ());
  }

  public void translate(int xOffset, int yOffset, int zOffset) {
    for (Triple pt : points) {
      pt.setX(pt.getX() + xOffset);
      pt.setY(pt.getY() + yOffset);
      pt.setZ(pt.getZ() + zOffset);
    }
  }
}
