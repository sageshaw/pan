package plugins.cmds.charts;

import datastructures.analysis.DataContainer;
import datastructures.graphs.HistogramDatasetPlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramType;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import plugins.cmds.DataCommand;

import javax.swing.*;
import java.awt.AWTEvent;
import java.awt.Dimension;

@Deprecated
@Plugin(type = Command.class/*, menuPath = "PAN>Histogram>Make histogram"*/)
public class MakeHistogram extends DataCommand {

    private static final int BIN_LIMIT = 99999;

    private GenericDialog histoDialog;
    private HistogramPreviewFrame histoPreviewFrame;
    private boolean scaleToOne;

    private int numBins;

    @Override
    protected void setup(String dataName, DataContainer dataset) {
        histoPreviewFrame = new HistogramPreviewFrame(dataName, dataset);

        histoDialog = new GenericDialog("Histogram...");
        histoDialog.addMessage("Choose histogram parameters:");
        histoDialog.addNumericField("Number of fields", 40, 0);
        histoDialog.addCheckbox("Make total area equal to 1", true);
        histoDialog.addDialogListener(histoPreviewFrame);

        histoPreviewFrame.setVisible(true);
        histoDialog.showDialog();

        histoPreviewFrame.setVisible(false);
        histoPreviewFrame.dispose();

        if (histoDialog.wasOKed()) {
            numBins = (int) histoDialog.getNextNumber();
            if (numBins <= 0 || numBins > BIN_LIMIT) numBins = 1;
            scaleToOne = histoDialog.getNextBoolean();

        }
    }

    @Override
    protected void forEveryDatasetDo(String dataName, DataContainer dataset, boolean isBatched) {

        if (histoDialog.wasOKed()) {

            HistogramDatasetPlus histoData = new HistogramDatasetPlus();
            histoData.addSeries(dataName, dataset.getData(), numBins);

            if (scaleToOne) {
                histoData.setType(HistogramType.SCALE_AREA_TO_1);
            } else {
                histoData.setType(HistogramType.FREQUENCY);
            }

            if (isBatched) {
                histoData.setBatchKey(dataset.getBatchKey());
            }

            panService.addHistoSet(panService.analysisResultKey(dataset), histoData);
        }
    }


    @Override
    protected void end() {

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


}
