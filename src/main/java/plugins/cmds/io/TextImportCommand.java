package plugins.cmds.io;

import datastructures.points.ChannelContainer;
import datastructures.points.Linear;
import datastructures.points.OperablePointContainer;
import datastructures.points.Triple;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import plugins.PanService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public abstract class TextImportCommand implements Command {


    private static final String X_TITLE = "X";
    private static final String Y_TITLE = "Y";
    private static final String Z_TITLE = "Z";
    // Ensure we are reading the correct type of text file by checking the first line (should always
    // be the same)
    private static final String CHECK_STRING =
            "Channel Name\tX\tY\tXc\tYc\tHeight\tArea\tWidth\tPhi\tAx\tBG\tI\tFrame\tLength\tLink\tValid\tZ\tZc\tPhotons\tLateral Localization Accuracy\tXw\tYw\tXwc\tYwc\tZw\tZwc";
    // To report errors (if necessary)
    @Parameter
    protected LogService logService;
    @Parameter
    protected PanService panService;
    private int X;
    private int Y;
    private int Z;

    protected String addPostDuplicateString(String name) {
      int duplNum = 1;
      boolean hasDuplicate = false;
      for (String loadedName : panService.channelSetKeys()) {
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
          newChannels.add(channelName, new Linear<Triple>());
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
