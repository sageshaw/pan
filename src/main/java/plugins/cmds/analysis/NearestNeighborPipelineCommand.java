package plugins.cmds.analysis;

import analysis.ops.BiLinearNearestNeighbor;
import analysis.ops.BiOperation;
import datastructures.analysis.DataContainer;
import datastructures.analysis.LinearData;
import datastructures.graphs.HistogramDatasetPlus;
import datastructures.points.ChannelContainer;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramType;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import plugins.cmds.charts.HistoUtil;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Plugin(type = Command.class, menuPath = "PAN>Process point data...")
public class NearestNeighborPipelineCommand extends BiChannelCommand {
    private int numBins;
    private boolean scaleToOne;

    private double xLowerBound;
    private double xUpperBound;
    private boolean displayPeakData;

    private JPanel rangePanel;

    @Override
    protected boolean setup(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName, boolean isBatched) {

        Map<String, ChannelContainer> batchMap;

        if (isBatched) {
            batchMap = panService.getStringChannelSetBatchMap(channels.getBatchKey());
        } else {
            batchMap = new HashMap<>();
            batchMap.put(channelSetName, channels);
        }


        // Run nearest-neighbor analysis for default values for dialog box
        BiOperation biNN = new BiLinearNearestNeighbor();
        biNN.setChannel(channels.get(fromChannelName), channels.get(toChannelName));
        String defaultResultName = "NearestNeighbor" + fromChannelName + "->" + toChannelName;
        DataContainer defaultResult = new LinearData(defaultResultName, biNN.execute());

        // Fill in histogram selection dialog box using nearest-neighbor data found in the data output by previous process
        GenericDialog histoDialog = new GenericDialog("Constructing histogram...");
        String[] batchChannelKeys = batchMap.keySet().toArray(new String[0]);
        histoDialog.addChoice("Preview data: ", batchChannelKeys, channelSetName);
        histoDialog.addMessage("Choose histogram parameters:");
        histoDialog.addMessage("Specify x-axis range: ");
        histoDialog.addNumericField("From", defaultResult.min(), 2);
        histoDialog.addNumericField("To", defaultResult.max(), 2);
        histoDialog.addNumericField("Number of boxes", 100, 0);
        histoDialog.addCheckbox("Make total area equal to 1", true);
        histoDialog.addCheckbox("Display peak data", false);


        // Construct preview frame and add as listener to dynamically update as user changes parameters in histogram dialog box
        HistogramPreviewFrame hPrevFrame = new HistogramPreviewFrame(batchMap, fromChannelName, toChannelName);
        histoDialog.addDialogListener(hPrevFrame);

        hPrevFrame.setVisible(true);
        histoDialog.showDialog();

        if (histoDialog.wasCanceled()) {
            hPrevFrame.setVisible(false);
            hPrevFrame.dispose();
            return false;
        }

        // User has finished entering parameters. Extract selected parameters from dialog box.
        xLowerBound = histoDialog.getNextNumber();
        xUpperBound = histoDialog.getNextNumber();

        numBins = (int) histoDialog.getNextNumber();
        if (numBins <= 0 || numBins > BIN_LIMIT) numBins = 1;
        scaleToOne = histoDialog.getNextBoolean();

        displayPeakData = histoDialog.getNextBoolean();

        hPrevFrame.setVisible(false);
        hPrevFrame.dispose();

        rangePanel = new JPanel();
        rangePanel.setLayout(new BoxLayout(rangePanel, BoxLayout.PAGE_AXIS));


        return true;
    }

