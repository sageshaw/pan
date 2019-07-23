package plugins.cmds;

import datastructures.analysis.DataContainer;
import ij.gui.GenericDialog;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.Initializable;
import org.scijava.command.DynamicCommand;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import plugins.PanService;

import java.util.ArrayList;
import java.util.List;

public abstract class DataCommand extends DynamicCommand implements Initializable {

    @Parameter
    protected PanService panService;

    @Parameter(label = "Analysis result", choices = {"a", "b"})
    private String dataName;

    @Override
    public void initialize() {
        ArrayList<String> options = new ArrayList<>();

        String[] dataKeys = panService.analysisResultKeys();

        if (dataKeys.length == 0) throw new NullArgumentException();

        for (String dataKey : dataKeys)
            options.add(dataKey);

        MutableModuleItem<String> selectDataItem = getInfo().getMutableInput("dataName", String.class);

        selectDataItem.setChoices(options);

    }

    @Override
    public void run() {

        DataContainer dataset = panService.getAnalysisResult(dataName);

        setup(dataName, dataset);

        if (dataset.isBatched()) {
            GenericDialog batchDialogue = new GenericDialog("Found batch...");
            batchDialogue.addMessage("'" + dataName + "' was found to be in a batch. Run operation on entire batch?");
            batchDialogue.setOKLabel("Yes");
            batchDialogue.setCancelLabel("No");
            batchDialogue.showDialog();

            if (batchDialogue.wasOKed()) {
                List<DataContainer> datasets = panService.getAnalysisBatch(dataset.getBatchKey());
                for (DataContainer d : datasets) {
                    forEveryDatasetDo(panService.analysisResultKey(d), d, true);

                }

                end();

                return;
            }

        }

        forEveryDatasetDo(dataName, dataset, false);

        end();
    }


    protected abstract void setup(String dataName, DataContainer dataset);

    protected abstract void forEveryDatasetDo(String dataName, DataContainer dataset, boolean isBatched);

    protected abstract void end();

}
