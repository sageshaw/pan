package plugins.cmds.io;

import analysis.util.ExportUtilities;
import datastructures.analysis.DataContainer;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import plugins.cmds.DataCommand;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Plugin(type = Command.class, menuPath = "PAN>Export>Nearest neighbor results")
public class ExportDataset extends DataCommand {

    private File export;

    private String[][] tabulatedContent;
    private int columnCount;

    private String content;


    @Override
    protected boolean setup(String dataName, DataContainer dataset) {
        List<DataContainer> datasets;

        if (dataset.isBatched()) {
            datasets = panService.getAnalysisBatch(dataset.getBatchKey());
        } else {
            datasets = new ArrayList<>();
            datasets.add(dataset);
        }

        int longestData = 0;

        for (DataContainer d : datasets) {
            if (d.getSize() > longestData)
                longestData = d.getSize();
        }

        export = ExportUtilities.getFile("");
        if (export == null) return false;

        tabulatedContent = new String[longestData + 1][datasets.size()];
        columnCount = 0;

        return true;
    }

    @Override
    protected void forEveryDatasetDo(String dataName, DataContainer dataset, boolean isBatched) {

        tabulatedContent[0][columnCount] = dataName;

        String[][] body = dataset.body();

        for (int r = 0; r < body.length; r++) {
            tabulatedContent[r + 1][columnCount] = body[r][0];
        }

    }

    @Override
    protected void end() {
        content = "";


        for (String[] row : tabulatedContent) {
            for (String col : row) {
                if (col == null) col = "N/A";
                content += col + ",";
            }
            content += System.lineSeparator();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(export.toPath())) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
