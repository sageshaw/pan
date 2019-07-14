package plugins.cmds.analysis;

import analysis.ops.BiLinearNearestNeighbor;
import analysis.ops.BiOperation;
import datastructures.points.OperablePointContainer;
import datastructures.postanalysis.AnalysisContainer;
import datastructures.postanalysis.LinearData;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanContext;
import plugins.cmds.BiChannelCommand;

import java.util.Map;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Cross-Channel Analysis>Nearest Neighbor")
public class CrossChannelNearestNeighbor extends BiChannelCommand {

    @Parameter
    PanContext ptStore;

    @Override
    public void run() {
        AnalysisContainer result = new LinearData();
        BiOperation operation = new BiLinearNearestNeighbor();

        OperablePointContainer from = getFromChannel();
        OperablePointContainer to = getToChannel();

        String fromName = getFromName();
        String toName = getToName();

        operation.setChannel(from,to);

        String operableName = fromName + "->" + toName;
        result.add(operableName, operation.execute());

        String resultName = "CrossNearestNeighbor " + operableName;
        ptStore.addAnalysisResult(resultName, result);

    }

    @Override
    protected boolean allowAnalysisSelection() {
        return false;
    }
}
