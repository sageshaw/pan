package cmds.analysis;

import analysis.data.ListPointContainer;
import analysis.data.OperablePointContainer;
import analysis.ops.CrossLinearNearestNeighbor;
import cmds.AnalysisTextExporter;
import cmds.DynamicOutputDoubleChannel;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "PAN>Export analysis...>")
public class ExportCrossNearestNeighborText extends DynamicOutputDoubleChannel implements AnalysisTextExporter {


    //TODO: Find a better way to resolve this conflict
    public void run() {
        AnalysisTextExporter.super.run();
    }

    @Override
    public String getOutput() {


        String result = "FromChannel->ToChannel\tValue\r\n";

        OperablePointContainer from = getFromChannel();
        OperablePointContainer to = getToChannel();

        String fromName = getFromName();
        String toName = getToName();

        double[] nearestNeighborResult = new CrossLinearNearestNeighbor((ListPointContainer) from, (ListPointContainer) to).execute();

        for (double value : nearestNeighborResult) {
            result += fromName + "->" + toName + "\t" + value + "\r\n";
        }

        return result;
    }


}
