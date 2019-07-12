package cmds.analysis;

import datastructures.OperablePointContainer;
import analysis.ops.BiOperation;
import analysis.util.StatUtilities;
import cmds.BiChannelCommand;
import cmds.gui.HistogramFrame;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent display of cross-channel analysis data in a histogram.
 */

@Deprecated
@Plugin(type = Command.class, menuPath = "PAN>Analysis>Cross-Channel Analysis>Show Cross Histogram (Deprecated)")
public class BiShowHistogram extends BiChannelCommand {
    @Parameter
    LogService logService;

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

    public void run() {


        logService.info("Bi");

        String fromChannelName = getFromName();
        String toChannelName = getToName();

        OperablePointContainer fromChannel = getFromChannel();
        OperablePointContainer toChannel = getToChannel();


        BiOperation operation = null;

        try {
            operation = (BiOperation) getChosenAnalysisOp().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        operation.setChannel(fromChannel, toChannel);
        double analysisResult[] = operation.execute();

        Map <String, double[]> displayData = new HashMap <>();
        displayData.put(fromChannelName + "->" + toChannelName, analysisResult);

        int histoNumber = panContext.getHistogramNumber();

        JFrame statFrame = new JFrame("HistogramStat" + histoNumber);
        JPanel statPanel = new JPanel();
        statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.PAGE_AXIS));

        if (showStat) {
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

        statFrame.setContentPane(statPanel);

        HistogramFrame histoFrame = new HistogramFrame("Histogram" + histoNumber,
                graphName, xAxisLabel, yAxisLabel, numberOfBins, displayData);


        statFrame.pack();
        histoFrame.pack();

        statFrame.setVisible(true);
        histoFrame.setVisible(true);

        panContext.setHistogramNumber(histoNumber + 1);
    }


    @Override
    protected boolean allowAnalysisSelection() {
        return true;
    }


}
