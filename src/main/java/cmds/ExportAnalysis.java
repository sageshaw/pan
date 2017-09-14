package cmds;

import cmds.gui.ChannelModuleItem;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.nio.file.Path;
import java.util.List;

/**
 * Exports file with analysis data (double list)
 */
@Plugin(type = Command.class, menuPath = "PAN>Export analysis...")
public class ExportAnalysis extends OutputAnalysisCommand {

    @Parameter
    private Path path;

    @Override
    public void run() {
        List <ChannelModuleItem <Boolean>> checkedItems = getCheckedModules();


    }


}
