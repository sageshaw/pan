package cmds;

import analysis.data.Linear;
import analysis.data.OperablePointContainer;
import analysis.data.PointContainer;
import analysis.ops.LinearNearestNeighbor;
import ij.gui.GenericDialog;
import ij.io.SaveDialog;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;

/**
 * Exports file with analysis data (double list)
 */
@Plugin(type = Command.class, menuPath = "PAN>Export analysis...")
public class ExportAnalysis extends OutputAnalysisCommand {


    @Override
    public void run() {
        //Get file directory to use, if user hits cancel, getFile will return null, and the command will
        //exit
        File export = getFile("");
        if (export == null) return;

        String result = "Channel\tValue\r\n";

        Map <String, PointContainer> checkedChannels = getCheckedChannels();
        Set <String> channelNames = checkedChannels.keySet();

        OperablePointContainer channel;
        double[] nearestNeighborResult;

        for (String name : channelNames) {
            nearestNeighborResult = new LinearNearestNeighbor((Linear) checkedChannels.get(name)).process();
            for (double value : nearestNeighborResult) {
                result += name + "\t" + value + "\r\n";
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(export.toPath())) {
            writer.write(result);
        } catch (IOException e) {

        }


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
