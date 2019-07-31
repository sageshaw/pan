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

@Plugin(type = Command.class, menuPath = "PAN>Export>Histogram ranges")
public class ExportHistogramRanges extends HISTOTextExportCommand {

    @Override
    public String getOutput(String histoName, HistogramDatasetPlus histoData) {
        return histoData.header() + System.lineSeparator() + histoData.body();
    }


}
