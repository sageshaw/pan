package plugins.cmds.io;

import datastructures.points.ChannelContainer;
import ij.gui.GenericDialog;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

@Plugin(type = Command.class, menuPath = "PAN>Import>Add point set batch by selection")
public class AddPointSetBatchSelect extends TextImportCommand {


    @Override
    public void run() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int returnVal = chooser.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            //Ask to make data relative
            GenericDialog relativeDialog = new GenericDialog("Crop data...");
            relativeDialog.addMessage("Crop images to fit dataset?");
            relativeDialog.setOKLabel("Yes");
            relativeDialog.setCancelLabel("No");
            relativeDialog.showDialog();

            File[] files = chooser.getSelectedFiles();

            if (panService.isCurrentBatchNameUsed()) panService.uptickBatchName();
            String batchKey = panService.getCurrentBatchName();

            for (File file : files) {
                ChannelContainer newData = null;
                try {
                    newData = loadFile(file);    //Load the file
                } catch (IOException e) {
                    logService.error(e);

                } finally {
                    String channelSetName = file.getName().trim(); //Extract data and store in PanService

                    if (relativeDialog.getNextBoolean()) newData.makeRelative();
                    newData.setBatchKey(batchKey);
                    panService.addChannelSet(channelSetName, newData);

                }
            }
        }


    }
}
