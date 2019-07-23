//package plugins.cmds.analysis;
//
//import datastructures.points.OperablePointContainer;
//import analysis.ops.BiOperation;
//import plugins.cmds.analysis.BiChannelCommand;
//import plugins.cmds.io.HISTOTextExportCommand;
//import org.scijava.command.Command;
//import org.scijava.plugin.Plugin;
//
///**
// * Command plugin to export cross-channel analysis.
// */
//@Deprecated
//@Plugin(type = Command.class, menuPath = "PAN>Analysis>Cross-Channel Analysis>Export Cross-Analysis as Text File (Deprecated)")
//public class BiExportText extends BiChannelCommand implements HISTOTextExportCommand {
//
//
//    @Override
//    public String getOutput() {
//        String result = "FromChannel->ToChannel\tValue\r\n";
//
//        OperablePointContainer from = getFromChannel();
//        OperablePointContainer to = getToChannel();
//
//        String fromName = getFromName();
//        String toName = getToName();
//
//        BiOperation operation;
//
//        try {
//            operation = (BiOperation) getChosenAnalysisOp().newInstance();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        operation.setChannel(from, to);
//        double[] processedData = operation.execute();
//
//        for (double value : processedData) {
//            result += fromName + "->" + toName + "\t" + value + "\r\n";
//        }
//
//        return result;
//
//
//    }
//
//    public void run() {
//        HISTOTextExportCommand.super.run();
//    }
//
//
//    @Override
//    protected boolean allowAnalysisSelection() {
//        return true;
//    }
//}
