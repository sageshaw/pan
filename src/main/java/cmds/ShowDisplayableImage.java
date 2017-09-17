package cmds;

import analysis.data.PointContainer;
import display.DisplayImgGenerator;
import display.Displayable;
import display.ImgGenerator;
import net.imglib2.img.display.imagej.ImageJFunctions;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.Map;

@Plugin(type = Command.class, menuPath = "PAN>Render image from dataset...")
public class ShowDisplayableImage extends DynamicOutputCommand {

    @Parameter(label = "Point Marker Size")
    int markerSize = 10;

    public void run() {
        Map <String, PointContainer> checkedChannels = getCheckedChannels();
        ImgGenerator imgGenerator = new DisplayImgGenerator(DisplayImgGenerator.PointMarker.plus, markerSize);
        ImageJFunctions.show(imgGenerator.generate(checkedChannels.values().toArray(new Displayable[0])));
    }
}
