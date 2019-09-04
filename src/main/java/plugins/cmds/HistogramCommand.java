package plugins.cmds;

import datastructures.graphs.HistogramDatasetPlus;
import ij.gui.GenericDialog;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.Initializable;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import plugins.PanService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class HistogramCommand extends DynamicCommand implements Initializable {

    // relates display names with actual keynames stored in PanService
    private HashMap<String, String> displayToHistoNames;

    @Parameter
    private LogService logService;

    @Parameter
    protected PanService panService;

    @Parameter(label = "Histogram data", choices = {"a", "b"})
    private String histoDisplayName;

    @Override
    public void initialize() {
        ArrayList<String> options = new ArrayList<>();

        String[] histoKeys = panService.histoKeys();

        if (histoKeys.length == 0) cancel("No histograms stored");


        displayToHistoNames = new HashMap<String, String>();
        for (String histoKey : histoKeys) {
            String displayName;
            HistogramDatasetPlus histoSet = panService.getHistoSet(histoKey);

            if (histoSet.isBatched()) {
                displayName = "[" + histoSet.getBatchKey() + "] " + histoKey;
            } else {
                displayName = new String(histoKey);
            }

            displayToHistoNames.put(displayName, histoKey);
            options.add(displayName);
        }

        MutableModuleItem<String> selectHistoItem = getInfo().getMutableInput("histoDisplayName", String.class);

        selectHistoItem.setChoices(options);
    }

    @Override
    public void run() {

        String histoName = displayToHistoNames.get(histoDisplayName);

        HistogramDatasetPlus histoData = panService.getHistoSet(histoName);

        setup(histoName, histoData);

        if (histoData.isBatched()) {
            GenericDialog batchDialogue = new GenericDialog("Found batch...");
            batchDialogue.addMessage("'" + histoName + "' was found to be in a batch. Run operation on entire batch?");
            batchDialogue.setOKLabel("Yes");
            batchDialogue.setCancelLabel("No");
            batchDialogue.showDialog();

            if (batchDialogue.wasOKed()) {
                List<HistogramDatasetPlus> histos = panService.getHistoBatch(histoData.getBatchKey());
                for (HistogramDatasetPlus h : histos) {
                    forEveryHistoDo(panService.histoKey(h), h, true);
                }

                end();

                return;
            }
        }

        forEveryHistoDo(histoName, histoData, false);

        end();
    }


    protected abstract void setup(String histoName, HistogramDatasetPlus histoData);

    protected abstract void forEveryHistoDo(String histoName, HistogramDatasetPlus histoData, boolean isBatched);

    protected abstract void end();

}
