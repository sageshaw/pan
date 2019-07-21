package plugins.cmds.charts;

import datastructures.analysis.DataContainer;
import datastructures.graphs.HistogramDatasetPlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import org.apache.commons.math3.exception.NullArgumentException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramType;
import org.scijava.Initializable;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanService;

import javax.swing.*;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

@Plugin(type = Command.class, menuPath = "PAN>Charts>Make Histogram")
public class MakeHistogram extends DynamicCommand implements Initializable {

    @Parameter
    LogService logService;

    @Parameter
    private PanService panService;

    @Parameter(label = "Analysis result", choices = {"a", "b"})
    private String dataName;

    private DataContainer dataset;

    private static final int BIN_LIMIT = 99999;



    @Override
    public void initialize() {

        ArrayList<String> options = new ArrayList<>();

        String[] analysisResultKeys = panService.analysisResultKeys();

        if (analysisResultKeys.length == 0) throw new NullArgumentException();

        for (String analysisResultKey : analysisResultKeys)
            options.add(analysisResultKey);

        MutableModuleItem<String> selectDataItem = getInfo().getMutableInput("dataName", String.class);

        selectDataItem.setChoices(options);

    }


    @Override
    public void run() {
        //Grab dataset
        dataset = panService.getAnalysisResult(dataName);

        //Build selection dialogue
        HistogramPreviewFrame histoPreviewFrame = new HistogramPreviewFrame();

        GenericDialog histoDialogue = new GenericDialog("Histogram...");
        histoDialogue.addMessage("Choose histogram parameters:");
        histoDialogue.addNumericField("Number of fields", 40, 0);
        histoDialogue.addCheckbox("Make total area equal to 1", true);
        histoDialogue.addDialogListener(histoPreviewFrame);
        histoPreviewFrame.setVisible(true);
        histoDialogue.showDialog();

        if (histoDialogue.wasCanceled()) return;
        else {
            histoPreviewFrame.setVisible(false);
            histoPreviewFrame.dispose();

            int numBins = (int) histoDialogue.getNextNumber();
            if (numBins <= 0 || numBins > BIN_LIMIT) numBins = 1;


            if (dataset.isBatched()) {
                GenericDialog batchDialogue = new GenericDialog("Found batch...");
                batchDialogue.addMessage("'" + dataName + "' was found to be in a batch. Run operation on entire batch?");
                batchDialogue.setOKLabel("Yes");
                batchDialogue.setCancelLabel("No");
                batchDialogue.showDialog();

                if (batchDialogue.wasOKed()) {
                    List<DataContainer> datasets = panService.getAnalysisBatch(dataset.getBatchKey());

                    for (DataContainer dataset : datasets) {
                        HistogramDatasetPlus histoData = new HistogramDatasetPlus();
                        histoData.addSeries(dataName, dataset.getData(), numBins);
                        if (histoDialogue.getNextBoolean())
                            histoData.setType(HistogramType.SCALE_AREA_TO_1);
                        else
                            histoData.setType(HistogramType.FREQUENCY);
                        histoData.setBatchKey(dataset.getBatchKey());
                        panService.addHistoSet(dataset.getName(), histoData);
                    }

                    return;
                }
            }

            HistogramDatasetPlus histoData = new HistogramDatasetPlus();
            histoData.addSeries(dataName, dataset.getData(), numBins);
            if (histoDialogue.getNextBoolean())
                histoData.setType(HistogramType.SCALE_AREA_TO_1);
            else
                histoData.setType(HistogramType.FREQUENCY);
            panService.addHistoSet(dataset.getName(), histoData);

        }
    }


    private class HistogramPreviewFrame extends JFrame implements DialogListener {
        private static final int TEXT_CHANGE_ID = 900;

        public HistogramPreviewFrame() {
            super();
        }

        @Override
        public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {

            int numBins = (int) gd.getNextNumber();

            if (numBins <= 0 || numBins > BIN_LIMIT) return false;

            HistogramDatasetPlus previewHistoData = new HistogramDatasetPlus();
            previewHistoData.addSeries(dataName, dataset.getData(), numBins);

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
