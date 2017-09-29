package cmds.analysis;

import analysis.data.OperablePointContainer;
import analysis.ops.CrossLinearNearestNeighbor;
import cmds.CrossChannelCommand;
import cmds.TextExportCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "PAN>Export analysis...>Cross-channel Nearest Neighbor")
public class ExportCrossNearestNeighborText extends CrossChannelCommand implements TextExportCommand {


    //TODO: Find a better way to resolve this conflict
    public void run() {
        TextExportCommand.super.run();
    }

    @Override
    public String getOutput() {


        String result = "FromChannel->ToChannel\tValue\r\n";

        OperablePointContainer from = getFromChannel();
        OperablePointContainer to = getToChannel();

        String fromName = getFromName();
        String toName = getToName();

        double[] nearestNeighborResult = new CrossLinearNearestNeighbor(from, to).execute();

        for (double value : nearestNeighborResult) {
            result += fromName + "->" + toName + "\t" + value + "\r\n";
        }

        return result;
    }


}
