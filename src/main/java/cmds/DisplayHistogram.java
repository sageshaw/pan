package cmds;

import analysis.pts.Linear;
import analysis.pts.MappedContainer;
import analysis.pts.OperablePointContainer;
import analysis.techs.LinearNearestNeighbor;
import cmds.gui.ChannelModuleItem;
import filters.MaxCutoff;
import filters.PanFilter;
import net.imagej.ops.Initializable;
import org.apache.commons.math3.exception.NullArgumentException;
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
import plugins.IOStorage;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
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

  //TODO: fix casting hell
  @Override
  public void initialize() {

    String[] channelSetKeys = ptStore.keys();

    if (channelSetKeys.length == 0) {
      throw new NullArgumentException();
    }

    MappedContainer channelSet = null;
    String[] channelKeys;

    for (String channelSetKey : channelSetKeys) {
      channelSet = (MappedContainer) ptStore.get(channelSetKey);
      channelKeys = channelSet.keys();

      for (String channelKey : channelKeys) {
        OperablePointContainer channel = (OperablePointContainer) channelSet.get(channelKey);
        final ChannelModuleItem <Boolean> bundledChannelItem =
                new ChannelModuleItem <>(getInfo(), channelKey, boolean.class, channel);

        bundledChannelItem.getModuleItem().setLabel(channelKey + "(" + channelSetKey + ")");
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

        double[] nearestNeighborResult = new LinearNearestNeighbor((Linear) (bundledChannelItem.getChannel())).process();

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
