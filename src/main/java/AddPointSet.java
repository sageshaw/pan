import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;

@Plugin(type = Command.class, menuPath = "PAN>Add point set from text file...")
public class AddPointSet implements Command {

  //To report errors (if necessary)
  @Parameter private LogService logService;

  //To obtain text file (must be output from Nikon software) TODO: get software name
  @Parameter private File pointSet;

  //STRICTLY for testing purposes
  public static void main(final String... args) {
    // Launch ImageJ as usual.
    final ImageJ ij = net.imagej.Main.launch(args);

    // Launch command right away
    ij.command().run(AddPointSet.class, true);
  }

  @Override
  public void run() {

    //FOR TESTING PURPOSES ONLY - IN PRODUCTION, PAN WILL BE RECEIVED FROM CONTEXT
    Pan pan = new Pan();

    //ensure we have a text file
    String name = pointSet.getName();
    if (name.toLowerCase().lastIndexOf(".txt") != name.length() - 4) {
      logService.error(new IllegalArgumentException("Please specify a file with .txt extension"));
      return;
    }

    //attempt to load dataset into plugin memory
    try {
      pan.loadFile(pointSet);
    } catch (Exception err) {
      logService.error(err);
    }
  }
}
