package plugins;

import structs.ChannelSet;
import structs.PointContainer;
import net.imagej.ImageJService;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;

import java.util.HashMap;
import java.util.Iterator;

/*
Plugin service for all PointsANalysis operation. User interface is given through command plugins.
 */
@Plugin(type = ImageJService.class)
public class IOStorage extends AbstractPTService<ImageJService> implements ImageJService, Iterable {



  // master channel list
  private HashMap<String, ChannelSet> channelSets;



  public IOStorage() {
    channelSets = new HashMap <>();
  }

  public void addChannelSet(ChannelSet newChannelSet) {
    // TODO: temporary fix for image rendering implementation (only can work with one channelset at a time
    channelSets = new HashMap <>();

    channelSets.put(newChannelSet.getName(),newChannelSet);
  }

  public PointContainer remove(String name) {
    return channelSets.remove(name);
  }

  public PointContainer get(String name) {
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
}
