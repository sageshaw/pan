package cmds.analysis;

import analysis.data.OperablePointContainer;
import analysis.ops.CrossChannelOperation;
import analysis.ops.OpScript;
import cmds.AnalysisTextExporter;
import cmds.DynamicOutputDoubleChannel;
import org.scijava.command.Command;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Plugin(type = Command.class, menuPath = "PAN>Export Cross-channel Analysis...")
public class ExportCrossText extends DynamicOutputDoubleChannel implements AnalysisTextExporter {


    @Parameter(label = "Analysis", choices = {"a", "b"})
    String analysisChoice;

    @Parameter
    PanContext panContext;

    public void initialize() {
        ArrayList <String> options = new ArrayList <>();
        List <Class <?>> analysisClasses = panContext.findOpScripts();
        if (analysisClasses == null) throw new IllegalStateException("Cannot find OpScripts.");

        for (Class <?> c : analysisClasses) {
            for (Annotation a : c.getAnnotations()) {
                if (a instanceof OpScript && ((OpScript) a).type() == CrossChannelOperation.class) {
                    options.add(((OpScript) a).label());
                }
            }
        }

        MutableModuleItem <String> choiceItem = getInfo().getMutableInput("analysisChoice", String.class);
        choiceItem.setChoices(options);

        super.initialize();

    }

    private Class <?> getChosenAnalysisOp() {
        List <Class <?>> analysisClasses = panContext.findOpScripts();

        for (Class <?> c : analysisClasses) {
            for (Annotation a : c.getAnnotations()) {
                if (a instanceof OpScript && ((OpScript) a).type() == CrossChannelOperation.class) {
                    if (analysisChoice.equals(((OpScript) a).label())) return c;
                }
            }
        }

        throw new IllegalStateException("Chosen analysis algorithm not available");
    }

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

        operation.init(from, to);
        double[] processedData = operation.execute();

        for (double value : processedData) {
            result += fromName + "->" + toName + "\t" + value + "\r\n";
        }

        return result;


    }

    @Override
    public void run() {
        AnalysisTextExporter.super.run();
    }
}
