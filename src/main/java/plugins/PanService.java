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
public class PanService extends AbstractPTService<ImageJService> implements ImageJService, Iterable {


    // master channel list
    private BiMap<String, SuperPointContainer> channelSets;
    private BiMap<String, AnalysisContainer> results;

    private int numHistos;


    public PanService() {
        channelSets = HashBiMap.create();
        results = HashBiMap.create();
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

    public void addChannelSet(String name, SuperPointContainer container) { channelSets.put(name, container); }

    public SuperPointContainer removeChannelSet(String name) {
        return channelSets.remove(name);
    }

    public boolean removeChannelSet(SuperPointContainer value) { return channelSets.remove(channelSetKey(value), value); }

    public SuperPointContainer getChannelSet(String name) { return channelSets.get(name); }

    public int getNumChannelSets() { return channelSets.size(); }



    public String analysisResultKey(AnalysisContainer value) { return results.inverse().get(value);}

    public String[] analysisResultKeys() { return results.keySet().toArray(new String[0]); }

    public void addAnalysisResult(String name, AnalysisContainer result) { results.put(name, result); }

    public AnalysisContainer removeAnalysisResult(String name) {return results.remove(name); }

    public boolean removeAnalysisResult(AnalysisContainer value) {return results.remove(analysisResultKey(value), value); }

    public AnalysisContainer getAnalysisResult (String name) { return results.get(name); }

    public int getNumAnalysisResults() {
        return results.size();
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
