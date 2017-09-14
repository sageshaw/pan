package cmds;

import analysis.pts.Linear;
import analysis.techs.LinearNearestNeighbor;
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

@Plugin(type = Command.class, menuPath = "PAN>Display histogram from currently loaded file...")
public class DisplayHistogram extends OutputAnalysisCommand {

  @Parameter(label = "Number of bins") private int numberOfBins;
  @Parameter(label = "X Axis Label") private String xAxisLabel;
  @Parameter(label = "Y Axis Label") private String yAxisLabel;
  @Parameter(label = "Graph Title") private String graphName;

  @Parameter(label = "Max distance cutoff") private int maxDistance;


    //TODO: Clean up coupling between superclass and subclass implementation
  @Override
  public void run() {
    HashMap<String, double[]> displayData = new HashMap<>();
    List<String> keys = new ArrayList<>();
      List <ChannelModuleItem <Boolean>> checkedItems = getCheckedModules();

    ModuleItem<Boolean> moduleItem;

      for (ChannelModuleItem <Boolean> bundledChannelItem : checkedItems) {
      moduleItem = bundledChannelItem.getModuleItem();

          double[] nearestNeighborResult = new LinearNearestNeighbor((Linear) (bundledChannelItem.getChannel())).process();
          PanFilter cutoff = new MaxCutoff(maxDistance);
          nearestNeighborResult = cutoff.filter(nearestNeighborResult);
          displayData.put(moduleItem.getName(), nearestNeighborResult);
          keys.add(moduleItem.getName());

    }

    HistogramFrame demo = new HistogramFrame("Histogram", displayData, keys);
    demo.pack();
    demo.setVisible(true);
  }

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
