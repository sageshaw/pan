package plugins.cmds.analysis;

import analysis.ops.BiLinearNearestNeighbor;
import analysis.ops.BiOperation;
import datastructures.points.OperablePointContainer;
import datastructures.postanalysis.AnalysisContainer;
import datastructures.postanalysis.LinearData;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.cmds.BiChannelCommand;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Cross-Channel Analysis>Cross Nearest Neighbor")
public class CrossChannelNearestNeighbor extends BiChannelCommand {

    @Parameter
    LogService logService;

    @Override
    public void run() {
        logService.info("Bi");

        AnalysisContainer result = new LinearData();
        BiOperation operation = new BiLinearNearestNeighbor();

        String fromName = getFromName();
        String toName = getToName();

        OperablePointContainer from = getFromChannel();
        OperablePointContainer to = getToChannel();



        operation.setChannel(from,to);

        String operableName = fromName + "->" + toName;
        result.add(operableName, operation.execute());

        String resultName = "CrossNearestNeighbor " + operableName;
        panService.addAnalysisResult(resultName, result);

    }

    @Override
    protected boolean allowAnalysisSelection() {
        return false;
    }
}
