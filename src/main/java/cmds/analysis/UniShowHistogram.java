package cmds.analysis;

import datastructures.points.OperablePointContainer;
import datastructures.points.PointContainer;
import analysis.ops.UniOperation;
import analysis.util.StatUtilities;
import cmds.UniChannelCommand;
import cmds.gui.HistogramFrame;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Command plugin to display single-channel analysis in a histogram.
 */
@Deprecated
@Plugin(type = Command.class, menuPath = "PAN>Analysis>Single-Channel Analysis>Show Single Histogram (Deprecated)")
public class UniShowHistogram extends UniChannelCommand {
    //Parameters for histogram
    @Parameter(label = "Number of bins")
    private int numberOfBins;
    @Parameter(label = "X Axis Label")
    private String xAxisLabel;
    @Parameter(label = "Y Axis Label")
    private String yAxisLabel;
    @Parameter(label = "Graph Title")
    private String graphName;
    @Parameter(label = "Data statistics")
    private boolean showStat;
    //Provide a cutoff distance (may be useful for third objective)
//  @Parameter(label = "Max distance cutoff") private int maxDistance;


    @Override
    public void run() {

        //Grab checked items
        HashMap <String, double[]> displayData = new HashMap <>();

        Map <String, PointContainer> checkedChannels = getCheckedChannels();
        Set <String> channelNames = checkedChannels.keySet();

        int histoNumber = panContext.getHistogramNumber();
        JFrame statFrame = new JFrame("HistogramStat" + histoNumber);
        JPanel statPanel = new JPanel();
        statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.PAGE_AXIS));

        OperablePointContainer channel;
        double analysisResult[];
        UniOperation operation = null;
        try {
            operation = (UniOperation) getChosenAnalysisOp().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        for (String name : channelNames) {
            operation.setChannel((OperablePointContainer) checkedChannels.get(name));
            analysisResult = operation.execute();
            displayData.put(name, analysisResult);

            if (showStat) {
                JLabel categoryLabel = new JLabel(name);
                categoryLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
                statPanel.add(categoryLabel);

                Font fieldFont = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
                JTextArea statField = new JTextArea("Mean: " + StatUtilities.mean(analysisResult) + System.getProperty("line.separator") +
                        "Median: " + StatUtilities.median(analysisResult) + System.getProperty("line.separator") +
                        "Standard Deviation: " + StatUtilities.sampleStandardDeviation(analysisResult) + System.getProperty("line.separator") +
                        "Lower quartile (exclusive): " + StatUtilities.lowerQuartileExc(analysisResult) + System.getProperty("line.separator") +
                        "Upper quartile (exclusive): " + StatUtilities.upperQuartileExc(analysisResult));
                statField.setFont(fieldFont);
                statField.setEditable(false);
                statPanel.add(statField);
            }

        }

        statFrame.setContentPane(statPanel);

        //Create the chart (courtesy of JFreeChart), pack, and display
        HistogramFrame demo = new HistogramFrame("Histogram" + histoNumber, graphName, xAxisLabel, yAxisLabel, numberOfBins, displayData);

        statFrame.pack();
        demo.pack();

        statFrame.setVisible(true);
        demo.setVisible(true);

        panContext.setHistogramNumber(histoNumber + 1);


    }

    @Override
    protected boolean allowAnalysisSelection() {
        return true;
    }

}
