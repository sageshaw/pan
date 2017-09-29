package cmds.analysis;

import analysis.data.OperablePointContainer;
import analysis.data.PointContainer;
import analysis.ops.SingleChannelOperation;
import cmds.SingleChannelCommand;
import cmds.TextExportCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.util.Map;
import java.util.Set;

@Plugin(type = Command.class, menuPath = "PAN>Analysis>Single-Channel Analysis>Export as Text File")
public class ExportSingleText extends SingleChannelCommand implements TextExportCommand {


    @Override
    public String getOutput() {

        String result = "Channel\tValue\r\n";
        Map <String, PointContainer> checkedChannels = getCheckedChannels();
        Set <String> channelNames = checkedChannels.keySet();

        OperablePointContainer channel;
        double analysisResult[];
        SingleChannelOperation operation = null;

        try {
            operation = (SingleChannelOperation) getChosenAnalysisOp().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //TODO: should not be casting here
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
