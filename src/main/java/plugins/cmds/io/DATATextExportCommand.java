package plugins.cmds.io;

import datastructures.analysis.DataContainer;
import datastructures.graphs.HistogramDatasetPlus;
import ij.io.SaveDialog;
import plugins.cmds.DataCommand;
import plugins.cmds.HistogramCommand;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Interface providing basic framework for text output of analysis data.
 */
//TODO: generalize the container choice system, and reunify textexport
public abstract class DATATextExportCommand extends DataCommand {

    protected abstract String getOutput(String dataName, DataContainer dataset);

    private File export;

    private String content;

    @Override
    protected void setup(String dataName, DataContainer dataset) {

        //Get file directory to use, if user hits cancel, getFile will return null, and the command will
        //exit
        export = getFile("");
        if (export == null) return;

        content = "";
    }


    @Override
    protected void forEveryDatasetDo(String dataName, DataContainer dataset, boolean isBatched) {
        content += getOutput(dataName, dataset);

    }

    @Override
    protected void end() {

        try (BufferedWriter writer = Files.newBufferedWriter(export.toPath())) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private File getFile(String defaultName) {
        String path = getPath(defaultName);
        if (path == null) return null;
        File export = new File(path);


        return export;
    }

    private String getPath(String defaultName) {
        SaveDialog sd = new SaveDialog("Export Nearest Neighbor Analysis", defaultName, ".txt");
        String path = sd.getDirectory() + sd.getFileName();
        if (path.equals("nullnull")) return null;
        return path;
    }

}
