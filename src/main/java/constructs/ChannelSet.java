package constructs;

import images.Displayable;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/** Flyweight class for TripleContainers. */
public class ChannelSet extends OperableContainer implements Displayable{

  private ArrayList <OperableContainer> channels = new ArrayList <>();

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

    for (TripleContainer channel : channels) {
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

    for (OperableContainer channel : channels) {
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
    for (OperableContainer channel : channels) {
      maxes = channel.getMax();
      maxX = Math.max(maxes.getX(), maxX);
      maxY = Math.max(maxes.getY(), maxY);
      maxZ = Math.max(maxes.getZ(), maxZ);
    }
    return new Triple(maxX, maxY, maxZ);
  }

  @Override
  public double[] getNearestNeighborAnalysis() {
    return new double[0];
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
    channels.add((OperableContainer) element);
  }

  @Override
  public Iterator <OperableContainer> iterator() {
    return channels.iterator();
  }






}




