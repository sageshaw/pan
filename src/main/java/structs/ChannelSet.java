package structs;

import display.Displayable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/** Flyweight class for TripleContainers. */
public class ChannelSet extends OperableContainer implements Displayable{

  private HashMap<String, OperableContainer> channels = new HashMap <>();

  public ChannelSet(String name) {
    super(name);
  }

  @Override
  public Triple getCentroid() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Triple getDimensions() {
    Triple mins = getMin();
    Triple maxes = getMax();

    return new Triple(maxes.getX()-mins.getX(), maxes.getY()-mins.getY(), maxes.getZ()-mins.getZ());
  }

  @Override
  public List<Triple> getData() {

    ArrayList<Triple> data = new ArrayList <>();

    for (PointContainer channel : channels.values()) {
      Iterator<Triple> itr = channel.iterator();
      while(itr.hasNext()) data.add(itr.next());

    }

    return data;
  }

  @Override
  public Triple getMin() {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int minZ = Integer.MAX_VALUE;

    Triple mins;

    for (OperableContainer channel : channels.values()) {
      mins = channel.getMin();
      minX = Math.min(mins.getX(), minX);
      minY = Math.min(mins.getY(), minY);
      minZ = Math.min(mins.getZ(), minZ);
    }

    return new Triple(minX, minY, minZ);
  }

  @Override
  public Triple getMax() {
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    int maxZ = Integer.MIN_VALUE;
    Triple maxes;
    for (OperableContainer channel : channels.values()) {
      maxes = channel.getMax();
      maxX = Math.max(maxes.getX(), maxX);
      maxY = Math.max(maxes.getY(), maxY);
      maxZ = Math.max(maxes.getZ(), maxZ);
    }
    return new Triple(maxX, maxY, maxZ);
  }

  @Override
  public double[] getNearestNeighborAnalysis() {
    throw new UnsupportedOperationException("Specify specific channel, not group");
  }

  public PointContainer getChannel(String name) {
    return channels.get(name);
  }

  public PointContainer removeChannel(String name) {
    for (int i = 0; i < channels.size(); i++) {
      if (channels.get(i).getName().equals(name)) return channels.remove(i);
    }

    return null;
  }

  @Override
  public void makeRelative() {
    Triple mins = getMin();
    translate(-mins.getX(), -mins.getY(), -mins.getZ());
  }

  @Override
  public void translate(int xOffset, int yOffst, int zOffset) {
    for (PointContainer channel : channels.values()) {
      channel.translate(xOffset, yOffst, zOffset);
    }
  }

  //TODO: Figure out how to use generics for this
 @Override
  public void add(Object e) {
    channels.put(((PointContainer)e).getName(), (OperableContainer)e);
  }

  @Override
  public Iterator <OperableContainer> iterator() {
    return channels.values().iterator();
  }






}




