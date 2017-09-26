package cmds.analysis;

import analysis.data.ListPointContainer;
import analysis.data.OperablePointContainer;
import analysis.ops.CrossLinearNearestNeighbor;
import cmds.DynamicOutputDoubleChannel;
import ij.io.SaveDialog;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Plugin(type = Command.class, menuPath = "PAN>Export analysis...>")
public class ExportCrossNearestNeighborText extends DynamicOutputDoubleChannel {


    public void run() {
        //Get file directory to use, if user hits cancel, getFile will return null, and the command will
        //exit
        File export = getFile("");
        if (export == null) return;

        String result = "FromChannel->ToChannel\tValue\r\n";

        OperablePointContainer from = getFromChannel();
        OperablePointContainer to = getToChannel();

        String fromName = getFromName();
        String toName = getToName();

        double[] nearestNeighborResult = new CrossLinearNearestNeighbor((ListPointContainer) from, (ListPointContainer) to).execute();

        for (double value : nearestNeighborResult) {
            result += fromName + "->" + toName + "\t" + value + "\r\n";
        }


        try (BufferedWriter writer = Files.newBufferedWriter(export.toPath())) {
            writer.write(result);
        } catch (IOException e) {

        }


    }


    private String getPath(String defaultName) {
        SaveDialog sd = new SaveDialog("Export Nearest Neighbor Analysis", defaultName, ".txt");
        String path = sd.getDirectory() + sd.getFileName();
        if (path.equals("nullnull")) return null;
        return path;
    }

    private File getFile(String defaultName) {
        String path = getPath(defaultName);
        if (path == null) return null;
        File export = new File(path);
//        if (export.exists()) {
//            GenericDialog gd = new GenericDialog("Confirm Save");
//            gd.addMessage("'" + export.getName() + "' already exists. Would you like to replace it?");
//            gd.setOKLabel("Replace");
//            gd.showDialog();
//            if (gd.wasCanceled()) {
//                return getFile(defaultName);
//            }
//        }

        return export;
    }
}
