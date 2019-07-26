package plugins.cmds.analysis;

import analysis.ops.BiLinearNearestNeighbor;
import analysis.ops.BiOperation;
import datastructures.points.ChannelContainer;
import datastructures.analysis.DataContainer;
import datastructures.analysis.LinearData;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.List;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Cross-channel nearest neighbor")
public class CrossChannelNearestNeighbor extends BiChannelCommand {


    @Parameter
    LogService logService;


    @Override
    protected boolean setup(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName) {
        logService.info("Running cross nearest-neighbor...");
        return true;
    }

    @Override
    protected void forEveryChannelSetDo(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName, boolean isBatched) {
        BiOperation biNN = new BiLinearNearestNeighbor();
        biNN.setChannel(channels.get(fromChannelName), channels.get(toChannelName));

        String operableName = fromChannelName + "->" + toChannelName;
        DataContainer result = new LinearData(operableName, biNN.execute());

        if (isBatched) result.setBatchKey(channels.getBatchKey());
        String resultName = "CrossNearestNeighbor " + operableName + "(" + panService.channelSetKey(channels) + ")";
        panService.addAnalysisResult(resultName, result);

    }

    @Override
    protected void end() {
    }

}
