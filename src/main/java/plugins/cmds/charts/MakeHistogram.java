package plugins.cmds.charts;

import datastructures.analysis.DataContainer;
import datastructures.graphs.BatchableHistogramDataset;
import ij.gui.GenericDialog;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.Initializable;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.command.Previewable;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanService;

import java.util.ArrayList;

@Plugin(type = Command.class, menuPath = "PAN>Charts>Make Histogram")
public class MakeHistogram extends DynamicCommand implements Initializable {

    @Parameter
    private PanService panService;

    @Parameter(label = "Analysis result", choices = {"a", "b"})
    private String dataName;

    @Override
    public void initialize() {

        ArrayList<String> options = new ArrayList<>();

        String[] analysisResultKeys = panService.analysisResultKeys();

        if (analysisResultKeys.length == 0) throw new NullArgumentException();

        for (String analysisResultKey : analysisResultKeys)
            options.add(analysisResultKey);

        MutableModuleItem<String> selectDataItem = getInfo().getMutableInput("dataName", String.class);

        selectDataItem.setChoices(options);

    }


    @Override
    public void run() {
        //Grab dataset
        DataContainer dataset = panService.getAnalysisResult(dataName);

        //Build selection dialogue
        GenericDialog histoDialogue = new GenericDialog("Histogram...");
        histoDialogue.addMessage("Choose histogram parameters:");
        histoDialogue.addNumericField("Number of fields", 10, 3);
        histoDialogue.showDialog();

        //TODO: may have to multi-thread this
        while (!histoDialogue.wasOKed() && !histoDialogue.wasCanceled()) {
            BatchableHistogramDataset previewHisto = new BatchableHistogramDataset();
            previewHisto.addSeries(dataName, dataset.getData(), (int) histoDialogue.getNextNumber());

            //TODO add preview (make JFreeChart and show it)
            //TODO after while loop, finally exit command and load it into PanContext

        }
    }


}
