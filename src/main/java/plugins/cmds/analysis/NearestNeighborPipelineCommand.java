package plugins.cmds.analysis;

import analysis.ops.BiLinearNearestNeighbor;
import analysis.ops.BiOperation;
import analysis.util.StatUtilities;
import datastructures.analysis.DataContainer;
import datastructures.analysis.LinearData;
import datastructures.graphs.HistogramDatasetPlus;
import datastructures.points.ChannelContainer;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.DefaultXYDataset;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import plugins.cmds.charts.HistoUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Plugin(type = Command.class, menuPath = "PAN>Process point data...")
public class NearestNeighborPipelineCommand extends BiChannelCommand {
    private int numBins;
    private boolean scaleToOne;


    private double xLowerBound;
    private double xUpperBound;
    private boolean displayPeakData;
    private String fitFunctionChoice;

    private boolean multiplePeaks;
    private boolean excludeEndpoints;

    private JPanel rangePanel;

    @Override
    protected boolean setup(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName, boolean isBatched) {

        Map<String, ChannelContainer> batchMap;

        if (isBatched) {
            batchMap = panService.getStringChannelSetBatchMap(channels.getBatchKey());
        } else {
            batchMap = new HashMap<>();
            batchMap.put(channelSetName, channels);
        }


        // Run nearest-neighbor analysis for default values for dialog box
        BiOperation biNN = new BiLinearNearestNeighbor();
        biNN.setChannel(channels.get(fromChannelName), channels.get(toChannelName));
        String defaultResultName = "NearestNeighbor" + fromChannelName + "->" + toChannelName;
        DataContainer defaultResult = new LinearData(defaultResultName, biNN.execute());

        // Fill in histogram selection dialog box using nearest-neighbor data found in the data output by previous process
        GenericDialog histoDialog = new GenericDialog("Constructing histogram...");
        String[] batchChannelKeys = batchMap.keySet().toArray(new String[0]);
        Arrays.sort(batchChannelKeys);
        histoDialog.addChoice("Preview data: ", batchChannelKeys, channelSetName);
        histoDialog.addCheckbox("Automatic preview:", true);
        histoDialog.addMessage("Choose histogram parameters:");
        histoDialog.addMessage("Specify x-axis range: ");
        histoDialog.addNumericField("From", defaultResult.min(), 2);
        histoDialog.addNumericField("To", defaultResult.max(), 2);
        histoDialog.addNumericField("Number of boxes", 100, 0);
        histoDialog.addCheckbox("Make total area equal to 1", true);
        histoDialog.addChoice("Fitting data: ", FIT_OPTIONS, FIT_OPTIONS[0]);
        histoDialog.addCheckbox("Display peak data", false);

        // Construct preview frame and add as listener to dynamically update as user changes parameters in histogram dialog box
        HistogramPreviewFrame hPrevFrame = new HistogramPreviewFrame(batchMap, fromChannelName, toChannelName);
        histoDialog.addDialogListener(hPrevFrame);


        hPrevFrame.setVisible(true);
        histoDialog.showDialog();

        if (histoDialog.wasCanceled()) {
            hPrevFrame.setVisible(false);
            hPrevFrame.dispose();
            return false;
        }

        // User has finished entering parameters. Extract selected parameters from dialog box.
        histoDialog.getNextChoice(); // No need to save selected preview channelset
        histoDialog.getNextBoolean(); //No need to save automatic preview option

        xLowerBound = histoDialog.getNextNumber();
        xUpperBound = histoDialog.getNextNumber();

        numBins = (int) histoDialog.getNextNumber();
        if (numBins <= 0 || numBins > BIN_LIMIT) numBins = 1;

        scaleToOne = histoDialog.getNextBoolean();

        fitFunctionChoice = histoDialog.getNextChoice();

        displayPeakData = histoDialog.getNextBoolean();

        hPrevFrame.setVisible(false);
        hPrevFrame.dispose();

        rangePanel = new JPanel();
        rangePanel.setLayout(new BoxLayout(rangePanel, BoxLayout.PAGE_AXIS));


        // Ask and extract additional fitting options (how many peaks, excluding endpoint vals, etc)
        if (!fitFunctionChoice.equals(FIT_OPTIONS[0]) && Arrays.asList(FIT_OPTIONS).contains(fitFunctionChoice)) {
            GenericDialog fitDialog = new GenericDialog("Fitting options...");
            fitDialog.addCheckbox("Find multiple peaks", false);
            fitDialog.addCheckbox("Exclude endpoints from peak calculation", true);
            fitDialog.showDialog();

            if (fitDialog.wasCanceled()) return false;

            multiplePeaks = fitDialog.getNextBoolean();
            excludeEndpoints = fitDialog.getNextBoolean();

        } else {
            multiplePeaks = false;
            excludeEndpoints = false;
        }

        return true;
    }

