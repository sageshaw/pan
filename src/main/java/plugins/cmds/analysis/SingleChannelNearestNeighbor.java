package plugins.cmds.analysis;

import analysis.ops.BiLinearNearestNeighbor;
import analysis.ops.BiOperation;
import datastructures.points.ChannelContainer;
import datastructures.points.OperablePointContainer;
import datastructures.points.PointContainer;
import datastructures.postanalysis.AnalysisContainer;
import datastructures.postanalysis.LinearData;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanService;
import plugins.cmds.UniChannelCommand;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Single-Channel Analysis>Nearest Neighbor")
public class SingleChannelNearestNeighbor extends UniChannelCommand {

    @Parameter
    PanService panService;

    @Parameter
    LogService logService;


    @Override
    protected void doOnRun(List<ChannelContainer> channelSets, String channelName) {
        logService.info("Running nearest-neighbor...");

        BiOperation operation = new BiLinearNearestNeighbor();

        for (ChannelContainer channelSet : channelSets) {
            AnalysisContainer result = new LinearData();

            operation.setChannel(channelSet.get(channelName), channelSet.get(channelName));

            result.add(channelName, operation.execute());

            if (channelSet.isBatched()) {
                result.setBatchKey(channelSet.getBatchKey());
            }

            String resultName = "SingleNearestNeighbor " + channelName;
            panService.addAnalysisResult(resultName, result);
        }

    }


}
