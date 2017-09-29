package cmds.analysis;

import analysis.data.OperablePointContainer;
import analysis.ops.CrossChannelOperation;
import cmds.CrossChannelCommand;
import cmds.gui.HistogramFrame;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Cross-Channel Analysis>Show Histogram")
public class ShowCrossHistogram extends CrossChannelCommand {
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


        CrossChannelOperation operation = null;

        try {
            operation = (CrossChannelOperation) getChosenAnalysisOp().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        operation.setChannel(fromChannel, toChannel);
        double analysisResult[] = operation.execute();

        Map <String, double[]> displayData = new HashMap <>();
        displayData.put(fromChannelName + "->" + toChannelName, analysisResult);

        HistogramFrame demo = new HistogramFrame("Cross-channel Nearest Neighbor Histogram",
                graphName, xAxisLabel, yAxisLabel, numberOfBins, displayData);
        demo.pack();
        demo.setVisible(true);
    }
}
