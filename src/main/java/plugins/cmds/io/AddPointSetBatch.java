package plugins.cmds.io;

import datastructures.points.ChannelContainer;
import org.scijava.ItemIO;
import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.module.MethodCallException;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;

@Plugin(type = Command.class,menuPath = "PAN>Add point set batch from text file")
public class AddPointSetBatch extends TextImportCommand {


    @Parameter(label = "Crop images to fit dataset")
    private boolean isRelative;


    @Parameter(label="Exported .txt file from Nikon Elements", callback = "updateExpression")
    private File dummySet;

    @Parameter(label="File format", initializer = "initExpression", callback="updateFilesFound", persist = false)
    String searchExpression;

    @Parameter(label="Files found", visibility = ItemVisibility.MESSAGE)
    int filesFound;


    private void initExpression() {
        searchExpression = "";
    }

    private void updateExpression() {
        String name = "";

        try {
            name = dummySet.getName();
        } catch (NullPointerException e) {
            updateExpression(); //TODO: dangerous, find a better solution
        }
        searchExpression = name;
    }

    //Note: callback does not trigger on autofill, so user does need to modify directory to
    //get file found numbers to show
    private void updateFilesFound() {
        //build pattern and matcher
        int total = 0;
        String pattern = "**" + searchExpression + "*";
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        //loop through all files in current directory to see if they match the pattern
        for (File file : dummySet.getParentFile().listFiles()) {
            if(matcher.matches(file.toPath()))
                total++;
        }

        filesFound = total;
    }

    @Override
    public void run() {
        //Find files that match the search expression, extract ChannelSet, then give to PanContext
        String pattern = "**" + searchExpression + "*";
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

        for (File file : dummySet.getParentFile().listFiles()) {
            if(matcher.matches(file.toPath())) {
                ChannelContainer newData = null;
                try {
                    newData = loadFile(file);
                } catch (IOException e) {
                    logService.error(e);

                } finally {
                    String channelSetName = addPostDuplicateString(file.getName());
                    if(isRelative) newData.makeRelative();
                    ptStore.addChannelSet(channelSetName, newData);

                }



            }
        }



    }


}