    @Override
    protected void forEveryChannelSetDo(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName, boolean isBatched) {

        // Step 1: run nearest-neighbor analysis
        BiOperation biNN = new BiLinearNearestNeighbor();
        biNN.setChannel(channels.get(fromChannelName), channels.get(toChannelName));

        String operationName = "NearestNeighbor " + fromChannelName + "->" + toChannelName + "|" + fitFunctionChoice;
        DataContainer dataResult = new LinearData(operationName, biNN.execute());
        if (isBatched) dataResult.setBatchKey(channels.getBatchKey());

        String dataResultName = operationName + " (" + channelSetName + ")";
        panService.addAnalysisResult(dataResultName, dataResult);

        // Step 2: construct histogram
        HistogramDatasetPlus histoData = new HistogramDatasetPlus();
        histoData.addSeries(dataResultName, dataResult.getDataWithinRange(xLowerBound, xUpperBound), numBins);
        if (scaleToOne) {
            histoData.setType(HistogramType.SCALE_AREA_TO_1);
        } else {
            histoData.setType(HistogramType.FREQUENCY);
        }
        if (isBatched) histoData.setBatchKey(dataResult.getBatchKey());
        panService.addHistoSet(dataResultName, histoData);


        // Step 3: fit data with specified function to find peak
        int nx = histoData.getItemCount(0);
        if (!fitFunctionChoice.equals(NO_FIT)) {
            AbstractCurveFitter fitter = getCurveFitter(fitFunctionChoice);
            UnivariateDifferentiableFunction fittedFunc = fitData(fitter, histoData);

            // find all critical points
            ArrayList<double[]> cps = new ArrayList<>();
            double yprime_prev = -1.0;
            for (double x = xLowerBound; x <= xUpperBound; x += FUNC_SAMPLING_RATE) {
                DerivativeStructure xs = new DerivativeStructure(1, 1, 0, x);
                DerivativeStructure ys = fittedFunc.value(xs);

                double y = ys.getValue();
                double yprime = ys.getPartialDerivative(1);

                if (yprime_prev > 0.0) {
                    if (yprime < 0.0) {
                        cps.add(new double[]{x, y});
                    } else if (yprime == 0.0) continue;
                }

                yprime_prev = yprime;

            }


            // Add endpoints if a 'peak' value
            if (!excludeEndpoints) {
                DerivativeStructure lowboundXs = new DerivativeStructure(1, 1, 0, xLowerBound);
                DerivativeStructure highboundXs = new DerivativeStructure(1, 1, 0, xUpperBound);

                DerivativeStructure lowBoundYs = fittedFunc.value(lowboundXs);
                DerivativeStructure highBoundYs = fittedFunc.value(highboundXs);

                if (lowBoundYs.getPartialDerivative(1) < 0.0)
                    cps.add(0, new double[]{xLowerBound, lowBoundYs.getValue()});
                if (highBoundYs.getPartialDerivative(1) > 0.0)
                    cps.add(new double[]{xUpperBound, highBoundYs.getValue()});

            }


            if (multiplePeaks) {
                int count = 1;

                for (double[] pair : cps) {
                    histoData.addEntry("Peak(" + count + ") X", pair[0]);
                    histoData.addEntry("Peak(" + count + ") Y", pair[1]);
                    count++;
                }

            } else {

                double xmax = -10;
                double ymax = -10;

                for (double[] pair : cps) {
                    double y = pair[1];

                    if (y > ymax) {
                        xmax = pair[0];
                        ymax = y;
                    }
                }

                if (ymax >= 0.0) {
                    histoData.addEntry("PeakX", xmax);
                    histoData.addEntry("PeakY", ymax);
                }

            }


            // calculating R^2 value and adding as annotation
            double[] yData = new double[nx];
            double[] fModel = new double[nx];

            for (int ix = 0; ix < nx; ix++) {
                yData[ix] = histoData.getY(0, ix).doubleValue();
                fModel[ix] = fittedFunc.value(histoData.getX(0, ix).doubleValue());
            }

            histoData.addEntry("R^2", StatUtilities.RSquared(yData, fModel));


        } else { // if no fit function was specified, find highest bar
            double xmax = Double.MIN_VALUE;
            double ymax = Double.MIN_VALUE;

            for (int i = 0; i < nx; i++) {

                double y = histoData.getYValue(0, i);

                if (y > ymax) {
                    ymax = y;
                    xmax = 0.5 * (histoData.getStartXValue(0, i) + histoData.getEndXValue(0, i));

                }
            }

            histoData.addEntry("PeakX", xmax);
            histoData.addEntry("PeakY", ymax);

            histoData.addEntry("R^2", -1.0);
        }

        histoData.addEntry("Bound Lower", xLowerBound);
        histoData.addEntry("Bound Upper", xUpperBound);
        histoData.addEntry("Number of bins", numBins);


        JLabel dataLabel = new JLabel("Peak Analysis: " + dataResultName);
        dataLabel.setFont(HistoUtil.HEADER_FONT);
        rangePanel.add(dataLabel);

        JTextArea dataText = new JTextArea(histoData.txtHeader() + histoData.txtBody());
        dataText.setFont(HistoUtil.PARAGRAPH_FONT);
        dataText.setEditable(false);
        rangePanel.add(dataText);
    }

