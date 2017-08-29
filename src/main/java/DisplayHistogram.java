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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

@Plugin(type = Command.class, menuPath = "PAN>Display histogram from currently loaded file...")
public class DisplayHistogram implements Command {

    @Parameter private LogService logService;
//    @Parameter private Pan pan;

    @Parameter private File input;

    @Override
    public void run() {


        ArrayList<String> rawInput = null;
        try {
            rawInput = (ArrayList<String>) Files.readAllLines(input.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[] data = new double[rawInput.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = Double.parseDouble(rawInput.get(i));
        }

        HistogramFrame demo = new HistogramFrame("Trial1", data);
        demo.pack();
        demo.setVisible(true);

    }

    class HistogramFrame extends ApplicationFrame {

        public HistogramFrame(String title, double[] data) {
            super(title);
            HistogramDataset dataset = new HistogramDataset();
            dataset.addSeries("647", data, 200);

            JFreeChart chart = ChartFactory.createHistogram("Example", "Distance (nm)",
                    "Frequency", dataset, PlotOrientation.VERTICAL, true, true, false);
            ChartPanel chartPanel = new ChartPanel(chart, false);
            chartPanel.setPreferredSize(new Dimension(500, 270));
            setContentPane(chartPanel);
        }
    }
}
