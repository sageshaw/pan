package cmds.analysis;

import analysis.data.OperablePointContainer;
import analysis.ops.BiOperation;
import cmds.BiChannelCommand;
import cmds.TextExportCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Cross-Channel Analysis>Export as Text File")
public class BiExportText extends BiChannelCommand implements TextExportCommand {



    @Override
    public String getOutput() {
        String result = "FromChannel->ToChannel\tValue\r\n";

        OperablePointContainer from = getFromChannel();
        OperablePointContainer to = getToChannel();

        String fromName = getFromName();
        String toName = getToName();

        BiOperation operation = null;

        try {
            operation = (BiOperation) getChosenAnalysisOp().newInstance();
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
