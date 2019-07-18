package plugins.cmds.analysis;

import analysis.ops.BiLinearNearestNeighbor;
import analysis.ops.BiOperation;
import datastructures.points.ChannelContainer;
import datastructures.points.OperablePointContainer;
import datastructures.postanalysis.AnalysisContainer;
import datastructures.postanalysis.LinearData;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanService;
import plugins.cmds.BiChannelCommand;

import java.util.List;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Cross-Channel Analysis>Cross Nearest Neighbor")
public class CrossChannelNearestNeighbor extends BiChannelCommand {

    @Parameter
    PanService panService;

    @Parameter
    LogService logService;

    @Override
    public void doOnRun(List<ChannelContainer> channelSets, String from, String to) {
        logService.info("Running cross nearest-neighbor...");

        BiOperation operation = new BiLinearNearestNeighbor();

        for (ChannelContainer channelSet : channelSets) {

            operation.setChannel(channelSet.get(from), channelSet.get(to));

            String operableName = from + "->" + to;
            AnalysisContainer result = new LinearData(operableName, operation.execute());

            if (channelSet.isBatched()) {
                result.setBatchKey(channelSet.getBatchKey());
            }
            String resultName = "CrossNearestNeighbor " + operableName;
            panService.addAnalysisResult(resultName, result);
        }

    }


}
