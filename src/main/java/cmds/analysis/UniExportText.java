package cmds.analysis;

import datastructures.OperablePointContainer;
import datastructures.PointContainer;
import analysis.ops.UniOperation;
import cmds.TextExportCommand;
import cmds.UniChannelCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.util.Map;
import java.util.Set;

/**
 * Command plugin to export single-channel analysis data in a .txt file.
 */
@Plugin(type = Command.class, menuPath = "PAN>Analysis>Single-Channel Analysis>Export Single-Analysis as Text File")
public class UniExportText extends UniChannelCommand implements TextExportCommand {


    @Override
    public String getOutput() {

        String result = "Channel\tValue\r\n";
        Map <String, PointContainer> checkedChannels = getCheckedChannels();
        Set <String> channelNames = checkedChannels.keySet();

        OperablePointContainer channel;
        double analysisResult[];
        UniOperation operation = null;

        try {
            operation = (UniOperation) getChosenAnalysisOp().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for (String name : channelNames) {
            operation.setChannel((OperablePointContainer) checkedChannels.get(name));
            analysisResult = operation.execute();
            for (double val : analysisResult) {
                result += name + "\t" + val + "\r\n";
            }
        }

        return result;
    }

    @Override
    public void run() {
        TextExportCommand.super.run();
    }
}