    @Override
    protected void end() {
        if (displayPeakData) {
            JFrame rangeFrame = new JFrame();
            rangeFrame.setContentPane(rangePanel);
            rangeFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            rangeFrame.pack();
            rangeFrame.setVisible(true);
        }

    }


    private class HistogramPreviewFrame extends JFrame implements DialogListener {
        private static final int TEXT_CHANGE_ID = 900;

        private Map<String, ChannelContainer> prevChannelSetMap;
        private BiOperation biNN;
        private String fromChannelName;
        private String toChannelname;


        public HistogramPreviewFrame(Map<String, ChannelContainer> prevChannelSetMap, String fromChannelName, String toChannelName) {
            super();
            this.prevChannelSetMap = prevChannelSetMap;
            biNN = new BiLinearNearestNeighbor();
            this.fromChannelName = fromChannelName;
            this.toChannelname = toChannelName;
        }


        @Override
        public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {

            //Extract selected channelset to preview
            String prevChannelSetKey = gd.getNextChoice();
            ChannelContainer prevChannelSet = prevChannelSetMap.get(prevChannelSetKey);

            if (!gd.getNextBoolean()) return false; //if preview not selected, disable

            //Perform nearest-neighbor analysis on preview channelset
            biNN.setChannel(prevChannelSet.get(fromChannelName), prevChannelSet.get(toChannelname));
            DataContainer previewNNResult = new LinearData(prevChannelSetKey, biNN.execute());

            //Extract data cropping range (with checking to prevent errors)
            Double low = gd.getNextNumber();
            Double high = gd.getNextNumber();
            if (low < 0.0 || low.isNaN() || high <= 0.0 || high.isNaN() || low >= high) return false;

            //Extract number of bins (with error checking)
            int numBins = (int) gd.getNextNumber();
            if (numBins <= 0 || numBins > BIN_LIMIT) return false;

            //Construct histogram with previous parameters
            HistogramDatasetPlus previewHistoData = new HistogramDatasetPlus();
            double[] previewData = previewNNResult.getDataWithinRange(low, high);
            if (previewData.length == 0) return false;
            previewHistoData.addSeries(prevChannelSetKey, previewData, numBins);
            if (gd.getNextBoolean())
                previewHistoData.setType(HistogramType.SCALE_AREA_TO_1);
            else
                previewHistoData.setType(HistogramType.FREQUENCY);

            // Plot histogram
            NumberAxis xAxis = new NumberAxis("");
            xAxis.setAutoRangeIncludesZero(false);
            ValueAxis yAxis = new NumberAxis("");
            XYPlot plot = new XYPlot(previewHistoData, xAxis, yAxis, new XYBarRenderer());
            plot.setOrientation(PlotOrientation.VERTICAL);
            plot.setDomainZeroBaselineVisible(true);
            plot.setRangeZeroBaselineVisible(true);

            // Extract fitting choice
            String fitChoice = gd.getNextChoice();

            // Find corresponding fitting function (if function is specified)
            AbstractCurveFitter fitter = getCurveFitter(fitChoice);

            // if fitting, find cure and add it to plot
            String chartTitle;

            if (fitter != null) {
                UnivariateDifferentiableFunction fitfunc = fitData(fitter, previewHistoData);

                int itemCount = previewHistoData.getItemCount(0);

                double[] yData = new double[itemCount]; // for R^2 calculation
                double[][] fitData = new double[2][itemCount]; // to display on histogram chart and R^2 calculation

                for (int i = 0; i < itemCount; i++) {
                    yData[i] = previewHistoData.getY(0, i).doubleValue();

                    double x = previewHistoData.getX(0, i).doubleValue();
                    fitData[0][i] = x;
                    fitData[1][i] = fitfunc.value(x);
                }

                // add curve to plot for chart display
                DefaultXYDataset fitDataset = new DefaultXYDataset();
                fitDataset.addSeries(fitChoice, fitData);

                plot.setDataset(0, fitDataset);
                plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));

                plot.setDataset(1, previewHistoData);
                plot.setRenderer(1, new XYBarRenderer());

                //Calculate R^2 an add to title
                chartTitle = prevChannelSetKey + " Preview, Curve Fit R^2 = " + StatUtilities.RSquared(yData, fitData[1]);
            } else {

                chartTitle = prevChannelSetKey + " Preview";
            }


