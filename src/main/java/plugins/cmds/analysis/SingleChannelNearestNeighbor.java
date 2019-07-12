package plugins.cmds.analysis;

import analysis.ops.BiLinearNearestNeighbor;
import analysis.ops.BiOperation;
import datastructures.points.OperablePointContainer;
import datastructures.points.PointContainer;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import plugins.cmds.UniChannelCommand;

import java.util.Map;
import java.util.Set;

@Plugin(type = Command.class, menuPath = "PAN>Single-Channel Analysis>Nearest Neighbor")
public class SingleChannelNearestNeighbor extends UniChannelCommand {



    @Override
    public void run() {
        BiOperation operation = new BiLinearNearestNeighbor();
        //TODO: this should be OperablePointContainer, FIX
        Map<String, PointContainer> checkedChannels = getCheckedChannels();
        Set<String> channelNames = checkedChannels.keySet();

        for (String name : channelNames) {
            OperablePointContainer channel = (OperablePointContainer) checkedChannels.get(name);
            operation.setChannel(channel, channel);

            /*TODO: this is where I left off: need to add Map capaibilities to AnalysisContainer to
               hold analysis results from multiple channels. FINISH implementing NN for cross-channel analysis as well
            */

        }

    }


    @Override
    protected boolean allowAnalysisSelection() {
        return false;
    }
}
