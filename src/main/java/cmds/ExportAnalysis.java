package cmds;

import cmds.gui.ChannelModuleItem;
import ij.gui.GenericDialog;
import ij.io.SaveDialog;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.List;

/**
 * Exports file with analysis data (double list)
 */
@Plugin(type = Command.class, menuPath = "PAN>Export analysis...")
public class ExportAnalysis extends OutputAnalysisCommand {


    @Override
    public void run() {
        List <ChannelModuleItem <Boolean>> checkedItems = getCheckedModules();

        //Get file directory to use, if user hits cancel, getFile will return null, and the command will
        //exit
        File export = getFile("");
        if (export == null) return;


    }

    private String getPath(String defaultName) {
        SaveDialog sd = new SaveDialog("Export Analysis", defaultName, ".txt");
        String path = sd.getDirectory() + sd.getFileName();
        if (path.equals("nullnull")) return null;
        return path;
    }

    private File getFile(String defaultName) {
        String path = getPath(defaultName);
        if (path == null) return null;
        File export = new File(path);
        if (export.exists()) {
            GenericDialog gd = new GenericDialog("Confirm Save");
            gd.addMessage("'" + export.getName() + "' already exists. Would you like to replace it?");
            gd.setOKLabel("Replace");
            gd.showDialog();
            if (gd.wasCanceled()) {
                return getFile(defaultName);
            }
        }

        return export;
    }


}
