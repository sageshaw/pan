package plugins.cmds.charts;

import datastructures.graphs.HistogramDatasetPlus;
import ij.gui.GenericDialog;
import org.apache.commons.math3.exception.NullArgumentException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.scijava.Initializable;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanService;

import javax.swing.*;
import java.util.ArrayList;

@Plugin(type = Command.class, menuPath = "PAN>Charts>Find peak in range")
public class FindPeakInRange extends DynamicCommand implements Initializable {

    @Parameter
    PanService panService;

    @Parameter(label = "Histogram data", choices = {"a", "b"})
    private String histoName;

    private HistogramDatasetPlus histoData;

    @Override
    public void initialize() {
        ArrayList<String> options = new ArrayList<>();

        String[] histoKeys = panService.histoKeys();

        if (histoKeys.length == 0) throw new NullArgumentException();

        for (String histoKey : histoKeys)
            options.add(histoKey);

        MutableModuleItem<String> selectHistoItem = getInfo().getMutableInput("histoName", String.class);

        selectHistoItem.setChoices(options);

    }

    @Override
    public void run() {
        //Grab histogram data
        histoData = panService.getHistoSet(histoName);

        //Show preview reference histogram
        JFreeChart chart = ChartFactory.createHistogram("Preview", "", "", histoData,
                PlotOrientation.VERTICAL, false, false, false);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(HistoUtil.PREVIEW_DIMENSIONS);

        JFrame previewFrame = new JFrame();
        previewFrame.setContentPane(chartPanel);
        previewFrame.setSize(HistoUtil.PREVIEW_DIMENSIONS);
        previewFrame.pack();
        previewFrame.setVisible(true);

        //Build range selection dialogue box
        GenericDialog rangeDialog = new GenericDialog("Select range...");
        rangeDialog.addMessage("Specify range in which to find peak:");

        //Find min and max X values to set as defaults for peak-finding
        int numBins = histoData.getItemCount(0);
        double min = histoData.getStartXValue(0, 0);
        double max = histoData.getEndXValue(0, numBins - 1);


        rangeDialog.addNumericField("From", min, NUM_RANGE_DECIMALS);
        rangeDialog.addNumericField("To", max, NUM_RANGE_DECIMALS);
        rangeDialog.showDialog();

        //If user OKs, find peak value in specified range
        if (rangeDialog.wasOKed()) {
            double lowBound = rangeDialog.getNextNumber();
            double upBound = rangeDialog.getNextNumber();

            double maxVal = -1.0;
            int maxIndex = -1;

            for (int i = 0; i < histoData.getItemCount(0); i++) {
                if (histoData.getStartXValue(0, i) >= lowBound &&
                        histoData.getEndXValue(0, i) <= upBound) {
                    double y = histoData.getYValue(0, i);

                    if (y > maxVal) {
                        maxVal = y;
                        maxIndex = i;
                    }


                }
            }

            //Annotate corresponding histogram
            String val_entryName = "Peak Value @ " + lowBound + "-" + upBound;
            String index_entryName = "Peak Index @ " + lowBound + "-" + upBound;

            histoData.addEntry(val_entryName, maxVal);
            histoData.addEntry(index_entryName, maxIndex);

            // Display calculated information
            JFrame statFrame = new JFrame();
            JPanel statPanel = new JPanel();

            JLabel dataLabel = new JLabel("Peak Analysis: " + histoName);
            dataLabel.setFont(HistoUtil.HEADER_FONT);
            statPanel.add(dataLabel);

            JTextArea dataText = new JTextArea(val_entryName + ":\t" + maxVal + System.getProperty("line.separator") +
                    index_entryName + ":\t" + maxIndex + System.getProperty("line.separator"));
            dataText.setFont(HistoUtil.PARAGRAPH_FONT);
            dataText.setEditable(false);
            statPanel.add(dataText);

            statFrame.setContentPane(statPanel);
            statFrame.pack();

            statFrame.setVisible(true);

        }


        previewFrame.setVisible(false);
        previewFrame.dispose();


    }


    private static final int NUM_RANGE_DECIMALS = 2;
}
