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
import java.util.List;

@Plugin(type = Command.class, menuPath = "PAN>Process point data...")
public class NearestNeighborPipelineCommand extends BiChannelCommand {
    private int numBins;
    private boolean scaleToOne;

    private double xLowBound;
    private double xUpBound;
    private boolean displayPeakData;

    private JPanel rangePanel;

    @Override
    protected boolean setup(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName) {

        // Step 1: run nearest-neighbor analysis
        BiOperation biNN = new BiLinearNearestNeighbor();
        biNN.setChannel(channels.get(fromChannelName), channels.get(toChannelName));
        String resultName = "NearestNeighbor" + fromChannelName + "->" + toChannelName;
        DataContainer result = new LinearData(resultName, biNN.execute());

        // Step 2: construct histogram from nearest-neighbor data
        GenericDialog histoDialog = new GenericDialog("Constructing histogram...");
        histoDialog.addMessage("Choose histogram parameters:");
        histoDialog.addNumericField("Number of fields", 40, 0);
        histoDialog.addCheckbox("Make total area equal to 1", true);

        HistogramPreviewFrame hPrevFrame = new HistogramPreviewFrame(resultName, result);
        histoDialog.addDialogListener(hPrevFrame);

        hPrevFrame.setVisible(true);
        histoDialog.showDialog();

        if (histoDialog.wasCanceled()) {
            hPrevFrame.setVisible(false);
            hPrevFrame.dispose();
            return false;
        }

        numBins = (int) histoDialog.getNextNumber();
        if (numBins <= 0 || numBins > BIN_LIMIT) numBins = 1;
        scaleToOne = histoDialog.getNextBoolean();

        HistogramDatasetPlus histoData = new HistogramDatasetPlus();
        histoData.addSeries("Preview", result.getData(), numBins);

        // Step 3: find peak values in specified range
        GenericDialog rangeDialog = new GenericDialog("Select range...");
        rangeDialog.addMessage("Specify range on x-axis on which to find peak:");

        double min = histoData.getStartXValue(0, 0);
        double max = histoData.getEndXValue(0, numBins - 1);

        rangeDialog.addNumericField("From", min, NUM_RANGE_DECIMALS);
        rangeDialog.addNumericField("To", max, NUM_RANGE_DECIMALS);
        rangeDialog.addCheckbox("Display data after processing", false);
        rangeDialog.showDialog();

        if (rangeDialog.wasCanceled()) {
            hPrevFrame.setVisible(false);
            hPrevFrame.dispose();
            return false;
        }

        xLowBound = rangeDialog.getNextNumber();
        xUpBound = rangeDialog.getNextNumber();
        displayPeakData = rangeDialog.getNextBoolean();

        hPrevFrame.setVisible(false);
        hPrevFrame.dispose();

        rangePanel = new JPanel();

        return true;
    }

    @Override
    protected void forEveryChannelSetDo(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName, boolean isBatched) {

        // Step 1: run nearest-neighbor analysis
        BiOperation biNN = new BiLinearNearestNeighbor();
        biNN.setChannel(channels.get(fromChannelName), channels.get(toChannelName));

        String operationName = "NearestNeighbor" + fromChannelName + "->" + toChannelName;
        DataContainer dataResult = new LinearData(operationName, biNN.execute());
        if (isBatched) dataResult.setBatchKey(channels.getBatchKey());

        String dataResultName = "NearestNeighbor " + operationName + "(" + channelSetName + ")";
        panService.addAnalysisResult(dataResultName, dataResult);

        // Step 2: construct histogram
        HistogramDatasetPlus histoData = new HistogramDatasetPlus();
        histoData.addSeries(dataResultName, dataResult.getData(), numBins);
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
            if (histoData.getStartXValue(0, i) >= xLowBound &&
                    histoData.getEndXValue(0, i) <= xUpBound) {
                double y = histoData.getYValue(0, i);

                if (y > maxVal) {
                    maxVal = y;
                    maxIndex = i;
                }


            }
        }

        String val_entryName = "Peak Value @ " + xLowBound + "-" + xUpBound;
        String midbox_entryName = "Peak Box Midpoint @ " + xLowBound + "-" + xUpBound;


        histoData.addEntry(val_entryName, maxVal);

        if (maxIndex != -1) {
            histoData.addEntry(midbox_entryName, 0.5 * (histoData.getStartXValue(0, maxIndex)
                    + histoData.getEndXValue(0, maxIndex)));
        } else {
            histoData.addEntry(midbox_entryName, -1);
        }


        JLabel dataLabel = new JLabel("Peak Analysis: " + dataResultName);
        dataLabel.setFont(HistoUtil.HEADER_FONT);
        rangePanel.add(dataLabel);

        JTextArea dataText = new JTextArea(val_entryName + ":\t" + maxVal + System.getProperty("line.separator") +
                midbox_entryName + ":\t" + maxIndex + System.getProperty("line.separator"));
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

            int numBins = (int) gd.getNextNumber();

            if (numBins <= 0 || numBins > BIN_LIMIT) return false;

            HistogramDatasetPlus previewHistoData = new HistogramDatasetPlus();
            previewHistoData.addSeries(previewName, previewSet.getData(), numBins);

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