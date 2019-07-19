package plugins;

import datastructures.Batchable;
import datastructures.graphs.BatchableHistogramDataset;
import datastructures.points.ChannelContainer;
import analysis.ops.OpScript;
import analysis.util.ClassUtilities;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import datastructures.analysis.DataContainer;
import net.imagej.ImageJService;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Serves as a common context for all command plugins.
 */
@Plugin(type = ImageJService.class)
public class PanService extends AbstractPTService<ImageJService> implements ImageJService {


    // master channel list
    private BiMap<String, ChannelContainer> channelSets;
    private BiMap<String, DataContainer> results;
    private BiMap<String, BatchableHistogramDataset> histoSets;

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

    public List<ChannelContainer> getChannelSetBatch(String batchKey) {

        List<ChannelContainer> batch = new ArrayList<>();

        for (ChannelContainer channelSet : channelSets.values()) {
            if (batchKey.equals(channelSet.getBatchKey()))
                batch.add(channelSet);

        }

        return batch;
    }


    public String analysisResultKey(DataContainer value) {
        return results.inverse().get(value);
    }

    public String[] analysisResultKeys() { return results.keySet().toArray(new String[0]); }

    public void addAnalysisResult(String name, DataContainer result) {
        results.put(name, result);
    }

    public DataContainer removeAnalysisResult(String name) {
        return results.remove(name);
    }

    public boolean removeAnalysisResult(DataContainer value) {
        return results.remove(analysisResultKey(value), value);
    }

    public DataContainer getAnalysisResult(String name) {
        return results.get(name);
    }

    public int getNumAnalysisResults() {
        return results.size();
    }

    public List<DataContainer> getAnalysisBatch(String batchKey) {

        List<DataContainer> batch = new ArrayList<>();

        for (DataContainer dataset : results.values()) {
            if (batchKey.equals(dataset.getBatchKey()))
                batch.add(dataset);

        }

        return batch;
    }


    public String histoKey(BatchableHistogramDataset value) {
        return histoSets.inverse().get(value);
    }

    public String[] histoKeys() {
        return histoSets.keySet().toArray(new String[0]);
    }

    public void addHistoSet(String name, BatchableHistogramDataset result) {
        histoSets.put(name, result);
    }

    public BatchableHistogramDataset removeHistoSet(String name) {
        return histoSets.remove(name);
    }

    public boolean removeHistoSet(BatchableHistogramDataset value) {
        return histoSets.remove(histoKey(value), value);
    }

    public BatchableHistogramDataset getHistoSet(String name) {
        return histoSets.get(name);
    }

    public int getNumHistoSets() {
        return histoSets.size();
    }

    public List<BatchableHistogramDataset> getHistoBatch(String batchKey) {

        List<BatchableHistogramDataset> batch = new ArrayList<>();

        for (BatchableHistogramDataset histoset : histoSets.values()) {
            if (batchKey.equals(histoset.getBatchKey()))
                batch.add(histoset);

        }

        return batch;
    }


    @Deprecated
    public int getHistogramNumber() {
        return numHistos;
    }

    @Deprecated
    public void setHistogramNumber(int numHisto) {
        this.numHistos = numHisto;
    }

    public Class<ImageJService> getPluginType() {
        return ImageJService.class;
    }

}
