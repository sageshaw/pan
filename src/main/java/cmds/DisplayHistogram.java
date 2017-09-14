package cmds;

import analysis.data.Linear;
import analysis.ops.LinearNearestNeighbor;
import cmds.gui.ChannelModuleItem;
import filters.MaxCutoff;
import filters.PanFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.ApplicationFrame;
import org.scijava.command.Command;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO: change to distribution graph instead of bar histogram graph

/**
 * Class to handle data output display. Currently only displays Nearest Neighbor data (since no other analysis
 * techniques have been implemented yet.
 */
@Plugin(type = Command.class, menuPath = "PAN>Display histogram from currently loaded file...")
public class DisplayHistogram extends OutputAnalysisCommand {

  //Parameters for histogram
  @Parameter(label = "Number of bins") private int numberOfBins;
  @Parameter(label = "X Axis Label") private String xAxisLabel;
  @Parameter(label = "Y Axis Label") private String yAxisLabel;
  @Parameter(label = "Graph Title") private String graphName;

  //Provide a cutoff distance (may be useful for third objective)
  @Parameter(label = "Max distance cutoff") private int maxDistance;


  //TODO: Clean up coupling between superclass and subclass implementation
  @Override
  public void run() {
    //Grab checked items
    HashMap<String, double[]> displayData = new HashMap<>();
    List<String> keys = new ArrayList<>();
    List <ChannelModuleItem <Boolean>> checkedItems = getCheckedModules();

    ModuleItem<Boolean> moduleItem;
    //Loop through checked items, filter for cutoff distance, add to hashmap for input, add corresponding key to array
    for (ChannelModuleItem <Boolean> bundledChannelItem : checkedItems) {
      moduleItem = bundledChannelItem.getModuleItem();

      double[] nearestNeighborResult = new LinearNearestNeighbor((Linear) (bundledChannelItem.getChannel())).process();
      PanFilter cutoff = new MaxCutoff(maxDistance);
      nearestNeighborResult = cutoff.filter(nearestNeighborResult);
      displayData.put(moduleItem.getName(), nearestNeighborResult);
      keys.add(moduleItem.getName());

    }

    //Create the chart (courtesy of JFreeChart), pack, and display
    HistogramFrame demo = new HistogramFrame("Histogram", displayData, keys);
    demo.pack();
    demo.setVisible(true);
  }

  //No other classes use this, so inner class it is. This represents the Histogram window
  class HistogramFrame extends ApplicationFrame {

    HistogramFrame(String title, HashMap <String, double[]> data, List <String> keys) {
      super(title);
      HistogramDataset dataset = new HistogramDataset();

      for (String key : keys) {
        dataset.addSeries(key, data.get(key), numberOfBins);
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
