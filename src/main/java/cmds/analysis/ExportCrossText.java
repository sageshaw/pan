package cmds.analysis;

import analysis.data.OperablePointContainer;
import analysis.ops.CrossChannelOperation;
import cmds.CrossChannelCommand;
import cmds.TextExportCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Cross-Channel Analysis>Export as Text File")
public class ExportCrossText extends CrossChannelCommand implements TextExportCommand {



    @Override
    public String getOutput() {
        String result = "FromChannel->ToChannel\tValue\r\n";

        OperablePointContainer from = getFromChannel();
        OperablePointContainer to = getToChannel();

        String fromName = getFromName();
        String toName = getToName();

        CrossChannelOperation operation = null;

        try {
            operation = (CrossChannelOperation) getChosenAnalysisOp().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        operation.setChannel(from, to);
        double[] processedData = operation.execute();

        for (double value : processedData) {
            result += fromName + "->" + toName + "\t" + value + "\r\n";
        }

        return result;


    }

    public void run() {
        TextExportCommand.super.run();
    }

}
