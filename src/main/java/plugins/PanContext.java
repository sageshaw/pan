package plugins;

import datastructures.points.SuperPointContainer;
import analysis.ops.OpScript;
import analysis.util.ClassUtilities;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import datastructures.postanalysis.AnalysisContainer;
import net.imagej.ImageJService;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;

import java.util.Iterator;
import java.util.List;

/**
 * Serves as a common context for all command plugins.
 */
@Plugin(type = ImageJService.class)
public class PanContext extends AbstractPTService<ImageJService> implements ImageJService, Iterable {


    // master channel list
    private BiMap<String, SuperPointContainer> channelSets;
    private BiMap<String, AnalysisContainer> results;

    private int numHistos;


    public PanContext() {
        channelSets = HashBiMap.create();
        numHistos = 0;
    }

    public List<Class<?>> findOpScripts() {
        List<Class<?>> processes = null;
        try {
            processes = ClassUtilities.findAnnotatedClasses("analysis.ops", OpScript.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return processes;

    }


    public String channelSetKey(SuperPointContainer value) {
        return channelSets.inverse().get(value);
    }


    public String[] channelSetKeys() {
        return channelSets.keySet().toArray(new String[0]);
    }


    public void addChannelSet(String name, SuperPointContainer container) {
        channelSets.put(name, (SuperPointContainer) container);
    }


    public SuperPointContainer removeChannelSet(String name) {
        return channelSets.remove(name);
    }


    public boolean removeChannelSet(SuperPointContainer value) {
        return channelSets.remove(channelSetKey(value), value);
    }


    public SuperPointContainer getChannelSet(String name) {
        return channelSets.get(name);
    }

    public int channelSetSize() {
        return channelSets.size();
    }


    public int getNumChannelSets() {
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

    public Class<ImageJService> getPluginType() {
        return ImageJService.class;
    }

}
