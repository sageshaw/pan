package plugins;

import constructs.ChannelSet;
import constructs.TripleContainer;
import net.imagej.ImageJService;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;

/*
Plugin service for all PointsANalysis operation. User interface is given through command plugins.
 */
@Plugin(type = ImageJService.class)
public class IOStorage extends AbstractPTService<ImageJService> implements ImageJService, Iterable {



  // master channel list
  private ArrayList<ChannelSet> channelSets;



  public IOStorage() {
    channelSets = new ArrayList<>();
  }

  public void addChannelSet(ChannelSet newChannelSet) {
    // TODO: temporary fix for image rendering implementation (only can work with one channelset at
    // a time)
    channelSets = new ArrayList<>();

    channelSets.add(newChannelSet);
  }

  public boolean remove(TripleContainer channelSet) {
    return channelSets.remove(channelSet);
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
    return channelSets.iterator();
  }
}
