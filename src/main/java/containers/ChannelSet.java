package containers;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import java.util.ArrayList;
import java.util.Iterator;


/** Flyweight class for TripleContainers. */
public class ChannelSet extends OperableContainer {

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



  //TODO: Factor out drawing/image generation responsibilities to an image generation factory

  private Img<UnsignedByteType> blankImage() {
    Triple trplDims = getDimensions();
    int[] dims = new int[]{trplDims.getX()+1, trplDims.getY()+1, trplDims.getZ()+1};
    ImgFactory<UnsignedByteType> imageFactory = new ArrayImgFactory<>();
    Img<UnsignedByteType> img = imageFactory.create(dims, new UnsignedByteType());
    return img;
  }

  private void drawPoint(int x, int y, int z, Img<UnsignedByteType> img) {
    RandomAccess<UnsignedByteType> r = img.randomAccess();
    r.setPosition(x, 0);
    r.setPosition(y, 1);
    r.setPosition(z, 2);
    UnsignedByteType t = r.get();
    t.set(255);
  }

  private void drawX(int x, int y, int lineLength, Img<UnsignedByteType> img) {
    for (int i = 1; i < lineLength; i++) {
      try {
        drawPoint(x+i, y+i, 0, img);

      } catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("Point ("+x+","+y+") out of bounds. Skipping...");
      }
    }
    for (int i = 1; i < lineLength; i++) {
      try {
        drawPoint(x-i, y-i, 0, img);

      } catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("Point ("+x+","+y+") out of bounds. Skipping...");
      }
    }
    for (int i = 1; i < lineLength; i++) {
      try {
        drawPoint(x-i, y+i, 0, img);

      } catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("Point ("+x+","+y+") out of bounds. Skipping...");
      }
    }
    for (int i = 1; i < lineLength; i++) {
      try {
        drawPoint(x+i, y-i, 0, img);

      } catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("Point ("+x+","+y+") out of bounds. Skipping...");
      }
    }
  }

  public Img<UnsignedByteType> displayableImage() {
    Img<UnsignedByteType> img = blankImage();
    Iterator<Triple> channelItr;
    Triple pt;

    for (TripleContainer channel : channels) {
      channelItr = channel.iterator();
      while (channelItr.hasNext()) {
        pt = channelItr.next();
        drawX(pt.getX(), pt.getY(), 10, img);

      }

    }

    return img;
  }

  public Img<UnsignedByteType> image() {

    Img<UnsignedByteType> img = blankImage();
    Iterator<Triple> channelItr;
    Triple pt;

    for (TripleContainer channel : channels) {
      channelItr = channel.iterator();
      while (channelItr.hasNext()) {
        pt = channelItr.next();
        drawPoint(pt.getX(), pt.getY(), pt.getZ(), img);

      }
    }

    return img;
  }



}




