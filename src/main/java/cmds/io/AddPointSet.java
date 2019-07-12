package cmds.io;

import datastructures.points.Triple;
import datastructures.points.ChannelContainer;
import datastructures.points.Linear;
import datastructures.points.OperablePointContainer;
import display.DisplayImgGenerator;
import display.ImgGenerator;
import net.imagej.ImageJ;
import net.imglib2.img.display.imagej.ImageJFunctions;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Command to handle .txt file input (export from Nikon elements)
 */
@Plugin(type = Command.class, menuPath = "PAN>Add point set from text file...")
public class AddPointSet implements Command {

  private static final String X_TITLE = "X";
  private static final String Y_TITLE = "Y";
  private static final String Z_TITLE = "Z";


  private int X;
  private int Y;
  private int Z;

  // Ensure we are reading the correct type of text file by checking the first line (should always
  // be the same)
  private static final String CHECK_STRING =
          "Channel Name\tX\tY\tXc\tYc\tHeight\tArea\tWidth\tPhi\tAx\tBG\tI\tFrame\tLength\tLink\tValid\tZ\tZc\tPhotons\tLateral Localization Accuracy\tXw\tYw\tXwc\tYwc\tZw\tZwc";

  // To report errors (if necessary)
  @Parameter private LogService logService;

  // To obtain text file export from Nikon Elements
  @Parameter(label = "Exported .txt file from Nikon Elements")
  private File pointSet;

  @Parameter(label = "Crop image to only fit dataset")
  private boolean isRelative;

  @Parameter(label = "Show image render of dataset")
  private boolean display;

    @Parameter
    private PanContext ptStore;

  // STRICTLY for testing purposes
  public static void main(final String... args) {
    // Launch ImageJ as usual.
    final ImageJ ij = net.imagej.Main.launch(args);
  }

  @Override
  public void run() {

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

      if (newData != null) ptStore.addChannelSet(channelSetName, newData);
    }
  }

  private String addPostDuplicateString(String name) {
    int duplNum = 1;
    boolean hasDuplicate = false;
    for (String loadedName : ptStore.channelSetKeys()) {
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
    ArrayList<String> rawInput = (ArrayList<String>) Files.readAllLines(file.toPath());

    ChannelContainer <OperablePointContainer> newChannels = new ChannelContainer();

    // Ensure we have the right filetype
    if (rawInput == null || !rawInput.get(0).equals(CHECK_STRING)) {
      throw new IllegalArgumentException(
              "Incorrect file content. Please ensure file was exported properly");
    }

    String[] categories = rawInput.get(0).split("\t");

    for (int i = 0; i < categories.length; i++) {
      switch (categories[i]) {
        case X_TITLE:
          X = i;
          break;
        case Y_TITLE:
          Y = i;
          break;
        case Z_TITLE:
          Z = i;
          break;
        default:
          break;
      }
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

      ((Linear) newChannels.get(channelName)).add(new Triple(x, y, z));

    }
    return newChannels;
  }
}
