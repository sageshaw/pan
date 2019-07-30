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

@Plugin(type = Command.class /*, menuPath = "PAN>Analysis>Export analysis result as text file"*/)
public class ExportAnalysis extends DATATextExportCommand {


    @Override
    public String getOutput(String dataName, DataContainer dataset) {

        return dataset.header() + "\n" + dataset.body();

    }
}
