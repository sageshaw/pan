package containers;

import java.util.ArrayList;
import java.util.Iterator;

/** Flyweight class for TripleContainers. */
public class ChannelSet extends TripleContainer {

  private ArrayList<TripleContainer> channels = new ArrayList<>();

  public ChannelSet(String name) {
    super(name);
  }

  @Override
  public Triple getMin() {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int minZ = Integer.MAX_VALUE;

    Triple mins;

    for (TripleContainer channel : channels) {
      mins = channel.getMin();
      minX = Math.min(mins.getX(), minX);
      minY = Math.min(mins.getY(), minY);
      minZ = Math.min(mins.getZ(), minZ);
    }

    return new Triple(minX, minY, minZ);
  }

  public TripleContainer getChannel(String name) {
    for (TripleContainer channel : channels) {
      if (channel.getName().equals(name)) return channel;
    }

    return null;
  }

  public TripleContainer removeChannel(String name) {
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
    for (TripleContainer channel : channels) {
      channel.translate(xOffset, yOffst, zOffset);
    }
  }

  //TODO: Figure out how to use generics for this

  public void add(Object element) {
    channels.add((TripleContainer) element);
  }

  @Override
  public Iterator<TripleContainer> iterator() {
    return channels.iterator();
  }
}
