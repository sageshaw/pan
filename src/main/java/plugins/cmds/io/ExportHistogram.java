package plugins.cmds.io;

import datastructures.graphs.HistogramDatasetPlus;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.Initializable;
import org.scijava.command.Command;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanService;

import java.util.ArrayList;

@Plugin(type = Command.class, menuPath = "PAN>Charts>Export histogram as text file")
public class ExportHistogram extends TextExportCommand implements Initializable {

    @Parameter
    PanService panService;

    @Parameter(label = "Histogram data", choices = {"a", "b"})
    private String histoName;


    @Override
    public void initialize() {
        ArrayList<String> options = new ArrayList<>();

        String[] histoKeys = panService.histoKeys();

        if (histoKeys.length == 0) throw new NullArgumentException();

        for (String histoKey : histoKeys)
            options.add(histoKey);

        MutableModuleItem<String> selectHistoItem = getInfo().getMutableInput("histoName", String.class);

        selectHistoItem.setChoices(options);
    }

    @Override
    public String getOutput() {

        HistogramDatasetPlus histo = panService.getHistoSet(histoName);
        return histo.header() + "\n" + histo.body();
    }

}
