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
import plugins.cmds.HistogramCommand;

import javax.swing.*;
import java.util.ArrayList;

@Deprecated
@Plugin(type = Command.class /*, menuPath = "PAN>Histogram>Find peak in range"*/)
public class FindPeakInRange extends HistogramCommand {
    private JPanel statPanel;

    private JFrame previewFrame;

    private GenericDialog rangeDialog;
    private double lowBound;
    private double upBound;


    @Override
    protected void setup(String histoName, HistogramDatasetPlus histoData) {
        //Show preview reference histogram
        JFreeChart chart = ChartFactory.createHistogram("Preview", "", "", histoData,
                PlotOrientation.VERTICAL, false, false, false);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(HistoUtil.PREVIEW_DIMENSIONS);

        statPanel = new JPanel();
        statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.PAGE_AXIS));

        previewFrame = new JFrame();
        previewFrame.setContentPane(chartPanel);
        previewFrame.setSize(HistoUtil.PREVIEW_DIMENSIONS);
        previewFrame.pack();
        previewFrame.setVisible(true);

        //Build range selection dialogue box
        rangeDialog = new GenericDialog("Select range...");
        rangeDialog.addMessage("Specify range in which to find peak:");

        //Find min and max X values to set as defaults for peak-finding
        int numBins = histoData.getItemCount(0);
        double min = histoData.getStartXValue(0, 0);
        double max = histoData.getEndXValue(0, numBins - 1);


        rangeDialog.addNumericField("From", min, NUM_RANGE_DECIMALS);
        rangeDialog.addNumericField("To", max, NUM_RANGE_DECIMALS);
        rangeDialog.addCheckbox("Display data after processing", false);
        rangeDialog.showDialog();

        if (rangeDialog.wasOKed()) {
            lowBound = rangeDialog.getNextNumber();
            upBound = rangeDialog.getNextNumber();
        }

    }

    @Override
    protected void forEveryHistoDo(String histoName, HistogramDatasetPlus histoData, boolean isBatched) {
        //If user OKs, find peak value in specified range
        if (rangeDialog.wasOKed()) {

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
            String midbox_entryName = "Peak Box Midpoint @ " + lowBound + "-" + upBound;

            histoData.addEntry(val_entryName, maxVal);
            histoData.addEntry(midbox_entryName, 0.5 * (histoData.getStartXValue(0, maxIndex)
                    + histoData.getEndXValue(0, maxIndex)));

            JLabel dataLabel = new JLabel("Peak Analysis: " + histoName);
            dataLabel.setFont(HistoUtil.HEADER_FONT);
            statPanel.add(dataLabel);

            JTextArea dataText = new JTextArea(val_entryName + ":\t" + maxVal + System.getProperty("line.separator") +
                    midbox_entryName + ":\t" + maxIndex + System.getProperty("line.separator"));
            dataText.setFont(HistoUtil.PARAGRAPH_FONT);
            dataText.setEditable(false);
            statPanel.add(dataText);

        }


    }

    @Override
    protected void end() {
        previewFrame.setVisible(false);
        previewFrame.dispose();

        if (rangeDialog.getNextBoolean()) {
            JFrame statFrame = new JFrame();
            statFrame.setContentPane(statPanel);
            statFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            statFrame.pack();
            statFrame.setVisible(true);
        }
    }


    private static final int NUM_RANGE_DECIMALS = 2;
}
