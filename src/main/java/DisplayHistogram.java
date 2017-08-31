import containers.TripleContainer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.ApplicationFrame;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.DefaultMutableModuleItem;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Plugin(type = Command.class, menuPath = "PAN>Display histogram from currently loaded file...")
public class DisplayHistogram extends DynamicCommand implements net.imagej.ops.Initializable {

  @Parameter private LogService logService;

  @Parameter private Pan pan;

  @Parameter private UIService ui;

  @Parameter private int numberOfBins;

  @Parameter private String xAxisLabel;

  @Parameter private String yAxisLabel;

  @Parameter private String graphName;

  private List<ModuleItem<Boolean>> checkboxItems = new ArrayList<>();

  private ArrayList<double[]> rawData;

  @Override
  public void run() {

    rawData = pan.getNearestNeighborAnalysis();

    HashMap<String, double[]> displayData = new HashMap<>();
    List<String> keys = new ArrayList<>();

    ModuleItem<Boolean> item;

    for (int i = 0; i < checkboxItems.size(); i++) {
        item = checkboxItems.get(i);
        if (item.getValue(this)) {
            displayData.put(item.getName(), rawData.get(i));
            keys.add(item.getName());
        }
    }


    HistogramFrame demo = new HistogramFrame("Histogram", displayData, keys);
    demo.pack();
    demo.setVisible(true);
  }

  @Override
  public void initialize() {
    Iterator panChannelSetIterator = pan.iterator();
    if (!panChannelSetIterator.hasNext())
      throw new NullPointerException(
          "Pan must have at least one ChannelSet loaded to display histogram");
    TripleContainer channelSet = (TripleContainer) panChannelSetIterator.next();
    Iterator channelIterator = channelSet.iterator();
    while (channelIterator.hasNext()) {
      TripleContainer channel = (TripleContainer) channelIterator.next();
      final ModuleItem<Boolean> item =
          new DefaultMutableModuleItem<Boolean>(getInfo(), channel.getName(), boolean.class);
      item.setLabel(channel.getName());
      checkboxItems.add(item);
      getInfo().addInput(item);
    }
  }

  class HistogramFrame extends ApplicationFrame {

    public HistogramFrame(String title, HashMap<String, double[]> data, List<String> keys) {
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
