package cmds.analysis;

import analysis.data.OperablePointContainer;
import analysis.ops.CrossChannelOperation;
import cmds.CrossChannelCommand;
import cmds.TextExportCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanContext;

@Plugin(type = Command.class, menuPath = "PAN>Export Cross-channel Analysis...")
public class ExportCrossText extends CrossChannelCommand implements TextExportCommand {



    @Parameter
    PanContext panContext;


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
