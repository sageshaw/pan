package cmds.io;

import datastructures.Triple;
import datastructures.ChannelContainer;
import datastructures.Linear;
import datastructures.ListPointContainer;
import datastructures.OperablePointContainer;
import display.DisplayImgGenerator;
import display.ImgGenerator;
import net.imagej.ImageJ;
import net.imagej.ops.Initializable;
import net.imglib2.img.display.imagej.ImageJFunctions;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import plugins.PanContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command to handle .txt file input (export from Nikon elements) with specification of proper field columns.
 * Currently unavailable due to limitations of the SciJava framework.
 */

@Deprecated
//@Plugin(type = Command.class, menuPath = "PAN>Dynamic Loading")
public class DynamicAddPointSet extends DynamicCommand implements Command, Initializable {


    private int X;
    private int Y;
    private int Z;

    // Ensure we are reading the correct type of text file by checking the first line (should always
    // be the same)
    private static final String CHECK_STRING =
            "Channel Name\t";

    // To report errors (if necessary)
    @Parameter
    private LogService logService;

    // To obtain text file export from Nikon Elements
    @Parameter(label = "Exported .txt file from Nikon Elements", persist = false, callback = "getXYZLabels")
    private File pointSet;

    @Parameter(label = "X Column Label", choices = {"select valid file first"})
    String xLabel;

    @Parameter(label = "Y Column Label", choices = {"select valid file first"})
    String yLabel;

    @Parameter(label = "Z Column label", choices = {"select valid file first"})
    String zLabel;

    @Parameter(label = "Crop image to only fit dataset")
    private boolean isRelative;

    @Parameter(label = "Show image render of dataset")
    private boolean display;

    @Parameter
    private PanContext ptStore;

    private boolean failedInit = false;


    public void initialize() {
        getInfo();
    }

    //TODO: implement stronger file checking
    private void getXYZLabels() {
        ArrayList <String> rawInput = null;

        try {
            rawInput = (ArrayList <String>) Files.readAllLines(pointSet.toPath());
        } catch (Exception e) {
            e.printStackTrace();
            failedInit = true;
        }

        if (rawInput == null || !rawInput.get(0).substring(0, CHECK_STRING.length()).equals(CHECK_STRING)) {
            failedInit = true;
            return;
        }

        String[] cats = rawInput.get(0).split("\t");
        List <String> options = Arrays.asList(Arrays.copyOfRange(cats, 1, cats.length));

        MutableModuleItem <String> xLabelItem = getInfo().getMutableInput("xLabel", String.class);
        MutableModuleItem <String> yLabelItem = getInfo().getMutableInput("yLabel", String.class);
        MutableModuleItem <String> zLabelItem = getInfo().getMutableInput("zLabel", String.class);

        xLabelItem.setChoices(options);
        yLabelItem.setChoices(options);
        zLabelItem.setChoices(options);

        failedInit = false;
    }

    // STRICTLY for testing purposes
    public static void main(final String... args) {
        // Launch ImageJ as usual.
        final ImageJ ij = net.imagej.Main.launch(args);
    }

    @Override
    public void run() {

        if (failedInit) return;
        // ensure we have a text file
        String name = pointSet.getName();
        if (name.toLowerCase().lastIndexOf(".txt") != name.length() - 4) {
            logService.error(new IllegalArgumentException("Please specify a file with .txt extension"));
            return;
        }

        ChannelContainer newData = null;

        // attempt to load dataset into plugin memory
        try {
            newData = loadFile(pointSet);
        } catch (Exception err) {
            logService.error(err);
        } finally {

            //Grab name, and label duplicate if necessary
            String channelSetName = addPostDuplicateString(pointSet.getName());


            //Reduce coordinates, preserve distance relationships since it makes computations easier (some things will not work
            // if this is not used
            if (isRelative) newData.makeRelative();
            //Generate a displayable image (for users only, not for analysis) and open on screen
            if (display) {
                ImgGenerator imgGenerator = new DisplayImgGenerator(DisplayImgGenerator.PointMarker.plus, 7);
                ImageJFunctions.show(imgGenerator.generate(newData));
            }

            if (newData != null) ptStore.add(channelSetName, newData);
        }
    }

    private String addPostDuplicateString(String name) {
        int duplNum = 1;
        boolean hasDuplicate = false;
        for (String loadedName : ptStore.keys()) {
            if (loadedName.contains(name)) {
                hasDuplicate = true;
                if (!loadedName.equals(name)) {
                    int currentNum = Integer.parseInt(loadedName.substring(loadedName.length() - 2, loadedName.length() - 1));
                    if (currentNum >= duplNum) {
                        duplNum = currentNum + 1;
                    }
                }
            }
        }

        if (hasDuplicate) {
            name += "(" + duplNum + ")";
        }

        return name;
    }

    // Parse file and load into channelSets list

    public ChannelContainer loadFile(File file) throws IllegalArgumentException, IOException {

        // read file data line by line into list(thank you Java 8!)
        ArrayList <String> rawInput = (ArrayList <String>) Files.readAllLines(file.toPath());

        ChannelContainer <OperablePointContainer> newChannels = new ChannelContainer();

        String[] categories = rawInput.get(0).split("\t");

        for (int i = 0; i < categories.length; i++) {

            if (categories[i].equals(xLabel)) X = i;
            else if (categories[i].equals(yLabel)) Y = i;
            else if (categories[i].equals(zLabel)) Z = i;

        }

        // some initialization so we don't need to constantly discard references
        String line;
        String channelName = null;

        int x;
        int y;
        int z;

        // parse the rest of the file and load points into channelSets list
        for (int i = 1; i < rawInput.size(); i++) {
            // grab line, take channel name
            line = rawInput.get(i);

            String[] splitLine = line.split("\t");

            channelName = splitLine[0];
            // check if we have a channel named 'channelName' already in 'channelSets', if not, create
            if (newChannels.get(channelName) == null) {
                newChannels.add(channelName, new Linear <Triple>());
            }
            // Find x,y,z value based on tab delimitation
            // Note: this may work for now, but these hardcoded values may need to be more flexible
            x =
                    (int)
                            (Double.parseDouble(splitLine[X]) + 0.5);
            y =
                    (int)
                            (Double.parseDouble(splitLine[Y]) + 0.5);
            z =
                    (int)
                            (Double.parseDouble(splitLine[Z]) + 0.5);

            ((ListPointContainer) newChannels.get(channelName)).add(new Triple(x, y, z));

        }
        return newChannels;
    }
}

