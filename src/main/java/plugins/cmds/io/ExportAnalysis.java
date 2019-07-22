package plugins.cmds.io;

import datastructures.analysis.DataContainer;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.Initializable;
import org.scijava.command.Command;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanService;

import java.util.ArrayList;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Export analysis result as text file")
public class ExportAnalysis extends TextExportCommand implements Initializable {

    @Parameter
    PanService panService;

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
    public String getOutput() {

        DataContainer dataset = panService.getAnalysisResult(dataName);
        return dataset.header() + "\n" + dataset.body();

    }
}
