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

@Deprecated
@Plugin(type = Command.class /*, menuPath = "PAN>Analysis>Single-channel nearest neighbor"*/)
public class SingleChannelNearestNeighbor extends UniChannelCommand {


    @Parameter
    LogService logService;


    @Override
    protected void doOnRun(List<ChannelContainer> channelSets, String channelName) {
        logService.info("Running nearest-neighbor...");

        BiOperation operation = new BiLinearNearestNeighbor();

        for (ChannelContainer channelSet : channelSets) {

            operation.setChannel(channelSet.get(channelName), channelSet.get(channelName));

            DataContainer<Double> result = new LinearData(channelName, operation.execute());

            if (channelSet.isBatched()) {
                result.setBatchKey(channelSet.getBatchKey());
            }

            String resultName = "SingleNearestNeighbor " + channelName + "(" + panService.channelSetKey(channelSet) + ")";
            panService.addAnalysisResult(resultName, result);
        }

    }


}
