package datastructures.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Represents a histogram frame. Abstracts away JFreeChart usage from rest of codebase.
 */
@Deprecated
public class HistogramFrame extends JFrame {

    public HistogramFrame(String title, String graphName, String xAxisLabel, String yAxisLabel, int numberOfBins, Map <String, double[]> data) {
        super(title);
        HistogramDataset dataset = new HistogramDataset();

        for (String name : data.keySet()) {
            dataset.addSeries(name, data.get(name), numberOfBins);
        }

        JFreeChart chart =
                ChartFactory.createHistogram(
                        graphName,
                        xAxisLabel,
                        yAxisLabel,
                        dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false);


        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
    }

}