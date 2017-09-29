package cmds.analysis;

import analysis.data.OperablePointContainer;
import analysis.data.PointContainer;
import analysis.ops.SingleChannelOperation;
import cmds.SingleChannelCommand;
import cmds.gui.HistogramFrame;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Single-Channel Analysis>Show Histogram")
public class ShowSingleHistogram extends SingleChannelCommand {
    //Parameters for histogram
    @Parameter(label = "Number of bins")
    private int numberOfBins;
    @Parameter(label = "X Axis Label")
    private String xAxisLabel;
    @Parameter(label = "Y Axis Label")
    private String yAxisLabel;
    @Parameter(label = "Graph Title")
    private String graphName;

    //Provide a cutoff distance (may be useful for third objective)
//  @Parameter(label = "Max distance cutoff") private int maxDistance;


    @Override
    public void run() {
        //Grab checked items
        HashMap <String, double[]> displayData = new HashMap <>();

        Map <String, PointContainer> checkedChannels = getCheckedChannels();
        Set <String> channelNames = checkedChannels.keySet();

        OperablePointContainer channel;
        double analysisResult[];
        SingleChannelOperation operation = null;

        try {
            operation = (SingleChannelOperation) getChosenAnalysisOp().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        for (String name : channelNames) {
            operation.setChannel((OperablePointContainer) checkedChannels.get(name));
            analysisResult = operation.execute();
            displayData.put(name, analysisResult);
        }
        //Create the chart (courtesy of JFreeChart), pack, and display
        HistogramFrame demo = new HistogramFrame("Nearest Neighbor Histogram", graphName, xAxisLabel, yAxisLabel, numberOfBins, displayData);

        demo.pack();
        demo.setVisible(true);
    }

}