            JFreeChart chart = new JFreeChart(chartTitle, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
            currentTheme.apply(chart);
            ChartPanel chartPanel = new ChartPanel(chart, false);
            chartPanel.setPreferredSize(HistoUtil.PREVIEW_DIMENSIONS);

            setContentPane(chartPanel);
            setSize(new Dimension(300, 200));
            pack();
            repaint();

            return true;
        }


        private ChartTheme currentTheme = new StandardChartTheme("JFree");


    }

    private AbstractCurveFitter getCurveFitter(String fitterChoice) {

        AbstractCurveFitter fitter;

        switch (fitterChoice) {
            case POLY_2:
                fitter = PolynomialCurveFitter.create(2);
                break;

            case POLY_3:
                fitter = PolynomialCurveFitter.create(3);
                break;

            case POLY_4:
                fitter = PolynomialCurveFitter.create(4);
                break;

            case POLY_5:
                fitter = PolynomialCurveFitter.create(5);
                break;

            case POLY_6:
                fitter = PolynomialCurveFitter.create(6);
                break;

            case POLY_7:
                fitter = PolynomialCurveFitter.create(7);
                break;

            case POLY_8:
                fitter = PolynomialCurveFitter.create(8);
                break;

            case POLY_9:
                fitter = PolynomialCurveFitter.create(9);
                break;

            case POLY_10:
                fitter = PolynomialCurveFitter.create(10);
                break;

            case POLY_11:
                fitter = PolynomialCurveFitter.create(11);
                break;

            case POLY_12:
                fitter = PolynomialCurveFitter.create(12);
                break;

            case POLY_13:
                fitter = PolynomialCurveFitter.create(13);
                break;

            case POLY_14:
                fitter = PolynomialCurveFitter.create(14);
                break;

            case POLY_15:
                fitter = PolynomialCurveFitter.create(15);
                break;

            case GAUSSIAN_DIST:
                fitter = GaussianCurveFitter.create();
                break;

            default: //No-fit is default
                fitter = null;
                break;

        }

        return fitter;
    }

    private UnivariateDifferentiableFunction fitData(AbstractCurveFitter fitter, HistogramDataset histoData) {
        WeightedObservedPoints obs = new WeightedObservedPoints();
        int itemCount = histoData.getItemCount(0);
        for (int i = 0; i < itemCount; i++) {
            double y = histoData.getY(0, i).doubleValue();
            obs.add(histoData.getX(0, i).doubleValue(), y);
        }

        // fit data
        double[] parameters = fitter.fit(obs.toList());

        // sample data over x-axis values
        UnivariateDifferentiableFunction fitfunc;
        double[][] fitData = new double[2][itemCount];

        if (fitter instanceof GaussianCurveFitter) {
            // If gaussian, fit gaussian
            fitfunc = new Gaussian(parameters[0], parameters[1], parameters[2]);
        } else if (fitter instanceof PolynomialCurveFitter) {
            //If polynomial, construct polynomial func and sample along x vals for histo display
            fitfunc = new PolynomialFunction(parameters);
        } else {
            fitfunc = null;
        }

        return fitfunc;
    }


    private static final int BIN_LIMIT = 99999;
    private static final int NUM_RANGE_DECIMALS = 2;


    private static final String NO_FIT = "No fit";
    private static final String POLY_2 = "2nd-degree polynomial";
    private static final String POLY_3 = "3rd-degree polynomial";
    private static final String POLY_4 = "4th-degree polynomial";
    private static final String POLY_5 = "5th-degree polynomial";
    private static final String POLY_6 = "6th-degree polynomial";
    private static final String POLY_7 = "7th-degree polynomial";
    private static final String POLY_8 = "8th-degree polynomial";
    private static final String POLY_9 = "9th-degree polynomial";
    private static final String POLY_10 = "10th-degree polynomial";
    private static final String POLY_11 = "11th-degree polynomial";
    private static final String POLY_12 = "12th-degree polynomial";
    private static final String POLY_13 = "13th-degree polynomial";
    private static final String POLY_14 = "14th-degree polynomial";
    private static final String POLY_15 = "15th-degree polynomial";
    private static final String GAUSSIAN_DIST = "Gaussian";

    private static final String[] FIT_OPTIONS = new String[]{
            NO_FIT,
            POLY_2,
            POLY_3,
            POLY_4,
            POLY_5,
            POLY_6,
            POLY_7,
            POLY_8,
            POLY_9,
            POLY_10,
            POLY_11,
            POLY_12,
            POLY_13,
            POLY_14,
            POLY_15,
            GAUSSIAN_DIST};


    private static final double FUNC_SAMPLING_RATE = 0.01;


}

