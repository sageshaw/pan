package commands;

import commands.gui.ChannelModuleItem;
import constructs.OperableContainer;
import constructs.PointContainer;
import filters.MaxCutoff;
import filters.PanFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.ApplicationFrame;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import net.imagej.ops.Initializable;
import plugins.IOStorage;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Plugin(type = Command.class, menuPath = "PAN>Display histogram from currently loaded file...")
public class DisplayHistogram extends DynamicCommand implements Initializable {

  @Parameter private LogService logService;
  @Parameter private IOStorage ptStore;

  @Parameter(label = "Number of bins") private int numberOfBins;
  @Parameter(label = "X Axis Label") private String xAxisLabel;
  @Parameter(label = "Y Axis Label") private String yAxisLabel;
  @Parameter(label = "Graph Title") private String graphName;

  @Parameter(label = "Max distance cutoff") private int maxDistance;

  private List<ChannelModuleItem<Boolean>> checkboxItems = new ArrayList<>();

  @Override
  public void initialize() {
    Iterator panChannelSetIterator = ptStore.iterator();
    if (!panChannelSetIterator.hasNext())
      throw new NullPointerException(
              "plugins.IOStorage must have at least one ChannelSet loaded to display histogram");

    while (panChannelSetIterator.hasNext()) {
      PointContainer channelSet = (PointContainer) panChannelSetIterator.next();

      for (Object aChannelSet : channelSet) {
        OperableContainer channel = (OperableContainer) aChannelSet;
        final ChannelModuleItem <Boolean> bundledChannelItem =
                new ChannelModuleItem <>(getInfo(), channel.getName(), boolean.class, channel);

        bundledChannelItem.getModuleItem().setLabel(channel.getName() + "(" + channelSet.getName() + ")");
        checkboxItems.add(bundledChannelItem);
        getInfo().addInput(bundledChannelItem.getModuleItem());
      }
    }
  }

  @Override
  public void run() {

    HashMap<String, double[]> displayData = new HashMap<>();
    List<String> keys = new ArrayList<>();

    ModuleItem<Boolean> moduleItem;

    for (ChannelModuleItem <Boolean> bundledChannelItem : checkboxItems) {
      moduleItem = bundledChannelItem.getModuleItem();

      if (moduleItem.getValue(this)) {

          double[] nearestNeighborResult = ((OperableContainer) bundledChannelItem.getChannel()).getNearestNeighborAnalysis();

          PanFilter cutoff = new MaxCutoff(maxDistance);

          nearestNeighborResult = cutoff.filter(nearestNeighborResult);

          displayData.put(moduleItem.getName(), nearestNeighborResult);

        keys.add(moduleItem.getName());
      }
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
