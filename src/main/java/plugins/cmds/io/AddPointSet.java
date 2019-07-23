package plugins.cmds.io;

import datastructures.points.ChannelContainer;
import display.DisplayImgGenerator;
import display.ImgGenerator;
import net.imagej.ImageJ;
import net.imglib2.img.display.imagej.ImageJFunctions;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;

/**
 * Command to handle .txt file input (export from Nikon elements)
 */
@Plugin(type = Command.class, menuPath = "PAN>Add point set from text file...")
public class AddPointSet extends TextImportCommand {


    // To obtain text file export from Nikon Elements
  @Parameter(label = "Exported .txt file from Nikon Elements")
  private File pointSet;

  @Parameter(label = "Crop image to only fit dataset")
  private boolean isRelative;

  @Parameter(label = "Show image render of dataset")
  private boolean display;

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
        String channelSetName = addPostDuplicateString(pointSet.getName().trim());



      //Reduce coordinates, preserve distance relationships since it makes computations easier (some things will not work
      // if this is not used
      if (isRelative) newData.makeRelative();
      //Generate a displayable image (for users only, not for analysis) and open on screen
      if (display) {
        ImgGenerator imgGenerator = new DisplayImgGenerator(DisplayImgGenerator.PointMarker.plus, 7);
        ImageJFunctions.show(imgGenerator.generate(newData));
      }

      if (newData != null) panService.addChannelSet(channelSetName, newData);
    }
  }

    // Parse file and load into channelSets list

}
