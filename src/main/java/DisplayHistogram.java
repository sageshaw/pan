import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "PAN>Display histogram from currently loaded file...")
public class DisplayHistogram implements Command {

    @Parameter private LogService logService;

    @Override
    public void run() {

    }
}
