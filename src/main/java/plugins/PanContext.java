package plugins;

import datastructures.SuperPointContainer;
import datastructures.PointContainer;
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
public class PanContext extends AbstractPTService <ImageJService> implements ImageJService, Iterable {


  // master channel list
  private BiMap <String, SuperPointContainer> channelSets;

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


  public String key(PointContainer value) {
    return channelSets.inverse().get(value);
  }


  public String[] keys() {
    return channelSets.keySet().toArray(new String[0]);
  }


  public void add(String name, PointContainer container) {
      channelSets.put(name, (SuperPointContainer) container);
  }


  public SuperPointContainer remove(String name) {
    return channelSets.remove(name);
  }


  public boolean remove(PointContainer value) {
    return channelSets.remove(key(value), value);
  }


  public SuperPointContainer get(String name) {
    return channelSets.get(name);
  }

  public int channelSetSize() {
    return channelSets.size();
  }


    public Class<ImageJService> getPluginType() {
    return ImageJService.class;
  }

    public int getSize() {
        return channelSets.size();
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

    public int getHistogramNumber() {
        return numHistos;
    }

    public void setHistogramNumber(int numHisto) {
        this.numHistos = numHisto;
    }
}
