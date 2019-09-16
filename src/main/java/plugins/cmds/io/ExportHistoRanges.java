package plugins.cmds.io;

import analysis.util.ExportUtilities;
import datastructures.graphs.HistogramDatasetPlus;
import ij.io.SaveDialog;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import plugins.cmds.HistogramCommand;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface providing basic framework for text output of analysis data.
 */
//TODO: generalize the container choice system, and reunify textexport
@Plugin(type = Command.class, menuPath = "PAN>Export>Histogram ranges")
public class ExportHistoRanges extends HistogramCommand {

    private File export;

    private String content;

    private int maxPeaks;

    @Override
    protected boolean setup(String histoName, HistogramDatasetPlus histoData) {

        List<HistogramDatasetPlus> histos;
        // TODO: ask after and add batching choice as a param (same for DatCommand)
        if (histoData.isBatched()) {
            histos = panService.getHistoBatch(histoData.getBatchKey());
        } else {
            histos = new ArrayList<>();
            histos.add(histoData);
        }


        HistogramDatasetPlus longestHeaderHisto = histoData;

        for (HistogramDatasetPlus histo : histos) {
            if (longestHeaderHisto.header().length < histo.header().length) {
                longestHeaderHisto = histo;
            }

        }


        maxPeaks = getPeakCount(longestHeaderHisto);



        //Get file directory to use, if user hits cancel, getFile will return null, and the command will
        //exit
        export = ExportUtilities.getFile("");
        if (export == null) return false;

        content = "ID," + longestHeaderHisto.csvHeader();

        return true;
    }

    @Override
    protected void forEveryHistoDo(String histoName, HistogramDatasetPlus histoData, boolean isBatched) {

        int numPeaks = getPeakCount(histoData);
        while (numPeaks < maxPeaks) {
            histoData.addEntry("Peak(" + (numPeaks + 1) + ") X", -1);
            histoData.addEntry("Peak(" + (numPeaks + 1) + ") Y", -1);
            numPeaks++;
        }

        content += histoName + "," + histoData.csvBody();
    }

    @Override
    protected void end() {

        try (BufferedWriter writer = Files.newBufferedWriter(export.toPath())) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println(e);
        }
    }



    private int getPeakCount(HistogramDatasetPlus histo) {
        int peakCount = 0;
        for (String label : histo.header()) {
            if (label.contains("Peak")) {
                peakCount++;
            }
        }
        return peakCount / 2;
    }

}
