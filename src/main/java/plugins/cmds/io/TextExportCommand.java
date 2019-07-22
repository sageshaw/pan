package plugins.cmds.io;

import ij.io.SaveDialog;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Interface providing basic framework for text output of analysis data.
 */
public abstract class TextExportCommand extends DynamicCommand {

    abstract String getOutput();

    public void run() {
        //Get file directory to use, if user hits cancel, getFile will return null, and the command will
        //exit
        File export = getFile("");
        if (export == null) return;

        String content = getOutput();

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
