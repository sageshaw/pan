package plugins.cmds.analysis;

import analysis.ops.BiLinearNearestNeighbor;
import analysis.ops.BiOperation;
import datastructures.points.OperablePointContainer;
import datastructures.points.PointContainer;
import datastructures.postanalysis.AnalysisContainer;
import datastructures.postanalysis.LinearData;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanContext;
import plugins.cmds.UniChannelCommand;

import java.util.Map;
import java.util.Set;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Single-Channel Analysis>Nearest Neighbor")
public class SingleChannelNearestNeighbor extends UniChannelCommand {


    @Override
    public void run() {

        BiOperation operation = new BiLinearNearestNeighbor();
        //TODO: this should be OperablePointContainer, FIX
        Map<String, PointContainer> checkedChannels = getCheckedChannels();
        Set<String> channelNames = checkedChannels.keySet();

        AnalysisContainer results = new LinearData();
        String resultName = "SingleNearestNeighbor";

        for (String name : channelNames) {
            OperablePointContainer channel = (OperablePointContainer) checkedChannels.get(name);
            operation.setChannel(channel, channel);

            results.add(name, operation.execute());
            resultName += " " + name;
        }

        panContext.addAnalysisResult(resultName, results);

    }


    @Override
    protected boolean allowAnalysisSelection() {
        return false;
    }
}
