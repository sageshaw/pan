package cmds.analysis;

import analysis.data.ListPointContainer;
import analysis.data.OperablePointContainer;
import analysis.ops.CrossLinearNearestNeighbor;
import cmds.DynamicOutputDoubleChannel;
import cmds.gui.HistogramFrame;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

@Plugin(type = Command.class, menuPath = "PAN>Display histogram...>Cross-channel Nearest Neighbor")
public class ShowCrossNearestNeighborHistogram extends DynamicOutputDoubleChannel {

    @Parameter(label = "Number of bins")
    private int numberOfBins;
    @Parameter(label = "X Axis Label")
    private String xAxisLabel;
    @Parameter(label = "Y Axis Label")
    private String yAxisLabel;
    @Parameter(label = "Graph Title")
    private String graphName;

    public void run() {
        String fromChannelName = getFromName();
        String toChannelName = getToName();

        OperablePointContainer fromChannel = getFromChannel();
        OperablePointContainer toChannel = getToChannel();

        double[] nearestNeighborResult = new CrossLinearNearestNeighbor((ListPointContainer) fromChannel, (ListPointContainer) toChannel).execute();

        Map <String, double[]> displayData = new HashMap <>();
        displayData.put(fromChannelName + "->" + toChannelName, nearestNeighborResult);

        HistogramFrame demo = new HistogramFrame("Cross-channel Nearest Neighbor Histogram",
                graphName, xAxisLabel, yAxisLabel, numberOfBins, displayData);
        demo.pack();
        demo.setVisible(true);
    }


}
