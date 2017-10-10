package plugins;

import analysis.data.MappedPointContainer;
import analysis.data.PointContainer;
import analysis.ops.OpScript;
import analysis.util.ClassUtilities;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.imagej.ImageJService;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;

import java.util.Iterator;
import java.util.List;

/**
 * Serves as a common context for all command plugins.
 */
@Plugin(type = ImageJService.class)
public class PanContext extends AbstractPTService <ImageJService> implements ImageJService, MappedPointContainer {


  // master channel list
  private BiMap <String, MappedPointContainer> channelSets;

    private int numHistos;

  public PanContext() {
    channelSets = HashBiMap.create();
      numHistos = 0;
  }

  public List <Class <?>> findOpScripts() {
    List <Class <?>> processes = null;
    try {
      processes = ClassUtilities.findAnnotatedClasses("analysis.ops", OpScript.class);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return processes;

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
      channelSets.put(name, (MappedPointContainer) container);
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

    public int getHistogramNumber() {
        return numHistos;
    }

    public void setHistogramNumber(int numHisto) {
        this.numHistos = numHisto;
    }
}
