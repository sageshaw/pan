import net.imagej.ImageJ;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.ApplicationFrame;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.util.List;

@Plugin(type = Command.class, menuPath = "PAN>Display histogram from currently loaded file...")
public class DisplayHistogram implements Command {

    @Parameter private LogService logService;

//    @Parameter private Pan pan;


    @Override
    public void run() {

 //       List<double[]> data = pan.getNearestNeighborAnalysis();

        double[] data = new double[500];
        for (int i = 0; i < data.length; i++) {
            data[i] = Math.random()*100;
        }

        MiniHistogram demo = new MiniHistogram("Trial1", data);
        demo.pack();
        demo.setVisible(true);

    }

    class MiniHistogram extends ApplicationFrame {

        public MiniHistogram(String title, double[] data) {
            super(title);
            HistogramDataset dataset = new HistogramDataset();
            dataset.addSeries("Meh", data, 10);

            JFreeChart chart = ChartFactory.createHistogram("Example", "ploof",
                    "plof", dataset, PlotOrientation.VERTICAL, true, true, false);
            ChartPanel chartPanel = new ChartPanel(chart, false);
            chartPanel.setPreferredSize(new Dimension(500, 270));
            setContentPane(chartPanel);
        }
    }
}
