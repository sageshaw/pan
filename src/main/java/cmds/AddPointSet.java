package cmds;

import display.DisplayImgGenerator;
import display.ImgGenerator;
import net.imagej.ImageJ;
import net.imglib2.img.display.imagej.ImageJFunctions;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.IOStorage;
import structs.ChannelContainer;
import structs.Linear;
import structs.Triple;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

@Plugin(type = Command.class, menuPath = "PAN>Add point set from text file...")
public class AddPointSet implements Command {

  // Ensure we are reading the correct type of text file by checking the first line (should always
  // be the same)
  private static final String CHECK_STRING =
      "Channel Name\tX\tY\tXc\tYc\tHeight\tArea\tWidth\tPhi\tAx\tBG\tI\tFrame\tLength\tLink\tValid\tZ\tZc\tPhotons\tLateral Localization Accuracy\tXw\tYw\tXwc\tYwc\tZw\tZwc";

  // To report errors (if necessary)
  @Parameter private LogService logService;

  // To obtain text file export from Nikon Elements
  @Parameter private File pointSet;

  @Parameter private IOStorage ptStore;

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
        String channelSetName = "set" + ptStore.channelSetSize();
        if (newData != null) ptStore.add(channelSetName, newData);
    }
  }

  // Data is delimited by tab characters. This 'function' (initiator function and recursive body)
  // can skip to the
  // nth tab of a provided string
  private int skipTabs(String line, int n) {
    return findNextTab(line, n, 0);
  }

  private int findNextTab(String line, int n, int currentPos) {
    if (n == 0) return currentPos - 1;
    return findNextTab(line, n - 1, line.indexOf('\t', currentPos) + 1);
  }

  // Parse file and load into channelSets list

    public ChannelContainer loadFile(File file) throws IllegalArgumentException, IOException {

    // read file data line by line into list(thank you Java 8!)
    ArrayList<String> rawInput = (ArrayList<String>) Files.readAllLines(file.toPath());

        ChannelContainer newChannels = new ChannelContainer();

    // Ensure we have the right filetype
    if (rawInput == null || !rawInput.get(0).equals(CHECK_STRING)) {
      throw new IllegalArgumentException(
          "Incorrect file content. Please ensure file was exported properly");
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
      channelName = line.substring(0, line.indexOf('\t'));
      // check if we have a channel named 'channelName' already in 'channelSets', if not, create
        if (newChannels.get(channelName) == null) {
            newChannels.add(channelName, new Linear());
      }
      // Find x,y,z value based on tab delimitation
      // Note: this may work for now, but these hardcoded values may need to be more flexible
      x =
          (int)
              (Double.parseDouble(line.substring(skipTabs(line, 4) + 1, skipTabs(line, 5))) + 0.5);
      y =
          (int)
              (Double.parseDouble(line.substring(skipTabs(line, 5) + 1, skipTabs(line, 6))) + 0.5);
      z =
          (int)
              (Double.parseDouble(line.substring(skipTabs(line, 13) + 1, skipTabs(line, 14)))
                  + 0.5);

        newChannels.get(channelName).add(new Triple(x, y, z));

    }

    newChannels.makeRelative();

    ImgGenerator imgGenerator = new DisplayImgGenerator(DisplayImgGenerator.PointMarker.plus);
    ImageJFunctions.show(imgGenerator.generate(newChannels));

    return newChannels;
  }
}
