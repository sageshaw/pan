package plugins;

import containers.*;
import net.imagej.ImageJService;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
Plugin service for all PointsANalysis operation. User interface is given through command plugins.
 */
@Plugin(type = ImageJService.class)
public class IOStorage extends AbstractPTService<ImageJService> implements ImageJService, Iterable {


  //master channel list
  private ArrayList<ChannelSet> channelSets;

  public IOStorage() {
    channelSets = new ArrayList<>();
  }

  public void addChannelSet(ChannelSet newChannelSet) {
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
