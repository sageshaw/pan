package plugins;

import analysis.data.ChannelContainer;
import analysis.data.MappedPointContainer;
import analysis.data.PointContainer;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.imagej.ImageJService;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;

import java.util.Iterator;

/*
Plugin service for all PointsANalysis operation. User interface is given through command plugins.
 */
@Plugin(type = ImageJService.class)
public class IOStorage extends AbstractPTService <ImageJService> implements ImageJService, Iterable, MappedPointContainer {



  // master channel list
  private BiMap <String, MappedPointContainer> channelSets;



  public IOStorage() {
    channelSets = HashBiMap.create();
  }

  @Override
  public String key(PointContainer value) {
    return channelSets.inverse().get(value);
  }

  @Override
  public String[] keys() {
    return channelSets.keySet().toArray(new String[0]);
  }

  @Override
  public void add(String name, PointContainer container) {
    // TODO: temporary fix for image rendering implementation (only can work with one channelset at a time
    channelSets = HashBiMap.create();

    channelSets.put(name, (ChannelContainer) container);
  }

  @Override
  public MappedPointContainer remove(String name) {
    return channelSets.remove(name);
  }

  @Override
  public boolean remove(PointContainer value) {
    return channelSets.remove(key(value), value);
  }

  @Override
  public MappedPointContainer get(String name) {
    return channelSets.get(name);
  }

  public int channelSetSize() {
    return channelSets.size();
  }

  @Override
  public Class<ImageJService> getPluginType() {
    return ImageJService.class;
  }

  /**
   * Returns an iterator over elements of type {@code T}.
   *
   * @return an Iterator.
   */
  @Override
  public Iterator iterator() {
    return channelSets.values().iterator();
  }

  @Override
  public void translate(int xOffset, int yOffset, int zOffset) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getSize() {
    return channelSets.size();
  }

  @Override
  public void makeRelative() {
    throw new UnsupportedOperationException();
  }
}
