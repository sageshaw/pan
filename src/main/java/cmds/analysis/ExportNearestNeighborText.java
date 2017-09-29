package cmds.analysis;

import analysis.data.Linear;
import analysis.data.OperablePointContainer;
import analysis.data.PointContainer;
import analysis.ops.LinearNearestNeighbor;
import cmds.SingleChannelCommand;
import cmds.TextExportCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.util.Map;
import java.util.Set;

/**
 * Exports file with analysis data (double list)
 */
@Plugin(type = Command.class, menuPath = "PAN>Export analysis...>Nearest Neighbor")
public class ExportNearestNeighborText extends SingleChannelCommand implements TextExportCommand {

    @Override
    public void run() {
        TextExportCommand.super.run();
    }

    @Override
    public String getOutput() {
        String result = "Channel\tValue\r\n";

        Map <String, PointContainer> checkedChannels = getCheckedChannels();
        Set <String> channelNames = checkedChannels.keySet();

        OperablePointContainer channel;
        double[] nearestNeighborResult;

        for (String name : channelNames) {
            nearestNeighborResult = new LinearNearestNeighbor((Linear) checkedChannels.get(name)).process();
            for (double value : nearestNeighborResult) {
                result += name + "\t" + value + "\r\n";
            }
        }

        return result;
    }




}