    @Override
    protected void forEveryChannelSetDo(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName, boolean isBatched) {

        // Step 1: run nearest-neighbor analysis
        BiOperation biNN = new BiLinearNearestNeighbor();
        biNN.setChannel(channels.get(fromChannelName), channels.get(toChannelName));

        String operationName = "NearestNeighbor " + fromChannelName + "->" + toChannelName;
        DataContainer dataResult = new LinearData(operationName, biNN.execute());
        if (isBatched) dataResult.setBatchKey(channels.getBatchKey());

        String dataResultName = operationName + " (" + channelSetName + ")";
        panService.addAnalysisResult(dataResultName, dataResult);

        // Step 2: construct histogram
        HistogramDatasetPlus histoData = new HistogramDatasetPlus();
        histoData.addSeries(dataResultName, dataResult.getDataWithinRange(xLowerBound, xUpperBound), numBins);
        if (scaleToOne) {
            histoData.setType(HistogramType.SCALE_AREA_TO_1);
        } else {
            histoData.setType(HistogramType.FREQUENCY);
        }
        if (isBatched) histoData.setBatchKey(dataResult.getBatchKey());
        panService.addHistoSet(dataResultName, histoData);

        // Step 3: find peak in histogram data
        double maxVal = -1.0;
        int maxIndex = -1;

        for (int i = 0; i < histoData.getItemCount(0); i++) {

                double y = histoData.getYValue(0, i);

                if (y > maxVal) {
                    maxVal = y;
                    maxIndex = i;

            }
        }

        histoData.addEntry("Selected Lower Range", xLowerBound);
        histoData.addEntry("Selected Upper Bound", xUpperBound);

        String val_entryName = "Peak Value";
        String midbox_entryName = "Peak Box Midpoint";



        histoData.addEntry(val_entryName, maxVal);

        double midBoxVal;

        if (maxIndex != -1) {
            midBoxVal = 0.5 * (histoData.getStartXValue(0, maxIndex) + histoData.getEndXValue(0, maxIndex));
        } else {
            midBoxVal = -1.0;
        }

        histoData.addEntry(midbox_entryName, midBoxVal);


        JLabel dataLabel = new JLabel("Peak Analysis: " + dataResultName);
        dataLabel.setFont(HistoUtil.HEADER_FONT);
        rangePanel.add(dataLabel);

        JTextArea dataText = new JTextArea(val_entryName + ":\t" + maxVal + System.lineSeparator() +
                midbox_entryName + ":\t" + midBoxVal + System.lineSeparator() +
                "Selected Range:\t" + xLowerBound + " - " + xUpperBound + System.lineSeparator());
        dataText.setFont(HistoUtil.PARAGRAPH_FONT);
        dataText.setEditable(false);
        rangePanel.add(dataText);
    }

    @Override
    protected void end() {
        if (displayPeakData) {
            JFrame rangeFrame = new JFrame();
            rangeFrame.setContentPane(rangePanel);
            rangeFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            rangeFrame.pack();
            rangeFrame.setVisible(true);
        }

    }


    private class HistogramPreviewFrame extends JFrame implements DialogListener {
        private static final int TEXT_CHANGE_ID = 900;

        private Map<String, ChannelContainer> prevChannelSetMap;
        private BiOperation biNN;
        private String fromChannelName;
        private String toChannelname;


        public HistogramPreviewFrame(Map<String, ChannelContainer> prevChannelSetMap, String fromChannelName, String toChannelName) {
            super();
            this.prevChannelSetMap = prevChannelSetMap;
            biNN = new BiLinearNearestNeighbor();
            this.fromChannelName = fromChannelName;
            this.toChannelname = toChannelName;
        }


        @Override
        public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
            String prevChannelSetKey = gd.getNextChoice();
            ChannelContainer prevChannelSet = prevChannelSetMap.get(prevChannelSetKey);

            biNN.setChannel(prevChannelSet.get(fromChannelName), prevChannelSet.get(toChannelname));
            DataContainer previewNNResult = new LinearData(prevChannelSetKey, biNN.execute());

            Double low = gd.getNextNumber();
            Double high = gd.getNextNumber();

            if (low < 0.0 || low.isNaN() || high <= 0.0 || high.isNaN() || low >= high) return false;

            int numBins = (int) gd.getNextNumber();

            if (numBins <= 0 || numBins > BIN_LIMIT) return false;

            HistogramDatasetPlus previewHistoData = new HistogramDatasetPlus();

            double[] previewData = previewNNResult.getDataWithinRange(low, high);
            if (previewData.length == 0) return false;


            previewHistoData.addSeries(prevChannelSetKey, previewData, numBins);

            if (gd.getNextBoolean())
                previewHistoData.setType(HistogramType.SCALE_AREA_TO_1);
            else
                previewHistoData.setType(HistogramType.FREQUENCY);

            JFreeChart chart = ChartFactory.createHistogram(prevChannelSetKey + " Preview", "", "", previewHistoData,
                    PlotOrientation.VERTICAL, false, false, false);
            ChartPanel chartPanel = new ChartPanel(chart, false);
            chartPanel.setPreferredSize(HistoUtil.PREVIEW_DIMENSIONS);

            setContentPane(chartPanel);
            setSize(new Dimension(300, 200));
            pack();
            repaint();

            return true;
        }

    }

    private static final int BIN_LIMIT = 99999;
    private static final int NUM_RANGE_DECIMALS = 2;


}
