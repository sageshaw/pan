package plugins.cmds.io;

import datastructures.graphs.HistogramDatasetPlus;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.command.Command;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanService;
import plugins.cmds.HistogramCommand;

import java.util.ArrayList;

@Plugin(type = Command.class, menuPath = "PAN>Histogram>Export histogram as text file")
public class ExportHistogram extends HISTOTextExportCommand {

    @Override
    public String getOutput(String histoName, HistogramDatasetPlus histoData) {
        return histoData.header() + "\n" + histoData.body();
    }


}
