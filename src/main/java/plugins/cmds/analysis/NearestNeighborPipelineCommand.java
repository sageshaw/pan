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

@Plugin(type = Command.class, menuPath = "PAN>Process point data...")
public class NearestNeighborPipelineCommand extends BiChannelCommand {
    private int numBins;
    private boolean scaleToOne;

    private double xLowerBound;
    private double xUpperBound;
    private boolean displayPeakData;

    private JPanel rangePanel;

    @Override
    protected boolean setup(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName) {

        // Step 1: run nearest-neighbor analysis
        BiOperation biNN = new BiLinearNearestNeighbor();
        biNN.setChannel(channels.get(fromChannelName), channels.get(toChannelName));
        String resultName = "NearestNeighbor" + fromChannelName + "->" + toChannelName;
        DataContainer result = new LinearData(resultName, biNN.execute());

        // Step 2: construct histogram from nearest-neighbor data in a given range (to crop data)
        GenericDialog histoDialog = new GenericDialog("Constructing histogram...");
        histoDialog.addMessage("Choose histogram parameters:");
        histoDialog.addMessage("Specify x-axis range: ");
        histoDialog.addNumericField("From", result.min(), 2);
        histoDialog.addNumericField("To", result.max(), 2);
        histoDialog.addNumericField("Number of boxes", 100, 0);
        histoDialog.addCheckbox("Make total area equal to 1", true);
        histoDialog.addCheckbox("Display peak data", false);

        HistogramPreviewFrame hPrevFrame = new HistogramPreviewFrame(resultName, result);
        histoDialog.addDialogListener(hPrevFrame);

        hPrevFrame.setVisible(true);
        histoDialog.showDialog();

        if (histoDialog.wasCanceled()) {
            hPrevFrame.setVisible(false);
            hPrevFrame.dispose();
            return false;
        }

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

        private String previewName;
        private DataContainer previewSet;


        public HistogramPreviewFrame(String name, DataContainer set) {
            super();
            previewName = name;
            previewSet = set;
        }

        @Override
        public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
            Double low = gd.getNextNumber();
            Double high = gd.getNextNumber();

            if (low < 0.0 || low.isNaN() || high <= 0.0 || high.isNaN() || low >= high) return false;

            int numBins = (int) gd.getNextNumber();

            if (numBins <= 0 || numBins > BIN_LIMIT) return false;

            HistogramDatasetPlus previewHistoData = new HistogramDatasetPlus();

            double[] previewData = previewSet.getDataWithinRange(low, high);
            if (previewData.length == 0) return false;


            previewHistoData.addSeries(previewName, previewData, numBins);

            if (gd.getNextBoolean())
                previewHistoData.setType(HistogramType.SCALE_AREA_TO_1);
            else
                previewHistoData.setType(HistogramType.FREQUENCY);

            JFreeChart chart = ChartFactory.createHistogram("Preview", "", "", previewHistoData,
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
