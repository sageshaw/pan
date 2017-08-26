import containers.Linear;
import containers.ChannelSet;
import containers.Triple;
import containers.TripleContainer;
import org.scijava.plugin.Plugin;
import org.scijava.service.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

/*
Plugin service for all PointsANalysis operation. User interface is given through command plugins.

TODO: Allow for operation on multiple point datasets from different textfiles (introduce linking?)
 */
@Plugin(type = Service.class)
public class Pan {

  //Ensure we are reading the correct type of text file by checking the first line (should always be the same)
  private static final String CHECK_STRING =
      "Name\tX\tY\tXc\tYc\tHeight\tArea\tWidth\tPhi\tAx\tBG\tI\tFrame\tLength\tLink\tValid\tZ\tZc\tPhotons\tLateral Localization Accuracy\tXw\tYw\tXwc\tYwc\tZw\tZwc";

  //master channel list
  private ArrayList<ChannelSet> channelSets;

  public Pan() {
    channelSets = new ArrayList<>();
  }


  //Data is delimited by tab characters. This 'function' (initiator function and recursive body) can skip to the
  //nth tab of a provided string
  private int skipTabs(String line, int n) {
    return findNextTab(line, n, 0);
  }

  private int findNextTab(String line, int n, int currentPos) {
    if (n == 0) return currentPos - 1;
    return findNextTab(line, n - 1, line.indexOf('\t', currentPos) + 1);
  }

  //Parse file and load into channelSets list
  public void loadFile(File file) throws IllegalArgumentException, IOException {

    //read file data line by line into list(thank you Java 8!)
    ArrayList<String> rawInput = (ArrayList<String>) Files.readAllLines(file.toPath());

    ChannelSet newChannels = new ChannelSet("set" + channelSets.size());

    //Ensure we have the right filetype
    if (rawInput == null || !rawInput.get(0).equals(CHECK_STRING)) {
      throw new IllegalArgumentException(
          "Incorrect file content. Please ensure file was exported properly");
    }
    //some initialization so we don't need to constantly discard references
    String line;
    String channelName;
    Iterator<TripleContainer> channelIterator;
    int x;
    int y;
    int z;


    //parse the rest of the file and load points into channelSets list
    for (int i = 1; i < rawInput.size(); i++) {
      //grab line, take channel name
      line = rawInput.get(i);
      channelName = line.substring(0, line.indexOf('\t'));
      //check if we have a channel named 'channelName' already in 'channelSets', if not, create
      if (newChannels.getChannel("channelName") == null) {
        newChannels.add(new Linear(channelName));
      }
      //Find x,y,z value based on tab delimitation
      //Note: this may work for now, but these hardcoded values may need to be more flexible
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


      channelIterator = newChannels.iterator();

      while (channelIterator.hasNext()) {

          TripleContainer channel = channelIterator.next();
          if (channel.getName().equals((channelName))) channel.add(new Triple(x, y, z));
      }

    }

    newChannels.makeRelative();

    channelSets.add(newChannels);

  }



}
