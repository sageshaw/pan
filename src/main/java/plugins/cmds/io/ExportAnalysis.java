package plugins.cmds.io;

import datastructures.analysis.DataContainer;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

@Deprecated
@Plugin(type = Command.class /*, menuPath = "PAN>Analysis>Export analysis result as text file"*/)
public class ExportAnalysis extends DATATextExportCommand {


    @Override
    public String getOutput(String dataName, DataContainer dataset) {

        return dataset.csvHeader() + System.lineSeparator() + dataset.csvBody();

    }
}
