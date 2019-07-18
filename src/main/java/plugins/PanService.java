package plugins;

import datastructures.points.ChannelContainer;
import datastructures.points.SuperPointContainer;
import analysis.ops.OpScript;
import analysis.util.ClassUtilities;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import datastructures.postanalysis.AnalysisContainer;
import net.imagej.ImageJService;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Serves as a common context for all command plugins.
 */
@Plugin(type = ImageJService.class)
public class PanService extends AbstractPTService<ImageJService> implements ImageJService {


    // master channel list
    private BiMap<String, ChannelContainer> channelSets;
    private BiMap<String, AnalysisContainer> results;

    private int numHistos;


    public PanService() {
        channelSets = HashBiMap.create();
        results = HashBiMap.create();
        numHistos = 0;
    }

    @Deprecated
    public List<Class<?>> findOpScripts() {
        List<Class<?>> processes = null;
        try {
            processes = ClassUtilities.findAnnotatedClasses("analysis.ops", OpScript.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return processes;

    }


    public String channelSetKey(ChannelContainer value) {
        return channelSets.inverse().get(value);
    }

    public String[] channelSetKeys() {
        return channelSets.keySet().toArray(new String[0]);
    }

    public void addChannelSet(String name, ChannelContainer container) {
        channelSets.put(name, container);
    }

    public ChannelContainer removeChannelSet(String name) {
        return channelSets.remove(name);
    }

    public boolean removeChannelSet(ChannelContainer value) {
        return channelSets.remove(channelSetKey(value), value);
    }

    public ChannelContainer getChannelSet(String name) {
        return channelSets.get(name);
    }

    public int getNumChannelSets() { return channelSets.size(); }

    public List<ChannelContainer> getBatch(String batchKey) {

        List<ChannelContainer> batch = new ArrayList<>();

        for (ChannelContainer channelSet : channelSets.values()) {
            if (batchKey.equals(channelSet.getBatchKey()))
                batch.add(channelSet);

        }

        return batch;
    }




    public String analysisResultKey(AnalysisContainer value) { return results.inverse().get(value);}

    public String[] analysisResultKeys() { return results.keySet().toArray(new String[0]); }

    public void addAnalysisResult(String name, AnalysisContainer result) { results.put(name, result); }

    public AnalysisContainer removeAnalysisResult(String name) {return results.remove(name); }

    public boolean removeAnalysisResult(AnalysisContainer value) {return results.remove(analysisResultKey(value), value); }

    public AnalysisContainer getAnalysisResult (String name) { return results.get(name); }

    public int getNumAnalysisResults() {
        return results.size();
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
