package plugins;

import datastructures.graphs.HistogramDatasetPlus;
import datastructures.points.ChannelContainer;
import analysis.ops.OpScript;
import analysis.util.ClassUtilities;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import datastructures.analysis.DataContainer;
import net.imagej.ImageJService;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;

import java.util.*;

/**
 * Serves as a common context for all command plugins.
 */
@Plugin(type = ImageJService.class)
public class PanService extends AbstractPTService<ImageJService> implements ImageJService {


    // master channel list
    private BiMap<String, ChannelContainer> channelSets;
    private BiMap<String, DataContainer> analysisResults;
    private BiMap<String, HistogramDatasetPlus> histoSets;

    private int currentBatchNum;
    private boolean batchNameUsed;

    public PanService() {
        channelSets = HashBiMap.create();
        analysisResults = HashBiMap.create();
        histoSets = HashBiMap.create();
        currentBatchNum = 0;
        batchNameUsed = false;
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
        name = addNameDuplicateNumber(name, channelSets.keySet());
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

    public Map<String, ChannelContainer> getStringChannelSetBatchMap(String batchKey) {
        Map<String, ChannelContainer> batchMap = new HashMap<String, ChannelContainer>();

        for (ChannelContainer channelSet : channelSets.values()) {
            if (batchKey.equals(channelSet.getBatchKey())) {
                batchMap.put(channelSetKey(channelSet), channelSet);
            }
        }

        return batchMap;
    }


    public boolean isCurrentBatchNameUsed() {
        return batchNameUsed;
    }

    public String getCurrentBatchName() {
        batchNameUsed = true;
        return "Batch" + currentBatchNum;
    }

    public void uptickBatchName() {
        currentBatchNum++;
        batchNameUsed = false;
    }




    public String analysisResultKey(DataContainer value) {
        return analysisResults.inverse().get(value);
    }

    public String[] analysisResultKeys() {
        return analysisResults.keySet().toArray(new String[0]);
    }

    public void addAnalysisResult(String name, DataContainer result) {

        name = addNameDuplicateNumber(name, analysisResults.keySet());

        analysisResults.put(name, result);
    }

    public DataContainer removeAnalysisResult(String name) {
        return analysisResults.remove(name);
    }

    public boolean removeAnalysisResult(DataContainer value) {
        return analysisResults.remove(analysisResultKey(value), value);
    }

    public DataContainer getAnalysisResult(String name) {
        return analysisResults.get(name);
    }

    public int getNumAnalysisResults() {
        return analysisResults.size();
    }

    public List<DataContainer> getAnalysisBatch(String batchKey) {

        List<DataContainer> batch = new ArrayList<>();

        for (DataContainer dataset : analysisResults.values()) {
            if (batchKey.equals(dataset.getBatchKey()))
                batch.add(dataset);

        }

        return batch;
    }


    public String histoKey(HistogramDatasetPlus value) {
        return histoSets.inverse().get(value);
    }

    public String[] histoKeys() {
        return histoSets.keySet().toArray(new String[0]);
    }

    public void addHistoSet(String name, HistogramDatasetPlus result) {
        name = addNameDuplicateNumber(name, histoSets.keySet());
        histoSets.put(name, result);
    }

    public HistogramDatasetPlus removeHistoSet(String name) {
        return histoSets.remove(name);
    }

    public boolean removeHistoSet(HistogramDatasetPlus value) {
        return histoSets.remove(histoKey(value), value);
    }

    public HistogramDatasetPlus getHistoSet(String name) {
        return histoSets.get(name);
    }

    public int getNumHistoSets() {
        return histoSets.size();
    }

    public List<HistogramDatasetPlus> getHistoBatch(String batchKey) {

        List<HistogramDatasetPlus> batch = new ArrayList<>();

        for (HistogramDatasetPlus histoset : histoSets.values()) {
            if (batchKey.equals(histoset.getBatchKey()))
                batch.add(histoset);

        }

        return batch;
    }


    private String addNameDuplicateNumber(String name, Collection<String> savedNames) {
        int duplNum = 1;
        boolean hasDuplicate = false;

        for (String savedName : savedNames) {
            if (savedName.contains(name)) {
                hasDuplicate = true;

                if (!savedName.equals(name)) {
                    int currentNum = Integer.parseInt(savedName.substring(savedName.length() - 2, savedName.length() - 1));
                    if (currentNum >= duplNum) {
                        duplNum = currentNum + 1;
                    }
                }
            }
        }

        if (hasDuplicate) {
            name += "(" + duplNum + ")";
        }

        return name;
    }


    public Class<ImageJService> getPluginType() {
        return ImageJService.class;
    }


}
