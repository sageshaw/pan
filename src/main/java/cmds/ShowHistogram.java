package cmds;

import analysis.data.Linear;
import analysis.data.PointContainer;
import analysis.ops.LinearNearestNeighbor;
import filters.MaxCutoff;
import filters.PanFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//TODO: change to distribution graph instead of bar histogram graph

/**
 * Class to handle data output display. Currently only displays Nearest Neighbor data (since no other analysis
 * techniques have been implemented yet.
 */
@Plugin(type = Command.class, menuPath = "PAN>Display histogram from currently loaded file...")
public class ShowHistogram extends DynamicOutputCommand {

  //Parameters for histogram
  @Parameter(label = "Number of bins") private int numberOfBins;
  @Parameter(label = "X Axis Label") private String xAxisLabel;
  @Parameter(label = "Y Axis Label") private String yAxisLabel;
  @Parameter(label = "Graph Title") private String graphName;

  //Provide a cutoff distance (may be useful for third objective)
  @Parameter(label = "Max distance cutoff") private int maxDistance;


  @Override
  public void run() {
    //Grab checked items
    HashMap<String, double[]> displayData = new HashMap<>();

    Map <String, PointContainer> checkedChannels = getCheckedChannels();
    Set <String> channelNames = checkedChannels.keySet();

    //Loop through checked items, filter for cutoff distance, add to hashmap for input, add corresponding key to array
    for (String name : channelNames) {

      double[] nearestNeighborResult = new LinearNearestNeighbor((Linear) (checkedChannels.get(name))).process();
      PanFilter cutoff = new MaxCutoff(maxDistance);
      nearestNeighborResult = cutoff.filter(nearestNeighborResult);
      displayData.put(name, nearestNeighborResult);

    }

    //Create the chart (courtesy of JFreeChart), pack, and display
    HistogramFrame demo = new HistogramFrame("Histogram", displayData);

    demo.pack();
    demo.setVisible(true);
  }

  //No other classes use this, so inner class it is. This represents the Histogram window
  class HistogramFrame extends JFrame {

    HistogramFrame(String title, Map <String, double[]> data) {
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

}
